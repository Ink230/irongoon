package lod.irongoon.models;

import org.legendofdragoon.modloader.registries.RegistryId;

import javax.annotation.Nullable;

public record AdditionHitOverride(
    RegistryId additionId,
    int hitNumber,
    @Nullable Integer totalFrames,
    @Nullable Integer overlayHitFrameOffset,
    @Nullable Integer totalSuccessFrames,
    @Nullable Integer overlayStartingFrameOffset
) {
    public AdditionHitOverride {
        if(additionId == null) throw new IllegalArgumentException("Addition hit override ID cannot be null");
        if(hitNumber < 1) throw new IllegalArgumentException("Addition hit override number must be one-based");
    }
}
