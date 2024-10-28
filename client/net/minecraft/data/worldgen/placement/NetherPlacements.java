package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class NetherPlacements {
   public static final ResourceKey<PlacedFeature> DELTA = PlacementUtils.createKey("delta");
   public static final ResourceKey<PlacedFeature> SMALL_BASALT_COLUMNS = PlacementUtils.createKey("small_basalt_columns");
   public static final ResourceKey<PlacedFeature> LARGE_BASALT_COLUMNS = PlacementUtils.createKey("large_basalt_columns");
   public static final ResourceKey<PlacedFeature> BASALT_BLOBS = PlacementUtils.createKey("basalt_blobs");
   public static final ResourceKey<PlacedFeature> BLACKSTONE_BLOBS = PlacementUtils.createKey("blackstone_blobs");
   public static final ResourceKey<PlacedFeature> GLOWSTONE_EXTRA = PlacementUtils.createKey("glowstone_extra");
   public static final ResourceKey<PlacedFeature> GLOWSTONE = PlacementUtils.createKey("glowstone");
   public static final ResourceKey<PlacedFeature> CRIMSON_FOREST_VEGETATION = PlacementUtils.createKey("crimson_forest_vegetation");
   public static final ResourceKey<PlacedFeature> WARPED_FOREST_VEGETATION = PlacementUtils.createKey("warped_forest_vegetation");
   public static final ResourceKey<PlacedFeature> NETHER_SPROUTS = PlacementUtils.createKey("nether_sprouts");
   public static final ResourceKey<PlacedFeature> TWISTING_VINES = PlacementUtils.createKey("twisting_vines");
   public static final ResourceKey<PlacedFeature> WEEPING_VINES = PlacementUtils.createKey("weeping_vines");
   public static final ResourceKey<PlacedFeature> PATCH_CRIMSON_ROOTS = PlacementUtils.createKey("patch_crimson_roots");
   public static final ResourceKey<PlacedFeature> BASALT_PILLAR = PlacementUtils.createKey("basalt_pillar");
   public static final ResourceKey<PlacedFeature> SPRING_DELTA = PlacementUtils.createKey("spring_delta");
   public static final ResourceKey<PlacedFeature> SPRING_CLOSED = PlacementUtils.createKey("spring_closed");
   public static final ResourceKey<PlacedFeature> SPRING_CLOSED_DOUBLE = PlacementUtils.createKey("spring_closed_double");
   public static final ResourceKey<PlacedFeature> SPRING_OPEN = PlacementUtils.createKey("spring_open");
   public static final ResourceKey<PlacedFeature> PATCH_SOUL_FIRE = PlacementUtils.createKey("patch_soul_fire");
   public static final ResourceKey<PlacedFeature> PATCH_FIRE = PlacementUtils.createKey("patch_fire");

   public NetherPlacements() {
      super();
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(NetherFeatures.DELTA);
      Holder.Reference var3 = var1.getOrThrow(NetherFeatures.SMALL_BASALT_COLUMNS);
      Holder.Reference var4 = var1.getOrThrow(NetherFeatures.LARGE_BASALT_COLUMNS);
      Holder.Reference var5 = var1.getOrThrow(NetherFeatures.BASALT_BLOBS);
      Holder.Reference var6 = var1.getOrThrow(NetherFeatures.BLACKSTONE_BLOBS);
      Holder.Reference var7 = var1.getOrThrow(NetherFeatures.GLOWSTONE_EXTRA);
      Holder.Reference var8 = var1.getOrThrow(NetherFeatures.CRIMSON_FOREST_VEGETATION);
      Holder.Reference var9 = var1.getOrThrow(NetherFeatures.WARPED_FOREST_VEGETION);
      Holder.Reference var10 = var1.getOrThrow(NetherFeatures.NETHER_SPROUTS);
      Holder.Reference var11 = var1.getOrThrow(NetherFeatures.TWISTING_VINES);
      Holder.Reference var12 = var1.getOrThrow(NetherFeatures.WEEPING_VINES);
      Holder.Reference var13 = var1.getOrThrow(NetherFeatures.PATCH_CRIMSON_ROOTS);
      Holder.Reference var14 = var1.getOrThrow(NetherFeatures.BASALT_PILLAR);
      Holder.Reference var15 = var1.getOrThrow(NetherFeatures.SPRING_LAVA_NETHER);
      Holder.Reference var16 = var1.getOrThrow(NetherFeatures.SPRING_NETHER_CLOSED);
      Holder.Reference var17 = var1.getOrThrow(NetherFeatures.SPRING_NETHER_OPEN);
      Holder.Reference var18 = var1.getOrThrow(NetherFeatures.PATCH_SOUL_FIRE);
      Holder.Reference var19 = var1.getOrThrow(NetherFeatures.PATCH_FIRE);
      PlacementUtils.register(var0, DELTA, var2, (PlacementModifier[])(CountOnEveryLayerPlacement.of(40), BiomeFilter.biome()));
      PlacementUtils.register(var0, SMALL_BASALT_COLUMNS, var3, (PlacementModifier[])(CountOnEveryLayerPlacement.of(4), BiomeFilter.biome()));
      PlacementUtils.register(var0, LARGE_BASALT_COLUMNS, var4, (PlacementModifier[])(CountOnEveryLayerPlacement.of(2), BiomeFilter.biome()));
      PlacementUtils.register(var0, BASALT_BLOBS, var5, (PlacementModifier[])(CountPlacement.of(75), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, BLACKSTONE_BLOBS, var6, (PlacementModifier[])(CountPlacement.of(25), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, GLOWSTONE_EXTRA, var7, (PlacementModifier[])(CountPlacement.of(BiasedToBottomInt.of(0, 9)), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome()));
      PlacementUtils.register(var0, GLOWSTONE, var7, (PlacementModifier[])(CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, CRIMSON_FOREST_VEGETATION, var8, (PlacementModifier[])(CountOnEveryLayerPlacement.of(6), BiomeFilter.biome()));
      PlacementUtils.register(var0, WARPED_FOREST_VEGETATION, var9, (PlacementModifier[])(CountOnEveryLayerPlacement.of(5), BiomeFilter.biome()));
      PlacementUtils.register(var0, NETHER_SPROUTS, var10, (PlacementModifier[])(CountOnEveryLayerPlacement.of(4), BiomeFilter.biome()));
      PlacementUtils.register(var0, TWISTING_VINES, var11, (PlacementModifier[])(CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, WEEPING_VINES, var12, (PlacementModifier[])(CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_CRIMSON_ROOTS, var13, (PlacementModifier[])(PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, BASALT_PILLAR, var14, (PlacementModifier[])(CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, SPRING_DELTA, var15, (PlacementModifier[])(CountPlacement.of(16), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome()));
      PlacementUtils.register(var0, SPRING_CLOSED, var16, (PlacementModifier[])(CountPlacement.of(16), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, BiomeFilter.biome()));
      PlacementUtils.register(var0, SPRING_CLOSED_DOUBLE, var16, (PlacementModifier[])(CountPlacement.of(32), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, BiomeFilter.biome()));
      PlacementUtils.register(var0, SPRING_OPEN, var17, (PlacementModifier[])(CountPlacement.of(8), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome()));
      List var20 = List.of(CountPlacement.of(UniformInt.of(0, 5)), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
      PlacementUtils.register(var0, PATCH_SOUL_FIRE, var18, (List)var20);
      PlacementUtils.register(var0, PATCH_FIRE, var19, (List)var20);
   }
}
