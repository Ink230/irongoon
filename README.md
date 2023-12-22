# Irongoon

The Irongoon mod for [Severed Chains]() allows an "Ironmon" style of play for Legend of Dragoon.

# Irongoon Rules
The [Irongoon Rules](https://gist.github.com/Ink230/76197fd8251de5e0927d99077e0c1124) is a WIP goal of capturing the Ironmon essence.

Some rules are currently impossible, not implemented, or need further discussion on their worth.

During the development stage of Irongoon feel free to label your runs as `Dabas`

- "Dabas Irongoon Run", "Dabas Ultimate Irongoon Run"

# Irongoon Settings

Beyond the stock settings, the mod permits additional tunings to some of the randomizer options.

### Total Stats Per Level
How many total stat points characters will have to be distributed to each stat on a level up.
Speed excluded.


- Random with Bounds
  - At each level, we calculate a random amount bounded by the lowest to highest total stats of all characters for the current level
- Maintain Stock
  - Each character's standard total stat points per level is used 
- Average
  - At each level, we use the average of all character total stat points

### Total Stats Bounds
How the bounds for Random with Bounds works.

- Stock
  - Bounds are as described in Random with Bounds
- Random Modifier
  - We use 0% to 200% of the randomly chosen amount between the lowest and highest total stats of all characters for the current level
- Random Modifier Custom
  - Enter your custom upper bound

### Total Stats Distribution
How the total stat points are distributed among the various stats.

- Random
  - On each level up, each stat is given a percentage of the total stat points
  - This is zero-sum, so some stats may get all or no stat points
- Dabas Fixed
  - A fixed distribution is applied to each stat for all level ups
- Dabas Random
  - A fixed distribution is distributed randomly to each stat for each level up
- Dabas Fixed Custom
- Dabas Random Custom

'fixed distribution'
- 4 zero-sum percents pre-baked (25%, 40%, 5%, 30%)
- Dabas Fixed applies these to the same stats for each level up
- Dabas Random applies these to different stats for each level up
- Custom lets you enter your own zero-sum % distribution
- Compared to Random where the distribution percents are randomly generated each time

### HP
Determines how HP is provided.

- RPG
  - Calculate HP from a combination of Level, Attack, and Defense
- RPG Random
  - Randomize the weights of Level, Attack, and Defense, fixed for all levels
- RPG Random Random
  - Randomize as above, but use 3 randomly selected stats for each level
- RPG Random Ultra
  - Randomize as above, but the weights are randomized for each level
- Random
  - Gain HP equal to a random amount of the total stat points gained bounded from 40% to 160% 
- Stock
  - Gain HP as normal for the stock character

### MP
Figuring this one out, help us discuss!

### Speed
Determines how Speed is initially generated.

- Random
  - Randomizes Speed for characters bounded by the lowest to highest of stock speed stats
- Random Custom
  - Enter your own custom lower and upper bounds
- Stock
  - Characters retain their stock speed
- Stock Shuffle
  - Shuffles the stock character speeds amongst the characters
- Chaos Stock Shuffle
  - Shuffles the stock character speeds amongst the characters on every battle
- Chaos Random Shuffle
  - Calculates speeds as Random amongst the characters for every battle

# Contributing

Anyone is welcome to contribute via Pull Requests or ideas on [Discord](https://discord.gg/legendofdragoon).

# Credits / Resources

### [Monoxide](https://github.com/LordMonoxide)

- Creating Severed Chains

### [Zynchronix](https://github.com/Zychronix)

- Compiling and maintaining the various LoD stats in use by the community

### [Ironmon 101](https://gist.github.com/valiant-code/adb18d248fa0fae7da6b639e2ee8f9c1)

### [Community LoD Discord]()