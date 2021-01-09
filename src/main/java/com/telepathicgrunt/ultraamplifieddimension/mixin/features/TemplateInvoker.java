package com.telepathicgrunt.ultraamplifieddimension.mixin.features;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Structure.class)
public interface TemplateInvoker {

    @Accessor("blockInfoLists")
    List<Structure.PalettedBlockInfoList> getBlocks();

    @Accessor("entities")
    List<Structure.StructureEntityInfo> getEntities();

    @Accessor("size")
    BlockPos getSize();

    @Invoker("addEntitiesToWorld")
    void invokeSpawnEntities(ServerWorldAccess serverIWorld, BlockPos pos, StructurePlacementData placementIn);
}
