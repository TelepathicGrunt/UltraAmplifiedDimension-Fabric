package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.utils.OpenSimplexNoise;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.PondConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class Pond extends Feature<PondConfig> {

    protected OpenSimplexNoise noiseGen;
    protected long seed;

    public void setSeed(long seed) {
        if (this.seed != seed || this.noiseGen == null) {
            this.noiseGen = new OpenSimplexNoise(seed);
            this.seed = seed;
        }
    }

    public Pond(Codec<PondConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<PondConfig> context) {
        // Set to chunk center as our pond will not cross chunk boundaries.
        BlockPos.Mutable blockpos = new BlockPos.Mutable().set((context.getOrigin().getX() >> 4) << 4, context.getOrigin().getY(), (context.getOrigin().getZ() >> 4) << 4);
        blockpos.move(8, 0, 8);
        BlockPos centerPos = blockpos.toImmutable();
        Chunk cachedChunk = context.getWorld().getChunk(blockpos);

        // Validation checks to make sure lake is in a safe spot to generate
        for(int x = -7; x < 7; x++){
            for(int z = -7; z < 7; z++){
                for(int y = -3; y < 4; y++){
                    double normX = x / 7d;
                    double normY = y / 4d;
                    double normZ = z / 7d;
                    // Check only in a squished sphere space
                    if((normX * normX) + (normY * normY) + (normZ * normZ) <= 0.60d){
                        blockpos.set(centerPos).move(x, y, z);
                        BlockState blockState = cachedChunk.getBlockState(blockpos);

                        // No liquids in lake space above
                        if(y >= 0 && !blockState.getFluidState().isEmpty()){
                            return false;
                        }
                        // No air/liquid space in lake space below (allow it's own inside state tho)
                        else if(!GeneralUtils.isFullCube(context.getWorld(), blockpos, blockState) && blockState != context.getConfig().insideState){
                            return false;
                        }
                    }
                }
            }
        }

        // Setup noise
        setSeed(context.getWorld().getSeed());
        BlockState aboveState = null;
        for(int x = -8; x < 8; x++){
            for(int z = -8; z < 8; z++){
                for(int y = 4; y >= -4; y--){
                    blockpos.set(centerPos).move(x, y, z);
                    double noiseVal = noiseGen.eval(blockpos.getX() * 0.21d, blockpos.getY() * 0.06d, blockpos.getZ() * 0.21d);
                    double normX = x / 8d;
                    double normY = y / 4d;
                    double normZ = z / 8d;
                    double lakeVal = (normX * normX) + (normY * normY) + (normZ * normZ) - ((noiseVal + 1) * 0.9d);

                    if(lakeVal < -0.065d){
                        BlockState blockState1 = cachedChunk.getBlockState(blockpos);

                        if(y == 4){

                            if(context.getConfig().placeOutsideStateOften && GeneralUtils.isFullCube(context.getWorld(), blockpos, blockState1) && context.getRandom().nextFloat() < 0.70f){
                                aboveState = cachedChunk.getBlockState(blockpos.move(Direction.UP));
                                blockpos.move(Direction.DOWN);

                                if(aboveState.isAir()){
                                    cachedChunk.setBlockState(blockpos, context.getConfig().topState, false);
                                }
                                else{
                                    cachedChunk.setBlockState(blockpos, context.getConfig().outsideState, false);
                                }
                            }

                            // Store y == 4 as that block in context.getWorld() must be stored as aboveState
                            aboveState = cachedChunk.getBlockState(blockpos);
                        }

                        if(GeneralUtils.isFullCube(context.getWorld(), blockpos, blockState1) || blockState1.isIn(BlockTags.ICE)){
                            // Edge of chunk and bottom of lake is always solid blocks.
                            // Threshold used for the encasing in outside blockstate.
                            if(x == -8 || z== -8 || x == 7 || z == 7 || lakeVal > -0.48d || y == -4){
                                if(context.getConfig().placeOutsideStateOften){
                                    if(aboveState.isAir() || aboveState.isOf(Blocks.SNOW)){
                                        cachedChunk.setBlockState(blockpos, context.getConfig().topState, false);
                                    }
                                    else{
                                        cachedChunk.setBlockState(blockpos, context.getConfig().outsideState, false);
                                    }
                                }
                            }
                            else if (y <= 0){
                                cachedChunk.setBlockState(blockpos, context.getConfig().insideState, false);

                                for(Direction direction : Direction.values()){
                                    // Will never go into other chunk due to the edge of chunk check above.
                                    // This will contain the liquid as best as possible.
                                    if(direction != Direction.UP){
                                        BlockState blockState = cachedChunk.getBlockState(blockpos.move(direction));
                                        if(!GeneralUtils.isFullCube(context.getWorld(), blockpos, blockState) && blockState != context.getConfig().insideState){
                                            cachedChunk.setBlockState(blockpos, context.getConfig().outsideState, false);
                                        }
                                        blockpos.move(direction.getOpposite());
                                    }
                                    // Prevent stuff like lava ponds getting water placed above it.
                                    else if(!context.getConfig().insideState.getFluidState().isEmpty()){
                                        BlockState blockState = cachedChunk.getBlockState(blockpos.move(direction));
                                        if(!blockState.getFluidState().isEmpty() && blockState != context.getConfig().insideState){
                                            cachedChunk.setBlockState(blockpos, context.getConfig().outsideState, false);
                                        }
                                        blockpos.move(direction.getOpposite());
                                    }
                                }
                            }
                            else {
                                if(!aboveState.getFluidState().isEmpty()){
                                    cachedChunk.setBlockState(blockpos, context.getConfig().outsideState, false);
                                }
                                else{
                                    cachedChunk.setBlockState(blockpos, Blocks.CAVE_AIR.getDefaultState(), false);
                                }
                            }

                            // Remove floating plants.
                            BlockState plantCheckState = aboveState;
                            while(blockpos.getY() <= context.getGenerator().getWorldHeight() &&
                                    !plantCheckState.isOpaque() &&
                                    !plantCheckState.canPlaceAt(context.getWorld(), blockpos))
                            {
                                cachedChunk.setBlockState(blockpos, Blocks.AIR.getDefaultState(), false);
                                blockpos.move(Direction.UP);
                                plantCheckState = cachedChunk.getBlockState(blockpos);
                            }
                        }
                    }
                    else{
                        // If fail to place lake blocks, store our current block for when we move down 1
                        aboveState = cachedChunk.getBlockState(blockpos);
                    }
                }
                // Reset above state when going to new column
                aboveState = null;
            }
        }

        return true;
    }
}
