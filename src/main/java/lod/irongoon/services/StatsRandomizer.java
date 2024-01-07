package lod.irongoon.services;

import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.characters.CharacterStatsEvent;
import lod.irongoon.models.DivineFruit;
import lod.irongoon.services.randomizer.Randomizer;

public class StatsRandomizer {
    public static void randomize(CharacterStatsEvent character) {
        DivineFruit bodyStatsRandomized = Randomizer.doCharacterStats(character);
        DivineFruit dragoonStatsRandomized = Randomizer.doDragoonStats(character);
        DivineFruit hpStatRandomized = Randomizer.doCharacterHP(character);
        DivineFruit speedStatRandomized = Randomizer.doCharacterSpeed(character);

        character.bodyAttack = bodyStatsRandomized.bodyAttack;
        character.bodyDefence = bodyStatsRandomized.bodyDefense;
        character.bodyMagicAttack = bodyStatsRandomized.bodyMagicAttack;
        character.bodyMagicDefence = bodyStatsRandomized.bodyMagicDefense;

        character.dragoonAttack = dragoonStatsRandomized.dragoonAttack;
        character.dragoonDefence = dragoonStatsRandomized.dragoonDefense;
        character.dragoonMagicAttack = dragoonStatsRandomized.dragoonMagicAttack;
        character.dragoonMagicDefence = dragoonStatsRandomized.dragoonMagicDefense;

        character.maxHp = hpStatRandomized.maxHP;
        character.bodySpeed = speedStatRandomized.bodySpeed;
    }

    public static void randomize(MonsterStatsEvent monster) {
        DivineFruit monsterStatsRandomized = Randomizer.doMonsterStats(monster);
        DivineFruit monsterHPRandomized = Randomizer.doMonsterHP(monster);
        DivineFruit monsterSpeedRandomized = Randomizer.doMonsterSpeed(monster);

        Randomizer.doMonsterVariance(monsterStatsRandomized, monsterHPRandomized, monsterSpeedRandomized);

        monster.attack = monsterStatsRandomized.bodyAttack;
        monster.defence = monsterStatsRandomized.bodyDefense;
        monster.magicAttack = monsterStatsRandomized.bodyMagicAttack;
        monster.magicDefence = monsterStatsRandomized.bodyMagicDefense;

        monster.maxHp = monsterHPRandomized.maxHP;
        monster.hp = monsterHPRandomized.maxHP;
        monster.speed = monsterSpeedRandomized.bodySpeed;
    }
}
