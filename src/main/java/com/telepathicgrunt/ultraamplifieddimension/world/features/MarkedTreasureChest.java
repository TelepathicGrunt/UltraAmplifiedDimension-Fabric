package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.LootTableConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class MarkedTreasureChest extends Feature<LootTableConfig> {

    public MarkedTreasureChest(Codec<LootTableConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<LootTableConfig> context) {
        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable().set(context.getOrigin());

        //surface block must be solid with water above
        if (!context.getWorld().getBlockState(blockpos$Mutable).isOpaque() || context.getWorld().getBlockState(blockpos$Mutable.up()).getFluidState().isEmpty()) {
            return false;
        }

        //chest position must be surrounded by solid blocks
        for (Direction face : Direction.values()) {
            //skip above block as we already checked it
            if (face == Direction.UP) {
                continue;
            }

            if (!context.getWorld().getBlockState(blockpos$Mutable.down().offset(face)).isOpaque()) {
                return false;
            }
        }

        //if we reached here, then the placement is good for generation.

        //creates the x marker
        int size = 5;
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                int absx = Math.abs(x);
                int absz = Math.abs(z);

                //doesn't place red sand on corners
                if (absx == size && absz == size) {
                    continue;
                }

                //creates a thick x shape
                if (context.getRandom().nextFloat() < 0.85 && Math.abs(absx - absz) < 2) {
                    context.getWorld().setBlockState(blockpos$Mutable.set(context.getOrigin()).move(x, 0, z), Blocks.RED_SAND.getDefaultState(), 2);
                }
            }
        }

        blockpos$Mutable.set(context.getOrigin()).move(Direction.DOWN);
        //places chest with a 50/50 split between treasure chest and end city loot
        context.getWorld().setBlockState(blockpos$Mutable, StructurePiece.orientateChest(context.getWorld(), blockpos$Mutable, Blocks.CHEST.getDefaultState()), 2);
        LootableContainerBlockEntity.setLootTable(context.getWorld(), context.getRandom(), blockpos$Mutable, context.getConfig().lootTable);
        //UltraAmplified.Logger.log(Level.DEBUG, "Marked Treasure Chest "+" | "+blockpos.getX()+" "+blockpos.getZ());

        return true;
    }
}