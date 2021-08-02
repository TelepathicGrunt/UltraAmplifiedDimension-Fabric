package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class SwampCross extends Feature<DefaultFeatureConfig>
{
	public SwampCross(Codec<DefaultFeatureConfig> configFactory)
	{
		super(configFactory);
	}


	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context)
	{
		BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable().set(context.getOrigin()).move(Direction.DOWN, 2);

		//creates vertical log blocks
		for (int i = 0; i < 8; i++) {
			this.setBlockState( context.getWorld(), blockpos$Mutable.move(Direction.UP), Blocks.SPRUCE_LOG.getDefaultState());
		}

		blockpos$Mutable.move(Direction.DOWN);
		//adds horizontal log blocks towards top
		for (int i = -2; i < 3; i++) {
			context.getWorld().setBlockState(blockpos$Mutable.east(i), Blocks.SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.X), 16 | 2);
		}

		//adds skull underground if block and above block is not a fluid or air
		blockpos$Mutable.set(context.getOrigin()).move(Direction.DOWN, 2).move(Direction.NORTH, 1);
		if (context.getWorld().getBlockState(blockpos$Mutable).isOpaque() && context.getWorld().getBlockState(blockpos$Mutable.up()).isOpaque()) {
			if (context.getRandom().nextFloat() < 0.1F) {
				context.getWorld().setBlockState(blockpos$Mutable, Blocks.WITHER_SKELETON_WALL_SKULL.getDefaultState(), 2);
			}
			else {
				context.getWorld().setBlockState(blockpos$Mutable, Blocks.SKELETON_WALL_SKULL.getDefaultState(), 2);
			}
		}

		//adds hidden chest underground if block is not water, lava, air, and if next boolean is true
		blockpos$Mutable.set(context.getOrigin()).move(Direction.DOWN, 3);
		if (context.getWorld().getBlockState(blockpos$Mutable).isOpaque() && context.getRandom().nextBoolean()) {
			context.getWorld().setBlockState(blockpos$Mutable, Blocks.AIR.getDefaultState(), 2); // Reset any TE here somehow.
			context.getWorld().setBlockState(blockpos$Mutable, Blocks.CHEST.getDefaultState(), 2);

			BlockEntity tileentity = context.getWorld().getBlockEntity(blockpos$Mutable);
			if (tileentity instanceof ChestBlockEntity) {
				LootableContainerBlockEntity.setLootTable(context.getWorld(), context.getRandom(), blockpos$Mutable, LootTables.SPAWN_BONUS_CHEST);
			}
		}

		return true;
	}
}