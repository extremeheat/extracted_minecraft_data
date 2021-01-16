package net.minecraft.world.entity.npc;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class VillagerType {
   public static final VillagerType DESERT = register("desert");
   public static final VillagerType JUNGLE = register("jungle");
   public static final VillagerType PLAINS = register("plains");
   public static final VillagerType SAVANNA = register("savanna");
   public static final VillagerType SNOW = register("snow");
   public static final VillagerType SWAMP = register("swamp");
   public static final VillagerType TAIGA = register("taiga");
   private final String name;
   private static final Map<ResourceKey<Biome>, VillagerType> BY_BIOME = (Map)Util.make(Maps.newHashMap(), (var0) -> {
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

   private VillagerType(String var1) {
      super();
      this.name = var1;
   }

   public String toString() {
      return this.name;
   }

   private static VillagerType register(String var0) {
      return (VillagerType)Registry.register(Registry.VILLAGER_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new VillagerType(var0));
   }

   public static VillagerType byBiome(Optional<ResourceKey<Biome>> var0) {
      return (VillagerType)var0.flatMap((var0x) -> {
         return Optional.ofNullable(BY_BIOME.get(var0x));
      }).orElse(PLAINS);
   }
}
