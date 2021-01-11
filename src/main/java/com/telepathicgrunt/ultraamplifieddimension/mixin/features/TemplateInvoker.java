package com.telepathicgrunt.ultraamplifieddimension.mixin.features;

import net.minecraft.structure.Structure;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Structure.class)
public interface TemplateInvoker {

    @Accessor("blockInfoLists")
    List<Structure.PalettedBlockInfoList> uad_getBlocks();

    @Accessor("entities")
    List<Structure.StructureEntityInfo> uad_getEntities();

    @Accessor("size")
    BlockPos uad_getSize();

    @Invoker("spawnEntities")
    void uad_invokeSpawnEntities(ServerWorldAccess serverWorldAccess, BlockPos pos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos pivot, BlockBox area, boolean bl);
}
