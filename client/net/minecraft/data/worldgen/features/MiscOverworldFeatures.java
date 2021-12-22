package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;

public class MiscOverworldFeatures {
   public static final ConfiguredFeature<NoneFeatureConfiguration, ?> ICE_SPIKE;
   public static final ConfiguredFeature<DiskConfiguration, ?> ICE_PATCH;
   public static final ConfiguredFeature<BlockStateConfiguration, ?> FOREST_ROCK;
   public static final ConfiguredFeature<BlockStateConfiguration, ?> ICEBERG_PACKED;
   public static final ConfiguredFeature<BlockStateConfiguration, ?> ICEBERG_BLUE;
   public static final ConfiguredFeature<NoneFeatureConfiguration, ?> BLUE_ICE;
   public static final ConfiguredFeature<LakeFeature.Configuration, ?> LAKE_LAVA;
   public static final ConfiguredFeature<DiskConfiguration, ?> DISK_CLAY;
   public static final ConfiguredFeature<DiskConfiguration, ?> DISK_GRAVEL;
   public static final ConfiguredFeature<DiskConfiguration, ?> DISK_SAND;
   public static final ConfiguredFeature<?, ?> FREEZE_TOP_LAYER;
   public static final ConfiguredFeature<?, ?> BONUS_CHEST;
   public static final ConfiguredFeature<?, ?> VOID_START_PLATFORM;
   public static final ConfiguredFeature<NoneFeatureConfiguration, ?> DESERT_WELL;
   public static final ConfiguredFeature<SpringConfiguration, ?> SPRING_LAVA_OVERWORLD;
   public static final ConfiguredFeature<SpringConfiguration, ?> SPRING_LAVA_FROZEN;
   public static final ConfiguredFeature<SpringConfiguration, ?> SPRING_WATER;

   public MiscOverworldFeatures() {
      super();
   }

   static {
      ICE_SPIKE = FeatureUtils.register("ice_spike", Feature.ICE_SPIKE.configured(FeatureConfiguration.NONE));
      ICE_PATCH = FeatureUtils.register("ice_patch", Feature.ICE_PATCH.configured(new DiskConfiguration(Blocks.PACKED_ICE.defaultBlockState(), UniformInt.method_45(2, 3), 1, List.of(Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.PODZOL.defaultBlockState(), Blocks.COARSE_DIRT.defaultBlockState(), Blocks.MYCELIUM.defaultBlockState(), Blocks.SNOW_BLOCK.defaultBlockState(), Blocks.ICE.defaultBlockState()))));
      FOREST_ROCK = FeatureUtils.register("forest_rock", Feature.FOREST_ROCK.configured(new BlockStateConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState())));
      ICEBERG_PACKED = FeatureUtils.register("iceberg_packed", Feature.ICEBERG.configured(new BlockStateConfiguration(Blocks.PACKED_ICE.defaultBlockState())));
      ICEBERG_BLUE = FeatureUtils.register("iceberg_blue", Feature.ICEBERG.configured(new BlockStateConfiguration(Blocks.BLUE_ICE.defaultBlockState())));
      BLUE_ICE = FeatureUtils.register("blue_ice", Feature.BLUE_ICE.configured(FeatureConfiguration.NONE));
      LAKE_LAVA = FeatureUtils.register("lake_lava", Feature.LAKE.configured(new LakeFeature.Configuration(BlockStateProvider.simple(Blocks.LAVA.defaultBlockState()), BlockStateProvider.simple(Blocks.STONE.defaultBlockState()))));
      DISK_CLAY = FeatureUtils.register("disk_clay", Feature.DISK.configured(new DiskConfiguration(Blocks.CLAY.defaultBlockState(), UniformInt.method_45(2, 3), 1, List.of(Blocks.DIRT.defaultBlockState(), Blocks.CLAY.defaultBlockState()))));
      DISK_GRAVEL = FeatureUtils.register("disk_gravel", Feature.DISK.configured(new DiskConfiguration(Blocks.GRAVEL.defaultBlockState(), UniformInt.method_45(2, 5), 2, List.of(Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()))));
      DISK_SAND = FeatureUtils.register("disk_sand", Feature.DISK.configured(new DiskConfiguration(Blocks.SAND.defaultBlockState(), UniformInt.method_45(2, 6), 2, List.of(Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()))));
      FREEZE_TOP_LAYER = FeatureUtils.register("freeze_top_layer", Feature.FREEZE_TOP_LAYER.configured(FeatureConfiguration.NONE));
      BONUS_CHEST = FeatureUtils.register("bonus_chest", Feature.BONUS_CHEST.configured(FeatureConfiguration.NONE));
      VOID_START_PLATFORM = FeatureUtils.register("void_start_platform", Feature.VOID_START_PLATFORM.configured(FeatureConfiguration.NONE));
      DESERT_WELL = FeatureUtils.register("desert_well", Feature.DESERT_WELL.configured(FeatureConfiguration.NONE));
      SPRING_LAVA_OVERWORLD = FeatureUtils.register("spring_lava_overworld", Feature.SPRING.configured(new SpringConfiguration(Fluids.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, new Block[]{Blocks.CALCITE, Blocks.DIRT}))));
      SPRING_LAVA_FROZEN = FeatureUtils.register("spring_lava_frozen", Feature.SPRING.configured(new SpringConfiguration(Fluids.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE))));
      SPRING_WATER = FeatureUtils.register("spring_water", Feature.SPRING.configured(new SpringConfiguration(Fluids.WATER.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, new Block[]{Blocks.CALCITE, Blocks.DIRT, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE}))));
   }
}
