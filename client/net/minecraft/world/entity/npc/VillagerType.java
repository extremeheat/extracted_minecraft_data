package net.minecraft.world.entity.npc;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class VillagerType {
   public static final ResourceKey<VillagerType> DESERT = createKey("desert");
   public static final ResourceKey<VillagerType> JUNGLE = createKey("jungle");
   public static final ResourceKey<VillagerType> PLAINS = createKey("plains");
   public static final ResourceKey<VillagerType> SAVANNA = createKey("savanna");
   public static final ResourceKey<VillagerType> SNOW = createKey("snow");
   public static final ResourceKey<VillagerType> SWAMP = createKey("swamp");
   public static final ResourceKey<VillagerType> TAIGA = createKey("taiga");
   public static final Codec<Holder<VillagerType>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<VillagerType>> STREAM_CODEC;
   private static final Map<ResourceKey<Biome>, ResourceKey<VillagerType>> BY_BIOME;

   public VillagerType() {
      super();
   }

   private static ResourceKey<VillagerType> createKey(String var0) {
      return ResourceKey.create(Registries.VILLAGER_TYPE, Identifier.withDefaultNamespace(var0));
   }

   private static VillagerType register(Registry<VillagerType> var0, ResourceKey<VillagerType> var1) {
      return (VillagerType)Registry.register(var0, (ResourceKey)var1, new VillagerType());
   }

   public static VillagerType bootstrap(Registry<VillagerType> var0) {
      register(var0, DESERT);
      register(var0, JUNGLE);
      register(var0, PLAINS);
      register(var0, SAVANNA);
      register(var0, SNOW);
      register(var0, SWAMP);
      return register(var0, TAIGA);
   }

   public static ResourceKey<VillagerType> byBiome(Holder<Biome> var0) {
      Optional var10000 = var0.unwrapKey();
      Map var10001 = BY_BIOME;
      Objects.requireNonNull(var10001);
      return (ResourceKey)var10000.map(var10001::get).orElse(PLAINS);
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<VillagerType>>create(Registries.VILLAGER_TYPE);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.VILLAGER_TYPE);
      BY_BIOME = (Map)Util.make(Maps.newHashMap(), (var0) -> {
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
   }
}
