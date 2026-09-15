package net.minecraft.world.level.levelgen.densityfunction;

public interface DensitySampler {
   void sampleVolume(SamplerContext context, DensityBuffer outputBuffer, DensityVolume volume);

   static void sampleVolumeNaive(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume, final DensitySampler sampler) {
      int index = 0;

      for(int z = 0; z < volume.sizeZ(); ++z) {
         int blockZ = volume.blockZ(z);

         for(int x = 0; x < volume.sizeX(); ++x) {
            int blockX = volume.blockX(x);

            for(int y = 0; y < volume.sizeY(); ++y) {
               int blockY = volume.blockY(y);
               outputBuffer.set(index++, sampler.sampleValue(context, blockX, blockY, blockZ));
            }
         }
      }

   }

   float sampleValue(SamplerContext context, int blockX, int blockY, int blockZ);

   default Bound bind(final SamplerContext context) {
      return new Bound(this, context);
   }

   public static record Bound(DensitySampler sampler, SamplerContext context) {
      public Bound {
         super();
      }

      public float sampleValue(final int blockX, final int blockY, final int blockZ) {
         return this.sampler.sampleValue(this.context, blockX, blockY, blockZ);
      }

      public void sampleVolume(final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.sampler.sampleVolume(this.context, outputBuffer, volume);
      }

      public ScopedDensityBuffer sampleVolume(final DensityVolume volume) {
         ScopedDensityBuffer buffer = this.context.acquireBuffer(volume);
         this.sampleVolume(buffer, volume);
         return buffer;
      }
   }
}
