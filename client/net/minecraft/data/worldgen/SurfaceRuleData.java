package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

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
   private static final SurfaceRules.RuleSource MUD;
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
   private static final BlockState CINNABAR;
   private static final BlockState SULFUR;

   public SurfaceRuleData() {
      super();
   }

   private static SurfaceRules.RuleSource makeStateRule(final Block block) {
      return SurfaceRules.state(block.defaultBlockState());
   }

   public static SurfaceRules.RuleSource overworld() {
      return overworldLike(true, false, true);
   }

   public static SurfaceRules.RuleSource overworldLike(final boolean doPreliminarySurfaceCheck, final boolean bedrockRoof, final boolean bedrockFloor) {
      SurfaceRules.ConditionSource woodedBadlandsTop = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
      SurfaceRules.ConditionSource badlandsTop = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
      SurfaceRules.ConditionSource badlandsHeightCondition = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
      SurfaceRules.ConditionSource badlandsMid = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
      SurfaceRules.ConditionSource mangroveSwampPuddleLevel = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
      SurfaceRules.ConditionSource swampPuddleLevel = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
      SurfaceRules.ConditionSource aboveOverworldSeaLevel = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
      SurfaceRules.ConditionSource notUnderwater = SurfaceRules.waterBlockCheck(-1, 0);
      SurfaceRules.ConditionSource aboveWater = SurfaceRules.waterBlockCheck(0, 0);
      SurfaceRules.ConditionSource notUnderDeepWater = SurfaceRules.waterStartCheck(-6, -1);
      SurfaceRules.ConditionSource hole = SurfaceRules.hole();
      SurfaceRules.ConditionSource frozenOcean = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
      SurfaceRules.ConditionSource steep = SurfaceRules.steep();
      SurfaceRules.RuleSource grassOrDirtIfUnderwater = SurfaceRules.sequence(SurfaceRules.ifTrue(aboveWater, GRASS_BLOCK), DIRT);
      SurfaceRules.RuleSource sandOrSandstoneIfCeiling = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
      SurfaceRules.RuleSource gravelOrStoneIfCeiling = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
      SurfaceRules.ConditionSource biomesWithSandAndSandstone = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
      SurfaceRules.ConditionSource biomesWithSandAndVeryDeepSandstone = SurfaceRules.isBiome(Biomes.DESERT);
      SurfaceRules.RuleSource commonSurfaceAndUnderRules = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_SHORE), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05, 0.05), gravelOrStoneIfCeiling), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE)), SurfaceRules.ifTrue(biomesWithSandAndSandstone, sandOrSandstoneIfCeiling), SurfaceRules.ifTrue(biomesWithSandAndVeryDeepSandstone, sandOrSandstoneIfCeiling), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), STONE));
      SurfaceRules.RuleSource powderSnowUnderRule = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), SurfaceRules.ifTrue(aboveWater, POWDER_SNOW));
      SurfaceRules.RuleSource powderSnowSurfaceRule = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), SurfaceRules.ifTrue(aboveWater, POWDER_SNOW));
      SurfaceRules.RuleSource biomeUnderSurfaceRule = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625, 0.025), ICE), SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), powderSnowUnderRule, SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(powderSnowUnderRule, DIRT)), commonSurfaceAndUnderRules, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), gravelOrStoneIfCeiling), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), DIRT), gravelOrStoneIfCeiling)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD), DIRT);
      SurfaceRules.RuleSource biomeSurfaceRule = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0, 0.025), ICE), SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), powderSnowSurfaceRule, SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(powderSnowSurfaceRule, SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK))), commonSurfaceAndUnderRules, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.5), COARSE_DIRT))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), gravelOrStoneIfCeiling), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), grassOrDirtIfUnderwater), gravelOrStoneIfCeiling)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), COARSE_DIRT), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.95), PODZOL))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SurfaceRules.ifTrue(aboveWater, SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM), grassOrDirtIfUnderwater);
      SurfaceRules.ConditionSource clayBand1 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
      SurfaceRules.ConditionSource clayBand2 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
      SurfaceRules.ConditionSource clayBand3 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
      SurfaceRules.RuleSource mainRuleCloseToSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), SurfaceRules.ifTrue(woodedBadlandsTop, SurfaceRules.sequence(SurfaceRules.ifTrue(clayBand1, COARSE_DIRT), SurfaceRules.ifTrue(clayBand2, COARSE_DIRT), SurfaceRules.ifTrue(clayBand3, COARSE_DIRT), grassOrDirtIfUnderwater))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SWAMP), SurfaceRules.ifTrue(swampPuddleLevel, SurfaceRules.ifTrue(SurfaceRules.not(aboveOverworldSeaLevel), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), SurfaceRules.ifTrue(mangroveSwampPuddleLevel, SurfaceRules.ifTrue(SurfaceRules.not(aboveOverworldSeaLevel), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(badlandsTop, ORANGE_TERRACOTTA), SurfaceRules.ifTrue(badlandsMid, SurfaceRules.sequence(SurfaceRules.ifTrue(clayBand1, TERRACOTTA), SurfaceRules.ifTrue(clayBand2, TERRACOTTA), SurfaceRules.ifTrue(clayBand3, TERRACOTTA), SurfaceRules.bandlands())), SurfaceRules.ifTrue(notUnderwater, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)), SurfaceRules.ifTrue(SurfaceRules.not(hole), ORANGE_TERRACOTTA), SurfaceRules.ifTrue(notUnderDeepWater, WHITE_TERRACOTTA), gravelOrStoneIfCeiling)), SurfaceRules.ifTrue(badlandsHeightCondition, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveOverworldSeaLevel, SurfaceRules.ifTrue(SurfaceRules.not(badlandsMid), ORANGE_TERRACOTTA)), SurfaceRules.bandlands())), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(notUnderDeepWater, WHITE_TERRACOTTA)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(notUnderwater, SurfaceRules.sequence(SurfaceRules.ifTrue(frozenOcean, SurfaceRules.ifTrue(hole, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveWater, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER))), biomeSurfaceRule))), SurfaceRules.ifTrue(notUnderDeepWater, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(frozenOcean, SurfaceRules.ifTrue(hole, WATER))), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, biomeUnderSurfaceRule), SurfaceRules.ifTrue(biomesWithSandAndSandstone, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE)), SurfaceRules.ifTrue(biomesWithSandAndVeryDeepSandstone, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SANDSTONE)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), sandOrSandstoneIfCeiling), gravelOrStoneIfCeiling)));
      ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
      if (bedrockRoof) {
         builder.add(SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK));
      }

      if (bedrockFloor) {
         builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
      }

      SurfaceRules.RuleSource ruleAbovePreliminarySurface = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), mainRuleCloseToSurface);
      builder.add(doPreliminarySurfaceCheck ? ruleAbovePreliminarySurface : mainRuleCloseToSurface);
      builder.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SULFUR_CAVES), SurfaceRules.noiseGradient(Noises.SULFUR_CAVE_GRADIENT, List.of(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(CINNABAR), Optional.of(SULFUR), Optional.of(CINNABAR), Optional.of(CINNABAR), Optional.of(CINNABAR)))));
      builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
      return SurfaceRules.sequence((SurfaceRules.RuleSource[])builder.build().toArray((x$0) -> new SurfaceRules.RuleSource[x$0]));
   }

   public static SurfaceRules.RuleSource nether() {
      SurfaceRules.ConditionSource aboveNetherLavaLevel = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
      SurfaceRules.ConditionSource aboveNetherLavaSurface = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
      SurfaceRules.ConditionSource netherBandAroundLavaLevelBottom = SurfaceRules.yStartCheck(VerticalAnchor.absolute(30), 0);
      SurfaceRules.ConditionSource netherBandAroundLavaLevelTop = SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(35), 0));
      SurfaceRules.ConditionSource closeToCeiling = SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
      SurfaceRules.ConditionSource hole = SurfaceRules.hole();
      SurfaceRules.ConditionSource soulSandLayer = SurfaceRules.noiseCondition(Noises.SOUL_SAND_LAYER, -0.012);
      SurfaceRules.ConditionSource gravelLayer = SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.012);
      SurfaceRules.ConditionSource patch = SurfaceRules.noiseCondition(Noises.PATCH, -0.012);
      SurfaceRules.ConditionSource netherrack = SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.54);
      SurfaceRules.ConditionSource netherWart = SurfaceRules.noiseCondition(Noises.NETHER_WART, 1.17);
      SurfaceRules.ConditionSource netherStateSelector = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0);
      SurfaceRules.RuleSource gravelPatch = SurfaceRules.ifTrue(patch, SurfaceRules.ifTrue(netherBandAroundLavaLevelBottom, SurfaceRules.ifTrue(netherBandAroundLavaLevelTop, GRAVEL)));
      return SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK), SurfaceRules.ifTrue(closeToCeiling, NETHERRACK), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BASALT_DELTAS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, BASALT), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(gravelPatch, SurfaceRules.ifTrue(netherStateSelector, BASALT), BLACKSTONE)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SOUL_SAND_VALLEY), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, SurfaceRules.sequence(SurfaceRules.ifTrue(netherStateSelector, SOUL_SAND), SOUL_SOIL)), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(gravelPatch, SurfaceRules.ifTrue(netherStateSelector, SOUL_SAND), SOUL_SOIL)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(aboveNetherLavaSurface), SurfaceRules.ifTrue(hole, LAVA)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARPED_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(netherrack), SurfaceRules.ifTrue(aboveNetherLavaLevel, SurfaceRules.sequence(SurfaceRules.ifTrue(netherWart, WARPED_WART_BLOCK), WARPED_NYLIUM)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.CRIMSON_FOREST), SurfaceRules.ifTrue(SurfaceRules.not(netherrack), SurfaceRules.ifTrue(aboveNetherLavaLevel, SurfaceRules.sequence(SurfaceRules.ifTrue(netherWart, NETHER_WART_BLOCK), CRIMSON_NYLIUM)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.NETHER_WASTES), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(soulSandLayer, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.not(hole), SurfaceRules.ifTrue(netherBandAroundLavaLevelBottom, SurfaceRules.ifTrue(netherBandAroundLavaLevelTop, SOUL_SAND))), NETHERRACK))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(aboveNetherLavaLevel, SurfaceRules.ifTrue(netherBandAroundLavaLevelTop, SurfaceRules.ifTrue(gravelLayer, SurfaceRules.sequence(SurfaceRules.ifTrue(aboveNetherLavaSurface, GRAVEL), SurfaceRules.ifTrue(SurfaceRules.not(hole), GRAVEL)))))))), NETHERRACK);
   }

   public static SurfaceRules.RuleSource end() {
      return ENDSTONE;
   }

   public static SurfaceRules.RuleSource air() {
      return AIR;
   }

   private static SurfaceRules.ConditionSource surfaceNoiseAbove(final double threshold) {
      return SurfaceRules.noiseCondition(Noises.SURFACE, threshold / 8.25, 1.7976931348623157E308);
   }

   static {
      AIR = makeStateRule(Blocks.AIR);
      BEDROCK = makeStateRule(Blocks.BEDROCK);
      WHITE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.white());
      ORANGE_TERRACOTTA = makeStateRule(Blocks.DYED_TERRACOTTA.orange());
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
      MUD = makeStateRule(Blocks.MUD);
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
      CINNABAR = Blocks.CINNABAR.defaultBlockState();
      SULFUR = Blocks.SULFUR.defaultBlockState();
   }
}
