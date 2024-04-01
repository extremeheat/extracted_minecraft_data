package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class TreePlacements {
   public static final ResourceKey<PlacedFeature> CRIMSON_FUNGI = PlacementUtils.createKey("crimson_fungi");
   public static final ResourceKey<PlacedFeature> WARPED_FUNGI = PlacementUtils.createKey("warped_fungi");
   public static final ResourceKey<PlacedFeature> POTATO_TREE = PlacementUtils.createKey("potato_tree");
   public static final ResourceKey<PlacedFeature> POTATO_TREE_TALL = PlacementUtils.createKey("potato_tree_tall");
   public static final ResourceKey<PlacedFeature> MOTHER_POTATO_TREE = PlacementUtils.createKey("mother_potato_tree");
   public static final ResourceKey<PlacedFeature> POTATO_CHECKED = PlacementUtils.createKey("potato_checked");
   public static final ResourceKey<PlacedFeature> MOTHER_POTATO_CHECKED = PlacementUtils.createKey("mother_potato_checked");
   public static final ResourceKey<PlacedFeature> OAK_CHECKED = PlacementUtils.createKey("oak_checked");
   public static final ResourceKey<PlacedFeature> DARK_OAK_CHECKED = PlacementUtils.createKey("dark_oak_checked");
   public static final ResourceKey<PlacedFeature> BIRCH_CHECKED = PlacementUtils.createKey("birch_checked");
   public static final ResourceKey<PlacedFeature> ACACIA_CHECKED = PlacementUtils.createKey("acacia_checked");
   public static final ResourceKey<PlacedFeature> SPRUCE_CHECKED = PlacementUtils.createKey("spruce_checked");
   public static final ResourceKey<PlacedFeature> MANGROVE_CHECKED = PlacementUtils.createKey("mangrove_checked");
   public static final ResourceKey<PlacedFeature> CHERRY_CHECKED = PlacementUtils.createKey("cherry_checked");
   public static final ResourceKey<PlacedFeature> PINE_ON_SNOW = PlacementUtils.createKey("pine_on_snow");
   public static final ResourceKey<PlacedFeature> SPRUCE_ON_SNOW = PlacementUtils.createKey("spruce_on_snow");
   public static final ResourceKey<PlacedFeature> PINE_CHECKED = PlacementUtils.createKey("pine_checked");
   public static final ResourceKey<PlacedFeature> JUNGLE_TREE_CHECKED = PlacementUtils.createKey("jungle_tree");
   public static final ResourceKey<PlacedFeature> FANCY_OAK_CHECKED = PlacementUtils.createKey("fancy_oak_checked");
   public static final ResourceKey<PlacedFeature> MEGA_JUNGLE_TREE_CHECKED = PlacementUtils.createKey("mega_jungle_tree_checked");
   public static final ResourceKey<PlacedFeature> MEGA_SPRUCE_CHECKED = PlacementUtils.createKey("mega_spruce_checked");
   public static final ResourceKey<PlacedFeature> MEGA_PINE_CHECKED = PlacementUtils.createKey("mega_pine_checked");
   public static final ResourceKey<PlacedFeature> TALL_MANGROVE_CHECKED = PlacementUtils.createKey("tall_mangrove_checked");
   public static final ResourceKey<PlacedFeature> JUNGLE_BUSH = PlacementUtils.createKey("jungle_bush");
   public static final ResourceKey<PlacedFeature> SUPER_BIRCH_BEES_0002 = PlacementUtils.createKey("super_birch_bees_0002");
   public static final ResourceKey<PlacedFeature> SUPER_BIRCH_BEES = PlacementUtils.createKey("super_birch_bees");
   public static final ResourceKey<PlacedFeature> OAK_BEES_0002 = PlacementUtils.createKey("oak_bees_0002");
   public static final ResourceKey<PlacedFeature> OAK_BEES_002 = PlacementUtils.createKey("oak_bees_002");
   public static final ResourceKey<PlacedFeature> BIRCH_BEES_0002_PLACED = PlacementUtils.createKey("birch_bees_0002");
   public static final ResourceKey<PlacedFeature> BIRCH_BEES_002 = PlacementUtils.createKey("birch_bees_002");
   public static final ResourceKey<PlacedFeature> FANCY_OAK_BEES_0002 = PlacementUtils.createKey("fancy_oak_bees_0002");
   public static final ResourceKey<PlacedFeature> FANCY_OAK_BEES_002 = PlacementUtils.createKey("fancy_oak_bees_002");
   public static final ResourceKey<PlacedFeature> FANCY_OAK_BEES = PlacementUtils.createKey("fancy_oak_bees");
   public static final ResourceKey<PlacedFeature> CHERRY_BEES_005 = PlacementUtils.createKey("cherry_bees_005");

   public TreePlacements() {
      super();
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(TreeFeatures.CRIMSON_FUNGUS);
      Holder.Reference var3 = var1.getOrThrow(TreeFeatures.WARPED_FUNGUS);
      Holder.Reference var4 = var1.getOrThrow(TreeFeatures.POTATO_TREE_TALL);
      Holder.Reference var5 = var1.getOrThrow(TreeFeatures.MOTHER_POTATO_TREE);
      Holder.Reference var6 = var1.getOrThrow(TreeFeatures.POTATO_TREE);
      Holder.Reference var7 = var1.getOrThrow(TreeFeatures.OAK);
      Holder.Reference var8 = var1.getOrThrow(TreeFeatures.DARK_OAK);
      Holder.Reference var9 = var1.getOrThrow(TreeFeatures.BIRCH);
      Holder.Reference var10 = var1.getOrThrow(TreeFeatures.ACACIA);
      Holder.Reference var11 = var1.getOrThrow(TreeFeatures.SPRUCE);
      Holder.Reference var12 = var1.getOrThrow(TreeFeatures.MANGROVE);
      Holder.Reference var13 = var1.getOrThrow(TreeFeatures.CHERRY);
      Holder.Reference var14 = var1.getOrThrow(TreeFeatures.PINE);
      Holder.Reference var15 = var1.getOrThrow(TreeFeatures.JUNGLE_TREE);
      Holder.Reference var16 = var1.getOrThrow(TreeFeatures.FANCY_OAK);
      Holder.Reference var17 = var1.getOrThrow(TreeFeatures.MEGA_JUNGLE_TREE);
      Holder.Reference var18 = var1.getOrThrow(TreeFeatures.MEGA_SPRUCE);
      Holder.Reference var19 = var1.getOrThrow(TreeFeatures.MEGA_PINE);
      Holder.Reference var20 = var1.getOrThrow(TreeFeatures.TALL_MANGROVE);
      Holder.Reference var21 = var1.getOrThrow(TreeFeatures.JUNGLE_BUSH);
      Holder.Reference var22 = var1.getOrThrow(TreeFeatures.SUPER_BIRCH_BEES_0002);
      Holder.Reference var23 = var1.getOrThrow(TreeFeatures.SUPER_BIRCH_BEES);
      Holder.Reference var24 = var1.getOrThrow(TreeFeatures.OAK_BEES_0002);
      Holder.Reference var25 = var1.getOrThrow(TreeFeatures.OAK_BEES_002);
      Holder.Reference var26 = var1.getOrThrow(TreeFeatures.BIRCH_BEES_0002);
      Holder.Reference var27 = var1.getOrThrow(TreeFeatures.BIRCH_BEES_002);
      Holder.Reference var28 = var1.getOrThrow(TreeFeatures.FANCY_OAK_BEES_0002);
      Holder.Reference var29 = var1.getOrThrow(TreeFeatures.FANCY_OAK_BEES_002);
      Holder.Reference var30 = var1.getOrThrow(TreeFeatures.FANCY_OAK_BEES);
      Holder.Reference var31 = var1.getOrThrow(TreeFeatures.CHERRY_BEES_005);
      PlacementUtils.register(var0, CRIMSON_FUNGI, var2, CountOnEveryLayerPlacement.of(8), BiomeFilter.biome());
      PlacementUtils.register(var0, WARPED_FUNGI, var3, CountOnEveryLayerPlacement.of(8), BiomeFilter.biome());
      PlacementUtils.register(
         var0, POTATO_TREE, var6, CountOnEveryLayerPlacement.of(6), BiomeFilter.biome(), PlacementUtils.filteredByBlockSurvival(Blocks.POTATO_SPROUTS)
      );
      PlacementUtils.register(
         var0,
         POTATO_TREE_TALL,
         var4,
         RarityFilter.onAverageOnceEvery(6),
         CountOnEveryLayerPlacement.of(1),
         BiomeFilter.biome(),
         PlacementUtils.filteredByBlockSurvival(Blocks.POTATO_SPROUTS)
      );
      PlacementUtils.register(
         var0,
         MOTHER_POTATO_TREE,
         var5,
         RarityFilter.onAverageOnceEvery(48),
         CountOnEveryLayerPlacement.of(1),
         BiomeFilter.biome(),
         PlacementUtils.filteredByBlockSurvival(Blocks.POTATO_SPROUTS)
      );
      PlacementUtils.register(var0, POTATO_CHECKED, var6, PlacementUtils.filteredByBlockSurvival(Blocks.POTATO_SPROUTS));
      PlacementUtils.register(var0, MOTHER_POTATO_CHECKED, var5, PlacementUtils.filteredByBlockSurvival(Blocks.POTATO_SPROUTS));
      PlacementUtils.register(var0, OAK_CHECKED, var7, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, DARK_OAK_CHECKED, var8, PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
      PlacementUtils.register(var0, BIRCH_CHECKED, var9, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      PlacementUtils.register(var0, ACACIA_CHECKED, var10, PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
      PlacementUtils.register(var0, SPRUCE_CHECKED, var11, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(var0, MANGROVE_CHECKED, var12, PlacementUtils.filteredByBlockSurvival(Blocks.MANGROVE_PROPAGULE));
      PlacementUtils.register(var0, CHERRY_CHECKED, var13, PlacementUtils.filteredByBlockSurvival(Blocks.CHERRY_SAPLING));
      BlockPredicate var32 = BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW);
      List var33 = List.of(
         EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.not(BlockPredicate.matchesBlocks(Blocks.POWDER_SNOW)), 8),
         BlockPredicateFilter.forPredicate(var32)
      );
      PlacementUtils.register(var0, PINE_ON_SNOW, var14, var33);
      PlacementUtils.register(var0, SPRUCE_ON_SNOW, var11, var33);
      PlacementUtils.register(var0, PINE_CHECKED, var14, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(var0, JUNGLE_TREE_CHECKED, var15, PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
      PlacementUtils.register(var0, FANCY_OAK_CHECKED, var16, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, MEGA_JUNGLE_TREE_CHECKED, var17, PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
      PlacementUtils.register(var0, MEGA_SPRUCE_CHECKED, var18, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(var0, MEGA_PINE_CHECKED, var19, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(var0, TALL_MANGROVE_CHECKED, var20, PlacementUtils.filteredByBlockSurvival(Blocks.MANGROVE_PROPAGULE));
      PlacementUtils.register(var0, JUNGLE_BUSH, var21, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, SUPER_BIRCH_BEES_0002, var22, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      PlacementUtils.register(var0, SUPER_BIRCH_BEES, var23, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      PlacementUtils.register(var0, OAK_BEES_0002, var24, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, OAK_BEES_002, var25, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, BIRCH_BEES_0002_PLACED, var26, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      PlacementUtils.register(var0, BIRCH_BEES_002, var27, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
      PlacementUtils.register(var0, FANCY_OAK_BEES_0002, var28, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, FANCY_OAK_BEES_002, var29, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, FANCY_OAK_BEES, var30, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, CHERRY_BEES_005, var31, PlacementUtils.filteredByBlockSurvival(Blocks.CHERRY_SAPLING));
   }
}
