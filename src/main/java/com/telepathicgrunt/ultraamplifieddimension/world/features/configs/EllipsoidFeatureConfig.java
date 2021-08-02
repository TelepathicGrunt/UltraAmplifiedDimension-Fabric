package com.telepathicgrunt.ultraamplifieddimension.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EllipsoidFeatureConfig implements FeatureConfig {

    public static final Codec<EllipsoidFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            RuleTest.TYPE_CODEC.fieldOf("target").forGetter((oreFeatureConfig) -> oreFeatureConfig.target),
            BlockState.CODEC.fieldOf("state").forGetter((oreFeatureConfig) -> oreFeatureConfig.state),
            Codec.intRange(0, 100).fieldOf("size").forGetter((oreFeatureConfig) -> oreFeatureConfig.size)
    ).apply(instance, EllipsoidFeatureConfig::new));

    public final RuleTest target;
    public final BlockState state;
    public final int size;

    public EllipsoidFeatureConfig(RuleTest target, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }
}
