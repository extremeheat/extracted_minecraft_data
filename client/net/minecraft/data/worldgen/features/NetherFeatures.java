package net.minecraft.data.worldgen.features;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.Directional;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.TrapezoidInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.BlockColumnFeature;
import net.minecraft.world.level.levelgen.feature.DeltaFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoOpFeature;
import net.minecraft.world.level.levelgen.feature.OverlayFeature;
import net.minecraft.world.level.levelgen.feature.ProjectedRandomPatchySquare;
import net.minecraft.world.level.levelgen.feature.RandomNeighborSpreadFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlobsFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.SingleBlockPillarFeature;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.SteppedColumnClusterFeature;
import net.minecraft.world.level.levelgen.feature.WeightedRandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.material.Fluids;

public class NetherFeatures {
   public static final ResourceKey<Feature> DELTA = FeatureUtils.createKey("delta");
   public static final ResourceKey<Feature> SMALL_BASALT_COLUMNS = FeatureUtils.createKey("small_basalt_columns");
   public static final ResourceKey<Feature> LARGE_BASALT_COLUMNS = FeatureUtils.createKey("large_basalt_columns");
   public static final ResourceKey<Feature> BASALT_BLOBS = FeatureUtils.createKey("basalt_blobs");
   public static final ResourceKey<Feature> BLACKSTONE_BLOBS = FeatureUtils.createKey("blackstone_blobs");
   public static final ResourceKey<Feature> GLOWSTONE_EXTRA = FeatureUtils.createKey("glowstone_extra");
   public static final ResourceKey<Feature> NYLIUM_BONEMEAL = FeatureUtils.createKey("nylium_bonemeal");
   public static final ResourceKey<Feature> CRIMSON_FOREST_VEGETATION = FeatureUtils.createKey("crimson_forest_vegetation");
   public static final ResourceKey<Feature> WARPED_FOREST_VEGETION = FeatureUtils.createKey("warped_forest_vegetation");
   public static final ResourceKey<Feature> NETHER_SPROUTS = FeatureUtils.createKey("nether_sprouts");
   public static final ResourceKey<Feature> TWISTING_VINES = FeatureUtils.createKey("twisting_vines");
   public static final ResourceKey<Feature> WEEPING_VINES = FeatureUtils.createKey("weeping_vines");
   public static final ResourceKey<Feature> CRIMSON_ROOTS = FeatureUtils.createKey("crimson_roots");
   public static final ResourceKey<Feature> BASALT_PILLAR = FeatureUtils.createKey("basalt_pillar");
   public static final ResourceKey<Feature> SPRING_LAVA_NETHER = FeatureUtils.createKey("spring_lava_nether");
   public static final ResourceKey<Feature> SPRING_NETHER_CLOSED = FeatureUtils.createKey("spring_nether_closed");
   public static final ResourceKey<Feature> SPRING_NETHER_OPEN = FeatureUtils.createKey("spring_nether_open");
   public static final ResourceKey<Feature> FIRE = FeatureUtils.createKey("patch_fire");
   public static final ResourceKey<Feature> SOUL_FIRE = FeatureUtils.createKey("patch_soul_fire");

   public NetherFeatures() {
      super();
   }

