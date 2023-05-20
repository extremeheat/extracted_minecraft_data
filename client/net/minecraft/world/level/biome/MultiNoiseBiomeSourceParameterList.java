package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
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
   public static final Codec<MultiNoiseBiomeSourceParameterList> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               MultiNoiseBiomeSourceParameterList.Preset.CODEC.fieldOf("preset").forGetter(var0x -> var0x.preset),
               RegistryOps.retrieveGetter(Registries.BIOME)
            )
            .apply(var0, MultiNoiseBiomeSourceParameterList::new)
   );
   public static final Codec<Holder<MultiNoiseBiomeSourceParameterList>> CODEC = RegistryFileCodec.create(
      Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, DIRECT_CODEC
   );
   private final MultiNoiseBiomeSourceParameterList.Preset preset;
   private final Climate.ParameterList<Holder<Biome>> parameters;

   public MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset var1, HolderGetter<Biome> var2) {
      super();
      this.preset = var1;
      this.parameters = var1.provider.apply(var2::getOrThrow);
   }

   public Climate.ParameterList<Holder<Biome>> parameters() {
      return this.parameters;
   }

   public static Map<MultiNoiseBiomeSourceParameterList.Preset, Climate.ParameterList<ResourceKey<Biome>>> knownPresets() {
      return MultiNoiseBiomeSourceParameterList.Preset.BY_NAME
         .values()
         .stream()
         .collect(Collectors.toMap(var0 -> var0, var0 -> var0.provider().apply(var0x -> var0x)));
   }

   public static record Preset(ResourceLocation e, MultiNoiseBiomeSourceParameterList.Preset.SourceProvider f) {
      private final ResourceLocation id;
      final MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider;
      public static final MultiNoiseBiomeSourceParameterList.Preset NETHER = new MultiNoiseBiomeSourceParameterList.Preset(
         new ResourceLocation("nether"),
         new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
            @Override
            public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1) {
               return new Climate.ParameterList<>(
                  List.of(
                     Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var1.apply(Biomes.NETHER_WASTES)),
                     Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var1.apply(Biomes.SOUL_SAND_VALLEY)),
                     Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var1.apply(Biomes.CRIMSON_FOREST)),
                     Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), var1.apply(Biomes.WARPED_FOREST)),
                     Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), var1.apply(Biomes.BASALT_DELTAS))
                  )
               );
            }
         }
      );
      public static final MultiNoiseBiomeSourceParameterList.Preset OVERWORLD = new MultiNoiseBiomeSourceParameterList.Preset(
         new ResourceLocation("overworld"), new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
            @Override
            public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1) {
               return MultiNoiseBiomeSourceParameterList.Preset.generateOverworldBiomes(var1, OverworldBiomeBuilder.Modifier.NONE);
            }
         }
      );
      public static final MultiNoiseBiomeSourceParameterList.Preset OVERWORLD_UPDATE_1_20 = new MultiNoiseBiomeSourceParameterList.Preset(
         new ResourceLocation("overworld_update_1_20"), new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
            @Override
            public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1) {
               return MultiNoiseBiomeSourceParameterList.Preset.generateOverworldBiomes(var1, OverworldBiomeBuilder.Modifier.UPDATE_1_20);
            }
         }
      );
      static final Map<ResourceLocation, MultiNoiseBiomeSourceParameterList.Preset> BY_NAME = Stream.of(NETHER, OVERWORLD, OVERWORLD_UPDATE_1_20)
         .collect(Collectors.toMap(MultiNoiseBiomeSourceParameterList.Preset::id, var0 -> var0));
      public static final Codec<MultiNoiseBiomeSourceParameterList.Preset> CODEC = ResourceLocation.CODEC
         .flatXmap(
            var0 -> (DataResult)Optional.ofNullable(BY_NAME.get(var0))
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Unknown preset: " + var0)),
            var0 -> DataResult.success(var0.id)
         );

      public Preset(ResourceLocation var1, MultiNoiseBiomeSourceParameterList.Preset.SourceProvider var2) {
         super();
         this.id = var1;
         this.provider = var2;
      }

      static <T> Climate.ParameterList<T> generateOverworldBiomes(Function<ResourceKey<Biome>, T> var0, OverworldBiomeBuilder.Modifier var1) {
         Builder var2 = ImmutableList.builder();
         new OverworldBiomeBuilder(var1).addBiomes(var2x -> var2.add(var2x.mapSecond(var0)));
         return new Climate.ParameterList<>(var2.build());
      }

      public Stream<ResourceKey<Biome>> usedBiomes() {
         return this.provider.apply(var0 -> var0).values().stream().<ResourceKey<Biome>>map(Pair::getSecond).distinct();
      }

      @FunctionalInterface
      interface SourceProvider {
         <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1);
      }
   }
}
