package net.minecraft.world.level.levelgen;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class RandomState {
   final PositionalRandomFactory random;
   private final HolderGetter<NormalNoise.NoiseParameters> noises;
   private final NoiseRouter router;
   private final Climate.Sampler sampler;
   private final SurfaceSystem surfaceSystem;
   private final PositionalRandomFactory aquiferRandom;
   private final PositionalRandomFactory oreRandom;
   private final Map<ResourceKey<NormalNoise.NoiseParameters>, NormalNoise> noiseIntances;
   private final Map<ResourceLocation, PositionalRandomFactory> positionalRandoms;

   public static RandomState create(HolderGetter.Provider var0, ResourceKey<NoiseGeneratorSettings> var1, long var2) {
      return create((NoiseGeneratorSettings)var0.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(var1).value(), var0.lookupOrThrow(Registries.NOISE), var2);
   }

   public static RandomState create(NoiseGeneratorSettings var0, HolderGetter<NormalNoise.NoiseParameters> var1, long var2) {
      return new RandomState(var0, var1, var2);
   }

   private RandomState(NoiseGeneratorSettings var1, HolderGetter<NormalNoise.NoiseParameters> var2, final long var3) {
      super();
      this.random = var1.getRandomSource().newInstance(var3).forkPositional();
      this.noises = var2;
      this.aquiferRandom = this.random.fromHashOf(ResourceLocation.withDefaultNamespace("aquifer")).forkPositional();
      this.oreRandom = this.random.fromHashOf(ResourceLocation.withDefaultNamespace("ore")).forkPositional();
      this.noiseIntances = new ConcurrentHashMap();
      this.positionalRandoms = new ConcurrentHashMap();
      this.surfaceSystem = new SurfaceSystem(this, var1.defaultBlock(), var1.seaLevel(), this.random);
      final boolean var5 = var1.useLegacyRandomSource();

      class 1NoiseWiringHelper implements DensityFunction.Visitor {
         private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();

         _NoiseWiringHelper/* $FF was: 1NoiseWiringHelper*/() {
            super();
         }

         private RandomSource newLegacyInstance(long var1) {
            return new LegacyRandomSource(var3 + var1);
         }

         public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder var1) {
            Holder var2 = var1.noiseData();
            NormalNoise var3x;
            if (var5) {
               if (var2.is(Noises.TEMPERATURE)) {
                  var3x = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(0L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                  return new DensityFunction.NoiseHolder(var2, var3x);
               }

               if (var2.is(Noises.VEGETATION)) {
                  var3x = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(1L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                  return new DensityFunction.NoiseHolder(var2, var3x);
               }

               if (var2.is(Noises.SHIFT)) {
                  var3x = NormalNoise.create(RandomState.this.random.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0, new double[0]));
                  return new DensityFunction.NoiseHolder(var2, var3x);
               }
            }

            var3x = RandomState.this.getOrCreateNoise((ResourceKey)var2.unwrapKey().orElseThrow());
            return new DensityFunction.NoiseHolder(var2, var3x);
         }

         private DensityFunction wrapNew(DensityFunction var1) {
            if (var1 instanceof BlendedNoise var2) {
               RandomSource var3x = var5 ? this.newLegacyInstance(0L) : RandomState.this.random.fromHashOf(ResourceLocation.withDefaultNamespace("terrain"));
               return var2.withNewRandom(var3x);
            } else {
               return (DensityFunction)(var1 instanceof DensityFunctions.EndIslandDensityFunction ? new DensityFunctions.EndIslandDensityFunction(var3) : var1);
            }
         }

         public DensityFunction apply(DensityFunction var1) {
            return (DensityFunction)this.wrapped.computeIfAbsent(var1, this::wrapNew);
         }
      }

      this.router = var1.noiseRouter().mapAll(new 1NoiseWiringHelper());
      DensityFunction.Visitor var6 = new DensityFunction.Visitor(this) {
         private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();

         private DensityFunction wrapNew(DensityFunction var1) {
            if (var1 instanceof DensityFunctions.HolderHolder var3) {
               return (DensityFunction)var3.function().value();
            } else if (var1 instanceof DensityFunctions.Marker var2) {
               return var2.wrapped();
            } else {
               return var1;
            }
         }

         public DensityFunction apply(DensityFunction var1) {
            return (DensityFunction)this.wrapped.computeIfAbsent(var1, this::wrapNew);
         }
      };
      this.sampler = new Climate.Sampler(this.router.temperature().mapAll(var6), this.router.vegetation().mapAll(var6), this.router.continents().mapAll(var6), this.router.erosion().mapAll(var6), this.router.depth().mapAll(var6), this.router.ridges().mapAll(var6), var1.spawnTarget());
   }

   public NormalNoise getOrCreateNoise(ResourceKey<NormalNoise.NoiseParameters> var1) {
      return (NormalNoise)this.noiseIntances.computeIfAbsent(var1, (var2) -> {
         return Noises.instantiate(this.noises, this.random, var1);
      });
   }

   public PositionalRandomFactory getOrCreateRandomFactory(ResourceLocation var1) {
      return (PositionalRandomFactory)this.positionalRandoms.computeIfAbsent(var1, (var2) -> {
         return this.random.fromHashOf(var1).forkPositional();
      });
   }

   public NoiseRouter router() {
      return this.router;
   }

   public Climate.Sampler sampler() {
      return this.sampler;
   }

   public SurfaceSystem surfaceSystem() {
      return this.surfaceSystem;
   }

   public PositionalRandomFactory aquiferRandom() {
      return this.aquiferRandom;
   }

   public PositionalRandomFactory oreRandom() {
      return this.oreRandom;
   }
}
