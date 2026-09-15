package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public record NoiseFunction(Holder<NormalNoise> noise, double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) implements DensityFunction {
   public static final MapCodec<NoiseFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(NormalNoise.CODEC.fieldOf("noise").forGetter(NoiseFunction::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(NoiseFunction::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(NoiseFunction::yScale), DensityFunction.CODEC.optionalFieldOf("shift_x", DensityFunctions.zero()).forGetter(NoiseFunction::shiftX), DensityFunction.CODEC.optionalFieldOf("shift_y", DensityFunctions.zero()).forGetter(NoiseFunction::shiftY), DensityFunction.CODEC.optionalFieldOf("shift_z", DensityFunctions.zero()).forGetter(NoiseFunction::shiftZ)).apply(i, NoiseFunction::new));

   public NoiseFunction(Holder<NormalNoise> noise, @Deprecated double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) {
      super();
      this.noise = noise;
      this.xzScale = xzScale;
      this.yScale = yScale;
      this.shiftX = shiftX;
      this.shiftY = shiftY;
      this.shiftZ = shiftZ;
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      Noise noise = context.createNoiseSampler(this.noise);
      if (this.shiftX.equals(DensityFunctions.zero()) && this.shiftY.equals(DensityFunctions.zero()) && this.shiftZ.equals(DensityFunctions.zero())) {
         return new Sampler(noise, this.xzScale, this.yScale);
      } else {
         DensitySampler shiftX = this.shiftX.compileSampler(context);
         DensitySampler shiftZ = this.shiftZ.compileSampler(context);
         if (this.shiftY.equals(DensityFunctions.zero())) {
            return new ShiftedXzSampler(shiftX, shiftZ, noise, this.xzScale, this.yScale);
         } else {
            DensitySampler shiftY = this.shiftY.compileSampler(context);
            return new ShiftedXyzSampler(shiftX, shiftY, shiftZ, noise, this.xzScale, this.yScale);
         }
      }
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction shiftX = rule.rewrite(this.shiftX);
      DensityFunction shiftY = rule.rewrite(this.shiftY);
      DensityFunction shiftZ = rule.rewrite(this.shiftZ);
      return shiftX == this.shiftX && shiftY == this.shiftY && shiftZ == this.shiftZ ? this : new NoiseFunction(this.noise, this.xzScale, this.yScale, shiftX, shiftY, shiftZ);
   }

   public Interval range() {
      return ((NormalNoise)this.noise.value()).range();
   }

   public @DensityFunction.Axes int domainAxes() {
      int axes = 7;
      if (this.yScale == 0.0) {
         axes &= -3;
      }

      if (this.xzScale == 0.0) {
         axes &= -6;
      }

      return axes | this.shiftX.domainAxes() | this.shiftY.domainAxes() | this.shiftZ.domainAxes();
   }

   public MapCodec<NoiseFunction> codec() {
      return CODEC;
   }

   /** @deprecated */
   @Deprecated
   public double xzScale() {
      return this.xzScale;
   }

   public static record Sampler(Noise noise, double xzScale, double yScale) implements DensitySampler {
      public Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         outputBuffer.fill(0.0F);
         this.noise.addToVolume(outputBuffer, volume, this.xzScale, this.yScale, 1.0F);
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.noise.get((double)blockX * this.xzScale, (double)blockY * this.yScale, (double)blockZ * this.xzScale);
      }
   }

   public static record ShiftedXzSampler(DensitySampler shiftX, DensitySampler shiftZ, Noise noise, double xzScale, double yScale) implements DensitySampler {
      public ShiftedXzSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.shiftX.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer shiftZBuffer = context.acquireBuffer(volume)) {
            this.shiftZ.sampleVolume(context, shiftZBuffer, volume);
            int index = 0;

            for(int z = 0; z < volume.sizeZ(); ++z) {
               double baseNoiseZ = (double)volume.blockZ(z) * this.xzScale;

               for(int x = 0; x < volume.sizeX(); ++x) {
                  double baseNoiseX = (double)volume.blockX(x) * this.xzScale;

                  for(int y = 0; y < volume.sizeY(); ++y) {
                     double noiseX = baseNoiseX + (double)outputBuffer.get(index);
                     double noiseY = (double)volume.blockY(y) * this.yScale;
                     double noiseZ = baseNoiseZ + (double)shiftZBuffer.get(index);
                     outputBuffer.set(index, this.noise.get(noiseX, noiseY, noiseZ));
                     ++index;
                  }
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         double x = (double)blockX * this.xzScale + (double)this.shiftX.sampleValue(context, blockX, blockY, blockZ);
         double y = (double)blockY * this.yScale;
         double z = (double)blockZ * this.xzScale + (double)this.shiftZ.sampleValue(context, blockX, blockY, blockZ);
         return this.noise.get(x, y, z);
      }
   }

   public static record ShiftedXyzSampler(DensitySampler shiftX, DensitySampler shiftY, DensitySampler shiftZ, Noise noise, double xzScale, double yScale) implements DensitySampler {
      public ShiftedXyzSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.shiftX.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer shiftYBuffer = context.acquireBuffer(volume)) {
            this.shiftY.sampleVolume(context, shiftYBuffer, volume);

            try (ScopedDensityBuffer shiftZBuffer = context.acquireBuffer(volume)) {
               this.shiftZ.sampleVolume(context, shiftZBuffer, volume);
               int index = 0;

               for(int z = 0; z < volume.sizeZ(); ++z) {
                  double baseNoiseZ = (double)volume.blockZ(z) * this.xzScale;

                  for(int x = 0; x < volume.sizeX(); ++x) {
                     double baseNoiseX = (double)volume.blockX(x) * this.xzScale;

                     for(int y = 0; y < volume.sizeY(); ++y) {
                        double noiseX = baseNoiseX + (double)outputBuffer.get(index);
                        double noiseY = (double)volume.blockY(y) * this.yScale + (double)shiftYBuffer.get(index);
                        double noiseZ = baseNoiseZ + (double)shiftZBuffer.get(index);
                        outputBuffer.set(index, this.noise.get(noiseX, noiseY, noiseZ));
                        ++index;
                     }
                  }
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         double x = (double)blockX * this.xzScale + (double)this.shiftX.sampleValue(context, blockX, blockY, blockZ);
         double y = (double)blockY * this.yScale + (double)this.shiftY.sampleValue(context, blockX, blockY, blockZ);
         double z = (double)blockZ * this.xzScale + (double)this.shiftZ.sampleValue(context, blockX, blockY, blockZ);
         return this.noise.get(x, y, z);
      }
   }
}
