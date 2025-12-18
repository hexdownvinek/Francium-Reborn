package net.fabricmc.fabric.dimension.api.v1.module.modules.render;

import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;

@SuppressWarnings("all")
public class NoBounce extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Only Crystals", "All Items", "Only Crystals");

    public NoBounce() {
        super("No Bounce", "removes hotbar's bounce", 0, Category.RENDER);
        addSettings(mode);
    }

}
