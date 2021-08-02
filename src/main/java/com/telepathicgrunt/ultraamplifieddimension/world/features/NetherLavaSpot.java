package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class NetherLavaSpot extends Feature<DefaultFeatureConfig> {

    public NetherLavaSpot(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState blockstate = context.getWorld().getBlockState(context.getOrigin());
        boolean generateLava = false;
        int solidSurrounding = 0;

        for(Direction side : Direction.Type.HORIZONTAL){
            if (context.getWorld().getBlockState(mutable.set(context.getOrigin()).move(Direction.DOWN).move(side)).isOpaque()) {
                ++solidSurrounding;
            }
        }

        //not enough solid blocks surrounding area to generate lava
        if (solidSurrounding < 3) {
            return false;
        }

        //full chance to generate in gravel
        if (blockstate == Blocks.GRAVEL.getDefaultState()) {
            mutable.set(context.getOrigin()).move(Direction.DOWN);
            if (context.getWorld().getBlockState(mutable).isOpaque()) {
                //can only generate in gravel if below is also a solid block to prevent
                //lava spawning in 1 thick gravel which causes the gravel to fall,
                //leaving a pillar of lava floating in mid-air which looks bad.
                generateLava = true;
            }
        }
        //1/3rd chance to generate in soulsand
        else if (blockstate == Blocks.SOUL_SAND.getDefaultState()) {
            if (context.getRandom().nextFloat() < 0.33) {
                generateLava = true;
            }
        }

        //1/30th chance to generate in netherrack
        else if (blockstate == Blocks.NETHERRACK.getDefaultState()) {
            if (context.getRandom().nextFloat() < 0.033) {
                generateLava = true;
            }
        }

        //generates surface lava that can flow
        if (generateLava) {
            context.getWorld().setBlockState(context.getOrigin(), Blocks.LAVA.getDefaultState(), 2);
            context.getWorld().getFluidTickScheduler().schedule(context.getOrigin(), Fluids.LAVA, 0);
        }
        return true;
    }
}