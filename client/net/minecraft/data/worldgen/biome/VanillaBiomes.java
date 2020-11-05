package net.minecraft.data.worldgen.biome;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

public class VanillaBiomes {
   private static int calculateSkyColor(float var0) {
      float var1 = var0 / 3.0F;
      var1 = Mth.clamp(var1, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F);
   }

   public static Biome giantTreeTaiga(float var0, float var1, float var2, boolean var3) {
      MobSpawnSettings.Builder var4 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var4);
      var4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4));
      var4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      var4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      if (var3) {
         BiomeDefaultFeatures.commonSpawns(var4);
      } else {
         BiomeDefaultFeatures.ambientSpawns(var4);
         BiomeDefaultFeatures.monsters(var4, 100, 25, 100);
      }

      BiomeGenerationSettings.Builder var5 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GIANT_TREE_TAIGA);
      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var5);
      var5.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var5);
      BiomeDefaultFeatures.addDefaultLakes(var5);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var5);
      BiomeDefaultFeatures.addMossyStoneBlock(var5);
      BiomeDefaultFeatures.addFerns(var5);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var5);
      BiomeDefaultFeatures.addDefaultOres(var5);
      BiomeDefaultFeatures.addDefaultSoftDisks(var5);
      var5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var3 ? Features.TREES_GIANT_SPRUCE : Features.TREES_GIANT);
      BiomeDefaultFeatures.addDefaultFlowers(var5);
      BiomeDefaultFeatures.addGiantTaigaVegetation(var5);
      BiomeDefaultFeatures.addDefaultMushrooms(var5);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var5);
      BiomeDefaultFeatures.addDefaultSprings(var5);
      BiomeDefaultFeatures.addSparseBerryBushes(var5);
      BiomeDefaultFeatures.addSurfaceFreezing(var5);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.TAIGA).depth(var0).scale(var1).temperature(var2).downfall(0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(var2)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var4.build()).generationSettings(var5.build()).build();
   }

   public static Biome birchForestBiome(float var0, float var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      BiomeDefaultFeatures.commonSpawns(var3);
      BiomeGenerationSettings.Builder var4 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var4);
      var4.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var4);
      BiomeDefaultFeatures.addDefaultLakes(var4);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var4);
      BiomeDefaultFeatures.addForestFlowers(var4);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      if (var2) {
         BiomeDefaultFeatures.addTallBirchTrees(var4);
      } else {
         BiomeDefaultFeatures.addBirchTrees(var4);
      }

      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addForestGrass(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4);
      BiomeDefaultFeatures.addDefaultSprings(var4);
      BiomeDefaultFeatures.addSurfaceFreezing(var4);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.FOREST).depth(var0).scale(var1).temperature(0.6F).downfall(0.6F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.6F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome jungleBiome() {
      return jungleBiome(0.1F, 0.2F, 40, 2, 3);
   }

   public static Biome jungleEdgeBiome() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var0);
      return baseJungleBiome(0.1F, 0.2F, 0.8F, false, true, false, var0);
   }

   public static Biome modifiedJungleEdgeBiome() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var0);
      return baseJungleBiome(0.2F, 0.4F, 0.8F, false, true, true, var0);
   }

   public static Biome modifiedJungleBiome() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var0);
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 10, 1, 1)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 1));
      return baseJungleBiome(0.2F, 0.4F, 0.9F, false, false, true, var0);
   }

   public static Biome jungleHillsBiome() {
      return jungleBiome(0.45F, 0.3F, 10, 1, 1);
   }

   public static Biome bambooJungleBiome() {
      return bambooJungleBiome(0.1F, 0.2F, 40, 2);
   }

   public static Biome bambooJungleHillsBiome() {
      return bambooJungleBiome(0.45F, 0.3F, 10, 1);
   }

   private static Biome jungleBiome(float var0, float var1, int var2, int var3, int var4) {
      MobSpawnSettings.Builder var5 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var5);
      var5.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, var2, 1, var3)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, var4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 1, 2));
      var5.setPlayerCanSpawn();
      return baseJungleBiome(var0, var1, 0.9F, false, false, false, var5);
   }

   private static Biome bambooJungleBiome(float var0, float var1, int var2, int var3) {
      MobSpawnSettings.Builder var4 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(var4);
      var4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, var2, 1, var3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 80, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 1));
      return baseJungleBiome(var0, var1, 0.9F, true, false, false, var4);
   }

   private static Biome baseJungleBiome(float var0, float var1, float var2, boolean var3, boolean var4, boolean var5, MobSpawnSettings.Builder var6) {
      BiomeGenerationSettings.Builder var7 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
      if (!var4 && !var5) {
         var7.addStructureStart(StructureFeatures.JUNGLE_TEMPLE);
      }

      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var7);
      var7.addStructureStart(StructureFeatures.RUINED_PORTAL_JUNGLE);
      BiomeDefaultFeatures.addDefaultCarvers(var7);
      BiomeDefaultFeatures.addDefaultLakes(var7);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var7);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var7);
      BiomeDefaultFeatures.addDefaultOres(var7);
      BiomeDefaultFeatures.addDefaultSoftDisks(var7);
      if (var3) {
         BiomeDefaultFeatures.addBambooVegetation(var7);
      } else {
         if (!var4 && !var5) {
            BiomeDefaultFeatures.addLightBambooVegetation(var7);
         }

         if (var4) {
            BiomeDefaultFeatures.addJungleEdgeTrees(var7);
         } else {
            BiomeDefaultFeatures.addJungleTrees(var7);
         }
      }

      BiomeDefaultFeatures.addWarmFlowers(var7);
      BiomeDefaultFeatures.addJungleGrass(var7);
      BiomeDefaultFeatures.addDefaultMushrooms(var7);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var7);
      BiomeDefaultFeatures.addDefaultSprings(var7);
      BiomeDefaultFeatures.addJungleExtraVegetation(var7);
      BiomeDefaultFeatures.addSurfaceFreezing(var7);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.JUNGLE).depth(var0).scale(var1).temperature(0.95F).downfall(var2).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.95F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var6.build()).generationSettings(var7.build()).build();
   }

   public static Biome mountainBiome(float var0, float var1, ConfiguredSurfaceBuilder<SurfaceBuilderBaseConfiguration> var2, boolean var3) {
      MobSpawnSettings.Builder var4 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var4);
      var4.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 5, 4, 6));
      BiomeDefaultFeatures.commonSpawns(var4);
      BiomeGenerationSettings.Builder var5 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(var2);
      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var5);
      var5.addStructureStart(StructureFeatures.RUINED_PORTAL_MOUNTAIN);
      BiomeDefaultFeatures.addDefaultCarvers(var5);
      BiomeDefaultFeatures.addDefaultLakes(var5);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var5);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var5);
      BiomeDefaultFeatures.addDefaultOres(var5);
      BiomeDefaultFeatures.addDefaultSoftDisks(var5);
      if (var3) {
         BiomeDefaultFeatures.addMountainEdgeTrees(var5);
      } else {
         BiomeDefaultFeatures.addMountainTrees(var5);
      }

      BiomeDefaultFeatures.addDefaultFlowers(var5);
      BiomeDefaultFeatures.addDefaultGrass(var5);
      BiomeDefaultFeatures.addDefaultMushrooms(var5);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var5);
      BiomeDefaultFeatures.addDefaultSprings(var5);
      BiomeDefaultFeatures.addExtraEmeralds(var5);
      BiomeDefaultFeatures.addInfestedStone(var5);
      BiomeDefaultFeatures.addSurfaceFreezing(var5);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.EXTREME_HILLS).depth(var0).scale(var1).temperature(0.2F).downfall(0.3F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.2F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var4.build()).generationSettings(var5.build()).build();
   }

   public static Biome desertBiome(float var0, float var1, boolean var2, boolean var3, boolean var4) {
      MobSpawnSettings.Builder var5 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.desertSpawns(var5);
      BiomeGenerationSettings.Builder var6 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.DESERT);
      if (var2) {
         var6.addStructureStart(StructureFeatures.VILLAGE_DESERT);
         var6.addStructureStart(StructureFeatures.PILLAGER_OUTPOST);
      }

      if (var3) {
         var6.addStructureStart(StructureFeatures.DESERT_PYRAMID);
      }

      if (var4) {
         BiomeDefaultFeatures.addFossilDecoration(var6);
      }

      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var6);
      var6.addStructureStart(StructureFeatures.RUINED_PORTAL_DESERT);
      BiomeDefaultFeatures.addDefaultCarvers(var6);
      BiomeDefaultFeatures.addDesertLakes(var6);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var6);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var6);
      BiomeDefaultFeatures.addDefaultOres(var6);
      BiomeDefaultFeatures.addDefaultSoftDisks(var6);
      BiomeDefaultFeatures.addDefaultFlowers(var6);
      BiomeDefaultFeatures.addDefaultGrass(var6);
      BiomeDefaultFeatures.addDesertVegetation(var6);
      BiomeDefaultFeatures.addDefaultMushrooms(var6);
      BiomeDefaultFeatures.addDesertExtraVegetation(var6);
      BiomeDefaultFeatures.addDefaultSprings(var6);
      BiomeDefaultFeatures.addDesertExtraDecoration(var6);
      BiomeDefaultFeatures.addSurfaceFreezing(var6);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.DESERT).depth(var0).scale(var1).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(2.0F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var5.build()).generationSettings(var6.build()).build();
   }

   public static Biome plainsBiome(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.plainsSpawns(var1);
      if (!var0) {
         var1.setPlayerCanSpawn();
      }

      BiomeGenerationSettings.Builder var2 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
      if (!var0) {
         var2.addStructureStart(StructureFeatures.VILLAGE_PLAINS).addStructureStart(StructureFeatures.PILLAGER_OUTPOST);
      }

      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var2);
      var2.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var2);
      BiomeDefaultFeatures.addDefaultLakes(var2);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var2);
      BiomeDefaultFeatures.addPlainGrass(var2);
      if (var0) {
         var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUNFLOWER);
      }

      BiomeDefaultFeatures.addDefaultUndergroundVariety(var2);
      BiomeDefaultFeatures.addDefaultOres(var2);
      BiomeDefaultFeatures.addDefaultSoftDisks(var2);
      BiomeDefaultFeatures.addPlainVegetation(var2);
      if (var0) {
         var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var2);
      if (var0) {
         var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
      } else {
         BiomeDefaultFeatures.addDefaultExtraVegetation(var2);
      }

      BiomeDefaultFeatures.addDefaultSprings(var2);
      BiomeDefaultFeatures.addSurfaceFreezing(var2);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.PLAINS).depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.8F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var1.build()).generationSettings(var2.build()).build();
   }

   private static Biome baseEndBiome(BiomeGenerationSettings.Builder var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.endSpawns(var1);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var1.build()).generationSettings(var0.build()).build();
   }

   public static Biome endBarrensBiome() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END);
      return baseEndBiome(var0);
   }

   public static Biome theEndBiome() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.END_SPIKE);
      return baseEndBiome(var0);
   }

   public static Biome endMidlandsBiome() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END).addStructureStart(StructureFeatures.END_CITY);
      return baseEndBiome(var0);
   }

   public static Biome endHighlandsBiome() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END).addStructureStart(StructureFeatures.END_CITY).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.END_GATEWAY).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.CHORUS_PLANT);
      return baseEndBiome(var0);
   }

   public static Biome smallEndIslandsBiome() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.END).addFeature(GenerationStep.Decoration.RAW_GENERATION, Features.END_ISLAND_DECORATED);
      return baseEndBiome(var0);
   }

   public static Biome mushroomFieldsBiome(float var0, float var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.mooshroomSpawns(var2);
      BiomeGenerationSettings.Builder var3 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.MYCELIUM);
      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var3);
      var3.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var3);
      BiomeDefaultFeatures.addDefaultLakes(var3);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var3);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addMushroomFieldVegetation(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3);
      BiomeDefaultFeatures.addDefaultSprings(var3);
      BiomeDefaultFeatures.addSurfaceFreezing(var3);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.MUSHROOM).depth(var0).scale(var1).temperature(0.9F).downfall(1.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.9F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var2.build()).generationSettings(var3.build()).build();
   }

   private static Biome baseSavannaBiome(float var0, float var1, float var2, boolean var3, boolean var4, MobSpawnSettings.Builder var5) {
      BiomeGenerationSettings.Builder var6 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(var4 ? SurfaceBuilders.SHATTERED_SAVANNA : SurfaceBuilders.GRASS);
      if (!var3 && !var4) {
         var6.addStructureStart(StructureFeatures.VILLAGE_SAVANNA).addStructureStart(StructureFeatures.PILLAGER_OUTPOST);
      }

      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var6);
      var6.addStructureStart(var3 ? StructureFeatures.RUINED_PORTAL_MOUNTAIN : StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var6);
      BiomeDefaultFeatures.addDefaultLakes(var6);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var6);
      if (!var4) {
         BiomeDefaultFeatures.addSavannaGrass(var6);
      }

      BiomeDefaultFeatures.addDefaultUndergroundVariety(var6);
      BiomeDefaultFeatures.addDefaultOres(var6);
      BiomeDefaultFeatures.addDefaultSoftDisks(var6);
      if (var4) {
         BiomeDefaultFeatures.addShatteredSavannaTrees(var6);
         BiomeDefaultFeatures.addDefaultFlowers(var6);
         BiomeDefaultFeatures.addShatteredSavannaGrass(var6);
      } else {
         BiomeDefaultFeatures.addSavannaTrees(var6);
         BiomeDefaultFeatures.addWarmFlowers(var6);
         BiomeDefaultFeatures.addSavannaExtraGrass(var6);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var6);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var6);
      BiomeDefaultFeatures.addDefaultSprings(var6);
      BiomeDefaultFeatures.addSurfaceFreezing(var6);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.SAVANNA).depth(var0).scale(var1).temperature(var2).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(var2)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var5.build()).generationSettings(var6.build()).build();
   }

   public static Biome savannaBiome(float var0, float var1, float var2, boolean var3, boolean var4) {
      MobSpawnSettings.Builder var5 = savannaMobs();
      return baseSavannaBiome(var0, var1, var2, var3, var4, var5);
   }

   private static MobSpawnSettings.Builder savannaMobs() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var0);
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 1));
      BiomeDefaultFeatures.commonSpawns(var0);
      return var0;
   }

   public static Biome savanaPlateauBiome() {
      MobSpawnSettings.Builder var0 = savannaMobs();
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 8, 4, 4));
      return baseSavannaBiome(1.5F, 0.025F, 1.0F, true, false, var0);
   }

   private static Biome baseBadlandsBiome(ConfiguredSurfaceBuilder<SurfaceBuilderBaseConfiguration> var0, float var1, float var2, boolean var3, boolean var4) {
      MobSpawnSettings.Builder var5 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(var5);
      BiomeGenerationSettings.Builder var6 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(var0);
      BiomeDefaultFeatures.addDefaultOverworldLandMesaStructures(var6);
      var6.addStructureStart(var3 ? StructureFeatures.RUINED_PORTAL_MOUNTAIN : StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var6);
      BiomeDefaultFeatures.addDefaultLakes(var6);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var6);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var6);
      BiomeDefaultFeatures.addDefaultOres(var6);
      BiomeDefaultFeatures.addExtraGold(var6);
      BiomeDefaultFeatures.addDefaultSoftDisks(var6);
      if (var4) {
         BiomeDefaultFeatures.addBadlandsTrees(var6);
      }

      BiomeDefaultFeatures.addBadlandGrass(var6);
      BiomeDefaultFeatures.addDefaultMushrooms(var6);
      BiomeDefaultFeatures.addBadlandExtraVegetation(var6);
      BiomeDefaultFeatures.addDefaultSprings(var6);
      BiomeDefaultFeatures.addSurfaceFreezing(var6);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.MESA).depth(var1).scale(var2).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(2.0F)).foliageColorOverride(10387789).grassColorOverride(9470285).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var5.build()).generationSettings(var6.build()).build();
   }

   public static Biome badlandsBiome(float var0, float var1, boolean var2) {
      return baseBadlandsBiome(SurfaceBuilders.BADLANDS, var0, var1, var2, false);
   }

   public static Biome woodedBadlandsPlateauBiome(float var0, float var1) {
      return baseBadlandsBiome(SurfaceBuilders.WOODED_BADLANDS, var0, var1, true, true);
   }

   public static Biome erodedBadlandsBiome() {
      return baseBadlandsBiome(SurfaceBuilders.ERODED_BADLANDS, 0.1F, 0.2F, true, false);
   }

   private static Biome baseOceanBiome(MobSpawnSettings.Builder var0, int var1, int var2, boolean var3, BiomeGenerationSettings.Builder var4) {
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.OCEAN).depth(var3 ? -1.8F : -1.0F).scale(0.1F).temperature(0.5F).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var1).waterFogColor(var2).fogColor(12638463).skyColor(calculateSkyColor(0.5F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var0.build()).generationSettings(var4.build()).build();
   }

   private static BiomeGenerationSettings.Builder baseOceanGeneration(ConfiguredSurfaceBuilder<SurfaceBuilderBaseConfiguration> var0, boolean var1, boolean var2, boolean var3) {
      BiomeGenerationSettings.Builder var4 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(var0);
      ConfiguredStructureFeature var5 = var2 ? StructureFeatures.OCEAN_RUIN_WARM : StructureFeatures.OCEAN_RUIN_COLD;
      if (var3) {
         if (var1) {
            var4.addStructureStart(StructureFeatures.OCEAN_MONUMENT);
         }

         BiomeDefaultFeatures.addDefaultOverworldOceanStructures(var4);
         var4.addStructureStart(var5);
      } else {
         var4.addStructureStart(var5);
         if (var1) {
            var4.addStructureStart(StructureFeatures.OCEAN_MONUMENT);
         }

         BiomeDefaultFeatures.addDefaultOverworldOceanStructures(var4);
      }

      var4.addStructureStart(StructureFeatures.RUINED_PORTAL_OCEAN);
      BiomeDefaultFeatures.addOceanCarvers(var4);
      BiomeDefaultFeatures.addDefaultLakes(var4);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var4);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      BiomeDefaultFeatures.addWaterTrees(var4);
      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addDefaultGrass(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4);
      BiomeDefaultFeatures.addDefaultSprings(var4);
      return var4;
   }

   public static Biome coldOceanBiome(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(var1, 3, 4, 15);
      var1.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5));
      boolean var2 = !var0;
      BiomeGenerationSettings.Builder var3 = baseOceanGeneration(SurfaceBuilders.GRASS, var0, false, var2);
      var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var0 ? Features.SEAGRASS_DEEP_COLD : Features.SEAGRASS_COLD);
      BiomeDefaultFeatures.addDefaultSeagrass(var3);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(var3);
      BiomeDefaultFeatures.addSurfaceFreezing(var3);
      return baseOceanBiome(var1, 4020182, 329011, var0, var3);
   }

   public static Biome oceanBiome(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(var1, 1, 4, 10);
      var1.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 1, 2));
      BiomeGenerationSettings.Builder var2 = baseOceanGeneration(SurfaceBuilders.GRASS, var0, false, true);
      var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var0 ? Features.SEAGRASS_DEEP : Features.SEAGRASS_NORMAL);
      BiomeDefaultFeatures.addDefaultSeagrass(var2);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(var2);
      BiomeDefaultFeatures.addSurfaceFreezing(var2);
      return baseOceanBiome(var1, 4159204, 329011, var0, var2);
   }

   public static Biome lukeWarmOceanBiome(boolean var0) {
      MobSpawnSettings.Builder var1 = new MobSpawnSettings.Builder();
      if (var0) {
         BiomeDefaultFeatures.oceanSpawns(var1, 8, 4, 8);
      } else {
         BiomeDefaultFeatures.oceanSpawns(var1, 10, 2, 15);
      }

      var1.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 5, 1, 3)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8)).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
      BiomeGenerationSettings.Builder var2 = baseOceanGeneration(SurfaceBuilders.OCEAN_SAND, var0, true, false);
      var2.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var0 ? Features.SEAGRASS_DEEP_WARM : Features.SEAGRASS_WARM);
      if (var0) {
         BiomeDefaultFeatures.addDefaultSeagrass(var2);
      }

      BiomeDefaultFeatures.addLukeWarmKelp(var2);
      BiomeDefaultFeatures.addSurfaceFreezing(var2);
      return baseOceanBiome(var1, 4566514, 267827, var0, var2);
   }

   public static Biome warmOceanBiome() {
      MobSpawnSettings.Builder var0 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 15, 1, 3));
      BiomeDefaultFeatures.warmOceanSpawns(var0, 10, 4);
      BiomeGenerationSettings.Builder var1 = baseOceanGeneration(SurfaceBuilders.FULL_SAND, false, true, false).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.WARM_OCEAN_VEGETATION).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_WARM).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SEA_PICKLE);
      BiomeDefaultFeatures.addSurfaceFreezing(var1);
      return baseOceanBiome(var0, 4445678, 270131, false, var1);
   }

   public static Biome deepWarmOceanBiome() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.warmOceanSpawns(var0, 5, 1);
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
      BiomeGenerationSettings.Builder var1 = baseOceanGeneration(SurfaceBuilders.FULL_SAND, true, true, false).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_DEEP_WARM);
      BiomeDefaultFeatures.addDefaultSeagrass(var1);
      BiomeDefaultFeatures.addSurfaceFreezing(var1);
      return baseOceanBiome(var0, 4445678, 270131, true, var1);
   }

   public static Biome frozenOceanBiome(boolean var0) {
      MobSpawnSettings.Builder var1 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
      BiomeDefaultFeatures.commonSpawns(var1);
      var1.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
      float var2 = var0 ? 0.5F : 0.0F;
      BiomeGenerationSettings.Builder var3 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.FROZEN_OCEAN);
      var3.addStructureStart(StructureFeatures.OCEAN_RUIN_COLD);
      if (var0) {
         var3.addStructureStart(StructureFeatures.OCEAN_MONUMENT);
      }

      BiomeDefaultFeatures.addDefaultOverworldOceanStructures(var3);
      var3.addStructureStart(StructureFeatures.RUINED_PORTAL_OCEAN);
      BiomeDefaultFeatures.addOceanCarvers(var3);
      BiomeDefaultFeatures.addDefaultLakes(var3);
      BiomeDefaultFeatures.addIcebergs(var3);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var3);
      BiomeDefaultFeatures.addBlueIce(var3);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var3);
      BiomeDefaultFeatures.addDefaultOres(var3);
      BiomeDefaultFeatures.addDefaultSoftDisks(var3);
      BiomeDefaultFeatures.addWaterTrees(var3);
      BiomeDefaultFeatures.addDefaultFlowers(var3);
      BiomeDefaultFeatures.addDefaultGrass(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3);
      BiomeDefaultFeatures.addDefaultSprings(var3);
      BiomeDefaultFeatures.addSurfaceFreezing(var3);
      return (new Biome.BiomeBuilder()).precipitation(var0 ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW).biomeCategory(Biome.BiomeCategory.OCEAN).depth(var0 ? -1.8F : -1.0F).scale(0.1F).temperature(var2).temperatureAdjustment(Biome.TemperatureModifier.FROZEN).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(var2)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var1.build()).generationSettings(var3.build()).build();
   }

   private static Biome baseForestBiome(float var0, float var1, boolean var2, MobSpawnSettings.Builder var3) {
      BiomeGenerationSettings.Builder var4 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var4);
      var4.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var4);
      BiomeDefaultFeatures.addDefaultLakes(var4);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var4);
      if (var2) {
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_VEGETATION_COMMON);
      } else {
         BiomeDefaultFeatures.addForestFlowers(var4);
      }

      BiomeDefaultFeatures.addDefaultUndergroundVariety(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      if (var2) {
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_TREES);
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_FOREST);
         BiomeDefaultFeatures.addDefaultGrass(var4);
      } else {
         BiomeDefaultFeatures.addOtherBirchTrees(var4);
         BiomeDefaultFeatures.addDefaultFlowers(var4);
         BiomeDefaultFeatures.addForestGrass(var4);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4);
      BiomeDefaultFeatures.addDefaultSprings(var4);
      BiomeDefaultFeatures.addSurfaceFreezing(var4);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.FOREST).depth(var0).scale(var1).temperature(0.7F).downfall(0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.7F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   private static MobSpawnSettings.Builder defaultSpawns() {
      MobSpawnSettings.Builder var0 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var0);
      BiomeDefaultFeatures.commonSpawns(var0);
      return var0;
   }

   public static Biome forestBiome(float var0, float var1) {
      MobSpawnSettings.Builder var2 = defaultSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4)).setPlayerCanSpawn();
      return baseForestBiome(var0, var1, false, var2);
   }

   public static Biome flowerForestBiome() {
      MobSpawnSettings.Builder var0 = defaultSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      return baseForestBiome(0.1F, 0.4F, true, var0);
   }

   public static Biome taigaBiome(float var0, float var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      MobSpawnSettings.Builder var6 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var6);
      var6.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      if (!var2 && !var3) {
         var6.setPlayerCanSpawn();
      }

      BiomeDefaultFeatures.commonSpawns(var6);
      float var7 = var2 ? -0.5F : 0.25F;
      BiomeGenerationSettings.Builder var8 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
      if (var4) {
         var8.addStructureStart(StructureFeatures.VILLAGE_TAIGA);
         var8.addStructureStart(StructureFeatures.PILLAGER_OUTPOST);
      }

      if (var5) {
         var8.addStructureStart(StructureFeatures.IGLOO);
      }

      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var8);
      var8.addStructureStart(var3 ? StructureFeatures.RUINED_PORTAL_MOUNTAIN : StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var8);
      BiomeDefaultFeatures.addDefaultLakes(var8);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var8);
      BiomeDefaultFeatures.addFerns(var8);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var8);
      BiomeDefaultFeatures.addDefaultOres(var8);
      BiomeDefaultFeatures.addDefaultSoftDisks(var8);
      BiomeDefaultFeatures.addTaigaTrees(var8);
      BiomeDefaultFeatures.addDefaultFlowers(var8);
      BiomeDefaultFeatures.addTaigaGrass(var8);
      BiomeDefaultFeatures.addDefaultMushrooms(var8);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var8);
      BiomeDefaultFeatures.addDefaultSprings(var8);
      if (var2) {
         BiomeDefaultFeatures.addBerryBushes(var8);
      } else {
         BiomeDefaultFeatures.addSparseBerryBushes(var8);
      }

      BiomeDefaultFeatures.addSurfaceFreezing(var8);
      return (new Biome.BiomeBuilder()).precipitation(var2 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.TAIGA).depth(var0).scale(var1).temperature(var7).downfall(var2 ? 0.4F : 0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var2 ? 4020182 : 4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(var7)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var6.build()).generationSettings(var8.build()).build();
   }

   public static Biome darkForestBiome(float var0, float var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      BiomeDefaultFeatures.commonSpawns(var3);
      BiomeGenerationSettings.Builder var4 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
      var4.addStructureStart(StructureFeatures.WOODLAND_MANSION);
      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var4);
      var4.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var4);
      BiomeDefaultFeatures.addDefaultLakes(var4);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var4);
      var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var2 ? Features.DARK_FOREST_VEGETATION_RED : Features.DARK_FOREST_VEGETATION_BROWN);
      BiomeDefaultFeatures.addForestFlowers(var4);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addDefaultSoftDisks(var4);
      BiomeDefaultFeatures.addDefaultFlowers(var4);
      BiomeDefaultFeatures.addForestGrass(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var4);
      BiomeDefaultFeatures.addDefaultSprings(var4);
      BiomeDefaultFeatures.addSurfaceFreezing(var4);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.FOREST).depth(var0).scale(var1).temperature(0.7F).downfall(0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.7F)).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome swampBiome(float var0, float var1, boolean var2) {
      MobSpawnSettings.Builder var3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(var3);
      BiomeDefaultFeatures.commonSpawns(var3);
      var3.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
      BiomeGenerationSettings.Builder var4 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.SWAMP);
      if (!var2) {
         var4.addStructureStart(StructureFeatures.SWAMP_HUT);
      }

      var4.addStructureStart(StructureFeatures.MINESHAFT);
      var4.addStructureStart(StructureFeatures.RUINED_PORTAL_SWAMP);
      BiomeDefaultFeatures.addDefaultCarvers(var4);
      if (!var2) {
         BiomeDefaultFeatures.addFossilDecoration(var4);
      }

      BiomeDefaultFeatures.addDefaultLakes(var4);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var4);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var4);
      BiomeDefaultFeatures.addDefaultOres(var4);
      BiomeDefaultFeatures.addSwampClayDisk(var4);
      BiomeDefaultFeatures.addSwampVegetation(var4);
      BiomeDefaultFeatures.addDefaultMushrooms(var4);
      BiomeDefaultFeatures.addSwampExtraVegetation(var4);
      BiomeDefaultFeatures.addDefaultSprings(var4);
      if (var2) {
         BiomeDefaultFeatures.addFossilDecoration(var4);
      } else {
         var4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SWAMP);
      }

      BiomeDefaultFeatures.addSurfaceFreezing(var4);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.SWAMP).depth(var0).scale(var1).temperature(0.8F).downfall(0.9F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(6388580).waterFogColor(2302743).fogColor(12638463).skyColor(calculateSkyColor(0.8F)).foliageColorOverride(6975545).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var3.build()).generationSettings(var4.build()).build();
   }

   public static Biome tundraBiome(float var0, float var1, boolean var2, boolean var3) {
      MobSpawnSettings.Builder var4 = (new MobSpawnSettings.Builder()).creatureGenerationProbability(0.07F);
      BiomeDefaultFeatures.snowySpawns(var4);
      BiomeGenerationSettings.Builder var5 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(var2 ? SurfaceBuilders.ICE_SPIKES : SurfaceBuilders.GRASS);
      if (!var2 && !var3) {
         var5.addStructureStart(StructureFeatures.VILLAGE_SNOWY).addStructureStart(StructureFeatures.IGLOO);
      }

      BiomeDefaultFeatures.addDefaultOverworldLandStructures(var5);
      if (!var2 && !var3) {
         var5.addStructureStart(StructureFeatures.PILLAGER_OUTPOST);
      }

      var5.addStructureStart(var3 ? StructureFeatures.RUINED_PORTAL_MOUNTAIN : StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var5);
      BiomeDefaultFeatures.addDefaultLakes(var5);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var5);
      if (var2) {
         var5.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.ICE_SPIKE);
         var5.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.ICE_PATCH);
      }

      BiomeDefaultFeatures.addDefaultUndergroundVariety(var5);
      BiomeDefaultFeatures.addDefaultOres(var5);
      BiomeDefaultFeatures.addDefaultSoftDisks(var5);
      BiomeDefaultFeatures.addSnowyTrees(var5);
      BiomeDefaultFeatures.addDefaultFlowers(var5);
      BiomeDefaultFeatures.addDefaultGrass(var5);
      BiomeDefaultFeatures.addDefaultMushrooms(var5);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var5);
      BiomeDefaultFeatures.addDefaultSprings(var5);
      BiomeDefaultFeatures.addSurfaceFreezing(var5);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.SNOW).biomeCategory(Biome.BiomeCategory.ICY).depth(var0).scale(var1).temperature(0.0F).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.0F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var4.build()).generationSettings(var5.build()).build();
   }

   public static Biome riverBiome(float var0, float var1, float var2, int var3, boolean var4) {
      MobSpawnSettings.Builder var5 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 2, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 5, 1, 5));
      BiomeDefaultFeatures.commonSpawns(var5);
      var5.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, var4 ? 1 : 100, 1, 1));
      BiomeGenerationSettings.Builder var6 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.GRASS);
      var6.addStructureStart(StructureFeatures.MINESHAFT);
      var6.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var6);
      BiomeDefaultFeatures.addDefaultLakes(var6);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var6);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var6);
      BiomeDefaultFeatures.addDefaultOres(var6);
      BiomeDefaultFeatures.addDefaultSoftDisks(var6);
      BiomeDefaultFeatures.addWaterTrees(var6);
      BiomeDefaultFeatures.addDefaultFlowers(var6);
      BiomeDefaultFeatures.addDefaultGrass(var6);
      BiomeDefaultFeatures.addDefaultMushrooms(var6);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var6);
      BiomeDefaultFeatures.addDefaultSprings(var6);
      if (!var4) {
         var6.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_RIVER);
      }

      BiomeDefaultFeatures.addSurfaceFreezing(var6);
      return (new Biome.BiomeBuilder()).precipitation(var4 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.RIVER).depth(var0).scale(var1).temperature(var2).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var3).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(var2)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var5.build()).generationSettings(var6.build()).build();
   }

   public static Biome beachBiome(float var0, float var1, float var2, float var3, int var4, boolean var5, boolean var6) {
      MobSpawnSettings.Builder var7 = new MobSpawnSettings.Builder();
      if (!var6 && !var5) {
         var7.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 5, 2, 5));
      }

      BiomeDefaultFeatures.commonSpawns(var7);
      BiomeGenerationSettings.Builder var8 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(var6 ? SurfaceBuilders.STONE : SurfaceBuilders.DESERT);
      if (var6) {
         BiomeDefaultFeatures.addDefaultOverworldLandStructures(var8);
      } else {
         var8.addStructureStart(StructureFeatures.MINESHAFT);
         var8.addStructureStart(StructureFeatures.BURIED_TREASURE);
         var8.addStructureStart(StructureFeatures.SHIPWRECH_BEACHED);
      }

      var8.addStructureStart(var6 ? StructureFeatures.RUINED_PORTAL_MOUNTAIN : StructureFeatures.RUINED_PORTAL_STANDARD);
      BiomeDefaultFeatures.addDefaultCarvers(var8);
      BiomeDefaultFeatures.addDefaultLakes(var8);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var8);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(var8);
      BiomeDefaultFeatures.addDefaultOres(var8);
      BiomeDefaultFeatures.addDefaultSoftDisks(var8);
      BiomeDefaultFeatures.addDefaultFlowers(var8);
      BiomeDefaultFeatures.addDefaultGrass(var8);
      BiomeDefaultFeatures.addDefaultMushrooms(var8);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var8);
      BiomeDefaultFeatures.addDefaultSprings(var8);
      BiomeDefaultFeatures.addSurfaceFreezing(var8);
      return (new Biome.BiomeBuilder()).precipitation(var5 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN).biomeCategory(var6 ? Biome.BiomeCategory.NONE : Biome.BiomeCategory.BEACH).depth(var0).scale(var1).temperature(var2).downfall(var3).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(var4).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(var2)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(var7.build()).generationSettings(var8.build()).build();
   }

   public static Biome theVoidBiome() {
      BiomeGenerationSettings.Builder var0 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.NOPE);
      var0.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Features.VOID_START_PLATFORM);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.NONE).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.5F)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(MobSpawnSettings.EMPTY).generationSettings(var0.build()).build();
   }

   public static Biome netherWastesBiome() {
      MobSpawnSettings var0 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.GHAST, 50, 4, 4)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 2, 4, 4)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 4, 4)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.PIGLIN, 15, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2)).build();
      BiomeGenerationSettings.Builder var1 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.NETHER).addStructureStart(StructureFeatures.RUINED_PORTAL_NETHER).addStructureStart(StructureFeatures.NETHER_BRIDGE).addStructureStart(StructureFeatures.BASTION_REMNANT).addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      var1.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.BROWN_MUSHROOM_NETHER).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.RED_MUSHROOM_NETHER).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED);
      BiomeDefaultFeatures.addNetherDefaultOres(var1);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(3344392).skyColor(calculateSkyColor(2.0F)).ambientLoopSound(SoundEvents.AMBIENT_NETHER_WASTES_LOOP).ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_NETHER_WASTES)).build()).mobSpawnSettings(var0).generationSettings(var1.build()).build();
   }

   public static Biome soulSandValleyBiome() {
      double var0 = 0.7D;
      double var2 = 0.15D;
      MobSpawnSettings var4 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 20, 5, 5)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.GHAST, 50, 4, 4)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2)).addMobCharge(EntityType.SKELETON, 0.7D, 0.15D).addMobCharge(EntityType.GHAST, 0.7D, 0.15D).addMobCharge(EntityType.ENDERMAN, 0.7D, 0.15D).addMobCharge(EntityType.STRIDER, 0.7D, 0.15D).build();
      BiomeGenerationSettings.Builder var5 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.SOUL_SAND_VALLEY).addStructureStart(StructureFeatures.NETHER_BRIDGE).addStructureStart(StructureFeatures.NETHER_FOSSIL).addStructureStart(StructureFeatures.RUINED_PORTAL_NETHER).addStructureStart(StructureFeatures.BASTION_REMNANT).addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA).addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Features.BASALT_PILLAR).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_CRIMSON_ROOTS).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_SOUL_SAND);
      BiomeDefaultFeatures.addNetherDefaultOres(var5);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(1787717).skyColor(calculateSkyColor(2.0F)).ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.00625F)).ambientLoopSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP).ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SOUL_SAND_VALLEY)).build()).mobSpawnSettings(var4).generationSettings(var5.build()).build();
   }

   public static Biome basaltDeltasBiome() {
      MobSpawnSettings var0 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.GHAST, 40, 1, 1)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 100, 2, 5)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2)).build();
      BiomeGenerationSettings.Builder var1 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.BASALT_DELTAS).addStructureStart(StructureFeatures.RUINED_PORTAL_NETHER).addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE).addStructureStart(StructureFeatures.NETHER_BRIDGE).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.DELTA).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA_DOUBLE).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.SMALL_BASALT_COLUMNS).addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.LARGE_BASALT_COLUMNS).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.BASALT_BLOBS).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.BLACKSTONE_BLOBS).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_DELTA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.BROWN_MUSHROOM_NETHER).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.RED_MUSHROOM_NETHER).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED_DOUBLE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_GOLD_DELTAS).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_QUARTZ_DELTAS);
      BiomeDefaultFeatures.addAncientDebris(var1);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(4341314).fogColor(6840176).skyColor(calculateSkyColor(2.0F)).ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.118093334F)).ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP).ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BASALT_DELTAS)).build()).mobSpawnSettings(var0).generationSettings(var1.build()).build();
   }

   public static Biome crimsonForestBiome() {
      MobSpawnSettings var0 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.HOGLIN, 9, 3, 4)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.PIGLIN, 5, 3, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2)).build();
      BiomeGenerationSettings.Builder var1 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.CRIMSON_FOREST).addStructureStart(StructureFeatures.RUINED_PORTAL_NETHER).addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE).addStructureStart(StructureFeatures.NETHER_BRIDGE).addStructureStart(StructureFeatures.BASTION_REMNANT).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      var1.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.WEEPING_VINES).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.CRIMSON_FUNGI).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.CRIMSON_FOREST_VEGETATION);
      BiomeDefaultFeatures.addNetherDefaultOres(var1);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(3343107).skyColor(calculateSkyColor(2.0F)).ambientParticle(new AmbientParticleSettings(ParticleTypes.CRIMSON_SPORE, 0.025F)).ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP).ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST)).build()).mobSpawnSettings(var0).generationSettings(var1.build()).build();
   }

   public static Biome warpedForestBiome() {
      MobSpawnSettings var0 = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2)).addMobCharge(EntityType.ENDERMAN, 1.0D, 0.12D).build();
      BiomeGenerationSettings.Builder var1 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(SurfaceBuilders.WARPED_FOREST).addStructureStart(StructureFeatures.NETHER_BRIDGE).addStructureStart(StructureFeatures.BASTION_REMNANT).addStructureStart(StructureFeatures.RUINED_PORTAL_NETHER).addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
      BiomeDefaultFeatures.addDefaultMushrooms(var1);
      var1.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.WARPED_FUNGI).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.WARPED_FOREST_VEGETATION).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.NETHER_SPROUTS).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TWISTING_VINES);
      BiomeDefaultFeatures.addNetherDefaultOres(var1);
      return (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(1705242).skyColor(calculateSkyColor(2.0F)).ambientParticle(new AmbientParticleSettings(ParticleTypes.WARPED_SPORE, 0.01428F)).ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP).ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111D)).backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_WARPED_FOREST)).build()).mobSpawnSettings(var0).generationSettings(var1.build()).build();
   }
}
