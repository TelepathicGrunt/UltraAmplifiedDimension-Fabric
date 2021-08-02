package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.BlockWithRuleReplaceConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class OnSolidBlockPlacer extends Feature<BlockWithRuleReplaceConfig> {

    public OnSolidBlockPlacer(Codec<BlockWithRuleReplaceConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<BlockWithRuleReplaceConfig> context) {
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(context.getOrigin());
        if (context.getConfig().target.test( context.getWorld().getBlockState(mutable), context.getRandom()) && context.getWorld().getBlockState(mutable.move(Direction.DOWN)).isSideSolidFullSquare( context.getWorld(), mutable, Direction.UP)) {
            context.getWorld().setBlockState(mutable.move(Direction.UP), context.getConfig().state, 2);
            return true;
        }

        return false;
    }
}