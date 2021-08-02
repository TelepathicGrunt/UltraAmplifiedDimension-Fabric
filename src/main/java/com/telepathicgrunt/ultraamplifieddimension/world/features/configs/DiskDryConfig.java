package com.telepathicgrunt.ultraamplifieddimension.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;

public class DiskDryConfig implements FeatureConfig {

    public static final Codec<DiskDryConfig> CODEC = RecordCodecBuilder.create((diskDryConfig) -> diskDryConfig.group(
            BlockState.CODEC.fieldOf("state").forGetter((config) -> config.state),
            IntProvider.createValidatingCodec(0, 36).fieldOf("radius").forGetter((config) -> config.radius),
            Codec.intRange(0, 36).fieldOf("half_height").forGetter((config) -> config.half_height),
            BlockState.CODEC.listOf().fieldOf("targets").forGetter((config) -> config.targets),
            Codec.BOOL.fieldOf("exposed_only").orElse(false).forGetter((config) -> config.exposedOnly)
    ).apply(diskDryConfig, DiskDryConfig::new));

    public final BlockState state;
    public final IntProvider radius;
    public final int half_height;
    public final List<BlockState> targets;
    public final boolean exposedOnly;

    public DiskDryConfig(BlockState blockState, IntProvider radius, int half_height, List<BlockState> targets, boolean exposedOnly) {
        this.state = blockState;
        this.radius = radius;
        this.half_height = half_height;
        this.targets = targets;
        this.exposedOnly = exposedOnly;
    }
}
