package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import net.minecraft.block.Block;
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
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class HangingRuins extends Feature<DefaultFeatureConfig>
{
	private static final Identifier HANGING_RUINS_RL = new Identifier(UltraAmplifiedDimension.MODID + ":hanging_ruins");

	public HangingRuins(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		//makes sure this ruins does not spawn too close to world height border.
		if (context.getOrigin().getY() < context.getGenerator().getSeaLevel() + 5) {
			return false;
		}

		BlockPos.Mutable mutableMain = new BlockPos.Mutable().set(context.getOrigin());
		BlockPos.Mutable mutableTemp = new BlockPos.Mutable();

		//move through roots to the actual bottom of ledges
		BlockState currentBlock = context.getWorld().getBlockState(mutableMain);
		while((BlockTags.LOGS.contains(currentBlock.getBlock()) || !currentBlock.isOpaque()) && 
			mutableMain.getY() < context.getGenerator().getWorldHeight())
		{
		    mutableMain.move(Direction.UP);
		    currentBlock = context.getWorld().getBlockState(mutableMain);
		}
		
		//makes sure there is enough solid blocks on ledge to hold this feature.
		for (int x = -5; x <= 5; x++) {
			for (int z = -5; z <= 5; z++) {
				if (Math.abs(x * z) > 9 && Math.abs(x * z) < 20) {
					mutableTemp.set(mutableMain).move(x, 1, z);
					if (!context.getWorld().getBlockState(mutableTemp).isOpaque()) {
						return false;
					}
				}
			}
		}

		//makes sure top won't be exposed to air
		if (shouldMoveDownOne(context.getWorld(), mutableMain)) {
			mutableMain.move(Direction.DOWN);
		}

		//UltraAmplified.LOGGER.debug("Hanging Ruins | " + position.getX() + " " + position.getY() + " "+position.getZ());
		StructureManager templatemanager = context.getWorld().toServerWorld().getServer().getStructureManager();
		Structure template = templatemanager.getStructureOrBlank(HANGING_RUINS_RL);

		if (template == null) {
			UltraAmplifiedDimension.LOGGER.warn("hanging ruins NTB does not exist!");
			return false;
		}

		// enough space for ruins.
		if(mutableMain.getY() == context.getGenerator().getWorldHeight() ||
				!context.getWorld().getBlockState(mutableMain.down(template.getSize().getY())).isAir() ||
				!context.getWorld().getBlockState(mutableMain.down(template.getSize().getY() + 5)).isAir())
		{
			return false;
		}

		BlockPos halfLengths = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
		StructurePlacementData placementsettings = new StructurePlacementData().setPosition(halfLengths).setRotation(BlockRotation.random(context.getRandom())).setIgnoreEntities(false);
		mutableMain.move(-halfLengths.getX(), -8, -halfLengths.getZ());
		template.place(context.getWorld(), mutableMain, mutableMain, placementsettings, context.getRandom(), Block.NO_REDRAW);
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
