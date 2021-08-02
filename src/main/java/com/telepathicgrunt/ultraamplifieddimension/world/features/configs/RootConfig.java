package com.telepathicgrunt.ultraamplifieddimension.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RootConfig implements FeatureConfig {
    public static final Codec<RootConfig> CODEC = RecordCodecBuilder.create((configInstance) -> configInstance.group(
            RuleTest.TYPE_CODEC.fieldOf("root_replace_target").forGetter((config) -> config.rootReplaceTarget),
            RuleTest.TYPE_CODEC.fieldOf("valid_above_state").forGetter((config) -> config.validAboveState),
            BlockState.CODEC.fieldOf("root_block").forGetter((config) -> config.rootBlock)
        ).apply(configInstance, RootConfig::new));

    public final RuleTest rootReplaceTarget;
    public final RuleTest validAboveState;
    public final BlockState rootBlock;

    public RootConfig(RuleTest rootReplaceTarget, RuleTest validAboveState, BlockState rootBlock) {
        this.rootReplaceTarget = rootReplaceTarget;
        this.validAboveState = validAboveState;
        this.rootBlock = rootBlock;
    }
}
