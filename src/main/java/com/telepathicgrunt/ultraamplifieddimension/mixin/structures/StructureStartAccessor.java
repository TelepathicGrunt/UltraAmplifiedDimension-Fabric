package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Random;

@Mixin(StructureStart.class)
public interface StructureStartAccessor {
    @Invoker("randomUpwardTranslation")
    void uad_callRandomUpwardTranslation(Random p_214626_1_, int p_214626_2_, int p_214626_3_);

    @Accessor("random")
    ChunkRandom uad_getRandom();
}
