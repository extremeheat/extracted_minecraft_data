package net.minecraft.data.worldgen.material;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

public class NetherMaterialRules {
   public static final ResourceKey<MaterialRule> NETHER = createKey("nether");
   private static final MaterialRule LAVA;
   private static final MaterialRule NETHERRACK;
   private static final MaterialRule SOUL_SAND;
   private static final MaterialRule SOUL_SOIL;
   private static final MaterialRule BASALT;
   private static final MaterialRule BLACKSTONE;
   private static final MaterialRule WARPED_WART_BLOCK;
   private static final MaterialRule WARPED_NYLIUM;
   private static final MaterialRule NETHER_WART_BLOCK;
   private static final MaterialRule CRIMSON_NYLIUM;
   private static final MaterialRule GRAVEL;

   public NetherMaterialRules() {
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
      HolderGetter<MaterialCondition> conditions = context.lookup(Registries.MATERIAL_CONDITION);
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      MaterialCondition onFloor = MaterialRules.getCondition(conditions, VanillaMaterialConditions.ON_FLOOR);
      MaterialCondition underCeiling = MaterialRules.getCondition(conditions, VanillaMaterialConditions.UNDER_CEILING);
      MaterialCondition underFloor = MaterialRules.getCondition(conditions, VanillaMaterialConditions.UNDER_FLOOR);
      MaterialCondition aboveNetherLavaLevel = MaterialRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
      MaterialCondition aboveNetherLavaSurface = MaterialRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
      MaterialCondition netherBandAroundLavaLevelBottom = MaterialRules.yStartCheck(VerticalAnchor.absolute(30), 0);
      MaterialCondition netherBandAroundLavaLevelTop = MaterialRules.not(MaterialRules.yStartCheck(VerticalAnchor.absolute(35), 0));
      MaterialCondition closeToCeiling = MaterialRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
      MaterialCondition hole = MaterialRules.hole();
      MaterialCondition soulSandLayer = MaterialRules.noiseCondition2d(Noises.SOUL_SAND_LAYER, -0.012);
      MaterialCondition gravelLayer = MaterialRules.noiseCondition2d(Noises.GRAVEL_LAYER, -0.012);
      MaterialCondition patch = MaterialRules.noiseCondition2d(Noises.PATCH, -0.012);
      MaterialCondition netherrack = MaterialRules.noiseCondition2d(Noises.NETHERRACK, 0.54);
      MaterialCondition netherWart = MaterialRules.noiseCondition2d(Noises.NETHER_WART, 1.17);
      MaterialCondition netherStateSelector = MaterialRules.noiseCondition2d(Noises.NETHER_STATE_SELECTOR, 0.0);
      MaterialRule gravelPatch = MaterialRules.ifTrue(patch, MaterialRules.ifTrue(netherBandAroundLavaLevelBottom, MaterialRules.ifTrue(netherBandAroundLavaLevelTop, GRAVEL)));
      context.register(NETHER, MaterialRules.sequence(MaterialRules.getRule(rules, VanillaMaterialRules.BEDROCK_FLOOR), MaterialRules.getRule(rules, VanillaMaterialRules.BEDROCK_ROOF), MaterialRules.ifTrue(closeToCeiling, NETHERRACK), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.BASALT_DELTAS), MaterialRules.sequence(MaterialRules.ifTrue(underCeiling, BASALT), MaterialRules.ifTrue(underFloor, MaterialRules.sequence(gravelPatch, MaterialRules.ifTrue(netherStateSelector, BASALT), BLACKSTONE)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.SOUL_SAND_VALLEY), MaterialRules.sequence(MaterialRules.ifTrue(underCeiling, MaterialRules.sequence(MaterialRules.ifTrue(netherStateSelector, SOUL_SAND), SOUL_SOIL)), MaterialRules.ifTrue(underFloor, MaterialRules.sequence(gravelPatch, MaterialRules.ifTrue(netherStateSelector, SOUL_SAND), SOUL_SOIL)))), MaterialRules.ifTrue(onFloor, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.not(aboveNetherLavaSurface), MaterialRules.ifTrue(hole, LAVA)), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.WARPED_FOREST), MaterialRules.ifTrue(MaterialRules.not(netherrack), MaterialRules.ifTrue(aboveNetherLavaLevel, MaterialRules.sequence(MaterialRules.ifTrue(netherWart, WARPED_WART_BLOCK), WARPED_NYLIUM)))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.CRIMSON_FOREST), MaterialRules.ifTrue(MaterialRules.not(netherrack), MaterialRules.ifTrue(aboveNetherLavaLevel, MaterialRules.sequence(MaterialRules.ifTrue(netherWart, NETHER_WART_BLOCK), CRIMSON_NYLIUM)))))), MaterialRules.ifTrue(MaterialRules.isBiome(biomes, Biomes.NETHER_WASTES), MaterialRules.sequence(MaterialRules.ifTrue(underFloor, MaterialRules.ifTrue(soulSandLayer, MaterialRules.sequence(MaterialRules.ifTrue(MaterialRules.not(hole), MaterialRules.ifTrue(netherBandAroundLavaLevelBottom, MaterialRules.ifTrue(netherBandAroundLavaLevelTop, SOUL_SAND))), NETHERRACK))), MaterialRules.ifTrue(onFloor, MaterialRules.ifTrue(aboveNetherLavaLevel, MaterialRules.ifTrue(netherBandAroundLavaLevelTop, MaterialRules.ifTrue(gravelLayer, MaterialRules.sequence(MaterialRules.ifTrue(aboveNetherLavaSurface, GRAVEL), MaterialRules.ifTrue(MaterialRules.not(hole), GRAVEL)))))))), NETHERRACK));
   }

   static {
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
      GRAVEL = makeStateRule(Blocks.GRAVEL);
   }
}
