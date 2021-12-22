package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;

public class CaveFeatures {
   public static final ConfiguredFeature<NoneFeatureConfiguration, ?> MONSTER_ROOM;
   private static final List<ResourceLocation> FOSSIL_STRUCTURES;
   private static final List<ResourceLocation> FOSSIL_COAL_STRUCTURES;
   public static final ConfiguredFeature<FossilFeatureConfiguration, ?> FOSSIL_COAL;
   public static final ConfiguredFeature<FossilFeatureConfiguration, ?> FOSSIL_DIAMONDS;
   public static final ConfiguredFeature<DripstoneClusterConfiguration, ?> DRIPSTONE_CLUSTER;
   public static final ConfiguredFeature<LargeDripstoneConfiguration, ?> LARGE_DRIPSTONE;
   public static final ConfiguredFeature<SimpleRandomFeatureConfiguration, ?> POINTED_DRIPSTONE;
   public static final ConfiguredFeature<UnderwaterMagmaConfiguration, ?> UNDERWATER_MAGMA;
   public static final ConfiguredFeature<GlowLichenConfiguration, ?> GLOW_LICHEN;
   public static final ConfiguredFeature<RootSystemConfiguration, ?> ROOTED_AZALEA_TREE;
   private static final WeightedStateProvider CAVE_VINES_BODY_PROVIDER;
   private static final RandomizedIntStateProvider CAVE_VINES_HEAD_PROVIDER;
   public static final ConfiguredFeature<BlockColumnConfiguration, ?> CAVE_VINE;
   public static final ConfiguredFeature<BlockColumnConfiguration, ?> CAVE_VINE_IN_MOSS;
   public static final ConfiguredFeature<SimpleBlockConfiguration, ?> MOSS_VEGETATION;
   public static final ConfiguredFeature<VegetationPatchConfiguration, ?> MOSS_PATCH;
   public static final ConfiguredFeature<VegetationPatchConfiguration, ?> MOSS_PATCH_BONEMEAL;
   public static final ConfiguredFeature<SimpleRandomFeatureConfiguration, ?> DRIPLEAF;
   public static final ConfiguredFeature<?, ?> CLAY_WITH_DRIPLEAVES;
   public static final ConfiguredFeature<?, ?> CLAY_POOL_WITH_DRIPLEAVES;
   public static final ConfiguredFeature<RandomBooleanFeatureConfiguration, ?> LUSH_CAVES_CLAY;
   public static final ConfiguredFeature<VegetationPatchConfiguration, ?> MOSS_PATCH_CEILING;
   public static final ConfiguredFeature<SimpleBlockConfiguration, ?> SPORE_BLOSSOM;
   public static final ConfiguredFeature<GeodeConfiguration, ?> AMETHYST_GEODE;

   public CaveFeatures() {
      super();
   }

