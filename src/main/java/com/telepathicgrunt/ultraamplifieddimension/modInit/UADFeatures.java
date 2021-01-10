package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.world.features.*;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.*;

import java.util.function.Supplier;


public class UADFeatures {
    public static Feature<DefaultFeatureConfig> CONTAIN_LIQUID_FOR_OCEANS = null;
    public static Feature<DefaultFeatureConfig> CONTAIN_UNDERGROUND_LIQUIDS = null;
    public static Feature<HeightConfig> BIG_CACTUS = null;
    public static Feature<TwoBlockStateConfig> NON_LIQUID_WATERFALL = null;
    public static Feature<ColumnConfig> COLUMN_RAMP = null;
    public static Feature<ColumnConfig> COLUMN_VERTICAL = null;
    public static Feature<CountConfig> GLOW_PATCH = null;
    public static Feature<SingleStateFeatureConfig> LAKE_WIDE_SHALLOW = null;
    public static Feature<NbtFeatureConfig> NBT_FEATURE = null;
    public static Feature<NbtDungeonConfig> NBT_DUNGEON = null;
    public static Feature<DefaultFeatureConfig> NETHER_SEA_ADJUSTER = null;
    public static Feature<DefaultFeatureConfig> NETHER_LAVA_SPOT = null;
    public static Feature<RootConfig> ROOTS = null;
    public static Feature<DefaultFeatureConfig> SNOW_ICE_LAYER_HANDLER_FEATURE = null;
    public static Feature<DefaultFeatureConfig> SNOW_ICE_ALL_LAYERS = null;
    public static Feature<DefaultFeatureConfig> SNOW_ICE_TOP_LAYER = null;
    public static Feature<DefaultFeatureConfig> SNOW_LAYER_WITHOUT_ICE = null;
    public static Feature<DiskDryConfig> DISK_DRY = null;
    public static Feature<DefaultFeatureConfig> SWAMP_CROSS = null;
    public static Feature<TreeFeatureConfig> TREE_SWAMP_HORNED = null;
    public static Feature<OreFeatureConfig> ELLIPSOID_POCKET = null;
    public static Feature<DefaultFeatureConfig> VINES_LONG = null;
    public static Feature<HeightConfig> VINES_SHORT = null;
    public static Feature<DefaultFeatureConfig> HANGING_RUINS = null;
    public static Feature<PondConfig> POND = null;
    public static Feature<BambooConfig> SAFE_BAMBOO = null;
    public static Feature<TreeFeatureConfig> TREE_GIANT_DARK_OAK = null;
    public static Feature<BlockWithRuleReplaceConfig> ON_SOLID_BLOCK_PLACER = null;
    public static Feature<ProbabilityAndCountConfig> PROPER_SEAGRASS = null;
    public static Feature<SeaPickleConfig> PROPER_SEAPICKLES = null;
    public static Feature<ProbabilityAndCountConfig> PROPER_KELP = null;
    public static Feature<BoulderFeatureConfig> BOULDERS = null;
    public static Feature<GiantSpikeConfig> GIANT_SPIKE = null;
    public static Feature<SingleStateFeatureConfig> ICEBERG_WITHOUT_AIR = null;
    public static Feature<LootTableConfig> MARKED_TREASURE_CHEST = null;

    public static <B extends Feature<?>> B createFeature(String name, Supplier<B> featureSupplier) {
        return Registry.register(Registry.FEATURE, new Identifier(UltraAmplifiedDimension.MODID, name), featureSupplier.get());
    }

