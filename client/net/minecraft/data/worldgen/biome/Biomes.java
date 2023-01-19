package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;

public abstract class Biomes {
   public Biomes() {
      super();
   }

   public static void bootstrap(BootstapContext<Biome> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      HolderGetter var2 = var0.lookup(Registries.CONFIGURED_CARVER);
      var0.register(net.minecraft.world.level.biome.Biomes.THE_VOID, OverworldBiomes.theVoid(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.PLAINS, OverworldBiomes.plains(var1, var2, false, false, false));
      var0.register(net.minecraft.world.level.biome.Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains(var1, var2, true, false, false));
      var0.register(net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS, OverworldBiomes.plains(var1, var2, false, true, false));
      var0.register(net.minecraft.world.level.biome.Biomes.ICE_SPIKES, OverworldBiomes.plains(var1, var2, false, true, true));
      var0.register(net.minecraft.world.level.biome.Biomes.DESERT, OverworldBiomes.desert(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.SWAMP, OverworldBiomes.swamp(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP, OverworldBiomes.mangroveSwamp(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.FOREST, OverworldBiomes.forest(var1, var2, false, false, false));
      var0.register(net.minecraft.world.level.biome.Biomes.FLOWER_FOREST, OverworldBiomes.forest(var1, var2, false, false, true));
      var0.register(net.minecraft.world.level.biome.Biomes.BIRCH_FOREST, OverworldBiomes.forest(var1, var2, true, false, false));
      var0.register(net.minecraft.world.level.biome.Biomes.DARK_FOREST, OverworldBiomes.darkForest(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest(var1, var2, true, true, false));
      var0.register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.TAIGA, OverworldBiomes.taiga(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.SNOWY_TAIGA, OverworldBiomes.taiga(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.SAVANNA, OverworldBiomes.savanna(var1, var2, false, false));
      var0.register(net.minecraft.world.level.biome.Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna(var1, var2, false, true));
      var0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna(var1, var2, true, false));
      var0.register(net.minecraft.world.level.biome.Biomes.JUNGLE, OverworldBiomes.jungle(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.BADLANDS, OverworldBiomes.badlands(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.ERODED_BADLANDS, OverworldBiomes.badlands(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.WOODED_BADLANDS, OverworldBiomes.badlands(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.MEADOW, OverworldBiomes.meadow(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.GROVE, OverworldBiomes.grove(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.RIVER, OverworldBiomes.river(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.FROZEN_RIVER, OverworldBiomes.river(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.BEACH, OverworldBiomes.beach(var1, var2, false, false));
      var0.register(net.minecraft.world.level.biome.Biomes.SNOWY_BEACH, OverworldBiomes.beach(var1, var2, true, false));
      var0.register(net.minecraft.world.level.biome.Biomes.STONY_SHORE, OverworldBiomes.beach(var1, var2, false, true));
      var0.register(net.minecraft.world.level.biome.Biomes.WARM_OCEAN, OverworldBiomes.warmOcean(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.OCEAN, OverworldBiomes.ocean(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.DEEP_OCEAN, OverworldBiomes.ocean(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.COLD_OCEAN, OverworldBiomes.coldOcean(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean(var1, var2, false));
      var0.register(net.minecraft.world.level.biome.Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean(var1, var2, true));
      var0.register(net.minecraft.world.level.biome.Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.LUSH_CAVES, OverworldBiomes.lushCaves(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.DEEP_DARK, OverworldBiomes.deepDark(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.NETHER_WASTES, NetherBiomes.netherWastes(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.WARPED_FOREST, NetherBiomes.warpedForest(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.THE_END, EndBiomes.theEnd(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.END_HIGHLANDS, EndBiomes.endHighlands(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.END_MIDLANDS, EndBiomes.endMidlands(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands(var1, var2));
      var0.register(net.minecraft.world.level.biome.Biomes.END_BARRENS, EndBiomes.endBarrens(var1, var2));
   }
}
