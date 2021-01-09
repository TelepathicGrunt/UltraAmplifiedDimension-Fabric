package com.telepathicgrunt.ultraamplifieddimension.blocks;

import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.SandBlock;
import net.minecraft.sound.BlockSoundGroup;


public class GlowsandBlock extends SandBlock {

    public GlowsandBlock() {
        super(14406560, Settings.of(Material.AGGREGATE, MaterialColor.SAND).strength(0.4F).sounds(BlockSoundGroup.SAND).luminance((blockState) -> 15));
    }

}
