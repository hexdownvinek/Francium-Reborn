package net.fabricmc.fabric.dimension.api.v1.module.modules.client;

import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;
import net.fabricmc.fabric.dimension.api.v1.util.GlowstoneUtils;

public class DiscordRPC extends Module {

    public final ModeSetting mode = new ModeSetting("Mode", this, "Francium",  "Lunar", "Francium");

    public DiscordRPC() {
        super("DiscordRPC", "Working rich presence?", 0, Category.CLIENT);
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        switch (mode.getMode()) {
            case "Lunar" -> {
                GlowstoneUtils.startLunarRPC();
            }
            case "Francium" -> {
                GlowstoneUtils.startFranciumRPC();
            }
        }
    }

    @Override
    public void onDisable() {
        GlowstoneUtils.stopRPC();
    }
}