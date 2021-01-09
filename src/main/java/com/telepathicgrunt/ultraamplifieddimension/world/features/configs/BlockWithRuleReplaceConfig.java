package com.telepathicgrunt.ultraamplifieddimension.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BlockWithRuleReplaceConfig implements FeatureConfig {
    public static final Codec<BlockWithRuleReplaceConfig> CODEC = RecordCodecBuilder.create((columnConfigInstance) -> columnConfigInstance.group(
            RuleTest.field_25012.fieldOf("target").forGetter((config) -> config.target),
            BlockState.CODEC.fieldOf("state").forGetter((config) -> config.state)
        ).apply(columnConfigInstance, BlockWithRuleReplaceConfig::new));

    public final RuleTest target;
    public final BlockState state;

    public BlockWithRuleReplaceConfig(RuleTest target, BlockState state) {
        this.state = state;
        this.target = target;
    }
}
