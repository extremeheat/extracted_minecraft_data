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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.NoiseSampler;
import net.minecraft.world.level.levelgen.TerrainInfo;
import net.minecraft.world.level.levelgen.blending.Blender;

public class MultiNoiseBiomeSource extends BiomeSource {
   public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.nonEmptyList(RecordCodecBuilder.create((var0x) -> {
         return var0x.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(var0x, Pair::of);
      }).listOf()).xmap(Climate.ParameterList::new, Climate.ParameterList::values).fieldOf("biomes").forGetter((var0x) -> {
         return var0x.parameters;
      })).apply(var0, MultiNoiseBiomeSource::new);
   });
   public static final Codec<MultiNoiseBiomeSource> CODEC;
   private final Climate.ParameterList<Supplier<Biome>> parameters;
   private final Optional<MultiNoiseBiomeSource.PresetInstance> preset;

   private MultiNoiseBiomeSource(Climate.ParameterList<Supplier<Biome>> var1) {
      this(var1, Optional.empty());
   }

   MultiNoiseBiomeSource(Climate.ParameterList<Supplier<Biome>> var1, Optional<MultiNoiseBiomeSource.PresetInstance> var2) {
      super(var1.values().stream().map(Pair::getSecond));
      this.preset = var2;
      this.parameters = var1;
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long var1) {
      return this;
   }

   private Optional<MultiNoiseBiomeSource.PresetInstance> preset() {
      return this.preset;
   }

   public boolean stable(MultiNoiseBiomeSource.Preset var1) {
      return this.preset.isPresent() && Objects.equals(((MultiNoiseBiomeSource.PresetInstance)this.preset.get()).preset(), var1);
   }

   public Biome getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return this.getNoiseBiome(var4.sample(var1, var2, var3));
   }

   @VisibleForDebug
   public Biome getNoiseBiome(Climate.TargetPoint var1) {
      return (Biome)((Supplier)this.parameters.findValue(var1, () -> {
         return net.minecraft.data.worldgen.biome.Biomes.THE_VOID;
      })).get();
   }

   public void addMultinoiseDebugInfo(List<String> var1, BlockPos var2, Climate.Sampler var3) {
      int var4 = QuartPos.fromBlock(var2.getX());
      int var5 = QuartPos.fromBlock(var2.getY());
      int var6 = QuartPos.fromBlock(var2.getZ());
      Climate.TargetPoint var7 = var3.sample(var4, var5, var6);
      float var8 = Climate.unquantizeCoord(var7.continentalness());
      float var9 = Climate.unquantizeCoord(var7.erosion());
      float var10 = Climate.unquantizeCoord(var7.temperature());
      float var11 = Climate.unquantizeCoord(var7.humidity());
      float var12 = Climate.unquantizeCoord(var7.weirdness());
      double var13 = (double)TerrainShaper.peaksAndValleys(var12);
      DecimalFormat var15 = new DecimalFormat("0.000");
      String var10001 = var15.format((double)var8);
      var1.add("Multinoise C: " + var10001 + " E: " + var15.format((double)var9) + " T: " + var15.format((double)var10) + " H: " + var15.format((double)var11) + " W: " + var15.format((double)var12));
      OverworldBiomeBuilder var16 = new OverworldBiomeBuilder();
      var10001 = OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(var13);
      var1.add("Biome builder PV: " + var10001 + " C: " + var16.getDebugStringForContinentalness((double)var8) + " E: " + var16.getDebugStringForErosion((double)var9) + " T: " + var16.getDebugStringForTemperature((double)var10) + " H: " + var16.getDebugStringForHumidity((double)var11));
      if (var3 instanceof NoiseSampler) {
         NoiseSampler var17 = (NoiseSampler)var3;
         TerrainInfo var18 = var17.terrainInfo(var2.getX(), var2.getZ(), var8, var12, var9, Blender.empty());
         var10001 = var15.format(var13);
         var1.add("Terrain PV: " + var10001 + " O: " + var15.format(var18.offset()) + " F: " + var15.format(var18.factor()) + " JA: " + var15.format(var18.jaggedness()));
      }
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

   private static record PresetInstance(MultiNoiseBiomeSource.Preset b, Registry<Biome> c) {
      private final MultiNoiseBiomeSource.Preset preset;
      private final Registry<Biome> biomes;
      public static final MapCodec<MultiNoiseBiomeSource.PresetInstance> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ResourceLocation.CODEC.flatXmap((var0x) -> {
            return (DataResult)Optional.ofNullable((MultiNoiseBiomeSource.Preset)MultiNoiseBiomeSource.Preset.BY_NAME.get(var0x)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error("Unknown preset: " + var0x);
            });
         }, (var0x) -> {
            return DataResult.success(var0x.name);
         }).fieldOf("preset").stable().forGetter(MultiNoiseBiomeSource.PresetInstance::preset), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(MultiNoiseBiomeSource.PresetInstance::biomes)).apply(var0, var0.stable(MultiNoiseBiomeSource.PresetInstance::new));
      });

      PresetInstance(MultiNoiseBiomeSource.Preset var1, Registry<Biome> var2) {
         super();
         this.preset = var1;
         this.biomes = var2;
      }

      public MultiNoiseBiomeSource biomeSource() {
         return this.preset.biomeSource(this, true);
      }

      public MultiNoiseBiomeSource.Preset preset() {
         return this.preset;
      }

      public Registry<Biome> biomes() {
         return this.biomes;
      }
   }

   public static class Preset {
      static final Map<ResourceLocation, MultiNoiseBiomeSource.Preset> BY_NAME = Maps.newHashMap();
      public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(new ResourceLocation("nether"), (var0) -> {
         return new Climate.ParameterList(ImmutableList.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return (Biome)var0.getOrThrow(Biomes.NETHER_WASTES);
         }), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return (Biome)var0.getOrThrow(Biomes.SOUL_SAND_VALLEY);
         }), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return (Biome)var0.getOrThrow(Biomes.CRIMSON_FOREST);
         }), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), () -> {
            return (Biome)var0.getOrThrow(Biomes.WARPED_FOREST);
         }), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
            return (Biome)var0.getOrThrow(Biomes.BASALT_DELTAS);
         })));
      });
      public static final MultiNoiseBiomeSource.Preset OVERWORLD = new MultiNoiseBiomeSource.Preset(new ResourceLocation("overworld"), (var0) -> {
         Builder var1 = ImmutableList.builder();
         (new OverworldBiomeBuilder()).addBiomes((var2) -> {
            var1.add(var2.mapSecond((var1x) -> {
               return () -> {
                  return (Biome)var0.getOrThrow(var1x);
               };
            }));
         });
         return new Climate.ParameterList(var1.build());
      });
      final ResourceLocation name;
      private final Function<Registry<Biome>, Climate.ParameterList<Supplier<Biome>>> parameterSource;

      public Preset(ResourceLocation var1, Function<Registry<Biome>, Climate.ParameterList<Supplier<Biome>>> var2) {
         super();
         this.name = var1;
         this.parameterSource = var2;
         BY_NAME.put(var1, this);
      }

      MultiNoiseBiomeSource biomeSource(MultiNoiseBiomeSource.PresetInstance var1, boolean var2) {
         Climate.ParameterList var3 = (Climate.ParameterList)this.parameterSource.apply(var1.biomes());
         return new MultiNoiseBiomeSource(var3, var2 ? Optional.of(var1) : Optional.empty());
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> var1, boolean var2) {
         return this.biomeSource(new MultiNoiseBiomeSource.PresetInstance(this, var1), var2);
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> var1) {
         return this.biomeSource(var1, true);
      }
   }
}
