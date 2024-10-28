package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public abstract class BiomeData {
   public BiomeData() {
      super();
   }

   public static void bootstrap(BootstrapContext<Biome> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      HolderGetter var2 = var0.lookup(Registries.CONFIGURED_CARVER);
      var0.register(Biomes.THE_VOID, OverworldBiomes.theVoid(var1, var2));
      var0.register(Biomes.PLAINS, OverworldBiomes.plains(var1, var2, false, false, false));
      var0.register(Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains(var1, var2, true, false, false));
      var0.register(Biomes.SNOWY_PLAINS, OverworldBiomes.plains(var1, var2, false, true, false));
      var0.register(Biomes.ICE_SPIKES, OverworldBiomes.plains(var1, var2, false, true, true));
      var0.register(Biomes.DESERT, OverworldBiomes.desert(var1, var2));
      var0.register(Biomes.SWAMP, OverworldBiomes.swamp(var1, var2));
      var0.register(Biomes.MANGROVE_SWAMP, OverworldBiomes.mangroveSwamp(var1, var2));
      var0.register(Biomes.FOREST, OverworldBiomes.forest(var1, var2, false, false, false));
      var0.register(Biomes.FLOWER_FOREST, OverworldBiomes.forest(var1, var2, false, false, true));
      var0.register(Biomes.BIRCH_FOREST, OverworldBiomes.forest(var1, var2, true, false, false));
      var0.register(Biomes.DARK_FOREST, OverworldBiomes.darkForest(var1, var2));
      var0.register(Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest(var1, var2, true, true, false));
      var0.register(Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga(var1, var2, false));
      var0.register(Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga(var1, var2, true));
      var0.register(Biomes.TAIGA, OverworldBiomes.taiga(var1, var2, false));
      var0.register(Biomes.SNOWY_TAIGA, OverworldBiomes.taiga(var1, var2, true));
      var0.register(Biomes.SAVANNA, OverworldBiomes.savanna(var1, var2, false, false));
      var0.register(Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna(var1, var2, false, true));
      var0.register(Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills(var1, var2, false));
      var0.register(Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills(var1, var2, false));
      var0.register(Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills(var1, var2, true));
      var0.register(Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna(var1, var2, true, false));
      var0.register(Biomes.JUNGLE, OverworldBiomes.jungle(var1, var2));
      var0.register(Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle(var1, var2));
      var0.register(Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle(var1, var2));
      var0.register(Biomes.BADLANDS, OverworldBiomes.badlands(var1, var2, false));
      var0.register(Biomes.ERODED_BADLANDS, OverworldBiomes.badlands(var1, var2, false));
      var0.register(Biomes.WOODED_BADLANDS, OverworldBiomes.badlands(var1, var2, true));
      var0.register(Biomes.MEADOW, OverworldBiomes.meadowOrCherryGrove(var1, var2, false));
      var0.register(Biomes.CHERRY_GROVE, OverworldBiomes.meadowOrCherryGrove(var1, var2, true));
      var0.register(Biomes.GROVE, OverworldBiomes.grove(var1, var2));
      var0.register(Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes(var1, var2));
      var0.register(Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks(var1, var2));
      var0.register(Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks(var1, var2));
      var0.register(Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks(var1, var2));
      var0.register(Biomes.RIVER, OverworldBiomes.river(var1, var2, false));
      var0.register(Biomes.FROZEN_RIVER, OverworldBiomes.river(var1, var2, true));
      var0.register(Biomes.BEACH, OverworldBiomes.beach(var1, var2, false, false));
      var0.register(Biomes.SNOWY_BEACH, OverworldBiomes.beach(var1, var2, true, false));
      var0.register(Biomes.STONY_SHORE, OverworldBiomes.beach(var1, var2, false, true));
      var0.register(Biomes.WARM_OCEAN, OverworldBiomes.warmOcean(var1, var2));
      var0.register(Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(var1, var2, false));
      var0.register(Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(var1, var2, true));
      var0.register(Biomes.OCEAN, OverworldBiomes.ocean(var1, var2, false));
      var0.register(Biomes.DEEP_OCEAN, OverworldBiomes.ocean(var1, var2, true));
      var0.register(Biomes.COLD_OCEAN, OverworldBiomes.coldOcean(var1, var2, false));
      var0.register(Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean(var1, var2, true));
      var0.register(Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean(var1, var2, false));
      var0.register(Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean(var1, var2, true));
      var0.register(Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields(var1, var2));
      var0.register(Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves(var1, var2));
      var0.register(Biomes.LUSH_CAVES, OverworldBiomes.lushCaves(var1, var2));
      var0.register(Biomes.DEEP_DARK, OverworldBiomes.deepDark(var1, var2));
      var0.register(Biomes.NETHER_WASTES, NetherBiomes.netherWastes(var1, var2));
      var0.register(Biomes.WARPED_FOREST, NetherBiomes.warpedForest(var1, var2));
      var0.register(Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest(var1, var2));
      var0.register(Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley(var1, var2));
      var0.register(Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas(var1, var2));
      var0.register(Biomes.THE_END, EndBiomes.theEnd(var1, var2));
      var0.register(Biomes.END_HIGHLANDS, EndBiomes.endHighlands(var1, var2));
      var0.register(Biomes.END_MIDLANDS, EndBiomes.endMidlands(var1, var2));
      var0.register(Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands(var1, var2));
      var0.register(Biomes.END_BARRENS, EndBiomes.endBarrens(var1, var2));
   }
}
