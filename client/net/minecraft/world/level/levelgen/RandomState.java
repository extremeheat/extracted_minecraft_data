package net.minecraft.world.level.levelgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class RandomState {
   private final PositionalRandomFactory random;
   private final HolderGetter<NormalNoise.NoiseParameters> noises;
   private final NoiseRouter router;
   private final Climate.Sampler sampler;
   private final SurfaceSystem surfaceSystem;
   private final PositionalRandomFactory aquiferRandom;
   private final PositionalRandomFactory oreRandom;
   private final Map<ResourceKey<NormalNoise.NoiseParameters>, NormalNoise> noiseIntances;
   private final Map<Identifier, PositionalRandomFactory> positionalRandoms;

   public static RandomState create(final HolderGetter.Provider holders, final ResourceKey<NoiseGeneratorSettings> noiseSettings, final long seed) {
      return create((NoiseGeneratorSettings)holders.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(noiseSettings).value(), holders.lookupOrThrow(Registries.NOISE), seed);
   }

   public static RandomState create(final NoiseGeneratorSettings settings, final HolderGetter<NormalNoise.NoiseParameters> noises, final long seed) {
      return new RandomState(settings, noises, seed);
   }

   private RandomState(final NoiseGeneratorSettings settings, final HolderGetter<NormalNoise.NoiseParameters> noises, final long seed) {
      super();
      this.random = settings.getRandomSource().newInstance(seed).forkPositional();
      this.noises = noises;
      this.aquiferRandom = this.random.fromHashOf(Identifier.withDefaultNamespace("aquifer")).forkPositional();
      this.oreRandom = this.random.fromHashOf(Identifier.withDefaultNamespace("ore")).forkPositional();
      this.noiseIntances = new ConcurrentHashMap();
      this.positionalRandoms = new ConcurrentHashMap();
      this.surfaceSystem = new SurfaceSystem(this, settings.defaultBlock(), settings.seaLevel(), this.random);
      final boolean useLegacyInit = settings.useLegacyRandomSource();

      class NoiseWiringHelper implements DensityFunction.Visitor {
         private final Map<DensityFunction, DensityFunction> wrapped;

         NoiseWiringHelper() {
            Objects.requireNonNull(RandomState.this);
            super();
            this.wrapped = new HashMap();
         }

         private RandomSource newLegacyInstance(final long seedOffset) {
            return new LegacyRandomSource(seed + seedOffset);
         }

         public DensityFunction.NoiseHolder visitNoise(final DensityFunction.NoiseHolder noise) {
            Holder<NormalNoise.NoiseParameters> noiseData = noise.noiseData();
            if (useLegacyInit) {
               if (noiseData.is(Noises.TEMPERATURE)) {
                  NormalNoise newNoise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(0L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                  return new DensityFunction.NoiseHolder(noiseData, newNoise);
               }

               if (noiseData.is(Noises.VEGETATION)) {
                  NormalNoise newNoise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(1L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                  return new DensityFunction.NoiseHolder(noiseData, newNoise);
               }

               if (noiseData.is(Noises.SHIFT)) {
                  NormalNoise newOffsetNoise = NormalNoise.create(RandomState.this.random.fromHashOf(Noises.SHIFT.identifier()), new NormalNoise.NoiseParameters(0, 0.0, new double[0]));
                  return new DensityFunction.NoiseHolder(noiseData, newOffsetNoise);
               }
            }

            NormalNoise instantiate = RandomState.this.getOrCreateNoise((ResourceKey)noiseData.unwrapKey().orElseThrow());
            return new DensityFunction.NoiseHolder(noiseData, instantiate);
         }

         private DensityFunction wrapNew(final DensityFunction function) {
            if (function instanceof BlendedNoise noise) {
               RandomSource terrainRandom = useLegacyInit ? this.newLegacyInstance(0L) : RandomState.this.random.fromHashOf(Identifier.withDefaultNamespace("terrain"));
               return noise.withNewRandom(terrainRandom);
            } else {
               return (DensityFunction)(function instanceof DensityFunctions.EndIslandDensityFunction ? new DensityFunctions.EndIslandDensityFunction(seed) : function);
            }
         }

         public DensityFunction apply(final DensityFunction function) {
            return (DensityFunction)this.wrapped.computeIfAbsent(function, this::wrapNew);
         }
      }

      this.router = settings.noiseRouter().mapAll(new NoiseWiringHelper());
      DensityFunction.Visitor noiseFlattener = new DensityFunction.Visitor() {
         private final Map<DensityFunction, DensityFunction> wrapped;

         {
            Objects.requireNonNull(RandomState.this);
            this.wrapped = new HashMap();
         }

         private DensityFunction wrapNew(final DensityFunction function) {
            if (function instanceof DensityFunctions.HolderHolder holder) {
               return (DensityFunction)holder.function().value();
            } else if (function instanceof DensityFunctions.Marker marker) {
               return marker.wrapped();
            } else {
               return function;
            }
         }

         public DensityFunction apply(final DensityFunction input) {
            return (DensityFunction)this.wrapped.computeIfAbsent(input, this::wrapNew);
         }
      };
      this.sampler = new Climate.Sampler(this.router.temperature().mapAll(noiseFlattener), this.router.vegetation().mapAll(noiseFlattener), this.router.continents().mapAll(noiseFlattener), this.router.erosion().mapAll(noiseFlattener), this.router.depth().mapAll(noiseFlattener), this.router.ridges().mapAll(noiseFlattener), settings.spawnTarget());
   }

   public NormalNoise getOrCreateNoise(final ResourceKey<NormalNoise.NoiseParameters> noise) {
      return (NormalNoise)this.noiseIntances.computeIfAbsent(noise, (key) -> Noises.instantiate(this.noises, this.random, noise));
   }

   public PositionalRandomFactory getOrCreateRandomFactory(final Identifier name) {
      return (PositionalRandomFactory)this.positionalRandoms.computeIfAbsent(name, (key) -> this.random.fromHashOf(name).forkPositional());
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
