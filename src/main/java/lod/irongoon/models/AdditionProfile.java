package lod.irongoon.models;

public record AdditionProfile(
    boolean unlockReorderSafe,
    boolean statReplacementSafe,
    boolean scalingReplacementSafe,
    boolean hitTimingReplacementSafe
) {
    public AdditionProfile {
        if(!unlockReorderSafe && !statReplacementSafe && !scalingReplacementSafe && !hitTimingReplacementSafe) {
            throw new IllegalArgumentException("Addition profile must opt into at least one supported capability");
        }
    }

    public static AdditionProfile stock() {
        return new AdditionProfile(true, true, true, true);
    }
}
