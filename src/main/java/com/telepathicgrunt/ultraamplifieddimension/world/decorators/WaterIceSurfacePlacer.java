package com.telepathicgrunt.ultraamplifieddimension.world.decorators;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


public class WaterIceSurfacePlacer extends Decorator<WaterIceSurfaceConfig> {

    public WaterIceSurfacePlacer(Codec<WaterIceSurfaceConfig> codec) {
        super(codec);
    }


    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random rand, WaterIceSurfaceConfig config, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> list = new ArrayList<>();

        // Count specifies number of columns passes we will do
        for(int count = 0; count < config.columnCount; ++count) {

            // Randomizes first pos and set it to heightmap
            int x = rand.nextInt(16) + pos.getX();
            int z = rand.nextInt(16) + pos.getZ();
            int heightMapY = context.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
            boolean skippedTopLedge;
            mutable.set(x, heightMapY, z);

            // Set the block above for heightmap pos
            BlockState prevBlockState = context.getBlockState(mutable.up());
            int bottomYLimit = ((ServerWorld)context.getWorld()).getChunkManager().getChunkGenerator().getSeaLevel();

            // Move downward towards sealevel and get every surface along the way
            while (mutable.getY() >= bottomYLimit - 20) {

                BlockState currentBlockState = context.getBlockState(mutable);

                // This is true if above is spacious while current block is solid.
                // We are at ledge if this is the case.
                // Also allows underside placements as well
                if (isLiquidOrIce(currentBlockState, config) && prevBlockState.isAir())
                {
                    // If we are skipping top ledge, then current y must not be terrain heightmap.
                    // The -1 is because we check top down and find surfaces if our block is a solid
                    // block with space above but the heightmap method always returns that above space.
                    // thus we need to subtract one to be able to tell if our pos is topmost terrain.
                    // Underside placing skips the top ledge checks
                    skippedTopLedge = (config.skipTopLedge && mutable.getY() == context.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, mutable.getX(), mutable.getZ()) - 1);

                    if(!skippedTopLedge)
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
                }

                // Set prevblock to this block and move down one.
                prevBlockState = currentBlockState;
                mutable.move(Direction.DOWN);
            }
        }

        return list.stream();
    }

    private static boolean isLiquidOrIce(BlockState state, WaterIceSurfaceConfig config) {
        return state.getFluidState().isIn(FluidTags.WATER) || (config.includeIcePlacement && state.isIn(BlockTags.ICE));
    }
}
