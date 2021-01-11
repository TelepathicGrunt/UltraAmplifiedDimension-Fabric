package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.google.common.collect.ImmutableList;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.mixin.structures.StructureFeatureAccessor;
import com.telepathicgrunt.ultraamplifieddimension.world.structures.GenericJigsawStructure;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class UADStructures {
    // Random seed
    // https://www.google.com/search?q=random+number
    // 2147483647
    public static Set<StructureFeature<?>> REGISTERED_UAD_STRUCTURES = new HashSet<>();

    public static StructureFeature<DefaultFeatureConfig> SUN_SHRINE = null;
    public static StructureFeature<DefaultFeatureConfig> STONEHENGE = null;
    public static StructureFeature<DefaultFeatureConfig> ICE_SPIKE_TEMPLE = null;
    public static StructureFeature<DefaultFeatureConfig> MUSHROOM_TEMPLE = null;

    private static <T extends StructureFeature<?>> T registerStructure(String name, Supplier<T> structure) {
        return Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(UltraAmplifiedDimension.MODID, name), structure.get());
    }

    /**
     * Adds the provided structure to the registry, and adds the separation settings.
     * The rarity of the structure is determined based on the values passed into
     * this method in the structureSeparationSettings argument. Called by registerFeatures.
     */
    public static <F extends StructureFeature<?>> void setupMapSpacingAndLand(
            F structure,
            boolean transformSurroundingLand)
    {
        StructureFeature.STRUCTURES.put(Registry.STRUCTURE_FEATURE.getId(structure).toString(), structure);
        REGISTERED_UAD_STRUCTURES.add(structure);

        if(transformSurroundingLand){
            StructureFeatureAccessor.setJIGSAW_STRUCTURES(
                    ImmutableList.<StructureFeature<?>>builder()
                        .addAll(StructureFeature.JIGSAW_STRUCTURES)
                        .add(structure)
                        .build()
            );
        }
    }

    /**
     * This is where we set the rarity of your structures and determine if land conforms to it.
     * See the comments in below for more details.
     */
    public static void setupStructures() {
        setupMapSpacingAndLand(SUN_SHRINE, true);
        setupMapSpacingAndLand(STONEHENGE, true);
        setupMapSpacingAndLand(ICE_SPIKE_TEMPLE, false);
        setupMapSpacingAndLand(MUSHROOM_TEMPLE, false);
    }

    /**
     * Creates the structures
     */
    public static void init(){
        SUN_SHRINE = registerStructure("sun_shrine", () -> (
                new GenericJigsawStructure(
                        DefaultFeatureConfig.CODEC,
                        new Identifier(UltraAmplifiedDimension.MODID, "sun_shrine_start"),
                        10,
                        0,
                        0,
                        0
                )
        ));

        STONEHENGE = registerStructure("stonehenge", () -> (
                new GenericJigsawStructure(
                        DefaultFeatureConfig.CODEC,
                        new Identifier(UltraAmplifiedDimension.MODID, "stonehenge/center_start"),
                        10,
                        -2,
                        0,
                        0
                )
        ));

        ICE_SPIKE_TEMPLE = registerStructure("ice_spike_temple", () -> (
                new GenericJigsawStructure(
                        DefaultFeatureConfig.CODEC,
                        new Identifier(UltraAmplifiedDimension.MODID, "ice_spike_temple/body_start"),
                        10,
                        -7,
                        0,
                        1
                )
        ));

        MUSHROOM_TEMPLE = registerStructure("mushroom_temple", () -> (
                new GenericJigsawStructure(
                        DefaultFeatureConfig.CODEC,
                        new Identifier(UltraAmplifiedDimension.MODID, "mushroom_temple/body_start"),
                        10,
                        -2,
                        0,
                        1
                )
        ));
    }
}
