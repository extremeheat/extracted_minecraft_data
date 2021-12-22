package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.material.Fluids;

public class NetherFeatures {
   public static final ConfiguredFeature<DeltaFeatureConfiguration, ?> DELTA;
   public static final ConfiguredFeature<ColumnFeatureConfiguration, ?> SMALL_BASALT_COLUMNS;
   public static final ConfiguredFeature<ColumnFeatureConfiguration, ?> LARGE_BASALT_COLUMNS;
   public static final ConfiguredFeature<ReplaceSphereConfiguration, ?> BASALT_BLOBS;
   public static final ConfiguredFeature<ReplaceSphereConfiguration, ?> BLACKSTONE_BLOBS;
   public static final ConfiguredFeature<NoneFeatureConfiguration, ?> GLOWSTONE_EXTRA;
   public static final WeightedStateProvider CRIMSON_VEGETATION_PROVIDER;
   public static final ConfiguredFeature<?, ?> CRIMSON_FOREST_VEGETATION;
   public static final ConfiguredFeature<?, ?> CRIMSON_FOREST_VEGETATION_BONEMEAL;
   public static final WeightedStateProvider WARPED_VEGETATION_PROVIDER;
   public static final ConfiguredFeature<?, ?> WARPED_FOREST_VEGETION;
   public static final ConfiguredFeature<?, ?> WARPED_FOREST_VEGETATION_BONEMEAL;
   public static final ConfiguredFeature<?, ?> NETHER_SPROUTS;
   public static final ConfiguredFeature<?, ?> NETHER_SPROUTS_BONEMEAL;
   public static final ConfiguredFeature<?, ?> TWISTING_VINES;
   public static final ConfiguredFeature<?, ?> TWISTING_VINES_BONEMEAL;
   public static final ConfiguredFeature<NoneFeatureConfiguration, ?> WEEPING_VINES;
   public static final ConfiguredFeature<RandomPatchConfiguration, ?> PATCH_CRIMSON_ROOTS;
   public static final ConfiguredFeature<NoneFeatureConfiguration, ?> BASALT_PILLAR;
   public static final ConfiguredFeature<SpringConfiguration, ?> SPRING_LAVA_NETHER;
   public static final ConfiguredFeature<SpringConfiguration, ?> SPRING_NETHER_CLOSED;
   public static final ConfiguredFeature<SpringConfiguration, ?> SPRING_NETHER_OPEN;
   public static final ConfiguredFeature<RandomPatchConfiguration, ?> PATCH_FIRE;
   public static final ConfiguredFeature<RandomPatchConfiguration, ?> PATCH_SOUL_FIRE;

   public NetherFeatures() {
      super();
   }

   static {
      DELTA = FeatureUtils.register("delta", Feature.DELTA_FEATURE.configured(new DeltaFeatureConfiguration(Blocks.LAVA.defaultBlockState(), Blocks.MAGMA_BLOCK.defaultBlockState(), UniformInt.method_45(3, 7), UniformInt.method_45(0, 2))));
      SMALL_BASALT_COLUMNS = FeatureUtils.register("small_basalt_columns", Feature.BASALT_COLUMNS.configured(new ColumnFeatureConfiguration(ConstantInt.method_49(1), UniformInt.method_45(1, 4))));
      LARGE_BASALT_COLUMNS = FeatureUtils.register("large_basalt_columns", Feature.BASALT_COLUMNS.configured(new ColumnFeatureConfiguration(UniformInt.method_45(2, 3), UniformInt.method_45(5, 10))));
      BASALT_BLOBS = FeatureUtils.register("basalt_blobs", Feature.REPLACE_BLOBS.configured(new ReplaceSphereConfiguration(Blocks.NETHERRACK.defaultBlockState(), Blocks.BASALT.defaultBlockState(), UniformInt.method_45(3, 7))));
      BLACKSTONE_BLOBS = FeatureUtils.register("blackstone_blobs", Feature.REPLACE_BLOBS.configured(new ReplaceSphereConfiguration(Blocks.NETHERRACK.defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState(), UniformInt.method_45(3, 7))));
      GLOWSTONE_EXTRA = FeatureUtils.register("glowstone_extra", Feature.GLOWSTONE_BLOB.configured(FeatureConfiguration.NONE));
      CRIMSON_VEGETATION_PROVIDER = new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 87).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 11).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 1));
      CRIMSON_FOREST_VEGETATION = FeatureUtils.register("crimson_forest_vegetation", Feature.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(CRIMSON_VEGETATION_PROVIDER, 8, 4)));
      CRIMSON_FOREST_VEGETATION_BONEMEAL = FeatureUtils.register("crimson_forest_vegetation_bonemeal", Feature.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(CRIMSON_VEGETATION_PROVIDER, 3, 1)));
      WARPED_VEGETATION_PROVIDER = new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.WARPED_ROOTS.defaultBlockState(), 85).add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 1).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 13).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1));
      WARPED_FOREST_VEGETION = FeatureUtils.register("warped_forest_vegetation", Feature.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(WARPED_VEGETATION_PROVIDER, 8, 4)));
      WARPED_FOREST_VEGETATION_BONEMEAL = FeatureUtils.register("warped_forest_vegetation_bonemeal", Feature.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(WARPED_VEGETATION_PROVIDER, 3, 1)));
      NETHER_SPROUTS = FeatureUtils.register("nether_sprouts", Feature.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(BlockStateProvider.simple(Blocks.NETHER_SPROUTS), 8, 4)));
      NETHER_SPROUTS_BONEMEAL = FeatureUtils.register("nether_sprouts_bonemeal", Feature.NETHER_FOREST_VEGETATION.configured(new NetherForestVegetationConfig(BlockStateProvider.simple(Blocks.NETHER_SPROUTS), 3, 1)));
      TWISTING_VINES = FeatureUtils.register("twisting_vines", Feature.TWISTING_VINES.configured(new TwistingVinesConfig(8, 4, 8)));
      TWISTING_VINES_BONEMEAL = FeatureUtils.register("twisting_vines_bonemeal", Feature.TWISTING_VINES.configured(new TwistingVinesConfig(3, 1, 2)));
      WEEPING_VINES = FeatureUtils.register("weeping_vines", Feature.WEEPING_VINES.configured(FeatureConfiguration.NONE));
      PATCH_CRIMSON_ROOTS = FeatureUtils.register("patch_crimson_roots", Feature.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.CRIMSON_ROOTS))))));
      BASALT_PILLAR = FeatureUtils.register("basalt_pillar", Feature.BASALT_PILLAR.configured(FeatureConfiguration.NONE));
      SPRING_LAVA_NETHER = FeatureUtils.register("spring_lava_nether", Feature.SPRING.configured(new SpringConfiguration(Fluids.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE))));
      SPRING_NETHER_CLOSED = FeatureUtils.register("spring_nether_closed", Feature.SPRING.configured(new SpringConfiguration(Fluids.LAVA.defaultFluidState(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK))));
      SPRING_NETHER_OPEN = FeatureUtils.register("spring_nether_open", Feature.SPRING.configured(new SpringConfiguration(Fluids.LAVA.defaultFluidState(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK))));
      PATCH_FIRE = FeatureUtils.register("patch_fire", Feature.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.FIRE))), List.of(Blocks.NETHERRACK))));
      PATCH_SOUL_FIRE = FeatureUtils.register("patch_soul_fire", Feature.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SOUL_FIRE))), List.of(Blocks.SOUL_SOIL))));
   }
}
