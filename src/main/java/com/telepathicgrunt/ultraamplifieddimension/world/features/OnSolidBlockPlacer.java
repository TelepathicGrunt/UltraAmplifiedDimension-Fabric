package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.BlockWithRuleReplaceConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;


public class OnSolidBlockPlacer extends Feature<BlockWithRuleReplaceConfig> {

    public OnSolidBlockPlacer(Codec<BlockWithRuleReplaceConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random rand, BlockPos position, BlockWithRuleReplaceConfig replaceBlockConfig) {
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(position);
        if (replaceBlockConfig.target.test(world.getBlockState(mutable), rand) && world.getBlockState(mutable.move(Direction.DOWN)).isSideSolidFullSquare(world, mutable, Direction.UP)) {
            world.setBlockState(mutable.move(Direction.UP), replaceBlockConfig.state, 2);
            return true;
        }

        return false;
    }
}