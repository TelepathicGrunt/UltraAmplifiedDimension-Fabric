package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.google.common.collect.ImmutableList;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.world.structures.GenericJigsawStructure;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class UADStructures {
    // Random seed
    // https://www.google.com/search?q=random+number
    // 2147483647

    public static Set<StructureFeature<?>> REGISTERED_UAD_STRUCTURES = new HashSet<>();
    public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, UltraAmplifiedDimension.MODID);

    public static final RegistryObject<StructureFeature<DefaultFeatureConfig>> SUN_SHRINE = registerStructure("sun_shrine", () -> (
            new GenericJigsawStructure(
                    DefaultFeatureConfig.CODEC,
                    new Identifier(UltraAmplifiedDimension.MODID, "sun_shrine_start"),
                    10,
                    0,
                    0,
                    0
            )
    ));

    public static final RegistryObject<StructureFeature<DefaultFeatureConfig>> STONEHENGE = registerStructure("stonehenge", () -> (
            new GenericJigsawStructure(
                    DefaultFeatureConfig.CODEC,
                    new Identifier(UltraAmplifiedDimension.MODID, "stonehenge/center_start"),
                    10,
                    -2,
                    0,
                    0
            )
    ));

    public static final RegistryObject<StructureFeature<DefaultFeatureConfig>> ICE_SPIKE_TEMPLE = registerStructure("ice_spike_temple", () -> (
            new GenericJigsawStructure(
                    DefaultFeatureConfig.CODEC,
                    new Identifier(UltraAmplifiedDimension.MODID, "ice_spike_temple/body_start"),
                    10,
                    -7,
                    7,
                    1
            )
    ));

    public static final RegistryObject<StructureFeature<DefaultFeatureConfig>> MUSHROOM_TEMPLE = registerStructure("mushroom_temple", () -> (
            new GenericJigsawStructure(
                    DefaultFeatureConfig.CODEC,
                    new Identifier(UltraAmplifiedDimension.MODID, "mushroom_temple/body_start"),
                    10,
                    -2,
                    0,
                    1
            )
    ));

    private static <T extends StructureFeature<?>> RegistryObject<T> registerStructure(String name, Supplier<T> structure) {
        return STRUCTURES.register(name, structure);
    }

    /**
     * This is where we set the rarity of your structures and determine if land conforms to it.
     * See the comments in below for more details.
     */
    public static void setupStructures() {
        setupMapSpacingAndLand(SUN_SHRINE.get(), true);
        setupMapSpacingAndLand(STONEHENGE.get(), true);
        setupMapSpacingAndLand(ICE_SPIKE_TEMPLE.get(), true);
        setupMapSpacingAndLand(MUSHROOM_TEMPLE.get(), false);
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
        StructureFeature.STRUCTURES.put(structure.getRegistryName().toString(), structure);
        REGISTERED_UAD_STRUCTURES.add(structure);
        if(transformSurroundingLand){
            StructureFeature.JIGSAW_STRUCTURES =
                    ImmutableList.<StructureFeature<?>>builder()
                            .addAll(StructureFeature.JIGSAW_STRUCTURES)
                            .add(structure)
                            .build();
        }
    }
}
