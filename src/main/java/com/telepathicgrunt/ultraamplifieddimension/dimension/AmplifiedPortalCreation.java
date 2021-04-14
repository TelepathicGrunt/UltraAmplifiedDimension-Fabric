package com.telepathicgrunt.ultraamplifieddimension.dimension;

import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADTags;
import com.telepathicgrunt.ultraamplifieddimension.world.features.AmplifiedPortalFrame;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * Handles creating the Amplified Portal block and holds the code to make the portal frame too.
 */
public class AmplifiedPortalCreation {

    public static ActionResult PortalCreationRightClick(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {

        if (!world.isClient() && !player.isInSneakingPose()) {
            if (player.getStackInHand(hand).getItem().isIn(UADTags.PORTAL_ACTIVATION_ITEMS)) {
                if(trySpawnPortal(world, hitResult.getBlockPos())){
                    player.swingHand(player.getActiveHand(), true);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }


    // ------------------------------------------------------------------------------------//
    // Portal creation and validation check
    public static boolean checkForGeneratedPortal(WorldAccess worldUA) {
        BlockPos pos = new BlockPos(8, worldUA.getDimensionHeight(), 8);
        worldUA.getChunk(pos);

        while (pos.getY() >= 0) {
            if (worldUA.getBlockState(pos) == UADBlocks.AMPLIFIED_PORTAL.getDefaultState()) {
                return true;
            }
            pos = pos.down();
        }

        return false;
    }


    public static void generatePortal(ServerWorld worldUA) {
        AmplifiedPortalFrame amplifiedPortalFrame = new AmplifiedPortalFrame();
        BlockPos pos = new BlockPos(8, worldUA.getDimensionHeight(), 8);
        worldUA.getChunk(pos);

        pos = worldUA.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
        if (pos.getY() > 252) {
            pos = pos.down(3);
        }
        else if (pos.getY() < 6) {
            pos = new BlockPos(pos.getX(), 6, pos.getZ());
        }

        amplifiedPortalFrame.generate(worldUA, pos);
    }


    public static boolean isValid(WorldAccess world, BlockPos pos) {
        // bottom of portal frame
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // Floor corners
                if (Math.abs(x * z) == 1) {
                    if (!world.getBlockState(pos.add(x, -1, z)).isIn(UADTags.PORTAL_CORNER_BLOCKS)) {
                        return false;
                    }
                }

                // Plus shape on floor
                else {
                    BlockState currentFloor = world.getBlockState(pos.add(x, -1, z));
                    if (!(currentFloor.isIn(UADTags.PORTAL_NON_CORNER_BLOCKS) &&
                        (!currentFloor.contains(SlabBlock.TYPE) || currentFloor.get(SlabBlock.TYPE) == SlabType.BOTTOM)))
                    {
                        return false;
                    }
                }
            }
        }

        // the center itself
        if (!world.getBlockState(pos.add(0, 0, 0)).isIn(UADTags.PORTAL_CENTER_BLOCKS)) {
            return false;
        }

        // top of portal frame
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // Top corners
                if (Math.abs(x * z) == 1) {
                    if (!world.getBlockState(pos.add(x, 1, z)).isIn(UADTags.PORTAL_CORNER_BLOCKS)) {
                        return false;
                    }
                }
                // Plus shape on ceiling
                else {
                    BlockState currentCeiling = world.getBlockState(pos.add(x, 1, z));
                    if (!(currentCeiling.isIn(UADTags.PORTAL_NON_CORNER_BLOCKS) &&
                        (!currentCeiling.contains(SlabBlock.TYPE) || currentCeiling.get(SlabBlock.TYPE) == SlabType.TOP)))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean trySpawnPortal(WorldAccess world, BlockPos pos) {
        boolean canMakePortal = isValid(world, pos);
        if (canMakePortal) {
            //place portal at pos in the portal frame.
            world.setBlockState(pos, UADBlocks.AMPLIFIED_PORTAL.getDefaultState(), 18);
        }
        return canMakePortal;
    }
}
