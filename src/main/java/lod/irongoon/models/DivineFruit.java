package lod.irongoon.models;

import legend.game.characters.ElementSet;
import lod.irongoon.data.CharacterStatsData;
import lod.irongoon.data.DragoonStatsData;
import lod.irongoon.data.Elements;

import java.util.HashMap;

public class DivineFruit {
    public int bodyAttack;
    public int bodyMagicAttack;
    public int bodyDefense;
    public int bodyMagicDefense;
    public int bodySpeed;
    public int dragoonAttack;
    public int dragoonMagicAttack;
    public int dragoonDefense;
    public int dragoonMagicDefense;
    public int maxHP;
    public int maxMP;
    public int level;
    public int dLevel;
    public Elements element;
    public ElementSet elementImmunity;

    public DivineFruit() {}

    public DivineFruit(int bodyAttack, int bodyDefense, int bodyMagicAttack, int bodyMagicDefense) {
        this.bodyAttack = bodyAttack;
        this.bodyDefense = bodyDefense;
        this.bodyMagicAttack = bodyMagicAttack;
        this.bodyMagicDefense = bodyMagicDefense;
    }

    public DivineFruit(int dragoonAttack, int dragoonDefense, int dragoonMagicAttack, int dragoonMagicDefense, boolean isDragoon) {
        this.dragoonAttack = dragoonAttack;
        this.dragoonDefense = dragoonDefense;
        this.dragoonMagicAttack = dragoonMagicAttack;
        this.dragoonMagicDefense = dragoonMagicDefense;
    }

    public DivineFruit(int HP, int MP) {
        this.maxHP = HP;
        this.maxMP = MP;
    }

    public DivineFruit(int speed) {
        this.bodySpeed = speed;
    }

    public DivineFruit (DivineFruit divineFruit) {
        this.bodyAttack = divineFruit.bodyAttack;
        this.bodyMagicAttack = divineFruit.bodyMagicAttack;
        this.bodyDefense = divineFruit.bodyDefense;
        this.bodyMagicDefense = divineFruit.bodyMagicDefense;
        this.bodySpeed = divineFruit.bodySpeed;
        this.dragoonAttack = divineFruit.dragoonAttack;
        this.dragoonMagicAttack = divineFruit.dragoonMagicAttack;
        this.dragoonDefense = divineFruit.dragoonDefense;
        this.dragoonMagicDefense = divineFruit.dragoonMagicDefense;
        this.maxHP = divineFruit.maxHP;
        this.maxMP = divineFruit.maxMP;
    }

    public DivineFruit(Elements element, ElementSet elementImmunity) {
        this.element = element;
        this.elementImmunity = elementImmunity;
    }

    public DivineFruit(HashMap<String, Integer> stats, boolean dragoon) {
        if (!dragoon) {
            this.bodySpeed = stats.get(CharacterStatsData.SPEED.name());
            this.bodyAttack = stats.get(CharacterStatsData.ATTACK.name());
            this.bodyMagicAttack = stats.get(CharacterStatsData.MAGIC_ATTACK.name());
            this.bodyDefense = stats.get(CharacterStatsData.DEFENSE.name());
            this.bodyMagicDefense = stats.get(CharacterStatsData.MAGIC_DEFENSE.name());
        } else {
            this.maxMP = stats.get(DragoonStatsData.MAX_MP.name());
            this.dragoonAttack = stats.get(DragoonStatsData.DRAGOON_ATTACK.name());
            this.dragoonMagicAttack = stats.get(DragoonStatsData.DRAGOON_MAGIC_ATTACK.name());
            this.dragoonDefense = stats.get(DragoonStatsData.DRAGOON_DEFENSE.name());
            this.dragoonMagicDefense = stats.get(DragoonStatsData.DRAGOON_MAGIC_DEFENSE.name());
        }
    }
}
