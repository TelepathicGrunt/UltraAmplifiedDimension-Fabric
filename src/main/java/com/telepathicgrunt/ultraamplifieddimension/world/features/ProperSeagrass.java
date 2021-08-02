package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.ProbabilityAndCountConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class ProperSeagrass extends Feature<ProbabilityAndCountConfig> {
    public ProperSeagrass(Codec<ProbabilityAndCountConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<ProbabilityAndCountConfig> context) {
        boolean flag = false;

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(int i = 0; i < context.getConfig().count; i++){

            int x = context.getRandom().nextInt(8) - context.getRandom().nextInt(8);
            int z = context.getRandom().nextInt(8) - context.getRandom().nextInt(8);
            int y = context.getRandom().nextInt(8) - context.getRandom().nextInt(8);
            mutable.set(context.getOrigin()).move(x, y, z);

            if (context.getWorld().getBlockState(mutable).isOf(Blocks.WATER)) {

                boolean spawnTallGrass = context.getRandom().nextFloat() < context.getConfig().probability;
                BlockState blockstate = spawnTallGrass ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();

                if (blockstate.canPlaceAt(context.getWorld(), mutable)) {
                    if (spawnTallGrass) {
                        BlockState blockstate1 = blockstate.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
                        if (context.getWorld().getBlockState(mutable.move(Direction.UP)).isOf(Blocks.WATER)) {
                            context.getWorld().setBlockState(mutable.move(Direction.DOWN), blockstate, 2);
                            context.getWorld().setBlockState(mutable.move(Direction.UP), blockstate1, 2);
                        }
                    } else {
                        context.getWorld().setBlockState(mutable, blockstate, 2);
                    }

                    flag = true;
                }
            }
        }

        return flag;
    }
}
