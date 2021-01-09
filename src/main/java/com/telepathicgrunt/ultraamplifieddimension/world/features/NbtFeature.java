package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.NbtFeatureConfig;
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
import java.util.Random;


public class NbtFeature extends Feature<NbtFeatureConfig> {

    public NbtFeature(Codec<NbtFeatureConfig> configFactory) {
        super(configFactory);
    }

    private final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_VOID = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.STRUCTURE_VOID));
    private final StructurePlacementData placementsettings = (new StructurePlacementData()).setMirror(BlockMirror.NONE).addProcessor(IGNORE_STRUCTURE_VOID).setIgnoreEntities(false);


    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random rand, BlockPos position, NbtFeatureConfig config) {

        // Person wants an empty feature for some reason.
        if (config.nbtResourcelocationsAndWeights.size() == 0) {
            return false;
        }

        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable().set(position);

        // Makes sure it generates with land around it instead of cutting into cliffs or hanging over an edge by checking if block at north, east, west, and south are acceptable terrain blocks that appear only at top of land.
        int radius = config.solidLandRadius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x * z) > radius && Math.abs(x * z) < radius * 2) {
                    blockpos$Mutable.set(position).move(-x, -1, -z);
                    if (!world.getBlockState(blockpos$Mutable).isOpaque()) {
                        return false;
                    }
                    //world.setBlockState(blockpos$Mutable.up(), Blocks.REDSTONE_BLOCK.getDefaultState(), 2);
                }
            }
        }

        StructureManager templatemanager = world.toServerWorld().getServer().getStructureManager();
        Identifier nbtRL = GeneralUtils.getRandomEntry(config.nbtResourcelocationsAndWeights, rand);
        Structure template = templatemanager.getStructure(nbtRL);

        if (template == null) {
            UltraAmplifiedDimension.LOGGER.warn(config.nbtResourcelocationsAndWeights.toString() + " NTB does not exist!");
            return false;
        }

        BlockPos halfLengths = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
        placementsettings.setRotation(BlockRotation.random(rand)).setPosition(halfLengths).setIgnoreEntities(false);
        if(config.processor != null){
            config.processor.get().getList().forEach(placementsettings::addProcessor);
        }
        template.placeAndNotifyListeners(world, blockpos$Mutable.set(position).move(-halfLengths.getX(), 0, -halfLengths.getZ()), placementsettings, rand);

        return true;
    }
}
