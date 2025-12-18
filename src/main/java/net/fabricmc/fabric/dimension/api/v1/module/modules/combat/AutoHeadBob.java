package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventPriority;
import net.minecraft.Util;
import net.minecraft.world.item.Items;

import java.util.Random;

public class AutoHeadBob extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Normal", "Packet", "Normal");
    private final NumberSetting amplitude = new NumberSetting("Head Bob Amplitude", this, 1.0d, 1.0d, 50d, 0.1d);
    private final NumberSetting speed = new NumberSetting("How Fast", this, 5.0d, 0d, 15d, 0.1d);
    private final BooleanSetting verticalRandomness = new BooleanSetting("Vertical Randomness", this, false);
    private final BooleanSetting horizontalRandomness = new BooleanSetting("Horizontal Randomness", this, false);
    private final NumberSetting randomness = new NumberSetting("Randomness", this, 0.5d, 0.5d, 5d, 0.1d);
    private final Random rng = new Random();

    private long startTime = 0;

    public AutoHeadBob() {
        super("Auto Head Bob", "Automatically Head bobs For You", 0, Category.COMBAT);
        addSettings(mode, amplitude, speed, verticalRandomness, horizontalRandomness, randomness);
    }

    public void reset() {
        startTime = Util.getMillis();
    }

    public void onEnable() {
        reset();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.player != null && mc.player.getMainHandItem().is(Items.END_CRYSTAL)) {
            long currentTime = Util.getMillis();

            long timePast = currentTime - startTime;
            double f = timePast / 1000.0 * 2.0 * Math.PI * speed.getIntValue();
            double g = -Math.cos(f) * amplitude.getIntValue(); // derivative of sin is cosine
            double h = 0.0;

            // random offsets
            double random = rng.nextDouble();
            // mapping [0, 1] to [-randomness, randomness]
            double randomnessV = randomness.getIntValue();
            random = random * 2 * randomnessV - randomnessV;

            if (verticalRandomness.isEnabled())
                g += random;
            if (horizontalRandomness.isEnabled())
                h += random;

            float yRot = mc.player.getYRot() + (float) h;
            float xRot = mc.player.getXRot() + (float) g;

            switch (mode.getMode()) {
                case "Packet" -> {
                    Client.rotatorManager().rotate(yRot, xRot);
                }
                case "Normal" -> {
                    mc.player.setYRot(yRot);
                    mc.player.setXRot(xRot);
                }
            }
        } else {
            Client.rotatorManager().stopRotating();
        }
    }
}
