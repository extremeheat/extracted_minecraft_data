package net.minecraft.world.entity.npc;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public interface VillagerType {
   VillagerType DESERT = register("desert");
   VillagerType JUNGLE = register("jungle");
   VillagerType PLAINS = register("plains");
   VillagerType SAVANNA = register("savanna");
   VillagerType SNOW = register("snow");
   VillagerType SWAMP = register("swamp");
   VillagerType TAIGA = register("taiga");
   Map<Biome, VillagerType> BY_BIOME = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put(Biomes.BADLANDS, DESERT);
      var0.put(Biomes.BADLANDS_PLATEAU, DESERT);
      var0.put(Biomes.DESERT, DESERT);
      var0.put(Biomes.DESERT_HILLS, DESERT);
      var0.put(Biomes.DESERT_LAKES, DESERT);
      var0.put(Biomes.ERODED_BADLANDS, DESERT);
      var0.put(Biomes.MODIFIED_BADLANDS_PLATEAU, DESERT);
      var0.put(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DESERT);
      var0.put(Biomes.WOODED_BADLANDS_PLATEAU, DESERT);
      var0.put(Biomes.BAMBOO_JUNGLE, JUNGLE);
      var0.put(Biomes.BAMBOO_JUNGLE_HILLS, JUNGLE);
      var0.put(Biomes.JUNGLE, JUNGLE);
      var0.put(Biomes.JUNGLE_EDGE, JUNGLE);
      var0.put(Biomes.JUNGLE_HILLS, JUNGLE);
      var0.put(Biomes.MODIFIED_JUNGLE, JUNGLE);
      var0.put(Biomes.MODIFIED_JUNGLE_EDGE, JUNGLE);
      var0.put(Biomes.SAVANNA_PLATEAU, SAVANNA);
      var0.put(Biomes.SAVANNA, SAVANNA);
      var0.put(Biomes.SHATTERED_SAVANNA, SAVANNA);
      var0.put(Biomes.SHATTERED_SAVANNA_PLATEAU, SAVANNA);
      var0.put(Biomes.DEEP_FROZEN_OCEAN, SNOW);
      var0.put(Biomes.FROZEN_OCEAN, SNOW);
      var0.put(Biomes.FROZEN_RIVER, SNOW);
      var0.put(Biomes.ICE_SPIKES, SNOW);
      var0.put(Biomes.SNOWY_BEACH, SNOW);
      var0.put(Biomes.SNOWY_MOUNTAINS, SNOW);
      var0.put(Biomes.SNOWY_TAIGA, SNOW);
      var0.put(Biomes.SNOWY_TAIGA_HILLS, SNOW);
      var0.put(Biomes.SNOWY_TAIGA_MOUNTAINS, SNOW);
      var0.put(Biomes.SNOWY_TUNDRA, SNOW);
      var0.put(Biomes.SWAMP, SWAMP);
      var0.put(Biomes.SWAMP_HILLS, SWAMP);
      var0.put(Biomes.GIANT_SPRUCE_TAIGA, TAIGA);
      var0.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, TAIGA);
      var0.put(Biomes.GIANT_TREE_TAIGA, TAIGA);
      var0.put(Biomes.GIANT_TREE_TAIGA_HILLS, TAIGA);
      var0.put(Biomes.GRAVELLY_MOUNTAINS, TAIGA);
      var0.put(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, TAIGA);
      var0.put(Biomes.MOUNTAIN_EDGE, TAIGA);
      var0.put(Biomes.MOUNTAINS, TAIGA);
      var0.put(Biomes.TAIGA, TAIGA);
      var0.put(Biomes.TAIGA_HILLS, TAIGA);
      var0.put(Biomes.TAIGA_MOUNTAINS, TAIGA);
      var0.put(Biomes.WOODED_MOUNTAINS, TAIGA);
   });

   static VillagerType register(final String var0) {
      return (VillagerType)Registry.register(Registry.VILLAGER_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new VillagerType() {
         public String toString() {
            return var0;
         }
      });
   }

   static VillagerType byBiome(Biome var0) {
      return (VillagerType)BY_BIOME.getOrDefault(var0, PLAINS);
   }
}
