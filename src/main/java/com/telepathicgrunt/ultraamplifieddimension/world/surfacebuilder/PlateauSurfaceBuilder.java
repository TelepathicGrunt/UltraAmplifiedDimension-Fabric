package com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.utils.OpenSimplexNoise;
import com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder.configs.QuadrarySurfaceBuilderConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.Random;


public class PlateauSurfaceBuilder extends SurfaceBuilder<QuadrarySurfaceBuilderConfig> {
    public PlateauSurfaceBuilder(Codec<QuadrarySurfaceBuilderConfig> codec) {
        super(codec);
    }

    protected long noiseSeed;
    protected OpenSimplexNoise noiseGen;

    @Override
    public void initSeed(long seed) {
        if (this.noiseSeed != seed || this.noiseGen == null) {
            this.noiseGen = new OpenSimplexNoise(seed);
        }

        this.noiseSeed = seed;
    }

    @Override
    public void generate(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int minY, long seed, QuadrarySurfaceBuilderConfig config) {
        this.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, config.getTopMaterial(), config.getUnderMaterial(), config.getUnderwaterMaterial(), config.getExtraMaterial(), seaLevel, minY);
    }

    protected void buildSurface(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, BlockState topBlock, BlockState middleBlock, BlockState bottomBlock, BlockState extraBlock, int seaLevel, int minY) {

        BlockState currentTopBlock = topBlock;
        BlockState currentMiddleBlock = middleBlock;
        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();
        int bottomLayerNoise = -1;
        int terrainThickness = 0;
        int currentDepth = 0;
        int noiseThing = (int) (noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int xInChunk = x & 15;
        int zInChunk = z & 15;
        BlockState aboveBlock = Blocks.AIR.getDefaultState();
        BlockState above2Block = Blocks.AIR.getDefaultState();
        BlockState above3Block = Blocks.AIR.getDefaultState();

        for (int y = startHeight - 1; y >= minY; --y) {
            blockpos$Mutable.set(xInChunk, y, zInChunk);
            BlockState currentBlock = chunkIn.getBlockState(blockpos$Mutable);

            if (currentBlock.getBlock() == defaultBlock.getBlock()) {

                if (bottomLayerNoise == -1) {
                    terrainThickness = 0;
                    currentDepth = 0;

                    if (noiseThing <= 0) {
                        currentTopBlock = Blocks.AIR.getDefaultState();
                        currentMiddleBlock = defaultBlock;
                    }
                    else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                        currentTopBlock = topBlock;
                        currentMiddleBlock = middleBlock;
                    }

                    if (y < seaLevel && currentTopBlock.getMaterial() == Material.AIR) {
                        if (biomeIn.getTemperature(blockpos$Mutable.set(x, y, z)) < 0.15F) {
                            currentTopBlock = Blocks.ICE.getDefaultState();
                        }
                        else {
                            currentTopBlock = defaultFluid;
                        }

                        blockpos$Mutable.set(xInChunk, y, zInChunk);
                    }

                    bottomLayerNoise = noiseThing;
                    if (y >= seaLevel - 1) {
                        if(aboveBlock.isAir()){
                            chunkIn.setBlockState(blockpos$Mutable, currentTopBlock, false);
                        }
                        else{
                            chunkIn.setBlockState(blockpos$Mutable, extraBlock, false);
                        }
                    }
                    else if (y < seaLevel - 7 - noiseThing) {
                        currentTopBlock = Blocks.AIR.getDefaultState();
                        currentMiddleBlock = defaultBlock;
                        chunkIn.setBlockState(blockpos$Mutable, bottomBlock, false);
                    }
                    else {
                        chunkIn.setBlockState(blockpos$Mutable, currentMiddleBlock, false);
                    }
                }
                else if (bottomLayerNoise > 0) {
                    --bottomLayerNoise;
                    chunkIn.setBlockState(blockpos$Mutable, currentMiddleBlock, false);
                }

                terrainThickness++;
            }
            else if (!currentBlock.isOpaque()) {

                if(currentBlock.isAir()){
                    bottomLayerNoise = -1;
                }

                // creates pillars under ledges
                // Uses terrainThickness to get thickness of terrain we are under.
                // Then when we are below the land, we start placing pillar based on that thickness while using
                // currentDepth to keep track of how far we are downward so we can stop placing land at right spot.
                double heightVariation = this.noiseGen.eval(x * 0.25D, y * 0.0075D, z * 0.25D) * 14;
                if (!aboveBlock.isAir() && !above2Block.isAir() && !above3Block.isAir() &&
                    currentDepth < ((5 * Math.pow(2, terrainThickness - 2)) + heightVariation))
                {
                    // Sets middle block to start making pillar.
                    boolean altBlock = this.noiseGen.eval(x * 0.065D, y * 0.25D, z * 0.065D) > 0.3D;
                    if(altBlock){
                        chunkIn.setBlockState(blockpos$Mutable, extraBlock, false);
                    }
                    else{
                        chunkIn.setBlockState(blockpos$Mutable, currentMiddleBlock, false);
                    }

                    // set to a solid block so this can be triggered all the way down.
                    currentBlock = Blocks.STONE.getDefaultState();
                }

                currentDepth++;
            }



            above3Block = above2Block;
            above2Block = aboveBlock;
            aboveBlock = currentBlock;
        }
    }
}