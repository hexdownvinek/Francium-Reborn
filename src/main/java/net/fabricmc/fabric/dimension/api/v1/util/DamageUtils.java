package net.fabricmc.fabric.dimension.api.v1.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.swing.text.html.BlockView;
import java.util.Objects;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class DamageUtils {

    // Crystal damage

    public static double crystalDamage(Player player, Vec3 crystal, boolean predictMovement, BlockPos obsidianPos, boolean ignoreTerrain) {
        if (player == null) return 0;
        if (PlayerUtils.getGameMode(player) == GameType.CREATIVE) return 0;

        Vec3 playerPos = player.position();

        if (predictMovement)
            playerPos = playerPos.add(player.getDeltaMovement());

        double modDistance = Math.sqrt(playerPos.distanceToSqr(crystal));
        if (modDistance > 12) return 0;

        double exposure = getExposure(crystal, player, predictMovement, obsidianPos, ignoreTerrain);
        double impact = (1 - (modDistance / 12)) * exposure;
        double damage = ((impact * impact + impact) / 2 * 7 * (6 * 2) + 1);

        damage = getDamageForDifficulty(damage);
        damage = getDamageLeft((float) damage, (float) player.getArmorValue(), (float) player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue());
        damage = resistanceReduction(player, damage);

        Explosion explosion = new Explosion(mc.level, null, crystal.x, crystal.y, crystal.z, 6, false, Explosion.BlockInteraction.DESTROY);
        damage = blastProtReduction(player, damage, explosion);

        return damage < 0 ? 0 : damage;
    }

    public static double crystalDamage(Player player, Vec3 crystal) {
        return crystalDamage(player, crystal, false, null, false);
    }

    // Sword damage

    public static double getSwordDamage(Player player, boolean charged) {
        ItemStack mainHand = player.getMainHandItem();
        
        // Get sword damage
        double damage = 0;
        if (charged) {
            if (mainHand.getItem() instanceof SwordItem sword)
                sword.getDamage();
            
            damage *= 1.5;
        }

        if (mainHand.getEnchantmentTags() != null) {
            if (EnchantmentHelper.getEnchantments(mainHand).containsKey(Enchantments.SHARPNESS)) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, mainHand);
                damage += (0.5 * level) + 0.5;
            }
        }

        if (player.getActiveEffects().contains(MobEffects.DAMAGE_BOOST)) {
            int strength = Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_BOOST)).getAmplifier() + 1;
            damage += 3 * strength;
        }

        // Reduce by resistance
        damage = resistanceReduction(player, damage);

        // Reduce by armour
        damage = getDamageLeft((float) damage, (float) player.getArmorValue(), (float) player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue());

        return damage < 0 ? 0 : damage;
    }

    // Bed damage

    public static double bedDamage(Player player, Vec3 bed) {
        if (PlayerUtils.getGameMode(player) == GameType.CREATIVE) return 0;

        double modDistance = Math.sqrt(player.distanceToSqr(bed));
        if (modDistance > 10) return 0;

        double exposure = Explosion.getSeenPercent(bed, player);
        double impact = (1.0 - (modDistance / 10.0)) * exposure;
        double damage = (impact * impact + impact) / 2 * 7 * (5 * 2) + 1;

        // Multiply damage by difficulty
        damage = getDamageForDifficulty(damage);

        // Reduce by resistance
        damage = resistanceReduction(player, damage);

        // Reduce by armour
        damage = getDamageLeft((float) damage, (float) player.getArmorValue(), (float) player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue());

        // Reduce by enchants
        Explosion explosion = new Explosion(mc.level, null, bed.x, bed.y, bed.z, 5, true, Explosion.BlockInteraction.DESTROY);
        damage = blastProtReduction(player, damage, explosion);

        if (damage < 0) damage = 0;
        return damage;
    }

    // Utils

    public static float getDamageLeft(float damage, float armor, float armorToughness) {
        float f = 2.0F + armorToughness / 4.0F;
        float g = Mth.clamp(armor - damage / f, armor * 0.2F, 20.0F);
        return damage * (1.0F - g / 25.0F);
    }

    private static double getDamageForDifficulty(double damage) {
        return switch (mc.level.getDifficulty()) {
            case PEACEFUL -> 0;
            case EASY     -> Math.min(damage / 2 + 1, damage);
            case HARD     -> damage * 3 / 2;
            default       -> damage;
        };
    }

    private static double blastProtReduction(Player player, double damage, Explosion explosion) {
        int protLevel = EnchantmentHelper.getDamageProtection(player.getArmorSlots(), explosion.getDamageSource());
        if (protLevel > 20) protLevel = 20;

        damage *= (1 - (protLevel / 25.0));
        return damage < 0 ? 0 : damage;
    }

    private static double resistanceReduction(Player player, double damage) {
        if (player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            int lvl = (player.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1);
            damage *= (1 - (lvl * 0.2));
        }

        return damage < 0 ? 0 : damage;
    }

    private static double getExposure(Vec3 source, Player entity, boolean predictMovement, BlockPos obsidianPos, boolean ignoreTerrain) {
        AABB box = entity.getBoundingBox();
        if (predictMovement) {
            Vec3 v = entity.getDeltaMovement();
            box.move(v.x, v.y, v.z);
        }

        double d = 1 / ((box.maxX - box.minX) * 2 + 1);
        double e = 1 / ((box.maxY - box.minY) * 2 + 1);
        double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
        double g = (1 - Math.floor(1 / d) * d) / 2;
        double h = (1 - Math.floor(1 / f) * f) / 2;

        if (!(d < 0) && !(e < 0) && !(f < 0)) {
            int i = 0;
            int j = 0;

            for (double k = 0; k <= 1; k += d) {
                for (double l = 0; l <= 1; l += e) {
                    for (double m = 0; m <= 1; m += f) {
                        double n = Mth.lerp(k, box.minX, box.maxX);
                        double o = Mth.lerp(l, box.minY, box.maxY);
                        double p = Mth.lerp(m, box.minZ, box.maxZ);

                        Vec3 vec3d = new Vec3(n + g, o, p + h);

                        ClipContext clipContext = new ClipContext(vec3d, source, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);

                        if (clip(clipContext, obsidianPos, ignoreTerrain).getType() == HitResult.Type.MISS) i++;

                        j++;
                    }
                }
            }

            return (double) i / j;
        }

        return 0;
    }

    private static double getExposure(Vec3 source, Entity entity, Vec3 playerPos, BlockPos obsidianPos, boolean ignoreTerrain) {
        AABB box = entity.getBoundingBox();
        Vec3 v = playerPos.subtract(entity.position());
        box.move(v.x, v.y, v.z);

        double d = 1 / ((box.maxX - box.minX) * 2 + 1);
        double e = 1 / ((box.maxY - box.minY) * 2 + 1);
        double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
        double g = (1 - Math.floor(1 / d) * d) / 2;
        double h = (1 - Math.floor(1 / f) * f) / 2;

        if (!(d < 0) && !(e < 0) && !(f < 0)) {
            int i = 0;
            int j = 0;

            for (double k = 0; k <= 1; k += d) {
                for (double l = 0; l <= 1; l += e) {
                    for (double m = 0; m <= 1; m += f) {
                        double n = Mth.lerp(k, box.minX, box.maxX);
                        double o = Mth.lerp(l, box.minY, box.maxY);
                        double p = Mth.lerp(m, box.minZ, box.maxZ);

                        Vec3 vec3d = new Vec3(n + g, o, p + h);

                        ClipContext clipContext = new ClipContext(vec3d, source, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);

                        if (clip(clipContext, obsidianPos, ignoreTerrain).getType() == HitResult.Type.MISS) i++;

                        j++;
                    }
                }
            }

            return (double) i / j;
        }

        return 0;
    }


    private static BlockHitResult clip(ClipContext context, BlockPos obsidianPos, boolean ignoreTerrain) {
        return BlockGetter.traverseBlocks(context.getTo(), context.getFrom(), context, (ClipContext clipContext, BlockPos blockPos) -> {
            BlockState blockState;
            if (blockPos.equals(obsidianPos)) blockState = Blocks.OBSIDIAN.defaultBlockState();
            else {
                blockState = mc.level.getBlockState(blockPos);
                if (blockState.getBlock().getExplosionResistance() < 600 && ignoreTerrain) blockState = Blocks.AIR.defaultBlockState();
            }

            Vec3 vec3d = clipContext.getTo();
            Vec3 vec3d2 = clipContext.getFrom();

            VoxelShape voxelShape = clipContext.getBlockShape(blockState, mc.level, blockPos);
            BlockHitResult blockHitResult = mc.level.clipWithInteractionOverride(vec3d, vec3d2, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = Shapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.clip(vec3d, vec3d2, blockPos);

            double d = blockHitResult == null ? Double.MAX_VALUE : clipContext.getTo().distanceToSqr(blockHitResult.getBlockPos().getCenter());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : clipContext.getFrom().distanceToSqr(blockHitResult2.getBlockPos().getCenter());

            return d <= e ? blockHitResult : blockHitResult2;
        }, (ClipContext clipContext) -> {
            Vec3 vec3 = clipContext.getTo().subtract(clipContext.getFrom());
            return BlockHitResult.miss(clipContext.getFrom(), Direction.getNearest(vec3.x, vec3.y, vec3.z), new BlockPos((int) clipContext.getFrom().x, (int) clipContext.getFrom().y, (int) clipContext.getFrom().z));
        });
    }


}
