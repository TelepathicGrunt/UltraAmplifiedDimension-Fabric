package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.google.common.collect.ImmutableList;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class AmplifiedPortalFrame extends Feature<DefaultFeatureConfig>
{
	private static final Identifier PORTAL_RL = new Identifier(UltraAmplifiedDimension.MODID, "auto_generated_portal");
	private final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_VOID = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.STRUCTURE_VOID));
	private final StructurePlacementData placementsettings = (new StructurePlacementData()).setMirror(BlockMirror.NONE).addProcessor(IGNORE_STRUCTURE_VOID).setIgnoreEntities(false);

	public AmplifiedPortalFrame() {
		super(DefaultFeatureConfig.CODEC);
	}

	//need to be made due to extending feature
	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		return generate(context.getWorld(), context.getOrigin());
	}

	//is called in AmplifiedPortalBehavior which doesn't have a chunk generator passed in
	public boolean generate(StructureWorldAccess world, BlockPos pos) {

		StructureManager templatemanager = world.getServer().getStructureManager();
		Structure template = templatemanager.getStructureOrBlank(PORTAL_RL);

		if (template == null) {
			UltraAmplifiedDimension.LOGGER.warn(PORTAL_RL + " NTB does not exist!");
			return false;
		}

		BlockPos halfLengths = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
		placementsettings.setRotation(BlockRotation.random(world.getRandom())).setPosition(halfLengths).setIgnoreEntities(false);
		BlockPos.Mutable mutable = new BlockPos.Mutable().set(pos).move(-halfLengths.getX(), 0, -halfLengths.getZ());
		template.place(world, mutable, mutable, placementsettings, world.getRandom(), Block.NO_REDRAW);

		return true;
	}
}