   private static PlacedFeature makeDripleaf(Direction var0) {
      return Feature.BLOCK_COLUMN.configured(new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(new WeightedListInt(SimpleWeightedRandomList.builder().add(UniformInt.method_45(0, 4), 2).add(ConstantInt.method_49(0), 1).build()), BlockStateProvider.simple((BlockState)Blocks.BIG_DRIPLEAF_STEM.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, var0))), BlockColumnConfiguration.layer(ConstantInt.method_49(1), BlockStateProvider.simple((BlockState)Blocks.BIG_DRIPLEAF.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, var0)))), Direction.field_526, BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE, true)).placed();
   }

   private static PlacedFeature makeSmallDripleaf() {
      return Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.builder().add((BlockState)Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, Direction.EAST), 1).add((BlockState)Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, Direction.WEST), 1).add((BlockState)Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, Direction.NORTH), 1).add((BlockState)Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, Direction.SOUTH), 1)))).placed();
   }

   static {
      MONSTER_ROOM = FeatureUtils.register("monster_room", Feature.MONSTER_ROOM.configured(FeatureConfiguration.NONE));
      FOSSIL_STRUCTURES = List.of(new ResourceLocation("fossil/spine_1"), new ResourceLocation("fossil/spine_2"), new ResourceLocation("fossil/spine_3"), new ResourceLocation("fossil/spine_4"), new ResourceLocation("fossil/skull_1"), new ResourceLocation("fossil/skull_2"), new ResourceLocation("fossil/skull_3"), new ResourceLocation("fossil/skull_4"));
      FOSSIL_COAL_STRUCTURES = List.of(new ResourceLocation("fossil/spine_1_coal"), new ResourceLocation("fossil/spine_2_coal"), new ResourceLocation("fossil/spine_3_coal"), new ResourceLocation("fossil/spine_4_coal"), new ResourceLocation("fossil/skull_1_coal"), new ResourceLocation("fossil/skull_2_coal"), new ResourceLocation("fossil/skull_3_coal"), new ResourceLocation("fossil/skull_4_coal"));
      FOSSIL_COAL = FeatureUtils.register("fossil_coal", Feature.FOSSIL.configured(new FossilFeatureConfiguration(FOSSIL_STRUCTURES, FOSSIL_COAL_STRUCTURES, ProcessorLists.FOSSIL_ROT, ProcessorLists.FOSSIL_COAL, 4)));
      FOSSIL_DIAMONDS = FeatureUtils.register("fossil_diamonds", Feature.FOSSIL.configured(new FossilFeatureConfiguration(FOSSIL_STRUCTURES, FOSSIL_COAL_STRUCTURES, ProcessorLists.FOSSIL_ROT, ProcessorLists.FOSSIL_DIAMONDS, 4)));
      DRIPSTONE_CLUSTER = FeatureUtils.register("dripstone_cluster", Feature.DRIPSTONE_CLUSTER.configured(new DripstoneClusterConfiguration(12, UniformInt.method_45(3, 6), UniformInt.method_45(2, 8), 1, 3, UniformInt.method_45(2, 4), UniformFloat.method_18(0.3F, 0.7F), ClampedNormalFloat.method_20(0.1F, 0.3F, 0.1F, 0.9F), 0.1F, 3, 8)));
      LARGE_DRIPSTONE = FeatureUtils.register("large_dripstone", Feature.LARGE_DRIPSTONE.configured(new LargeDripstoneConfiguration(30, UniformInt.method_45(3, 19), UniformFloat.method_18(0.4F, 2.0F), 0.33F, UniformFloat.method_18(0.3F, 0.9F), UniformFloat.method_18(0.4F, 1.0F), UniformFloat.method_18(0.0F, 0.3F), 4, 0.6F)));
      POINTED_DRIPSTONE = FeatureUtils.register("pointed_dripstone", Feature.SIMPLE_RANDOM_SELECTOR.configured(new SimpleRandomFeatureConfiguration(ImmutableList.of(() -> {
         return Feature.POINTED_DRIPSTONE.configured(new PointedDripstoneConfiguration(0.2F, 0.7F, 0.5F, 0.5F)).placed(EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(1)));
      }, () -> {
         return Feature.POINTED_DRIPSTONE.configured(new PointedDripstoneConfiguration(0.2F, 0.7F, 0.5F, 0.5F)).placed(EnvironmentScanPlacement.scanningFor(Direction.field_526, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(-1)));
      }))));
      UNDERWATER_MAGMA = FeatureUtils.register("underwater_magma", Feature.UNDERWATER_MAGMA.configured(new UnderwaterMagmaConfiguration(5, 1, 0.5F)));
      GLOW_LICHEN = FeatureUtils.register("glow_lichen", Feature.GLOW_LICHEN.configured(new GlowLichenConfiguration(20, false, true, true, 0.5F, List.of(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK, Blocks.CALCITE, Blocks.TUFF, Blocks.DEEPSLATE))));
      ROOTED_AZALEA_TREE = FeatureUtils.register("rooted_azalea_tree", Feature.ROOT_SYSTEM.configured(new RootSystemConfiguration(() -> {
         return TreeFeatures.AZALEA_TREE.placed();
      }, 3, 3, BlockTags.AZALEA_ROOT_REPLACEABLE.getName(), BlockStateProvider.simple(Blocks.ROOTED_DIRT), 20, 100, 3, 2, BlockStateProvider.simple(Blocks.HANGING_ROOTS), 20, 2, BlockPredicate.allOf(BlockPredicate.anyOf(BlockPredicate.matchesBlocks(List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR, Blocks.WATER)), BlockPredicate.matchesTag(BlockTags.LEAVES), BlockPredicate.matchesTag(BlockTags.REPLACEABLE_PLANTS)), BlockPredicate.matchesTag(BlockTags.AZALEA_GROWS_ON, Direction.DOWN.getNormal())))));
      CAVE_VINES_BODY_PROVIDER = new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.CAVE_VINES_PLANT.defaultBlockState(), 4).add((BlockState)Blocks.CAVE_VINES_PLANT.defaultBlockState().setValue(CaveVines.BERRIES, true), 1));
      CAVE_VINES_HEAD_PROVIDER = new RandomizedIntStateProvider(new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.CAVE_VINES.defaultBlockState(), 4).add((BlockState)Blocks.CAVE_VINES.defaultBlockState().setValue(CaveVines.BERRIES, true), 1)), CaveVinesBlock.AGE, UniformInt.method_45(23, 25));
      CAVE_VINE = FeatureUtils.register("cave_vine", Feature.BLOCK_COLUMN.configured(new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(new WeightedListInt(SimpleWeightedRandomList.builder().add(UniformInt.method_45(0, 19), 2).add(UniformInt.method_45(0, 2), 3).add(UniformInt.method_45(0, 6), 10).build()), CAVE_VINES_BODY_PROVIDER), BlockColumnConfiguration.layer(ConstantInt.method_49(1), CAVE_VINES_HEAD_PROVIDER)), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true)));
      CAVE_VINE_IN_MOSS = FeatureUtils.register("cave_vine_in_moss", Feature.BLOCK_COLUMN.configured(new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(new WeightedListInt(SimpleWeightedRandomList.builder().add(UniformInt.method_45(0, 3), 5).add(UniformInt.method_45(1, 7), 1).build()), CAVE_VINES_BODY_PROVIDER), BlockColumnConfiguration.layer(ConstantInt.method_49(1), CAVE_VINES_HEAD_PROVIDER)), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true)));
      MOSS_VEGETATION = FeatureUtils.register("moss_vegetation", Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.FLOWERING_AZALEA.defaultBlockState(), 4).add(Blocks.AZALEA.defaultBlockState(), 7).add(Blocks.MOSS_CARPET.defaultBlockState(), 25).add(Blocks.GRASS.defaultBlockState(), 50).add(Blocks.TALL_GRASS.defaultBlockState(), 10)))));
      MOSS_PATCH = FeatureUtils.register("moss_patch", Feature.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE.getName(), BlockStateProvider.simple(Blocks.MOSS_BLOCK), () -> {
         return MOSS_VEGETATION.placed();
      }, CaveSurface.FLOOR, ConstantInt.method_49(1), 0.0F, 5, 0.8F, UniformInt.method_45(4, 7), 0.3F)));
      MOSS_PATCH_BONEMEAL = FeatureUtils.register("moss_patch_bonemeal", Feature.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE.getName(), BlockStateProvider.simple(Blocks.MOSS_BLOCK), () -> {
         return MOSS_VEGETATION.placed();
      }, CaveSurface.FLOOR, ConstantInt.method_49(1), 0.0F, 5, 0.6F, UniformInt.method_45(1, 2), 0.75F)));
      DRIPLEAF = FeatureUtils.register("dripleaf", Feature.SIMPLE_RANDOM_SELECTOR.configured(new SimpleRandomFeatureConfiguration(List.of(CaveFeatures::makeSmallDripleaf, () -> {
         return makeDripleaf(Direction.EAST);
      }, () -> {
         return makeDripleaf(Direction.WEST);
      }, () -> {
         return makeDripleaf(Direction.SOUTH);
      }, () -> {
         return makeDripleaf(Direction.NORTH);
      }))));
      CLAY_WITH_DRIPLEAVES = FeatureUtils.register("clay_with_dripleaves", Feature.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(BlockTags.LUSH_GROUND_REPLACEABLE.getName(), BlockStateProvider.simple(Blocks.CLAY), () -> {
         return DRIPLEAF.placed();
      }, CaveSurface.FLOOR, ConstantInt.method_49(3), 0.8F, 2, 0.05F, UniformInt.method_45(4, 7), 0.7F)));
      CLAY_POOL_WITH_DRIPLEAVES = FeatureUtils.register("clay_pool_with_dripleaves", Feature.WATERLOGGED_VEGETATION_PATCH.configured(new VegetationPatchConfiguration(BlockTags.LUSH_GROUND_REPLACEABLE.getName(), BlockStateProvider.simple(Blocks.CLAY), () -> {
         return DRIPLEAF.placed();
      }, CaveSurface.FLOOR, ConstantInt.method_49(3), 0.8F, 5, 0.1F, UniformInt.method_45(4, 7), 0.7F)));
      LUSH_CAVES_CLAY = FeatureUtils.register("lush_caves_clay", Feature.RANDOM_BOOLEAN_SELECTOR.configured(new RandomBooleanFeatureConfiguration(() -> {
         return CLAY_WITH_DRIPLEAVES.placed();
      }, () -> {
         return CLAY_POOL_WITH_DRIPLEAVES.placed();
      })));
      MOSS_PATCH_CEILING = FeatureUtils.register("moss_patch_ceiling", Feature.VEGETATION_PATCH.configured(new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE.getName(), BlockStateProvider.simple(Blocks.MOSS_BLOCK), () -> {
         return CAVE_VINE_IN_MOSS.placed();
      }, CaveSurface.CEILING, UniformInt.method_45(1, 2), 0.0F, 5, 0.08F, UniformInt.method_45(4, 7), 0.3F)));
      SPORE_BLOSSOM = FeatureUtils.register("spore_blossom", Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SPORE_BLOSSOM))));
      AMETHYST_GEODE = FeatureUtils.register("amethyst_geode", Feature.GEODE.configured(new GeodeConfiguration(new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR), BlockStateProvider.simple(Blocks.AMETHYST_BLOCK), BlockStateProvider.simple(Blocks.BUDDING_AMETHYST), BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.SMOOTH_BASALT), List.of(Blocks.SMALL_AMETHYST_BUD.defaultBlockState(), Blocks.MEDIUM_AMETHYST_BUD.defaultBlockState(), Blocks.LARGE_AMETHYST_BUD.defaultBlockState(), Blocks.AMETHYST_CLUSTER.defaultBlockState()), BlockTags.FEATURES_CANNOT_REPLACE.getName(), BlockTags.GEODE_INVALID_BLOCKS.getName()), new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true, UniformInt.method_45(4, 6), UniformInt.method_45(3, 4), UniformInt.method_45(1, 2), -16, 16, 0.05D, 1)));
   }
}
