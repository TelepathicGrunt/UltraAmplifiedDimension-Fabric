package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;


public class SnowIceAllLayers extends Feature<DefaultFeatureConfig>
{
	public SnowIceAllLayers(Codec<DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig config) {

		Biome biome = world.getBiome(pos);
		BlockPos.Mutable blockposMutable1 = new BlockPos.Mutable();
		BlockPos.Mutable blockposMutable2 = new BlockPos.Mutable();
		Chunk cachedChunk = world.getChunk(pos);

		for (int y = chunkGenerator.getWorldHeight(); y >= chunkGenerator.getSeaLevel(); --y) {

			blockposMutable1.set(pos.getX(), y, pos.getZ());
			blockposMutable2.set(blockposMutable1).move(Direction.DOWN);

			BlockState blockStateTop = cachedChunk.getBlockState(blockposMutable1);
			BlockState blockStateBottom = cachedChunk.getBlockState(blockposMutable2);
			if ((blockStateTop.isAir() || blockStateTop.isOf(Blocks.VINE)) && !blockStateBottom.isAir()) {

				if (!blockStateBottom.getFluidState().isEmpty() && biome.canSetIce(world, blockposMutable2, false)) {
					cachedChunk.setBlockState(blockposMutable2, Blocks.ICE.getDefaultState(), false);
				}

				if (SnowIceLayerHandlerFeature.doesSnowGenerate(world, biome, blockposMutable1)) {
					// Extra check to follow leaves into nearby chunks and give them the snow they would've avoided
					// Run this only when on leaves and pos is on chunk edge to minimize wasted time
					int xMod = blockposMutable1.getX() & 0x000F;
					int zMod = blockposMutable1.getZ() & 0x000F;
					if (blockStateBottom.isIn(BlockTags.LEAVES) && (xMod == 0 || xMod == 15 || zMod == 0 || zMod == 15)) {
						SnowIceLayerHandlerFeature.placeSnowOnNearbyLeaves(world, biome, blockposMutable1, cachedChunk);
					}

					cachedChunk.setBlockState(blockposMutable1, Blocks.SNOW.getDefaultState(), false);
					if (blockStateBottom.contains(SnowyBlock.SNOWY)) {
						cachedChunk.setBlockState(blockposMutable2, blockStateBottom.with(SnowyBlock.SNOWY, true), false);
					}
				}
			}
		}
		return true;
	}
}