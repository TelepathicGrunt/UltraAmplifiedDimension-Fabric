package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.HeightConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class VinesShort extends Feature<HeightConfig>
{

	public VinesShort(Codec<HeightConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean generate(FeatureContext<HeightConfig> context) {

		//generates vines from given position down 6 blocks if path is clear and the given position is valid
		//Also won't generate vines below Y = 15.
		int length = 0;
		boolean extendingVine = false;

		BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(context.getOrigin());
		Chunk chunk = context.getWorld().getChunk(context.getOrigin());

		while (blockposMutable.getY() > 15 && length < context.getConfig().height) {
			if (chunk.getBlockState(blockposMutable).isAir()) {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					BlockState blockState = Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), true);
					if (blockState.canPlaceAt(context.getWorld(), blockposMutable)) {
						chunk.setBlockState(blockposMutable, blockState, false);
						length++;
						extendingVine = true;
						break;
					}
					else if(extendingVine){
						BlockState aboveBlockstate = chunk.getBlockState(blockposMutable.move(Direction.UP));
						blockposMutable.move(Direction.DOWN); // Move back to original pos.
						if (aboveBlockstate.isOf(Blocks.VINE)) {
							chunk.setBlockState(blockposMutable, aboveBlockstate, false);
							length++;
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