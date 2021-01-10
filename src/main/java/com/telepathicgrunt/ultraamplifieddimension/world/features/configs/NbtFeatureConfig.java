package com.telepathicgrunt.ultraamplifieddimension.world.features.configs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class NbtFeatureConfig implements FeatureConfig {
    public static final Codec<NbtFeatureConfig> CODEC = RecordCodecBuilder.create((configInstance) -> configInstance.group(
            Codec.intRange(0, 16).fieldOf("solid_land_radius").orElse(3).forGetter(nbtFeatureConfig -> nbtFeatureConfig.solidLandRadius),
            Codec.mapPair(Identifier.CODEC.fieldOf("resourcelocation"), Codec.intRange(1, Integer.MAX_VALUE).fieldOf("weight")).codec().listOf().fieldOf("nbt_entries").forGetter(nbtFeatureConfig -> nbtFeatureConfig.nbtResourcelocationsAndWeights),
            StructureProcessorType.REGISTRY_CODEC.optionalFieldOf("processors").orElse(null).forGetter(nbtFeatureConfig -> Optional.ofNullable(nbtFeatureConfig.processor))
            ).apply(configInstance, NbtFeatureConfig::new));

    public final int solidLandRadius;
    public final List<Pair<Identifier, Integer>> nbtResourcelocationsAndWeights;
    public final Supplier<StructureProcessorList> processor;

    public NbtFeatureConfig(int solidLandRadius, List<Pair<Identifier, Integer>> nbtResourcelocationsAndWeights, Optional<Supplier<StructureProcessorList>> processor) {
        this.solidLandRadius = solidLandRadius;
        this.nbtResourcelocationsAndWeights = nbtResourcelocationsAndWeights;
        this.processor = processor.orElse(null);
    }
}
