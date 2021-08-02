package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.SeaPickleConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class ProperSeapickle extends Feature<SeaPickleConfig> {
    public ProperSeapickle(Codec<SeaPickleConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<SeaPickleConfig> context) {
        int picklesPlaced = 0;

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(int i = 0; i < context.getConfig().count; ++i) {
            int x = context.getRandom().nextInt(8) - context.getRandom().nextInt(8);
            int z = context.getRandom().nextInt(8) - context.getRandom().nextInt(8);
            int y = context.getRandom().nextInt(8) - context.getRandom().nextInt(8);
            mutable.set(context.getOrigin()).move(x, y, z);
            BlockState blockstate = Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, context.getRandom().nextInt(context.getConfig().maxPickles - (context.getConfig().minPickles - 1)) + context.getConfig().minPickles);
            if ( context.getWorld().getBlockState(mutable).isOf(Blocks.WATER) && blockstate.canPlaceAt( context.getWorld(), mutable)) {
                context.getWorld().setBlockState(mutable, blockstate, 2);
                ++picklesPlaced;
            }
        }

        return picklesPlaced > 0;
    }
}
