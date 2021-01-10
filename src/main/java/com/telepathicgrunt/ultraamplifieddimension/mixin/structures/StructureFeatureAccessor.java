package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureFeature.class)
public interface StructureFeatureAccessor {
    @Mutable
    @Accessor
    static void setJIGSAW_STRUCTURES(List<StructureFeature<?>> JIGSAW_STRUCTURES) {
        throw new UnsupportedOperationException();
    }
}
