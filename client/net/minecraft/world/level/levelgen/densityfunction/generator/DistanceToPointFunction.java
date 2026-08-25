package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.DistanceMetric;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public record DistanceToPointFunction(Vec3i point, DistanceMetric metric) implements DensityFunction {
   public static final MapCodec<DistanceToPointFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Vec3i.CODEC.fieldOf("point").forGetter(DistanceToPointFunction::point), DistanceMetric.CODEC.fieldOf("metric").forGetter(DistanceToPointFunction::metric)).apply(i, DistanceToPointFunction::new));

   public DistanceToPointFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      return new Sampler(this.point.getX(), this.point.getY(), this.point.getZ(), this.metric);
   }

   public Interval range() {
      return Interval.of(0.0F, 1.0F / 0.0F);
   }

   public @DensityFunction.Axes int domainAxes() {
      return 7;
   }

   public MapCodec<DistanceToPointFunction> codec() {
      return CODEC;
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      return this;
   }

   private static record Sampler(int x, int y, int z, DistanceMetric metric) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         int index = 0;

         for(int z = 0; z < volume.sizeZ(); ++z) {
            int blockZ = volume.blockZ(z);

            for(int x = 0; x < volume.sizeX(); ++x) {
               int blockX = volume.blockX(x);

               for(int y = 0; y < volume.sizeY(); ++y) {
                  int blockY = volume.blockY(y);
                  outputBuffer.set(index++, this.sampleValue(context, blockX, blockY, blockZ));
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.metric.compute((float)(this.x - blockX), (float)(this.y - blockY), (float)(this.z - blockZ));
      }
   }
}