    public static void init(){
        CONTAIN_LIQUID_FOR_OCEANS = createFeature("contain_liquid_for_oceans", () -> new ContainLiquidForOceans(DefaultFeatureConfig.CODEC));
        CONTAIN_UNDERGROUND_LIQUIDS = createFeature("contain_underground_liquids", () -> new ContainUndergroundLiquids(DefaultFeatureConfig.CODEC));
        BIG_CACTUS = createFeature("big_cactus", () -> new BigCactus(HeightConfig.CODEC));
        NON_LIQUID_WATERFALL = createFeature("non_liquid_waterfall", () -> new NonLiquidWaterfall(TwoBlockStateConfig.CODEC));
        COLUMN_RAMP = createFeature("column_ramp", () -> new ColumnRamp(ColumnConfig.CODEC));
        COLUMN_VERTICAL = createFeature("column_vertical", () -> new ColumnVertical(ColumnConfig.CODEC));
        GLOW_PATCH = createFeature("glow_patch", () -> new GlowPatch(CountConfig.CODEC));
        LAKE_WIDE_SHALLOW = createFeature("lake_wide_shallow", () -> new LakeWideShallow(SingleStateFeatureConfig.CODEC));
        NBT_FEATURE = createFeature("nbt_feature", () -> new NbtFeature(NbtFeatureConfig.CODEC));
        NBT_DUNGEON = createFeature("nbt_dungeon", () -> new NbtDungeon(NbtDungeonConfig.CODEC));
        NETHER_SEA_ADJUSTER = createFeature("nether_sea_adjuster", () -> new NetherSeaAdjuster(DefaultFeatureConfig.CODEC));
        NETHER_LAVA_SPOT = createFeature("nether_lava_spot", () -> new NetherLavaSpot(DefaultFeatureConfig.CODEC));
        ROOTS = createFeature("roots", () -> new Roots(RootConfig.CODEC));
        SNOW_ICE_LAYER_HANDLER_FEATURE = createFeature("snow_ice_layer_handler_feature", () -> new SnowIceLayerHandlerFeature(DefaultFeatureConfig.CODEC));
        SNOW_ICE_ALL_LAYERS = createFeature("snow_ice_all_layers", () -> new SnowIceAllLayers(DefaultFeatureConfig.CODEC));
        SNOW_ICE_TOP_LAYER = createFeature("snow_ice_top_layer", () -> new SnowIceTopLayer(DefaultFeatureConfig.CODEC));
        SNOW_LAYER_WITHOUT_ICE = createFeature("snow_layer_without_ice", () -> new SnowLayerWithoutIceFeature(DefaultFeatureConfig.CODEC));
        DISK_DRY = createFeature("disk_dry", () -> new DiskDry(DiskDryConfig.CODEC));
        SWAMP_CROSS = createFeature("swamp_cross", () -> new SwampCross(DefaultFeatureConfig.CODEC));
        TREE_SWAMP_HORNED = createFeature("tree_swamp_horned", () -> new TreeSwampHorned(TreeFeatureConfig.CODEC));
        ELLIPSOID_POCKET = createFeature("ellipsoid_pocket", () -> new EllipsoidPocket(OreFeatureConfig.CODEC));
        VINES_LONG = createFeature("vines_long", () -> new VinesLong(DefaultFeatureConfig.CODEC));
        VINES_SHORT = createFeature("vines_short", () -> new VinesShort(HeightConfig.CODEC));
        HANGING_RUINS = createFeature("hanging_ruins", () -> new HangingRuins(DefaultFeatureConfig.CODEC));
        POND = createFeature("pond", () -> new Pond(PondConfig.CODEC));
        SAFE_BAMBOO = createFeature("safe_bamboo", () -> new SafeBamboo(BambooConfig.CODEC));
        TREE_GIANT_DARK_OAK = createFeature("tree_giant_dark_oak", () -> new TreeGiantDarkOak(TreeFeatureConfig.CODEC));
        ON_SOLID_BLOCK_PLACER = createFeature("on_solid_block_placer", () -> new OnSolidBlockPlacer(BlockWithRuleReplaceConfig.CODEC));
        PROPER_SEAGRASS = createFeature("proper_seagrass", () -> new ProperSeagrass(ProbabilityAndCountConfig.CODEC));
        PROPER_SEAPICKLES = createFeature("proper_sea_pickles", () -> new ProperSeapickle(SeaPickleConfig.CODEC));
        PROPER_KELP = createFeature("proper_kelp", () -> new ProperKelp(ProbabilityAndCountConfig.CODEC));
        BOULDERS = createFeature("boulders", () -> new Boulders(BoulderFeatureConfig.CODEC));
        GIANT_SPIKE = createFeature("giant_spike", () -> new GiantSpike(GiantSpikeConfig.CODEC));
        ICEBERG_WITHOUT_AIR = createFeature("iceberg_without_air", () -> new IcebergWithoutAir(SingleStateFeatureConfig.CODEC));
        MARKED_TREASURE_CHEST = createFeature("marked_treasure_chest", () -> new MarkedTreasureChest(LootTableConfig.CODEC));
    }

}