package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class SurfaceRuleData {
   private static final SurfaceRules.RuleSource AIR;
   private static final SurfaceRules.RuleSource BEDROCK;
   private static final SurfaceRules.RuleSource WHITE_TERRACOTTA;
   private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA;
   private static final SurfaceRules.RuleSource TERRACOTTA;
   private static final SurfaceRules.RuleSource RED_SAND;
   private static final SurfaceRules.RuleSource RED_SANDSTONE;
   private static final SurfaceRules.RuleSource STONE;
   private static final SurfaceRules.RuleSource DEEPSLATE;
   private static final SurfaceRules.RuleSource DIRT;
   private static final SurfaceRules.RuleSource PODZOL;
   private static final SurfaceRules.RuleSource COARSE_DIRT;
   private static final SurfaceRules.RuleSource MYCELIUM;
   private static final SurfaceRules.RuleSource GRASS_BLOCK;
   private static final SurfaceRules.RuleSource CALCITE;
   private static final SurfaceRules.RuleSource GRAVEL;
   private static final SurfaceRules.RuleSource SAND;
   private static final SurfaceRules.RuleSource SANDSTONE;
   private static final SurfaceRules.RuleSource PACKED_ICE;
   private static final SurfaceRules.RuleSource SNOW_BLOCK;
   private static final SurfaceRules.RuleSource POWDER_SNOW;
   private static final SurfaceRules.RuleSource ICE;
   private static final SurfaceRules.RuleSource WATER;
   private static final SurfaceRules.RuleSource LAVA;
   private static final SurfaceRules.RuleSource NETHERRACK;
   private static final SurfaceRules.RuleSource SOUL_SAND;
   private static final SurfaceRules.RuleSource SOUL_SOIL;
   private static final SurfaceRules.RuleSource BASALT;
   private static final SurfaceRules.RuleSource BLACKSTONE;
   private static final SurfaceRules.RuleSource WARPED_WART_BLOCK;
   private static final SurfaceRules.RuleSource WARPED_NYLIUM;
   private static final SurfaceRules.RuleSource NETHER_WART_BLOCK;
   private static final SurfaceRules.RuleSource CRIMSON_NYLIUM;
   private static final SurfaceRules.RuleSource ENDSTONE;

   public SurfaceRuleData() {
      super();
   }

   private static SurfaceRules.RuleSource makeStateRule(Block var0) {
      return SurfaceRules.state(var0.defaultBlockState());
   }

   public static SurfaceRules.RuleSource overworld() {
      return overworldLike(true, false, true);
   }

   public static SurfaceRules.RuleSource overworldLike(boolean var0, boolean var1, boolean var2) {
      SurfaceRules.ConditionSource var3 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
      SurfaceRules.ConditionSource var4 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
      SurfaceRules.ConditionSource var5 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
      SurfaceRules.ConditionSource var6 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
      SurfaceRules.ConditionSource var7 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
      SurfaceRules.ConditionSource var8 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
      SurfaceRules.ConditionSource var9 = SurfaceRules.waterBlockCheck(-1, 0);
      SurfaceRules.ConditionSource var10 = SurfaceRules.waterBlockCheck(0, 0);
      SurfaceRules.ConditionSource var11 = SurfaceRules.waterStartCheck(-6, -1);
      SurfaceRules.ConditionSource var12 = SurfaceRules.hole();
      SurfaceRules.ConditionSource var13 = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
      SurfaceRules.ConditionSource var14 = SurfaceRules.steep();
      SurfaceRules.RuleSource var15 = SurfaceRules.sequence(SurfaceRules.ifTrue(var9, GRASS_BLOCK), DIRT);
      SurfaceRules.RuleSource var16 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
      SurfaceRules.RuleSource var17 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
      SurfaceRules.ConditionSource var18 = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.DESERT, Biomes.BEACH, Biomes.SNOWY_BEACH);
      SurfaceRules.RuleSource var19 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125D, 0.0125D), CALCITE), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_SHORE), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05D, 0.05D), var17), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), STONE)), SurfaceRules.ifTrue(var18, var16), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), STONE));
      SurfaceRules.RuleSource var20 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45D, 0.58D), POWDER_SNOW);
      SurfaceRules.RuleSource var21 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35D, 0.6D), POWDER_SNOW);
      SurfaceRules.RuleSource var22 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(var14, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5D, 0.2D), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625D, 0.025D), ICE), SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(var14, STONE), var20, SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(var20, DIRT)), var19, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0D), var17), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0D), DIRT), var17)), DIRT);
      SurfaceRules.RuleSource var23 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(var14, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0D, 0.2D), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0D, 0.025D), ICE), SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(var14, STONE), var21, SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(var14, STONE), SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(var21, SNOW_BLOCK)), var19, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.5D), COARSE_DIRT))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0D), var17), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0D), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0D), var15), var17)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75D), COARSE_DIRT), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.95D), PODZOL))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SNOW_BLOCK), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM), var15);
      SurfaceRules.ConditionSource var24 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909D, -0.5454D);
      SurfaceRules.ConditionSource var25 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818D, 0.1818D);
      SurfaceRules.ConditionSource var26 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454D, 0.909D);
      SurfaceRules.RuleSource var27 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), SurfaceRules.ifTrue(var3, SurfaceRules.sequence(SurfaceRules.ifTrue(var24, COARSE_DIRT), SurfaceRules.ifTrue(var25, COARSE_DIRT), SurfaceRules.ifTrue(var26, COARSE_DIRT), var15))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SWAMP), SurfaceRules.ifTrue(var7, SurfaceRules.ifTrue(SurfaceRules.not(var8), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0D), WATER)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(var4, ORANGE_TERRACOTTA), SurfaceRules.ifTrue(var6, SurfaceRules.sequence(SurfaceRules.ifTrue(var24, TERRACOTTA), SurfaceRules.ifTrue(var25, TERRACOTTA), SurfaceRules.ifTrue(var26, TERRACOTTA), SurfaceRules.bandlands())), SurfaceRules.ifTrue(var9, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)), SurfaceRules.ifTrue(SurfaceRules.not(var12), ORANGE_TERRACOTTA), SurfaceRules.ifTrue(var11, WHITE_TERRACOTTA), var17)), SurfaceRules.ifTrue(var5, SurfaceRules.sequence(SurfaceRules.ifTrue(var8, SurfaceRules.ifTrue(SurfaceRules.not(var6), ORANGE_TERRACOTTA)), SurfaceRules.bandlands())), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(var11, WHITE_TERRACOTTA)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(var9, SurfaceRules.sequence(SurfaceRules.ifTrue(var13, SurfaceRules.ifTrue(var12, SurfaceRules.sequence(SurfaceRules.ifTrue(var10, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER))), var23))), SurfaceRules.ifTrue(var11, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(var13, SurfaceRules.ifTrue(var12, WATER))), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, var22), SurfaceRules.ifTrue(var18, SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, true, CaveSurface.FLOOR), SANDSTONE)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), var16), var17)));
      Builder var28 = ImmutableList.builder();
      if (var1) {
         var28.add(SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK));
      }

      if (var2) {
         var28.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
      }

      SurfaceRules.RuleSource var29 = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), var27);
      var28.add(var0 ? var29 : var27);
      var28.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
      return SurfaceRules.sequence((SurfaceRules.RuleSource[])var28.build().toArray((var0x) -> {
         return new SurfaceRules.RuleSource[var0x];
      }));
   }

   public static SurfaceRules.RuleSource nether() {
      SurfaceRules.ConditionSource var0 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
      SurfaceRules.ConditionSource var1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
      SurfaceRules.ConditionSource var2 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(30), 0);
      SurfaceRules.ConditionSource var3 = SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(35), 0));
      SurfaceRules.ConditionSource var4 = SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
      SurfaceRules.ConditionSource var5 = SurfaceRules.hole();
      SurfaceRules.ConditionSource var6 = SurfaceRules.noiseCondition(Noises.SOUL_SAND_LAYER, -0.012D);
      SurfaceRules.ConditionSource var7 = SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.012D);
      SurfaceRules.ConditionSource var8 = SurfaceRules.noiseCondition(Noises.PATCH, -0.012D);
      SurfaceRules.ConditionSource var9 = SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.54D);
      SurfaceRules.ConditionSource var10 = SurfaceRules.noiseCondition(Noises.NETHER_WART, 1.17D);
      SurfaceRules.ConditionSource var11 = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0D);
      SurfaceRules.RuleSource var12 = SurfaceRules.ifTrue(var8, SurfaceRules.ifTrue(var2, SurfaceRules.ifTrue(var3, GRAVEL)));
      return SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK), SurfaceRules.ifTrue(var4, NETHERRACK), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BASALT_DELTAS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, BASALT), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(var12, SurfaceRules.ifTrue(var11, BASALT), BLACKSTONE)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SOUL_SAND_VALLEY), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, SurfaceRules.sequence(SurfaceRules.ifTrue(var11, SOUL_SAND), SOUL_SOIL)), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(var12, SurfaceRules.ifTrue(var11, SOUL_SAND), SOUL_SOIL)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(var1), SurfaceRules.ifTrue(var5, LAVA)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARPED_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(var9), SurfaceRules.ifTrue(var0, SurfaceRules.sequence(SurfaceRules.ifTrue(var10, WARPED_WART_BLOCK), WARPED_NYLIUM)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.CRIMSON_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(var9), SurfaceRules.ifTrue(var0, SurfaceRules.sequence(SurfaceRules.ifTrue(var10, NETHER_WART_BLOCK), CRIMSON_NYLIUM)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.NETHER_WASTES), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(var6, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(var5), SurfaceRules.ifTrue(var2, SurfaceRules.ifTrue(var3, SOUL_SAND))), NETHERRACK))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(var0, SurfaceRules.ifTrue(var3, SurfaceRules.ifTrue(var7, SurfaceRules.sequence(SurfaceRules.ifTrue(var1, GRAVEL), SurfaceRules.ifTrue(SurfaceRules.not(var5), GRAVEL)))))))), NETHERRACK);
   }

   public static SurfaceRules.RuleSource end() {
      return ENDSTONE;
   }

   private static SurfaceRules.ConditionSource surfaceNoiseAbove(double var0) {
      return SurfaceRules.noiseCondition(Noises.SURFACE, var0 / 8.25D, 1.7976931348623157E308D);
   }

   static {
      AIR = makeStateRule(Blocks.AIR);
      BEDROCK = makeStateRule(Blocks.BEDROCK);
      WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
      ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
      TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
      RED_SAND = makeStateRule(Blocks.RED_SAND);
      RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
      STONE = makeStateRule(Blocks.STONE);
      DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
      DIRT = makeStateRule(Blocks.DIRT);
      PODZOL = makeStateRule(Blocks.PODZOL);
      COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
      MYCELIUM = makeStateRule(Blocks.MYCELIUM);
      GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
      CALCITE = makeStateRule(Blocks.CALCITE);
      GRAVEL = makeStateRule(Blocks.GRAVEL);
      SAND = makeStateRule(Blocks.SAND);
      SANDSTONE = makeStateRule(Blocks.SANDSTONE);
      PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
      SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
      POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
      ICE = makeStateRule(Blocks.ICE);
      WATER = makeStateRule(Blocks.WATER);
      LAVA = makeStateRule(Blocks.LAVA);
      NETHERRACK = makeStateRule(Blocks.NETHERRACK);
      SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
      SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
      BASALT = makeStateRule(Blocks.BASALT);
      BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
      WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
      WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
      NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
      CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
      ENDSTONE = makeStateRule(Blocks.END_STONE);
   }
}
