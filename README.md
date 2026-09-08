# Irongoon

## Engine compatibility

This integration branch targets SC `main` with `config-presets-fixes` merged. It uses the PR's `ConfigCollection(false)` API directly; an engine JAR from unmodified `main` is insufficient for this branch.

When Irongoon is loaded, SC's built-in preset list includes **Irongoon (Blueprint)**. This read-only preset embeds all current Blueprint settings from the shipped schema, independently of local YAML profiles. Use SC's **Add** action with it selected to create an editable copy, then edit Irongoon through the mod options menu. The built-in preset leaves the random campaign seed unset; saved user presets can capture a seed, and a fixed `publicSeed` with `useRandomSeedOnNewCampaign: FALSE` remains supported. No additional SC preset discovery change or `.dpre` installation step is required.

In the Irongoon config menu, **Use settings** stages edits in the configuration being edited without writing a YAML profile. When editing an SC preset, return to SC's preset editor and confirm its save prompt to persist those edits; declining that prompt discards the preset draft. **Save Existing**, **Save As New**, and **Rename** remain explicit YAML profile operations. New campaigns use staged settings on start; existing campaigns use them after saving and reloading. The preset editor requires SC's `config-presets-fixes` changes for detached drafts, and registry-based selectors before campaign start require the `load-registries-for-mod-menus` fix (#2793).

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

## Manual preset checks

Use SC `main` with both `config-presets-fixes` and `load-registries-for-mod-menus` (#2793) for these checks:

1. Load Irongoon and open new campaign setup. Confirm **Irongoon (Blueprint)** appears in the preset list. Select it, open Irongoon's configuration, and check the character, element, item, and equipment selectors
2. In SC's preset editor, select **Irongoon (Blueprint)** and choose **Add**. Name the copy, open Irongoon, change a setting and campaign seed, choose **Use settings**, then confirm SC's save prompt. Reopen the preset and confirm both values remain
3. Edit that saved preset again, change a setting, and choose **Use settings**, but decline SC's save prompt. Reopen it and confirm the previously saved value remains. Neither operation should create or overwrite a YAML profile
4. Select the saved preset and start a campaign. Confirm its configured behavior, save, and reload. Open Irongoon from the in-game mod options, change a setting, choose **Use settings**, then save and reload again. Changes retain their documented lifecycle; new-campaign-only settings do not retroactively change an existing campaign

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
