package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.NbtFeatureConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class NbtFeature extends Feature<NbtFeatureConfig> {

    public NbtFeature(Codec<NbtFeatureConfig> configFactory) {
        super(configFactory);
    }

    private final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_VOID = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.STRUCTURE_VOID));
    private final StructurePlacementData placementsettings = (new StructurePlacementData()).setMirror(BlockMirror.NONE).addProcessor(IGNORE_STRUCTURE_VOID).setIgnoreEntities(false);


    @Override
    public boolean generate(FeatureContext<NbtFeatureConfig> context) {

        // Person wants an empty feature for some reason.
        if (context.getConfig().nbtResourcelocationsAndWeights.size() == 0) {
            return false;
        }

        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable().set(context.getOrigin());

        // Makes sure it generates with land around it instead of cutting into cliffs or hanging over an edge by checking if block at north, east, west, and south are acceptable terrain blocks that appear only at top of land.
        int radius = context.getConfig().solidLandRadius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x * z) > radius && Math.abs(x * z) < radius * 2) {
                    blockpos$Mutable.set(context.getOrigin()).move(-x, -1, -z);
                    if (! context.getWorld().getBlockState(blockpos$Mutable).isOpaque()) {
                        return false;
                    }
                    //context.getWorld().setBlockState(blockpos$Mutable.up(), Blocks.REDSTONE_BLOCK.getDefaultState(), 2);
                }
            }
        }

        StructureManager templatemanager = context.getWorld().toServerWorld().getServer().getStructureManager();
        Identifier nbtRL = GeneralUtils.getRandomEntry(context.getConfig().nbtResourcelocationsAndWeights, context.getRandom());
        Structure template = templatemanager.getStructureOrBlank(nbtRL);

        if (template == null) {
            UltraAmplifiedDimension.LOGGER.warn(context.getConfig().nbtResourcelocationsAndWeights.toString() + " NTB does not exist!");
            return false;
        }

        BlockPos halfLengths = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
        placementsettings.setRotation(BlockRotation.random(context.getRandom())).setPosition(halfLengths).setIgnoreEntities(false);
        if(context.getConfig().processor != null){
            context.getConfig().processor.get().getList().forEach(placementsettings::addProcessor);
        }
        blockpos$Mutable.set(context.getOrigin()).move(-halfLengths.getX(), 0, -halfLengths.getZ());
        template.place(context.getWorld(), blockpos$Mutable, blockpos$Mutable, placementsettings, context.getRandom(), Block.NO_REDRAW);

        return true;
    }
}
