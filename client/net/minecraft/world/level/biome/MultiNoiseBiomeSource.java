package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class MultiNoiseBiomeSource extends BiomeSource {
   private static final MultiNoiseBiomeSource.NoiseParameters DEFAULT_NOISE_PARAMETERS = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0D, 1.0D));
   public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.LONG.fieldOf("seed").forGetter((var0x) -> {
         return var0x.seed;
      }), RecordCodecBuilder.create((var0x) -> {
         return var0x.group(Biome.ClimateParameters.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(var0x, Pair::of);
      }).listOf().fieldOf("biomes").forGetter((var0x) -> {
         return var0x.parameters;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("temperature_noise").forGetter((var0x) -> {
         return var0x.temperatureParams;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("humidity_noise").forGetter((var0x) -> {
         return var0x.humidityParams;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("altitude_noise").forGetter((var0x) -> {
         return var0x.altitudeParams;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("weirdness_noise").forGetter((var0x) -> {
         return var0x.weirdnessParams;
      })).apply(var0, MultiNoiseBiomeSource::new);
   });
   public static final Codec<MultiNoiseBiomeSource> CODEC;
   private final MultiNoiseBiomeSource.NoiseParameters temperatureParams;
   private final MultiNoiseBiomeSource.NoiseParameters humidityParams;
   private final MultiNoiseBiomeSource.NoiseParameters altitudeParams;
   private final MultiNoiseBiomeSource.NoiseParameters weirdnessParams;
   private final NormalNoise temperatureNoise;
   private final NormalNoise humidityNoise;
   private final NormalNoise altitudeNoise;
   private final NormalNoise weirdnessNoise;
   private final List<Pair<Biome.ClimateParameters, Supplier<Biome>>> parameters;
   private final boolean useY;
   private final long seed;
   private final Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> preset;

   private MultiNoiseBiomeSource(long var1, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> var3, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> var4) {
      this(var1, var3, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, var4);
   }

   private MultiNoiseBiomeSource(long var1, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> var3, MultiNoiseBiomeSource.NoiseParameters var4, MultiNoiseBiomeSource.NoiseParameters var5, MultiNoiseBiomeSource.NoiseParameters var6, MultiNoiseBiomeSource.NoiseParameters var7) {
      this(var1, var3, var4, var5, var6, var7, Optional.empty());
   }

   private MultiNoiseBiomeSource(long var1, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> var3, MultiNoiseBiomeSource.NoiseParameters var4, MultiNoiseBiomeSource.NoiseParameters var5, MultiNoiseBiomeSource.NoiseParameters var6, MultiNoiseBiomeSource.NoiseParameters var7, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> var8) {
      super(var3.stream().map(Pair::getSecond));
      this.seed = var1;
      this.preset = var8;
      this.temperatureParams = var4;
      this.humidityParams = var5;
      this.altitudeParams = var6;
      this.weirdnessParams = var7;
      this.temperatureNoise = NormalNoise.create(new WorldgenRandom(var1), var4.firstOctave(), var4.amplitudes());
      this.humidityNoise = NormalNoise.create(new WorldgenRandom(var1 + 1L), var5.firstOctave(), var5.amplitudes());
      this.altitudeNoise = NormalNoise.create(new WorldgenRandom(var1 + 2L), var6.firstOctave(), var6.amplitudes());
      this.weirdnessNoise = NormalNoise.create(new WorldgenRandom(var1 + 3L), var7.firstOctave(), var7.amplitudes());
      this.parameters = var3;
      this.useY = false;
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long var1) {
      return new MultiNoiseBiomeSource(var1, this.parameters, this.temperatureParams, this.humidityParams, this.altitudeParams, this.weirdnessParams, this.preset);
   }

   private Optional<MultiNoiseBiomeSource.PresetInstance> preset() {
      return this.preset.map((var1) -> {
         return new MultiNoiseBiomeSource.PresetInstance((MultiNoiseBiomeSource.Preset)var1.getSecond(), (Registry)var1.getFirst(), this.seed);
      });
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      int var4 = this.useY ? var2 : 0;
      Biome.ClimateParameters var5 = new Biome.ClimateParameters((float)this.temperatureNoise.getValue((double)var1, (double)var4, (double)var3), (float)this.humidityNoise.getValue((double)var1, (double)var4, (double)var3), (float)this.altitudeNoise.getValue((double)var1, (double)var4, (double)var3), (float)this.weirdnessNoise.getValue((double)var1, (double)var4, (double)var3), 0.0F);
      return (Biome)this.parameters.stream().min(Comparator.comparing((var1x) -> {
         return ((Biome.ClimateParameters)var1x.getFirst()).fitness(var5);
      })).map(Pair::getSecond).map(Supplier::get).orElse(net.minecraft.data.worldgen.biome.Biomes.THE_VOID);
   }

   public boolean stable(long var1) {
      return this.seed == var1 && this.preset.isPresent() && Objects.equals(((Pair)this.preset.get()).getSecond(), MultiNoiseBiomeSource.Preset.NETHER);
   }

   // $FF: synthetic method
   MultiNoiseBiomeSource(long var1, List var3, Optional var4, Object var5) {
      this(var1, var3, var4);
   }

   static {
      CODEC = Codec.mapEither(MultiNoiseBiomeSource.PresetInstance.CODEC, DIRECT_CODEC).xmap((var0) -> {
         return (MultiNoiseBiomeSource)var0.map(MultiNoiseBiomeSource.PresetInstance::biomeSource, Function.identity());
      }, (var0) -> {
         return (Either)var0.preset().map(Either::left).orElseGet(() -> {
            return Either.right(var0);
         });
      }).codec();
   }

   public static class Preset {
      private static final Map<ResourceLocation, MultiNoiseBiomeSource.Preset> BY_NAME = Maps.newHashMap();
      public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(new ResourceLocation("nether"), (var0, var1, var2) -> {
         return new MultiNoiseBiomeSource(var2, ImmutableList.of(Pair.of(new Biome.ClimateParameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return (Biome)var1.getOrThrow(Biomes.NETHER_WASTES);
         }), Pair.of(new Biome.ClimateParameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), () -> {
            return (Biome)var1.getOrThrow(Biomes.SOUL_SAND_VALLEY);
         }), Pair.of(new Biome.ClimateParameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return (Biome)var1.getOrThrow(Biomes.CRIMSON_FOREST);
         }), Pair.of(new Biome.ClimateParameters(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), () -> {
            return (Biome)var1.getOrThrow(Biomes.WARPED_FOREST);
         }), Pair.of(new Biome.ClimateParameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
            return (Biome)var1.getOrThrow(Biomes.BASALT_DELTAS);
         })), Optional.of(Pair.of(var1, var0)));
      });
      private final ResourceLocation name;
      private final Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSource;

      public Preset(ResourceLocation var1, Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> var2) {
         super();
         this.name = var1;
         this.biomeSource = var2;
         BY_NAME.put(var1, this);
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> var1, long var2) {
         return (MultiNoiseBiomeSource)this.biomeSource.apply(this, var1, var2);
      }
   }

   static final class PresetInstance {
      public static final MapCodec<MultiNoiseBiomeSource.PresetInstance> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ResourceLocation.CODEC.flatXmap((var0x) -> {
            return (DataResult)Optional.ofNullable(MultiNoiseBiomeSource.Preset.BY_NAME.get(var0x)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error("Unknown preset: " + var0x);
            });
         }, (var0x) -> {
            return DataResult.success(var0x.name);
         }).fieldOf("preset").stable().forGetter(MultiNoiseBiomeSource.PresetInstance::preset), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(MultiNoiseBiomeSource.PresetInstance::biomes), Codec.LONG.fieldOf("seed").stable().forGetter(MultiNoiseBiomeSource.PresetInstance::seed)).apply(var0, var0.stable(MultiNoiseBiomeSource.PresetInstance::new));
      });
      private final MultiNoiseBiomeSource.Preset preset;
      private final Registry<Biome> biomes;
      private final long seed;

      private PresetInstance(MultiNoiseBiomeSource.Preset var1, Registry<Biome> var2, long var3) {
         super();
         this.preset = var1;
         this.biomes = var2;
         this.seed = var3;
      }

      public MultiNoiseBiomeSource.Preset preset() {
         return this.preset;
      }

      public Registry<Biome> biomes() {
         return this.biomes;
      }

      public long seed() {
         return this.seed;
      }

      public MultiNoiseBiomeSource biomeSource() {
         return this.preset.biomeSource(this.biomes, this.seed);
      }

      // $FF: synthetic method
      PresetInstance(MultiNoiseBiomeSource.Preset var1, Registry var2, long var3, Object var5) {
         this(var1, var2, var3);
      }
   }

   static class NoiseParameters {
      private final int firstOctave;
      private final DoubleList amplitudes;
      public static final Codec<MultiNoiseBiomeSource.NoiseParameters> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("firstOctave").forGetter(MultiNoiseBiomeSource.NoiseParameters::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(MultiNoiseBiomeSource.NoiseParameters::amplitudes)).apply(var0, MultiNoiseBiomeSource.NoiseParameters::new);
      });

      public NoiseParameters(int var1, List<Double> var2) {
         super();
         this.firstOctave = var1;
         this.amplitudes = new DoubleArrayList(var2);
      }

      public int firstOctave() {
         return this.firstOctave;
      }

      public DoubleList amplitudes() {
         return this.amplitudes;
      }
   }
}
