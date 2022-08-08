package net.minecraft.data.worldgen.placement;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.PileFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VillagePlacements {
   public static final Holder<PlacedFeature> PILE_HAY_VILLAGE;
   public static final Holder<PlacedFeature> PILE_MELON_VILLAGE;
   public static final Holder<PlacedFeature> PILE_SNOW_VILLAGE;
   public static final Holder<PlacedFeature> PILE_ICE_VILLAGE;
   public static final Holder<PlacedFeature> PILE_PUMPKIN_VILLAGE;
   public static final Holder<PlacedFeature> OAK_VILLAGE;
   public static final Holder<PlacedFeature> ACACIA_VILLAGE;
   public static final Holder<PlacedFeature> SPRUCE_VILLAGE;
   public static final Holder<PlacedFeature> PINE_VILLAGE;
   public static final Holder<PlacedFeature> PATCH_CACTUS_VILLAGE;
   public static final Holder<PlacedFeature> FLOWER_PLAIN_VILLAGE;
   public static final Holder<PlacedFeature> PATCH_TAIGA_GRASS_VILLAGE;
   public static final Holder<PlacedFeature> PATCH_BERRY_BUSH_VILLAGE;

   public VillagePlacements() {
      super();
   }

   static {
      PILE_HAY_VILLAGE = PlacementUtils.register("pile_hay", PileFeatures.PILE_HAY);
      PILE_MELON_VILLAGE = PlacementUtils.register("pile_melon", PileFeatures.PILE_MELON);
      PILE_SNOW_VILLAGE = PlacementUtils.register("pile_snow", PileFeatures.PILE_SNOW);
      PILE_ICE_VILLAGE = PlacementUtils.register("pile_ice", PileFeatures.PILE_ICE);
      PILE_PUMPKIN_VILLAGE = PlacementUtils.register("pile_pumpkin", PileFeatures.PILE_PUMPKIN);
      OAK_VILLAGE = PlacementUtils.register("oak", TreeFeatures.OAK, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      ACACIA_VILLAGE = PlacementUtils.register("acacia", TreeFeatures.ACACIA, PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
      SPRUCE_VILLAGE = PlacementUtils.register("spruce", TreeFeatures.SPRUCE, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PINE_VILLAGE = PlacementUtils.register("pine", TreeFeatures.PINE, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PATCH_CACTUS_VILLAGE = PlacementUtils.register("patch_cactus", VegetationFeatures.PATCH_CACTUS);
      FLOWER_PLAIN_VILLAGE = PlacementUtils.register("flower_plain", VegetationFeatures.FLOWER_PLAIN);
      PATCH_TAIGA_GRASS_VILLAGE = PlacementUtils.register("patch_taiga_grass", VegetationFeatures.PATCH_TAIGA_GRASS);
      PATCH_BERRY_BUSH_VILLAGE = PlacementUtils.register("patch_berry_bush", VegetationFeatures.PATCH_BERRY_BUSH);
   }
}
