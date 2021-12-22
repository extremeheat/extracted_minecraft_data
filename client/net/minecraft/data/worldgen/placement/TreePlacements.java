package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class TreePlacements {
   public static final PlacedFeature CRIMSON_FUNGI;
   public static final PlacedFeature WARPED_FUNGI;
   public static final PlacedFeature OAK_CHECKED;
   public static final PlacedFeature DARK_OAK_CHECKED;
   public static final PlacedFeature BIRCH_CHECKED;
   public static final PlacedFeature ACACIA_CHECKED;
   public static final PlacedFeature SPRUCE_CHECKED;
   public static final BlockPredicate SNOW_TREE_PREDICATE;
   public static final List<PlacementModifier> SNOW_TREE_FILTER_DECORATOR;
   public static final PlacedFeature PINE_ON_SNOW;
   public static final PlacedFeature SPRUCE_ON_SNOW;
   public static final PlacedFeature PINE_CHECKED;
   public static final PlacedFeature JUNGLE_TREE_CHECKED;
   public static final PlacedFeature FANCY_OAK_CHECKED;
   public static final PlacedFeature MEGA_JUNGLE_TREE_CHECKED;
   public static final PlacedFeature MEGA_SPRUCE_CHECKED;
   public static final PlacedFeature MEGA_PINE_CHECKED;
   public static final PlacedFeature JUNGLE_BUSH;
   public static final PlacedFeature SUPER_BIRCH_BEES_0002;
   public static final PlacedFeature SUPER_BIRCH_BEES;
   public static final PlacedFeature OAK_BEES_0002;
   public static final PlacedFeature OAK_BEES_002;
   public static final PlacedFeature BIRCH_BEES_0002_PLACED;
   public static final PlacedFeature BIRCH_BEES_002;
   public static final PlacedFeature FANCY_OAK_BEES_0002;
   public static final PlacedFeature FANCY_OAK_BEES_002;
   public static final PlacedFeature FANCY_OAK_BEES;

   public TreePlacements() {
      super();
   }

   static {
      CRIMSON_FUNGI = PlacementUtils.register("crimson_fungi", TreeFeatures.CRIMSON_FUNGUS.placed(CountOnEveryLayerPlacement.method_34(8), BiomeFilter.biome()));
      WARPED_FUNGI = PlacementUtils.register("warped_fungi", TreeFeatures.WARPED_FUNGUS.placed(CountOnEveryLayerPlacement.method_34(8), BiomeFilter.biome()));
      OAK_CHECKED = PlacementUtils.register("oak_checked", TreeFeatures.OAK.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      DARK_OAK_CHECKED = PlacementUtils.register("dark_oak_checked", TreeFeatures.DARK_OAK.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
      BIRCH_CHECKED = PlacementUtils.register("birch_checked", TreeFeatures.BIRCH.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      ACACIA_CHECKED = PlacementUtils.register("acacia_checked", TreeFeatures.ACACIA.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
      SPRUCE_CHECKED = PlacementUtils.register("spruce_checked", TreeFeatures.SPRUCE.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      SNOW_TREE_PREDICATE = BlockPredicate.matchesBlocks(List.of(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW), new BlockPos(0, -1, 0));
      SNOW_TREE_FILTER_DECORATOR = List.of(EnvironmentScanPlacement.scanningFor(Direction.field_526, BlockPredicate.not(BlockPredicate.matchesBlock(Blocks.POWDER_SNOW, BlockPos.ZERO)), 8), BlockPredicateFilter.forPredicate(SNOW_TREE_PREDICATE));
      PINE_ON_SNOW = PlacementUtils.register("pine_on_snow", TreeFeatures.PINE.placed(SNOW_TREE_FILTER_DECORATOR));
      SPRUCE_ON_SNOW = PlacementUtils.register("spruce_on_snow", TreeFeatures.SPRUCE.placed(SNOW_TREE_FILTER_DECORATOR));
      PINE_CHECKED = PlacementUtils.register("pine_checked", TreeFeatures.PINE.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      JUNGLE_TREE_CHECKED = PlacementUtils.register("jungle_tree", TreeFeatures.JUNGLE_TREE.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
      FANCY_OAK_CHECKED = PlacementUtils.register("fancy_oak_checked", TreeFeatures.FANCY_OAK.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      MEGA_JUNGLE_TREE_CHECKED = PlacementUtils.register("mega_jungle_tree_checked", TreeFeatures.MEGA_JUNGLE_TREE.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
      MEGA_SPRUCE_CHECKED = PlacementUtils.register("mega_spruce_checked", TreeFeatures.MEGA_SPRUCE.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      MEGA_PINE_CHECKED = PlacementUtils.register("mega_pine_checked", TreeFeatures.MEGA_PINE.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      JUNGLE_BUSH = PlacementUtils.register("jungle_bush", TreeFeatures.JUNGLE_BUSH.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      SUPER_BIRCH_BEES_0002 = PlacementUtils.register("super_birch_bees_0002", TreeFeatures.SUPER_BIRCH_BEES_0002.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      SUPER_BIRCH_BEES = PlacementUtils.register("super_birch_bees", TreeFeatures.SUPER_BIRCH_BEES.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      OAK_BEES_0002 = PlacementUtils.register("oak_bees_0002", TreeFeatures.OAK_BEES_0002.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      OAK_BEES_002 = PlacementUtils.register("oak_bees_002", TreeFeatures.OAK_BEES_002.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      BIRCH_BEES_0002_PLACED = PlacementUtils.register("birch_bees_0002", TreeFeatures.BIRCH_BEES_0002.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      BIRCH_BEES_002 = PlacementUtils.register("birch_bees_002", TreeFeatures.BIRCH_BEES_002.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      FANCY_OAK_BEES_0002 = PlacementUtils.register("fancy_oak_bees_0002", TreeFeatures.FANCY_OAK_BEES_0002.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      FANCY_OAK_BEES_002 = PlacementUtils.register("fancy_oak_bees_002", TreeFeatures.FANCY_OAK_BEES_002.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      FANCY_OAK_BEES = PlacementUtils.register("fancy_oak_bees", TreeFeatures.FANCY_OAK_BEES.filteredByBlockSurvival(Blocks.OAK_SAPLING));
   }
}
