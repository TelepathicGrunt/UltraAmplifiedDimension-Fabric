package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import net.minecraft.block.BlockState;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import java.util.Random;


public class HangingRuins extends Feature<DefaultFeatureConfig>
{
	private static final Identifier HANGING_RUINS_RL = new Identifier(UltraAmplifiedDimension.MODID + ":hanging_ruins");

	public HangingRuins(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random rand, BlockPos position, DefaultFeatureConfig config) {
		//makes sure this ruins does not spawn too close to world height border.
		if (position.getY() < chunkGenerator.getSeaLevel() + 5) {
			return false;
		}

		BlockPos.Mutable mutableMain = new BlockPos.Mutable().set(position);
		BlockPos.Mutable mutableTemp = new BlockPos.Mutable();

		//move through roots to the actual bottom of ledges
		BlockState currentBlock = world.getBlockState(mutableMain);
		while((BlockTags.LOGS.contains(currentBlock.getBlock()) || !currentBlock.isOpaque()) && 
			mutableMain.getY() < chunkGenerator.getWorldHeight())
		{
		    mutableMain.move(Direction.UP);
		    currentBlock = world.getBlockState(mutableMain);
		}
		
		//makes sure there is enough solid blocks on ledge to hold this feature.
		for (int x = -5; x <= 5; x++) {
			for (int z = -5; z <= 5; z++) {
				if (Math.abs(x * z) > 9 && Math.abs(x * z) < 20) {
					mutableTemp.set(mutableMain).move(x, 1, z);
					if (!world.getBlockState(mutableTemp).isOpaque()) {
						return false;
					}
				}
			}
		}

		//makes sure top won't be exposed to air
		if (shouldMoveDownOne(world, mutableMain)) {
			mutableMain.move(Direction.DOWN);
		}

		//UltraAmplified.LOGGER.debug("Hanging Ruins | " + position.getX() + " " + position.getY() + " "+position.getZ());
		StructureManager templatemanager = world.toServerWorld().getServer().getStructureManager();
		Structure template = templatemanager.getStructure(HANGING_RUINS_RL);

		if (template == null) {
			UltraAmplifiedDimension.LOGGER.warn("hanging ruins NTB does not exist!");
			return false;
		}

		// enough space for ruins.
		if(mutableMain.getY() == chunkGenerator.getWorldHeight() ||
				!world.getBlockState(mutableMain.down(template.getSize().getY())).isAir() ||
				!world.getBlockState(mutableMain.down(template.getSize().getY() + 5)).isAir())
		{
			return false;
		}

		BlockPos halfLengths = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
		StructurePlacementData placementsettings = new StructurePlacementData().setPosition(halfLengths).setRotation(BlockRotation.random(rand)).setIgnoreEntities(false);
		template.placeAndNotifyListeners(world, mutableMain.move(-halfLengths.getX(), -8, -halfLengths.getZ()), placementsettings, rand);
		return true;
	}


	private boolean shouldMoveDownOne(WorldAccess world, BlockPos.Mutable mutableMain) {
		BlockPos.Mutable mutableTemp = new BlockPos.Mutable();
		//if we are on a 1 block thick ledge at any point, move down one so ruins ceiling isn't exposed 
		for (int x = -5; x <= 5; x++) {
			for (int z = -5; z <= 5; z++) {
				mutableTemp.set(mutableMain).move(x, 2, z);
				if (Math.abs(x * z) < 20 && !world.getBlockState(mutableTemp).isOpaque()) {
					//world.setBlockState(mutableTemp, Blocks.REDSTONE_BLOCK.getDefaultState(), 2);
					return true;
				}
			}
		}
		return false;
	}
}
