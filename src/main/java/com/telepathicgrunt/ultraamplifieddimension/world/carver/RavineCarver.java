package com.telepathicgrunt.ultraamplifieddimension.world.carver;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.BiomeContainerAccessor;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.configs.CaveConfig;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.configs.RavineConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Function;


public class RavineCarver extends Carver<RavineConfig>
{
	private final float[] WALL_LEDGES = new float[1024];
	private SimpleRegistry<Biome> biomeRegistry;

	public RavineCarver(Codec<RavineConfig> codec) {
		super(codec);
		this.alwaysCarvableBlocks = new HashSet<>(this.alwaysCarvableBlocks);
		this.alwaysCarvableBlocks.add(Blocks.NETHERRACK);
		this.alwaysCarvableBlocks.add(Blocks.ICE);
		this.alwaysCarvableBlocks.add(Blocks.SNOW_BLOCK);
		this.alwaysCarvableBlocks.add(Blocks.END_STONE);
		this.alwaysCarvableBlocks.add(Blocks.LAVA);
	}

	@Override
	public boolean shouldCarve(RavineConfig config, Random random) {
		return random.nextFloat() <= config.probability;
	}

	@Override
	public boolean carve(CarverContext carverContext, RavineConfig config, Chunk region, Function<BlockPos, Biome> biomeBlockPos, Random random, AquiferSampler aquiferSampler, ChunkPos chunkPos, BitSet mask) {
		IndexedIterable<Biome> reg = region.getBiomeArray() != null ? ((BiomeContainerAccessor)region.getBiomeArray()).uad_getBiomes() : null;
		if(reg instanceof SimpleRegistry && reg != biomeRegistry){
			biomeRegistry = (SimpleRegistry<Biome>)((BiomeContainerAccessor)region.getBiomeArray()).uad_getBiomes();
		}

		int i = (this.getBranchFactor() * 2 - 1) * 16;
		double xpos = chunkPos.getStartX() + random.nextInt(16);
		double height = config.y.get(random, carverContext);
		double zpos = chunkPos.getStartZ() + random.nextInt(16);
		float xzNoise2 = random.nextFloat() * ((float) Math.PI * 2F);
		float xzCosNoise = (random.nextFloat() - 0.5F) / 8.0F;
		float widthHeightBase = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
		int maxIteration = i - random.nextInt(i / 4);
		this.func_202535_a(carverContext, region, biomeBlockPos, random.nextLong(), chunkPos, xpos, height, zpos, widthHeightBase, xzNoise2, xzCosNoise, maxIteration, config.tallness.get(random, carverContext) / 10D, mask, config);
		return true;
	}


	private void func_202535_a(CarverContext carverContext, Chunk chunk, Function<BlockPos, Biome> biomeBlockPos, long randomSeed, ChunkPos chunkPos, double randomBlockX, double randomBlockY, double randomBlockZ, float widthHeightBase, float xzNoise2, float xzCosNoise, int maxIteration, double heightMultiplier, BitSet mask, RavineConfig config) {
		Random random = new Random(randomSeed);

		float f = 1.0F;

		for (int i = 0; i < config.cutoffHeight; ++i) {
			if (i == 0 || random.nextInt(3) == 0) {
				f = 1.0F + random.nextFloat() * random.nextFloat();
			}

			this.WALL_LEDGES[i] = f * f;
		}

		float f4 = 0.0F;
		float f1 = 0.0F;

		for (int j = 0; j < maxIteration; ++j) {
			double placementXZBound = 2D + MathHelper.sin(j * (float) Math.PI / maxIteration) * widthHeightBase;
			double placementYBound = placementXZBound * heightMultiplier;
			placementXZBound = placementXZBound * (random.nextFloat() * 0.15D + 0.65D); //thickness
			placementYBound = placementYBound * 0.8D;
			float f2 = MathHelper.cos(xzCosNoise); //multiply by 0.1f to make cylinders
			randomBlockX += MathHelper.cos(xzNoise2) * f2;
			randomBlockZ += MathHelper.sin(xzNoise2) * f2;
			xzCosNoise = xzCosNoise * 0.8F;
			xzCosNoise = xzCosNoise + f1 * 0.08F;
			xzNoise2 += f4 * 0.1F;
			f1 = f1 * 0.8F;
			f4 = f4 * 0.5F;
			f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 1.5F;
			f4 = f4 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F;
			if (random.nextInt(4) != 0) {
				if (!this.canCarveBranch(chunkPos, randomBlockX, randomBlockZ, j, maxIteration, widthHeightBase)) {
					return;
				}

				this.carveAtTarget(carverContext, chunk, biomeBlockPos, random, chunkPos, randomBlockX, randomBlockY, randomBlockZ, placementXZBound, placementYBound, mask, config);
			}
		}

	}

