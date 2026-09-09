"""Package the existing Gradle outputs; never include a developer game directory."""

import argparse
from datetime import datetime, timedelta, timezone
from pathlib import Path
import re
import shutil
import stat
import zipfile


ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "build/bundles"
PLATFORMS = ("Windows", "Steam_Deck", "Linux", "Linux_ARM64", "MacOS_Intel", "MacOS_M1")


def version():
    value = (ROOT / ".github/build-version.txt").read_text().strip()
    if not re.fullmatch(r"0\.\d+\.\d+", value):
        raise ValueError(f"Expected a 0.x.x build version, got {value!r}")
    return value


def require_file(path):
    if not path.is_file():
        raise FileNotFoundError(path)


def reset_stage(stage):
    if stage.resolve().parent != (ROOT / "build").resolve() or not stage.name.startswith("package-"):
        raise ValueError(f"Refusing to clear a directory outside the packaging workspace: {stage}")
    if stage.exists():
        shutil.rmtree(stage)


def write_zip(source, destination):
    OUTPUT.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(destination, "w", zipfile.ZIP_DEFLATED) as archive:
        for path in sorted(source.rglob("*")):
            if path.is_file():
                relative = path.relative_to(source).as_posix()
                info = zipfile.ZipInfo.from_file(path, relative)
                info.create_system = 3
                mode = 0o755 if relative in ("launch", "download-java") else 0o644
                info.external_attr = (stat.S_IFREG | mode) << 16
                archive.writestr(info, path.read_bytes(), compress_type=zipfile.ZIP_DEFLATED)
    with zipfile.ZipFile(destination) as archive:
        if archive.testzip() is not None:
            raise ValueError(f"Corrupt ZIP: {destination}")
    print(f"Verified {destination.name} ({destination.stat().st_size:,} bytes)")


def stamp(args):
    if not re.fullmatch(r"[0-9a-f]{40}", args.sha) or not args.build.isdecimal():
        raise ValueError("Expected a full SC commit SHA and numeric build number")
    source = args.sc / "src/main/java/legend/core/Version.java"
    text = source.read_text()
    # Match upstream's development channel and five-minute build-time allowance.
    timestamp = (datetime.now(timezone.utc) + timedelta(minutes=5)).isoformat()
    replacements = {
        'BUILD = "SNAPSHOT"': f'BUILD = "{args.build}"',
        'HASH = "COMMIT"': f'HASH = "{args.sha}"',
        'CHANNEL = "CHANNEL"': 'CHANNEL = "devbuild"',
        "TIMESTAMP = null": f'TIMESTAMP = ZonedDateTime.parse("{timestamp}")',
    }
    for before, after in replacements.items():
        if text.count(before) != 1:
            raise ValueError(f"SC version template changed: expected one {before!r}")
        text = text.replace(before, after)
    source.write_text(text)


def mod(args):
    require_file(args.jar)
    with zipfile.ZipFile(args.jar) as archive:
        names = set(archive.namelist())
        for required in ("lod/irongoon/Irongoon.class", "org/yaml/snakeyaml/Yaml.class", "com/opencsv/CSVReader.class"):
            if required not in names:
                raise ValueError(f"Shaded Irongoon JAR is missing {required}")
        if "legend/game/Main.class" in names:
            raise ValueError("Irongoon JAR must not embed SC")
    stage = ROOT / "build/package-irongoon"
    reset_stage(stage)
    stage.mkdir(parents=True)
    shutil.copy2(args.jar, stage / f"irongoon-v{version()}.jar")
    shutil.copytree(ROOT / "mods/irongoon", stage / "irongoon")
    require_file(stage / "irongoon/config.yaml")
    require_file(stage / "irongoon/irongoon-data/scdk-character-stats.csv")
    write_zip(stage, OUTPUT / f"irongoon-v{version()}.zip")


def bundle(args):
    source = args.sc / "build/libs"
    engine = f"lod-game-{args.sha}.jar"
    launcher = "launch.bat" if args.platform == "Windows" else "launch"
    for name in (engine, "updater.jar", "log4j2.xml", "log4j2-updater.xml", launcher):
        require_file(source / name)
    for directory in ("libs", "gfx", "lang", "patches"):
        if not (source / directory).is_dir() or not any((source / directory).rglob("*")):
            raise ValueError(f"SC distribution is missing {directory}")
    if args.platform != "Windows":
        require_file(source / "download-java")
    launcher_text = (source / launcher).read_text()
    if engine not in launcher_text or any(token in launcher_text for token in ("@version@", "@libs@", "@false@")):
        raise ValueError("SC launcher was not processed by Gradle")
    # Only fresh distribution output is accepted, never game images or user data.
    for forbidden in ("isos", "mods", "saves", "files", "jdk25"):
        if (source / forbidden).exists():
            raise ValueError(f"Unexpected user/runtime directory in SC output: {forbidden}")
    if {path.name for path in source.glob("*.jar")} != {engine, "updater.jar"}:
        raise ValueError("Unexpected engine JARs in SC output; use a clean SC build")
    stage = ROOT / f"build/package-{args.platform}"
    reset_stage(stage)
    shutil.copytree(source, stage)
    # Upstream also removes these Unix helpers from its Windows distribution.
    if args.platform == "Windows":
        for name in ("launch", "download-java"):
            (stage / name).unlink(missing_ok=True)
    elif (stage / "launch.bat").exists():
        raise ValueError("Windows launcher in a Unix distribution; use a clean SC build")
    with zipfile.ZipFile(OUTPUT / f"irongoon-v{version()}.zip") as archive:
        archive.extractall(stage / "mods")
    require_file(stage / "mods" / f"irongoon-v{version()}.jar")
    require_file(stage / "mods/irongoon/config.yaml")
    (stage / "isos").mkdir()
    (stage / "isos/help.txt").write_text("Place your own ISOs or BINs in this folder. Game images are not included.\n")
    write_zip(stage, OUTPUT / f"sc-modified-irongoon-{args.platform}.zip")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    stamp_parser = commands.add_parser("stamp")
    stamp_parser.add_argument("--sc", type=Path, required=True)
    stamp_parser.add_argument("--sha", required=True)
    stamp_parser.add_argument("--build", required=True)
    mod_parser = commands.add_parser("mod")
    mod_parser.add_argument("--jar", type=Path, required=True)
    bundle_parser = commands.add_parser("bundle")
    bundle_parser.add_argument("--sc", type=Path, required=True)
    bundle_parser.add_argument("--sha", required=True)
    bundle_parser.add_argument("--platform", choices=PLATFORMS, required=True)
    arguments = parser.parse_args()
    {"stamp": stamp, "mod": mod, "bundle": bundle}[arguments.command](arguments)
