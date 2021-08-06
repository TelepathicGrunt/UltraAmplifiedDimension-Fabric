package com.telepathicgrunt.ultraamplifieddimension.world.carver;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.BiomeContainerAccessor;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.utils.OpenSimplexNoise;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.configs.CaveConfig;
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
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.chunk.AquiferSampler;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Function;


public class CaveCavityCarver extends Carver<CaveConfig>
{

	private final float[] ledgeWidthArrayYIndex = new float[1024];
	protected static long NOISE_SEED;
	protected static OpenSimplexNoise NOISE_GEN;
	private SimpleRegistry<Biome> biomeRegistry;

	/**
	 * Sets the internal seed for this carver after we get the world seed. (Based on Nether's surface builder code)
	 */
	public static void setSeed(long seed) {
		if (NOISE_SEED != seed || NOISE_GEN == null) {
			NOISE_GEN = new OpenSimplexNoise(seed);
			NOISE_SEED = seed;
		}
	}


	public CaveCavityCarver(Codec<CaveConfig> codec) {
		super(codec);
		this.alwaysCarvableBlocks = new HashSet<>(this.alwaysCarvableBlocks);
		this.alwaysCarvableBlocks.add(Blocks.NETHERRACK);
		this.alwaysCarvableBlocks.add(Blocks.ICE);
		this.alwaysCarvableBlocks.add(Blocks.SNOW_BLOCK);
		this.alwaysCarvableBlocks.add(Blocks.END_STONE);
		this.alwaysCarvableBlocks.add(Blocks.LAVA);
	}

	/**
	 * Checks whether the entire cave can spawn or not. (Not the individual parts)
	 */
	@Override
	public boolean shouldCarve(CaveConfig config, Random random) {
		return random.nextFloat() <= config.probability;
	}

	@Override
	public boolean carve(CarverContext carverContext, CaveConfig config, Chunk region, Function<BlockPos, Biome> biomeBlockPos, Random random, AquiferSampler aquiferSampler, ChunkPos chunkPos, BitSet mask) {
		BiomeArray biomeArray = region.getBiomeArray();
		IndexedIterable<Biome> biomeIterable = biomeArray != null ? ((BiomeContainerAccessor)biomeArray).uad_getBiomes() : null;
		if(biomeIterable != biomeRegistry && biomeIterable instanceof SimpleRegistry<Biome> regCasted){
			biomeRegistry = regCasted;
		}

		int maxIterations = (this.getBranchFactor() * 2 - 1) * 16;
		double xpos = chunkPos.getStartX() + random.nextInt(16);
		double height = random.nextInt(random.nextInt(2) + 1) + 34;
		double zpos = chunkPos.getStartZ() + random.nextInt(16);
		float xzNoise2 = random.nextFloat() * ((float) Math.PI);
		float xzCosNoise = (random.nextFloat() - 0.5F) / 16.0F;
		float widthHeightBase = (random.nextFloat() + random.nextFloat()) / 16; // width And Height Modifier
		this.carveCavity(carverContext, region, biomeBlockPos, random, aquiferSampler, chunkPos.x, chunkPos.z, xpos, height, zpos, widthHeightBase, xzNoise2, xzCosNoise, 0, maxIterations, random.nextDouble() + 20D, mask, config);
		return true;
	}


	private void carveCavity(CarverContext carverContext, Chunk chunk, Function<BlockPos, Biome> biomeBlockPos, Random random, AquiferSampler aquiferSampler, int mainChunkX, int mainChunkZ, double randomBlockX, double randomBlockY, double randomBlockZ, float widthHeightBase, float xzNoise2, float xzCosNoise, int startIteration, int maxIteration, double heightMultiplier, BitSet mask, CaveConfig config) {
		float ledgeWidth = 1.0F;

		// CONTROLS THE LEDGES' WIDTH! FINALLY FOUND WHAT THIS JUNK DOES
		for (int currentHeight = 0; currentHeight <= config.cutoffHeight; ++currentHeight) {

			//attempt at creating dome ceilings
			if (currentHeight > 44 && currentHeight < 60) {
				ledgeWidth = 1.0F + random.nextFloat() * 0.3f;
				ledgeWidth = (float) (ledgeWidth + Math.max(0, Math.pow((currentHeight - 44) * 0.15F, 2)));
			}

			//normal ledges on walls
			else {
				if (currentHeight == 0 || random.nextInt(3) == 0)
				{
					ledgeWidth = 1.0F + random.nextFloat() * 0.5F;
				}
			}

			this.ledgeWidthArrayYIndex[currentHeight] = ledgeWidth;
		}

		double placementXZBound = 2D + MathHelper.sin(1 * (float) Math.PI / maxIteration) * widthHeightBase;
		double placementYBound = placementXZBound * heightMultiplier;
		placementXZBound = placementXZBound * 32D; // thickness of the "room" itself
		placementYBound = placementYBound * 2.2D;

		this.carveAtTarget(carverContext, chunk, biomeBlockPos, random, mainChunkX, mainChunkZ, randomBlockX, randomBlockY, randomBlockZ, placementXZBound, placementYBound, mask, config);
	}


