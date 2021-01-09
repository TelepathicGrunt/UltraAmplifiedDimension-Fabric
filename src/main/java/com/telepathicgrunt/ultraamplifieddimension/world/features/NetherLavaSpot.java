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
import java.util.Random;


public class NetherLavaSpot extends Feature<DefaultFeatureConfig> {

    public NetherLavaSpot(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, DefaultFeatureConfig config) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState blockstate = world.getBlockState(pos);
        boolean generateLava = false;
        int solidSurrounding = 0;

        for(Direction side : Direction.Type.HORIZONTAL){
            if (world.getBlockState(mutable.set(pos).move(Direction.DOWN).move(side)).isOpaque()) {
                ++solidSurrounding;
            }
        }

        //not enough solid blocks surrounding area to generate lava
        if (solidSurrounding < 3) {
            return false;
        }

        //full chance to generate in gravel
        if (blockstate == Blocks.GRAVEL.getDefaultState()) {
            mutable.set(pos).move(Direction.DOWN);
            if (world.getBlockState(mutable).isOpaque()) {
                //can only generate in gravel if below is also a solid block to prevent
                //lava spawning in 1 thick gravel which causes the gravel to fall,
                //leaving a pillar of lava floating in mid-air which looks bad.
                generateLava = true;
            }
        }
        //1/3rd chance to generate in soulsand
        else if (blockstate == Blocks.SOUL_SAND.getDefaultState()) {
            if (rand.nextFloat() < 0.33) {
                generateLava = true;
            }
        }

        //1/30th chance to generate in netherrack
        else if (blockstate == Blocks.NETHERRACK.getDefaultState()) {
            if (rand.nextFloat() < 0.033) {
                generateLava = true;
            }
        }

        //generates surface lava that can flow
        if (generateLava) {
            world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 2);
            world.getFluidTickScheduler().schedule(pos, Fluids.LAVA, 0);
        }
        return true;
    }
}