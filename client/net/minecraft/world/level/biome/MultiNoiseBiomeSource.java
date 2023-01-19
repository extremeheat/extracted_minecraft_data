package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class MultiNoiseBiomeSource extends BiomeSource {
   public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ExtraCodecs.nonEmptyList(
                     RecordCodecBuilder.create(
                           var0x -> var0x.group(
                                    Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst),
                                    Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)
                                 )
                                 .apply(var0x, Pair::of)
                        )
                        .listOf()
                  )
                  .xmap(Climate.ParameterList::new, Climate.ParameterList::values)
                  .fieldOf("biomes")
                  .forGetter(var0x -> var0x.parameters)
            )
            .apply(var0, MultiNoiseBiomeSource::new)
   );
   public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.PresetInstance.CODEC, DIRECT_CODEC)
      .xmap(
         var0 -> (MultiNoiseBiomeSource)var0.map(MultiNoiseBiomeSource.PresetInstance::biomeSource, Function.identity()),
         var0 -> (Either)var0.preset().map(Either::left).orElseGet(() -> Either.right(var0))
      )
      .codec();
   private final Climate.ParameterList<Holder<Biome>> parameters;
   private final Optional<MultiNoiseBiomeSource.PresetInstance> preset;

   private MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> var1) {
      this(var1, Optional.empty());
   }

   MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> var1, Optional<MultiNoiseBiomeSource.PresetInstance> var2) {
      super(var1.values().stream().map(Pair::getSecond));
      this.preset = var2;
      this.parameters = var1;
   }

   @Override
   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   private Optional<MultiNoiseBiomeSource.PresetInstance> preset() {
      return this.preset;
   }

   public boolean stable(MultiNoiseBiomeSource.Preset var1) {
      return this.preset.isPresent() && Objects.equals(this.preset.get().preset(), var1);
   }

   @Override
   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return this.getNoiseBiome(var4.sample(var1, var2, var3));
   }

   @VisibleForDebug
   public Holder<Biome> getNoiseBiome(Climate.TargetPoint var1) {
      return this.parameters.findValue(var1);
   }

   @Override
   public void addDebugInfo(List<String> var1, BlockPos var2, Climate.Sampler var3) {
      int var4 = QuartPos.fromBlock(var2.getX());
      int var5 = QuartPos.fromBlock(var2.getY());
      int var6 = QuartPos.fromBlock(var2.getZ());
      Climate.TargetPoint var7 = var3.sample(var4, var5, var6);
      float var8 = Climate.unquantizeCoord(var7.continentalness());
      float var9 = Climate.unquantizeCoord(var7.erosion());
      float var10 = Climate.unquantizeCoord(var7.temperature());
      float var11 = Climate.unquantizeCoord(var7.humidity());
      float var12 = Climate.unquantizeCoord(var7.weirdness());
      double var13 = (double)NoiseRouterData.peaksAndValleys(var12);
      OverworldBiomeBuilder var15 = new OverworldBiomeBuilder();
      var1.add(
         "Biome builder PV: "
            + OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(var13)
            + " C: "
            + var15.getDebugStringForContinentalness((double)var8)
            + " E: "
            + var15.getDebugStringForErosion((double)var9)
            + " T: "
            + var15.getDebugStringForTemperature((double)var10)
            + " H: "
            + var15.getDebugStringForHumidity((double)var11)
      );
   }

   public static class Preset {
      static final Map<ResourceLocation, MultiNoiseBiomeSource.Preset> BY_NAME = Maps.newHashMap();
      public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
         new ResourceLocation("nether"),
         var0 -> new Climate.ParameterList<>(
               ImmutableList.of(
                  Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var0.getOrThrow(Biomes.NETHER_WASTES)),
                  Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var0.getOrThrow(Biomes.SOUL_SAND_VALLEY)),
                  Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var0.getOrThrow(Biomes.CRIMSON_FOREST)),
                  Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), var0.getOrThrow(Biomes.WARPED_FOREST)),
                  Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), var0.getOrThrow(Biomes.BASALT_DELTAS))
               )
            )
      );
      public static final MultiNoiseBiomeSource.Preset OVERWORLD = new MultiNoiseBiomeSource.Preset(new ResourceLocation("overworld"), var0 -> {
         Builder var1 = ImmutableList.builder();
         new OverworldBiomeBuilder().addBiomes(var2 -> var1.add(var2.mapSecond(var0::getOrThrow)));
         return new Climate.ParameterList<>(var1.build());
      });
      final ResourceLocation name;
      private final Function<HolderGetter<Biome>, Climate.ParameterList<Holder<Biome>>> parameterSource;

      public Preset(ResourceLocation var1, Function<HolderGetter<Biome>, Climate.ParameterList<Holder<Biome>>> var2) {
         super();
         this.name = var1;
         this.parameterSource = var2;
         BY_NAME.put(var1, this);
      }

      @VisibleForDebug
      public static Stream<Pair<ResourceLocation, MultiNoiseBiomeSource.Preset>> getPresets() {
         return BY_NAME.entrySet().stream().map(var0 -> Pair.of((ResourceLocation)var0.getKey(), (MultiNoiseBiomeSource.Preset)var0.getValue()));
      }

      MultiNoiseBiomeSource biomeSource(MultiNoiseBiomeSource.PresetInstance var1, boolean var2) {
         Climate.ParameterList var3 = this.parameterSource.apply(var1.biomes());
         return new MultiNoiseBiomeSource(var3, var2 ? Optional.of(var1) : Optional.empty());
      }

      public MultiNoiseBiomeSource biomeSource(HolderGetter<Biome> var1, boolean var2) {
         return this.biomeSource(new MultiNoiseBiomeSource.PresetInstance(this, var1), var2);
      }

      public MultiNoiseBiomeSource biomeSource(HolderGetter<Biome> var1) {
         return this.biomeSource(var1, true);
      }

      public Stream<ResourceKey<Biome>> possibleBiomes(HolderGetter<Biome> var1) {
         return this.biomeSource(var1).possibleBiomes().stream().flatMap(var0 -> var0.unwrapKey().stream());
      }
   }

   static record PresetInstance(MultiNoiseBiomeSource.Preset b, HolderGetter<Biome> c) {
      private final MultiNoiseBiomeSource.Preset preset;
      private final HolderGetter<Biome> biomes;
      public static final MapCodec<MultiNoiseBiomeSource.PresetInstance> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  ResourceLocation.CODEC
                     .flatXmap(
                        var0x -> (DataResult)Optional.ofNullable(MultiNoiseBiomeSource.Preset.BY_NAME.get(var0x))
                              .map(DataResult::success)
                              .orElseGet(() -> DataResult.error("Unknown preset: " + var0x)),
                        var0x -> DataResult.success(var0x.name)
                     )
                     .fieldOf("preset")
                     .stable()
                     .forGetter(MultiNoiseBiomeSource.PresetInstance::preset),
                  RegistryOps.retrieveGetter(Registries.BIOME)
               )
               .apply(var0, var0.stable(MultiNoiseBiomeSource.PresetInstance::new))
      );

      PresetInstance(MultiNoiseBiomeSource.Preset var1, HolderGetter<Biome> var2) {
         super();
         this.preset = var1;
         this.biomes = var2;
      }

      public MultiNoiseBiomeSource biomeSource() {
         return this.preset.biomeSource(this, true);
      }
   }
}
