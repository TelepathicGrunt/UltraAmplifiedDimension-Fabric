package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InterpolatedNoiseSampler.class)
public interface InterpolatedNoiseSamplerAccessor {
    @Accessor("lowerInterpolatedNoise")
    OctavePerlinNoiseSampler uad_getLowerInterpolatedNoise();

    @Accessor("upperInterpolatedNoise")
    OctavePerlinNoiseSampler uad_getUpperInterpolatedNoise();

    @Accessor("interpolationNoise")
    OctavePerlinNoiseSampler uad_getInterpolationNoise();
}
