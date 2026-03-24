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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class MultiNoiseBiomeSourceParameterList {
   public static final Codec<MultiNoiseBiomeSourceParameterList> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(MultiNoiseBiomeSourceParameterList.Preset.CODEC.fieldOf("preset").forGetter((e) -> e.preset), RegistryOps.retrieveGetter(Registries.BIOME)).apply(i, MultiNoiseBiomeSourceParameterList::new));
   public static final Codec<Holder<MultiNoiseBiomeSourceParameterList>> CODEC;
   private final Preset preset;
   private final Climate.ParameterList<Holder<Biome>> parameters;

   public MultiNoiseBiomeSourceParameterList(final Preset preset, final HolderGetter<Biome> biomes) {
      super();
      this.preset = preset;
      Preset.SourceProvider var10001 = preset.provider;
      Objects.requireNonNull(biomes);
      this.parameters = var10001.<Holder<Biome>>apply(biomes::getOrThrow);
   }

   public Climate.ParameterList<Holder<Biome>> parameters() {
      return this.parameters;
   }

   public static Map<Preset, Climate.ParameterList<ResourceKey<Biome>>> knownPresets() {
      return (Map)MultiNoiseBiomeSourceParameterList.Preset.BY_NAME.values().stream().collect(Collectors.toMap((e) -> e, (e) -> e.provider().apply((k) -> k)));
   }

   static {
      CODEC = RegistryFileCodec.<Holder<MultiNoiseBiomeSourceParameterList>>create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, DIRECT_CODEC);
   }

   public static record Preset(Identifier id, SourceProvider provider) {
      public static final Preset NETHER = new Preset(Identifier.withDefaultNamespace("nether"), new SourceProvider() {
         public <T> Climate.ParameterList<T> apply(final Function<ResourceKey<Biome>, T> lookup) {
            return new Climate.ParameterList<T>(List.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), lookup.apply(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), lookup.apply(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), lookup.apply(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), lookup.apply(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), lookup.apply(Biomes.BASALT_DELTAS))));
         }
      });
      public static final Preset OVERWORLD = new Preset(Identifier.withDefaultNamespace("overworld"), new SourceProvider() {
         public <T> Climate.ParameterList<T> apply(final Function<ResourceKey<Biome>, T> lookup) {
            return MultiNoiseBiomeSourceParameterList.Preset.<T>generateOverworldBiomes(lookup);
         }
      });
      private static final Map<Identifier, Preset> BY_NAME;
      public static final Codec<Preset> CODEC;

      public Preset {
         super();
      }

      private static <T> Climate.ParameterList<T> generateOverworldBiomes(final Function<ResourceKey<Biome>, T> lookup) {
         ImmutableList.Builder<Pair<Climate.ParameterPoint, T>> builder = ImmutableList.builder();
         (new OverworldBiomeBuilder()).addBiomes((p) -> builder.add(p.mapSecond(lookup)));
         return new Climate.ParameterList<T>(builder.build());
      }

      public Stream<ResourceKey<Biome>> usedBiomes() {
         return this.provider.apply((e) -> e).values().stream().map(Pair::getSecond).distinct();
      }

      static {
         BY_NAME = (Map)Stream.of(NETHER, OVERWORLD).collect(Collectors.toMap(Preset::id, (p) -> p));
         CODEC = Identifier.CODEC.flatXmap((name) -> (DataResult)Optional.ofNullable((Preset)BY_NAME.get(name)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown preset: " + String.valueOf(name))), (p) -> DataResult.success(p.id));
      }

      @FunctionalInterface
      private interface SourceProvider {
         <T> Climate.ParameterList<T> apply(final Function<ResourceKey<Biome>, T> lookup);
      }
   }
}
