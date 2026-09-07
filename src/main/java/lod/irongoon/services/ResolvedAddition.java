package lod.irongoon.services;

import legend.game.additions.Addition;
import legend.game.additions.AdditionHitProperties10;
import legend.game.characters.CharacterAdditionInfo;
import legend.game.characters.CharacterData2c;
import legend.game.unpacker.FileData;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ResolvedAddition extends Addition {
    private final RegistryId additionId;
    private final Addition baseAddition;
    private final AdditionHitProperties10[] hits;
    private final float[] damageMultipliers;
    private final float[] spMultipliers;
    private final boolean resolvedDamage;
    private final boolean resolvedSp;

    public ResolvedAddition(
        final RegistryId additionId,
        final Addition baseAddition,
        final AdditionHitProperties10[] hits,
        final float[] damageMultipliers,
        final float[] spMultipliers,
        final boolean resolvedDamage,
        final boolean resolvedSp
    ) {
        if(additionId == null) throw new IllegalArgumentException("Resolved addition ID cannot be null");
        if(baseAddition == null) throw new IllegalArgumentException("Resolved base addition cannot be null");
        if(hits == null || hits.length == 0) throw new IllegalArgumentException("Resolved addition hits cannot be null or empty");
        if(damageMultipliers == null || damageMultipliers.length == 0) throw new IllegalArgumentException("Resolved addition damage multipliers cannot be null or empty");
        if(spMultipliers == null || spMultipliers.length != damageMultipliers.length) {
            throw new IllegalArgumentException("Resolved addition SP multipliers must match damage multiplier levels");
        }

        this.additionId = additionId;
        this.baseAddition = baseAddition;
        this.hits = copyHits(hits);
        this.damageMultipliers = damageMultipliers.clone();
        this.spMultipliers = spMultipliers.clone();
        this.resolvedDamage = resolvedDamage;
        this.resolvedSp = resolvedSp;
    }

    public static ResolvedAddition stock(final RegistryId additionId, final Addition baseAddition, final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        final AdditionHitProperties10[] hits = new AdditionHitProperties10[baseAddition.getHitCount(character, additionInfo)];
        for(int hitIndex = 0; hitIndex < hits.length; hitIndex++) {
            hits[hitIndex] = new AdditionHitProperties10(baseAddition.getHit(character, additionInfo, hitIndex));
        }

        final int maxLevel = baseAddition.getMaxLevel(character, additionInfo);
        final float[] damageMultipliers = new float[maxLevel];
        final float[] spMultipliers = new float[maxLevel];
        final CharacterAdditionInfo levelInfo = new CharacterAdditionInfo(additionInfo);
        for(int level = 1; level <= maxLevel; level++) {
            levelInfo.level = level;
            damageMultipliers[level - 1] = baseAddition.getDamageMultiplier(character, levelInfo);
            spMultipliers[level - 1] = baseAddition.getSpMultiplier(character, levelInfo);
        }

        return new ResolvedAddition(additionId, baseAddition, hits, damageMultipliers, spMultipliers, false, false);
    }

    @Override
    public RegistryId getRegistryId() {
        return this.additionId;
    }

    @Override
    public String getName() {
        return this.baseAddition.getName();
    }

    @Override
    public int getDamage(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        if(!this.resolvedDamage) return this.baseAddition.getDamage(character, additionInfo);

        int damage = 0;
        for(final AdditionHitProperties10 hit : this.hits) {
            damage += hit.damageMultiplier_04;
        }
        return (int)(damage * this.getDamageMultiplier(character, additionInfo));
    }

    @Override
    public int getSp(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        if(!this.resolvedSp) return this.baseAddition.getSp(character, additionInfo);

        final float multiplier = this.getSpMultiplier(character, additionInfo);
        int sp = 0;
        for(final AdditionHitProperties10 hit : this.hits) {
            sp += (int)(hit.sp_05 * multiplier);
        }
        return sp;
    }

    @Override
    public float getDamageMultiplier(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.resolvedDamage
            ? this.damageMultipliers[additionInfo.level - 1]
            : this.baseAddition.getDamageMultiplier(character, additionInfo);
    }

    @Override
    public float getSpMultiplier(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.resolvedSp
            ? this.spMultipliers[additionInfo.level - 1]
            : this.baseAddition.getSpMultiplier(character, additionInfo);
    }

    @Override
    public int getXpToNextLevel(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.baseAddition.getXpToNextLevel(character, additionInfo);
    }

    @Override
    public int getMaxLevel(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.damageMultipliers.length;
    }

    @Override
    public boolean isComplete(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.baseAddition.isComplete(character, additionInfo);
    }

    @Override
    public boolean countsTowardsMastery(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.baseAddition.countsTowardsMastery(character, additionInfo);
    }

    @Override
    public int getHitCount(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.hits.length;
    }

    @Override
    public AdditionHitProperties10 getHit(final CharacterData2c character, final CharacterAdditionInfo additionInfo, final int index) {
        return new AdditionHitProperties10(this.hits[index]);
    }

    @Override
    public CompletableFuture<List<FileData>> loadAnimations(final CharacterData2c character, final CharacterAdditionInfo additionInfo) {
        return this.baseAddition.loadAnimations(character, additionInfo);
    }

    public AdditionHitProperties10[] copyHits() {
        return copyHits(this.hits);
    }

    public float[] copyDamageMultipliers() {
        return this.damageMultipliers.clone();
    }

    public float[] copySpMultipliers() {
        return this.spMultipliers.clone();
    }

    public Addition baseAddition() {
        return this.baseAddition;
    }

    public boolean hasResolvedDamage() {
        return this.resolvedDamage;
    }

    public boolean hasResolvedSp() {
        return this.resolvedSp;
    }

    private static AdditionHitProperties10[] copyHits(final AdditionHitProperties10[] hits) {
        final AdditionHitProperties10[] copy = new AdditionHitProperties10[hits.length];
        for(int hitIndex = 0; hitIndex < hits.length; hitIndex++) {
            if(hits[hitIndex] == null) throw new IllegalArgumentException("Resolved addition hits cannot contain null entries");
            copy[hitIndex] = new AdditionHitProperties10(hits[hitIndex]);
        }
        return copy;
    }
}
