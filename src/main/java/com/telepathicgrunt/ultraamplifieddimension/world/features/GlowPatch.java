package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.CountConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GlowPatch extends Feature<CountConfig> {

    private static Map<BlockState, BlockState> GLOWBLOCK_MAP;


    /**
     * Have to make this map in UltraAmplified setup method since the blocks needs to be initialized first
     */
    public static void setFillerMap() {
        if (GLOWBLOCK_MAP == null) {
            GLOWBLOCK_MAP = new HashMap<>();

            GLOWBLOCK_MAP.put(Blocks.DIRT.getDefaultState(), UADBlocks.GLOWDIRT.getDefaultState());
            GLOWBLOCK_MAP.put(Blocks.COARSE_DIRT.getDefaultState(), UADBlocks.COARSE_GLOWDIRT.getDefaultState());
            GLOWBLOCK_MAP.put(Blocks.GRASS_BLOCK.getDefaultState(), UADBlocks.GLOWGRASS_BLOCK.getDefaultState());
            GLOWBLOCK_MAP.put(Blocks.MYCELIUM.getDefaultState(), UADBlocks.GLOWMYCELIUM.getDefaultState());
            GLOWBLOCK_MAP.put(Blocks.STONE.getDefaultState(), UADBlocks.GLOWSTONE_ORE.getDefaultState());
            GLOWBLOCK_MAP.put(Blocks.PODZOL.getDefaultState(), UADBlocks.GLOWPODZOL.getDefaultState());
            GLOWBLOCK_MAP.put(Blocks.SAND.getDefaultState(), UADBlocks.GLOWSAND.getDefaultState());
            GLOWBLOCK_MAP.put(Blocks.RED_SAND.getDefaultState(), UADBlocks.RED_GLOWSAND.getDefaultState());
        }
    }


    public GlowPatch(Codec<CountConfig> configFactory) {
        super(configFactory);
        setFillerMap();
    }


    @Override
    public boolean generate(FeatureContext<CountConfig> context) {
        BlockPos.Mutable blockposMutable = new BlockPos.Mutable();
        Chunk cachedChunk = context.getWorld().getChunk(context.getOrigin());

        // Cancel glowpatch spawning if too close to visible sky.
        for(Direction direction : Direction.Type.HORIZONTAL){
            blockposMutable.set(context.getOrigin()).move(direction, 6);
            int nearbyLandY = context.getWorld().getTopY(Heightmap.Type.OCEAN_FLOOR_WG, blockposMutable.getX(), blockposMutable.getZ());

            if(nearbyLandY < context.getOrigin().getY()){
                return false;
            }
        }

        // tries as times specified to convert a randomly chosen nearby block
        for (int attempts = 0; attempts < context.getConfig().count; ++attempts) {
            // clustered around the center the most
            int gausX = (int) (Math.max(Math.min(context.getRandom().nextGaussian() * 3, 15), -15)); // range of -15 to 15
            int gausY = context.getRandom().nextInt(4) - context.getRandom().nextInt(4); // range of -4 to 4
            int gausZ = (int) (Math.max(Math.min(context.getRandom().nextGaussian() * 3, 15), -15)); // range of -15 to 15

            blockposMutable.set(context.getOrigin()).move(gausX, gausY + 1, gausZ);
            if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                cachedChunk = context.getWorld().getChunk(blockposMutable);

            // Glowstuff cannot be placed in sunlight
            // Need cache for heightmap checking
            //if(world.getLightFor(LightType.SKY, blockpos$Mutable) > 10) continue;
            //if (chunkGenerator.getHeight(blockpos$Mutable.getX(), blockpos$Mutable.getX(), Heightmap.Type.OCEAN_FLOOR) - 1 != blockpos$Mutable.getY()) {

            BlockState chosenAboveBlock = cachedChunk.getBlockState(blockposMutable);
            BlockState chosenBlock = cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN));

            // turns stone into glowstone ore even if no air above
            if (chosenBlock.getBlock() == Blocks.STONE) {
                cachedChunk.setBlockState(blockposMutable, GLOWBLOCK_MAP.get(chosenBlock), false);
            }
            // turns valid surface blocks with air above into glowstone variants
            else if (GLOWBLOCK_MAP.containsKey(chosenBlock) && chosenAboveBlock.getMaterial() == Material.AIR) {
                cachedChunk.setBlockState(blockposMutable, GLOWBLOCK_MAP.get(chosenBlock), false);
            }
        }

        //debugging
        //UltraAmplified.Logger.log(Level.DEBUG, "Glowpatch at "+pos.getX() +", "+pos.getY()+", "+pos.getZ());
        return true;
    }

}
