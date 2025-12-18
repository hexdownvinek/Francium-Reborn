package net.fabricmc.fabric.dimension.api.v1.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class CrystalUtils {

    public static boolean isReplacedCrystal(Entity entity) {
        BlockPos blockPos = entity.getOnPos();
        BlockState blockState = mc.level.getBlockState(blockPos);

        return blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.BEDROCK);
    }

    public static boolean canPlaceCrystalClient(BlockPos block) {
        BlockState blockState = mc.level.getBlockState(block);

        if (!blockState.is(Blocks.OBSIDIAN) && !blockState.is(Blocks.BEDROCK))
            return false;

        BlockPos blockPosAbove = block.above();

        if (!mc.level.isEmptyBlock(blockPosAbove))
            return false;

        double d = blockPosAbove.getX();
        double e = blockPosAbove.getY();
        double f = blockPosAbove.getZ();
        List<Entity> endCrystals = mc.level.getEntities(null, new AABB(d, e, f, d + 1.0D, e + 2.0D, f + 1.0D));

        endCrystals = endCrystals.stream().filter(entity -> entity instanceof EndCrystal).toList();

        return endCrystals.isEmpty();
    }

    public static boolean canPlaceCrystalClientAssumeObsidian(BlockPos block, AABB bb) {
        BlockPos blockPosAbove = block.above();

        if (!mc.level.isEmptyBlock(blockPosAbove))
            return false;

        double d = blockPosAbove.getX();
        double e = blockPosAbove.getY();
        double f = blockPosAbove.getZ();

        AABB crystalBox = new AABB(d, e, f, d + 1.0D, e + 2.0D, f + 1.0D);
        if (crystalBox.intersects(bb))
            return false;

        List<Entity> endCrystals = mc.level.getEntities(null, crystalBox);
        endCrystals = endCrystals.stream().filter(entity -> entity instanceof EndCrystal).toList();
        return endCrystals.isEmpty();
    }
    
}
