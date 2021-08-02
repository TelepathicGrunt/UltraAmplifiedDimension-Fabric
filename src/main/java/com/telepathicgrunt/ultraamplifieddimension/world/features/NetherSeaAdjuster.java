package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class NetherSeaAdjuster extends Feature<DefaultFeatureConfig> {
    public NetherSeaAdjuster(Codec<DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {

        BlockPos.Mutable blockposMutable = new BlockPos.Mutable(context.getOrigin().getX(), 0, context.getOrigin().getZ());  //set y to 0
        BlockPos.Mutable blockposMutableTemp = new BlockPos.Mutable();
        Chunk cachedChunk = context.getWorld().getChunk(context.getOrigin().getX() >> 4, context.getOrigin().getZ() >> 4);

        // We have to check quit a bit outward due to delta features placing lava WAY outside their chunk.
        for (int x = -6; x < 22; ++x) {
            for (int z = -6; z < 22; ++z) {

                // Run only in nether biomes
                if ( context.getWorld().getBiome(blockposMutable.set(context.getOrigin()).move(x, context.getGenerator().getSeaLevel() - 7, z)).getCategory() != Biome.Category.NETHER) {
                    continue;
                }

                if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z){
                    cachedChunk = context.getWorld().getChunk(blockposMutable);
                }

                BlockState prevBlockState = Blocks.AIR.getDefaultState();
                // Nether biomes gets only 7 block thick water above magma blocks.
                // But the water will not be bubble columns unless updated.
                // We will set the bubble columns manually.
                for (int y = context.getGenerator().getSeaLevel() - 7; y <= context.getGenerator().getSeaLevel(); ++y) {

                    BlockState currentBlockState = cachedChunk.getBlockState(blockposMutable);
                    if(currentBlockState.getFluidState().isIn(FluidTags.WATER)){

                        // Make sure water isn't touching lava tagged blocks by turning water to obsidian.
                        for(Direction direction : Direction.values()){
                            blockposMutableTemp.set(blockposMutable).move(direction);
                            BlockState neighboringBlock;
                            if(blockposMutableTemp.getX() >> 4 != cachedChunk.getPos().x || blockposMutableTemp.getZ() >> 4 != cachedChunk.getPos().z){
                                neighboringBlock = context.getWorld().getBlockState(blockposMutableTemp);
                            }
                            else{
                                neighboringBlock = cachedChunk.getBlockState(blockposMutableTemp);
                            }

                            if(neighboringBlock.getFluidState().isIn(FluidTags.LAVA)){
                                cachedChunk.setBlockState(blockposMutableTemp, Blocks.OBSIDIAN.getDefaultState(), false);
                                prevBlockState = Blocks.OBSIDIAN.getDefaultState();
                                blockposMutable.move(Direction.UP);


                                break;
                            }
                        }

                        // Set bubble columns upward from magma blocks.
                        // If we set obsidian before, this gets skipped.
                        if(prevBlockState.isOf(Blocks.MAGMA_BLOCK) || prevBlockState.isOf(Blocks.BUBBLE_COLUMN)){
                            cachedChunk.setBlockState(blockposMutable, Blocks.BUBBLE_COLUMN.getDefaultState(), false);
                            prevBlockState = Blocks.BUBBLE_COLUMN.getDefaultState();
                            blockposMutable.move(Direction.UP);
                            continue;
                        }
                    }

                    prevBlockState = currentBlockState;
                    blockposMutable.move(Direction.UP);
                }
            }
        }

        return true;
    }
}