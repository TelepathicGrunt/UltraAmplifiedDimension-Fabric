package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import java.util.Random;


public class AmplifiedPortalFrame extends Feature<DefaultFeatureConfig>
{

	public AmplifiedPortalFrame() {
		super(DefaultFeatureConfig.CODEC);
	}

	//need to be made due to extending feature
	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, DefaultFeatureConfig config) {
		return false;
	}

	//is called in AmplifiedPortalBehavior which doesn't have a chunk generator passed in
	public boolean generate(StructureWorldAccess world, BlockPos pos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable().set(pos);

		//7x7 flooring around bottom of frame
		for (int x = -3; x <= 3; x++) {
			for (int z = -3; z <= 3; z++) {
				world.setBlockState(
						mutable.set(pos).move(x, -1, z),
						Blocks.POLISHED_GRANITE.getDefaultState(),
						3);
			}
		}

		//bottom of portal frame
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (Math.abs(x * z) == 1) {
					world.setBlockState(
							mutable.set(pos).move(x, 0, z),
							Blocks.POLISHED_GRANITE.getDefaultState(),
							3);
				}
				else {
					//sets slab but also waterlogs it if block it replaces is water based
					world.setBlockState(
							mutable.set(pos).move(x, 0, z),
							Blocks.POLISHED_ANDESITE_SLAB.getDefaultState()
									.with(SlabBlock.TYPE, SlabType.BOTTOM)
									.with(SlabBlock.WATERLOGGED, world.getBlockState(pos.add(x, 0, z)).getMaterial() == Material.WATER),
							3);
				}
			}
		}

		//the portal itself
		world.setBlockState(
				pos.add(0, 1, 0),
				UADBlocks.AMPLIFIED_PORTAL.get().getDefaultState(),
				3);

		//top of portal frame
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (Math.abs(x * z) == 1) {
					world.setBlockState(
							pos.add(x, 2, z),
							Blocks.POLISHED_GRANITE.getDefaultState(),
							3);
				}
				else {
					//sets slab but also waterlogs it if block it replaces is water based
					world.setBlockState(
							pos.add(x, 2, z),
							Blocks.POLISHED_ANDESITE_SLAB.getDefaultState()
									.with(SlabBlock.TYPE, SlabType.TOP)
									.with(SlabBlock.WATERLOGGED, world.getBlockState(pos.add(x, 2, z)).getMaterial() == Material.WATER),
							3);
				}
			}
		}

		return true;
	}
}
