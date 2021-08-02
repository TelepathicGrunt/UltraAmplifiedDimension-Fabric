package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class VinesLong extends Feature<DefaultFeatureConfig>
{

	public VinesLong(Codec<DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {

		//generates vines from given position all the way down to sealevel + 1 if path is clear and the given position is valid
		BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(context.getOrigin());
		Chunk chunk = context.getWorld().getChunk(context.getOrigin());

		while (blockposMutable.getY() > context.getGenerator().getSeaLevel() + 1) {
			if (chunk.getBlockState(blockposMutable).isAir()) {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					BlockState blockState = Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), true);
					if (blockState.canPlaceAt( context.getWorld(), blockposMutable)) {
						chunk.setBlockState(blockposMutable, blockState, false);
						break;
					}
					else {
						BlockState aboveBlockstate = chunk.getBlockState(blockposMutable.move(Direction.UP));
						blockposMutable.move(Direction.DOWN); // Move back to original pos.

						if (aboveBlockstate.isOf(Blocks.VINE)) {
							chunk.setBlockState(blockposMutable, aboveBlockstate, false);
							break;
						}
					}
				}
			}

			blockposMutable.move(Direction.DOWN);
		}

		return true;
	}
}
