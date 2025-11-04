package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.FloatModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class OverworldBiomes {
   protected static final int NORMAL_WATER_COLOR = 4159204;
   private static final int DARK_DRY_FOLIAGE_COLOR = 8082228;
   public static final int SWAMP_SKELETON_WEIGHT = 70;

   public OverworldBiomes() {
      super();
   }

   public static int calculateSkyColor(float var0) {
      float var1 = var0 / 3.0F;
      var1 = Mth.clamp(var1, -1.0F, 1.0F);
      return ARGB.opaque(Mth.hsvToRgb(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F));
   }

   private static Biome.BiomeBuilder baseBiome(float var0, float var1) {
      return (new Biome.BiomeBuilder()).hasPrecipitation(true).temperature(var0).downfall(var1).setAttribute(EnvironmentAttributes.SKY_COLOR, calculateSkyColor(var0)).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).build());
   }

   private static void globalOverworldGeneration(BiomeGenerationSettings.Builder var0) {
      BiomeDefaultFeatures.addDefaultCarversAndLakes(var0);
      BiomeDefaultFeatures.addDefaultCrystalFormations(var0);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var0);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var0);
      BiomeDefaultFeatures.addDefaultSprings(var0);
      BiomeDefaultFeatures.addSurfaceFreezing(var0);
   }

   public static Biome oldGrowthTaiga(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      var3.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 4));
      var3.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3));
      var3.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.FOX, 2, 4));
      if (var2) {
         BiomeDefaultFeatures.commonSpawns(var3);
      } else {
         BiomeDefaultFeatures.caveSpawns(var3);
         BiomeDefaultFeatures.monsters(var3, 100, 25, 0, 100, false);
      }

      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var4);
      BiomeDefaultFeatures.addMossyStoneBlock(var4);
      BiomeDefaultFeatures.addFerns(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var2 ? VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA);
      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addGiantTaigaVegetation(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4, true);
      BiomeDefaultFeatures.addCommonBerryBushes(var4);
      return baseBiome(var2 ? 0.25F : 0.3F, 0.8F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome sparseJungle(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var2);
      var2.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 2, 4));
      return baseJungle(var0, var1, 0.8F, false, true, false).mobSpawnSettings(var2.build()).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_SPARSE_JUNGLE)).build();
   }

   public static Biome jungle(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var2);
      var2.addSpawn(MobCategory.CREATURE, 40, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 1, 2)).addSpawn(MobCategory.MONSTER, 2, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 1, 3)).addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 2));
      return baseJungle(var0, var1, 0.9F, false, false, true).mobSpawnSettings(var2.build()).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_JUNGLE)).setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true).build();
   }

   public static Biome bambooJungle(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var2);
      var2.addSpawn(MobCategory.CREATURE, 40, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 1, 2)).addSpawn(MobCategory.CREATURE, 80, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 2)).addSpawn(MobCategory.MONSTER, 2, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 1, 1));
      return baseJungle(var0, var1, 0.9F, true, false, true).mobSpawnSettings(var2.build()).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_BAMBOO_JUNGLE)).setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true).build();
   }

   private static Biome.BiomeBuilder baseJungle(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, float var2, boolean var3, boolean var4, boolean var5) {
      BiomeGenerationSettings.Builder var6 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var6);
      BiomeDefaultFeatures.addDefaultOres(var6);
      BiomeDefaultFeatures.addDefaultSoftDisks(var6);
      if (var3) {
         BiomeDefaultFeatures.addBambooVegetation(var6);
      } else {
         if (var5) {
            BiomeDefaultFeatures.addLightBambooVegetation(var6);
         }

         if (var4) {
            BiomeDefaultFeatures.addSparseJungleTrees(var6);
         } else {
            BiomeDefaultFeatures.addJungleTrees(var6);
         }
      }

      BiomeDefaultFeatures.addWarmFlowers(var6);
      BiomeDefaultFeatures.addJungleGrass(var6);
      BiomeDefaultFeatures.addDefaultMushrooms(var6);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var6, true);
      BiomeDefaultFeatures.addJungleVines(var6);
      if (var4) {
         BiomeDefaultFeatures.addSparseJungleMelons(var6);
      } else {
         BiomeDefaultFeatures.addJungleMelons(var6);
      }

      return baseBiome(0.95F, var2).generationSettings(var6.build());
   }

   public static Biome windsweptHills(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      var3.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 4, 6));
      BiomeDefaultFeatures.commonSpawns(var3);
      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      if (var2) {
         BiomeDefaultFeatures.addMountainForestTrees(var4);
      } else {
         BiomeDefaultFeatures.addMountainTrees(var4);
      }

      BiomeDefaultFeatures.addBushes(var4);
      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addDefaultGrass(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4, true);
      BiomeDefaultFeatures.addExtraEmeralds(var4);
      BiomeDefaultFeatures.addInfestedStone(var4);
      return baseBiome(0.2F, 0.3F).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome desert(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.desertSpawns(var2);
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      BiomeDefaultFeatures.addFossilDecoration(var3);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addDefaultFlowers(var3);
      BiomeDefaultFeatures.addDefaultGrass(var3);
      BiomeDefaultFeatures.addDesertVegetation(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDesertExtraVegetation(var3);
      BiomeDefaultFeatures.addDesertExtraDecoration(var3);
      return baseBiome(2.0F, 0.0F).hasPrecipitation(false).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_DESERT)).setAttribute(EnvironmentAttributes.SNOW_GOLEM_MELTS, true).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   public static Biome plains(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2, boolean var3, boolean var4) {
      MobSpawnSettings.Builder var5 = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder var6 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var6);
      if (var3) {
         var5.creatureGenerationProbability(0.07F);
         BiomeDefaultFeatures.snowySpawns(var5, !var4);
         if (var4) {
            var6.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_SPIKE);
            var6.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_PATCH);
         }
      } else {
         BiomeDefaultFeatures.plainsSpawns(var5);
         BiomeDefaultFeatures.addPlainGrass(var6);
         if (var2) {
            var6.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUNFLOWER);
         } else {
            BiomeDefaultFeatures.addBushes(var6);
         }
      }

      BiomeDefaultFeatures.addDefaultOres(var6);
      BiomeDefaultFeatures.addDefaultSoftDisks(var6);
      if (var3) {
         BiomeDefaultFeatures.addSnowyTrees(var6);
         BiomeDefaultFeatures.addDefaultFlowers(var6);
         BiomeDefaultFeatures.addDefaultGrass(var6);
      } else {
         BiomeDefaultFeatures.addPlainVegetation(var6);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var6);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var6, true);
      return baseBiome(var3 ? 0.0F : 0.8F, var3 ? 0.5F : 0.4F).mobSpawnSettings(var5.build()).generationSettings(var6.build()).build();
   }

   public static Biome mushroomFields(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.mooshroomSpawns(var2);
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addMushroomFieldVegetation(var3);
      BiomeDefaultFeatures.addNearWaterVegetation(var3);
      return baseBiome(0.9F, 1.0F).setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true).setAttribute(EnvironmentAttributes.CAN_PILLAGER_PATROL_SPAWN, false).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   public static Biome savanna(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2, boolean var3) {
      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var4);
      if (!var2) {
         BiomeDefaultFeatures.addSavannaGrass(var4);
      }

      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      if (var2) {
         BiomeDefaultFeatures.addShatteredSavannaTrees(var4);
         BiomeDefaultFeatures.addDefaultFlowers(var4);
         BiomeDefaultFeatures.addShatteredSavannaGrass(var4);
      } else {
         BiomeDefaultFeatures.addSavannaTrees(var4);
         BiomeDefaultFeatures.addWarmFlowers(var4);
         BiomeDefaultFeatures.addSavannaExtraGrass(var4);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4, true);
      MobSpawnSettings.Builder var5 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var5);
      var5.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 2, 6)).addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1)).addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 2, 3));
      BiomeDefaultFeatures.commonSpawnWithZombieHorse(var5);
      if (var3) {
         var5.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 4, 4));
         var5.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 8));
      }

      return baseBiome(2.0F, 0.0F).hasPrecipitation(false).setAttribute(EnvironmentAttributes.SNOW_GOLEM_MELTS, true).mobSpawnSettings(var5.build()).generationSettings(var4.build()).build();
   }

   public static Biome badlands(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      BiomeDefaultFeatures.commonSpawns(var3);
      var3.addSpawn(MobCategory.CREATURE, 6, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 1, 2));
      var3.creatureGenerationProbability(0.03F);
      if (var2) {
         var3.addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 8));
         var3.creatureGenerationProbability(0.04F);
      }

      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addExtraGold(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      if (var2) {
         BiomeDefaultFeatures.addBadlandsTrees(var4);
      }

      BiomeDefaultFeatures.addBadlandGrass(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addBadlandExtraVegetation(var4);
      return baseBiome(2.0F, 0.0F).hasPrecipitation(false).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_BADLANDS)).setAttribute(EnvironmentAttributes.SNOW_GOLEM_MELTS, true).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).foliageColorOverride(10387789).grassColorOverride(9470285).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   private static Biome.BiomeBuilder baseOcean() {
      return baseBiome(0.5F, 0.5F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD.withUnderwater(Musics.UNDER_WATER));
   }

   private static BiomeGenerationSettings.Builder baseOceanGeneration(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      BiomeDefaultFeatures.addWaterTrees(var2);
      BiomeDefaultFeatures.addDefaultFlowers(var2);
      BiomeDefaultFeatures.addDefaultGrass(var2);
      BiomeDefaultFeatures.addDefaultMushrooms(var2);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var2, true);
      return var2;
   }

   public static Biome coldOcean(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(var3, 3, 4, 15);
      var3.addSpawn(MobCategory.WATER_AMBIENT, 15, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 1, 5));
      var3.addSpawn(MobCategory.WATER_CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.NAUTILUS, 1, 1));
      BiomeGenerationSettings.Builder var4 = baseOceanGeneration(var0, var1);
      var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var2 ? AquaticPlacements.SEAGRASS_DEEP_COLD : AquaticPlacements.SEAGRASS_COLD);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(var4);
      return baseOcean().specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4020182).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome ocean(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(var3, 1, 4, 10);
      var3.addSpawn(MobCategory.WATER_CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 2)).addSpawn(MobCategory.WATER_CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.NAUTILUS, 1, 1));
      BiomeGenerationSettings.Builder var4 = baseOceanGeneration(var0, var1);
      var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var2 ? AquaticPlacements.SEAGRASS_DEEP : AquaticPlacements.SEAGRASS_NORMAL);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(var4);
      return baseOcean().mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome lukeWarmOcean(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      if (var2) {
         BiomeDefaultFeatures.oceanSpawns(var3, 8, 4, 8);
      } else {
         BiomeDefaultFeatures.oceanSpawns(var3, 10, 2, 15);
      }

      var3.addSpawn(MobCategory.WATER_AMBIENT, 5, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 1, 3)).addSpawn(MobCategory.WATER_AMBIENT, 25, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 8, 8)).addSpawn(MobCategory.WATER_CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 2)).addSpawn(MobCategory.WATER_CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.NAUTILUS, 1, 1));
      BiomeGenerationSettings.Builder var4 = baseOceanGeneration(var0, var1);
      var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var2 ? AquaticPlacements.SEAGRASS_DEEP_WARM : AquaticPlacements.SEAGRASS_WARM);
      BiomeDefaultFeatures.addLukeWarmKelp(var4);
      return baseOcean().setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, -16509389).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4566514).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome warmOcean(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_AMBIENT, 15, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 1, 3)).addSpawn(MobCategory.WATER_CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.NAUTILUS, 1, 1));
      BiomeDefaultFeatures.warmOceanSpawns(var2, 10, 4);
      BiomeGenerationSettings.Builder var3 = baseOceanGeneration(var0, var1).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);
      return baseOcean().setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, -16507085).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4445678).build()).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   public static Biome frozenOcean(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, 15, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 1, 5)).addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 2)).addSpawn(MobCategory.WATER_CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.NAUTILUS, 1, 1));
      BiomeDefaultFeatures.commonSpawns(var3);
      var3.addSpawn(MobCategory.MONSTER, 5, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 1, 1));
      float var4 = var2 ? 0.5F : 0.0F;
      BiomeGenerationSettings.Builder var5 = new BiomeGenerationSettings.Builder(var0, var1);
      BiomeDefaultFeatures.addIcebergs(var5);
      globalOverworldGeneration(var5);
      BiomeDefaultFeatures.addBlueIce(var5);
      BiomeDefaultFeatures.addDefaultOres(var5);
      BiomeDefaultFeatures.addDefaultSoftDisks(var5);
      BiomeDefaultFeatures.addWaterTrees(var5);
      BiomeDefaultFeatures.addDefaultFlowers(var5);
      BiomeDefaultFeatures.addDefaultGrass(var5);
      BiomeDefaultFeatures.addDefaultMushrooms(var5);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var5, true);
      return baseBiome(var4, 0.5F).temperatureAdjustment(Biome.TemperatureModifier.FROZEN).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3750089).build()).mobSpawnSettings(var3.build()).generationSettings(var5.build()).build();
   }

   public static Biome forest(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2, boolean var3, boolean var4) {
      BiomeGenerationSettings.Builder var5 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var5);
      BackgroundMusic var6;
      if (var4) {
         var6 = new BackgroundMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST);
         var5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
      } else {
         var6 = new BackgroundMusic(SoundEvents.MUSIC_BIOME_FOREST);
         BiomeDefaultFeatures.addForestFlowers(var5);
      }

      BiomeDefaultFeatures.addDefaultOres(var5);
      BiomeDefaultFeatures.addDefaultSoftDisks(var5);
      if (var4) {
         var5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_FLOWER_FOREST);
         var5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);
         BiomeDefaultFeatures.addDefaultGrass(var5);
      } else {
         if (var2) {
            BiomeDefaultFeatures.addBirchForestFlowers(var5);
            if (var3) {
               BiomeDefaultFeatures.addTallBirchTrees(var5);
            } else {
               BiomeDefaultFeatures.addBirchTrees(var5);
            }
         } else {
            BiomeDefaultFeatures.addOtherBirchTrees(var5);
         }

         BiomeDefaultFeatures.addBushes(var5);
         BiomeDefaultFeatures.addDefaultFlowers(var5);
         BiomeDefaultFeatures.addForestGrass(var5);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var5);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var5, true);
      MobSpawnSettings.Builder var7 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var7);
      BiomeDefaultFeatures.commonSpawns(var7);
      if (var4) {
         var7.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3));
      } else if (!var2) {
         var7.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 4));
      }

      return baseBiome(var2 ? 0.6F : 0.7F, var2 ? 0.6F : 0.8F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, var6).mobSpawnSettings(var7.build()).generationSettings(var5.build()).build();
   }

   public static Biome taiga(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      var3.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 4)).addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3)).addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.FOX, 2, 4));
      BiomeDefaultFeatures.commonSpawns(var3);
      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var4);
      BiomeDefaultFeatures.addFerns(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      BiomeDefaultFeatures.addTaigaTrees(var4);
      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addTaigaGrass(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4, true);
      if (var2) {
         BiomeDefaultFeatures.addRareBerryBushes(var4);
      } else {
         BiomeDefaultFeatures.addCommonBerryBushes(var4);
      }

      int var5 = var2 ? 4020182 : 4159204;
      return baseBiome(var2 ? -0.5F : 0.25F, var2 ? 0.4F : 0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var5).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome darkForest(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      if (!var2) {
         BiomeDefaultFeatures.farmAnimals(var3);
      }

      BiomeDefaultFeatures.commonSpawns(var3);
      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var4);
      var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var2 ? VegetationPlacements.PALE_GARDEN_VEGETATION : VegetationPlacements.DARK_FOREST_VEGETATION);
      if (!var2) {
         BiomeDefaultFeatures.addForestFlowers(var4);
      } else {
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PALE_MOSS_PATCH);
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PALE_GARDEN_FLOWERS);
      }

      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      if (!var2) {
         BiomeDefaultFeatures.addDefaultFlowers(var4);
      } else {
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PALE_GARDEN);
      }

      BiomeDefaultFeatures.addForestGrass(var4);
      if (!var2) {
         BiomeDefaultFeatures.addDefaultMushrooms(var4);
         BiomeDefaultFeatures.addLeafLitterPatch(var4);
      }

      BiomeDefaultFeatures.addDefaultExtraVegetation(var4, true);
      EnvironmentAttributeMap var5 = EnvironmentAttributeMap.builder().set(EnvironmentAttributes.SKY_COLOR, -4605511).set(EnvironmentAttributes.FOG_COLOR, -8292496).set(EnvironmentAttributes.WATER_FOG_COLOR, -11179648).set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.EMPTY).set(EnvironmentAttributes.MUSIC_VOLUME, 0.0F).build();
      EnvironmentAttributeMap var6 = EnvironmentAttributeMap.builder().set(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_FOREST)).build();
      return baseBiome(0.7F, 0.8F).putAttributes(var2 ? var5 : var6).specialEffects(var2 ? (new BiomeSpecialEffects.Builder()).waterColor(7768221).grassColorOverride(7832178).foliageColorOverride(8883574).dryFoliageColorOverride(10528412).build() : (new BiomeSpecialEffects.Builder()).waterColor(4159204).dryFoliageColorOverride(8082228).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome swamp(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var2);
      BiomeDefaultFeatures.swampSpawns(var2, 70);
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      BiomeDefaultFeatures.addFossilDecoration(var3);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addSwampClayDisk(var3);
      BiomeDefaultFeatures.addSwampVegetation(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addSwampExtraVegetation(var3);
      var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
      return baseBiome(0.8F, 0.9F).setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, -14474473).modifyAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, FloatModifier.MULTIPLY, 0.85F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_SWAMP)).setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(6388580).foliageColorOverride(6975545).dryFoliageColorOverride(8082228).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).build()).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   public static Biome mangroveSwamp(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.swampSpawns(var2, 70);
      var2.addSpawn(MobCategory.WATER_AMBIENT, 25, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 8, 8));
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      BiomeDefaultFeatures.addFossilDecoration(var3);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addMangroveSwampDisks(var3);
      BiomeDefaultFeatures.addMangroveSwampVegetation(var3);
      BiomeDefaultFeatures.addMangroveSwampExtraVegetation(var3);
      return baseBiome(0.8F, 0.9F).setAttribute(EnvironmentAttributes.FOG_COLOR, -4138753).setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, -11699616).modifyAttribute(EnvironmentAttributes.WATER_FOG_END_DISTANCE, FloatModifier.MULTIPLY, 0.85F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_SWAMP)).setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3832426).foliageColorOverride(9285927).dryFoliageColorOverride(8082228).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).build()).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   public static Biome river(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      MobSpawnSettings.Builder var3 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, 5, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 1, 5));
      BiomeDefaultFeatures.commonSpawns(var3);
      var3.addSpawn(MobCategory.MONSTER, var2 ? 1 : 100, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 1, 1));
      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      BiomeDefaultFeatures.addWaterTrees(var4);
      BiomeDefaultFeatures.addBushes(var4);
      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addDefaultGrass(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4, true);
      if (!var2) {
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
      }

      return baseBiome(var2 ? 0.0F : 0.5F, 0.5F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD.withUnderwater(Musics.UNDER_WATER)).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var2 ? 3750089 : 4159204).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome beach(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2, boolean var3) {
      MobSpawnSettings.Builder var4 = new MobSpawnSettings.Builder();
      boolean var5 = !var3 && !var2;
      if (var5) {
         var4.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 2, 5));
      }

      BiomeDefaultFeatures.commonSpawns(var4);
      BiomeGenerationSettings.Builder var6 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var6);
      BiomeDefaultFeatures.addDefaultOres(var6);
      BiomeDefaultFeatures.addDefaultSoftDisks(var6);
      BiomeDefaultFeatures.addDefaultFlowers(var6);
      BiomeDefaultFeatures.addDefaultGrass(var6);
      BiomeDefaultFeatures.addDefaultMushrooms(var6);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var6, true);
      float var7;
      if (var2) {
         var7 = 0.05F;
      } else if (var3) {
         var7 = 0.2F;
      } else {
         var7 = 0.8F;
      }

      int var8 = var2 ? 4020182 : 4159204;
      return baseBiome(var7, var5 ? 0.4F : 0.3F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var8).build()).mobSpawnSettings(var4.build()).generationSettings(var6.build()).build();
   }

   public static Biome theVoid(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder(var0, var1);
      var2.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.VOID_START_PLATFORM);
      return baseBiome(0.5F, 0.5F).hasPrecipitation(false).mobSpawnSettings((new MobSpawnSettings.Builder()).build()).generationSettings(var2.build()).build();
   }

   public static Biome meadowOrCherryGrove(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1, boolean var2) {
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      MobSpawnSettings.Builder var4 = new MobSpawnSettings.Builder();
      var4.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(var2 ? EntityType.PIG : EntityType.DONKEY, 1, 2)).addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 6)).addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 4));
      BiomeDefaultFeatures.commonSpawns(var4);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addPlainGrass(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      if (var2) {
         BiomeDefaultFeatures.addCherryGroveVegetation(var3);
      } else {
         BiomeDefaultFeatures.addMeadowVegetation(var3);
      }

      BiomeDefaultFeatures.addExtraEmeralds(var3);
      BiomeDefaultFeatures.addInfestedStone(var3);
      if (var2) {
         BiomeSpecialEffects.Builder var5 = (new BiomeSpecialEffects.Builder()).waterColor(6141935).grassColorOverride(11983713).foliageColorOverride(11983713);
         return baseBiome(0.5F, 0.8F).setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, -10635281).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE)).specialEffects(var5.build()).mobSpawnSettings(var4.build()).generationSettings(var3.build()).build();
      } else {
         return baseBiome(0.5F, 0.8F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_MEADOW)).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(937679).build()).mobSpawnSettings(var4.build()).generationSettings(var3.build()).build();
      }
   }

   private static Biome.BiomeBuilder basePeaks(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder(var0, var1);
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      var3.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3));
      BiomeDefaultFeatures.commonSpawns(var3);
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addFrozenSprings(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      BiomeDefaultFeatures.addExtraEmeralds(var2);
      BiomeDefaultFeatures.addInfestedStone(var2);
      return baseBiome(-0.7F, 0.9F).setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true).mobSpawnSettings(var3.build()).generationSettings(var2.build());
   }

   public static Biome frozenPeaks(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      return basePeaks(var0, var1).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS)).build();
   }

   public static Biome jaggedPeaks(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      return basePeaks(var0, var1).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS)).build();
   }

   public static Biome stonyPeaks(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder(var0, var1);
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(var3);
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      BiomeDefaultFeatures.addExtraEmeralds(var2);
      BiomeDefaultFeatures.addInfestedStone(var2);
      return baseBiome(1.0F, 0.3F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS)).mobSpawnSettings(var3.build()).generationSettings(var2.build()).build();
   }

   public static Biome snowySlopes(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder(var0, var1);
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      var3.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3)).addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3));
      BiomeDefaultFeatures.commonSpawns(var3);
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addFrozenSprings(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var2, false);
      BiomeDefaultFeatures.addExtraEmeralds(var2);
      BiomeDefaultFeatures.addInfestedStone(var2);
      return baseBiome(-0.3F, 0.9F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES)).setAttribute(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, true).mobSpawnSettings(var3.build()).generationSettings(var2.build()).build();
   }

   public static Biome grove(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder(var0, var1);
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      var3.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 1, 1)).addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3)).addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.FOX, 2, 4));
      BiomeDefaultFeatures.commonSpawns(var3);
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addFrozenSprings(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      BiomeDefaultFeatures.addGroveTrees(var2);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var2, false);
      BiomeDefaultFeatures.addExtraEmeralds(var2);
      BiomeDefaultFeatures.addInfestedStone(var2);
      return baseBiome(-0.2F, 0.8F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_GROVE)).mobSpawnSettings(var3.build()).generationSettings(var2.build()).build();
   }

   public static Biome lushCaves(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      var2.addSpawn(MobCategory.AXOLOTLS, 10, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 4, 6));
      var2.addSpawn(MobCategory.WATER_AMBIENT, 25, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 8, 8));
      BiomeDefaultFeatures.commonSpawns(var2);
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addPlainGrass(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addLushCavesSpecialOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addLushCavesVegetationFeatures(var3);
      return baseBiome(0.5F, 0.5F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES)).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   public static Biome dripstoneCaves(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.dripstoneCavesSpawns(var2);
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addPlainGrass(var3);
      BiomeDefaultFeatures.addDefaultOres(var3, true);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addPlainVegetation(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3, false);
      BiomeDefaultFeatures.addDripstone(var3);
      return baseBiome(0.8F, 0.4F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES)).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   public static Biome deepDark(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      var3.addCarver(Carvers.CAVE);
      var3.addCarver(Carvers.CAVE_EXTRA_UNDERGROUND);
      var3.addCarver(Carvers.CANYON);
      BiomeDefaultFeatures.addDefaultCrystalFormations(var3);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var3);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var3);
      BiomeDefaultFeatures.addSurfaceFreezing(var3);
      BiomeDefaultFeatures.addPlainGrass(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addPlainVegetation(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3, false);
      BiomeDefaultFeatures.addSculk(var3);
      return baseBiome(0.8F, 0.4F).setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK)).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }
}
