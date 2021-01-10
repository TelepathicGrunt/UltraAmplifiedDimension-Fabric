package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADFeatures;
import com.telepathicgrunt.ultraamplifieddimension.utils.BiomeSetsHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;


public class SnowIceLayerHandlerFeature extends Feature<DefaultFeatureConfig>
{
	public SnowIceLayerHandlerFeature(Codec<DefaultFeatureConfig> p_i51435_1_) {
		super(p_i51435_1_);
	}

	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos position, DefaultFeatureConfig config) {
		BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();

		for (int xOffset = 0; xOffset < 16; xOffset++) {
			for (int zOffset = 0; zOffset < 16; zOffset++) {
				blockpos$Mutable.set(position).move(xOffset, 0, zOffset);
				Biome biome = world.getBiome(blockpos$Mutable);
				if (BiomeSetsHelper.FROZEN_BIOMES.contains(biome)) {
					UADFeatures.SNOW_ICE_ALL_LAYERS.generate(world, generator, random, blockpos$Mutable, config);
				}
				else if (BiomeSetsHelper.COLD_OCEAN_BIOMES.contains(biome)) {
					UADFeatures.SNOW_LAYER_WITHOUT_ICE.generate(world, generator, random, blockpos$Mutable, config);
				}
				else {
					UADFeatures.SNOW_ICE_TOP_LAYER.generate(world, generator, random, blockpos$Mutable, config);
				}
			}
		}

		return true;
	}

	public static void placeSnowOnNearbyLeaves(StructureWorldAccess world, Biome biome, BlockPos.Mutable blockposMutable1, Chunk cachedChunk) {
		BlockPos.Mutable nearbyPos = new BlockPos.Mutable();
		BlockPos.Mutable nearbyPosBelow = new BlockPos.Mutable();
		Chunk chunk = cachedChunk;
		int range = 5;
		for (int xOffset = -range; xOffset <= range; xOffset++) {
			for (int zOffset = -range; zOffset <= range; zOffset++) {
				nearbyPos.set(blockposMutable1).move(xOffset, 0, zOffset);
				nearbyPosBelow.set(nearbyPos).move(Direction.DOWN);

				// Only run in chunks outside our current chunk
				if (nearbyPos.getX() >> 4 != cachedChunk.getPos().x || nearbyPos.getZ() >> 4 != cachedChunk.getPos().z){
					if(nearbyPos.getX() >> 4 != chunk.getPos().x || nearbyPos.getZ() >> 4 != chunk.getPos().z){
						chunk = world.getChunk(nearbyPos);
					}

					BlockState nearbyBlockStateTop = chunk.getBlockState(nearbyPos);
					BlockState nearbyBlockStateBottom = chunk.getBlockState(nearbyPosBelow);

					if ((nearbyBlockStateTop.isAir() || nearbyBlockStateTop.isOf(Blocks.VINE)) &&
						doesSnowGenerate(world, biome, nearbyPos) &&
						nearbyBlockStateBottom.isIn(BlockTags.LEAVES))
					{
						chunk.setBlockState(nearbyPos, Blocks.SNOW.getDefaultState(), false);

						if (nearbyBlockStateBottom.contains(SnowyBlock.SNOWY)) {
							chunk.setBlockState(nearbyPosBelow, nearbyBlockStateBottom.with(SnowyBlock.SNOWY, true), false);
						}
					}
				}
			}
		}
	}

	public static boolean doesSnowGenerate(WorldView worldIn, Biome biome, BlockPos pos) {
		if (!(biome.getTemperature(pos) >= 0.15F)) {
			if (pos.getY() >= 0 && pos.getY() < 256) {
				return Blocks.SNOW.getDefaultState().canPlaceAt(worldIn, pos);
			}

		}
		return false;
	}
}