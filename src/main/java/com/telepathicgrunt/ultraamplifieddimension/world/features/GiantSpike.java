package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.GiantSpikeConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class GiantSpike extends Feature<GiantSpikeConfig> {

    public GiantSpike(Codec<GiantSpikeConfig> configFactory) {
        super(configFactory);
    }

    //ice spike code was changed to only generate taller ice spikes and to have spikes go all the way to Y = 5 if path is clear.
    @Override
    public boolean generate(FeatureContext<GiantSpikeConfig> context) {

    	// Prevent feature stacking
    	BlockState startBlockState = context.getWorld().getBlockState(context.getOrigin());
        if (!context.getConfig().target.test(startBlockState, context.getRandom()) &&
			startBlockState != context.getConfig().aboveSeaState &&
			startBlockState != context.getConfig().belowSeaState)
        {
            return false;
        }

		BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable().set(context.getOrigin());
		mutableBlockPos.move(Direction.UP, context.getRandom().nextInt(4));
		int headHeightOffset = context.getRandom().nextInt(4) + 7;
		int tailWidthOffset = headHeightOffset / 4 + context.getRandom().nextInt(2);
		int finalHeight;

		if (context.getRandom().nextFloat() < context.getConfig().giantSpikeChance) {
			int extraHeight = context.getRandom().nextInt(context.getConfig().giantSpikeMaxHeight - context.getConfig().giantSpikeMinHeight) + context.getConfig().giantSpikeMinHeight;

			//if ice spike has the potential to generate too close to top of world, then shrink it so it fits in world
			if (mutableBlockPos.getY() + extraHeight > context.getGenerator().getWorldHeight() - 10) {
				mutableBlockPos.move(Direction.UP, context.getGenerator().getWorldHeight() - 10 - mutableBlockPos.getY());
			}
			else {
				mutableBlockPos.move(Direction.UP, extraHeight);
			}

		}

		finalHeight = mutableBlockPos.getY();
		for (int y = 0; y < headHeightOffset; ++y) {
			float maxWidth = (1.0F - (float) y / (float) headHeightOffset) * tailWidthOffset;
			int range = MathHelper.ceil(maxWidth);

			for (int x = -range; x <= range; ++x) {
				float xWidth = MathHelper.abs(x) - 0.25F;

				for (int z = -range; z <= range; ++z) {
					float zWidth = MathHelper.abs(z) - 0.25F;

					if ((x == 0 && z == 0 || (xWidth * xWidth) + (zWidth * zWidth) <= maxWidth * maxWidth) &&
						((x != -range && x != range && z != -range && z != range) || context.getRandom().nextFloat() <= 0.75F))
					{
						BlockPos topPos = mutableBlockPos.add(x, y, z);
						BlockPos bottomPos = mutableBlockPos.add(x, -y, z);
						BlockState currentBlockState = context.getWorld().getBlockState(topPos);
						if (context.getConfig().target.test(currentBlockState, context.getRandom()) && topPos.getY() >= context.getGenerator().getSeaLevel() - 1) {
							this.setBlockState( context.getWorld(), topPos, context.getConfig().aboveSeaState);
						}
						else if (!currentBlockState.getFluidState().isEmpty()) {
							this.setBlockState( context.getWorld(), topPos, context.getConfig().belowSeaState);
						}

						if (y != 0 && range > 1) {
							currentBlockState = context.getWorld().getBlockState(bottomPos);

							if (context.getConfig().target.test(currentBlockState, context.getRandom()) && bottomPos.getY() >= context.getGenerator().getSeaLevel() - 1) {
								this.setBlockState( context.getWorld(), bottomPos, context.getConfig().aboveSeaState);
							}
							else if (!currentBlockState.getFluidState().isEmpty()) {
								this.setBlockState( context.getWorld(), bottomPos, context.getConfig().belowSeaState);
							}
						}
					}
				}
			}
		}

		int maxWidth = 1;
		for (int x = -maxWidth; x <= maxWidth; ++x) {
			for (int z = -maxWidth; z <= maxWidth; ++z) {
				mutableBlockPos.set(context.getOrigin().getX() + x, finalHeight - 1, context.getOrigin().getZ() + z);
				int modeThreshold = Integer.MAX_VALUE;
				boolean placingMode = true;

				if (Math.abs(x) == maxWidth && Math.abs(z) == maxWidth) {
					modeThreshold = context.getRandom().nextInt(5);
				}


				//how far down the ice spike can generate
				while (mutableBlockPos.getY() > 5) {
					BlockState currentBlockState = context.getWorld().getBlockState(mutableBlockPos);
					if (mutableBlockPos.getY() != finalHeight -1 && !context.getConfig().target.test(currentBlockState, context.getRandom())) {
						break;
					}

					if(placingMode){
						if (mutableBlockPos.getY() < context.getGenerator().getSeaLevel() - 1) {
							this.setBlockState(context.getWorld(), mutableBlockPos, context.getConfig().belowSeaState);
						}
						else {
							this.setBlockState(context.getWorld(), mutableBlockPos, context.getConfig().aboveSeaState);
						}
					}
					else if(mutableBlockPos.getY() == context.getGenerator().getSeaLevel() - 1){
						this.setBlockState(context.getWorld(), mutableBlockPos, context.getConfig().aboveSeaState);
					}

					mutableBlockPos.move(Direction.DOWN);

					// Using rng spam, this is what makes the missing blocks on the corners of the Ice Spike pillar.
					// The blocks will be placed in a row or missing in a row.
					if (modeThreshold <= 0) {
						++modeThreshold;

						if(placingMode){
							placingMode = false;
							modeThreshold = -(context.getRandom().nextInt(6) - 1);
						}
					}
					else {
						--modeThreshold;

						if(!placingMode){
							placingMode = true;
							modeThreshold = context.getRandom().nextInt(5);
						}
					}
				}
			}
		}

		return true;
    }
}
