# Irongoon

## Automated builds

[Latest Irongoon Build](https://github.com/Ink230/irongoon/releases/tag/irongoon-latest) packages `main` only, compiled against SC `main`. Download `irongoon-v<version>.zip` for the shaded mod JAR and `irongoon` config/data folder, or `irongoon-v<version>-source.zip` for the tracked source from the same commit. SC is not bundled in this channel.

The main prerelease version is the latest **stable GitHub release** plus one patch: `v0.4.16` produces `v0.4.17`, even when a `v0.5.0` prerelease exists. The rolling tag stays `irongoon-latest`; automation does not create version tags or stable releases. Actual releases remain manual.

[Latest Irongoon Future](https://github.com/Ink230/irongoon/releases/tag/irongoon-future) packages `main.future` against SC `main.spike-testing`, including the complete platform bundles:

- Download `sc-modified-irongoon-<platform>.zip` for Windows x64, Steam Deck, Linux x64/ARM64, or macOS Intel/Apple Silicon, then extract into a new directory
- Supply your own disc images in `isos`; launch SC using `launch.bat` on Windows or `launch` on Linux/macOS
- Irongoon is already installed as `mods/irongoon-v<version>.jar` and `mods/irongoon/`, including its default config and CSV data
- To install only the mod, extract `irongoon-v<version>.zip` into an existing SC `mods` directory; remove the previous Irongoon JAR and back up your config before replacing the `irongoon` folder

The bundles retain SC's launchers, automatic JDK download, game unpacking, and upstream development updater. SC updates preserve `mods`, but can replace the bundled spike-testing engine with an official development build; they do not update Irongoon. The initial bundle is built against the exact SC commit recorded in the release notes. Cross-platform packaging does not establish runtime compatibility on every operating system.

Every push to `main` starts both workflows independently. [Latest Irongoon Future](https://github.com/Ink230/irongoon/actions/workflows/build-bundles.yml) first rebases `main.future` onto the triggering main commit, then builds that exact future revision. [Latest Irongoon Build](https://github.com/Ink230/irongoon/actions/workflows/build-main.yml) builds the triggering main commit without waiting for future synchronization. A future conflict does not block the main prerelease.

Every push to `main.future` also builds and publishes **Latest Irongoon Future**, using the exact pushed commit without rebasing. Both push paths fetch the latest SC `main.spike-testing` revision and serialize future runs so publication cannot overlap. The automated rebase uses `GITHUB_TOKEN`, whose branch pushes do not trigger another workflow run; the original main-triggered run performs the future build and publication.

Future synchronization replays first-parent changes onto main, retaining each merge's recorded changes as an ordinary commit with original author/message and source-commit attribution. This intentionally linearizes future history so old merge resolutions are preserved instead of reconstructed. Empty first-parent deltas are skipped. The complete result must equal Git's clean merge tree before publishing. Conflicts or a tree mismatch stop the job without changing remote `main.future`; an explicit expected-head lease prevents overwriting concurrent branch updates. After a successful rewrite, local future checkouts must be realigned with `origin/main.future` after preserving any local work. No SC branches are modified.

Both workflows also build PRs targeting `main` and support manual runs. Those runs upload test artifacts without publishing or updating `main.future`; the future workflow uses the current remote future tip in these cases. Actions downloads wrap distribution ZIPs in an artifact ZIP; extract the outer archive first. Unix executable permissions are preserved inside the distribution ZIP. Artifacts use the repository's retention policy and generally require a GitHub login; prerelease assets provide public downloads.

Maintainer settings:

- `.github/workflows/build-bundles.yml` selects SC's repository and target branch, currently `Legend-of-Dragoon-Modding/Severed-Chains` / `main.spike-testing`; the workflow resolves that branch once and uses the same commit for the mod and all six platforms
- `.github/build-version.txt` controls future package naming, initially `0.5.1`; main package naming is calculated from the latest stable release instead
- Successful pushes to `main` update `irongoon-future` with seven ZIPs and `irongoon-latest` with the main mod/source ZIPs; pushes to `main.future` update only `irongoon-future`; no `v0.x.x` tags are created
- Manual version-tag releases remain a separate, Irongoon-only process; this workflow does not run on tag pushes
- No SC repository writes or SC release credentials are needed; builds use SC's committed patch metadata without running its private metadata scraper

The existing `shadowJar` task remains the mod build. CI supplies a fresh SC dependency as `lod-game-snapshot-2.jar`, then packages the shaded JAR and `mods/irongoon` directory from the selected Irongoon revision. Both workflows use Java 25 and each repository's checked-in Gradle wrapper. Packaging fails if required mod classes, SC libraries/support directories, processed launchers, or the updater are missing. `.github/scripts/test_release_tools.py` exercises version selection, source/config packaging, preserved merge resolutions, repeated rebases, and conflict/concurrent-update refusal using disposable local repositories.

## Engine compatibility

`main` targets Severed Chains `main`. Addition and Dragoon-spell randomization from Irongoon PRs #19 and #18 are reserved for `main.future`, which requires SC `main.spike-testing` with upstream PRs #2771 and #2765. SC #2790 is optional for Irongoon; #2793 enables the new-campaign mod-menu registry flow and is not required for compilation.

SC config presets preserve Irongoon's campaign snapshot. If no snapshot exists, Irongoon selects its default profile when configuring or starting a campaign; it no longer depends on SC's removed remembered-campaign-settings API. Existing campaign snapshots remain authoritative. On `main`, the 66 future-only settings are ignored with warnings when reading a future profile, and are omitted when exporting or saving that profile.

Build against a JAR produced from the corresponding SC branch: copy its `build/libs/lod-game-snapshot.jar` to this checkout's ignored `lod-game-snapshot-2.jar`, then run `gradlew.bat compileJava assemble`. A stale local engine JAR does not establish branch compatibility.

The Irongoon mod for [Severed Chains](https://github.com/Legend-of-Dragoon-Modding/Legend-of-Dragoon-Java) allows an "Ironmon" style of play for Legend of Dragoon.

An online tool to help visually generate different settings can be found on [dragoonmods.com](https://dragoonmods.com/)

# In the Beginning
Experiment with the config settings (different randomizer options) and see what is fun.

Quick Notes

- Early game is heavily balanced around power functions, there is some attempt at adjusting for this but good luck
- You may deal lots of damage but you may also take lots of damage ✔

At the moment, the mod is a glorified character and monster stat randomizer. Elements, additions, rewards, and some combat mechanics are somewhat doable and will be done next. Everything else in the Irongoon ruleset needs additional mod support to implement.
# Irongoon Rules
The [Irongoon Rules](https://gist.github.com/Ink230/76197fd8251de5e0927d99077e0c1124) is a WIP goal of capturing the Ironmon essence.

Some rules are currently impossible, not implemented, or need further discussion on their worth.

# Irongoon Settings

The mod permits customizing the randomizer and having different randomization options. This is done through the use of a config.yaml file.

A default configuration file is included in the latest release. This can be more easily generated by the mentioned online tool on [dragoonmods.com](https://dragoonmods.com/)

Please see the [Config Reference](https://github.com/Ink230/irongoon/wiki/Config-Reference) for an explanation on all the settings.

## Game data sources

Irongoon keeps its existing table-based data layer and selects a source independently for each logical dataset.

With `csvDataOverrides: FALSE`, Irongoon loads monster stats from Severed Chains and uses the bundled CSVs as compatibility tables for character progression, Dragoon progression, addition hits, and addition unlock levels. Character and Dragoon tables receive live updates from real SC level events, and monster rows receive live updates from `MonsterStatsEvent`, after higher-priority mods have changed those events.

Set `csvDataOverrides: TRUE` in `mods/irongoon/config.yaml` to make any matching file in `mods/irongoon/irongoon-data` a fixed override. A missing file still follows the normal SC-or-compatibility resolution policy. A present but invalid override stops initialization with its logical dataset and validation error rather than silently falling back.

The recognized logical filenames are:

- `scdk-addition-stats.csv`
- `scdk-character-stats.csv`
- `scdk-dragoon-stats.csv`
- `scdk-monster-stats.csv`
- `scdk-addition-unlock-levels.csv`

Startup logs report the selected source, the reason it was selected, and whether live SC updates are enabled for each dataset.

Current SC does not expose side-effect-free complete progression tables for character or Dragoon levels, so their bundled CSVs provide the full bootstrap tables. Irongoon does not simulate level-ups to reconstruct them. SC addition hits are contextual, and addition unlocks are semantic criteria rather than one numeric threshold, so both addition datasets remain CSV compatibility sources in this implementation.


# Contributing

Anyone is welcome to contribute via Pull Requests or ideas on [Discord](https://discord.gg/legendofdragoon).

# Credits / Resources

### [Monoxide](https://github.com/LordMonoxide)

- Creating Severed Chains

### [Zychronix](https://github.com/Zychronix)

- Compiling and maintaining the various LoD stats in use by the community

### [Ironmon 101](https://gist.github.com/valiant-code/adb18d248fa0fae7da6b639e2ee8f9c1)

### [Community LoD Discord](https://discord.gg/rQWXgK5)
