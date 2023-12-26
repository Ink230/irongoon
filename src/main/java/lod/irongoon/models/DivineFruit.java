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
