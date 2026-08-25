package net.minecraft.data.worldgen.material;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.OreVeinRule;

public class OverworldMaterialRules {
   public static final ResourceKey<MaterialRule> OVERWORLD = createKey("overworld");
   public static final ResourceKey<MaterialRule> OVERWORLD_CAVES = createKey("overworld_caves");
   public static final ResourceKey<MaterialRule> OVERWORLD_FLOATING_ISLANDS = createKey("overworld_floating_islands");
   private static final ResourceKey<MaterialRule> SURFACE = createKey("overworld/surface");
   private static final ResourceKey<MaterialRule> UNDERGROUND = createKey("overworld/underground");
   private static final ResourceKey<MaterialRule> SULFUR_CAVE_BANDS = createKey("overworld/sulfur_cave_bands");
   private static final ResourceKey<MaterialRule> COPPER_ORE_VEIN = createKey("overworld/copper_ore_vein");
   private static final ResourceKey<MaterialRule> IRON_ORE_VEIN = createKey("overworld/iron_ore_vein");
   private static final ResourceKey<MaterialRule> BIOME_SURFACE = createKey("overworld/biome_surface");
   private static final ResourceKey<MaterialRule> DAPPLED_FOREST_SURFACE = createKey("overworld/biome_surface/dappled_forest");
   private static final ResourceKey<MaterialRule> DEFAULT_BIOME_SURFACE = createKey("overworld/biome_surface/default");
   private static final ResourceKey<MaterialRule> DRIPSTONE_CAVES_SURFACE = createKey("overworld/biome_surface/dripstone_caves");
   private static final ResourceKey<MaterialRule> FROZEN_PEAKS_SURFACE = createKey("overworld/biome_surface/frozen_peaks");
   private static final ResourceKey<MaterialRule> GROVE_SURFACE = createKey("overworld/biome_surface/grove");
   private static final ResourceKey<MaterialRule> ICE_SPIKES_SURFACE = createKey("overworld/biome_surface/ice_spikes");
   private static final ResourceKey<MaterialRule> JAGGED_PEAKS_SURFACE = createKey("overworld/biome_surface/jagged_peaks");
   private static final ResourceKey<MaterialRule> MANGROVE_SWAMP_SURFACE = createKey("overworld/biome_surface/mangrove_swamp");
   private static final ResourceKey<MaterialRule> MUSHROOM_FIELDS_SURFACE = createKey("overworld/biome_surface/mushroom_fields");
   private static final ResourceKey<MaterialRule> OLD_GROWTH_PINE_TAIGA_SURFACE = createKey("overworld/biome_surface/old_growth_pine_taiga");
   private static final ResourceKey<MaterialRule> SNOWY_SLOPES_SURFACE = createKey("overworld/biome_surface/snowy_slopes");
   private static final ResourceKey<MaterialRule> STONY_PEAKS_SURFACE = createKey("overworld/biome_surface/stony_peaks");
   private static final ResourceKey<MaterialRule> STONY_SHORE_SURFACE = createKey("overworld/biome_surface/stony_shore");
   private static final ResourceKey<MaterialRule> SULFUR_CAVES_SURFACE = createKey("overworld/biome_surface/sulfur_caves");
   private static final ResourceKey<MaterialRule> WINDSWEPT_GRAVELLY_HILLS_SURFACE = createKey("overworld/biome_surface/windswept_gravelly_hills");
   private static final ResourceKey<MaterialRule> WINDSWEPT_HILLS_SURFACE = createKey("overworld/biome_surface/windswept_hills");
   private static final ResourceKey<MaterialRule> WINDSWEPT_SAVANNA_SURFACE = createKey("overworld/biome_surface/windswept_savanna");
   private static final ResourceKey<MaterialRule> UNDER_BIOME_SURFACE = createKey("overworld/under_biome_surface");
   private static final ResourceKey<MaterialRule> DEFAULT_UNDER_BIOME_SURFACE = createKey("overworld/under_biome_surface/default");
   private static final ResourceKey<MaterialRule> FROZEN_PEAKS_UNDER_SURFACE = createKey("overworld/under_biome_surface/frozen_peaks");
   private static final ResourceKey<MaterialRule> GROVE_UNDER_SURFACE = createKey("overworld/under_biome_surface/grove");
   private static final ResourceKey<MaterialRule> JAGGED_PEAKS_UNDER_SURFACE = createKey("overworld/under_biome_surface/jagged_peaks");
   private static final ResourceKey<MaterialRule> SNOWY_SLOPES_UNDER_SURFACE = createKey("overworld/under_biome_surface/snowy_slopes");
   private static final ResourceKey<MaterialRule> WINDSWEPT_GRAVELLY_HILLS_UNDER_SURFACE = createKey("overworld/under_biome_surface/windswept_gravelly_hills");
   private static final ResourceKey<MaterialRule> WINDSWEPT_SAVANNA_UNDER_SURFACE = createKey("overworld/under_biome_surface/windswept_savanna");
   private static final ResourceKey<MaterialRule> SAND_OR_SANDSTONE_IF_CEILING = createKey("overworld/sand_or_sandstone_if_ceiling");
   private static final ResourceKey<MaterialRule> GRAVEL_OR_STONE_IF_CEILING = createKey("overworld/gravel_or_stone_if_ceiling");
   private static final ResourceKey<MaterialRule> POWDER_SNOW_SURFACE = createKey("overworld/powder_snow_surface");
   private static final ResourceKey<MaterialRule> POWDER_SNOW_UNDER_SURFACE = createKey("overworld/powder_snow_under_surface");
   private static final MaterialRule AIR;
   private static final MaterialRule WHITE_TERRACOTTA;
   private static final MaterialRule ORANGE_TERRACOTTA;
   private static final MaterialRule TERRACOTTA;
   private static final MaterialRule RED_SAND;
   private static final MaterialRule RED_SANDSTONE;
   private static final MaterialRule STONE;
   private static final MaterialRule DEEPSLATE;
   private static final MaterialRule DIRT;
   private static final MaterialRule PODZOL;
   private static final MaterialRule COARSE_DIRT;
   private static final MaterialRule MYCELIUM;
   private static final MaterialRule GRASS_BLOCK;
   private static final MaterialRule CALCITE;
   private static final MaterialRule GRAVEL;
   private static final MaterialRule SAND;
   private static final MaterialRule SANDSTONE;
   private static final MaterialRule PACKED_ICE;
   private static final MaterialRule SNOW_BLOCK;
   private static final MaterialRule MUD;
   private static final MaterialRule POWDER_SNOW;
   private static final MaterialRule ICE;
   private static final MaterialRule WATER;
   private static final MaterialRule CINNABAR;
   private static final MaterialRule SULFUR;

