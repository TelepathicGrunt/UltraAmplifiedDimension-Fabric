package com.telepathicgrunt.ultraamplifieddimension.blocks;

import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;
import java.util.Random;


public class BigCactusMainBlock extends FacingBlock
{
    public static final IntProperty AGE = Properties.AGE_15;
    public static final DirectionProperty VALIDFACING = DirectionProperty.of("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP);
    public static final DirectionProperty FACING = VALIDFACING;

    protected static final VoxelShape HITBOX_DIMENSIONS = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected static final VoxelShape OUTLINE_DIMENSION = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);


    public BigCactusMainBlock() {
        super(Settings.of(Material.CACTUS).ticksRandomly().strength(0.4F).sounds(BlockSoundGroup.WOOL));
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(FACING, Direction.UP));
    }


    @Override
	@SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4))
            return; // Forge: prevent growing cactus from loading unloaded chunks with block update
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
        else {
            BlockPos blockpos = pos.up();
            if (world.isAir(blockpos)) {
                int i = 1;

                while (world.getBlockState(pos.down(i)).getBlock() == this ||
						world.getBlockState(pos.down(i)).getBlock() == UADBlocks.BIG_CACTUS_BODY_BLOCK ||
						world.getBlockState(pos.down(i)).getBlock() == UADBlocks.BIG_CACTUS_CORNER_BLOCK)
                {
                    i++;
                }

                if (i < 3) {
                    int j = state.get(AGE);
                    if (j == 15) {
                        world.setBlockState(blockpos, this.getDefaultState());
                        BlockState blockstate = state.with(AGE, 0);
                        world.setBlockState(pos, blockstate, 4);
                        blockstate.neighborUpdate(world, blockpos, this, pos, false);
                    }
                    else {
                        world.setBlockState(pos, state.with(AGE, j + 1), 4);
                    }
                }
            }
        }
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction direction = context.getSide().getOpposite();
        return this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.UP : direction);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
        builder.add(FACING);
    }



	@Override
	@SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return HITBOX_DIMENSIONS;
    }



	@Override
	@SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_DIMENSION;
    }


    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state. For example,
     * fences make their connections to the passed in state if possible, and wet concrete powder immediately returns its
     * solidified counterpart. Note that this method should ideally consider only the specific face passed in.
     */

	@Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.canPlaceAt(world, currentPos)) {
            world.getBlockTickScheduler().schedule(currentPos, this, 1);
        }

        return super.getStateForNeighborUpdate(stateIn, facing, facingState, world, currentPos, facingPos);
    }


    @Override
	@SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {

        //up needs a cactus block below
        if (state.get(FACING) == Direction.UP) {

            //cannot have lava next to it
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (world.getFluidState(pos.offset(direction)).isIn(FluidTags.LAVA)) {
                    return false;
                }
            }

            return validBlockBelow(world, pos.down());
        }

        //sideways needs 1 body or corner cactus block next to it with valid block below that and no lava nearby too
        else {
            boolean hasMainCactusBody = false;

            for (Direction direction : Direction.Type.HORIZONTAL) {

                //skip all axis that isn't aligned with this block's axis
                if (state.get(FACING).getAxis() != direction.getAxis()) {
                    continue;
                }

                BlockState blockstate = world.getBlockState(pos.offset(direction));

                //NO LAVA ALLOWED
                if (world.getFluidState(pos.offset(direction)).isIn(FluidTags.LAVA)) {
                    return false;
                }

                //if modded cactus is next to this block, check to see if it has a valid block below.
                //this is so this block can be placed onto main cactus body by hand and will break if
                //main cactus body is breaking even though this block may have a upward branch on the other side.
                if (blockstate.getBlock() != Blocks.CACTUS && blockstate.getBlock() != this && blockstate.getMaterial() == Material.CACTUS) {
                    if (validBlockBelow(world, pos.offset(direction).down())) {
                        hasMainCactusBody = true;
                    }
                }
            }

            return hasMainCactusBody;
        }
    }


    /**
     * Will return true if it is sand or modded cactus at desired position
     *
     * @param world - current world to check in
     * @param pos   - position of where to check
     * @return - is sand or modded cactus at pos
     */
    private boolean validBlockBelow(WorldView world, BlockPos pos) {
        BlockState blockToCheck = world.getBlockState(pos);
		return BlockTags.SAND.contains(blockToCheck.getBlock()) || (blockToCheck.getBlock() != Blocks.CACTUS && blockToCheck.getMaterial() == Material.CACTUS);
	}


    @Override
	@SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entityIn) {
        entityIn.damage(DamageSource.CACTUS, 1.0F);
    }


    @Override
	@SuppressWarnings("deprecation")
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
