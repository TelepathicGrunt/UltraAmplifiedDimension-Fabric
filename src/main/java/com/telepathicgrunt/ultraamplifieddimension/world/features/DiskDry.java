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

import java.util.Random;


public class DiskDry extends Feature<DiskDryConfig>
{
	public DiskDry(Codec<DiskDryConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos position, DiskDryConfig config) {
		int placedBlocks = 0;
		int radius = config.radius.getValue(random);
		if (radius > 2) {
			radius = random.nextInt(radius - 2) + 2;
		}

		BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(position);
		Chunk cachedChunk = world.getChunk(blockposMutable);

		for (int x = position.getX() - radius; x <= position.getX() + radius; ++x) {
			for (int z = position.getZ() - radius; z <= position.getZ() + radius; ++z) {

				int trueX = x - position.getX();
				int trueZ = z - position.getZ();
				if (trueX * trueX + trueZ * trueZ <= radius * radius) {
					blockposMutable.set(x, position.getY(), z);
					if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
						cachedChunk = world.getChunk(blockposMutable);

					blockposMutable.move(Direction.DOWN, config.half_height); // start at bottom of half height
					for (int y = -config.half_height; y <= config.half_height; ++y) {
						BlockState aboveBlockState = cachedChunk.getBlockState(blockposMutable.move(Direction.UP));
						BlockState blockState = cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN));

						if(!config.exposedOnly || !aboveBlockState.isOpaque()){
							for (BlockState targetBlockState : config.targets) {
								if (targetBlockState.getBlock() == blockState.getBlock()) {
									cachedChunk.setBlockState(blockposMutable, config.state, false);
									++placedBlocks;

									if(aboveBlockState.isOf(Blocks.SNOW) && !aboveBlockState.canPlaceAt(world, blockposMutable)){
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