   public OverworldMaterialRules() {
      super();
   }

   private static ResourceKey<MaterialRule> createKey(final String name) {
      return ResourceKey.create(Registries.MATERIAL_RULE, Identifier.withDefaultNamespace(name));
   }

   private static MaterialRule makeStateRule(final Block block) {
      return MaterialRules.state(block.defaultBlockState());
   }

   public static void bootstrap(final BootstrapContext<MaterialRule> context) {
      HolderGetter<MaterialRule> rules = context.lookup(Registries.MATERIAL_RULE);
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      MaterialRule sulfurCaveBands = MaterialRules.registerAndWrap(context, SULFUR_CAVE_BANDS, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.noiseCondition3d(Noises.SULFUR_CAVE_GRADIENT, -0.4000000059604645, -0.10000000149011612), CINNABAR), MaterialRules.ifTrue(MaterialRules.noiseCondition3d(Noises.SULFUR_CAVE_GRADIENT, 0.0, 0.4000000059604645), SULFUR), MaterialRules.ifTrue(MaterialRules.noiseCondition3d(Noises.SULFUR_CAVE_GRADIENT, 0.4000000059604645), CINNABAR)));
      MaterialRule underground = MaterialRules.registerAndWrap(context, UNDERGROUND, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.SULFUR_CAVES), sulfurCaveBands), MaterialRules.ifTrue(MaterialRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE)));
      List<MaterialRule> oreVeins = registerOreVeins(context);
      MaterialRule surface = registerSurface(context, sulfurCaveBands);
      context.register(OVERWORLD, createOverworldLike(rules, true, false, true, surface, underground, oreVeins));
      context.register(OVERWORLD_CAVES, createOverworldLike(rules, false, true, true, surface, underground, oreVeins));
      context.register(OVERWORLD_FLOATING_ISLANDS, createOverworldLike(rules, false, false, false, surface, underground, oreVeins));
   }

   private static MaterialRule registerSurface(final BootstrapContext<MaterialRule> context, final MaterialRule sulfurCaveBands) {
      HolderGetter<MaterialCondition> conditions = context.lookup(Registries.MATERIAL_CONDITION);
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      MaterialCondition woodedBadlandsTop = MaterialRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
      MaterialCondition badlandsTop = MaterialRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
      MaterialCondition badlandsHeightCondition = MaterialRules.yStartCheck(VerticalAnchor.absolute(63), -1);
      MaterialCondition badlandsMid = MaterialRules.yStartCheck(VerticalAnchor.absolute(74), 1);
      MaterialCondition mangroveSwampPuddleLevel = MaterialRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
      MaterialCondition swampPuddleLevel = MaterialRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
      MaterialCondition aboveOverworldSeaLevel = MaterialRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
      MaterialCondition onFloor = MaterialRules.getCondition(conditions, VanillaMaterialConditions.ON_FLOOR);
      MaterialCondition onCeiling = MaterialRules.getCondition(conditions, VanillaMaterialConditions.ON_CEILING);
      MaterialCondition underFloor = MaterialRules.getCondition(conditions, VanillaMaterialConditions.UNDER_FLOOR);
      MaterialCondition notUnderwater = MaterialRules.getCondition(conditions, VanillaMaterialConditions.NOT_UNDERWATER);
      MaterialCondition notUnderDeepWater = MaterialRules.getCondition(conditions, VanillaMaterialConditions.NOT_UNDER_DEEP_WATER);
      MaterialCondition hole = MaterialRules.hole();
      MaterialCondition frozenOcean = MaterialRules.isBiome(biomes, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
      MaterialCondition steep = MaterialRules.steep();
      MaterialRule defaultBiomeSurface = MaterialRules.registerAndWrap(context, DEFAULT_BIOME_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(notUnderwater, GRASS_BLOCK), DIRT));
      MaterialRule sandOrSandstoneIfCeiling = MaterialRules.registerAndWrap(context, SAND_OR_SANDSTONE_IF_CEILING, MaterialRules.sequence(MaterialRules.ifTrue(onCeiling, SANDSTONE), SAND));
      MaterialRule gravelOrStoneIfCeiling = MaterialRules.registerAndWrap(context, GRAVEL_OR_STONE_IF_CEILING, MaterialRules.sequence(MaterialRules.ifTrue(onCeiling, STONE), GRAVEL));
      MaterialCondition biomesWithSandAndSandstone = MaterialRules.isBiome(biomes, Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
      MaterialCondition biomesWithSandAndVeryDeepSandstone = MaterialRules.isBiome(biomes, Biomes.DESERT);
      MaterialRule commonSurfaceAndUnderRules = MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.STONY_PEAKS), MaterialRules.registerAndWrap(context, STONY_PEAKS_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.STONY_SHORE), MaterialRules.registerAndWrap(context, STONY_SHORE_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.GRAVEL, -0.05, 0.05), gravelOrStoneIfCeiling), STONE))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_HILLS), MaterialRules.registerAndWrap(context, WINDSWEPT_HILLS_SURFACE, MaterialRules.ifTrue(surfaceNoiseAbove(1.0), STONE))), MaterialRules.ifTrue(biomesWithSandAndSandstone, sandOrSandstoneIfCeiling), MaterialRules.ifTrue(biomesWithSandAndVeryDeepSandstone, sandOrSandstoneIfCeiling), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.DRIPSTONE_CAVES), MaterialRules.registerAndWrap(context, DRIPSTONE_CAVES_SURFACE, STONE)), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.SULFUR_CAVES), MaterialRules.registerAndWrap(context, SULFUR_CAVES_SURFACE, MaterialRules.sequence(sulfurCaveBands, STONE))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.MANGROVE_SWAMP), MaterialRules.registerAndWrap(context, MANGROVE_SWAMP_SURFACE, MUD)));
      MaterialRule powderSnowUnderRule = MaterialRules.registerAndWrap(context, POWDER_SNOW_UNDER_SURFACE, MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.POWDER_SNOW, 0.45, 0.58), MaterialRules.ifTrue(notUnderwater, POWDER_SNOW)));
      MaterialRule powderSnowSurfaceRule = MaterialRules.registerAndWrap(context, POWDER_SNOW_SURFACE, MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.POWDER_SNOW, 0.35, 0.6), MaterialRules.ifTrue(notUnderwater, POWDER_SNOW)));
      MaterialRule underBiomeSurfaceRule = MaterialRules.registerAndWrap(context, UNDER_BIOME_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.FROZEN_PEAKS), MaterialRules.registerAndWrap(context, FROZEN_PEAKS_UNDER_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(steep, PACKED_ICE), MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE), MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.ICE, -0.0625, 0.025), ICE), MaterialRules.ifTrue(notUnderwater, SNOW_BLOCK)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.SNOWY_SLOPES), MaterialRules.registerAndWrap(context, SNOWY_SLOPES_UNDER_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(steep, STONE), powderSnowUnderRule, MaterialRules.ifTrue(notUnderwater, SNOW_BLOCK)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.JAGGED_PEAKS), MaterialRules.registerAndWrap(context, JAGGED_PEAKS_UNDER_SURFACE, STONE)), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.GROVE), MaterialRules.registerAndWrap(context, GROVE_UNDER_SURFACE, MaterialRules.sequence(powderSnowUnderRule, DIRT))), commonSurfaceAndUnderRules, MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_SAVANNA), MaterialRules.registerAndWrap(context, WINDSWEPT_SAVANNA_UNDER_SURFACE, MaterialRules.ifTrue(surfaceNoiseAbove(1.75), STONE))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_GRAVELLY_HILLS), MaterialRules.registerAndWrap(context, WINDSWEPT_GRAVELLY_HILLS_UNDER_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(surfaceNoiseAbove(2.0), gravelOrStoneIfCeiling), MaterialRules.ifTrue(surfaceNoiseAbove(1.0), STONE), MaterialRules.ifTrue(surfaceNoiseAbove(-1.0), DIRT), gravelOrStoneIfCeiling))), MaterialRules.registerAndWrap(context, DEFAULT_UNDER_BIOME_SURFACE, DIRT)));
      MaterialRule biomeSurfaceRule = MaterialRules.registerAndWrap(context, BIOME_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.FROZEN_PEAKS), MaterialRules.registerAndWrap(context, FROZEN_PEAKS_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(steep, PACKED_ICE), MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE), MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.ICE, 0.0, 0.025), ICE), MaterialRules.ifTrue(notUnderwater, SNOW_BLOCK)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.SNOWY_SLOPES), MaterialRules.registerAndWrap(context, SNOWY_SLOPES_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(steep, STONE), powderSnowSurfaceRule, MaterialRules.ifTrue(notUnderwater, SNOW_BLOCK)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.JAGGED_PEAKS), MaterialRules.registerAndWrap(context, JAGGED_PEAKS_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(steep, STONE), MaterialRules.ifTrue(notUnderwater, SNOW_BLOCK)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.GROVE), MaterialRules.registerAndWrap(context, GROVE_SURFACE, MaterialRules.sequence(powderSnowSurfaceRule, MaterialRules.ifTrue(notUnderwater, SNOW_BLOCK)))), commonSurfaceAndUnderRules, MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_SAVANNA), MaterialRules.registerAndWrap(context, WINDSWEPT_SAVANNA_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(surfaceNoiseAbove(1.75), STONE), MaterialRules.ifTrue(surfaceNoiseAbove(-0.5), COARSE_DIRT)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WINDSWEPT_GRAVELLY_HILLS), MaterialRules.registerAndWrap(context, WINDSWEPT_GRAVELLY_HILLS_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(surfaceNoiseAbove(2.0), gravelOrStoneIfCeiling), MaterialRules.ifTrue(surfaceNoiseAbove(1.0), STONE), MaterialRules.ifTrue(surfaceNoiseAbove(-1.0), defaultBiomeSurface), gravelOrStoneIfCeiling))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), MaterialRules.registerAndWrap(context, OLD_GROWTH_PINE_TAIGA_SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(surfaceNoiseAbove(1.75), COARSE_DIRT), MaterialRules.ifTrue(surfaceNoiseAbove(-0.95), PODZOL)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.ICE_SPIKES), MaterialRules.registerAndWrap(context, ICE_SPIKES_SURFACE, MaterialRules.ifTrue(notUnderwater, SNOW_BLOCK))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.MUSHROOM_FIELDS), MaterialRules.registerAndWrap(context, MUSHROOM_FIELDS_SURFACE, MYCELIUM)), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.DAPPLED_FOREST), MaterialRules.registerAndWrap(context, DAPPLED_FOREST_SURFACE, MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.SMALL_PATCH, 1.2000000476837158), COARSE_DIRT))), defaultBiomeSurface));
      MaterialCondition clayBand1 = MaterialRules.noiseCondition2d(Noises.SURFACE, -0.909, -0.5454);
      MaterialCondition clayBand2 = MaterialRules.noiseCondition2d(Noises.SURFACE, -0.1818, 0.1818);
      MaterialCondition clayBand3 = MaterialRules.noiseCondition2d(Noises.SURFACE, 0.5454, 0.909);
      return MaterialRules.registerAndWrap(context, SURFACE, MaterialRules.sequence(MaterialRules.ifTrue(onFloor, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WOODED_BADLANDS), MaterialRules.ifTrue(woodedBadlandsTop, MaterialRules.sequence(MaterialRules.ifTrue(clayBand1, COARSE_DIRT), MaterialRules.ifTrue(clayBand2, COARSE_DIRT), MaterialRules.ifTrue(clayBand3, COARSE_DIRT), defaultBiomeSurface))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.SWAMP), MaterialRules.ifTrue(swampPuddleLevel, MaterialRules.ifTrue(MaterialRules.not(aboveOverworldSeaLevel), MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.SWAMP, 0.0), WATER)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.MANGROVE_SWAMP), MaterialRules.ifTrue(mangroveSwampPuddleLevel, MaterialRules.ifTrue(MaterialRules.not(aboveOverworldSeaLevel), MaterialRules.ifTrue(MaterialRules.noiseCondition2d(Noises.SWAMP, 0.0), WATER)))))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), MaterialRules.sequence(MaterialRules.ifTrue(onFloor, MaterialRules.sequence(MaterialRules.ifTrue(badlandsTop, ORANGE_TERRACOTTA), MaterialRules.ifTrue(badlandsMid, MaterialRules.sequence(MaterialRules.ifTrue(clayBand1, TERRACOTTA), MaterialRules.ifTrue(clayBand2, TERRACOTTA), MaterialRules.ifTrue(clayBand3, TERRACOTTA), MaterialRules.bandlands())), MaterialRules.ifTrue(notUnderwater, MaterialRules.sequence(MaterialRules.ifTrue(onCeiling, RED_SANDSTONE), RED_SAND)), MaterialRules.ifTrue(MaterialRules.not(hole), ORANGE_TERRACOTTA), MaterialRules.ifTrue(notUnderDeepWater, WHITE_TERRACOTTA), gravelOrStoneIfCeiling)), MaterialRules.ifTrue(badlandsHeightCondition, MaterialRules.sequence(MaterialRules.ifTrue(aboveOverworldSeaLevel, MaterialRules.ifTrue(MaterialRules.not(badlandsMid), ORANGE_TERRACOTTA)), MaterialRules.bandlands())), MaterialRules.ifTrue(underFloor, MaterialRules.ifTrue(notUnderDeepWater, WHITE_TERRACOTTA)))), MaterialRules.ifTrue(onFloor, MaterialRules.ifTrue(notUnderwater, MaterialRules.sequence(MaterialRules.ifTrue(frozenOcean, MaterialRules.ifTrue(hole, AIR)), biomeSurfaceRule))), MaterialRules.ifTrue(notUnderDeepWater, MaterialRules.sequence(MaterialRules.ifTrue(onFloor, MaterialRules.ifTrue(frozenOcean, MaterialRules.ifTrue(hole, WATER))), MaterialRules.ifTrue(underFloor, underBiomeSurfaceRule), MaterialRules.ifTrue(biomesWithSandAndSandstone, MaterialRules.ifTrue(MaterialRules.getCondition(conditions, VanillaMaterialConditions.DEEP_UNDER_FLOOR), SANDSTONE)), MaterialRules.ifTrue(biomesWithSandAndVeryDeepSandstone, MaterialRules.ifTrue(MaterialRules.getCondition(conditions, VanillaMaterialConditions.VERY_DEEP_UNDER_FLOOR), SANDSTONE)))), MaterialRules.ifTrue(onFloor, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), sandOrSandstoneIfCeiling), gravelOrStoneIfCeiling))));
   }

   private static MaterialRule createOverworldLike(final HolderGetter<MaterialRule> rules, final boolean doPreliminarySurfaceCheck, final boolean bedrockRoof, final boolean bedrockFloor, final MaterialRule mainRuleCloseToSurface, final MaterialRule underground, final List<MaterialRule> oreVeins) {
      ImmutableList.Builder<MaterialRule> builder = ImmutableList.builder();
      if (bedrockRoof) {
         builder.add(MaterialRules.getRule(rules, VanillaMaterialRules.BEDROCK_ROOF));
      }

      if (bedrockFloor) {
         builder.add(MaterialRules.getRule(rules, VanillaMaterialRules.BEDROCK_FLOOR));
      }

      builder.addAll(oreVeins);
      MaterialRule ruleAbovePreliminarySurface = MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), mainRuleCloseToSurface);
      builder.add(doPreliminarySurfaceCheck ? ruleAbovePreliminarySurface : mainRuleCloseToSurface);
      builder.add(underground);
      return MaterialRules.sequence((List)builder.build());
   }

   private static MaterialCondition surfaceNoiseAbove(final double threshold) {
      return MaterialRules.noiseCondition2d(Noises.SURFACE, threshold / 8.25, 1.7976931348623157E308);
   }

   private static List<MaterialRule> registerOreVeins(final BootstrapContext<MaterialRule> context) {
      HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
      DensityFunction richness = NoiseRouterData.getFunction(functions, NoiseRouterData.ORE_VEIN_RICHNESS);
      DensityFunction gap = NoiseRouterData.getFunction(functions, NoiseRouterData.ORE_VEIN_GAP);
      return List.of(MaterialRules.registerAndWrap(context, COPPER_ORE_VEIN, (MaterialRule)OreVeinRule.VeinType.COPPER.create(NoiseRouterData.getFunction(functions, NoiseRouterData.ORE_VEIN_COPPER_DENSITY), richness, gap)), MaterialRules.registerAndWrap(context, IRON_ORE_VEIN, (MaterialRule)OreVeinRule.VeinType.IRON.create(NoiseRouterData.getFunction(functions, NoiseRouterData.ORE_VEIN_IRON_DENSITY), richness, gap)));
   }

   static {
      AIR = makeStateRule(Blocks.AIR);
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
      CINNABAR = makeStateRule(Blocks.CINNABAR);
      SULFUR = makeStateRule(Blocks.SULFUR);
   }
}
