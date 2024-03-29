package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Random;


public class ContainLiquidForOceans extends Feature<DefaultFeatureConfig> {
    public ContainLiquidForOceans(Codec<DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    private final static BlockState ICE = Blocks.ICE.getDefaultState();
    private final static BlockState SNOW = Blocks.SNOW.getDefaultState();

    private final static BlockState[] DEAD_CORAL_ARRAY = {
            Blocks.DEAD_HORN_CORAL_BLOCK.getDefaultState(),
            Blocks.DEAD_BRAIN_CORAL_BLOCK.getDefaultState(),
            Blocks.DEAD_BUBBLE_CORAL_BLOCK.getDefaultState(),
            Blocks.DEAD_FIRE_CORAL_BLOCK.getDefaultState(),
            Blocks.DEAD_TUBE_CORAL_BLOCK.getDefaultState()
    };


    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        //checks to see if there is an ocean biome in this chunk
        //breaks out of nested loop if ocean if found so oceanBiome holds the ocean
        Biome oceanBiome = getOceanInChunk(context.getWorld(), context.getOrigin());

        //does not do anything if there is no ocean biome
        if (oceanBiome == null) {
            return false;
        }

        int sealevel = context.getWorld().toServerWorld().getSeaLevel();
        boolean containedFlag;
        BlockState currentblock;
        BlockState blockAbove;
        BlockPos.Mutable blockposMutable = new BlockPos.Mutable(context.getOrigin().getX(), 0, context.getOrigin().getZ());  //set y to 0
        BlockPos.Mutable blockposMutableAbove = new BlockPos.Mutable().set(blockposMutable);
        Chunk chunk = context.getWorld().getChunk(context.getOrigin().getX() >> 4, context.getOrigin().getZ() >> 4);

        BlockState oceanTopBlock = oceanBiome.getGenerationSettings().getSurfaceConfig().getTopMaterial();
        BlockState oceanUnderBlock = oceanBiome.getGenerationSettings().getSurfaceConfig().getUnderMaterial();

        boolean useCoralTop = oceanTopBlock == DEAD_CORAL_ARRAY[0];
        boolean useCoralBottom = oceanTopBlock == DEAD_CORAL_ARRAY[0];

        //ocean biome was found and thus, is not null. Can safely contain all water in this chunk
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                blockposMutable.set(context.getOrigin().getX() + x, 256, context.getOrigin().getZ() + z);
                for (; blockposMutable.getY() >= sealevel; blockposMutable.move(Direction.DOWN)) {

                    currentblock = chunk.getBlockState(blockposMutable);

                    //move down until we hit a liquid block
                    while (currentblock.getFluidState().isEmpty() && blockposMutable.getY() >= sealevel) {
                        blockposMutable.move(Direction.DOWN);
                        currentblock = chunk.getBlockState(blockposMutable);
                    }

                    //too low now, break out of the loop and move to next xz coordinate
                    if (blockposMutable.getY() < sealevel) {
                        break;
                    }
                    //y value is now fully set for rest of code

                    /*
                     * // Keep this here in case we want to change behavior later // Must be solid all around even diagonally for(int x2 =
                     * -1; x2 < 2; x2++) { for(int z2 = -1; z2 < 2; z2++) {
                     *
                     * material = world.getBlockState(blockpos$Mutable.west(x2).north(z2)).getMaterial();
                     *
                     * if(!material.isSolid() && material != Material.WATER ) { notContainedFlag = true; } } }
                     */

                    //Adjacent blocks must be solid
                    containedFlag = true;
                    for (Direction face : Direction.Type.HORIZONTAL) {
                        //Do world instead of chunk as this could check into the next chunk over.
                        blockposMutable.move(face);
                        if(blockposMutable.getX() >> 4 != chunk.getPos().x || blockposMutable.getZ() >> 4 != chunk.getPos().z)
                            chunk = context.getWorld().getChunk(blockposMutable);

                        currentblock = chunk.getBlockState(blockposMutable);

                        // If the block is snow or not solid without liquid, set contains to false.
                        // Yes, snow layers are considered solid and need a second check.
                        if ((!currentblock.isOpaque() && currentblock.getFluidState().isEmpty() && currentblock != ICE) ||
                            currentblock == SNOW)
                        {
                            containedFlag = false;
                            blockposMutable.move(face.getOpposite());
                            break;
                        }

                        blockposMutable.move(face.getOpposite());
                    }

                    blockposMutableAbove.set(blockposMutable).move(Direction.UP);
                    if(blockposMutable.getX() >> 4 != chunk.getPos().x || blockposMutable.getZ() >> 4 != chunk.getPos().z)
                        chunk = context.getWorld().getChunk(blockposMutable);

                    if (containedFlag) {
                        //water block is contained

                        blockAbove = chunk.getBlockState(blockposMutableAbove);

                        //if above is middle block, replace above block with third config block so middle block (sand/gravel) cannot fall.
                        if (blockAbove == oceanUnderBlock) {
                            if (useCoralBottom || !(oceanBiome.getGenerationSettings().getSurfaceConfig() instanceof TernarySurfaceConfig)) {
                                chunk.setBlockState(blockposMutableAbove, DEAD_CORAL_ARRAY[context.getRandom().nextInt(DEAD_CORAL_ARRAY.length)], false);
                            }
                            else {
                                chunk.setBlockState(blockposMutableAbove, ((TernarySurfaceConfig) oceanBiome.getGenerationSettings().getSurfaceConfig()).getUnderwaterMaterial(), false);
                            }
                        }
                    }
                    else {
                        //water is not contained
                        if (blockposMutable.getY() < 256) {
                            blockAbove = chunk.getBlockState(blockposMutableAbove);

                            if (blockAbove.isOpaque() || !blockAbove.getFluidState().isEmpty()) {

                                //if above is solid or water, place second config block
                                chunk.setBlockState(blockposMutable, oceanUnderBlock, false);
                            }

                            //place first config block if no solid block above and below
                            else {
                                //if config top block is dead coral, randomly chooses any dead coral block to place
                                if (useCoralTop) {
                                    chunk.setBlockState(blockposMutable, DEAD_CORAL_ARRAY[context.getRandom().nextInt(DEAD_CORAL_ARRAY.length)], false);
                                }
                                else {
                                    chunk.setBlockState(blockposMutable, oceanTopBlock, false);
                                }
                            }
                        }

                        //place first config block if too high
                        //if config top block is dead coral, randomly chooses any dead coral block to place
                        else if (useCoralTop) {
                            chunk.setBlockState(blockposMutable, DEAD_CORAL_ARRAY[context.getRandom().nextInt(DEAD_CORAL_ARRAY.length)], false);
                        }
                        else {
                            chunk.setBlockState(blockposMutable, oceanTopBlock, false);
                        }
                    }

                }
            }
        }
        return true;

    }


    /**
     * Gets the first ocean biome found within the edges of the chunk.
     */
    private Biome getOceanInChunk(WorldAccess world, BlockPos originalPosition) {
        Biome biomeAtLocation;
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();

        //checks to see if there is an ocean biome in this chunk
        //breaks out of nested loop if ocean if found so oceanBiome holds the ocean
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                //only check along chunk edges for better performance
                if ((x != 0 && x != 15) && (z != 0 && z != 15)) {
                    continue;
                }

                mutableBlockPos.set(originalPosition.getX() + x, 0, originalPosition.getZ() + z);
                biomeAtLocation = world.getBiome(mutableBlockPos);
                if (biomeAtLocation.getCategory() == Biome.Category.OCEAN) {
                    return biomeAtLocation;
                }
            }
        }

        return null;
    }
}