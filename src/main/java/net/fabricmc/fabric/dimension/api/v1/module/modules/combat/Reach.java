package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.util.MathUtils;

public class Reach extends Module {
    private final NumberSetting reachChance = new NumberSetting("Chance", this, 100d, 0d, 100d, 1d);
    private final NumberSetting distance = new NumberSetting("Distance", this, 3.0d, 0d, 6.0d, 0.01d);
    private final BooleanSetting onlySprinting = new BooleanSetting("Only Sprint", this, false);

    public Reach() {
        super("Reach", "Credits to kisr for making it", 0, Category.COMBAT);
        addSettings(reachChance, distance, onlySprinting);
    }

    public double getDistance() {
        int randomNum = MathUtils.getRandomInt(1, 100);
        if (randomNum <= reachChance.getIntValue()) {
            return distance.getValue();
        }

        return mc.gameMode.getPickRange();
    }

    public boolean shouldExtendReach() {
        if (onlySprinting.isEnabled()) {
            return mc.player.isSprinting();
        }

        return true;
    }
}
