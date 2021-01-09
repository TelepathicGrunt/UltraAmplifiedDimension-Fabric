package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.util.math.BlockBox;

@Mixin(MineshaftGenerator.MineshaftRoom.class)
public interface MineshaftRoomAccessor {
    @Accessor
    List<BlockBox> getEntrances();
}
