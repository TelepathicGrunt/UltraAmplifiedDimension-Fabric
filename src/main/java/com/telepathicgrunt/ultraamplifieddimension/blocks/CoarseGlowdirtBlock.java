package com.telepathicgrunt.ultraamplifieddimension.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;


public class CoarseGlowdirtBlock extends Block {

    public CoarseGlowdirtBlock() {
        super(Settings.of(Material.SOIL, MaterialColor.DIRT).strength(0.4F).sounds(BlockSoundGroup.GRAVEL).luminance((blockState) -> 15));
    }

}
