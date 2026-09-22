package net.minecraft.world.level.levelgen.densityfunction.op;

import com.google.common.collect.Comparators;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record IntervalSelectFunction(DensityFunction input, FloatList thresholds, List<DensityFunction> functions) implements DensityFunction {
   private static final Codec<FloatList> THRESHOLDS_CODEC;
   public static final MapCodec<IntervalSelectFunction> CODEC;

   public IntervalSelectFunction {
      super();
   }

   private DataResult<IntervalSelectFunction> validate() {
      if (this.thresholds.size() != this.functions.size() - 1) {
         return DataResult.error(() -> {
            int var10000 = this.functions.size() - 1;
            return "Expected " + var10000 + " thresholds for " + this.functions.size() + " functions, but got " + this.thresholds.size();
         });
      } else {
         return !Comparators.isInOrder(this.thresholds, Float::compare) ? DataResult.error(() -> "Threshold values must be ordered from smallest to largest") : DataResult.success(this);
      }
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      DensitySampler input = this.input.compileSampler(context);
      if (this.thresholds.size() == 1) {
         float threshold = this.thresholds.getFloat(0);
         DensitySampler ifBelow = ((DensityFunction)this.functions.getFirst()).compileSampler(context);
         DensitySampler ifAbove = ((DensityFunction)this.functions.getLast()).compileSampler(context);
         return new SingleThresholdSampler(input, threshold, ifBelow, ifAbove);
      } else {
         return new Sampler(input, this.thresholds.toFloatArray(), (DensitySampler[])this.functions.stream().map((function) -> function.compileSampler(context)).toArray((x$0) -> new DensitySampler[x$0]));
      }
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      MutableBoolean functionsChanged = new MutableBoolean();
      List<DensityFunction> functions = this.functions.stream().map((function) -> {
         DensityFunction newFunction = rule.rewrite(function);
         if (newFunction != function) {
            functionsChanged.setTrue();
         }

         return newFunction;
      }).toList();
      return input == this.input && !functionsChanged.booleanValue() ? this : new IntervalSelectFunction(input, this.thresholds, functions);
   }

   public Interval range() {
      return Interval.encapsulating(Lists.transform(this.functions, DensityFunction::range));
   }

   public @DensityFunction.Axes int domainAxes() {
      int axes = this.input.domainAxes();

      for(DensityFunction function : this.functions) {
         axes |= function.domainAxes();
      }

      return axes;
   }

   public MapCodec<IntervalSelectFunction> codec() {
      return CODEC;
   }

   static {
      THRESHOLDS_CODEC = DensityFunctions.NOISE_VALUE_CODEC.listOf().xmap(FloatArrayList::new, Function.identity());
      CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(IntervalSelectFunction::input), THRESHOLDS_CODEC.fieldOf("thresholds").forGetter(IntervalSelectFunction::thresholds), DensityFunction.CODEC.listOf(2, 2147483647).fieldOf("functions").forGetter(IntervalSelectFunction::functions)).apply(i, IntervalSelectFunction::new)).validate(IntervalSelectFunction::validate);
   }

   private static record Sampler(DensitySampler input, float[] thresholds, DensitySampler[] samplers) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         try (ScopedDensityBuffer inputBuffer = context.acquireBuffer(volume)) {
            this.input.sampleVolume(context, inputBuffer, volume);
            int firstSamplerIndex = this.selectSamplerIndex(inputBuffer.get(0));
            this.samplers[firstSamplerIndex].sampleVolume(context, outputBuffer, volume);

            int i;
            for(i = 1; i < inputBuffer.size(); ++i) {
               int samplerIndex = this.selectSamplerIndex(inputBuffer.get(i));
               if (samplerIndex != firstSamplerIndex) {
                  break;
               }
            }

            if (i == inputBuffer.size()) {
               return;
            }

            DensityBuffer[] buffers = new DensityBuffer[this.samplers.length];
            buffers[firstSamplerIndex] = outputBuffer;

            while(true) {
               boolean var22 = false;

               try {
                  var22 = true;
                  if (i >= inputBuffer.size()) {
                     var22 = false;
                     break;
                  }

                  int samplerIndex = this.selectSamplerIndex(inputBuffer.get(i));
                  DensityBuffer buffer = buffers[samplerIndex];
                  if (buffer == null) {
                     buffer = context.acquireBuffer(volume);
                     buffers[samplerIndex] = buffer;
                     this.samplers[samplerIndex].sampleVolume(context, buffer, volume);
                  }

                  outputBuffer.set(i, buffer.get(i));
                  ++i;
               } finally {
                  if (var22) {
                     DensityBuffer[] var14 = buffers;
                     int var15 = buffers.length;
                     int var16 = 0;

                     while(true) {
                        if (var16 >= var15) {
                           ;
                        } else {
                           DensityBuffer buffer = var14[var16];
                           if (buffer != outputBuffer && buffer instanceof ScopedDensityBuffer) {
                              ScopedDensityBuffer scoped = (ScopedDensityBuffer)buffer;
                              scoped.close();
                           }

                           ++var16;
                        }
                     }
                  }
               }
            }

            for(DensityBuffer buffer : buffers) {
               if (buffer != outputBuffer && buffer instanceof ScopedDensityBuffer scoped) {
                  scoped.close();
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float input = this.input.sampleValue(context, blockX, blockY, blockZ);
         return this.samplers[this.selectSamplerIndex(input)].sampleValue(context, blockX, blockY, blockZ);
      }

      private int selectSamplerIndex(final float input) {
         for(int i = 0; i < this.thresholds.length; ++i) {
            if (input < this.thresholds[i]) {
               return i;
            }
         }

         return this.samplers.length - 1;
      }
   }

   private static record SingleThresholdSampler(DensitySampler input, float threshold, DensitySampler ifBelow, DensitySampler ifAbove) implements DensitySampler {
      private SingleThresholdSampler {
         super();
      }

      private boolean isBelow(final float input) {
         return input < this.threshold;
      }

      private DensitySampler selectSampler(final boolean firstBelow) {
         return firstBelow ? this.ifBelow : this.ifAbove;
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         try (ScopedDensityBuffer inputBuffer = context.acquireBuffer(volume)) {
            this.input.sampleVolume(context, inputBuffer, volume);
            boolean firstBelow = this.isBelow(inputBuffer.get(0));
            this.selectSampler(firstBelow).sampleVolume(context, outputBuffer, volume);

            int i;
            for(i = 1; i < inputBuffer.size() && firstBelow == this.isBelow(inputBuffer.get(i)); ++i) {
            }

            if (i == inputBuffer.size()) {
               return;
            }

            try (ScopedDensityBuffer otherBuffer = context.acquireBuffer(volume)) {
               this.selectSampler(!firstBelow).sampleVolume(context, otherBuffer, volume);

               for(; i < inputBuffer.size(); ++i) {
                  if (firstBelow != this.isBelow(inputBuffer.get(i))) {
                     outputBuffer.set(i, otherBuffer.get(i));
                  }
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float input = this.input.sampleValue(context, blockX, blockY, blockZ);
         return this.isBelow(input) ? this.ifBelow.sampleValue(context, blockX, blockY, blockZ) : this.ifAbove.sampleValue(context, blockX, blockY, blockZ);
      }
   }
}
