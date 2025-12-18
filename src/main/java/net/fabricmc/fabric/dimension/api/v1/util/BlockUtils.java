package net.fabricmc.fabric.dimension.api.v1.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.stream.Stream;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class BlockUtils {

    private static void addToArrayIfHasBlock(ArrayList<BlockPos> array, BlockPos pos) {
        if (hasBlock(pos) && !isBlockReplaceable(pos))
            array.add(pos);
    }

    public static ArrayList<BlockPos> getClickableNeighbors(BlockPos pos) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        addToArrayIfHasBlock(blocks, pos.offset(1,  0,  0));
        addToArrayIfHasBlock(blocks, pos.offset(0,  1,  0));
        addToArrayIfHasBlock(blocks, pos.offset(0,  0,  1));
        addToArrayIfHasBlock(blocks, pos.offset(-1,  0,  0));
        addToArrayIfHasBlock(blocks, pos.offset(0, -1,  0));
        addToArrayIfHasBlock(blocks, pos.offset(0,  0, -1));
        return blocks;
    }

    public static boolean canPlace(BlockState state, BlockPos pos) {
        return mc.level.isUnobstructed(state, pos, null);
    }

    public static boolean hasBlock(BlockPos pos)
    {
        return !mc.level.getBlockState(pos).isAir();
    }

    public static boolean isBlock(Block block, BlockPos pos)
    {
        return getBlockState(pos).getBlock() == block;
    }

    public static Block getBlock(BlockPos pos) {
        return mc.level.getBlockState(pos).getBlock();
    }

    public static BlockState getBlockState(BlockPos pos) {
        return mc.level.getBlockState(pos);
    }

    public static BlockState getDefaultBlockState() {
        return Blocks.STONE.getStateDefinition().any();
    }

    public static boolean isBlockReplaceable(BlockPos pos) {
        return getBlockState(pos).getMaterial().isReplaceable();
    }

    public static Stream<BlockPos> getAllInBoxStream(BlockPos from, BlockPos to) {
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()),
                Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()),
                Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

        Stream<BlockPos> stream = Stream.iterate(min, pos -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            x++;

            if(x > max.getX())
            {
                x = min.getX();
                y++;
            }

            if(y > max.getY())
            {
                y = min.getY();
                z++;
            }

            if(z > max.getZ())
                throw new IllegalStateException("Stream limit didn't work.");

            return new BlockPos(x, y, z);
        });

        int limit = (max.getX() - min.getX() + 1)
                * (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);

        return stream.limit(limit);
    }

}
