package net.fabricmc.fabric.dimension.api.v1.module.modules.player;

import net.fabricmc.fabric.dimension.api.v1.event.events.PacketEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.orbit.listeners.Packet;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;

import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;

@SuppressWarnings("unused")
public class Freecam extends Module {

    //private PlayerCopyEntity dummy;
    private double[] playerPos;
    private float[] playerRot;
    private Entity riding;

    private boolean prevFlying;
    private float prevFlySpeed;
    private final NumberSetting Speed = new NumberSetting("Speed", this, 0d, 0d, 3d, 1d);
    public Freecam() {
        super("Freecam", "Automatically places shit", 0, Category.RENDER);
        addSettings(Speed);
    }


    /*public Freecam() {
        super("Freecam", KEY_UNBOUND, ModuleCategory.PLAYER, "Its freecam, you know what it does.",
                new SettingSlider("Speed", 0, 3, 0.5, 2).withDesc("Moving speed in freecam."),
                new SettingToggle("HorseInv", true).withDesc("Opens your Horse inventory when riding a horse."));
    }*/

    public void onEnable(boolean inWorld) {
        if (!inWorld)
            return;

        mc.player.setSprinting(true);
        //mc.chunkCullingEnabled = false;

        //playerPos = new double[] { mc.player.getX(), mc.player.getY(), mc.player.getZ() };
        //playerRot = new float[] { mc.player.(), mc.player.getPitch() };

        //dummy = new PlayerCopyEntity(mc.player);

        //dummy.spawn();

        if (mc.player.getVehicle() != null) {
            riding = mc.player.getVehicle();
            mc.player.getVehicle().ejectPassengers();
        }

        if (mc.player.isSprinting()) {
        }

        prevFlying = mc.player.getAbilities().flying;
        prevFlySpeed = mc.player.getAbilities().getFlyingSpeed();
        mc.player.getAbilities().flying = true;
    }

    @Override
    public void onDisable() {
        /*if (inWorld) {
            //mc.chunkCullingEnabled = true;

            //dummy.despawn();
            //mc.player.noClip = false;
            mc.player.getAbilities().flying = prevFlying;
            mc.player.getAbilities().setFlyingSpeed(prevFlySpeed);

            //mc.refreshPositionAndAngles(playerPos[0], playerPos[1], playerPos[2], playerRot[0], playerRot[1]);
            //mc.player.setVelocity(Vec3d.ZERO);

            if (riding != null && mc.player.getEntityById(riding.getId()) != null) {
                mc.player.startRiding(riding);
           } */
        mc.player.getAbilities().flying = false;
        mc.player.setSprinting(false);

    }

    @EventHandler
    public void sendPacket(PacketEvent.Send event) {
        if (event.packet instanceof ServerboundMovePlayerPacket || event.packet instanceof ClientboundMoveEntityPacket.Pos || event.packet instanceof ClientboundMoveEntityPacket.Rot || event.packet instanceof ClientboundMoveEntityPacket.PosRot) {
            event.setCancelled(true);

            System.out.println("fdfsfdsf");
        }
    }

    /*@EventHandler
    public void onClientMove(EventClientMove event) {
        mc.player.noClip = true;

    }*/

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        mc.player.setOnGround(false);
        mc.player.getAbilities().setFlyingSpeed( (Speed.getFloatValue() / 5));
        mc.player.getAbilities().flying = true;
        mc.player.setPose(Pose.STANDING);

    }






}
