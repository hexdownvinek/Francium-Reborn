package net.fabricmc.fabric.dimension.api.v1.util;

import net.fabricmc.fabric.dimension.api.v1.util.rotation.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class RotationUtils {

    public static Vec3 getEyesPos() {
        return mc.getBlockEntityRenderDispatcher().camera.getPosition();
    }

    public static BlockPos getCameraBlockPos() {
        return mc.getBlockEntityRenderDispatcher().camera.getBlockPosition();
    }

    public static BlockPos getEyesBlockPos()
    {
        return new BlockPos((int) getEyesPos().x, (int) getEyesPos().y, (int) getEyesPos().z);
    }

    public static Vec3 getPlayerLookVec(Player player) {
        float f = 0.017453292F;
        float pi = (float) Math.PI;

        float f1 = Mth.cos(-player.getYRot() * f - pi);
        float f2 = Mth.sin(-player.getYRot() * f - pi);
        float f3 = -Mth.cos(-player.getXRot() * f);
        float f4 = Mth.sin(-player.getXRot() * f);

        return new Vec3(f2 * f3, f4, f1 * f3).normalize();
    }

    public static Vec3 getClientLookVec() {
        return getPlayerLookVec(mc.player);
    }

    public static Rotation getDirection(Entity entity, Vec3 vec) {
        double dx = vec.x - entity.getX(),
                dy = vec.y - entity.getY(),
                dz = vec.z - entity.getZ(),
                dist = Mth.sqrt((float) (dx * dx + dz * dz));

        return new Rotation(Mth.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0), -Mth.wrapDegrees(Math.toDegrees(Math.atan2(dy, dist))));
    }

    public static double getAngleToRotation(Rotation rotation) {
        double currentYaw = Mth.wrapDegrees(mc.player.getYRot());
        double currentPitch = Mth.wrapDegrees(mc.player.getXRot());

        double diffYaw = Mth.wrapDegrees(currentYaw - rotation.yaw());
        double diffPitch = Mth.wrapDegrees(currentPitch - rotation.pitch());

        return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
    }

}
