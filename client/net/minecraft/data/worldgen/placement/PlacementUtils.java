package net.minecraft.data.worldgen.placement;

import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
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

   public static PlacedFeature bootstrap() {
      PlacedFeature[] var0 = new PlacedFeature[]{AquaticPlacements.KELP_COLD, CavePlacements.CAVE_VINES, EndPlacements.CHORUS_PLANT, MiscOverworldPlacements.BLUE_ICE, NetherPlacements.BASALT_BLOBS, OrePlacements.ORE_ANCIENT_DEBRIS_LARGE, TreePlacements.ACACIA_CHECKED, VegetationPlacements.BAMBOO_VEGETATION, VillagePlacements.PILE_HAY_VILLAGE};
      return (PlacedFeature)Util.getRandom((Object[])var0, new Random());
   }

   public static PlacedFeature register(String var0, PlacedFeature var1) {
      return (PlacedFeature)Registry.register(BuiltinRegistries.PLACED_FEATURE, (String)var0, var1);
   }

   public static PlacementModifier countExtra(int var0, float var1, int var2) {
      float var3 = 1.0F / var1;
      if (Math.abs(var3 - (float)((int)var3)) > 1.0E-5F) {
         throw new IllegalStateException("Chance data cannot be represented as list weight");
      } else {
         SimpleWeightedRandomList var4 = SimpleWeightedRandomList.builder().add(ConstantInt.method_49(var0), (int)var3 - 1).add(ConstantInt.method_49(var0 + var2), 1).build();
         return CountPlacement.method_38(new WeightedListInt(var4));
      }
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
