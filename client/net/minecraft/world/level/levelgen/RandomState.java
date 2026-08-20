package net.minecraft.world.level.levelgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.generator.EndIslandFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.BinaryFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.MarkerFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class RandomState {
   private final long seed;
   private final PositionalRandomFactory random;
   private final HolderGetter<NormalNoise> noises;
   private final NoiseRouter router;
   private final Climate.Sampler sampler;
   private final SurfaceSystem surfaceSystem;
   private final Map<ResourceKey<NormalNoise>, Noise> noiseInstances;
   private final Map<Identifier, PositionalRandomFactory> positionalRandoms;
   private final List<SpawnTargetPoint.Wired> spawnTarget;
   private final Optional<Aquifer.Config> aquifers;
   private final List<OreVeinifier> oreVeins;

   public static RandomState create(final HolderGetter<NormalNoise> noises, final long seed, final NoiseGeneratorSettings settings) {
      return create(noises, seed, settings.useLegacyRandomSource(), settings.defaultBlock(), settings.seaLevel(), settings.noiseRouter(), settings.spawnTarget(), settings.aquifers(), settings.oreVeins());
   }

   public static RandomState create(final HolderGetter<NormalNoise> noises, final long seed, final boolean useLegacyRandom, final BlockState defaultBlock, final int seaLevel, final NoiseRouter noiseRouter, final List<SpawnTargetPoint> spawnTarget, final Optional<Aquifer.Config> aquifers, final List<OreVeinifier> oreVeins) {
      return new RandomState(noises, seed, useLegacyRandom, defaultBlock, seaLevel, noiseRouter, spawnTarget, aquifers, oreVeins);
   }

   private RandomState(final HolderGetter<NormalNoise> noises, final long seed, final boolean useLegacyRandom, final BlockState defaultBlock, final int seaLevel, final NoiseRouter noiseRouter, final List<SpawnTargetPoint> spawnTarget, final Optional<Aquifer.Config> aquifers, final List<OreVeinifier> oreVeins) {
      super();
      WorldgenRandom.Algorithm randomAlgorithm = useLegacyRandom ? WorldgenRandom.Algorithm.LEGACY : WorldgenRandom.Algorithm.XOROSHIRO;
      this.seed = seed;
      this.random = randomAlgorithm.newInstance(seed).forkPositional();
      this.noises = noises;
      this.noiseInstances = new ConcurrentHashMap();
      this.positionalRandoms = new ConcurrentHashMap();
      this.surfaceSystem = new SurfaceSystem(this, defaultBlock, seaLevel, this.random);

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
            Holder<NormalNoise> noiseData = noise.noiseData();
            if (noiseData.is(Noises.TEMPERATURE_NETHER)) {
               Noise newNoise = ((NormalNoise)noiseData.value()).createForLegacyNetherBiome(this.newLegacyInstance(0L));
               return new DensityFunction.NoiseHolder(noiseData, newNoise);
            } else if (noiseData.is(Noises.VEGETATION_NETHER)) {
               Noise newNoise = ((NormalNoise)noiseData.value()).createForLegacyNetherBiome(this.newLegacyInstance(1L));
               return new DensityFunction.NoiseHolder(noiseData, newNoise);
            } else {
               Noise instantiate = RandomState.this.getOrCreateNoise((ResourceKey)noiseData.unwrapKey().orElseThrow());
               return new DensityFunction.NoiseHolder(noiseData, instantiate);
            }
         }

         private DensityFunction wrapNew(final DensityFunction function) {
            Objects.requireNonNull(function);
            byte var3 = 0;
            Object var10000;
            //$FF: var3->value
            //0->net/minecraft/world/level/levelgen/densityfunction/op/BinaryFunction
            //1->net/minecraft/world/level/levelgen/synth/BlendedNoise
            //2->net/minecraft/world/level/levelgen/densityfunction/generator/EndIslandFunction
            switch (function.typeSwitch<invokedynamic>(function, var3)) {
               case 0:
                  BinaryFunction binary = (BinaryFunction)function;
                  var10000 = binary.trySimplify();
                  break;
               case 1:
                  BlendedNoise noise = (BlendedNoise)function;
                  RandomSource terrainRandom = useLegacyRandom ? this.newLegacyInstance(0L) : RandomState.this.random.fromHashOf(Identifier.withDefaultNamespace("terrain"));
                  var10000 = noise.withNewRandom(terrainRandom);
                  break;
               case 2:
                  var10000 = new EndIslandFunction(seed);
                  break;
               default:
                  var10000 = function;
            }

            return (DensityFunction)var10000;
         }

         public DensityFunction apply(final DensityFunction function) {
            return (DensityFunction)this.wrapped.computeIfAbsent(function, this::wrapNew);
         }
      }

      NoiseWiringHelper noiseWirer = new NoiseWiringHelper();
      this.router = noiseRouter.mapAll(noiseWirer);
      this.aquifers = aquifers.map((config) -> config.mapAll(noiseWirer));
      this.oreVeins = oreVeins.stream().map((config) -> config.mapAll(noiseWirer)).toList();
      DensityFunction.Visitor noiseFlattener = new DensityFunction.Visitor() {
         private final Map<DensityFunction, DensityFunction> wrapped;

         {
            Objects.requireNonNull(RandomState.this);
            this.wrapped = new HashMap();
         }

         private DensityFunction wrapNew(final DensityFunction function) {
            if (function instanceof DensityFunctions.HolderHolder holder) {
               return (DensityFunction)holder.function().value();
            } else if (function instanceof MarkerFunction marker) {
               return marker.wrapped();
            } else {
               return function;
            }
         }

         public DensityFunction apply(final DensityFunction input) {
            return (DensityFunction)this.wrapped.computeIfAbsent(input, this::wrapNew);
         }
      };
      this.sampler = new Climate.Sampler(this.router.temperature().mapAll(noiseFlattener), this.router.vegetation().mapAll(noiseFlattener), this.router.continents().mapAll(noiseFlattener), this.router.erosion().mapAll(noiseFlattener), this.router.depth().mapAll(noiseFlattener), this.router.ridges().mapAll(noiseFlattener));
      this.spawnTarget = spawnTarget.stream().map((point) -> point.wire(noiseWirer, noiseFlattener)).toList();
   }

   public Noise getOrCreateNoise(final ResourceKey<NormalNoise> noise) {
      return (Noise)this.noiseInstances.computeIfAbsent(noise, (key) -> Noises.instantiate(this.noises, this.random, noise));
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

   public List<SpawnTargetPoint.Wired> spawnTarget() {
      return this.spawnTarget;
   }

   public Optional<Aquifer.Config> aquifers() {
      return this.aquifers;
   }

   public List<OreVeinifier> oreVeins() {
      return this.oreVeins;
   }

   /** @deprecated */
   @Deprecated
   public long seed() {
      return this.seed;
   }
}
