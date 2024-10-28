package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MultiNoiseBiomeSourceParameterList {
   public static final Codec<MultiNoiseBiomeSourceParameterList> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(MultiNoiseBiomeSourceParameterList.Preset.CODEC.fieldOf("preset").forGetter((var0x) -> {
         return var0x.preset;
      }), RegistryOps.retrieveGetter(Registries.BIOME)).apply(var0, MultiNoiseBiomeSourceParameterList::new);
   });
   public static final Codec<Holder<MultiNoiseBiomeSourceParameterList>> CODEC;
   private final Preset preset;
   private final Climate.ParameterList<Holder<Biome>> parameters;

   public MultiNoiseBiomeSourceParameterList(Preset var1, HolderGetter<Biome> var2) {
      super();
      this.preset = var1;
      Preset.SourceProvider var10001 = var1.provider;
      Objects.requireNonNull(var2);
      this.parameters = var10001.apply(var2::getOrThrow);
   }

   public Climate.ParameterList<Holder<Biome>> parameters() {
      return this.parameters;
   }

   public static Map<Preset, Climate.ParameterList<ResourceKey<Biome>>> knownPresets() {
      return (Map)MultiNoiseBiomeSourceParameterList.Preset.BY_NAME.values().stream().collect(Collectors.toMap((var0) -> {
         return var0;
      }, (var0) -> {
         return var0.provider().apply((var0x) -> {
            return var0x;
         });
      }));
   }

   static {
      CODEC = RegistryFileCodec.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, DIRECT_CODEC);
   }

   public static record Preset(ResourceLocation id, SourceProvider provider) {
      final SourceProvider provider;
      public static final Preset NETHER = new Preset(new ResourceLocation("nether"), new SourceProvider() {
         public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1) {
            return new Climate.ParameterList(List.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var1.apply(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var1.apply(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var1.apply(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), var1.apply(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), var1.apply(Biomes.BASALT_DELTAS))));
         }
      });
      public static final Preset OVERWORLD = new Preset(new ResourceLocation("overworld"), new SourceProvider() {
         public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1) {
            return MultiNoiseBiomeSourceParameterList.Preset.generateOverworldBiomes(var1);
         }
      });
      static final Map<ResourceLocation, Preset> BY_NAME;
      public static final Codec<Preset> CODEC;

      public Preset(ResourceLocation var1, SourceProvider var2) {
         super();
         this.id = var1;
         this.provider = var2;
      }

      static <T> Climate.ParameterList<T> generateOverworldBiomes(Function<ResourceKey<Biome>, T> var0) {
         ImmutableList.Builder var1 = ImmutableList.builder();
         (new OverworldBiomeBuilder()).addBiomes((var2) -> {
            var1.add(var2.mapSecond(var0));
         });
         return new Climate.ParameterList(var1.build());
      }

      public Stream<ResourceKey<Biome>> usedBiomes() {
         return this.provider.apply((var0) -> {
            return var0;
         }).values().stream().map(Pair::getSecond).distinct();
      }

      public ResourceLocation id() {
         return this.id;
      }

      public SourceProvider provider() {
         return this.provider;
      }

      static {
         BY_NAME = (Map)Stream.of(NETHER, OVERWORLD).collect(Collectors.toMap(Preset::id, (var0) -> {
            return var0;
         }));
         CODEC = ResourceLocation.CODEC.flatXmap((var0) -> {
            return (DataResult)Optional.ofNullable((Preset)BY_NAME.get(var0)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error(() -> {
                  return "Unknown preset: " + String.valueOf(var0);
               });
            });
         }, (var0) -> {
            return DataResult.success(var0.id);
         });
      }

      @FunctionalInterface
      interface SourceProvider {
         <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1);
      }
   }
}
