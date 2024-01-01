package lod.irongoon.models;

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
}
