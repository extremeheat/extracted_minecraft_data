package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class OrePlacements {
   public static final ResourceKey<PlacedFeature> ORE_MAGMA = PlacementUtils.createKey("ore_magma");
   public static final ResourceKey<PlacedFeature> ORE_SOUL_SAND = PlacementUtils.createKey("ore_soul_sand");
   public static final ResourceKey<PlacedFeature> ORE_GOLD_DELTAS = PlacementUtils.createKey("ore_gold_deltas");
   public static final ResourceKey<PlacedFeature> ORE_QUARTZ_DELTAS = PlacementUtils.createKey("ore_quartz_deltas");
   public static final ResourceKey<PlacedFeature> ORE_GOLD_NETHER = PlacementUtils.createKey("ore_gold_nether");
   public static final ResourceKey<PlacedFeature> ORE_QUARTZ_NETHER = PlacementUtils.createKey("ore_quartz_nether");
   public static final ResourceKey<PlacedFeature> ORE_GRAVEL_NETHER = PlacementUtils.createKey("ore_gravel_nether");
   public static final ResourceKey<PlacedFeature> ORE_BLACKSTONE = PlacementUtils.createKey("ore_blackstone");
   public static final ResourceKey<PlacedFeature> ORE_DIRT = PlacementUtils.createKey("ore_dirt");
   public static final ResourceKey<PlacedFeature> ORE_GRAVEL = PlacementUtils.createKey("ore_gravel");
   public static final ResourceKey<PlacedFeature> ORE_GRANITE_UPPER = PlacementUtils.createKey("ore_granite_upper");
   public static final ResourceKey<PlacedFeature> ORE_GRANITE_LOWER = PlacementUtils.createKey("ore_granite_lower");
   public static final ResourceKey<PlacedFeature> ORE_DIORITE_UPPER = PlacementUtils.createKey("ore_diorite_upper");
   public static final ResourceKey<PlacedFeature> ORE_DIORITE_LOWER = PlacementUtils.createKey("ore_diorite_lower");
   public static final ResourceKey<PlacedFeature> ORE_ANDESITE_UPPER = PlacementUtils.createKey("ore_andesite_upper");
   public static final ResourceKey<PlacedFeature> ORE_ANDESITE_LOWER = PlacementUtils.createKey("ore_andesite_lower");
   public static final ResourceKey<PlacedFeature> ORE_TUFF = PlacementUtils.createKey("ore_tuff");
   public static final ResourceKey<PlacedFeature> ORE_COAL_UPPER = PlacementUtils.createKey("ore_coal_upper");
   public static final ResourceKey<PlacedFeature> ORE_COAL_LOWER = PlacementUtils.createKey("ore_coal_lower");
   public static final ResourceKey<PlacedFeature> ORE_IRON_UPPER = PlacementUtils.createKey("ore_iron_upper");
   public static final ResourceKey<PlacedFeature> ORE_IRON_MIDDLE = PlacementUtils.createKey("ore_iron_middle");
   public static final ResourceKey<PlacedFeature> ORE_IRON_SMALL = PlacementUtils.createKey("ore_iron_small");
   public static final ResourceKey<PlacedFeature> ORE_GOLD_EXTRA = PlacementUtils.createKey("ore_gold_extra");
   public static final ResourceKey<PlacedFeature> ORE_GOLD = PlacementUtils.createKey("ore_gold");
   public static final ResourceKey<PlacedFeature> ORE_GOLD_LOWER = PlacementUtils.createKey("ore_gold_lower");
   public static final ResourceKey<PlacedFeature> ORE_REDSTONE = PlacementUtils.createKey("ore_redstone");
   public static final ResourceKey<PlacedFeature> ORE_REDSTONE_LOWER = PlacementUtils.createKey("ore_redstone_lower");
   public static final ResourceKey<PlacedFeature> ORE_DIAMOND = PlacementUtils.createKey("ore_diamond");
   public static final ResourceKey<PlacedFeature> ORE_DIAMOND_MEDIUM = PlacementUtils.createKey("ore_diamond_medium");
   public static final ResourceKey<PlacedFeature> ORE_DIAMOND_LARGE = PlacementUtils.createKey("ore_diamond_large");
   public static final ResourceKey<PlacedFeature> ORE_DIAMOND_BURIED = PlacementUtils.createKey("ore_diamond_buried");
   public static final ResourceKey<PlacedFeature> ORE_LAPIS = PlacementUtils.createKey("ore_lapis");
   public static final ResourceKey<PlacedFeature> ORE_LAPIS_BURIED = PlacementUtils.createKey("ore_lapis_buried");
   public static final ResourceKey<PlacedFeature> ORE_INFESTED = PlacementUtils.createKey("ore_infested");
   public static final ResourceKey<PlacedFeature> ORE_EMERALD = PlacementUtils.createKey("ore_emerald");
   public static final ResourceKey<PlacedFeature> ORE_ANCIENT_DEBRIS_LARGE = PlacementUtils.createKey("ore_ancient_debris_large");
   public static final ResourceKey<PlacedFeature> ORE_ANCIENT_DEBRIS_SMALL = PlacementUtils.createKey("ore_debris_small");
   public static final ResourceKey<PlacedFeature> ORE_COPPER = PlacementUtils.createKey("ore_copper");
   public static final ResourceKey<PlacedFeature> ORE_COPPER_LARGE = PlacementUtils.createKey("ore_copper_large");
   public static final ResourceKey<PlacedFeature> ORE_CLAY = PlacementUtils.createKey("ore_clay");

   public OrePlacements() {
      super();
   }

   private static List<PlacementModifier> orePlacement(PlacementModifier var0, PlacementModifier var1) {
      return List.of(var0, InSquarePlacement.spread(), var1, BiomeFilter.biome());
   }

   private static List<PlacementModifier> commonOrePlacement(int var0, PlacementModifier var1) {
      return orePlacement(CountPlacement.of(var0), var1);
   }

   private static List<PlacementModifier> rareOrePlacement(int var0, PlacementModifier var1) {
      return orePlacement(RarityFilter.onAverageOnceEvery(var0), var1);
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(OreFeatures.ORE_MAGMA);
      Holder.Reference var3 = var1.getOrThrow(OreFeatures.ORE_SOUL_SAND);
      Holder.Reference var4 = var1.getOrThrow(OreFeatures.ORE_NETHER_GOLD);
      Holder.Reference var5 = var1.getOrThrow(OreFeatures.ORE_QUARTZ);
      Holder.Reference var6 = var1.getOrThrow(OreFeatures.ORE_GRAVEL_NETHER);
      Holder.Reference var7 = var1.getOrThrow(OreFeatures.ORE_BLACKSTONE);
      Holder.Reference var8 = var1.getOrThrow(OreFeatures.ORE_DIRT);
      Holder.Reference var9 = var1.getOrThrow(OreFeatures.ORE_GRAVEL);
      Holder.Reference var10 = var1.getOrThrow(OreFeatures.ORE_GRANITE);
      Holder.Reference var11 = var1.getOrThrow(OreFeatures.ORE_DIORITE);
      Holder.Reference var12 = var1.getOrThrow(OreFeatures.ORE_ANDESITE);
      Holder.Reference var13 = var1.getOrThrow(OreFeatures.ORE_TUFF);
      Holder.Reference var14 = var1.getOrThrow(OreFeatures.ORE_COAL);
      Holder.Reference var15 = var1.getOrThrow(OreFeatures.ORE_COAL_BURIED);
      Holder.Reference var16 = var1.getOrThrow(OreFeatures.ORE_IRON);
      Holder.Reference var17 = var1.getOrThrow(OreFeatures.ORE_IRON_SMALL);
      Holder.Reference var18 = var1.getOrThrow(OreFeatures.ORE_GOLD);
      Holder.Reference var19 = var1.getOrThrow(OreFeatures.ORE_GOLD_BURIED);
      Holder.Reference var20 = var1.getOrThrow(OreFeatures.ORE_REDSTONE);
      Holder.Reference var21 = var1.getOrThrow(OreFeatures.ORE_DIAMOND_SMALL);
      Holder.Reference var22 = var1.getOrThrow(OreFeatures.ORE_DIAMOND_MEDIUM);
      Holder.Reference var23 = var1.getOrThrow(OreFeatures.ORE_DIAMOND_LARGE);
      Holder.Reference var24 = var1.getOrThrow(OreFeatures.ORE_DIAMOND_BURIED);
      Holder.Reference var25 = var1.getOrThrow(OreFeatures.ORE_LAPIS);
      Holder.Reference var26 = var1.getOrThrow(OreFeatures.ORE_LAPIS_BURIED);
      Holder.Reference var27 = var1.getOrThrow(OreFeatures.ORE_INFESTED);
      Holder.Reference var28 = var1.getOrThrow(OreFeatures.ORE_EMERALD);
      Holder.Reference var29 = var1.getOrThrow(OreFeatures.ORE_ANCIENT_DEBRIS_LARGE);
      Holder.Reference var30 = var1.getOrThrow(OreFeatures.ORE_ANCIENT_DEBRIS_SMALL);
      Holder.Reference var31 = var1.getOrThrow(OreFeatures.ORE_COPPPER_SMALL);
      Holder.Reference var32 = var1.getOrThrow(OreFeatures.ORE_COPPER_LARGE);
      Holder.Reference var33 = var1.getOrThrow(OreFeatures.ORE_CLAY);
      PlacementUtils.register(var0, ORE_MAGMA, var2, (List)commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.absolute(27), VerticalAnchor.absolute(36))));
      PlacementUtils.register(var0, ORE_SOUL_SAND, var3, (List)commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(31))));
      PlacementUtils.register(var0, ORE_GOLD_DELTAS, var4, (List)commonOrePlacement(20, PlacementUtils.RANGE_10_10));
      PlacementUtils.register(var0, ORE_QUARTZ_DELTAS, var5, (List)commonOrePlacement(32, PlacementUtils.RANGE_10_10));
      PlacementUtils.register(var0, ORE_GOLD_NETHER, var4, (List)commonOrePlacement(10, PlacementUtils.RANGE_10_10));
      PlacementUtils.register(var0, ORE_QUARTZ_NETHER, var5, (List)commonOrePlacement(16, PlacementUtils.RANGE_10_10));
      PlacementUtils.register(var0, ORE_GRAVEL_NETHER, var6, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(5), VerticalAnchor.absolute(41))));
      PlacementUtils.register(var0, ORE_BLACKSTONE, var7, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(5), VerticalAnchor.absolute(31))));
      PlacementUtils.register(var0, ORE_DIRT, var8, (List)commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160))));
      PlacementUtils.register(var0, ORE_GRAVEL, var9, (List)commonOrePlacement(14, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top())));
      PlacementUtils.register(var0, ORE_GRANITE_UPPER, var10, (List)rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
      PlacementUtils.register(var0, ORE_GRANITE_LOWER, var10, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
      PlacementUtils.register(var0, ORE_DIORITE_UPPER, var11, (List)rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
      PlacementUtils.register(var0, ORE_DIORITE_LOWER, var11, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
      PlacementUtils.register(var0, ORE_ANDESITE_UPPER, var12, (List)rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
      PlacementUtils.register(var0, ORE_ANDESITE_LOWER, var12, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
      PlacementUtils.register(var0, ORE_TUFF, var13, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0))));
      PlacementUtils.register(var0, ORE_COAL_UPPER, var14, (List)commonOrePlacement(30, HeightRangePlacement.uniform(VerticalAnchor.absolute(136), VerticalAnchor.top())));
      PlacementUtils.register(var0, ORE_COAL_LOWER, var15, (List)commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(192))));
      PlacementUtils.register(var0, ORE_IRON_UPPER, var16, (List)commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
      PlacementUtils.register(var0, ORE_IRON_MIDDLE, var16, (List)commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
      PlacementUtils.register(var0, ORE_IRON_SMALL, var17, (List)commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
      PlacementUtils.register(var0, ORE_GOLD_EXTRA, var18, (List)commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(32), VerticalAnchor.absolute(256))));
      PlacementUtils.register(var0, ORE_GOLD, var19, (List)commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));
      PlacementUtils.register(var0, ORE_GOLD_LOWER, var19, (List)orePlacement(CountPlacement.of(UniformInt.of(0, 1)), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48))));
      PlacementUtils.register(var0, ORE_REDSTONE, var20, (List)commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15))));
      PlacementUtils.register(var0, ORE_REDSTONE_LOWER, var20, (List)commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-32), VerticalAnchor.aboveBottom(32))));
      PlacementUtils.register(var0, ORE_DIAMOND, var21, (List)commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
      PlacementUtils.register(var0, ORE_DIAMOND_MEDIUM, var22, (List)commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-4))));
      PlacementUtils.register(var0, ORE_DIAMOND_LARGE, var23, (List)rareOrePlacement(9, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
      PlacementUtils.register(var0, ORE_DIAMOND_BURIED, var24, (List)commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
      PlacementUtils.register(var0, ORE_LAPIS, var25, (List)commonOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(32))));
      PlacementUtils.register(var0, ORE_LAPIS_BURIED, var26, (List)commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(64))));
      PlacementUtils.register(var0, ORE_INFESTED, var27, (List)commonOrePlacement(14, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(63))));
      PlacementUtils.register(var0, ORE_EMERALD, var28, (List)commonOrePlacement(100, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480))));
      PlacementUtils.register(var0, ORE_ANCIENT_DEBRIS_LARGE, var29, (PlacementModifier[])(InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(24)), BiomeFilter.biome()));
      PlacementUtils.register(var0, ORE_ANCIENT_DEBRIS_SMALL, var30, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.RANGE_8_8, BiomeFilter.biome()));
      PlacementUtils.register(var0, ORE_COPPER, var31, (List)commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))));
      PlacementUtils.register(var0, ORE_COPPER_LARGE, var32, (List)commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112))));
      PlacementUtils.register(var0, ORE_CLAY, var33, (List)commonOrePlacement(46, PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT));
   }
}
