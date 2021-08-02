package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class ContainUndergroundLiquids extends Feature<DefaultFeatureConfig>
{
	public ContainUndergroundLiquids(Codec<DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}
	private MutableRegistry<Biome> BIOME_REGISTRY = null;

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		if(BIOME_REGISTRY == null){
			BIOME_REGISTRY = context.getWorld().toServerWorld().getRegistryManager().getMutable(Registry.BIOME_KEY);
		}

		BlockState replacementBlock;
		BlockState currentblock;
		BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();
		Chunk chunk = context.getWorld().getChunk(context.getOrigin().getX() >> 4, context.getOrigin().getZ() >> 4);
		int maxHeight = Math.min(61, context.getGenerator().getSeaLevel() - 1);

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				blockpos$Mutable.set(context.getOrigin().getX() + x, maxHeight, context.getOrigin().getZ() + z);
				while (blockpos$Mutable.getY() > 10) {
					currentblock = chunk.getBlockState(blockpos$Mutable);

					// move down until we hit a liquid filled block
					while (currentblock.getFluidState().isEmpty() && blockpos$Mutable.getY() > 10) {
						currentblock = chunk.getBlockState(blockpos$Mutable.move(Direction.DOWN));
					}

					//if too low, break and go to next xz coordinate
					if (blockpos$Mutable.getY() <= 10) {
						break;
					}

					// y value is now fully set for rest of code
					// checks to see if we are touching an air block
					for (Direction face : Direction.values()) {
						blockpos$Mutable.move(face);
						if(blockpos$Mutable.getY() <= maxHeight){
							//Do world instead of chunk as this could check into the next chunk over.
							currentblock = context.getWorld().getBlockState(blockpos$Mutable);
							if (currentblock.isAir()) {
								//grabs what block to use based on what biome we are in
								Biome biome = context.getWorld().getBiome(blockpos$Mutable);
								Identifier rl = BIOME_REGISTRY.getId(biome);

								replacementBlock = GeneralUtils.carverFillerBlock(rl == null ? "" : rl.toString(), biome);
								context.getWorld().setBlockState(blockpos$Mutable, replacementBlock, 2);
							}
						}
						blockpos$Mutable.move(face.getOpposite());
					}

					blockpos$Mutable.move(Direction.DOWN);
				}
			}
		}
		return true;
	}
}