	protected void carveAtTarget(CarverContext carverContext, Chunk chunk, Function<BlockPos, Biome> biomeBlockPos, Random random, int mainChunkX, int mainChunkZ, double xRange, double yRange, double zRange, double placementXZBound, double placementYBound, BitSet mask, CaveConfig config) {
		ChunkPos chunkPos = chunk.getPos();
		double xPos = chunkPos.getCenterX();
		double zPos = chunkPos.getCenterZ();
		double multipliedXZBound = placementXZBound * 2.0D;

		if (!(xRange < xPos - 16.0D - multipliedXZBound) && !(zRange < zPos - 16.0D - multipliedXZBound) && !(xRange > xPos + 16.0D + multipliedXZBound) && !(zRange > zPos + 16.0D + multipliedXZBound)) {

			int startX = chunkPos.getStartX();
			int startZ = chunkPos.getStartZ();
			int xMin = Math.max(MathHelper.floor(xRange - placementXZBound) - startX - 1, 0);
			int xMax = Math.min(MathHelper.floor(xRange + placementXZBound) - startX + 1, 16);
			int yMin = Math.max(MathHelper.floor(yRange - placementYBound) - 1, 5);
			int yMax = Math.min(MathHelper.floor(yRange + placementYBound) + 1, config.cutoffHeight);
			int zMin = Math.max(MathHelper.floor(zRange - placementXZBound) - startZ - 1, 0);
			int zMax = Math.min(MathHelper.floor(zRange + placementXZBound) - startZ + 1, 16);
			if (xMin <= xMax && yMin <= yMax && zMin <= zMax) {
				BlockState fillerBlock;
				BlockState secondaryFloorBlockstate;
				BlockState currentBlockstate;
				BlockState aboveBlockstate;
				BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();
				BlockPos.Mutable blockpos$Mutableup = new BlockPos.Mutable();
				BlockPos.Mutable blockpos$Mutabledown = new BlockPos.Mutable();
				double stalagmiteDouble = 0;

				for (int xInChunk = xMin; xInChunk < xMax; ++xInChunk) {
					int x = chunkPos.getOffsetX(xInChunk);
					double xSquaringModified = (x + 0.5D - xRange) / placementXZBound;

					for (int zInChunk = zMin; zInChunk < zMax; ++zInChunk) {
						int z = chunkPos.getOffsetZ(zInChunk);
						double zSquaringModified = (z + 0.5D - zRange) / placementXZBound;
						double xzSquaredModified = xSquaringModified * xSquaringModified + zSquaringModified * zSquaringModified;

						if (xzSquaredModified < 1.0D) {
							if (yMax < yMin) {
								continue;
							}
							blockpos$Mutable.set(x, 60, z);

							if(yMax >= 60 || yMin < 11){
								Biome biome = biomeBlockPos.apply(blockpos$Mutable);
								Identifier biomeID = biomeRegistry != null ? biomeRegistry.getId(biome) : null;
								String biomeIDString = biomeID == null ? "" : biomeID.toString();

								fillerBlock = GeneralUtils.carverFillerBlock(biomeIDString, biome);
								secondaryFloorBlockstate = GeneralUtils.carverLavaReplacement(biomeIDString, biome);
							}
							else{
								// Set defaults as this will not be used as cave is not high or low enough
								fillerBlock = Blocks.STONE.getDefaultState();
								secondaryFloorBlockstate = Blocks.LAVA.getDefaultState();
							}

							for (int y = yMax; y > yMin; y--) {

								// Skip already carved spaces
								if(mask.get(xInChunk | zInChunk << 4 | y << 8)){
									continue;
								}

								// sets a trial and error value that widens base of pillar and makes paths
								// through lava that look good
								double yPillarModifier = y;

								if(y > 30) {
									//increase multiplier on end to widen top of pillar
									yPillarModifier = (Math.pow((yPillarModifier - 30D) * 0.033333D, 2) * 30D - (y * 0.016666D)) * 18D;
								}
								else {
									//increase multiplier on end to widen bottom of pillar
									yPillarModifier = Math.pow((Math.pow(yPillarModifier - 30D, 2) * 0.033333D), 2) * 2.8D;
								}
								
								if (yPillarModifier <= 0) {
									// prevents divide by 0 or by negative numbers (decreasing negative would make
									// more terrain be carved instead of not carve)
									yPillarModifier = 0.00001D;
								}
								else if (y < 10) {
									// creates a deep lava pool that starts 2 blocks deep automatically at edges.
									yPillarModifier -= 50D;
								}

								
								//limits calling pillar and stalagmite perlin generators to reduce gen time
								if (y < 60) {
									// Creates pillars that are widen at bottom.
									//
									// simplex field creates the main body for pillar by stepping slowly through x
									// and z and extremely slowly through y.
									// Then subtracted modified target height to flatten bottom of pillar to
									// make a path through lava.
									// Next, adds a random value to add some noise to the pillar.
									// And lastly, sets the greater than value to be very low so most of the cave gets carved
									// out.
									//
									//Increase step in X and Z to make pillars less frequent but thicker
									boolean flagPillars = NOISE_GEN.eval(
																x * 0.045D + (x % 16) * 0.002D, 
																z * 0.045D + (z % 16) * 0.002D, 
																y * 0.015D) - (yPillarModifier * 0.001D) + 
														 (random.nextDouble() * 0.01D) 
														 > -0.32D;

									if (!flagPillars) {
										//skip position if we are in pillar space
										continue;
									}

									//limits calling stalagmite perlin generators to reduce gen time
									if (y > 30) {
										// Creates large stalagmites that cannot reach floor of cavern.
										//
										// simplex field creates the main stalagmite shape and placement by stepping
										// though x and z pretty fast and through y very slowly.
										// Then adds 500/y so that as the y value gets lower, the more area gets carved
										// which sets the limit on how far down the stalagmites can go.
										// Next, add a random value to add some noise to the pillar.
										// And lastly, sets the greater than value to be high so more stalagmites can be made
										// while the 500/y has already carved out the rest of the cave.
										//
										//Increase step in X and Z to decrease number of stalagmites and make them slightly thicker
										stalagmiteDouble = NOISE_GEN.eval(x * 0.25D, z * 0.25D, 0) * 15.0D + (500D / (y));

										//adds more tiny stalagmites to ceiling
										if (y > 48) {
											stalagmiteDouble -= (y - 53D) / 3D;
										}

										//decrease constant to make stalagmites smaller and thinner
										boolean flagStalagmites = stalagmiteDouble > 5.3D;

										if (!flagStalagmites) {
											//skip position if we are in stalagmite space
											continue;
										}
									}
								}

								double ySquaringModified = (y - 1 + 0.5D - yRange) / placementYBound;

								// Where the pillar flag and stalagmite flag both flagged this block to be
								// carved, begin carving.
								// Thus the pillar and stalagmite is what is left after carving.
								if (xzSquaredModified * this.ledgeWidthArrayYIndex[y - 1] + ySquaringModified * ySquaringModified / 6.0D + random.nextFloat() * 0.015f < 1.0D) {

									blockpos$Mutable.set(x, y, z);
									currentBlockstate = chunk.getBlockState(blockpos$Mutable);
									blockpos$Mutableup.set(blockpos$Mutable).move(Direction.UP);
									blockpos$Mutabledown.set(blockpos$Mutable).move(Direction.DOWN);
									aboveBlockstate = chunk.getBlockState(blockpos$Mutableup);

									if (y >= 60) {
										//Creates the messy but cool plateau of stone on the ocean floor 
										//above this cave to help players locate caves when exploring
										//ocean biomes. Also helps to break up the blandness of ocean
										//floors.

										if (!currentBlockstate.getFluidState().isEmpty()) {
											chunk.setBlockState(blockpos$Mutable, fillerBlock, false);
										}
										else if (!aboveBlockstate.getFluidState().isEmpty()) {
											chunk.setBlockState(blockpos$Mutable, fillerBlock, false);
											chunk.setBlockState(blockpos$Mutableup, fillerBlock, false);
											chunk.setBlockState(blockpos$Mutabledown, fillerBlock, false);
										}
									}
									else if (this.canCarveBlock(currentBlockstate, aboveBlockstate))
									{

										if (y < 11) {
											currentBlockstate = Blocks.LAVA.getDefaultState();
											if (secondaryFloorBlockstate != null) {
												if (secondaryFloorBlockstate.isOf(Blocks.OBSIDIAN)) {
													currentBlockstate = Blocks.MAGMA_BLOCK.getDefaultState();
												}

												if (stalagmiteDouble > 13.5D) {
													if (y == 10) {
														currentBlockstate = secondaryFloorBlockstate;
													}
													else if (y == 9 && random.nextBoolean()) {
														currentBlockstate = secondaryFloorBlockstate;
													}
												}
											}

											chunk.setBlockState(blockpos$Mutable, currentBlockstate, false);
										}
										else {
											//carves the cave
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
