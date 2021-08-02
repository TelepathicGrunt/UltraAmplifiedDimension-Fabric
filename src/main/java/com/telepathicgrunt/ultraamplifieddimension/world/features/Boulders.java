package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.utils.OpenSimplexNoise;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.BoulderFeatureConfig;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;


public class Boulders extends Feature<BoulderFeatureConfig> {

    protected long seed;
    protected static OpenSimplexNoise noiseGen;

    public void setSeed(long seed) {
        if (this.seed != seed || noiseGen == null) {
            noiseGen = new OpenSimplexNoise(seed);
            this.seed = seed;
        }
    }

    public Boulders(Codec<BoulderFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<BoulderFeatureConfig> context) {

        BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(context.getOrigin());
        Chunk cachedChunk = context.getWorld().getChunk(blockposMutable);

        // No boulders on trees or too high up
        if(blockposMutable.getY() > ((context.getGenerator().getWorldHeight() - context.getConfig().maxRadius) - 2))
        {
            return false;
        }

        setSeed(context.getWorld().getSeed());
        int maxRadius;
        int minRadius;
        int startRadius;
        int prevHeight = 0;

        for(int stackCount = 0; stackCount < context.getConfig().boulderStackCount; stackCount++){
            maxRadius = context.getConfig().maxRadius;
            minRadius = context.getConfig().minRadius;
            int radiusModifier = (stackCount / (int)Math.max(Math.ceil((int)(context.getConfig().boulderStackCount / context.getConfig().maxRadius) + 1), 1));
            maxRadius = Math.max(maxRadius - radiusModifier, 1);
            minRadius = Math.max(minRadius - radiusModifier, 1);
            startRadius = Math.max(context.getRandom().nextInt(maxRadius - minRadius + 1) + minRadius, 1);
            int randMax = (int) Math.max(startRadius * 0.7f, 3);
            int randMin = (int) Math.max(startRadius * 0.35f, 1);

            //we are at a valid spot to generate a boulder now. Begin generation.
            for (int currentCount = 0; currentCount < 3; ++currentCount) {

                // randomizes the x, y, or z by +1/-1/0 independently to make boulders more natural looking
                int x = Math.max(Math.min(startRadius + (context.getRandom().nextInt(3) - 1), maxRadius), minRadius);
                int y = Math.max(Math.min(startRadius + (context.getRandom().nextInt(3) - 1), maxRadius), minRadius);
                int z = Math.max(Math.min(startRadius + (context.getRandom().nextInt(3) - 1), maxRadius), minRadius);

                float calculatedDistance = (x + y + z) * 0.333F + 0.5F;

                // Create the blob of boulder
                for (BlockPos blockpos : BlockPos.iterate(blockposMutable.add(-x, -y, -z), blockposMutable.add(x, y, z))) {
                    if (blockpos.getSquaredDistance(blockposMutable) <= calculatedDistance * calculatedDistance) {

                        double noiseValue = 1;
                        if(startRadius > 2){
                            noiseValue = noiseGen.eval(blockpos.getX() * 0.035D, blockpos.getY() * 0.0075D, blockpos.getZ() * 0.035D);
                        }
                        if(blockpos.getSquaredDistance(blockposMutable) > calculatedDistance * calculatedDistance * 0.65f &&
                                noiseValue > -0.3D && noiseValue < 0.3D){
                            continue; // Rough the surface of the boulders a bit
                        }

                        if(blockpos.getX() >> 4 != cachedChunk.getPos().x || blockpos.getZ() >> 4 != cachedChunk.getPos().z)
                            cachedChunk = context.getWorld().getChunk(blockpos);

                        //adds the blocks for generation in this boulder
                        BlockState boulderBlock = GeneralUtils.getRandomEntry(context.getConfig().blockAndWeights, context.getRandom());
                        cachedChunk.setBlockState(blockpos, boulderBlock, false);
                    }
                }

                // Randomizes pos of next blob to help keep boulders from looking samey
                if(context.getConfig().boulderStackCount > 1){
                    blockposMutable.move(
                            context.getRandom().nextInt(randMax) - randMin,
                            context.getRandom().nextInt(randMax) - randMin,
                            context.getRandom().nextInt(randMax) - randMin);
                }
                else{
                    blockposMutable.move(
                            context.getRandom().nextInt(startRadius * 2) - startRadius,
                            0,
                            context.getRandom().nextInt(startRadius * 2) - startRadius);

                    blockposMutable.move(Direction.UP,
                            context.getConfig().heightmapSpread ?
                                    context.getWorld().getTopY(Heightmap.Type.OCEAN_FLOOR_WG, blockposMutable.getX(), blockposMutable.getZ())
                                        - context.getRandom().nextInt(2) - blockposMutable.getY()
                                    : -context.getRandom().nextInt( 2)
                            );
                }
            }

            prevHeight += minRadius;

            // set next boulders on top of previous to do stacking
            blockposMutable.set(context.getOrigin()).move(
                    context.getRandom().nextInt(randMax) - randMin,
                    prevHeight,
                    context.getRandom().nextInt(randMax) - randMin);

            if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                cachedChunk = context.getWorld().getChunk(blockposMutable);

            BlockState currentState = cachedChunk.getBlockState(blockposMutable);
            while(!currentState.isAir() && !currentState.isIn(BlockTags.LEAVES) && !currentState.isIn(BlockTags.LOGS)){
                blockposMutable.move(Direction.UP);
                currentState = cachedChunk.getBlockState(blockposMutable);
            }

            if(blockposMutable.getY() > ((context.getGenerator().getWorldHeight() - context.getConfig().maxRadius) - 2))
            {
                return false;
            }
        }

        //finished generating the boulder
        return true;
    }
}
