package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.Layers;

public class OverworldBiomeSource extends BiomeSource {
   public static final Codec<OverworldBiomeSource> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.LONG.fieldOf("seed").stable().forGetter((var0x) -> {
         return var0x.seed;
      }), Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", false, Lifecycle.stable()).forGetter((var0x) -> {
         return var0x.legacyBiomeInitLayer;
      }), Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter((var0x) -> {
         return var0x.largeBiomes;
      }), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((var0x) -> {
         return var0x.biomes;
      })).apply(var0, var0.stable(OverworldBiomeSource::new));
   });
   private final Layer noiseBiomeLayer;
   private static final List<ResourceKey<Biome>> POSSIBLE_BIOMES;
   private final long seed;
   private final boolean legacyBiomeInitLayer;
   private final boolean largeBiomes;
   private final Registry<Biome> biomes;

   public OverworldBiomeSource(long var1, boolean var3, boolean var4, Registry<Biome> var5) {
      super(POSSIBLE_BIOMES.stream().map((var1x) -> {
         return () -> {
            return (Biome)var5.getOrThrow(var1x);
         };
      }));
      this.seed = var1;
      this.legacyBiomeInitLayer = var3;
      this.largeBiomes = var4;
      this.biomes = var5;
      this.noiseBiomeLayer = Layers.getDefaultLayer(var1, var3, var4 ? 6 : 4, 4);
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long var1) {
      return new OverworldBiomeSource(var1, this.legacyBiomeInitLayer, this.largeBiomes, this.biomes);
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      return this.noiseBiomeLayer.get(this.biomes, var1, var3);
   }

   static {
      POSSIBLE_BIOMES = ImmutableList.of(Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, new ResourceKey[]{Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU});
   }
}
