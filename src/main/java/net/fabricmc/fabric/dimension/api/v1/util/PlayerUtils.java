package net.fabricmc.fabric.dimension.api.v1.util;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class PlayerUtils {

    public static boolean isDeadBodyNearby(double sqrRadius) {
        for (AbstractClientPlayer player : mc.level.players()) {
            if (player.isDeadOrDying())
                if (player.distanceToSqr(mc.player) <= sqrRadius && player != mc.player) return true;
        }
        return false;
    }

    public static boolean isDeadBodyNearby(float radius) {
        for (AbstractClientPlayer player : mc.level.players()) {
            if (player.isDeadOrDying())
                if (player.distanceTo(mc.player) <= radius && player != mc.player) return true;
        }
        return false;
    }

    public static GameType getGameMode(Player player) {
        PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(player.getUUID());

        if (playerInfo == null)
            return GameType.SPECTATOR;

        return playerInfo.getGameMode();
    }

    public static boolean isCrit(Player player, Entity target) {
        return player.getAttackStrengthScale(0.5F) > 0.9F && player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && target instanceof LivingEntity;
    }

    public static boolean isCrit(Entity target) {
        return isCrit(mc.player, target);
    }

    public static <T extends Entity> T findClosest(Class<T> entityClass, float range) {
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entityClass.isAssignableFrom(entity.getClass()) && !entity.equals(mc.player) && entity.distanceTo(mc.player) <= range) {
                return (T) entity;
            }
        }
        return null;
    }

    public static Player findNearestPlayer(Player toPlayer, float range) {
        float minRange = Float.MAX_VALUE;
        Player minPlayer = null;

        for (Player player : mc.level.players()) {
            float distance = player.distanceTo(toPlayer);

            if (player != toPlayer && distance <= range) {
                if (distance < minRange) {
                    minRange = distance;
                    minPlayer = player;
                }
            }
        }

        return minPlayer;
    }

    public static Player findNearestPlayer(Player toPlayer, float range, boolean seeOnly) {
        float minRange = Float.MAX_VALUE;
        Player minPlayer = null;

        for (Player player : mc.level.players()) {
            float distance = player.distanceTo(toPlayer);

            if (player != toPlayer && distance <= range && player.hasLineOfSight(toPlayer) == seeOnly) {
                if (distance < minRange) {
                    minRange = distance;
                    minPlayer = player;
                }
            }
        }

        return minPlayer;
    }

}
