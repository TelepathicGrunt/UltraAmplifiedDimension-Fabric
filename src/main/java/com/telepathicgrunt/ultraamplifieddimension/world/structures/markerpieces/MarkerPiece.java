package com.telepathicgrunt.ultraamplifieddimension.world.structures.markerpieces;

import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import java.util.Random;

public class MarkerPiece extends SimpleStructurePiece {
    public MarkerPiece(StructurePieceType structurePieceTypeIn, int componentTypeIn) {
        super(structurePieceTypeIn, componentTypeIn);
        this.boundingBox = new BlockBox(0, 0, 0, 0, 0, 0);
        this.pos = new BlockPos(0,0,0);
    }

    @Override
    protected void handleMetadata(String function, BlockPos pos, ServerWorldAccess worldIn, Random rand, BlockBox sbb) {
    }
}
