package net.minecraft.data.worldgen.biome;

import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PotatoBiomes {
   protected static final int NORMAL_WATER_COLOR = 4187298;
   protected static final int NORMAL_WATER_FOG_COLOR = 740644;
   private static final int OVERWORLD_FOG_COLOR = 9821879;
   private static final int NORMAL_GRASS_COLOR = 6017107;
   private static final int NORMAL_FOLIAGE_COLOR = 2199366;
   private static final int CORRUPTED_COLOR = 1153876;
   private static final int CORRUPTED_COLOR_LIGHT = 1356889;
   private static final int CORRUPTED_COLOR_DARK = 1466676;
   private static final int POISON_COLOR = 5105159;
   private static final int POISON_COLOR_LIGHT = 8157780;
   private static final int POISON_COLOR_DARK = 4056583;
   @Nullable
   private static final Music NORMAL_MUSIC = null;

   public PotatoBiomes() {
      super();
   }

   protected static int calculateSkyColor(float var0) {
      float var1 = var0 / 3.0F;
      var1 = Mth.clamp(var1, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.4722222F - var1 * 0.1F, 0.7F + var1 * 0.1F, 1.0F);
   }

   private static void globalPotatoGeneration(BiomeGenerationSettings.Builder var0, boolean var1) {
      BiomeDefaultFeatures.addDefaultCarversAndLakes(var0);
      BiomeDefaultFeatures.addDefaultCrystalFormations(var0);
      BiomeDefaultFeatures.addDefaultMonsterRoom(var0);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_TATERSTONE);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_AMBER);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRAVTATER);
      BiomeDefaultFeatures.addDefaultSprings(var0);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, var1 ? VegetationPlacements.PATCH_POTATO_SPARSE : VegetationPlacements.PATCH_POTATO);
      var0.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.POTATO_CLOUD);
      BiomeDefaultFeatures.addDefaultOres(var0, false, true);
   }

   public static void farmAnimals(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 12, 4, 4));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 4, 4));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 8, 4, 4));
   }

   public static void desertSpawns(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 4, 1, 2));
      caveSpawns(var0);
      monsters(var0, 19, 1, 100);
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.HUSK, 80, 4, 4));
   }

   public static void arboretumSpawns(MobSpawnSettings.Builder var0) {
      farmAnimals(var0);
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 5, 2, 6));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 3));
      commonSpawns(var0);
   }

   public static void commonSpawns(MobSpawnSettings.Builder var0) {
      caveSpawns(var0);
      monsters(var0, 95, 5, 100);
   }

   public static void caveSpawns(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BAT, 10, 8, 8));
      var0.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BATATO, 13, 8, 16));
      var0.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID, 10, 4, 6));
   }

   public static void monsters(MobSpawnSettings.Builder var0, int var1, int var2, int var3) {
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 100, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, var1, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.POISONOUS_POTATO_ZOMBIE, 50, 1, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE_VILLAGER, var2, 1, 1));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, var3, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 100, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 10, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 5, 1, 1));
   }

   public static Biome hash(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      var2.creatureGenerationProbability(0.01F);
      desertSpawns(var2);
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      BiomeDefaultFeatures.addFossilDecoration(var3);
      globalPotatoGeneration(var3, true);
      BiomeDefaultFeatures.addDefaultFlowers(var3);
      BiomeDefaultFeatures.addDefaultGrass(var3);
      var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH_2_ALL_LEVELS);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.LEAF_PILE_HASH);
      var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.VENOMOUS_COLUMN_HASH);
      var3.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.HASH_WELL);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(false)
         .temperature(2.0F)
         .downfall(0.0F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(4187298)
               .waterFogColor(740644)
               .fogColor(9821879)
               .skyColor(calculateSkyColor(2.0F))
               .ambientLoopSound(SoundEvents.AMBIENT_HASH_LOOP)
               .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT))
               .grassColorOverride(6017107)
               .foliageColorOverride(2199366)
               .build()
         )
         .mobSpawnSettings(var2.build())
         .generationSettings(var3.build())
         .build();
   }

   public static Biome fields(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      farmAnimals(var2);
      commonSpawns(var2);
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      var3.addFeature(GenerationStep.Decoration.STRONGHOLDS, VegetationPlacements.POTATO_FIELD);
      globalPotatoGeneration(var3, false);
      BiomeDefaultFeatures.addPlainGrass(var3);
      BiomeDefaultFeatures.addPlainVegetation(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(0.8F)
         .downfall(0.4F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(4187298)
               .waterFogColor(740644)
               .fogColor(9821879)
               .skyColor(calculateSkyColor(0.8F))
               .ambientLoopSound(SoundEvents.AMBIENT_FIELDS_LOOP)
               .backgroundMusic(NORMAL_MUSIC)
               .grassColorOverride(6017107)
               .foliageColorOverride(2199366)
               .build()
         )
         .mobSpawnSettings(var2.build())
         .generationSettings(var3.build())
         .build();
   }

   public static Biome arboretum(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      arboretumSpawns(var2);
      var2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      var2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 2, 4, 4));
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      var3.addFeature(GenerationStep.Decoration.STRONGHOLDS, VegetationPlacements.PARK_LANE_SURFACE);
      var3.addFeature(GenerationStep.Decoration.STRONGHOLDS, VegetationPlacements.PARK_LANE);
      globalPotatoGeneration(var3, false);
      var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
      var3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.ARBORETUM_TREES);
      BiomeDefaultFeatures.addDefaultFlowers(var3);
      BiomeDefaultFeatures.addForestGrass(var3);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      BiomeDefaultFeatures.addDefaultExtraVegetation(var3);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(0.9F)
         .downfall(0.5F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(4187298)
               .waterFogColor(740644)
               .fogColor(9821879)
               .skyColor(calculateSkyColor(0.7F))
               .ambientLoopSound(SoundEvents.AMBIENT_ARBORETUM_LOOP)
               .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST))
               .grassColorOverride(6017107)
               .build()
         )
         .mobSpawnSettings(var2.build())
         .generationSettings(var3.build())
         .build();
   }

   public static Biome wasteland(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      var2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 5, 1, 2));
      var2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.GIANT, 10, 1, 4));
      var2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 5, 1, 1));
      var2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.TOXIFIN, 25, 1, 4));
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      globalPotatoGeneration(var3, true);
      var3.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NETHER)
         .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.RED_MUSHROOM_NETHER)
         .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, CavePlacements.LARGE_POTATOSTONE)
         .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.LARGE_POTATO_COLUMNS)
         .addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, NetherPlacements.SMALL_DEBRIS_COLUMNS)
         .addFeature(GenerationStep.Decoration.FLUID_SPRINGS, NetherPlacements.POISON_POOL)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.POTATO_LEAF)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, CavePlacements.TWISTED_POTATO);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(2.0F)
         .downfall(0.5F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(5105159)
               .waterFogColor(4056583)
               .fogColor(8157780)
               .grassColorOverride(5105159)
               .foliageColorOverride(4056583)
               .skyColor(calculateSkyColor(1.0F))
               .ambientLoopSound(SoundEvents.AMBIENT_WASTELAND_LOOP)
               .ambientParticle(new AmbientParticleSettings(ParticleTypes.DRIPPING_HONEY, 0.001F))
               .build()
         )
         .mobSpawnSettings(var2.build())
         .generationSettings(var3.build())
         .build();
   }

   public static Biome corruption(HolderGetter<PlacedFeature> var0, HolderGetter<ConfiguredWorldCarver<?>> var1) {
      MobSpawnSettings.Builder var2 = new MobSpawnSettings.Builder();
      farmAnimals(var2);
      var2.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CAT, 40, 1, 1));
      commonSpawns(var2);
      var2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 100, 1, 1));
      var2.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.PHANTOM, 100, 1, 1));
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder(var0, var1);
      globalPotatoGeneration(var3, true);
      BiomeDefaultFeatures.addDefaultMushrooms(var3);
      var3.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NETHER)
         .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VegetationPlacements.RED_MUSHROOM_NETHER)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherPlacements.CORRUPTED_BUDS)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NetherPlacements.POTATO_SPROUTS)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TreePlacements.MOTHER_POTATO_TREE)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TreePlacements.POTATO_TREE_TALL)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TreePlacements.POTATO_TREE);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(2.0F)
         .downfall(1.0F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(1356889)
               .waterFogColor(1153876)
               .fogColor(1356889)
               .grassColorOverride(1466676)
               .foliageColorOverride(1153876)
               .skyColor(calculateSkyColor(0.0F))
               .ambientParticle(new AmbientParticleSettings(ParticleTypes.WARPED_SPORE, 0.01428F))
               .ambientLoopSound(SoundEvents.AMBIENT_CORRUPTION_LOOP)
               .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_WARPED_FOREST))
               .build()
         )
         .mobSpawnSettings(var2.build())
         .generationSettings(var3.build())
         .build();
   }
}
