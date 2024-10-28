package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {
   public static final Codec<NoiseRouter> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(field("barrier", NoiseRouter::barrierNoise), field("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise), field("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise), field("lava", NoiseRouter::lavaNoise), field("temperature", NoiseRouter::temperature), field("vegetation", NoiseRouter::vegetation), field("continents", NoiseRouter::continents), field("erosion", NoiseRouter::erosion), field("depth", NoiseRouter::depth), field("ridges", NoiseRouter::ridges), field("initial_density_without_jaggedness", NoiseRouter::initialDensityWithoutJaggedness), field("final_density", NoiseRouter::finalDensity), field("vein_toggle", NoiseRouter::veinToggle), field("vein_ridged", NoiseRouter::veinRidged), field("vein_gap", NoiseRouter::veinGap)).apply(var0, NoiseRouter::new);
   });

   public NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {
      super();
      this.barrierNoise = barrierNoise;
      this.fluidLevelFloodednessNoise = fluidLevelFloodednessNoise;
      this.fluidLevelSpreadNoise = fluidLevelSpreadNoise;
      this.lavaNoise = lavaNoise;
      this.temperature = temperature;
      this.vegetation = vegetation;
      this.continents = continents;
      this.erosion = erosion;
      this.depth = depth;
      this.ridges = ridges;
      this.initialDensityWithoutJaggedness = initialDensityWithoutJaggedness;
      this.finalDensity = finalDensity;
      this.veinToggle = veinToggle;
      this.veinRidged = veinRidged;
      this.veinGap = veinGap;
   }

   private static RecordCodecBuilder<NoiseRouter, DensityFunction> field(String var0, Function<NoiseRouter, DensityFunction> var1) {
      return DensityFunction.HOLDER_HELPER_CODEC.fieldOf(var0).forGetter(var1);
   }

   public NoiseRouter mapAll(DensityFunction.Visitor var1) {
      return new NoiseRouter(this.barrierNoise.mapAll(var1), this.fluidLevelFloodednessNoise.mapAll(var1), this.fluidLevelSpreadNoise.mapAll(var1), this.lavaNoise.mapAll(var1), this.temperature.mapAll(var1), this.vegetation.mapAll(var1), this.continents.mapAll(var1), this.erosion.mapAll(var1), this.depth.mapAll(var1), this.ridges.mapAll(var1), this.initialDensityWithoutJaggedness.mapAll(var1), this.finalDensity.mapAll(var1), this.veinToggle.mapAll(var1), this.veinRidged.mapAll(var1), this.veinGap.mapAll(var1));
   }

   public DensityFunction barrierNoise() {
      return this.barrierNoise;
   }

   public DensityFunction fluidLevelFloodednessNoise() {
      return this.fluidLevelFloodednessNoise;
   }

   public DensityFunction fluidLevelSpreadNoise() {
      return this.fluidLevelSpreadNoise;
   }

   public DensityFunction lavaNoise() {
      return this.lavaNoise;
   }

   public DensityFunction temperature() {
      return this.temperature;
   }

   public DensityFunction vegetation() {
      return this.vegetation;
   }

   public DensityFunction continents() {
      return this.continents;
   }

   public DensityFunction erosion() {
      return this.erosion;
   }

   public DensityFunction depth() {
      return this.depth;
   }

   public DensityFunction ridges() {
      return this.ridges;
   }

   public DensityFunction initialDensityWithoutJaggedness() {
      return this.initialDensityWithoutJaggedness;
   }

   public DensityFunction finalDensity() {
      return this.finalDensity;
   }

   public DensityFunction veinToggle() {
      return this.veinToggle;
   }

   public DensityFunction veinRidged() {
      return this.veinRidged;
   }

   public DensityFunction veinGap() {
      return this.veinGap;
   }
}
