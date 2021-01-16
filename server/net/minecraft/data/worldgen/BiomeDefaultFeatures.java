package net.minecraft.data.worldgen;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class BiomeDefaultFeatures {
   public static void addDefaultOverworldLandMesaStructures(BiomeGenerationSettings.Builder var0) {
      var0.addStructureStart(StructureFeatures.MINESHAFT_MESA);
      var0.addStructureStart(StructureFeatures.STRONGHOLD);
   }

   public static void addDefaultOverworldLandStructures(BiomeGenerationSettings.Builder var0) {
      var0.addStructureStart(StructureFeatures.MINESHAFT);
      var0.addStructureStart(StructureFeatures.STRONGHOLD);
   }

   public static void addDefaultOverworldOceanStructures(BiomeGenerationSettings.Builder var0) {
      var0.addStructureStart(StructureFeatures.MINESHAFT);
      var0.addStructureStart(StructureFeatures.SHIPWRECK);
   }

   public static void addDefaultCarvers(BiomeGenerationSettings.Builder var0) {
      var0.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
      var0.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
   }

   public static void addOceanCarvers(BiomeGenerationSettings.Builder var0) {
      var0.addCarver(GenerationStep.Carving.AIR, Carvers.OCEAN_CAVE);
      var0.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
      var0.addCarver(GenerationStep.Carving.LIQUID, Carvers.UNDERWATER_CANYON);
      var0.addCarver(GenerationStep.Carving.LIQUID, Carvers.UNDERWATER_CAVE);
   }

   public static void addDefaultLakes(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_WATER);
      var0.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_LAVA);
   }

   public static void addDesertLakes(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_LAVA);
   }

   public static void addDefaultMonsterRoom(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Features.MONSTER_ROOM);
   }

   public static void addDefaultUndergroundVariety(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_DIRT);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GRAVEL);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GRANITE);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_DIORITE);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_ANDESITE);
   }

   public static void addDefaultOres(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_COAL);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_IRON);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_REDSTONE);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_LAPIS);
   }

   public static void addExtraGold(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD_EXTRA);
   }

   public static void addExtraEmeralds(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.ORE_EMERALD);
   }

   public static void addInfestedStone(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_INFESTED);
   }

   public static void addDefaultSoftDisks(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_SAND);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_GRAVEL);
   }

   public static void addSwampClayDisk(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
   }

   public static void addMossyStoneBlock(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Features.FOREST_ROCK);
   }

   public static void addFerns(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_LARGE_FERN);
   }

   public static void addBerryBushes(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_DECORATED);
   }

   public static void addSparseBerryBushes(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_SPARSE);
   }

   public static void addLightBambooVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BAMBOO_LIGHT);
   }

   public static void addBambooVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BAMBOO);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BAMBOO_VEGETATION);
   }

   public static void addTaigaTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TAIGA_VEGETATION);
   }

   public static void addWaterTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_WATER);
   }

   public static void addBirchTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_BIRCH);
   }

   public static void addOtherBirchTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BIRCH_OTHER);
   }

   public static void addTallBirchTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BIRCH_TALL);
   }

   public static void addSavannaTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_SAVANNA);
   }

   public static void addShatteredSavannaTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_SHATTERED_SAVANNA);
   }

   public static void addMountainTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN);
   }

   public static void addMountainEdgeTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN_EDGE);
   }

   public static void addJungleTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE);
   }

   public static void addJungleEdgeTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE_EDGE);
   }

   public static void addBadlandsTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.OAK_BADLANDS);
   }

   public static void addSnowyTrees(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRUCE_SNOWY);
   }

   public static void addJungleGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_JUNGLE);
   }

   public static void addSavannaGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS);
   }

   public static void addShatteredSavannaGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
   }

   public static void addSavannaExtraGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_SAVANNA);
   }

   public static void addBadlandGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_BADLANDS);
   }

   public static void addForestFlowers(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_VEGETATION);
   }

   public static void addForestGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_FOREST);
   }

   public static void addSwampVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SWAMP_TREE);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_SWAMP);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_WATERLILLY);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_SWAMP);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_SWAMP);
   }

   public static void addMushroomFieldVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.MUSHROOM_FIELD_VEGETATION);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
   }

   public static void addPlainVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PLAIN_VEGETATION);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_PLAIN_DECORATED);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_PLAIN);
   }

   public static void addDesertVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_2);
   }

   public static void addGiantTaigaVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_GIANT);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_GIANT);
   }

   public static void addDefaultFlowers(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_DEFAULT);
   }

   public static void addWarmFlowers(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.FLOWER_WARM);
   }

   public static void addDefaultGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
   }

   public static void addTaigaGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA_2);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
   }

   public static void addPlainGrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS_2);
   }

   public static void addDefaultMushrooms(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_NORMAL);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_NORMAL);
   }

   public static void addDefaultExtraVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
   }

   public static void addBadlandExtraVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_BADLANDS);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DECORATED);
   }

   public static void addJungleExtraVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_MELON);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.VINES);
   }

   public static void addDesertExtraVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_DESERT);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DESERT);
   }

   public static void addSwampExtraVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_SWAMP);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
   }

   public static void addDesertExtraDecoration(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.WELL);
   }

   public static void addFossilDecoration(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Features.FOSSIL);
   }

   public static void addColdOceanExtraVegetation(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.KELP_COLD);
   }

   public static void addDefaultSeagrass(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SIMPLE);
   }

   public static void addLukeWarmKelp(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.KELP_WARM);
   }

   public static void addDefaultSprings(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
   }

   public static void addIcebergs(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_PACKED);
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_BLUE);
   }

   public static void addBlueIce(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Features.BLUE_ICE);
   }

   public static void addSurfaceFreezing(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Features.FREEZE_TOP_LAYER);
   }

   public static void addNetherDefaultOres(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_GRAVEL_NETHER);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_BLACKSTONE);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_GOLD_NETHER);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_QUARTZ_NETHER);
      addAncientDebris(var0);
   }

   public static void addAncientDebris(BiomeGenerationSettings.Builder var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_LARGE);
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_SMALL);
   }

   public static void farmAnimals(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 12, 4, 4));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 4, 4));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 8, 4, 4));
   }

   public static void ambientSpawns(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BAT, 10, 8, 8));
   }

   public static void commonSpawns(MobSpawnSettings.Builder var0) {
      ambientSpawns(var0);
      monsters(var0, 95, 5, 100);
   }

   public static void oceanSpawns(MobSpawnSettings.Builder var0, int var1, int var2, int var3) {
      var0.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, var1, 1, var2));
      var0.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.COD, var3, 3, 6));
      commonSpawns(var0);
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
   }

   public static void warmOceanSpawns(MobSpawnSettings.Builder var0, int var1, int var2) {
      var0.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, var1, var2, 4));
      var0.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
      var0.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
      commonSpawns(var0);
   }

   public static void plainsSpawns(MobSpawnSettings.Builder var0) {
      farmAnimals(var0);
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 5, 2, 6));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 3));
      commonSpawns(var0);
   }

   public static void snowySpawns(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 10, 2, 3));
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
      ambientSpawns(var0);
      monsters(var0, 95, 5, 20);
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 80, 4, 4));
   }

   public static void desertSpawns(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      ambientSpawns(var0);
      monsters(var0, 19, 1, 100);
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.HUSK, 80, 4, 4));
   }

   public static void monsters(MobSpawnSettings.Builder var0, int var1, int var2, int var3) {
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 100, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, var1, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE_VILLAGER, var2, 1, 1));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, var3, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 100, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 100, 4, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 1, 4));
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 5, 1, 1));
   }

   public static void mooshroomSpawns(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.MOOSHROOM, 8, 4, 8));
      ambientSpawns(var0);
   }

   public static void baseJungleSpawns(MobSpawnSettings.Builder var0) {
      farmAnimals(var0);
      var0.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
      commonSpawns(var0);
   }

   public static void endSpawns(MobSpawnSettings.Builder var0) {
      var0.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 4, 4));
   }
}
