package com.telepathicgrunt.ultraamplifieddimension.world.processors;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADProcessors;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;

/**
 * Workaround for https://bugs.mojang.com/browse/MC-130584
 * Due to a hardcoded field in Templates, any waterloggable blocks in structures replacing water in the world will become waterlogged.
 * Idea of workaround is detect if we are placing a waterloggable block and if so, remove the water in the world instead.
 */
public class WaterloggingFixProcessor extends StructureProcessor {

    public static final Codec<WaterloggingFixProcessor> CODEC = Codec.unit(WaterloggingFixProcessor::new);
    private WaterloggingFixProcessor() { }

    @Override
    public Structure.StructureBlockInfo process(WorldView worldReader, BlockPos pos, BlockPos pos2, Structure.StructureBlockInfo infoIn1, Structure.StructureBlockInfo infoIn2, StructurePlacementData settings) {

        // ONLY RUN THIS IF STRUCTURE BLOCK IS A DRY WATERLOGGABLE BLOCK
        ChunkPos currentChunkPos = new ChunkPos(infoIn2.pos);
        if(infoIn2.state.getBlock() instanceof Waterloggable && !infoIn2.state.get(Properties.WATERLOGGED)){
            Chunk currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
            if(worldReader.getFluidState(infoIn2.pos).isIn(FluidTags.WATER)){
                currentChunk.setBlockState(infoIn2.pos, Blocks.STONE.getDefaultState(), false);
            }

            // Remove water in adjacent blocks across chunk boundaries and above/below as well
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                mutable.set(infoIn2.pos).move(direction);
                if (currentChunkPos.x != mutable.getX() >> 4 || currentChunkPos.z != mutable.getZ() >> 4) {
                    currentChunk = worldReader.getChunk(mutable);
                    currentChunkPos = new ChunkPos(mutable);
                }

                if (currentChunk.getFluidState(mutable).isIn(FluidTags.WATER)) {
                    currentChunk.setBlockState(mutable, Blocks.STONE.getDefaultState(), false);
                }
            }
        }

        return infoIn2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return UADProcessors.WATER_FIX_PROCESSOR;
    }
}
