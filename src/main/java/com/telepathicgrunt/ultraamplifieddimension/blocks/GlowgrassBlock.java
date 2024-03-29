package com.telepathicgrunt.ultraamplifieddimension.blocks;

import com.telepathicgrunt.ultraamplifieddimension.mixin.blocks.SpreadableSnowyDirtBlockAccessor;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;

import java.util.Random;


public class GlowgrassBlock extends GrassBlock {

    public GlowgrassBlock() {
        super(Settings.of(Material.SOLID_ORGANIC, MapColor.GREEN).ticksRandomly().strength(0.5F).sounds(BlockSoundGroup.GRASS).luminance((blockState) -> 15));
    }


    /*
     * every tick, it'll attempt to spread normal mycelium instead of itself. If covered, will turn into glowdirt.
     */
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient) {
            for(int xOffset = -1; xOffset <= 1; xOffset++){
                for(int zOffset = -1; zOffset <= 1; zOffset++){
                    if (!world.isChunkLoaded((pos.getX() >> 4) + xOffset, (pos.getZ() >> 4) + zOffset))
                        return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
                }
            }

            if (!SpreadableSnowyDirtBlockAccessor.uad_callCanSurvive(state, world, pos)) {
                //block is covered and so will turn into glowdirt
                world.setBlockState(pos, UADBlocks.GLOWDIRT.getDefaultState());
            }
            else if (world.getLightLevel(pos.up()) >= 9) {
                //attempt to spread grass onto neighboring dirt (glowdirt handles its own conversion)
                BlockState replacementBlock = Blocks.GRASS_BLOCK.getDefaultState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (world.getBlockState(blockpos).getBlock() == Blocks.DIRT && SpreadableSnowyDirtBlockAccessor.uad_callCanSpread(replacementBlock, world, blockpos)) {
                        world.setBlockState(blockpos, replacementBlock.with(SNOWY, world.getBlockState(blockpos.up()).getBlock() == Blocks.SNOW));
                    }
                }
            }
        }
    }
}
