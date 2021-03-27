package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.mixin.features.TemplateInvoker;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.NbtDungeonConfig;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Clearable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.Level;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NbtDungeon extends Feature<NbtDungeonConfig>{

    public NbtDungeon(Codec<NbtDungeonConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos position, NbtDungeonConfig config) {

        Identifier nbtRL = GeneralUtils.getRandomEntry(config.nbtResourcelocationsAndWeights, random);

        StructureManager structureManager = world.toServerWorld().getStructureManager();
        Structure template = structureManager.getStructure(nbtRL);
        if(template == null){
            UltraAmplifiedDimension.LOGGER.error("Identifier to the specified nbt file was not found! : {}", nbtRL);
            return false;
        }
        BlockRotation rotation = BlockRotation.random(random);

        // Rotated blockpos for the nbt's sizes to be used later. Ignore Y
        BlockPos fullLengths = new BlockPos(
                Math.abs(template.getSize().rotate(rotation).getX()),
                Math.abs(template.getSize().rotate(rotation).getY()),
                Math.abs(template.getSize().rotate(rotation).getZ()));

        BlockPos halfLengths = new BlockPos(
                fullLengths.getX() / 2,
                fullLengths.getY() / 2,
                fullLengths.getZ() / 2);

        BlockPos.Mutable mutable = new BlockPos.Mutable().set(position);
        Chunk cachedChunk = world.getChunk(mutable);

        int xMin = -halfLengths.getX() + 1;
        int xMax = halfLengths.getX() - 1;
        int zMin = -halfLengths.getZ() + 1;
        int zMax = halfLengths.getZ() - 1;
        int validOpenings = 0;
        int ceilingOpenings = 0;
        int ceiling = template.getSize().getY();

        for (int x = xMin - 1; x <= xMax + 1; ++x) {
            for (int z = zMin - 1; z <= zMax + 1; ++z) {
                for (int y = -2; y <= ceiling + 1; ++y) {
                    mutable.set(position).move(x, y, z);
                    if(mutable.getX() >> 4 != cachedChunk.getPos().x || mutable.getZ() >> 4 != cachedChunk.getPos().z)
                        cachedChunk = world.getChunk(mutable);

                    BlockState state = cachedChunk.getBlockState(mutable);

                    // Dungeons cannot touch fluids if set to air mode and reverse if opposite
                    if(config.airRequirementIsNowWater ? state.isAir() : !state.getFluidState().isEmpty()){
                        return false;
                    }
                    // Floor must be complete
                    else if(!GeneralUtils.isFullCube(world, mutable, state)){
                        if (y == -1) {
                            return false;
                        }
                        else if(state.isIn(BlockTags.LEAVES)){
                            continue; // ignore leaves
                        }
                        else if (y == ceiling) {
                            ceilingOpenings++;
                        }
                    }

                    // Check only along wall bottoms for openings
                    if ((x == xMin || x == xMax || z == zMin || z == zMax) && y == 0 && isValidNonSolidBlock(config, state))
                    {
                        BlockState aboveState = cachedChunk.getBlockState(mutable.move(Direction.UP));
                        if(config.airRequirementIsNowWater ?
                            !aboveState.getFluidState().isEmpty() :
                            aboveState.isAir())
                        {
                            validOpenings++;
                        }
                        mutable.move(Direction.DOWN);
                    }

                    // Too much open space. Quit
                    if(validOpenings > config.maxAirSpace || ceilingOpenings >= config.maxAirSpace){
                        return false;
                    }
                }
            }
        }

        // offset the dungeon such as ocean dungeons down 1
        position = position.up(config.structureYOffset);

        // Check if we meet minimum for open space.
        if (validOpenings >= config.minAirSpace) {
            //UltraAmplifiedDimension.LOGGER.log(Level.INFO, nbtRL + " at X: "+position.getX() +", "+position.getY()+", "+position.getZ());
            StructurePlacementData placementsettings = (new StructurePlacementData()).setRotation(rotation).setPosition(halfLengths).setIgnoreEntities(false);
            config.processor.get().getList().forEach(placementsettings::addProcessor); // add all processors
            addBlocksToWorld(template, world, chunkGenerator, mutable.set(position).move(-halfLengths.getX(), -1, -halfLengths.getZ()), placementsettings, 2, random, config);
            spawnLootBlocks(world, random, position, config, fullLengths, halfLengths, mutable);
            return true;
        }

        return false;
    }

    private void spawnLootBlocks(StructureWorldAccess world, Random random, BlockPos position, NbtDungeonConfig config, BlockPos fullLengths, BlockPos halfLengths, BlockPos.Mutable mutable) {
        boolean isPlacingChestLikeBlock = config.lootBlock.getBlock() instanceof ChestBlock;

        // Add chests that are wall based
        for(int currentChestCount = 0; currentChestCount < config.maxNumOfChests; ++currentChestCount) {
            for (int currentChestAttempt = 0; currentChestAttempt < fullLengths.getX() + fullLengths.getZ() + halfLengths.getY(); ++currentChestAttempt) {
                if (currentChestCount == config.maxNumOfChests) {
                    return; // early exit
                }

                mutable.set(position).move(
                        random.nextInt(Math.max(fullLengths.getX() - 2, 1)) - halfLengths.getX() + 1,
                        random.nextInt(Math.max(fullLengths.getY() - 1, 1)),
                        random.nextInt(Math.max(fullLengths.getZ() - 2, 1)) - halfLengths.getZ() + 1);

                BlockState currentBlock = world.getBlockState(mutable);
                if (isValidNonSolidBlock(config, currentBlock)) {
                    if(world.getBlockState(mutable.move(Direction.DOWN)).isSideSolidFullSquare(world, mutable, Direction.UP)){
                        mutable.move(Direction.UP);
                        boolean isOnWall = false;

                        for(Direction direction : Direction.Type.HORIZONTAL){

                            mutable.move(direction);
                            BlockState neighboringState = world.getBlockState(mutable);
                            mutable.move(direction.getOpposite());

                            if(isPlacingChestLikeBlock && neighboringState.getBlock() instanceof ChestBlock){
                                // Only connect to single chests
                                if(neighboringState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE){

                                    // Set current chest direction to the side for the double chest connection.
                                    Direction doubleChestDirection = direction.rotateYClockwise();
                                    boolean flippedDirections = false;
                                    BlockState blockState = world.getBlockState(mutable.offset(direction));

                                    // Face opposite direction if facing wall.
                                    if(GeneralUtils.isFullCube(world, mutable, blockState) && !(blockState.getBlock() instanceof SpawnerBlock)){
                                        doubleChestDirection = doubleChestDirection.getOpposite();
                                        flippedDirections = true;
                                    }

                                    world.setBlockState(mutable,
                                            config.lootBlock
                                                .with(ChestBlock.WATERLOGGED, currentBlock.getFluidState().isIn(FluidTags.WATER))
                                                .with(ChestBlock.FACING, doubleChestDirection)
                                                .with(ChestBlock.CHEST_TYPE, flippedDirections ? ChestType.LEFT : ChestType.RIGHT), 2);
                                    LootableContainerBlockEntity.setLootTable(world, random, mutable, config.chestResourceLocation);

                                    BlockState blockBelow = world.getBlockState(mutable.down());
                                    if(blockBelow.contains(SlabBlock.TYPE)){
                                        world.setBlockState(mutable.down(), blockBelow.with(SlabBlock.TYPE, SlabType.DOUBLE), 3);
                                    }

                                    // Set neighboring chest to face same way too
                                    world.setBlockState(mutable.move(direction), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, doubleChestDirection).with(ChestBlock.CHEST_TYPE, flippedDirections ? ChestType.RIGHT : ChestType.LEFT), 2);
                                    currentChestCount++;
                                    isOnWall = false; // Skip wall code as we already placed chest
                                    break;
                                }
                            }
                            else if(GeneralUtils.isFullCube(world, mutable, neighboringState) && !(neighboringState.getBlock() instanceof SpawnerBlock)){
                                isOnWall = true;
                            }
                        }

                        // Is not next to another chest.
                        if(isOnWall){
                            BlockState lootBlock = config.lootBlock;
                            if(lootBlock.contains(Properties.WATERLOGGED)){
                                lootBlock.with(Properties.WATERLOGGED, currentBlock.getFluidState().isIn(FluidTags.WATER));
                            }
                            if(lootBlock.contains(Properties.HORIZONTAL_FACING)){
                                lootBlock = StructurePiece.orientateChest(world, mutable, lootBlock);
                            }

                            // Set chest to face away from wall.
                            world.setBlockState(mutable, lootBlock, 2);
                            LootableContainerBlockEntity.setLootTable(world, random, mutable, config.chestResourceLocation);

                            BlockState blockBelow = world.getBlockState(mutable.down());
                            if(blockBelow.contains(SlabBlock.TYPE)){
                                world.setBlockState(mutable.down(), blockBelow.with(SlabBlock.TYPE, SlabType.DOUBLE), 3);
                            }

                            currentChestCount++;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds blocks and entities from this structure to the given world.
     */
    public void addBlocksToWorld(Structure structure, StructureWorldAccess world, ChunkGenerator chunkGenerator, BlockPos pos, StructurePlacementData placementIn, int flags, Random random, NbtDungeonConfig config) {
        TemplateInvoker structureAccessor = ((TemplateInvoker) structure);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if (!structureAccessor.uad_getBlocks().isEmpty()) {
            List<Structure.StructureBlockInfo> list = placementIn.getRandomBlockInfos(structureAccessor.uad_getBlocks(), pos).getAll();
            if ((!list.isEmpty() || !placementIn.shouldIgnoreEntities() && !structureAccessor.uad_getEntities().isEmpty()) && structureAccessor.uad_getSize().getX() >= 1 && structureAccessor.uad_getSize().getY() >= 1 && structureAccessor.uad_getSize().getZ() >= 1) {
                BlockBox mutableboundingbox = placementIn.getBoundingBox();
                List<BlockPos> list1 = Lists.newArrayListWithCapacity(placementIn.shouldPlaceFluids() ? list.size() : 0);
                List<Pair<BlockPos, CompoundTag>> list2 = Lists.newArrayListWithCapacity(list.size());
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int minZ = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                int maxZ = Integer.MIN_VALUE;

                for (Structure.StructureBlockInfo template$blockinfo : Structure.process(world, pos, pos, placementIn, list)) {
                    BlockPos blockpos = template$blockinfo.pos;

                    if (mutableboundingbox == null || mutableboundingbox.contains(blockpos)) {
                        FluidState ifluidstate = placementIn.shouldPlaceFluids() ? world.getFluidState(blockpos) : null;
                        BlockState blockstate = template$blockinfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                        if (template$blockinfo.tag != null) {
                            BlockEntity blockentity = world.getBlockEntity(blockpos);
                            Clearable.clear(blockentity);
                            world.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 3);
                        }

                        BlockState originalBlockState = world.getBlockState(blockpos);
                        if (!originalBlockState.getBlock().hasBlockEntity())
                        {
                            // No floating chests or spawners
                            BlockState aboveState = world.getBlockState(mutable.set(blockpos).move(Direction.UP));

                            boolean forcePlaceBlock = false;
                            if(config.blocksToAlwaysPlace.isPresent()){
                                forcePlaceBlock = config.blocksToAlwaysPlace.get().contains(blockstate);
                            }

                            if(isNotSpawnerOrChest(aboveState) &&
                                    (config.replaceAir ||
                                    forcePlaceBlock ||
                                    GeneralUtils.isFullCube(world, mutable, originalBlockState) ||
                                    blockstate.getBlock().hasBlockEntity()))
                            {
                                // Attempt to let leaves stay in the dungeon space and not be cut off
                                if(!(isValidNonSolidBlock(config, blockstate) && originalBlockState.isIn(BlockTags.LEAVES))){
                                    world.setBlockState(blockpos, blockstate, 3);

                                    minX = Math.min(minX, blockpos.getX());
                                    minY = Math.min(minY, blockpos.getY());
                                    minZ = Math.min(minZ, blockpos.getZ());
                                    maxX = Math.max(maxX, blockpos.getX());
                                    maxY = Math.max(maxY, blockpos.getY());
                                    maxZ = Math.max(maxZ, blockpos.getZ());
                                    list2.add(Pair.of(blockpos, template$blockinfo.tag));

                                    if (template$blockinfo.tag != null){
                                        setBlockEntity(world, placementIn, random, config, template$blockinfo, blockpos, blockstate);
                                    }

                                    if (ifluidstate != null && blockstate.getBlock() instanceof FluidFillable) {
                                        ((FluidFillable) blockstate.getBlock()).tryFillWithFluid(world, blockpos, blockstate, ifluidstate);
                                        if (!ifluidstate.isStill()) {
                                            list1.add(blockpos);
                                        }
                                    }
                                }
                            }

                            // Prevent plants remaining at edge of dungeons like bamboo which then breaks as dungeon floor isn't valid for bamboo.
                            else if(!isValidNonSolidBlock(config, blockstate) && !originalBlockState.isIn(BlockTags.LEAVES) && !originalBlockState.isOpaque() && originalBlockState.getFluidState().isEmpty()){
                                world.setBlockState(blockpos, config.airRequirementIsNowWater ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState(), 3);

                                BlockPos.Mutable mutable1 = new BlockPos.Mutable().set(blockpos);
                                BlockState blockState = world.getBlockState(mutable1.move(Direction.UP));

                                while(mutable1.getY() < chunkGenerator.getWorldHeight() && !blockState.canPlaceAt(world, mutable1)){
                                    world.setBlockState(mutable1, Blocks.AIR.getDefaultState(), 3);
                                    blockState = world.getBlockState(mutable1.move(Direction.UP));
                                }
                            }
                        }
                    }
                }

                // fillFluidStates(world, list1);

                if (minX <= maxX) {
                    if (!placementIn.shouldUpdateNeighbors()) {
                        VoxelSet voxelshapepart = new BitSetVoxelSet(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
                        setVoxelShapeParts(world, flags, list2, minX, minY, minZ, voxelshapepart);
                    }

                    placeBlocks(world, placementIn, list2);
                }

                if (!placementIn.shouldIgnoreEntities()) {
                    structureAccessor.uad_invokeSpawnEntities(world, pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getPosition(), placementIn.getBoundingBox(), placementIn.method_27265());
                }
            }
        }
    }

    private void setBlockEntity(ServerWorldAccess world, StructurePlacementData placementIn, Random random, NbtDungeonConfig config, Structure.StructureBlockInfo template$blockinfo, BlockPos blockpos, BlockState blockstate) {
        BlockEntity blockentity1 = world.getBlockEntity(blockpos);
        if (blockentity1 != null) {
            template$blockinfo.tag.putInt("x", blockpos.getX());
            template$blockinfo.tag.putInt("y", blockpos.getY());
            template$blockinfo.tag.putInt("z", blockpos.getZ());
            blockentity1.fromTag(template$blockinfo.state, template$blockinfo.tag);
            blockentity1.applyMirror(placementIn.getMirror());
            blockentity1.applyRotation(placementIn.getRotation());

            if (blockentity1 instanceof MobSpawnerBlockEntity) {
                EntityType<?> entity = GeneralUtils.getRandomEntry(config.spawnerResourcelocationsAndWeights, random);

                if(entity != null){
                    ((MobSpawnerBlockEntity)blockentity1).getLogic().setEntityId(entity);
                }
                else{
                    UltraAmplifiedDimension.LOGGER.log(Level.WARN, "EntityType in a dungeon does not exist in registry!");
                }
            }
            else if(blockentity1 instanceof LootableContainerBlockEntity){
                if(blockstate.isOf(Blocks.CHEST)){
                    world.setBlockState(blockpos, StructurePiece.orientateChest(world, blockpos, Blocks.CHEST.getDefaultState()), 2);
                }
                LootableContainerBlockEntity.setLootTable(world, random, blockpos, config.chestResourceLocation);
            }
        }
    }

    private void fillFluidStates(ServerWorldAccess world, List<BlockPos> blockPosList) {
        boolean flag = true;
        while (flag && !blockPosList.isEmpty()) {
            flag = false;
            Iterator<BlockPos> iterator = blockPosList.iterator();

            while (iterator.hasNext()) {
                BlockPos blockpos2 = iterator.next();
                BlockPos blockpos3 = blockpos2;
                FluidState ifluidstate2 = world.getFluidState(blockpos2);

                for (int directionIndex = 1; directionIndex < 6 && !ifluidstate2.isStill(); ++directionIndex) {
                    // Skip down direction
                    Direction direction = Direction.byId(directionIndex);
                    BlockPos blockpos1 = blockpos3.offset(direction);
                    FluidState ifluidstate1 = world.getFluidState(blockpos1);
                    if (ifluidstate1.getHeight(world, blockpos1) > ifluidstate2.getHeight(world, blockpos3) ||
                            ifluidstate1.isStill() && !ifluidstate2.isStill())
                    {
                        ifluidstate2 = ifluidstate1;
                        blockpos3 = blockpos1;
                    }
                }

                if (ifluidstate2.isStill()) {
                    BlockState blockstate2 = world.getBlockState(blockpos2);
                    Block block = blockstate2.getBlock();
                    if (block instanceof FluidFillable) {
                        ((FluidFillable) block).tryFillWithFluid(world, blockpos2, blockstate2, ifluidstate2);
                        flag = true;
                        iterator.remove();
                    }
                }
            }
        }
    }

    private boolean isNotSpawnerOrChest(BlockState state){
        return !state.isOf(Blocks.SPAWNER) && !(state.getBlock() instanceof AbstractChestBlock);
    }

    protected static void placeBlocks(ServerWorldAccess world, StructurePlacementData placementIn, List<Pair<BlockPos, CompoundTag>> list2) {
        for (Pair<BlockPos, CompoundTag> pair : list2) {
            BlockPos blockpos4 = pair.getFirst();

            if (!placementIn.shouldUpdateNeighbors()) {
                BlockState blockstate1 = world.getBlockState(blockpos4);
                BlockState blockstate3 = Block.postProcessState(blockstate1, world, blockpos4);
                if (blockstate1 != blockstate3) {
                    world.setBlockState(blockpos4, blockstate3, 3);
                }

                world.updateNeighbors(blockpos4, blockstate3.getBlock());
            }

            if (pair.getSecond() != null) {
                BlockEntity blockentity2 = world.getBlockEntity(blockpos4);
                if (blockentity2 != null) {
                    blockentity2.markDirty();
                }
            }
        }
    }

    protected static void setVoxelShapeParts(ServerWorldAccess world, int flags, List<Pair<BlockPos, CompoundTag>> list2, int x, int y, int z, VoxelSet voxelshapepart) {
        for (Pair<BlockPos, CompoundTag> pair1 : list2) {
            BlockPos blockpos5 = pair1.getFirst();
            voxelshapepart.set(blockpos5.getX() - x, blockpos5.getY() - y, blockpos5.getZ() - z, true, true);
        }

        Structure.updateCorner(world, flags, voxelshapepart, x, y, z);
    }

    private boolean isValidNonSolidBlock(NbtDungeonConfig config, BlockState state){
        if(config.airRequirementIsNowWater){
            return !state.getFluidState().isEmpty();
        }
        return state.isAir();
    }
}
