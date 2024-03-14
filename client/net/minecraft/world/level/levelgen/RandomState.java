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
      this.aquiferRandom = this.random.fromHashOf(new ResourceLocation("aquifer")).forkPositional();
      this.oreRandom = this.random.fromHashOf(new ResourceLocation("ore")).forkPositional();
      this.noiseIntances = new ConcurrentHashMap<>();
      this.positionalRandoms = new ConcurrentHashMap<>();
      this.surfaceSystem = new SurfaceSystem(this, var1.defaultBlock(), var1.seaLevel(), this.random);
      final boolean var5 = var1.useLegacyRandomSource();

      class 1NoiseWiringHelper implements DensityFunction.Visitor {
         private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();

         _NoiseWiringHelper/* $VF was: 1NoiseWiringHelper*/() {
            super();
         }

         private RandomSource newLegacyInstance(long var1) {
            return new LegacyRandomSource(var3 + var1);
         }

         @Override
         public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder var1) {
            Holder var2 = var1.noiseData();
            if (var5) {
               if (var2.is(Noises.TEMPERATURE)) {
                  NormalNoise var6 = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(0L), new NormalNoise.NoiseParameters(-7, 1.0, 1.0));
                  return new DensityFunction.NoiseHolder(var2, var6);
               }

               if (var2.is(Noises.VEGETATION)) {
                  NormalNoise var5x = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(1L), new NormalNoise.NoiseParameters(-7, 1.0, 1.0));
                  return new DensityFunction.NoiseHolder(var2, var5x);
               }

               if (var2.is(Noises.SHIFT)) {
                  NormalNoise var4 = NormalNoise.create(RandomState.this.random.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0));
                  return new DensityFunction.NoiseHolder(var2, var4);
               }
            }

            NormalNoise var3x = RandomState.this.getOrCreateNoise((ResourceKey<NormalNoise.NoiseParameters>)var2.unwrapKey().orElseThrow());
            return new DensityFunction.NoiseHolder(var2, var3x);
         }

         // $VF: Could not properly define all variable types!
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         private DensityFunction wrapNew(DensityFunction var1) {
            if (var1 instanceof BlendedNoise var2) {
               RandomSource var3x = var5 ? this.newLegacyInstance(0L) : RandomState.this.random.fromHashOf(new ResourceLocation("terrain"));
               return var2.withNewRandom(var3x);
            } else {
               return (DensityFunction)(var1 instanceof DensityFunctions.EndIslandDensityFunction ? new DensityFunctions.EndIslandDensityFunction(var3) : var1);
            }
         }

         @Override
         public DensityFunction apply(DensityFunction var1) {
            return this.wrapped.computeIfAbsent(var1, this::wrapNew);
         }
      }

      this.router = var1.noiseRouter().mapAll(new 1NoiseWiringHelper());
      DensityFunction.Visitor var6 = new DensityFunction.Visitor() {
         private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();

         // $VF: Could not properly define all variable types!
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         private DensityFunction wrapNew(DensityFunction var1) {
            if (var1 instanceof DensityFunctions.HolderHolder var3) {
               return var3.function().value();
            } else {
               return var1 instanceof DensityFunctions.Marker var2 ? var2.wrapped() : var1;
            }
         }

         @Override
         public DensityFunction apply(DensityFunction var1) {
            return this.wrapped.computeIfAbsent(var1, this::wrapNew);
         }
      };
      this.sampler = new Climate.Sampler(
         this.router.temperature().mapAll(var6),
         this.router.vegetation().mapAll(var6),
         this.router.continents().mapAll(var6),
         this.router.erosion().mapAll(var6),
         this.router.depth().mapAll(var6),
         this.router.ridges().mapAll(var6),
         var1.spawnTarget()
      );
   }

   public NormalNoise getOrCreateNoise(ResourceKey<NormalNoise.NoiseParameters> var1) {
      return this.noiseIntances.computeIfAbsent(var1, var2 -> Noises.instantiate(this.noises, this.random, var1));
   }

   public PositionalRandomFactory getOrCreateRandomFactory(ResourceLocation var1) {
      return this.positionalRandoms.computeIfAbsent(var1, var2 -> this.random.fromHashOf(var1).forkPositional());
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
