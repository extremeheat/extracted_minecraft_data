package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSettings {
   public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(0, 256).fieldOf("height").forGetter(NoiseSettings::height), NoiseSamplingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::noiseSamplingSettings), NoiseSlideSettings.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::topSlideSettings), NoiseSlideSettings.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::bottomSlideSettings), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical), Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseSettings::densityFactor), Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseSettings::densityOffset), Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseSettings::useSimplexSurfaceNoise), Codec.BOOL.optionalFieldOf("random_density_offset", false, Lifecycle.experimental()).forGetter(NoiseSettings::randomDensityOffset), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(NoiseSettings::islandNoiseOverride), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(NoiseSettings::isAmplified)).apply(var0, NoiseSettings::new);
   });
   private final int height;
   private final NoiseSamplingSettings noiseSamplingSettings;
   private final NoiseSlideSettings topSlideSettings;
   private final NoiseSlideSettings bottomSlideSettings;
   private final int noiseSizeHorizontal;
   private final int noiseSizeVertical;
   private final double densityFactor;
   private final double densityOffset;
   private final boolean useSimplexSurfaceNoise;
   private final boolean randomDensityOffset;
   private final boolean islandNoiseOverride;
   private final boolean isAmplified;

   public NoiseSettings(int var1, NoiseSamplingSettings var2, NoiseSlideSettings var3, NoiseSlideSettings var4, int var5, int var6, double var7, double var9, boolean var11, boolean var12, boolean var13, boolean var14) {
      super();
      this.height = var1;
      this.noiseSamplingSettings = var2;
      this.topSlideSettings = var3;
      this.bottomSlideSettings = var4;
      this.noiseSizeHorizontal = var5;
      this.noiseSizeVertical = var6;
      this.densityFactor = var7;
      this.densityOffset = var9;
      this.useSimplexSurfaceNoise = var11;
      this.randomDensityOffset = var12;
      this.islandNoiseOverride = var13;
      this.isAmplified = var14;
   }

   public int height() {
      return this.height;
   }

   public NoiseSamplingSettings noiseSamplingSettings() {
      return this.noiseSamplingSettings;
   }

   public NoiseSlideSettings topSlideSettings() {
      return this.topSlideSettings;
   }

   public NoiseSlideSettings bottomSlideSettings() {
      return this.bottomSlideSettings;
   }

   public int noiseSizeHorizontal() {
      return this.noiseSizeHorizontal;
   }

   public int noiseSizeVertical() {
      return this.noiseSizeVertical;
   }

   public double densityFactor() {
      return this.densityFactor;
   }

   public double densityOffset() {
      return this.densityOffset;
   }

   @Deprecated
   public boolean useSimplexSurfaceNoise() {
      return this.useSimplexSurfaceNoise;
   }

   @Deprecated
   public boolean randomDensityOffset() {
      return this.randomDensityOffset;
   }

   @Deprecated
   public boolean islandNoiseOverride() {
      return this.islandNoiseOverride;
   }

   @Deprecated
   public boolean isAmplified() {
      return this.isAmplified;
   }
}
