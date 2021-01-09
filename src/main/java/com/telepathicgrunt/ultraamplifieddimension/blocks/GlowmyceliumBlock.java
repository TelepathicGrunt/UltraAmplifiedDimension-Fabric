package com.telepathicgrunt.ultraamplifieddimension.blocks;

import com.telepathicgrunt.ultraamplifieddimension.mixin.blocks.SpreadableSnowyDirtBlockAccessor;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.MyceliumBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import java.util.Random;


public class GlowmyceliumBlock extends MyceliumBlock {

    public GlowmyceliumBlock() {
        super(Settings.of(Material.SOLID_ORGANIC, MaterialColor.PURPLE).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.GRASS).luminance((blockState) -> 15));
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

            if (!SpreadableSnowyDirtBlockAccessor.callCanSurvive(state, world, pos)) {
                //block is covered and so will turn into glowdirt
                world.setBlockState(pos, UADBlocks.GLOWDIRT.getDefaultState());
            }
            else if (world.getLightLevel(pos.up()) >= 4) {
                if (world.getLightLevel(pos.up()) >= 9) {
                    //attempt to spread mycelium onto neighboring dirt (glowdirt handles its own conversion)
                    BlockState replacementBlock = Blocks.MYCELIUM.getDefaultState();

                    for (int i = 0; i < 4; ++i) {
                        BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                        if (world.getBlockState(blockpos).getBlock() == Blocks.DIRT && SpreadableSnowyDirtBlockAccessor.callCanSpread(replacementBlock, world, blockpos)) {
                            world.setBlockState(blockpos, replacementBlock.with(SNOWY, world.getBlockState(blockpos.up()).getBlock() == Blocks.SNOW));
                        }
                    }
                }

            }
        }
    }
}
