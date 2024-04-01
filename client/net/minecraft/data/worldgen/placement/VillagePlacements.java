package net.minecraft.data.worldgen.placement;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.PileFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VillagePlacements {
   public static final ResourceKey<PlacedFeature> PILE_HAY_VILLAGE = PlacementUtils.createKey("pile_hay");
   public static final ResourceKey<PlacedFeature> PILE_MELON_VILLAGE = PlacementUtils.createKey("pile_melon");
   public static final ResourceKey<PlacedFeature> PILE_POTATO_FRUIT_VILLAGE = PlacementUtils.createKey("pile_potato_fruit");
   public static final ResourceKey<PlacedFeature> PILE_SNOW_VILLAGE = PlacementUtils.createKey("pile_snow");
   public static final ResourceKey<PlacedFeature> PILE_ICE_VILLAGE = PlacementUtils.createKey("pile_ice");
   public static final ResourceKey<PlacedFeature> PILE_PUMPKIN_VILLAGE = PlacementUtils.createKey("pile_pumpkin");
   public static final ResourceKey<PlacedFeature> OAK_VILLAGE = PlacementUtils.createKey("oak");
   public static final ResourceKey<PlacedFeature> POTATO_VILLAGE = PlacementUtils.createKey("potato");
   public static final ResourceKey<PlacedFeature> ACACIA_VILLAGE = PlacementUtils.createKey("acacia");
   public static final ResourceKey<PlacedFeature> SPRUCE_VILLAGE = PlacementUtils.createKey("spruce");
   public static final ResourceKey<PlacedFeature> PINE_VILLAGE = PlacementUtils.createKey("pine");
   public static final ResourceKey<PlacedFeature> PATCH_CACTUS_VILLAGE = PlacementUtils.createKey("patch_cactus");
   public static final ResourceKey<PlacedFeature> FLOWER_PLAIN_VILLAGE = PlacementUtils.createKey("flower_plain");
   public static final ResourceKey<PlacedFeature> PATCH_TAIGA_GRASS_VILLAGE = PlacementUtils.createKey("patch_taiga_grass");
   public static final ResourceKey<PlacedFeature> PATCH_BERRY_BUSH_VILLAGE = PlacementUtils.createKey("patch_berry_bush");

   public VillagePlacements() {
      super();
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(PileFeatures.PILE_HAY);
      Holder.Reference var3 = var1.getOrThrow(PileFeatures.PILE_POTATO_FRUIT);
      Holder.Reference var4 = var1.getOrThrow(PileFeatures.PILE_MELON);
      Holder.Reference var5 = var1.getOrThrow(PileFeatures.PILE_SNOW);
      Holder.Reference var6 = var1.getOrThrow(PileFeatures.PILE_ICE);
      Holder.Reference var7 = var1.getOrThrow(PileFeatures.PILE_PUMPKIN);
      Holder.Reference var8 = var1.getOrThrow(TreeFeatures.OAK);
      Holder.Reference var9 = var1.getOrThrow(TreeFeatures.POTATO_TREE);
      Holder.Reference var10 = var1.getOrThrow(TreeFeatures.ACACIA);
      Holder.Reference var11 = var1.getOrThrow(TreeFeatures.SPRUCE);
      Holder.Reference var12 = var1.getOrThrow(TreeFeatures.PINE);
      Holder.Reference var13 = var1.getOrThrow(VegetationFeatures.PATCH_CACTUS);
      Holder.Reference var14 = var1.getOrThrow(VegetationFeatures.FLOWER_PLAIN);
      Holder.Reference var15 = var1.getOrThrow(VegetationFeatures.PATCH_TAIGA_GRASS);
      Holder.Reference var16 = var1.getOrThrow(VegetationFeatures.PATCH_BERRY_BUSH);
      PlacementUtils.register(var0, PILE_HAY_VILLAGE, var2);
      PlacementUtils.register(var0, PILE_POTATO_FRUIT_VILLAGE, var3);
      PlacementUtils.register(var0, PILE_MELON_VILLAGE, var4);
      PlacementUtils.register(var0, PILE_SNOW_VILLAGE, var5);
      PlacementUtils.register(var0, PILE_ICE_VILLAGE, var6);
      PlacementUtils.register(var0, PILE_PUMPKIN_VILLAGE, var7);
      PlacementUtils.register(var0, OAK_VILLAGE, var8, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, POTATO_VILLAGE, var9, PlacementUtils.filteredByBlockSurvival(Blocks.POTATO_SPROUTS));
      PlacementUtils.register(var0, ACACIA_VILLAGE, var10, PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
      PlacementUtils.register(var0, SPRUCE_VILLAGE, var11, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(var0, PINE_VILLAGE, var12, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(var0, PATCH_CACTUS_VILLAGE, var13);
      PlacementUtils.register(var0, FLOWER_PLAIN_VILLAGE, var14);
      PlacementUtils.register(var0, PATCH_TAIGA_GRASS_VILLAGE, var15);
      PlacementUtils.register(var0, PATCH_BERRY_BUSH_VILLAGE, var16);
   }
}
