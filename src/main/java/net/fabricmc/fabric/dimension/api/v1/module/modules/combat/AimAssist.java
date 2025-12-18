package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.MouseUpdateEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.*;
import net.fabricmc.fabric.dimension.api.v1.util.rotation.Rotation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("all")
public class AimAssist extends Module {
    public final ModeSetting aimAt = new ModeSetting("Aim at", this, "Head", "Chest", "Legs", "Head");
    public final BooleanSetting lookAtNearest = new BooleanSetting("Look At Nearest Hitbox's Corner", this, false);
    public final BooleanSetting yawAssist = new BooleanSetting("Horizontal", this, true);
    public final NumberSetting yawSpeed = new NumberSetting("Horizontal Speed", this, 1d, 0.1d, 10d, 0.1d);
    public final BooleanSetting pitchAssist = new BooleanSetting("Vertical", this, true);
    public final NumberSetting pitchSpeed = new NumberSetting("Verical Speed", this, 0.5d, 0.1d, 10d, 0.1d);
    public final NumberSetting distance = new NumberSetting("Distance", this, 6d, 3d, 10d, 0.1d);
    public final NumberSetting fov = new NumberSetting("FOV", this, 180d, 1d, 360d, 1d);
    public final BooleanSetting seeOnly = new BooleanSetting("See Only", this, true);

    public AimAssist() {
        super("Aim Assist", "Automatically Aims at players for you.", 0, Module.Category.COMBAT);
        addSettings(aimAt, lookAtNearest, yawAssist, yawSpeed, pitchAssist, pitchSpeed, distance, fov, seeOnly);
    }

    @EventHandler
    public void onMouseUpdate(MouseUpdateEvent event) {
        if (mc.screen == null) {
            Player targetPlayer = PlayerUtils.findNearestPlayer(mc.player, distance.getFloatValue(), seeOnly.isEnabled());

            if (targetPlayer == null)
                return;

            Vec3 targetPlayerPos = targetPlayer.position();

            switch (aimAt.getMode()) {
                case "Chest" -> {
                    targetPlayerPos = targetPlayerPos.add(0, -0.5, 0);
                }
                case "Legs" -> {
                    targetPlayerPos = targetPlayerPos.add(0, -1.2, 0);
                }
            }

            if (lookAtNearest.isEnabled()) {
                double offsetX;

                if (mc.player.getX() - targetPlayer.getX() > 0) {
                    offsetX = 0.29;
                } else {
                    offsetX = -0.29;
                }

                double offsetZ;

                if (mc.player.getZ() - targetPlayer.getZ() > 0) {
                    offsetZ = 0.29;
                } else {
                    offsetZ = -0.29;
                }

                targetPlayerPos = targetPlayerPos.add(offsetX, 0, offsetZ);
            }

            Rotation targetRot = RotationUtils.getDirection(mc.player, targetPlayerPos);

            if (RotationUtils.getAngleToRotation(targetRot) > fov.getValue() / 2)
                return;

            float yawStrength = yawSpeed.getFloatValue() / 50;
            float pitchStrength = pitchSpeed.getFloatValue() / 50;

            float yaw = Mth.rotLerp(yawStrength, mc.player.getYRot(), (float) targetRot.yaw());
            float pitch = Mth.rotLerp(pitchStrength, mc.player.getXRot(), (float) targetRot.pitch());

            if (yawAssist.isEnabled()) mc.player.setYRot(yaw);
            if (pitchAssist.isEnabled()) mc.player.setXRot(pitch);
        }
    }
}