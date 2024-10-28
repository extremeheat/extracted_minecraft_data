package net.minecraft.world.entity.npc;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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
      var0.put(Biomes.DESERT, DESERT);
      var0.put(Biomes.ERODED_BADLANDS, DESERT);
      var0.put(Biomes.WOODED_BADLANDS, DESERT);
      var0.put(Biomes.BAMBOO_JUNGLE, JUNGLE);
      var0.put(Biomes.JUNGLE, JUNGLE);
      var0.put(Biomes.SPARSE_JUNGLE, JUNGLE);
      var0.put(Biomes.SAVANNA_PLATEAU, SAVANNA);
      var0.put(Biomes.SAVANNA, SAVANNA);
      var0.put(Biomes.WINDSWEPT_SAVANNA, SAVANNA);
      var0.put(Biomes.DEEP_FROZEN_OCEAN, SNOW);
      var0.put(Biomes.FROZEN_OCEAN, SNOW);
      var0.put(Biomes.FROZEN_RIVER, SNOW);
      var0.put(Biomes.ICE_SPIKES, SNOW);
      var0.put(Biomes.SNOWY_BEACH, SNOW);
      var0.put(Biomes.SNOWY_TAIGA, SNOW);
      var0.put(Biomes.SNOWY_PLAINS, SNOW);
      var0.put(Biomes.GROVE, SNOW);
      var0.put(Biomes.SNOWY_SLOPES, SNOW);
      var0.put(Biomes.FROZEN_PEAKS, SNOW);
      var0.put(Biomes.JAGGED_PEAKS, SNOW);
      var0.put(Biomes.SWAMP, SWAMP);
      var0.put(Biomes.MANGROVE_SWAMP, SWAMP);
      var0.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, TAIGA);
      var0.put(Biomes.OLD_GROWTH_PINE_TAIGA, TAIGA);
      var0.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, TAIGA);
      var0.put(Biomes.WINDSWEPT_HILLS, TAIGA);
      var0.put(Biomes.TAIGA, TAIGA);
      var0.put(Biomes.WINDSWEPT_FOREST, TAIGA);
   });

   private VillagerType(String var1) {
      super();
      this.name = var1;
   }

   public String toString() {
      return this.name;
   }

   private static VillagerType register(String var0) {
      return (VillagerType)Registry.register(BuiltInRegistries.VILLAGER_TYPE, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new VillagerType(var0));
   }

   public static VillagerType byBiome(Holder<Biome> var0) {
      Optional var10000 = var0.unwrapKey();
      Map var10001 = BY_BIOME;
      Objects.requireNonNull(var10001);
      return (VillagerType)var10000.map(var10001::get).orElse(PLAINS);
   }
}
