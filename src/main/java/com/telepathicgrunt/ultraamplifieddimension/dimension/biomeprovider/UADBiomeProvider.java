package com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer.*;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.LayerAccessor;
import com.telepathicgrunt.ultraamplifieddimension.utils.WorldSeedHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.*;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UADBiomeProvider extends BiomeSource {

    public static final Codec<UADBiomeProvider> CODEC =
            RecordCodecBuilder.create((instance) -> instance.group(
                    Codec.LONG.fieldOf("seed").orElseGet(WorldSeedHolder::getSeed).forGetter((biomeSource) -> biomeSource.seed),
                    RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter((biomeSource) -> biomeSource.dynamicRegistry),
                    Codec.intRange(1, 20).fieldOf("biome_size").forGetter((biomeSource) -> biomeSource.biomeSize),
                    Codec.floatRange(0, 1).fieldOf("sub_biome_rate").forGetter((biomeSource) -> biomeSource.subBiomeRate),
                    Codec.floatRange(0, 1).fieldOf("mutated_biome_rate").forGetter((biomeSource) -> biomeSource.mutatedBiomeRate),
                    RegionManager.CODEC.fieldOf("regions").forGetter((biomeSource) -> biomeSource.regionManager),
                    Codec.BOOL.fieldOf("import_all_modded_biomes").orElse(false).forGetter((biomeSource) -> biomeSource.automaticallyImportModdedBiomes),
                    Identifier.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList).fieldOf("imported_biome_blacklist").orElse(new HashSet<>()).forGetter((biomeSource) -> biomeSource.blacklistedModdedBiomes))
            .apply(instance, instance.stable(UADBiomeProvider::new)));

    private final Registry<Biome> dynamicRegistry;
    private final RegionManager regionManager;
    private final BiomeLayerSampler biomeSampler;
    private final int biomeSize;
    private final float subBiomeRate;
    private final float mutatedBiomeRate;
    private final long seed;
    private final Set<Integer> printedMissingBiomes = new HashSet<>();
    private final boolean automaticallyImportModdedBiomes;
    private final HashSet<Identifier> blacklistedModdedBiomes;

    public UADBiomeProvider(long seed, Registry<Biome> biomeRegistry, int biomeSize, float subBiomeRate, float mutatedBiomeRate, RegionManager regionManager, boolean automaticallyImportModdedBiomes, HashSet<Identifier> blacklistedModdedBiomes) {
        super(Stream.concat(biomeRegistry.getEntries().stream()
                .filter(entry -> entry.getKey().getValue().getNamespace().equals(UltraAmplifiedDimension.MODID))
                .map(Map.Entry::getValue),
                getAllModdedBiomes(biomeRegistry, regionManager, automaticallyImportModdedBiomes, blacklistedModdedBiomes))
                    .collect(Collectors.toList()));

        this.seed = seed;
        this.biomeSize = biomeSize;
        this.subBiomeRate = subBiomeRate;
        this.mutatedBiomeRate = mutatedBiomeRate;
        this.regionManager = regionManager;
        this.dynamicRegistry = biomeRegistry;
        this.automaticallyImportModdedBiomes = automaticallyImportModdedBiomes;
        this.blacklistedModdedBiomes = blacklistedModdedBiomes;

        if(automaticallyImportModdedBiomes){
            this.regionManager.importAllModdedBiomes(this.dynamicRegistry, this.blacklistedModdedBiomes);
        }

        // Construct the biome layers last so all fields are ready
        this.biomeSampler = new BiomeLayerSampler(build((salt) -> new CachingLayerContext(25, this.seed, salt)));

        // Reset this as exiting and joining a different world could have completely different biomes in the dimension json
        this.printedMissingBiomes.clear();
    }

    private static Stream<Biome> getAllModdedBiomes(Registry<Biome> biomeRegistry, RegionManager regionManager, boolean automaticallyImportModdedBiomes, HashSet<Identifier> blacklistedModdedBiomes) {
        if(automaticallyImportModdedBiomes){
            return regionManager.importAllModdedBiomes(biomeRegistry, blacklistedModdedBiomes).stream();
        }
        else {
            return Collections.EMPTY_LIST.stream();
        }
    }

    /*
     * LAYER KEY FOR MYSELF:
     * 0 = ocean region
     * 1 = end region
     * 2 = nether region
     * 3 = hot region
     * 4 = warm region
     * 5 = cool region
     * 6 = icy region
     */
    public <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LongFunction<C> contextFactory) {
        LayerFactory<T> layer = (new BaseRegionLayer()).create(contextFactory.apply(1L));
        layer = new OceanBaseRegionLayer().create(contextFactory.apply(3459L), layer);
        layer = new ReduceOceanNoiseAndMagnifyEndNetherLayer().create(contextFactory.apply(2324L), layer);
        layer = ScaleLayer.NORMAL.create(contextFactory.apply(2402L), layer);
        layer = ScaleLayer.NORMAL.create(contextFactory.apply(6203L), layer);
        layer = new MainBiomeLayer(this.dynamicRegistry, this.regionManager).create(contextFactory.apply(1567L), layer);

        for(int currentExtraZoom = 0; currentExtraZoom < this.biomeSize; currentExtraZoom++){
            if(currentExtraZoom % 3 != this.biomeSize / 2){
                layer = ScaleLayer.NORMAL.create(contextFactory.apply(1503L + currentExtraZoom), layer);
            }
            else{
                layer = ScaleLayer.FUZZY.create(contextFactory.apply(1111L + (currentExtraZoom * 31L)), layer);
            }

            if (currentExtraZoom == 1 || this.biomeSize == 1) {
                layer = new ShoreEdgeHillsAndMutatationsBiomeLayer(this.dynamicRegistry, this.regionManager, this.subBiomeRate, this.mutatedBiomeRate, this.biomeSize).create(contextFactory.apply(3235L), layer);
            }
        }

        layer = ScaleLayer.FUZZY.create(contextFactory.apply(8204L), layer);
        return layer;
    }


    @Override
    public Biome getBiomeForNoiseGen(int x, int y, int z) {
        int biomeID = ((LayerAccessor)this.biomeSampler).uad_getSampler().sample(x, z);
        Biome biome = this.dynamicRegistry.get(biomeID);

        // If unknown biome, try a different attempt to get the biome.
        if (biome == null) {

            // Print the unknown biome ID only ONCE instead of spamming logs and lagging the game to heck and back.
            if(!printedMissingBiomes.contains(biomeID)){
                printedMissingBiomes.add(biomeID);
                UltraAmplifiedDimension.LOGGER.error("Unknown biome id: " + biomeID + "   Now using non-dynamic registry for biomes which might be wrong! Let Ultra Amplified Dev know about your issue.");
            }

            // Return void if backup biome way also failed to get a biome too
            Biome backupBiome = this.dynamicRegistry.get(BuiltinBiomes.fromRawId(biomeID));
            if(backupBiome == null){
                return Objects.requireNonNull(BuiltinRegistries.BIOME.get(BiomeKeys.THE_VOID));
            }

            return backupBiome;
        }

        return biome;
    }


    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new UADBiomeProvider(seed, this.dynamicRegistry, this.biomeSize, this.subBiomeRate, this.mutatedBiomeRate, this.regionManager, this.automaticallyImportModdedBiomes, this.blacklistedModdedBiomes);
    }

    /*
     * LAYER KEY FOR MYSELF:
     * 0 = ocean region
     * 1 = end region
     * 2 = nether region
     * 3 = hot region
     * 4 = warm region
     * 5 = cool region
     * 6 = icy region
     *
     * Do not change enum order
     */
    public enum REGIONS {
        OCEAN,
        END,
        NETHER,
        HOT,
        WARM,
        COOL,
        ICY
    }
}
