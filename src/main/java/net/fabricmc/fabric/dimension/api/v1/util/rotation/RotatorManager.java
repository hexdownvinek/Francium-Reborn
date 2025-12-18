package net.fabricmc.fabric.dimension.api.v1.util.rotation;

import net.fabricmc.fabric.dimension.api.v1.event.events.PacketEvent;
import net.fabricmc.fabric.dimension.api.v1.imixin.IServerboundMovePlayerPacket;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class RotatorManager {
    public boolean rotating = false;
    public float packetYRot, packetXRot;
    public float targetYRot, targetXRot;

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (rotating && event.packet instanceof ServerboundMovePlayerPacket packet) {
            packetYRot = packet.getYRot(mc.player.getYRot());
            packetXRot = packet.getXRot(mc.player.getXRot());
            ((IServerboundMovePlayerPacket) packet).setHasRot(true);
            ((IServerboundMovePlayerPacket) packet).setYRot(targetYRot);
            ((IServerboundMovePlayerPacket) packet).setXRot(targetXRot);
        }
    }

    public void rotate(Rotation rotation) {
        targetYRot = (float) rotation.yaw();
        targetXRot = (float) rotation.pitch();
        rotating = true;
    }

    public void rotate(float yRot, float xRot) {
        targetYRot = yRot;
        targetXRot = xRot;
        rotating = true;
    }

    public void rotateYRot(float yRot) {
        targetYRot = yRot;
        targetXRot = mc.player.getXRot();
        rotating = true;
    }

    public void rotateXRot(float xRot) {
        targetYRot = mc.player.getYRot();
        targetXRot = xRot;
        rotating = true;
    }

    public void stopRotating() {
        rotating = false;
        mc.player.setYRot(mc.player.getYRot());
        mc.player.setXRot(mc.player.getXRot());
    }

    public void sendRotPacket(Rotation rotation) {
        targetYRot = (float) rotation.yaw();
        targetXRot = (float) rotation.pitch();
        mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(targetYRot, targetXRot, mc.player.isOnGround()));
    }

    public void sendRotPacket(float yRot, float xRot) {
        targetYRot = yRot;
        targetXRot = xRot;
        mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(targetYRot, targetXRot, mc.player.isOnGround()));
    }

    public void sendYRotPacket(float yRot) {
        targetYRot = yRot;
        targetXRot = mc.player.getXRot();
        mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(targetYRot, targetXRot, mc.player.isOnGround()));
    }

    public void sendXRotPacket(float xRot) {
        targetYRot = mc.player.getYRot();
        targetXRot = xRot;
        mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(targetYRot, targetXRot, mc.player.isOnGround()));
    }

}
