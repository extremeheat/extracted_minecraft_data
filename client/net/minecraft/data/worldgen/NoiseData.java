package net.minecraft.data.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseData {
   public NoiseData() {
      super();
   }

   public static Holder<NormalNoise.NoiseParameters> bootstrap(Registry<NormalNoise.NoiseParameters> var0) {
      registerBiomeNoises(var0, 0, Noises.TEMPERATURE, Noises.VEGETATION, Noises.CONTINENTALNESS, Noises.EROSION);
      registerBiomeNoises(var0, -2, Noises.TEMPERATURE_LARGE, Noises.VEGETATION_LARGE, Noises.CONTINENTALNESS_LARGE, Noises.EROSION_LARGE);
      register(var0, Noises.RIDGE, -7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
      register(var0, Noises.SHIFT, -3, 1.0, 1.0, 1.0, 0.0);
      register(var0, Noises.AQUIFER_BARRIER, -3, 1.0);
      register(var0, Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0);
      register(var0, Noises.AQUIFER_LAVA, -1, 1.0);
      register(var0, Noises.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0);
      register(var0, Noises.PILLAR, -7, 1.0, 1.0);
      register(var0, Noises.PILLAR_RARENESS, -8, 1.0);
      register(var0, Noises.PILLAR_THICKNESS, -8, 1.0);
      register(var0, Noises.SPAGHETTI_2D, -7, 1.0);
      register(var0, Noises.SPAGHETTI_2D_ELEVATION, -8, 1.0);
      register(var0, Noises.SPAGHETTI_2D_MODULATOR, -11, 1.0);
      register(var0, Noises.SPAGHETTI_2D_THICKNESS, -11, 1.0);
      register(var0, Noises.SPAGHETTI_3D_1, -7, 1.0);
      register(var0, Noises.SPAGHETTI_3D_2, -7, 1.0);
      register(var0, Noises.SPAGHETTI_3D_RARITY, -11, 1.0);
      register(var0, Noises.SPAGHETTI_3D_THICKNESS, -8, 1.0);
      register(var0, Noises.SPAGHETTI_ROUGHNESS, -5, 1.0);
      register(var0, Noises.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0);
      register(var0, Noises.CAVE_ENTRANCE, -7, 0.4, 0.5, 1.0);
      register(var0, Noises.CAVE_LAYER, -8, 1.0);
      register(var0, Noises.CAVE_CHEESE, -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
      register(var0, Noises.ORE_VEININESS, -8, 1.0);
      register(var0, Noises.ORE_VEIN_A, -7, 1.0);
      register(var0, Noises.ORE_VEIN_B, -7, 1.0);
      register(var0, Noises.ORE_GAP, -5, 1.0);
      register(var0, Noises.NOODLE, -8, 1.0);
      register(var0, Noises.NOODLE_THICKNESS, -8, 1.0);
      register(var0, Noises.NOODLE_RIDGE_A, -7, 1.0);
      register(var0, Noises.NOODLE_RIDGE_B, -7, 1.0);
      register(var0, Noises.JAGGED, -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.SURFACE, -6, 1.0, 1.0, 1.0);
      register(var0, Noises.SURFACE_SECONDARY, -6, 1.0, 1.0, 0.0, 1.0);
      register(var0, Noises.CLAY_BANDS_OFFSET, -8, 1.0);
      register(var0, Noises.BADLANDS_PILLAR, -2, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.BADLANDS_PILLAR_ROOF, -8, 1.0);
      register(var0, Noises.BADLANDS_SURFACE, -6, 1.0, 1.0, 1.0);
      register(var0, Noises.ICEBERG_PILLAR, -6, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.ICEBERG_PILLAR_ROOF, -3, 1.0);
      register(var0, Noises.ICEBERG_SURFACE, -6, 1.0, 1.0, 1.0);
      register(var0, Noises.SWAMP, -2, 1.0);
      register(var0, Noises.CALCITE, -9, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.GRAVEL, -8, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.POWDER_SNOW, -6, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.PACKED_ICE, -7, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.ICE, -4, 1.0, 1.0, 1.0, 1.0);
      register(var0, Noises.SOUL_SAND_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
      register(var0, Noises.GRAVEL_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
      register(var0, Noises.PATCH, -5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
      register(var0, Noises.NETHERRACK, -3, 1.0, 0.0, 0.0, 0.35);
      register(var0, Noises.NETHER_WART, -3, 1.0, 0.0, 0.0, 0.9);
      return register(var0, Noises.NETHER_STATE_SELECTOR, -4, 1.0);
   }

   private static void registerBiomeNoises(Registry<NormalNoise.NoiseParameters> var0, int var1, ResourceKey<NormalNoise.NoiseParameters> var2, ResourceKey<NormalNoise.NoiseParameters> var3, ResourceKey<NormalNoise.NoiseParameters> var4, ResourceKey<NormalNoise.NoiseParameters> var5) {
      register(var0, var2, -10 + var1, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
      register(var0, var3, -8 + var1, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
      register(var0, var4, -9 + var1, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
      register(var0, var5, -9 + var1, 1.0, 1.0, 0.0, 1.0, 1.0);
   }

   private static Holder<NormalNoise.NoiseParameters> register(Registry<NormalNoise.NoiseParameters> var0, ResourceKey<NormalNoise.NoiseParameters> var1, int var2, double var3, double... var5) {
      return BuiltinRegistries.register(var0, (ResourceKey)var1, new NormalNoise.NoiseParameters(var2, var3, var5));
   }
}
