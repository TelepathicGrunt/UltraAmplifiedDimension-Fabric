package com.telepathicgrunt.ultraamplifieddimension.world.decorators;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

import java.util.Random;
import java.util.stream.Stream;


public class RangeValidationPlacer extends Decorator<RangeValidationPlacerConfig> {
    public RangeValidationPlacer(Codec<RangeValidationPlacerConfig> codec) {
        super(codec);
    }


    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random rand, RangeValidationPlacerConfig config, BlockPos pos) {
        if(pos.getY() <= config.maxY && pos.getY() > config.minY){
            return Stream.of(pos);
        }
        else{
            return Stream.empty();
        }
    }
}
