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

public record UnaryFunction(Type type, DensityFunction input) implements DensityFunction {
   public UnaryFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      DensitySampler input = this.input.compileSampler(context);
      Object var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = new AbsSampler(input);
         case 1 -> var10000 = new SquareSampler(input);
         case 2 -> var10000 = new CubeSampler(input);
         case 3 -> var10000 = new SqrtSampler(input);
         case 4 -> var10000 = new LeakyReLUSampler(input, 0.5F);
         case 5 -> var10000 = new LeakyReLUSampler(input, 0.25F);
         case 6 -> var10000 = new ReciprocalSampler(input);
         case 7 -> var10000 = new NegateSampler(input);
         case 8 -> var10000 = new SqueezeSampler(input);
         case 9 -> var10000 = new LogSampler(input);
         case 10 -> var10000 = new SignSampler(input);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (DensitySampler)var10000;
   }

   public UnaryFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      return input == this.input ? this : new UnaryFunction(this.type, input);
   }

   public MapCodec<UnaryFunction> codec() {
      return this.type.codec;
   }

   public Interval range() {
      Interval input = this.input.range();
      Interval var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = Interval.abs(input);
         case 1 -> var10000 = Interval.square(input);
         case 2 -> var10000 = Interval.mapMonotonic(input, Mth::cube);
         case 3 -> var10000 = Interval.pow(input, Interval.ofExact(0.5F));
         case 4 -> var10000 = Interval.mapMonotonic(input, (value) -> UnaryFunction.LeakyReLUSampler.apply(0.5F, value));
         case 5 -> var10000 = Interval.mapMonotonic(input, (value) -> UnaryFunction.LeakyReLUSampler.apply(0.25F, value));
         case 6 -> var10000 = Interval.reciprocal(input);
         case 7 -> var10000 = Interval.sub(Interval.ofExact(0.0F), input);
         case 8 -> var10000 = Interval.mapMonotonic(input, SqueezeSampler::apply);
         case 9 -> var10000 = Interval.log(input);
         case 10 -> var10000 = Interval.sign(input);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes();
   }

   public static enum Type {
      ABS("abs"),
      SQUARE("square"),
      CUBE("cube"),
      SQRT("sqrt"),
      HALF_NEGATIVE("half_negative"),
      QUARTER_NEGATIVE("quarter_negative"),
      RECIPROCAL("reciprocal"),
      NEGATE("negate"),
      SQUEEZE("squeeze"),
      LOG("log"),
      SIGN("sign");

      public final String id;
      public final MapCodec<UnaryFunction> codec = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(UnaryFunction::input)).apply(i, (input) -> new UnaryFunction(this, input)));

      private Type(final String id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{ABS, SQUARE, CUBE, SQRT, HALF_NEGATIVE, QUARTER_NEGATIVE, RECIPROCAL, NEGATE, SQUEEZE, LOG, SIGN};
      }
   }

   public static record AbsSampler(DensitySampler input) implements DensitySampler {
      public AbsSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, Math.abs(outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Math.abs(this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record SquareSampler(DensitySampler input) implements DensitySampler {
      public SquareSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, Mth.square(outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Mth.square(this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record SqrtSampler(DensitySampler input) implements DensitySampler {
      public SqrtSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, Mth.sqrt(outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Mth.sqrt(this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record ReciprocalSampler(DensitySampler input) implements DensitySampler {
      public ReciprocalSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, 1.0F / outputBuffer.get(i));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return 1.0F / this.input.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record NegateSampler(DensitySampler input) implements DensitySampler {
      public NegateSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, -outputBuffer.get(i));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return -this.input.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record CubeSampler(DensitySampler input) implements DensitySampler {
      public CubeSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, Mth.cube(outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Mth.cube(this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record LeakyReLUSampler(DensitySampler input, float negativeFactor) implements DensitySampler {
      public LeakyReLUSampler {
         super();
      }

      private static float apply(final float negativeFactor, final float input) {
         return input > 0.0F ? input : input * negativeFactor;
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, apply(this.negativeFactor, outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return apply(this.negativeFactor, this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record SqueezeSampler(DensitySampler input) implements DensitySampler {
      public SqueezeSampler {
         super();
      }

      private static float apply(final float input) {
         float clampedInput = Mth.clamp(input, -1.0F, 1.0F);
         return clampedInput / 2.0F - Mth.cube(clampedInput) / 24.0F;
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, apply(outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return apply(this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record LogSampler(DensitySampler input) implements DensitySampler {
      public LogSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, (float)Math.log((double)outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return (float)Math.log((double)this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record SignSampler(DensitySampler input) implements DensitySampler {
      public SignSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, Math.signum(outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Math.signum(this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }
}
