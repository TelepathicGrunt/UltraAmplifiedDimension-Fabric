package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MineshaftGenerator.MineshaftRoom.class)
public interface MineshaftRoomAccessor {
    @Accessor("entrances")
    List<BlockBox> uad_getEntrances();
}
