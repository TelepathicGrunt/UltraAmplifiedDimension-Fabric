package com.telepathicgrunt.ultraamplifieddimension.mixin.features;

import net.minecraft.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Structure.class)
public interface TemplateAccessor {

    @Accessor("blockInfoLists")
    List<Structure.PalettedBlockInfoList> uad_getBlocks();
}
