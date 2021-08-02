package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class SnowIceTopLayer extends Feature<DefaultFeatureConfig>
{
	public SnowIceTopLayer(Codec<DefaultFeatureConfig> p_i51435_1_) {
		super(p_i51435_1_);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		Biome biome = context.getWorld().getBiome(context.getOrigin());
		BlockPos.Mutable blockposMutable1 = new BlockPos.Mutable();
		BlockPos.Mutable blockposMutable2 = new BlockPos.Mutable();
		Chunk cachedChunk = context.getWorld().getChunk(context.getOrigin());

		int y = cachedChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, context.getOrigin().getX(), context.getOrigin().getZ()) + 1;
		blockposMutable1.set(context.getOrigin().getX(), y, context.getOrigin().getZ());
		blockposMutable2.set(blockposMutable1).move(Direction.DOWN);

		if (biome.canSetIce(context.getWorld(), blockposMutable2, false)) {
			cachedChunk.setBlockState(blockposMutable2, Blocks.ICE.getDefaultState(), false);
		}

		if (biome.canSetSnow(context.getWorld(), blockposMutable1)) {
			BlockState blockStateBottom = cachedChunk.getBlockState(blockposMutable2);

			// Extra check to follow leaves into nearby chunks and give them the snow they would've avoided
			// Run this only when on leaves and pos is on chunk edge to minimize wasted time
			int xMod = blockposMutable1.getX() & 0x000F;
			int zMod = blockposMutable1.getZ() & 0x000F;
			if (blockStateBottom.isIn(BlockTags.LEAVES) && (xMod == 0 || xMod == 15 || zMod == 0 || zMod == 15)) {
				SnowIceLayerHandlerFeature.placeSnowOnNearbyLeaves(context.getWorld(), biome, blockposMutable1, cachedChunk);
			}

			cachedChunk.setBlockState(blockposMutable1, Blocks.SNOW.getDefaultState(), false);
			BlockState blockstate = cachedChunk.getBlockState(blockposMutable2);
			if (blockstate.contains(SnowyBlock.SNOWY)) {
				cachedChunk.setBlockState(blockposMutable2, blockstate.with(SnowyBlock.SNOWY, Boolean.TRUE), false);
			}
		}
		return true;
	}
}