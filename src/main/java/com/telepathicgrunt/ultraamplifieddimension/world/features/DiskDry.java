package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.DiskDryConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class DiskDry extends Feature<DiskDryConfig>
{
	public DiskDry(Codec<DiskDryConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskDryConfig> context) {
		int placedBlocks = 0;
		int radius = context.getConfig().radius.get(context.getRandom());
		if (radius > 2) {
			radius = context.getRandom().nextInt(radius - 2) + 2;
		}

		BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(context.getOrigin());
		Chunk cachedChunk = context.getWorld().getChunk(blockposMutable);

		for (int x = context.getOrigin().getX() - radius; x <= context.getOrigin().getX() + radius; ++x) {
			for (int z = context.getOrigin().getZ() - radius; z <= context.getOrigin().getZ() + radius; ++z) {

				int trueX = x - context.getOrigin().getX();
				int trueZ = z - context.getOrigin().getZ();
				if (trueX * trueX + trueZ * trueZ <= radius * radius) {
					blockposMutable.set(x, context.getOrigin().getY(), z);
					if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
						cachedChunk = context.getWorld().getChunk(blockposMutable);

					blockposMutable.move(Direction.DOWN, context.getConfig().half_height); // start at bottom of half height
					for (int y = -context.getConfig().half_height; y <= context.getConfig().half_height; ++y) {
						BlockState aboveBlockState = cachedChunk.getBlockState(blockposMutable.move(Direction.UP));
						BlockState blockState = cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN));

						if(!context.getConfig().exposedOnly || !aboveBlockState.isOpaque()){
							for (BlockState targetBlockState : context.getConfig().targets) {
								if (targetBlockState.getBlock() == blockState.getBlock()) {
									cachedChunk.setBlockState(blockposMutable, context.getConfig().state, false);
									++placedBlocks;

									if(aboveBlockState.isOf(Blocks.SNOW) && !aboveBlockState.canPlaceAt( context.getWorld(), blockposMutable)){
										cachedChunk.setBlockState(blockposMutable.move(Direction.UP), Blocks.AIR.getDefaultState(), false);
										blockposMutable.move(Direction.DOWN);
									}
									break;
								}
							}
						}

						blockposMutable.move(Direction.UP);
					}
				}
			}
		}

		return placedBlocks > 0;
	}
}