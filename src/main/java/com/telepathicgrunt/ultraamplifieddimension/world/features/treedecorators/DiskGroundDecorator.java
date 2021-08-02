package com.telepathicgrunt.ultraamplifieddimension.world.features.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADTreeDecoratorTypes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class DiskGroundDecorator extends TreeDecorator {

    public static final Codec<DiskGroundDecorator> CODEC = RecordCodecBuilder.create((diskGroundDecorator) -> diskGroundDecorator.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("provider").forGetter((config) -> config.blockStateProvider),
            Codec.intRange(0, 36).fieldOf("radius").forGetter((config) -> config.radius)
    ).apply(diskGroundDecorator, DiskGroundDecorator::new));

    private final BlockStateProvider blockStateProvider;
    private final int radius;

    public DiskGroundDecorator(BlockStateProvider blockStateProvider, int radius) {
        this.blockStateProvider = blockStateProvider;
        this.radius = radius;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return UADTreeDecoratorTypes.DISK_GROUND_DECORATOR;
    }

    @Override
    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions) {
        int minY = logPositions.get(0).getY();

        // run blob code only for bottom trunks
        logPositions.stream().filter((pos) -> pos.getY() == minY).forEach((pos) -> this.genBlob(world, replacer, random, pos));
    }

    private void genBlob(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos centerBlockPos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(int x = -this.radius; x <= this.radius; ++x) {
            for(int z = -this.radius; z <= this.radius; ++z) {
                if ((x * x) + (z * z) <= (this.radius * this.radius)) {
                    this.setBlobBlock(world, replacer, random, mutable.set(centerBlockPos).move(x, 0, z));
                }
            }
        }

    }

    private void setBlobBlock(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startBlockPos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(startBlockPos).move(Direction.UP, 2);
        for(int y = 2; y >= -3; --y) {
            if (Feature.isSoil(world, mutable)) {
                replacer.accept(mutable, this.blockStateProvider.getBlockState(random, startBlockPos));
                break;
            }

            if (!Feature.isAir(world, mutable) && y < 0) {
                break;
            }

            mutable.move(Direction.DOWN);
        }
    }
}
