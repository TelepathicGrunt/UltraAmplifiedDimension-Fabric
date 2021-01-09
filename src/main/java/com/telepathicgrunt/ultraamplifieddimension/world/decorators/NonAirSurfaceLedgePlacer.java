package com.telepathicgrunt.ultraamplifieddimension.world.decorators;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


public class NonAirSurfaceLedgePlacer extends Decorator<NonAirSurfaceLedgePlacerConfig> {

    public NonAirSurfaceLedgePlacer(Codec<NonAirSurfaceLedgePlacerConfig> codec) {
        super(codec);
    }

    @Nonnull
    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random rand, NonAirSurfaceLedgePlacerConfig config, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> list = new ArrayList<>();

        // Count specifies number of columns passes we will do
        for(int count = 0; count < config.columnCount; ++count) {

            // Randomizes first pos and set it to heightmap
            int x = rand.nextInt(16) + pos.getX();
            int z = rand.nextInt(16) + pos.getZ();
            int heightMapY = context.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
            mutable.set(x, heightMapY, z);

            // Set the block above for heightmap pos
            BlockState prevBlockState = context.getBlockState(mutable.up());
            int bottomYLimit = context.getSeaLevel();

            // Move downward towards sealevel and get every surface along the way
            while (mutable.getY() >= bottomYLimit) {

                BlockState currentBlockState = context.getBlockState(mutable);

                // This is true if above is spacious while current block is solid.
                // We are at ledge if this is the case.
                // Also allows underside placements as well
                if (!currentBlockState.isIn(BlockTags.LEAVES) &&
                    !currentBlockState.isIn(BlockTags.LOGS) &&
                    !currentBlockState.isOf(Blocks.BEDROCK) &&
                    currentBlockState.getMaterial() != Material.CACTUS &&
                    !currentBlockState.isAir() &&
                    prevBlockState.isAir())
                {
                    if(rand.nextFloat() < config.validSpotChance){
                        list.add(mutable.toImmutable());
                    }

                    // pick a new x/z pos
                    mutable.set(
                            rand.nextInt(16) + pos.getX(),
                            mutable.getY(),
                            rand.nextInt(16) + pos.getZ());
                }

                // Set prevblock to this block and move down one.
                prevBlockState = currentBlockState;
                mutable.move(Direction.DOWN);
            }
        }

        return list.stream();
    }
}
