package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;

public record PowFunction(DensityFunction base, DensityFunction exponent) implements DensityFunction {
   public static final MapCodec<PowFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("base").forGetter(PowFunction::base), DensityFunction.CODEC.fieldOf("exponent").forGetter(PowFunction::exponent)).apply(i, PowFunction::new));

   public PowFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      DensitySampler base = this.base.compileSampler(context);
      DensitySampler exponent = this.exponent.compileSampler(context);
      DensityFunction var8 = this.base;
      if (var8 instanceof ConstantFunction var4) {
         ConstantFunction var10000 = var4;

         try {
            var15 = var10000.value();
         } catch (Throwable var12) {
            throw new MatchException(var12.toString(), var12);
         }

         float baseValue = var15;
         if (true) {
            return new ConstBaseSampler((double)baseValue, exponent);
         }
      }

      var8 = this.exponent;
      if (var8 instanceof ConstantFunction var6) {
         ConstantFunction var16 = var6;

         try {
            var17 = var16.value();
         } catch (Throwable var11) {
            throw new MatchException(var11.toString(), var11);
         }

         float baseValue = var17;
         if (true) {
            return compileConstExponent(base, baseValue);
         }
      }

      return new Sampler(base, exponent);
   }

   private static DensitySampler compileConstExponent(final DensitySampler base, final float exponent) {
      float absExponent = Math.abs(exponent);
      DensitySampler specialSampler;
      if (absExponent == 0.5F) {
         specialSampler = new UnaryFunction.SqrtSampler(base);
      } else if (absExponent == 1.0F) {
         specialSampler = base;
      } else if (absExponent == 2.0F) {
         specialSampler = new UnaryFunction.SquareSampler(base);
      } else {
         if (absExponent != 3.0F) {
            return new ConstExponentSampler(base, (double)exponent);
         }

         specialSampler = new UnaryFunction.CubeSampler(base);
      }

      return (DensitySampler)(exponent >= 0.0F ? specialSampler : new UnaryFunction.ReciprocalSampler(specialSampler));
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction base = rule.rewrite(this.base);
      DensityFunction exponent = rule.rewrite(this.exponent);
      return base == this.base && exponent == this.exponent ? this : new PowFunction(base, exponent);
   }

   public Interval range() {
      return Interval.pow(this.base.range(), this.exponent.range());
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.base.domainAxes() | this.exponent.domainAxes();
   }

   public MapCodec<PowFunction> codec() {
      return CODEC;
   }

   private static record Sampler(DensitySampler base, DensitySampler exponent) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.base.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer exponentBuffer = context.acquireBuffer(volume)) {
            this.exponent.sampleVolume(context, exponentBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               float base = outputBuffer.get(i);
               float exponent = exponentBuffer.get(i);
               outputBuffer.set(i, (float)Math.pow((double)base, (double)exponent));
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return (float)Math.pow((double)this.base.sampleValue(context, blockX, blockY, blockZ), (double)this.exponent.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   private static record ConstBaseSampler(double base, DensitySampler exponent) implements DensitySampler {
      private ConstBaseSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.exponent.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, (float)Math.pow(this.base, (double)outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return (float)Math.pow(this.base, (double)this.exponent.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   private static record ConstExponentSampler(DensitySampler base, double exponent) implements DensitySampler {
      private ConstExponentSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.base.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, (float)Math.pow((double)outputBuffer.get(i), this.exponent));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return (float)Math.pow((double)this.base.sampleValue(context, blockX, blockY, blockZ), this.exponent);
      }
   }
}