   public static void bootstrap(final BootstrapContext<Feature> context) {
      HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
      context.register(DELTA, new DeltaFeature(Blocks.LAVA.defaultBlockState(), Blocks.MAGMA_BLOCK.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2)));
      BlockPredicate replacedByBasaltColumns = BlockPredicate.anyOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.LAVA), BlockPredicate.heightRange(VerticalAnchor.bottom(), VerticalAnchor.seaLevel())));
      context.register(SMALL_BASALT_COLUMNS, new WeightedRandomSelectorFeature(WeightedList.of(new Weighted(PlacementUtils.inlinePlaced(new SteppedColumnClusterFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.matchesBlocks(Blocks.BASALT), replacedByBasaltColumns, blocks.getOrThrow(BlockTags.CANNOT_PLACE_BASALT_PILLAR_ON), ConstantInt.of(5), ConstantInt.of(50), ConstantInt.of(1), UniformInt.of(1, 4))), 9), new Weighted(PlacementUtils.inlinePlaced(new SteppedColumnClusterFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.matchesBlocks(Blocks.BASALT), replacedByBasaltColumns, blocks.getOrThrow(BlockTags.CANNOT_PLACE_BASALT_PILLAR_ON), ConstantInt.of(8), ConstantInt.of(15), ConstantInt.of(1), UniformInt.of(1, 4))), 1))));
      context.register(LARGE_BASALT_COLUMNS, new WeightedRandomSelectorFeature(WeightedList.of(new Weighted(PlacementUtils.inlinePlaced(new SteppedColumnClusterFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.matchesBlocks(Blocks.BASALT), replacedByBasaltColumns, blocks.getOrThrow(BlockTags.CANNOT_PLACE_BASALT_PILLAR_ON), ConstantInt.of(5), ConstantInt.of(50), UniformInt.of(2, 3), UniformInt.of(5, 10))), 9), new Weighted(PlacementUtils.inlinePlaced(new SteppedColumnClusterFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.matchesBlocks(Blocks.BASALT), replacedByBasaltColumns, blocks.getOrThrow(BlockTags.CANNOT_PLACE_BASALT_PILLAR_ON), ConstantInt.of(8), ConstantInt.of(15), UniformInt.of(2, 3), UniformInt.of(5, 10))), 1))));
      context.register(BASALT_BLOBS, new ReplaceBlobsFeature(Blocks.NETHERRACK.defaultBlockState(), Blocks.BASALT.defaultBlockState(), UniformInt.of(3, 7)));
      context.register(BLACKSTONE_BLOBS, new ReplaceBlobsFeature(Blocks.NETHERRACK.defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState(), UniformInt.of(3, 7)));
      context.register(GLOWSTONE_EXTRA, new RandomNeighborSpreadFeature(BlockStateProvider.simple(Blocks.GLOWSTONE), HolderSet.direct(blocks.getOrThrow(BlockItemIds.GLOWSTONE.block())), BlockPredicate.ONLY_IN_AIR_PREDICATE, ConstantInt.of(1500), TrapezoidInt.triangle(7), UniformInt.of(-11, 0)));
      PlacementModifier[] netherForestBonemealSpread = new PlacementModifier[]{CountPlacement.of(9), OffsetPlacement.ofTriangle(2, 0), BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)};
      SimpleBlockFeature crimsonVegetation = new SimpleBlockFeature(new WeightedStateProvider(WeightedList.builder().add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 87).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 11).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 1)));
      context.register(CRIMSON_FOREST_VEGETATION, crimsonVegetation);
      SimpleBlockFeature warpedVegetation = new SimpleBlockFeature(new WeightedStateProvider(WeightedList.builder().add(Blocks.WARPED_ROOTS.defaultBlockState(), 85).add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 1).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 13).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1)));
      context.register(WARPED_FOREST_VEGETION, warpedVegetation);
      SimpleBlockFeature netherSprouts = new SimpleBlockFeature(BlockStateProvider.simple(Blocks.NETHER_SPROUTS));
      context.register(NETHER_SPROUTS, netherSprouts);
      Feature crimsonNyliumBonemeal = new OverlayFeature(HolderSet.direct(PlacementUtils.inlinePlaced(crimsonVegetation, netherForestBonemealSpread)));
      Feature warpedNyliumBonemeal = new OverlayFeature(HolderSet.direct(PlacementUtils.inlinePlaced(warpedVegetation, netherForestBonemealSpread), PlacementUtils.inlinePlaced(netherSprouts, netherForestBonemealSpread), PlacementUtils.inlinePlaced(new WeightedRandomSelectorFeature(WeightedList.builder().add(PlacementUtils.inlinePlaced(createVines(1, 2, Direction.UP, Blocks.TWISTING_VINES_PLANT, Blocks.TWISTING_VINES), NetherPlacements.spreadTwistingVines(3, 1)), 1).add(PlacementUtils.inlinePlaced(new NoOpFeature()), 7).build()))));
      context.register(NYLIUM_BONEMEAL, new OverlayFeature(HolderSet.direct(PlacementUtils.inlinePlaced(crimsonNyliumBonemeal, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks((Directional)Direction.DOWN, Blocks.CRIMSON_NYLIUM))), PlacementUtils.inlinePlaced(warpedNyliumBonemeal, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks((Directional)Direction.DOWN, Blocks.WARPED_NYLIUM))))));
      context.register(TWISTING_VINES, createVines(1, 8, Direction.UP, Blocks.TWISTING_VINES_PLANT, Blocks.TWISTING_VINES));
      context.register(WEEPING_VINES, new OverlayFeature(HolderSet.direct(PlacementUtils.inlinePlaced(new RandomNeighborSpreadFeature(BlockStateProvider.simple(Blocks.NETHER_WART_BLOCK.defaultBlockState()), HolderSet.direct(blocks.getOrThrow(BlockItemIds.NETHERRACK.block()), blocks.getOrThrow(BlockItemIds.NETHER_WART_BLOCK.block())), BlockPredicate.ONLY_IN_AIR_PREDICATE, ConstantInt.of(200), TrapezoidInt.triangle(5), TrapezoidInt.of(-4, 1, 3))), PlacementUtils.inlinePlaced(createVines(2, 9, Direction.DOWN, Blocks.WEEPING_VINES_PLANT, Blocks.WEEPING_VINES), CountPlacement.of(100), OffsetPlacement.of(TrapezoidInt.triangle(7), TrapezoidInt.of(-4, 1, 3)), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks((Directional)Direction.UP, Blocks.NETHERRACK, Blocks.NETHER_WART_BLOCK)))))));
      context.register(CRIMSON_ROOTS, new SimpleBlockFeature(BlockStateProvider.simple(Blocks.CRIMSON_ROOTS)));
      context.register(BASALT_PILLAR, new OverlayFeature(HolderSet.direct(PlacementUtils.inlinePlaced(new SingleBlockPillarFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.ONLY_IN_AIR_PREDICATE, Direction.DOWN, 1.0F, Optional.of(PlacementUtils.inlinePlaced(new OverlayFeature(HolderSet.direct(PlacementUtils.inlinePlaced(new ProjectedRandomPatchySquare(RuleBasedStateProvider.ifTrueThenProvide(BlockPredicate.not(BlockPredicate.matchesTag((Directional)Direction.DOWN, BlockTags.AIR)), Blocks.BASALT), BlockPredicate.ONLY_IN_AIR_PREDICATE, ConstantInt.of(3), 3), OffsetPlacement.of(0, -1, 0)), PlacementUtils.inlinePlaced(new SimpleBlockFeature(BlockStateProvider.simple(Blocks.BASALT)), RarityFilter.onAverageOnceEvery(2), OffsetPlacement.of(1, 0, 0)), PlacementUtils.inlinePlaced(new SimpleBlockFeature(BlockStateProvider.simple(Blocks.BASALT)), RarityFilter.onAverageOnceEvery(2), OffsetPlacement.of(-1, 0, 0)), PlacementUtils.inlinePlaced(new SimpleBlockFeature(BlockStateProvider.simple(Blocks.BASALT)), RarityFilter.onAverageOnceEvery(2), OffsetPlacement.of(0, 0, 1)), PlacementUtils.inlinePlaced(new SimpleBlockFeature(BlockStateProvider.simple(Blocks.BASALT)), RarityFilter.onAverageOnceEvery(2), OffsetPlacement.of(0, 0, -1)))))))), PlacementUtils.inlinePlaced(new SingleBlockPillarFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.ONLY_IN_AIR_PREDICATE, Direction.DOWN, 0.9F), OffsetPlacement.of(1, 0, 0)), PlacementUtils.inlinePlaced(new SingleBlockPillarFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.ONLY_IN_AIR_PREDICATE, Direction.DOWN, 0.9F), OffsetPlacement.of(-1, 0, 0)), PlacementUtils.inlinePlaced(new SingleBlockPillarFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.ONLY_IN_AIR_PREDICATE, Direction.DOWN, 0.9F), OffsetPlacement.of(0, 0, 1)), PlacementUtils.inlinePlaced(new SingleBlockPillarFeature(BlockStateProvider.simple(Blocks.BASALT), BlockPredicate.ONLY_IN_AIR_PREDICATE, Direction.DOWN, 0.9F), OffsetPlacement.of(0, 0, -1)))));
      context.register(SPRING_LAVA_NETHER, new SpringFeature(Fluids.LAVA.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE)));
      context.register(SPRING_NETHER_CLOSED, new SpringFeature(Fluids.LAVA.defaultFluidState(), false, 5, 0, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK)));
      context.register(SPRING_NETHER_OPEN, new SpringFeature(Fluids.LAVA.defaultFluidState(), false, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK)));
      context.register(FIRE, new SimpleBlockFeature(BlockStateProvider.simple(Blocks.FIRE)));
      context.register(SOUL_FIRE, new SimpleBlockFeature(BlockStateProvider.simple(Blocks.SOUL_FIRE)));
   }

   private static Feature createVines(final int minHeight, final int maxHeight, final Direction direction, final Block mainBlock, final Block tipBlock) {
      IntProvider mainHeight = new WeightedListInt(WeightedList.builder().add(UniformInt.of(minHeight - 1, maxHeight - 1), 10).add(UniformInt.of(minHeight, maxHeight * 2 - 1), 2).add(ConstantInt.of(minHeight - 1), 3).build());
      return new BlockColumnFeature(List.of(BlockColumnFeature.layer(mainHeight, BlockStateProvider.simple(mainBlock)), BlockColumnFeature.layer(ConstantInt.of(1), new RandomizedIntStateProvider(BlockStateProvider.simple(tipBlock), GrowingPlantHeadBlock.AGE, UniformInt.of(17, 25)))), direction, BlockPredicate.ONLY_IN_AIR_PREDICATE, true);
   }
}
