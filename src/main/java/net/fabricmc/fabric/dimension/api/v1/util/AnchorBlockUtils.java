package net.fabricmc.fabric.dimension.api.v1.util;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.stream.Stream;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class AnchorBlockUtils {

    public static boolean isAnchorCharged(BlockPos anchor) {
        if (!isBlock(Blocks.RESPAWN_ANCHOR, anchor)) {
            return false;
        } else {
            try {
                return BlockUtils.getBlockState(anchor).getValue(RespawnAnchorBlock.CHARGE) != 0;
            } catch (IllegalArgumentException var2) {
                return false;
            }
        }
    }

    public static boolean isAnchorUncharged(BlockPos anchor) {
        return !isAnchorCharged(anchor);
    }

    public static boolean canPlace(BlockState state, BlockPos pos) {
        return mc.level.isUnobstructed(state, pos, null);
    }

    public static boolean hasBlock(BlockPos pos) {
        return !getBlockState(pos).isAir();
    }

    public static boolean isBlock(Block block, BlockPos pos) {
        return getBlockState(pos).getBlock() == block;
    }

    public static Block getBlock(BlockPos pos) {
        return getBlockState(pos).getBlock();
    }

    public static BlockState getBlockState(BlockPos pos) {
        return BlockUtils.getBlockState(pos);
    }

    public static BlockState getDefaultBlockState() {
        return Blocks.STONE.defaultBlockState();
    }

    public static boolean isBlockReplaceable(BlockPos pos) {
        return getBlockState(pos).getMaterial().isReplaceable();
    }

    private static void addToArrayIfHasBlock(ArrayList<BlockPos> array, BlockPos pos) {
        if (hasBlock(pos) && !isBlockReplaceable(pos)) {
            array.add(pos);
        }

    }

    public static ArrayList<BlockPos> getClickableNeighbors(BlockPos pos) {
        ArrayList<BlockPos> blocks = new ArrayList();
        addToArrayIfHasBlock(blocks, pos.offset(1, 0, 0));
        addToArrayIfHasBlock(blocks, pos.offset(0, 1, 0));
        addToArrayIfHasBlock(blocks, pos.offset(0, 0, 1));
        addToArrayIfHasBlock(blocks, pos.offset(-1, 0, 0));
        addToArrayIfHasBlock(blocks, pos.offset(0, -1, 0));
        addToArrayIfHasBlock(blocks, pos.offset(0, 0, -1));
        return blocks;
    }

    public static BlockHitResult clientRaycastBlock(BlockPos pos) {
        return mc.level.clipWithInteractionOverride(RotationUtils.getEyesPos(), RotationUtils.getClientLookVec().multiply(6.0D, 6.0D, 6.0D).add(RotationUtils.getEyesPos()), pos, getBlockState(pos).getShape(mc.level, pos), getBlockState(pos));
    }

    public static BlockHitResult serverRaycastBlock(BlockPos pos) {
        return mc.level.clipWithInteractionOverride(RotationUtils.getEyesPos(), RotationUtils.getClientLookVec().multiply(6.0D, 6.0D, 6.0D).add(RotationUtils.getEyesPos()), pos, getBlockState(pos).getShape(mc.level, pos), getBlockState(pos));
    }

    public static Stream<BlockPos> getAllInBoxStream(BlockPos from, BlockPos to) {
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        Stream<BlockPos> stream = Stream.iterate(min, (pos) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            ++x;
            if (x > max.getX()) {
                x = min.getX();
                ++y;
            }

            if (y > max.getY()) {
                y = min.getY();
                ++z;
            }

            if (z > max.getZ()) {
                throw new IllegalStateException("Stream limit didn't work.");
            } else {
                return new BlockPos(x, y, z);
            }
        });
        int limit = (max.getX() - min.getX() + 1) * (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);
        return stream.limit((long)limit);
    }

    public static boolean isBlockReachable(BlockPos blockPos, double reach) {
        BlockState state = BlockUtils.getBlockState(blockPos);
        VoxelShape shape = state.getInteractionShape(mc.level, blockPos);

        if (shape.isEmpty())
            shape = Shapes.block();

        Vec3 eyesPos = RotationUtils.getEyesPos();
        Vec3 relCenter = shape.bounds().getCenter();
        Vec3 center = Vec3.atCenterOf(blockPos).add(relCenter);
        boolean reachable = false;
        Direction[] var9 = Direction.values();
        int var10 = var9.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            Direction direction = var9[var11];
            Vec3i dirVec = direction.getNormal();
            Vec3 relHitVec = new Vec3(relCenter.x * (double)dirVec.getX(), relCenter.y * (double)dirVec.getY(), relCenter.z * (double)dirVec.getZ());
            Vec3 hitVec = center.add(relHitVec);
            if (eyesPos.distanceToSqr(hitVec) <= Math.pow(reach, 2)) {
                reachable = true;
            }
        }

        return reachable;
    }

    public static boolean rightClickBlock(BlockPos pos) {
        Direction side = null;
        Direction[] sides = Direction.values();
        BlockState state = BlockUtils.getBlockState(pos);
        VoxelShape shape = state.getInteractionShape(mc.level, pos);
        if (shape.isEmpty()) {
            return false;
        } else {
            Vec3 eyesPos = RotationUtils.getEyesPos();
            Vec3 relCenter = shape.bounds().getCenter();
            Vec3 center = Vec3.atCenterOf(pos).add(relCenter);
            Vec3[] hitVecs = new Vec3[sides.length];


            for(int i = 0; i < sides.length; ++i) {
                Vec3 dirVec = Vec3.atLowerCornerOf(sides[i].getNormal());
                Vec3 relHitVec = new Vec3(relCenter.x * dirVec.x, relCenter.y * dirVec.y, relCenter.z * dirVec.z);
                hitVecs[i] = center.add(relHitVec);
            }

            double distanceSqToCenter = eyesPos.distanceToSqr(center);

            for(int i = 0; i < sides.length; ++i) {
                if (!(eyesPos.distanceToSqr(hitVecs[i]) >= distanceSqToCenter)) {
                    side = sides[i];
                    break;
                }
            }

            if (side == null) {
                side = sides[0];
            }

            InteractionResult result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(hitVecs[side.ordinal()], side, pos, false));

            boolean bl = result == InteractionResult.SUCCESS;

            if (bl) {
                mc.player.swing(InteractionHand.MAIN_HAND);
            }

            return bl;
        }
    }

    public static boolean isContainer(BlockPos pos) {
        Block block = getBlock(pos);
        return block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL || block instanceof ShulkerBoxBlock;
    }

}
