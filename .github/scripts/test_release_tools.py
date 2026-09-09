"""Isolated packaging and branch-rebase checks; all Git pushes target temp repos."""

import argparse
import contextlib
import importlib.util
import io
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest
import zipfile

SCRIPTS = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("packaging", SCRIPTS / "package_bundles.py")
packaging = importlib.util.module_from_spec(spec)
spec.loader.exec_module(packaging)


def git(directory, *args):
    result = subprocess.run(["git", "-C", str(directory), *args], text=True, capture_output=True)
    if result.returncode:
        raise AssertionError(result.stdout + result.stderr)
    return result.stdout.strip()


class ReleaseToolsTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.original_root, self.original_output = packaging.ROOT, packaging.OUTPUT
        packaging.ROOT, packaging.OUTPUT = self.root, self.root / "build/bundles"

    def tearDown(self):
        packaging.ROOT, packaging.OUTPUT = self.original_root, self.original_output
        # Git writes read-only object files on Windows.
        def writable_remove(function, path, _):
            os.chmod(path, 0o700)
            function(path)
        shutil.rmtree(self.root, onerror=writable_remove)
        self.temp.cleanup()

    def test_next_stable_patch(self):
        for stable, expected in (("v0.4.16", "0.4.17"), ("v0.5.0", "0.5.1"), ("v0.4.99", "0.4.100")):
            output = io.StringIO()
            with contextlib.redirect_stdout(output):
                packaging.next_version(argparse.Namespace(tag=stable))
            self.assertEqual(output.getvalue().strip(), expected)
        with self.assertRaises(ValueError):
            packaging.next_version(argparse.Namespace(tag="v0.5.0-rc1"))

    def test_mod_uses_selected_source_and_version(self):
        source = self.root / "future"
        data = source / "mods/irongoon/irongoon-data"
        data.mkdir(parents=True)
        (data / "scdk-character-stats.csv").write_text("future data")
        (source / "mods/irongoon/config.yaml").write_text("future config")
        jar = self.root / "future.jar"
        with zipfile.ZipFile(jar, "w") as archive:
            for name in ("lod/irongoon/Irongoon.class", "org/yaml/snakeyaml/Yaml.class", "com/opencsv/CSVReader.class"):
                archive.writestr(name, b"fixture")
        packaging.mod(argparse.Namespace(source=source, jar=jar, version="0.4.17"))
        with zipfile.ZipFile(packaging.OUTPUT / "irongoon-v0.4.17.zip") as archive:
            self.assertEqual(archive.read("irongoon/config.yaml"), b"future config")
            self.assertEqual(archive.read("irongoon-v0.4.17.jar"), jar.read_bytes())

    def repository(self):
        repo = self.root / "repo"
        repo.mkdir()
        git(repo, "init", "-b", "main")
        git(repo, "config", "user.name", "Release test")
        git(repo, "config", "user.email", "release@example.invalid")
        (repo / "shared.txt").write_text("base\n")
        git(repo, "add", ".")
        git(repo, "commit", "-m", "add base")
        return repo

    def test_sources_are_exact_tracked_commit(self):
        repo = self.repository()
        (repo / "untracked.txt").write_text("exclude me")
        packaging.sources(argparse.Namespace(source=repo, version="0.4.17"))
        with zipfile.ZipFile(packaging.OUTPUT / "irongoon-v0.4.17-source.zip") as archive:
            self.assertEqual(archive.read("irongoon-v0.4.17/shared.txt"), b"base\n")
            self.assertNotIn("irongoon-v0.4.17/untracked.txt", archive.namelist())

    def sync_fixture(self, conflict=False):
        repo = self.repository()
        git(repo, "checkout", "-b", "main.future")
        git(repo, "checkout", "-b", "topic")
        (repo / "future.txt").write_text("future\n")
        git(repo, "add", ".")
        git(repo, "commit", "-m", "add future")
        git(repo, "checkout", "main.future")
        git(repo, "merge", "--no-ff", "--no-commit", "topic")
        # A merge-only change must survive synchronization.
        (repo / "resolution.txt").write_text("recorded merge resolution\n")
        if conflict:
            (repo / "shared.txt").write_text("future conflicting change\n")
        git(repo, "add", ".")
        git(repo, "commit", "-m", "merge topic with resolution")
        before = git(repo, "rev-parse", "HEAD")
        git(repo, "checkout", "main")
        (repo / "shared.txt").write_text("new main\n")
        git(repo, "add", ".")
        git(repo, "commit", "-m", "update main")
        main = git(repo, "rev-parse", "HEAD")
        remote = self.root / "remote.git"
        remote.mkdir()
        git(remote, "init", "--bare")
        git(repo, "remote", "add", "origin", str(remote))
        git(repo, "push", "origin", "main", "main.future")
        return repo, remote, before, main

    def run_sync(self, repo, main):
        env = os.environ | {
            "MAIN_SHA": main, "PUBLISH_FUTURE": "true",
            "GITHUB_OUTPUT": (self.root / "outputs.txt").as_posix(),
        }
        bash = os.environ.get("BASH_EXE", "bash")
        return subprocess.run([bash, str(SCRIPTS / "rebase_future.sh")], cwd=repo, env=env, text=True, capture_output=True)

    def test_rebase_preserves_merge_resolution_and_is_repeatable(self):
        repo, remote, before, main = self.sync_fixture()
        expected_tree = git(repo, "merge-tree", "--write-tree", before, main)
        result = self.run_sync(repo, main)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        after = git(remote, "rev-parse", "main.future")
        self.assertNotEqual(before, after)
        self.assertEqual(git(repo, "rev-parse", "HEAD^{tree}"), expected_tree)
        self.assertEqual(git(repo, "rev-list", "--merges", f"{main}..HEAD"), "")
        git(repo, "merge-base", "--is-ancestor", main, after)
        result = self.run_sync(repo, main)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual(git(remote, "rev-parse", "main.future"), after)
        git(repo, "checkout", "main")
        (repo / "second-main.txt").write_text("another update\n")
        git(repo, "add", ".")
        git(repo, "commit", "-m", "update main again")
        second_main = git(repo, "rev-parse", "HEAD")
        expected_tree = git(repo, "merge-tree", "--write-tree", after, second_main)
        result = self.run_sync(repo, second_main)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual(git(repo, "rev-parse", "HEAD^{tree}"), expected_tree)

    def test_concurrent_remote_change_is_not_overwritten(self):
        repo, remote, before, main = self.sync_fixture()
        racing = git(repo, "commit-tree", f"{before}^{{tree}}", "-p", before, "-m", "concurrent change")
        git(repo, "push", "origin", f"{racing}:refs/heads/racing")
        hook = repo / ".git/hooks/pre-push"
        hook.write_text(f"#!/bin/sh\ngit --git-dir='{remote.as_posix()}' update-ref refs/heads/main.future {racing} {before}\n")
        hook.chmod(0o755)
        result = self.run_sync(repo, main)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(git(remote, "rev-parse", "main.future"), racing)

    def test_conflict_does_not_change_remote(self):
        repo, remote, before, main = self.sync_fixture(conflict=True)
        result = self.run_sync(repo, main)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("requires manual conflict resolution", result.stderr)
        self.assertEqual(git(remote, "rev-parse", "main.future"), before)


if __name__ == "__main__":
    unittest.main()
