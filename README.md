<p align="center">
  <img src="image.png" alt="Irongoon">
</p>

<p align="center">
  Randomizer mod for <a href="https://github.com/Legend-of-Dragoon-Modding/Severed-Chains">Severed Chains</a>
</p>

# Soa sowed the seed
Randomize character and monster stats, change character and Dragoon elements, shuffle shop contents, and customize party availability. Choose a seed and tune individual options to shape your challenge, from a familiar adventure with QoL fixes to an Ironmon-inspired run.

Start with the included default configuration, then customize it through Irongoon's in-game settings. Irongoon and Severed Chains provide the configuration tools and game-data tables you need; no external config builder is required.

See the [Wiki Config Reference](https://github.com/Ink230/irongoon/wiki/Config-Reference) for individual settings, use the optional config builder at [dragoonmods.com](https://dragoonmods.com/), or configure a preset in-game.

# Builds & Releases

### [Latest Irongoon Build](https://github.com/Ink230/irongoon/releases/tag/irongoon-latest)

- Built from Irongoon `main` against the latest [Severed Chains `main`](https://github.com/Legend-of-Dragoon-Modding/Severed-Chains)
- Download `irongoon-v<version>.zip` and extract it into SC's `mods` directory; SC is installed separately
- Remove the previous Irongoon JAR and back up your configuration before replacing the `irongoon` folder

### [Latest Irongoon Future](https://github.com/Ink230/irongoon/releases/tag/irongoon-future)

- Experimental builds from `main.future`, requiring SC `main.spike-testing`; expect breaking changes
- Download the `sc-modified-irongoon-<platform>.zip` bundle for your platform to get the matching SC build with Irongoon already installed
- Extract a platform bundle into a new directory and place your disc images in `isos`
- SC's upstream updater can replace the experimental engine changes and does not update Irongoon

### [Stable Releases](https://github.com/Ink230/irongoon/releases/latest)

- Use a stable release with the SC version specified in its release notes; the rolling Latest and Future builds are prereleases
- SC RB3 compatibility: the pinned Irongoon version is still TBD

# Visuals

<img width="2554" height="1380" alt="image" src="https://github.com/user-attachments/assets/b2d4c1e5-568d-43c9-af06-aca6782f5841" />
<details>
  <summary>Expand more battle screens</summary>
  <img width="2554" height="1377" alt="image" src="https://github.com/user-attachments/assets/94797625-cc86-4ac2-8b59-5d82171d8668" />
  <img width="2555" height="1384" alt="image" src="https://github.com/user-attachments/assets/b58cded0-616f-4f11-95cf-a47ffd6973c6" />
</details>

<img width="1922" height="1373" alt="image" src="https://github.com/user-attachments/assets/5dcc7961-a835-42b7-bdbd-dabd08dba09a" />
<details>
  <summary>Expand more character stats</summary>

  <img width="1929" height="1362" alt="image" src="https://github.com/user-attachments/assets/76951e6b-b9b1-459b-84d3-a850f670aef5" />
  <img width="1923" height="1365" alt="image" src="https://github.com/user-attachments/assets/82c66e94-6f2a-4953-95aa-48ded169533c" />
</details>


<img width="1903" height="1373" alt="image" src="https://github.com/user-attachments/assets/02a24d45-22d7-4324-8271-dfe77d1f42e9" />
<details>
  <summary>Expand more menu settings</summary>
  
  <img width="1903" height="1369" alt="image" src="https://github.com/user-attachments/assets/fd2c5ba9-42ec-4f80-a364-05d0f2df22b6" />
  <img width="1903" height="1370" alt="image" src="https://github.com/user-attachments/assets/1ef4859b-d439-4e23-8b9b-8ebab41f3732" />
  <img width="1903" height="1372" alt="image" src="https://github.com/user-attachments/assets/fb0688e2-59c5-4b34-beb7-4a186deaae3c" />
</details>

# Irongoon Settings

Customize individual randomization options in-game or load a YAML profile. On current `main`, settings are selected in this order:

1. **Campaign settings**: an existing campaign's saved settings snapshot is authoritative, including when restored through a Severed Chains config preset
2. **YAML profiles**: when starting without a snapshot, Irongoon tries the remembered profile if enabled, then `default-ig.yaml`, then other valid `*-ig.yaml` profiles in filename order under `mods/irongoon/configs/`
3. **Legacy configuration**: if no profiles are available, Irongoon uses `mods/irongoon/config.yaml`, which is included in the mod download
4. **Built-in defaults**: if no usable configuration is available, Irongoon uses its built-in Blueprint settings

In the in-game editor, **Use settings** applies your choices to the campaign; saving a profile makes those settings reusable. Editing a YAML file does not automatically replace settings already saved into a campaign. Older campaigns without a snapshot migrate from `config.yaml` first when that file exists.

For option names, modes, and examples, see the [Config Reference](https://github.com/Ink230/irongoon/wiki/Config-Reference) and [Config Template](https://github.com/Ink230/irongoon/wiki/Config-Template).

Addition and Dragoon-spell randomization are currently found in the Latest Irongoon Future build.

## Future preset integration

This branch requires SC `main.spike-testing`, including the `config-presets-fixes` APIs for shared preset names, modified state, and detached drafts. Registry-based selectors before campaign start also require `load-registries-for-mod-menus` (#2793). Use the matching Future bundle; an engine JAR from unmodified SC `main` is insufficient.

When Irongoon is loaded, SC's built-in preset list includes **Irongoon (Blueprint)**. This read-only preset embeds all current Blueprint settings from the shipped schema, independently of local YAML profiles. Use SC's **Add** action with it selected to create an editable copy, then edit Irongoon through the mod options menu. The built-in preset leaves the random campaign seed unset; saved user presets can capture a seed, and a fixed `publicSeed` with `useRandomSeedOnNewCampaign: FALSE` remains supported. No additional SC preset discovery change or `.dpre` installation step is required.

In Irongoon's config menu, **Active Configuration** is the SC preset currently being edited. It is marked **(Modified)** when SC settings differ from that preset or when Irongoon has local, uncommitted edits. **Use settings** stages Irongoon edits in that configuration without writing a YAML file; when editing an SC preset, return to SC's preset editor and confirm its save prompt to persist them. Declining that prompt discards the preset draft and its original preset identity returns.

**Load YAML Profile** is separate: it shows **Choose a profile...** when files are available, or a disabled **No YAML profiles found** when none exist. Loading one stages its settings without claiming that a YAML file is the active SC preset. **Save YAML Profile**, **Save YAML As...**, and **Rename YAML Profile** remain explicit file operations; the YAML File / Source row identifies their target. New campaigns use staged settings on start; existing campaigns use them after saving and reloading.

# Game data sources

Game data provides the starting values that your randomizer settings transform. Irongoon selects a source separately for each dataset:

- **Severed Chains** supplies monster stats directly
- **Bundled CSV tables** supply character and dragoon progression, addition hits, and addition unlock levels where SC does not expose a complete table
- **CSV overrides** let you supply fixed data by enabling `csvDataOverrides: TRUE` in your active configuration and placing matching files in `mods/irongoon/irongoon-data/`

With overrides disabled, character, dragoon, and monster tables also receive live updates from SC events after higher-priority mods have modified them. With overrides enabled, a matching CSV becomes a fixed source for that dataset; missing files follow the normal source selection. An invalid override reports an error instead of silently falling back.

Recognized filenames are `scdk-character-stats.csv`, `scdk-dragoon-stats.csv`, `scdk-monster-stats.csv`, `scdk-addition-stats.csv`, and `scdk-addition-unlock-levels.csv`. The default download includes these tables, so editing game data is optional.

We try to develop Irongoon to be as mod-friendly and as mod-compatible as possible.

# Irongoon Rules
The [Irongoon Rules](https://gist.github.com/Ink230/76197fd8251de5e0927d99077e0c1124) is a WIP goal of capturing the Ironmon essence.

Some rules are currently impossible, not implemented, or need further discussion on their worth.

# Contributing

Anyone is welcome to contribute via Pull Requests or ideas on [Discord](https://discord.gg/legendofdragoon).

## Manual preset checks

Use the matching SC `main.spike-testing` build with `config-presets-fixes` and `load-registries-for-mod-menus` (#2793) for these checks:

1. Load Irongoon and open new campaign setup. Confirm **Irongoon (Blueprint)** appears in the preset list. Select it, open Irongoon's configuration, and check the character, element, item, and equipment selectors
2. In SC's preset editor, select **Irongoon (Blueprint)** and choose **Add**. Name the copy, open Irongoon, change a setting and campaign seed, choose **Use settings**, then confirm SC's save prompt. Reopen the preset and confirm both values remain
3. Edit that saved preset again, change a setting, and choose **Use settings**, but decline SC's save prompt. Reopen it and confirm the previously saved value remains. Neither operation should create or overwrite a YAML profile
4. Select the saved preset and start a campaign. Confirm its configured behavior, save, and reload. Open Irongoon from the in-game mod options, change a setting, choose **Use settings**, then save and reload again. Changes retain their documented lifecycle; new-campaign-only settings do not retroactively change an existing campaign
5. Select an SC preset with Irongoon settings. In Irongoon, confirm **Active Configuration** shows that SC preset and the YAML loader shows **Choose a profile...** or **No YAML profiles found**. Change an SC setting and reopen Irongoon: the active name is marked **(Modified)**. Revert or cancel the SC draft and confirm the original preset name returns
6. In new-campaign setup, change an Irongoon setting: **Active Configuration** is immediately marked **(Modified)**. Choose **Use settings** without saving the SC preset and confirm the active name remains modified. Save the preset, reopen it, and confirm it is clean. Load a YAML profile and confirm it appears only as the YAML file/source, never as the active SC preset

# Credits / Resources

### [Monoxide](https://github.com/LordMonoxide)

- Creating Severed Chains

### [Zychronix](https://github.com/Zychronix)

- Compiling and maintaining the various LoD stats in use by the community

### [Ironmon 101](https://gist.github.com/valiant-code/adb18d248fa0fae7da6b639e2ee8f9c1)

### [Community LoD Discord](https://discord.gg/rQWXgK5)
