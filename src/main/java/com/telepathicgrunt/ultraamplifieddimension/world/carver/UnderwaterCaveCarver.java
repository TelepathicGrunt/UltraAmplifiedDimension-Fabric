package com.telepathicgrunt.ultraamplifieddimension.world.carver;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.BiomeContainerAccessor;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.configs.RavineConfig;
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
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;


public class UnderwaterCaveCarver extends CaveCarver
{
	private SimpleRegistry<Biome> biomeRegistry;

	public UnderwaterCaveCarver(Codec<CaveCarverConfig> codec)
	{
		super(codec);
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
	public boolean shouldCarve(CaveCarverConfig config, Random random) {
		return random.nextFloat() <= config.probability;
	}

	@Override
	protected boolean isRegionUncarvable(Chunk chunk, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return false;
	}

	@Override
	protected boolean carveAtPoint(CarverContext context, CaveCarverConfig config, Chunk chunk, Function<BlockPos, Biome> biomePos, BitSet carvingMask, Random rand, BlockPos.Mutable pos, BlockPos.Mutable downPos, AquiferSampler sampler, MutableBoolean isSurface) {
		IndexedIterable<Biome> reg = chunk.getBiomeArray() != null ? ((BiomeContainerAccessor)chunk.getBiomeArray()).uad_getBiomes() : null;
		if(reg instanceof SimpleRegistry && reg != biomeRegistry){
			biomeRegistry = (SimpleRegistry<Biome>)((BiomeContainerAccessor)chunk.getBiomeArray()).uad_getBiomes();
		}

		return func_222728_a(context, config, biomeRegistry, biomePos, chunk, carvingMask, rand, pos, downPos, this.alwaysCarvableBlocks);
	}

	protected static boolean func_222728_a(CarverContext context, CaveCarverConfig config, SimpleRegistry<Biome> biomeRegistry, Function<BlockPos, Biome> biomePos, Chunk chunk, BitSet carvingMask, Random random, BlockPos.Mutable pos, BlockPos.Mutable downPos, Set<Block> carvableBlocks) {
		return carvingBlock(context, config, biomeRegistry, biomePos, chunk, carvingMask, random, pos, downPos, carvableBlocks);
	}


	protected static boolean carvingBlock(CarverContext context, CaveCarverConfig config, SimpleRegistry<Biome> biomeRegistry, Function<BlockPos, Biome> biomeBlockPos, Chunk chunkIn, BitSet carvingMask, Random random, BlockPos.Mutable pos, BlockPos.Mutable downPos, Set<Block> carvableBlocks) {
		if (pos.getY() >= 60) {
			return false;
		}
		else {
			BlockState lavaBlock;

			if(pos.getY() < 11){
				Biome biome = biomeBlockPos.apply(pos);
				Identifier biomeID = biomeRegistry != null ? biomeRegistry.getId(biome) : null;
				String biomeIDString = biomeID == null ? "" : biomeID.toString();

				lavaBlock = GeneralUtils.carverLavaReplacement(biomeIDString, biome);
			}
			else{
				// Set defaults as this will not be used as cave is not low enough
				lavaBlock = Blocks.LAVA.getDefaultState();
			}

			BlockState blockstate = chunkIn.getBlockState(pos);
			if (!carvableBlocks.contains(blockstate.getBlock())) {
				return false;
			}
			else if (pos.getY() == 10) {
				float f = random.nextFloat();
				if (f < 0.25D && lavaBlock != Blocks.OBSIDIAN.getDefaultState()) {
					chunkIn.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), false);
					chunkIn.getBlockTickScheduler().schedule(pos, Blocks.MAGMA_BLOCK, 0);
				}
				else {
					chunkIn.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), false);
				}

				return true;
			}
			else if (pos.getY() < 10) {
				chunkIn.setBlockState(pos, lavaBlock, false);
				return false;
			}
			else {
				chunkIn.setBlockState(pos, WATER.getBlockState(), false);
				return true;
			}
		}
	}
}