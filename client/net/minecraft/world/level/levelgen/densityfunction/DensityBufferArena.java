package net.minecraft.world.level.levelgen.densityfunction;

public interface DensityBufferArena {
   DensityBufferArena GLOBAL = new DensityBufferArena() {
      public ScopedDensityBuffer acquire(final int size) {
         return new ScopedDensityBuffer(this, size, size);
      }

      public void release(final ScopedDensityBuffer buffer) {
      }
   };

   ScopedDensityBuffer acquire(int size);

   void release(ScopedDensityBuffer buffer);
}
