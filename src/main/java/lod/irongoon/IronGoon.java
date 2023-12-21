package lod.irongoon;

import legend.core.GameEngine;
import legend.game.modding.Mod;

@Mod(id = IronGoon.MOD_ID)
public class IronGoon {
    public static final String MOD_ID = "irongoon";

    public IronGoon() {
        GameEngine.EVENTS.register(this);
    }
}
