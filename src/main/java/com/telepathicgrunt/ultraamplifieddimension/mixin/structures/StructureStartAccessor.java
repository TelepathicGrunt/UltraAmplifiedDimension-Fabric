package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Random;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.gen.ChunkRandom;

@Mixin(StructureStart.class)
public interface StructureStartAccessor {
    @Invoker
    void callSetBoundingBoxFromChildren();

    @Invoker
    void callRandomUpwardTranslation(Random p_214626_1_, int p_214626_2_, int p_214626_3_);

    @Accessor
    ChunkRandom getRandom();

    @Accessor
    BlockBox getBoundingBox();

    @Accessor
    List<StructurePiece> getChildren();
}
