package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public record BlendDensityFunction(DensityFunction input) implements DensityFunction {
   public static final MapCodec<BlendDensityFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(BlendDensityFunction::input)).apply(i, BlendDensityFunction::new));

   public BlendDensityFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      return new Sampler(this.input.compileSampler(context));
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      return input == this.input ? this : new BlendDensityFunction(input);
   }

   public Interval range() {
      return this.input.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes();
   }

   public MapCodec<BlendDensityFunction> codec() {
      return CODEC;
   }

   private static record Sampler(DensitySampler input) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);
         Blender blender = (Blender)context.getField(Blender.CONTEXT_KEY);
         if (blender != null && !blender.isEmpty()) {
            int index = 0;

            for(int z = 0; z < volume.sizeZ(); ++z) {
               int blockZ = volume.blockZ(z);

               for(int x = 0; x < volume.sizeX(); ++x) {
                  int blockX = volume.blockX(x);

                  for(int y = 0; y < volume.sizeY(); ++y) {
                     int blockY = volume.blockY(y);
                     outputBuffer.set(index, blender.blendDensity(blockX, blockY, blockZ, outputBuffer.get(index)));
                     ++index;
                  }
               }
            }

         }
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float input = this.input.sampleValue(context, blockX, blockY, blockZ);
         Blender blender = (Blender)context.getField(Blender.CONTEXT_KEY);
         return blender != null && !blender.isEmpty() ? blender.blendDensity(blockX, blockY, blockZ, input) : input;
      }
   }
}
