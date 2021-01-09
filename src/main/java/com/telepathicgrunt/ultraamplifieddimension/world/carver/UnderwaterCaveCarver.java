package com.telepathicgrunt.ultraamplifieddimension.world.carver;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.BiomeContainerAccessor;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CaveCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;


public class UnderwaterCaveCarver extends CaveCarver
{
	private SimpleRegistry<Biome> biomeRegistry;

	public UnderwaterCaveCarver(Codec<ProbabilityConfig> codec)
	{
		super(codec, 73);
		this.alwaysCarvableBlocks = new HashSet<>(this.alwaysCarvableBlocks);
		this.alwaysCarvableBlocks.add(Blocks.SAND);
		this.alwaysCarvableBlocks.add(Blocks.GRAVEL);
		this.alwaysCarvableBlocks.add(Blocks.WATER);
		this.alwaysCarvableBlocks.add(Blocks.LAVA);
		this.alwaysCarvableBlocks.add(Blocks.OBSIDIAN);
		this.alwaysCarvableBlocks.add(Blocks.AIR);
		this.alwaysCarvableBlocks.add(Blocks.CAVE_AIR);
	}


	@Override
	public boolean shouldCarve(Random random, int chunkX, int chunkZ, ProbabilityConfig config)
	{
		return random.nextFloat() <= config.probability;
	}


	@Override
	protected boolean isRegionUncarvable(Chunk chunkIn, int chunkX, int chunkZ, int minX, int maxX, int minY, int maxY, int minZ, int maxZ)
	{
		return false;
	}

	@Override
	protected boolean carveAtPoint(Chunk chunk, Function<BlockPos, Biome> biomePos, BitSet carvingMask, Random rand, BlockPos.Mutable p_230358_5_, BlockPos.Mutable p_230358_6_, BlockPos.Mutable p_230358_7_, int p_230358_8_, int p_230358_9_, int p_230358_10_, int posX, int posZ, int p_230358_13_, int posY, int p_230358_15_, MutableBoolean isSurface) {
		IndexedIterable<Biome> reg = chunk.getBiomeArray() != null ? ((BiomeContainerAccessor)chunk.getBiomeArray()).getField_25831() : null;
		if(reg instanceof SimpleRegistry && reg != biomeRegistry){
			biomeRegistry = (SimpleRegistry<Biome>)((BiomeContainerAccessor)chunk.getBiomeArray()).getField_25831();
		}

		return func_222728_a(biomeRegistry, biomePos, chunk, carvingMask, rand, p_230358_5_, p_230358_8_, p_230358_9_, p_230358_10_, posX, posZ, p_230358_13_, posY, p_230358_15_, this.alwaysCarvableBlocks);
	}


	protected static boolean func_222728_a(SimpleRegistry<Biome> biomeRegistry, Function<BlockPos, Biome> biomePos, Chunk chunk, BitSet carvingMask, Random random, BlockPos.Mutable MutableIn, int minHeight, int chunkX, int chunkZ, int x, int z, int maskY, int y, int atomicBoolean, Set<Block> carvableBlocks) {
		return carvingBlock(biomeRegistry, biomePos, chunk, carvingMask, random, MutableIn, minHeight, chunkX, chunkZ, x, z, maskY, y, atomicBoolean, carvableBlocks);
	}


	protected static boolean carvingBlock(SimpleRegistry<Biome> biomeRegistry, Function<BlockPos, Biome> biomeBlockPos, Chunk chunkIn, BitSet carvingMask, Random random, BlockPos.Mutable mutableBlockPos, int minHeight, int chunkX, int chunkZ, int xStart, int zStart, int maskY, int y, int atomicBoolean, Set<Block> carvableBlocks) {
		if (y >= minHeight) {
			return false;
		}
		else {
			mutableBlockPos.set(xStart, y, zStart);
			BlockState lavaBlock;

			if(y < 11){
				Biome biome = biomeBlockPos.apply(mutableBlockPos);
				Identifier biomeID = biomeRegistry != null ? biomeRegistry.getId(biome) : null;
				String biomeIDString = biomeID == null ? "" : biomeID.toString();

				lavaBlock = GeneralUtils.carverLavaReplacement(biomeIDString, biome);
			}
			else{
				// Set defaults as this will not be used as cave is not low enough
				lavaBlock = Blocks.LAVA.getDefaultState();
			}

			BlockState blockstate = chunkIn.getBlockState(mutableBlockPos);
			if (!carvableBlocks.contains(blockstate.getBlock())) {
				return false;
			}
			else if (y == 10) {
				float f = random.nextFloat();
				if (f < 0.25D && lavaBlock != Blocks.OBSIDIAN.getDefaultState()) {
					chunkIn.setBlockState(mutableBlockPos, Blocks.MAGMA_BLOCK.getDefaultState(), false);
					chunkIn.getBlockTickScheduler().schedule(mutableBlockPos, Blocks.MAGMA_BLOCK, 0);
				}
				else {
					chunkIn.setBlockState(mutableBlockPos, Blocks.OBSIDIAN.getDefaultState(), false);
				}

				return true;
			}
			else if (y < 10) {
				chunkIn.setBlockState(mutableBlockPos, lavaBlock, false);
				return false;
			}
			else {
				mutableBlockPos.set(xStart, y, zStart);
				chunkIn.setBlockState(mutableBlockPos, WATER.getBlockState(), false);
				return true;
			}
		}
	}
}