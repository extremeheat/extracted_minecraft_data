package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class PlacementUtils {
   public static final PlacementModifier HEIGHTMAP;
   public static final PlacementModifier HEIGHTMAP_TOP_SOLID;
   public static final PlacementModifier HEIGHTMAP_WORLD_SURFACE;
   public static final PlacementModifier HEIGHTMAP_OCEAN_FLOOR;
   public static final PlacementModifier FULL_RANGE;
   public static final PlacementModifier RANGE_10_10;
   public static final PlacementModifier RANGE_8_8;
   public static final PlacementModifier RANGE_4_4;
   public static final PlacementModifier RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT;

   public PlacementUtils() {
      super();
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      AquaticPlacements.bootstrap(var0);
      CavePlacements.bootstrap(var0);
      EndPlacements.bootstrap(var0);
      MiscOverworldPlacements.bootstrap(var0);
      NetherPlacements.bootstrap(var0);
      OrePlacements.bootstrap(var0);
      TreePlacements.bootstrap(var0);
      VegetationPlacements.bootstrap(var0);
      VillagePlacements.bootstrap(var0);
   }

   public static ResourceKey<PlacedFeature> createKey(String var0) {
      return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.withDefaultNamespace(var0));
   }

   public static void register(BootstrapContext<PlacedFeature> var0, ResourceKey<PlacedFeature> var1, Holder<ConfiguredFeature<?, ?>> var2, List<PlacementModifier> var3) {
      var0.register(var1, new PlacedFeature(var2, List.copyOf(var3)));
   }

   public static void register(BootstrapContext<PlacedFeature> var0, ResourceKey<PlacedFeature> var1, Holder<ConfiguredFeature<?, ?>> var2, PlacementModifier... var3) {
      register(var0, var1, var2, List.of(var3));
   }

   public static PlacementModifier countExtra(int var0, float var1, int var2) {
      float var3 = 1.0F / var1;
      if (Math.abs(var3 - (float)((int)var3)) > 1.0E-5F) {
         throw new IllegalStateException("Chance data cannot be represented as list weight");
      } else {
         SimpleWeightedRandomList var4 = SimpleWeightedRandomList.builder().add(ConstantInt.of(var0), (int)var3 - 1).add(ConstantInt.of(var0 + var2), 1).build();
         return CountPlacement.of(new WeightedListInt(var4));
      }
   }

   public static PlacementFilter isEmpty() {
      return BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
   }

   public static BlockPredicateFilter filteredByBlockSurvival(Block var0) {
      return BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(var0.defaultBlockState(), BlockPos.ZERO));
   }

   public static Holder<PlacedFeature> inlinePlaced(Holder<ConfiguredFeature<?, ?>> var0, PlacementModifier... var1) {
      return Holder.direct(new PlacedFeature(var0, List.of(var1)));
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> inlinePlaced(F var0, FC var1, PlacementModifier... var2) {
      return inlinePlaced(Holder.direct(new ConfiguredFeature(var0, var1)), var2);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> onlyWhenEmpty(F var0, FC var1) {
      return filtered(var0, var1, BlockPredicate.ONLY_IN_AIR_PREDICATE);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> filtered(F var0, FC var1, BlockPredicate var2) {
      return inlinePlaced(var0, var1, BlockPredicateFilter.forPredicate(var2));
   }

   static {
      HEIGHTMAP = HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING);
      HEIGHTMAP_TOP_SOLID = HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG);
      HEIGHTMAP_WORLD_SURFACE = HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG);
      HEIGHTMAP_OCEAN_FLOOR = HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR);
      FULL_RANGE = HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top());
      RANGE_10_10 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(10), VerticalAnchor.belowTop(10));
      RANGE_8_8 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(8));
      RANGE_4_4 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(4), VerticalAnchor.belowTop(4));
      RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT = HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(256));
   }
}
