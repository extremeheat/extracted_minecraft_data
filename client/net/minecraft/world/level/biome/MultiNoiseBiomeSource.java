package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class MultiNoiseBiomeSource extends BiomeSource {
   public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.nonEmptyList(RecordCodecBuilder.create((var0x) -> {
         return var0x.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(var0x, Pair::of);
      }).listOf()).xmap(Climate.ParameterList::new, Climate.ParameterList::values).fieldOf("biomes").forGetter((var0x) -> {
         return var0x.parameters;
      })).apply(var0, MultiNoiseBiomeSource::new);
   });
   public static final Codec<MultiNoiseBiomeSource> CODEC;
   private final Climate.ParameterList<Holder<Biome>> parameters;
   private final Optional<PresetInstance> preset;

   private MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> var1) {
      this(var1, Optional.empty());
   }

   MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> var1, Optional<PresetInstance> var2) {
      super(var1.values().stream().map(Pair::getSecond));
      this.preset = var2;
      this.parameters = var1;
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   private Optional<PresetInstance> preset() {
      return this.preset;
   }

   public boolean stable(Preset var1) {
      return this.preset.isPresent() && Objects.equals(((PresetInstance)this.preset.get()).preset(), var1);
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return this.getNoiseBiome(var4.sample(var1, var2, var3));
   }

   @VisibleForDebug
   public Holder<Biome> getNoiseBiome(Climate.TargetPoint var1) {
      return (Holder)this.parameters.findValue(var1);
   }

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
      String var10001 = OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(var13);
      var1.add("Biome builder PV: " + var10001 + " C: " + var15.getDebugStringForContinentalness((double)var8) + " E: " + var15.getDebugStringForErosion((double)var9) + " T: " + var15.getDebugStringForTemperature((double)var10) + " H: " + var15.getDebugStringForHumidity((double)var11));
   }

   static {
      CODEC = Codec.mapEither(MultiNoiseBiomeSource.PresetInstance.CODEC, DIRECT_CODEC).xmap((var0) -> {
         return (MultiNoiseBiomeSource)var0.map(PresetInstance::biomeSource, Function.identity());
      }, (var0) -> {
         return (Either)var0.preset().map(Either::left).orElseGet(() -> {
            return Either.right(var0);
         });
      }).codec();
   }

   private static record PresetInstance(Preset b, Registry<Biome> c) {
      private final Preset preset;
      private final Registry<Biome> biomes;
      public static final MapCodec<PresetInstance> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ResourceLocation.CODEC.flatXmap((var0x) -> {
            return (DataResult)Optional.ofNullable((Preset)MultiNoiseBiomeSource.Preset.BY_NAME.get(var0x)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error("Unknown preset: " + var0x);
            });
         }, (var0x) -> {
            return DataResult.success(var0x.name);
         }).fieldOf("preset").stable().forGetter(PresetInstance::preset), RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(PresetInstance::biomes)).apply(var0, var0.stable(PresetInstance::new));
      });

      PresetInstance(Preset var1, Registry<Biome> var2) {
         super();
         this.preset = var1;
         this.biomes = var2;
      }

      public MultiNoiseBiomeSource biomeSource() {
         return this.preset.biomeSource(this, true);
      }

      public Preset preset() {
         return this.preset;
      }

      public Registry<Biome> biomes() {
         return this.biomes;
      }
   }

   public static class Preset {
      static final Map<ResourceLocation, Preset> BY_NAME = Maps.newHashMap();
      public static final Preset NETHER = new Preset(new ResourceLocation("nether"), (var0) -> {
         return new Climate.ParameterList(ImmutableList.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var0.getOrCreateHolderOrThrow(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var0.getOrCreateHolderOrThrow(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), var0.getOrCreateHolderOrThrow(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), var0.getOrCreateHolderOrThrow(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), var0.getOrCreateHolderOrThrow(Biomes.BASALT_DELTAS))));
      });
      public static final Preset OVERWORLD = new Preset(new ResourceLocation("overworld"), (var0) -> {
         ImmutableList.Builder var1 = ImmutableList.builder();
         (new OverworldBiomeBuilder()).addBiomes((var2) -> {
            Objects.requireNonNull(var0);
            var1.add(var2.mapSecond(var0::getOrCreateHolderOrThrow));
         });
         return new Climate.ParameterList(var1.build());
      });
      final ResourceLocation name;
      private final Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> parameterSource;

      public Preset(ResourceLocation var1, Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> var2) {
         super();
         this.name = var1;
         this.parameterSource = var2;
         BY_NAME.put(var1, this);
      }

      @VisibleForDebug
      public static Stream<Pair<ResourceLocation, Preset>> getPresets() {
         return BY_NAME.entrySet().stream().map((var0) -> {
            return Pair.of((ResourceLocation)var0.getKey(), (Preset)var0.getValue());
         });
      }

      MultiNoiseBiomeSource biomeSource(PresetInstance var1, boolean var2) {
         Climate.ParameterList var3 = (Climate.ParameterList)this.parameterSource.apply(var1.biomes());
         return new MultiNoiseBiomeSource(var3, var2 ? Optional.of(var1) : Optional.empty());
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> var1, boolean var2) {
         return this.biomeSource(new PresetInstance(this, var1), var2);
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> var1) {
         return this.biomeSource(var1, true);
      }

      public Stream<ResourceKey<Biome>> possibleBiomes() {
         return this.biomeSource(BuiltinRegistries.BIOME).possibleBiomes().stream().flatMap((var0) -> {
            return var0.unwrapKey().stream();
         });
      }
   }
}
