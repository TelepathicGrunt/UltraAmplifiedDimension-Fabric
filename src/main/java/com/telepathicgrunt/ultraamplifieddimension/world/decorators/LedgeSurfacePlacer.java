package com.telepathicgrunt.ultraamplifieddimension.world.decorators;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


public class LedgeSurfacePlacer extends Decorator<LedgeSurfacePlacerConfig> {

    public LedgeSurfacePlacer(Codec<LedgeSurfacePlacerConfig> codec) {
        super(codec);
    }


    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random rand, LedgeSurfacePlacerConfig config, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> list = new ArrayList<>();

        // Count specifies number of columns passes we will do
        for(int count = 0; count < config.columnCount; ++count) {

            // Randomizes first pos and set it to heightmap
            int x = rand.nextInt(16) + pos.getX();
            int z = rand.nextInt(16) + pos.getZ();
            int heightMapY = context.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
            boolean skippedTopLedge;
            boolean isValidWaterPos;
            mutable.set(x, heightMapY, z);

            // Set the block above for heightmap pos
            BlockState prevBlockState = context.getBlockState(mutable.up());

            // If doing water pos, continue down until lava level instead of stopping at sealevel
            int bottomYLimit = config.waterPosOnly ? 11 : context.getWorld().toServerWorld().getChunkManager().getChunkGenerator().getSeaLevel();

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
                    ((!config.undersideOnly && !notSolidSpace(currentBlockState) && notSolidSpace(prevBlockState)) ||
                    (config.undersideOnly && notSolidSpace(currentBlockState) && !notSolidSpace(prevBlockState))))
                {
                    // If we are skipping top ledge, then current y must not be terrain heightmap.
                    // The -1 is because we check top down and find surfaces if our block is a solid
                    // block with space above but the heightmap method always returns that above space.
                    // thus we need to subtract one to be able to tell if our pos is topmost terrain.
                    // Underside placing skips the top ledge checks
                    skippedTopLedge = !config.undersideOnly && (config.skipTopLedge && mutable.getY() == context.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, mutable.getX(), mutable.getZ()) - 1);
                    isValidWaterPos = (config.undersideOnly ? currentBlockState.getFluidState().isIn(FluidTags.WATER) : prevBlockState.getFluidState().isIn(FluidTags.WATER));

                    if(((config.waterPosOnly && isValidWaterPos) ||
                        (!config.waterPosOnly && !isValidWaterPos)) &&
                        !skippedTopLedge)
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

    private static boolean notSolidSpace(BlockState state) {
        return state.isAir() || !(state.getFluidState().isEmpty() || state.isOpaque());
    }
}
