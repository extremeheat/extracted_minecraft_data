package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;

public record LerpFunction(DensityFunction alpha, DensityFunction first, DensityFunction second) implements DensityFunction {
   public static final MapCodec<LerpFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("alpha").forGetter(LerpFunction::alpha), DensityFunction.CODEC.fieldOf("first").forGetter(LerpFunction::first), DensityFunction.CODEC.fieldOf("second").forGetter(LerpFunction::second)).apply(i, LerpFunction::new));

   public LerpFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      DensitySampler alpha = this.alpha.compileSampler(context);
      DensitySampler first = this.first.compileSampler(context);
      DensitySampler second = this.second.compileSampler(context);
      DensityFunction var9 = this.first;
      if (var9 instanceof ConstantFunction var5) {
         ConstantFunction var10000 = var5;

         try {
            var16 = var10000.value();
         } catch (Throwable var13) {
            throw new MatchException(var13.toString(), var13);
         }

         float firstValue = var16;
         if (true) {
            return new ConstFirstSampler(alpha, firstValue, second);
         }
      }

      var9 = this.second;
      if (var9 instanceof ConstantFunction var7) {
         ConstantFunction var17 = var7;

         try {
            var18 = var17.value();
         } catch (Throwable var12) {
            throw new MatchException(var12.toString(), var12);
         }

         float firstValue = var18;
         if (true) {
            return new ConstSecondSampler(alpha, first, firstValue);
         }
      }

      return new Sampler(alpha, first, second);
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction alpha = rule.rewrite(this.alpha);
      DensityFunction first = rule.rewrite(this.first);
      DensityFunction second = rule.rewrite(this.second);
      return alpha == this.alpha && first == this.first && second == this.second ? this : new LerpFunction(alpha, first, second);
   }

   public Interval range() {
      return Interval.lerp(this.alpha.range(), this.first.range(), this.second.range());
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.alpha.domainAxes() | this.first.domainAxes() | this.second.domainAxes();
   }

   public MapCodec<LerpFunction> codec() {
      return CODEC;
   }

   public static record Sampler(DensitySampler alpha, DensitySampler first, DensitySampler second) implements DensitySampler {
      public Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.alpha.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer firstBuffer = context.acquireBuffer(volume)) {
            this.first.sampleVolume(context, firstBuffer, volume);

            try (ScopedDensityBuffer secondBuffer = context.acquireBuffer(volume)) {
               this.second.sampleVolume(context, secondBuffer, volume);

               for(int i = 0; i < outputBuffer.size(); ++i) {
                  float alpha = outputBuffer.get(i);
                  float first = firstBuffer.get(i);
                  float second = secondBuffer.get(i);
                  if (alpha == 0.0F) {
                     outputBuffer.set(i, first);
                  } else if (alpha == 1.0F) {
                     outputBuffer.set(i, second);
                  } else {
                     outputBuffer.set(i, Mth.lerp(alpha, first, second));
                  }
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float alpha = this.alpha.sampleValue(context, blockX, blockY, blockZ);
         if (alpha == 0.0F) {
            return this.first.sampleValue(context, blockX, blockY, blockZ);
         } else {
            return alpha == 1.0F ? this.second.sampleValue(context, blockX, blockY, blockZ) : Mth.lerp(alpha, this.first.sampleValue(context, blockX, blockY, blockZ), this.second.sampleValue(context, blockX, blockY, blockZ));
         }
      }
   }

   public static record ConstFirstSampler(DensitySampler alpha, float first, DensitySampler second) implements DensitySampler {
      public ConstFirstSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.alpha.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer secondBuffer = context.acquireBuffer(volume)) {
            this.second.sampleVolume(context, secondBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               float alpha = outputBuffer.get(i);
               float second = secondBuffer.get(i);
               if (alpha == 0.0F) {
                  outputBuffer.set(i, this.first);
               } else if (alpha == 1.0F) {
                  outputBuffer.set(i, second);
               } else {
                  outputBuffer.set(i, Mth.lerp(alpha, this.first, second));
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float alpha = this.alpha.sampleValue(context, blockX, blockY, blockZ);
         if (alpha == 0.0F) {
            return this.first;
         } else {
            return alpha == 1.0F ? this.second.sampleValue(context, blockX, blockY, blockZ) : Mth.lerp(alpha, this.first, this.second.sampleValue(context, blockX, blockY, blockZ));
         }
      }
   }

   public static record ConstSecondSampler(DensitySampler alpha, DensitySampler first, float second) implements DensitySampler {
      public ConstSecondSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.alpha.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer firstBuffer = context.acquireBuffer(volume)) {
            this.first.sampleVolume(context, firstBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               float alpha = outputBuffer.get(i);
               float first = firstBuffer.get(i);
               if (alpha == 0.0F) {
                  outputBuffer.set(i, first);
               } else if (alpha == 1.0F) {
                  outputBuffer.set(i, this.second);
               } else {
                  outputBuffer.set(i, Mth.lerp(alpha, first, this.second));
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float alpha = this.alpha.sampleValue(context, blockX, blockY, blockZ);
         if (alpha == 0.0F) {
            return this.first.sampleValue(context, blockX, blockY, blockZ);
         } else {
            return alpha == 1.0F ? this.second : Mth.lerp(alpha, this.first.sampleValue(context, blockX, blockY, blockZ), this.second);
         }
      }
   }
}