	protected void carveAtTarget(CarverContext carverContext, Chunk chunk, Function<BlockPos, Biome> biomeBlockPos, Random random, ChunkPos chunkPos, double xRange, double yRange, double zRange, double placementXZBound, double placementYBound, BitSet mask, RavineConfig config) {
		ChunkPos chunkPos2 = chunk.getPos();
		double d0 = chunkPos2.getCenterX();
		double d1 = chunkPos2.getCenterZ();
		if (!(xRange < d0 - 16.0D - placementXZBound * 2.0D) && !(zRange < d1 - 16.0D - placementXZBound * 2.0D) && !(xRange > d0 + 16.0D + placementXZBound * 2.0D) && !(zRange > d1 + 16.0D + placementXZBound * 2.0D)) {
			int startX = chunkPos2.getStartX();
			int startZ = chunkPos2.getStartZ();
			int i = Math.max(MathHelper.floor(xRange - placementXZBound) - startX - 1, 0);
			int j = Math.min(MathHelper.floor(xRange + placementXZBound) - startX + 1, 16);
			int minY = Math.max(MathHelper.floor(yRange - placementYBound) - 1, 9);
			int maxY = Math.min(MathHelper.floor(yRange + placementYBound) + 1, config.cutoffHeight);
			int i1 = Math.max(MathHelper.floor(zRange - placementXZBound) - startZ - 1, 0);
			int j1 = Math.min(MathHelper.floor(zRange + placementXZBound) - startZ + 1, 16);
			if (i <= j && minY <= maxY && i1 <= j1) {
				BlockState fillerBlock;
				BlockState secondaryFloorBlockstate;
				BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();
				BlockPos.Mutable blockpos$Mutableup = new BlockPos.Mutable();
				BlockPos.Mutable blockpos$Mutabledown = new BlockPos.Mutable();

				for (int xInChunk = i; xInChunk < j; ++xInChunk) {
					int x = chunkPos2.getOffsetX(xInChunk);
					double xSquaringModified = (x + 0.5D - xRange) / placementXZBound;

					for (int zInChunk = i1; zInChunk < j1; ++zInChunk) {
						int z = chunkPos2.getOffsetZ(zInChunk);
						double zSquaringModified = (z + 0.5D - zRange) / placementXZBound;
						double xzSquaredModified = (xSquaringModified * xSquaringModified) + (zSquaringModified * zSquaringModified);

						if (xzSquaredModified < 1.0D) {
							blockpos$Mutable.set(x, 60, z);

							if(maxY >= 60 || minY < 11){
								Biome biome = biomeBlockPos.apply(blockpos$Mutable);
								Identifier biomeID = biomeRegistry != null ? biomeRegistry.getId(biome) : null;
								String biomeIDString = biomeID == null ? "" : biomeID.toString();

								fillerBlock = GeneralUtils.carverFillerBlock(biomeIDString, biome);
								secondaryFloorBlockstate = GeneralUtils.carverLavaReplacement(biomeIDString, biome);
							}
							else{
								// Set defaults as this will not be used as ravine is not high or low enough
								fillerBlock = Blocks.STONE.getDefaultState();
								secondaryFloorBlockstate = Blocks.LAVA.getDefaultState();
							}

							for (int y = maxY; y > minY; --y) {
								double d4 = (y - 1 + 0.5D - yRange) / placementYBound;

								if (xzSquaredModified * this.WALL_LEDGES[y - 1] + d4 * d4 / 6.0D < 1.0D) {
									blockpos$Mutable.set(x, y, z);

									BlockState currentBlockstate = chunk.getBlockState(blockpos$Mutable);
									blockpos$Mutableup.set(blockpos$Mutable).move(Direction.UP);
									blockpos$Mutabledown.set(blockpos$Mutable).move(Direction.DOWN);
									BlockState aboveBlockstate = chunk.getBlockState(blockpos$Mutableup);

									if (y >= 60 && !aboveBlockstate.getFluidState().isEmpty()) {
										//Creates the messy but cool plateau of stone on the ocean floor 
										//above this ravine to help players locate ravines when exploring
										//ocean biomes. Also helps to break up the blandness of ocean
										//floors.

										chunk.setBlockState(blockpos$Mutable, fillerBlock, false);
										chunk.setBlockState(blockpos$Mutableup, fillerBlock, false);
										chunk.setBlockState(blockpos$Mutabledown, fillerBlock, false);
									}
									else if (!mask.get(xInChunk | zInChunk << 4 | y << 8) &&
											(this.canCarveBlock(currentBlockstate, aboveBlockstate)))
									{
										if (y < 11) {
											currentBlockstate = Blocks.LAVA.getDefaultState();
											if (secondaryFloorBlockstate != null) {
												if (secondaryFloorBlockstate.isOf(Blocks.OBSIDIAN)) {
													currentBlockstate = Blocks.MAGMA_BLOCK.getDefaultState();
												}

												if (random.nextFloat() > 0.35F) {
													if (y == 10) {
														currentBlockstate = secondaryFloorBlockstate;
													}
													else if (y == 9 && random.nextFloat() < 0.35F)
													{
														currentBlockstate = secondaryFloorBlockstate;
													}
												}
											}

											chunk.setBlockState(blockpos$Mutable, currentBlockstate, false);
										}
										else {
											//carves the ravine
											chunk.setBlockState(blockpos$Mutable, CAVE_AIR, false);
										}

										mask.set(xInChunk | zInChunk << 4 | y << 8);
									}
								}
							}
						}
					}
				}

			}
		}
	}
}
