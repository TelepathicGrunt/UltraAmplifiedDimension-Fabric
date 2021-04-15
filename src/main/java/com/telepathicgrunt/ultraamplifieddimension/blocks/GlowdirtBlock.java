package com.telepathicgrunt.ultraamplifieddimension.blocks;

import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

import java.util.Random;


public class GlowdirtBlock extends Block {

    public GlowdirtBlock() {
        super(Settings.of(Material.SOIL, MaterialColor.DIRT).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.GRAVEL).luminance((blockState) -> 15));
    }


    public static boolean validLightAndSpacing(BlockState blockStateIn, WorldView world, BlockPos blockPosIn) {
        BlockPos blockpos = blockPosIn.up();
        BlockState blockstate = world.getBlockState(blockpos);

        if (blockstate.getBlock() == Blocks.SNOW && blockstate.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        else if (blockstate.getMaterial() != Material.AIR) {
            return false;
        }
        else {
            int i = ChunkLightProvider.getRealisticOpacity(world, blockStateIn, blockPosIn, blockstate, blockpos, Direction.UP, blockstate.getOpacity(world, blockpos));
            return i < world.getMaxLightLevel();
        }
    }


    public static boolean validNeighboringBlockSpace(BlockState blockStateIn, WorldView world, BlockPos blockPosIn) {
        BlockPos blockpos = blockPosIn.up();
        return validLightAndSpacing(blockStateIn, world, blockPosIn) && !world.getFluidState(blockpos).isIn(FluidTags.WATER);
    }


    // checks to see if there is a nearby grass block, glowgrass block, mycelium, or
    // glow mycelium and will transform into glowgrass block or mycelium block
    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        for(int xOffset = -1; xOffset <= 1; xOffset++){
            for(int zOffset = -1; zOffset <= 1; zOffset++){
                if (!world.isChunkLoaded((pos.getX() >> 4) + xOffset, (pos.getZ() >> 4) + zOffset))
                    return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
            }
        }

        //if block is already in an invalid light level or has water/non-air/non-snow layer block above, exits method
        //as this block cannot convert now.
        if (!validNeighboringBlockSpace(this.getDefaultState(), world, pos)) {
            return;
        }

        if (world.getLightLevel(pos.up()) >= 9) {
            BlockState replacementBlock;

            for (int i = 0; i < 4; ++i) {
                BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                Block neighborBlock = world.getBlockState(blockpos).getBlock();

                if (neighborBlock == Blocks.GRASS_BLOCK || neighborBlock == UADBlocks.GLOWGRASS_BLOCK) {
                    replacementBlock = UADBlocks.GLOWGRASS_BLOCK.getDefaultState();
                    world.setBlockState(pos, replacementBlock.with(SnowyBlock.SNOWY, world.getBlockState(pos.up()).getBlock() == Blocks.SNOW));
                }
                else if (neighborBlock == Blocks.MYCELIUM || neighborBlock == UADBlocks.GLOWMYCELIUM) {
                    replacementBlock = UADBlocks.GLOWMYCELIUM.getDefaultState();
                    world.setBlockState(pos, replacementBlock.with(SnowyBlock.SNOWY, world.getBlockState(pos.up()).getBlock() == Blocks.SNOW));
                }
            }
        }
    }
}
