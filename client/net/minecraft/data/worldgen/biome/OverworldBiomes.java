package net.minecraft.data.worldgen.biome;

import javax.annotation.Nullable;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class OverworldBiomes {
   protected static final int NORMAL_WATER_COLOR = 4159204;
   protected static final int NORMAL_WATER_FOG_COLOR = 329011;
   private static final int OVERWORLD_FOG_COLOR = 12638463;
   @Nullable
   private static final Music NORMAL_MUSIC = null;

   public OverworldBiomes() {
      super();
   }

   protected static int calculateSkyColor(float var0) {
      float var1 = var0 / 3.0F;
      var1 = Mth.clamp(var1, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F);
   }

   private static Biome biome(Biome.Precipitation var0, float var1, float var2, MobSpawnSettings.Builder var3, BiomeGenerationSettings.Builder var4, @Nullable Music var5) {
      return biome(var0, var1, var2, 4159204, 329011, var3, var4, var5);
   }

   private static Biome biome(Biome.Precipitation var0, float var1, float var2, int var3, int var4, MobSpawnSettings.Builder var5, BiomeGenerationSettings.Builder var6, @Nullable Music var7) {
      return (new Biome.BiomeBuilder()).precipitation(var0).temperature(var1).downfall(var2).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var3).waterFogColor(var4).fogColor(12638463).skyColor(calculateSkyColor(var1)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(var7).build()).mobSpawnSettings(var5.build()).generationSettings(var6.build()).build();
   }

   private static void globalOverworldGeneration(BiomeGenerationSettings.Builder var0) {
      BiomeDefaultFeatures.addDefaultCarversAndLakes(var0);
      BiomeDefaultFeatures.addDefaultCrystalFormations(var0);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var0);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var0);
      BiomeDefaultFeatures.addDefaultSprings(var0);
      BiomeDefaultFeatures.addSurfaceFreezing(var0);
   }

   public static Biome oldGrowthTaiga(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var1);
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4));
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      if (var0) {
         BiomeDefaultFeatures.commonSpawns(var1);
      } else {
         BiomeDefaultFeatures.caveSpawns(var1);
         BiomeDefaultFeatures.monsters(var1, 100, 25, 100, false);
      }

      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addMossyStoneBlock(var2);
      BiomeDefaultFeatures.addFerns(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var0 ? VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA);
      BiomeDefaultFeatures.addDefaultFlowers(var2);
      BiomeDefaultFeatures.addGiantTaigaVegetation(var2);
      BiomeDefaultFeatures.addDefaultMushrooms(var2);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var2);
      BiomeDefaultFeatures.addCommonBerryBushes(var2);
      Music var3 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA);
      return biome(Biome.Precipitation.RAIN, var0 ? 0.25F : 0.3F, 0.8F, var1, var2, var3);
   }

   public static Biome sparseJungle() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var0);
      return baseJungle(0.8F, false, true, false, var0);
   }

   public static Biome jungle() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var0);
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 1, 2));
      return baseJungle(0.9F, false, false, true, var0);
   }

   public static Biome bambooJungle() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var0);
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 80, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 1));
      return baseJungle(0.9F, true, false, true, var0);
   }

   private static Biome baseJungle(float var0, boolean var1, boolean var2, boolean var3, MobSpawnSettings.Builder var4) {
      BiomeGenerationSettings.Builder var5 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var5);
      BiomeDefaultFeatures.addDefaultOres(var5);
      BiomeDefaultFeatures.addDefaultSoftDisks(var5);
      if (var1) {
         BiomeDefaultFeatures.addBambooVegetation(var5);
      } else {
         if (var3) {
            BiomeDefaultFeatures.addLightBambooVegetation(var5);
         }

         if (var2) {
            BiomeDefaultFeatures.addSparseJungleTrees(var5);
         } else {
            BiomeDefaultFeatures.addJungleTrees(var5);
         }
      }

      BiomeDefaultFeatures.addWarmFlowers(var5);
      BiomeDefaultFeatures.addJungleGrass(var5);
      BiomeDefaultFeatures.addDefaultMushrooms(var5);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var5);
      BiomeDefaultFeatures.addJungleVines(var5);
      if (var2) {
         BiomeDefaultFeatures.addSparseJungleMelons(var5);
      } else {
         BiomeDefaultFeatures.addJungleMelons(var5);
      }

      Music var6 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE_AND_FOREST);
      return biome(Biome.Precipitation.RAIN, 0.95F, var0, var4, var5, var6);
   }

   public static Biome windsweptHills(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var1);
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 5, 4, 6));
      BiomeDefaultFeatures.commonSpawns(var1);
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      if (var0) {
         BiomeDefaultFeatures.addMountainForestTrees(var2);
      } else {
         BiomeDefaultFeatures.addMountainTrees(var2);
      }

      BiomeDefaultFeatures.addDefaultFlowers(var2);
      BiomeDefaultFeatures.addDefaultGrass(var2);
      BiomeDefaultFeatures.addDefaultMushrooms(var2);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var2);
      BiomeDefaultFeatures.addExtraEmeralds(var2);
      BiomeDefaultFeatures.addInfestedStone(var2);
      return biome(Biome.Precipitation.RAIN, 0.2F, 0.3F, var1, var2, NORMAL_MUSIC);
   }

   public static Biome desert() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.desertSpawns(var0);
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      BiomeDefaultFeatures.addFossilDecoration(var1);
      globalOverworldGeneration(var1);
      BiomeDefaultFeatures.addDefaultOres(var1);
      BiomeDefaultFeatures.addDefaultSoftDisks(var1);
      BiomeDefaultFeatures.addDefaultFlowers(var1);
      BiomeDefaultFeatures.addDefaultGrass(var1);
      BiomeDefaultFeatures.addDesertVegetation(var1);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      BiomeDefaultFeatures.addDesertExtraVegetation(var1);
      BiomeDefaultFeatures.addDesertExtraDecoration(var1);
      return biome(Biome.Precipitation.NONE, 2.0F, 0.0F, var0, var1, NORMAL_MUSIC);
   }

   public static Biome plains(boolean var0, boolean var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var4);
      if (var1) {
         var3.creatureGenerationProbability(0.07F);
         BiomeDefaultFeatures.snowySpawns(var3);
         if (var2) {
            var4.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_SPIKE);
            var4.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_PATCH);
         }
      } else {
         BiomeDefaultFeatures.plainsSpawns(var3);
         BiomeDefaultFeatures.addPlainGrass(var4);
         if (var0) {
            var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUNFLOWER);
         }
      }

      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      if (var1) {
         BiomeDefaultFeatures.addSnowyTrees(var4);
         BiomeDefaultFeatures.addDefaultFlowers(var4);
         BiomeDefaultFeatures.addDefaultGrass(var4);
      } else {
         BiomeDefaultFeatures.addPlainVegetation(var4);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      if (var0) {
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE);
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
      } else {
         BiomeDefaultFeatures.addDefaultExtraVegetation(var4);
      }

      float var5 = var1 ? 0.0F : 0.8F;
      return biome(var1 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, var5, var1 ? 0.5F : 0.4F, var3, var4, NORMAL_MUSIC);
   }

   public static Biome mushroomFields() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.mooshroomSpawns(var0);
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var1);
      BiomeDefaultFeatures.addDefaultOres(var1);
      BiomeDefaultFeatures.addDefaultSoftDisks(var1);
      BiomeDefaultFeatures.addMushroomFieldVegetation(var1);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var1);
      return biome(Biome.Precipitation.RAIN, 0.9F, 1.0F, var0, var1, NORMAL_MUSIC);
   }

   public static Biome savanna(boolean var0, boolean var1) {
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var2);
      if (!var0) {
         BiomeDefaultFeatures.addSavannaGrass(var2);
      }

      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      if (var0) {
         BiomeDefaultFeatures.addShatteredSavannaTrees(var2);
         BiomeDefaultFeatures.addDefaultFlowers(var2);
         BiomeDefaultFeatures.addShatteredSavannaGrass(var2);
      } else {
         BiomeDefaultFeatures.addSavannaTrees(var2);
         BiomeDefaultFeatures.addWarmFlowers(var2);
         BiomeDefaultFeatures.addSavannaExtraGrass(var2);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var2);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var2);
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      var3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 1));
      BiomeDefaultFeatures.commonSpawns(var3);
      if (var1) {
         var3.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 8, 4, 4));
      }

      return biome(Biome.Precipitation.NONE, 2.0F, 0.0F, var3, var2, NORMAL_MUSIC);
   }

   public static Biome badlands(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(var1);
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addExtraGold(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      if (var0) {
         BiomeDefaultFeatures.addBadlandsTrees(var2);
      }

      BiomeDefaultFeatures.addBadlandGrass(var2);
      BiomeDefaultFeatures.addDefaultMushrooms(var2);
      BiomeDefaultFeatures.addBadlandExtraVegetation(var2);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(2.0F)).foliageColorOverride(10387789).grassColorOverride(9470285).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var1.build()).generationSettings(var2.build()).build();
   }

   private static Biome baseOcean(MobSpawnSettings.Builder var0, int var1, int var2, BiomeGenerationSettings.Builder var3) {
      return biome(Biome.Precipitation.RAIN, 0.5F, 0.5F, var1, var2, var0, var3, NORMAL_MUSIC);
   }

   private static BiomeGenerationSettings.Builder baseOceanGeneration() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var0);
      BiomeDefaultFeatures.addDefaultOres(var0);
      BiomeDefaultFeatures.addDefaultSoftDisks(var0);
      BiomeDefaultFeatures.addWaterTrees(var0);
      BiomeDefaultFeatures.addDefaultFlowers(var0);
      BiomeDefaultFeatures.addDefaultGrass(var0);
      BiomeDefaultFeatures.addDefaultMushrooms(var0);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var0);
      return var0;
   }

   public static Biome coldOcean(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(var1, 3, 4, 15);
      var1.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5));
      BiomeGenerationSettings.Builder var2 = baseOceanGeneration();
      var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var0 ? AquaticPlacements.SEAGRASS_DEEP_COLD : AquaticPlacements.SEAGRASS_COLD);
      BiomeDefaultFeatures.addDefaultSeagrass(var2);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(var2);
      return baseOcean(var1, 4020182, 329011, var2);
   }

   public static Biome ocean(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(var1, 1, 4, 10);
      var1.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 1, 2));
      BiomeGenerationSettings.Builder var2 = baseOceanGeneration();
      var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var0 ? AquaticPlacements.SEAGRASS_DEEP : AquaticPlacements.SEAGRASS_NORMAL);
      BiomeDefaultFeatures.addDefaultSeagrass(var2);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(var2);
      return baseOcean(var1, 4159204, 329011, var2);
   }

   public static Biome lukeWarmOcean(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      if (var0) {
         BiomeDefaultFeatures.oceanSpawns(var1, 8, 4, 8);
      } else {
         BiomeDefaultFeatures.oceanSpawns(var1, 10, 2, 15);
      }

      var1.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 5, 1, 3)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8)).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
      BiomeGenerationSettings.Builder var2 = baseOceanGeneration();
      var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var0 ? AquaticPlacements.SEAGRASS_DEEP_WARM : AquaticPlacements.SEAGRASS_WARM);
      if (var0) {
         BiomeDefaultFeatures.addDefaultSeagrass(var2);
      }

      BiomeDefaultFeatures.addLukeWarmKelp(var2);
      return baseOcean(var1, 4566514, 267827, var2);
   }

   public static Biome warmOcean() {
      MobSpawnSettings.Builder var0 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 15, 1, 3));
      BiomeDefaultFeatures.warmOceanSpawns(var0, 10, 4);
      BiomeGenerationSettings.Builder var1 = baseOceanGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);
      return baseOcean(var0, 4445678, 270131, var1);
   }

   public static Biome frozenOcean(boolean var0) {
      MobSpawnSettings.Builder var1 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
      BiomeDefaultFeatures.commonSpawns(var1);
      var1.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
      float var2 = var0 ? 0.5F : 0.0F;
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder();
      BiomeDefaultFeatures.addIcebergs(var3);
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addBlueIce(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addWaterTrees(var3);
      BiomeDefaultFeatures.addDefaultFlowers(var3);
      BiomeDefaultFeatures.addDefaultGrass(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3);
      return (new Biome.BiomeBuilder()).precipitation(var0 ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW).temperature(var2).temperatureAdjustment(Biome.TemperatureModifier.FROZEN).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(var2)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var1.build()).generationSettings(var3.build()).build();
   }

   public static Biome forest(boolean var0, boolean var1, boolean var2) {
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var3);
      if (var2) {
         var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
      } else {
         BiomeDefaultFeatures.addForestFlowers(var3);
      }

      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      if (var2) {
         var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_FLOWER_FOREST);
         var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);
         BiomeDefaultFeatures.addDefaultGrass(var3);
      } else {
         if (var0) {
            if (var1) {
               BiomeDefaultFeatures.addTallBirchTrees(var3);
            } else {
               BiomeDefaultFeatures.addBirchTrees(var3);
            }
         } else {
            BiomeDefaultFeatures.addOtherBirchTrees(var3);
         }

         BiomeDefaultFeatures.addDefaultFlowers(var3);
         BiomeDefaultFeatures.addForestGrass(var3);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3);
      MobSpawnSettings.Builder var4 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var4);
      BiomeDefaultFeatures.commonSpawns(var4);
      if (var2) {
         var4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      } else if (!var0) {
         var4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
      }

      float var5 = var0 ? 0.6F : 0.7F;
      Music var6 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE_AND_FOREST);
      return biome(Biome.Precipitation.RAIN, var5, var0 ? 0.6F : 0.8F, var4, var3, var6);
   }

   public static Biome taiga(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var1);
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      BiomeDefaultFeatures.commonSpawns(var1);
      float var2 = var0 ? -0.5F : 0.25F;
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var3);
      BiomeDefaultFeatures.addFerns(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addTaigaTrees(var3);
      BiomeDefaultFeatures.addDefaultFlowers(var3);
      BiomeDefaultFeatures.addTaigaGrass(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3);
      if (var0) {
         BiomeDefaultFeatures.addRareBerryBushes(var3);
      } else {
         BiomeDefaultFeatures.addCommonBerryBushes(var3);
      }

      return biome(var0 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, var2, var0 ? 0.4F : 0.8F, var0 ? 4020182 : 4159204, 329011, var1, var3, NORMAL_MUSIC);
   }

   public static Biome darkForest() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var0);
      BiomeDefaultFeatures.commonSpawns(var0);
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var1);
      var1.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
      BiomeDefaultFeatures.addForestFlowers(var1);
      BiomeDefaultFeatures.addDefaultOres(var1);
      BiomeDefaultFeatures.addDefaultSoftDisks(var1);
      BiomeDefaultFeatures.addDefaultFlowers(var1);
      BiomeDefaultFeatures.addForestGrass(var1);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var1);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE_AND_FOREST);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).temperature(0.7F).downfall(0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.7F)).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(var2).build()).mobSpawnSettings(var0.build()).generationSettings(var1.build()).build();
   }

   public static Biome swamp() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var0);
      BiomeDefaultFeatures.commonSpawns(var0);
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 10, 2, 5));
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      BiomeDefaultFeatures.addFossilDecoration(var1);
      globalOverworldGeneration(var1);
      BiomeDefaultFeatures.addDefaultOres(var1);
      BiomeDefaultFeatures.addSwampClayDisk(var1);
      BiomeDefaultFeatures.addSwampVegetation(var1);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      BiomeDefaultFeatures.addSwampExtraVegetation(var1);
      var1.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).temperature(0.8F).downfall(0.9F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(6388580).waterFogColor(2302743).fogColor(12638463).skyColor(calculateSkyColor(0.8F)).foliageColorOverride(6975545).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(var2).build()).mobSpawnSettings(var0.build()).generationSettings(var1.build()).build();
   }

   public static Biome mangroveSwamp() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(var0);
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 10, 2, 5));
      var0.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      BiomeDefaultFeatures.addFossilDecoration(var1);
      globalOverworldGeneration(var1);
      BiomeDefaultFeatures.addDefaultOres(var1);
      BiomeDefaultFeatures.addMangroveSwampDisks(var1);
      BiomeDefaultFeatures.addMangroveSwampVegetation(var1);
      var1.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).temperature(0.8F).downfall(0.9F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3832426).waterFogColor(5077600).fogColor(12638463).skyColor(calculateSkyColor(0.8F)).foliageColorOverride(9285927).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(var2).build()).mobSpawnSettings(var0.build()).generationSettings(var1.build()).build();
   }

   public static Biome river(boolean var0) {
      MobSpawnSettings.Builder var1 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 2, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 5, 1, 5));
      BiomeDefaultFeatures.commonSpawns(var1);
      var1.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, var0 ? 1 : 100, 1, 1));
      BiomeGenerationSettings.Builder var2 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      BiomeDefaultFeatures.addWaterTrees(var2);
      BiomeDefaultFeatures.addDefaultFlowers(var2);
      BiomeDefaultFeatures.addDefaultGrass(var2);
      BiomeDefaultFeatures.addDefaultMushrooms(var2);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var2);
      if (!var0) {
         var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
      }

      float var3 = var0 ? 0.0F : 0.5F;
      return biome(var0 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, var3, 0.5F, var0 ? 3750089 : 4159204, 329011, var1, var2, NORMAL_MUSIC);
   }

   public static Biome beach(boolean var0, boolean var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      boolean var3 = !var1 && !var0;
      if (var3) {
         var2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 5, 2, 5));
      }

      BiomeDefaultFeatures.commonSpawns(var2);
      BiomeGenerationSettings.Builder var4 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addDefaultGrass(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4);
      float var5;
      if (var0) {
         var5 = 0.05F;
      } else if (var1) {
         var5 = 0.2F;
      } else {
         var5 = 0.8F;
      }

      return biome(var0 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, var5, var3 ? 0.4F : 0.3F, var0 ? 4020182 : 4159204, 329011, var2, var4, NORMAL_MUSIC);
   }

   public static Biome theVoid() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      var0.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.VOID_START_PLATFORM);
      return biome(Biome.Precipitation.NONE, 0.5F, 0.5F, new MobSpawnSettings.Builder(), var0, NORMAL_MUSIC);
   }

   public static Biome meadow() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 2, 4));
      BiomeDefaultFeatures.commonSpawns(var1);
      globalOverworldGeneration(var0);
      BiomeDefaultFeatures.addPlainGrass(var0);
      BiomeDefaultFeatures.addDefaultOres(var0);
      BiomeDefaultFeatures.addDefaultSoftDisks(var0);
      BiomeDefaultFeatures.addMeadowVegetation(var0);
      BiomeDefaultFeatures.addExtraEmeralds(var0);
      BiomeDefaultFeatures.addInfestedStone(var0);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW);
      return biome(Biome.Precipitation.RAIN, 0.5F, 0.8F, 937679, 329011, var1, var0, var2);
   }

   public static Biome frozenPeaks() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(var1);
      globalOverworldGeneration(var0);
      BiomeDefaultFeatures.addFrozenSprings(var0);
      BiomeDefaultFeatures.addDefaultOres(var0);
      BiomeDefaultFeatures.addDefaultSoftDisks(var0);
      BiomeDefaultFeatures.addExtraEmeralds(var0);
      BiomeDefaultFeatures.addInfestedStone(var0);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS);
      return biome(Biome.Precipitation.SNOW, -0.7F, 0.9F, var1, var0, var2);
   }

   public static Biome jaggedPeaks() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(var1);
      globalOverworldGeneration(var0);
      BiomeDefaultFeatures.addFrozenSprings(var0);
      BiomeDefaultFeatures.addDefaultOres(var0);
      BiomeDefaultFeatures.addDefaultSoftDisks(var0);
      BiomeDefaultFeatures.addExtraEmeralds(var0);
      BiomeDefaultFeatures.addInfestedStone(var0);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS);
      return biome(Biome.Precipitation.SNOW, -0.7F, 0.9F, var1, var0, var2);
   }

   public static Biome stonyPeaks() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(var1);
      globalOverworldGeneration(var0);
      BiomeDefaultFeatures.addDefaultOres(var0);
      BiomeDefaultFeatures.addDefaultSoftDisks(var0);
      BiomeDefaultFeatures.addExtraEmeralds(var0);
      BiomeDefaultFeatures.addInfestedStone(var0);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS);
      return biome(Biome.Precipitation.RAIN, 1.0F, 0.3F, var1, var0, var2);
   }

   public static Biome snowySlopes() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(var1);
      globalOverworldGeneration(var0);
      BiomeDefaultFeatures.addFrozenSprings(var0);
      BiomeDefaultFeatures.addDefaultOres(var0);
      BiomeDefaultFeatures.addDefaultSoftDisks(var0);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var0);
      BiomeDefaultFeatures.addExtraEmeralds(var0);
      BiomeDefaultFeatures.addInfestedStone(var0);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES);
      return biome(Biome.Precipitation.SNOW, -0.3F, 0.9F, var1, var0, var2);
   }

   public static Biome grove() {
      BiomeGenerationSettings.Builder var0 = new BiomeGenerationSettings.Builder();
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var1);
      var1.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      BiomeDefaultFeatures.commonSpawns(var1);
      globalOverworldGeneration(var0);
      BiomeDefaultFeatures.addFrozenSprings(var0);
      BiomeDefaultFeatures.addDefaultOres(var0);
      BiomeDefaultFeatures.addDefaultSoftDisks(var0);
      BiomeDefaultFeatures.addGroveTrees(var0);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var0);
      BiomeDefaultFeatures.addExtraEmeralds(var0);
      BiomeDefaultFeatures.addInfestedStone(var0);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE);
      return biome(Biome.Precipitation.SNOW, -0.2F, 0.8F, var1, var0, var2);
   }

   public static Biome lushCaves() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      var0.addSpawn(MobCategory.AXOLOTLS, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 10, 4, 6));
      var0.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
      BiomeDefaultFeatures.commonSpawns(var0);
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var1);
      BiomeDefaultFeatures.addPlainGrass(var1);
      BiomeDefaultFeatures.addDefaultOres(var1);
      BiomeDefaultFeatures.addLushCavesSpecialOres(var1);
      BiomeDefaultFeatures.addDefaultSoftDisks(var1);
      BiomeDefaultFeatures.addLushCavesVegetationFeatures(var1);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
      return biome(Biome.Precipitation.RAIN, 0.5F, 0.5F, var0, var1, var2);
   }

   public static Biome dripstoneCaves() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.dripstoneCavesSpawns(var0);
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      globalOverworldGeneration(var1);
      BiomeDefaultFeatures.addPlainGrass(var1);
      BiomeDefaultFeatures.addDefaultOres(var1, true);
      BiomeDefaultFeatures.addDefaultSoftDisks(var1);
      BiomeDefaultFeatures.addPlainVegetation(var1);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var1);
      BiomeDefaultFeatures.addDripstone(var1);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
      return biome(Biome.Precipitation.RAIN, 0.8F, 0.4F, var0, var1, var2);
   }

   public static Biome deepDark() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder var1 = new BiomeGenerationSettings.Builder();
      var1.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
      var1.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
      var1.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
      BiomeDefaultFeatures.addDefaultCrystalFormations(var1);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var1);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var1);
      BiomeDefaultFeatures.addSurfaceFreezing(var1);
      BiomeDefaultFeatures.addPlainGrass(var1);
      BiomeDefaultFeatures.addDefaultOres(var1, true);
      BiomeDefaultFeatures.addDefaultSoftDisks(var1);
      BiomeDefaultFeatures.addPlainVegetation(var1);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var1);
      BiomeDefaultFeatures.addSculk(var1);
      Music var2 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK);
      return biome(Biome.Precipitation.RAIN, 0.8F, 0.4F, var0, var1, var2);
   }
}
