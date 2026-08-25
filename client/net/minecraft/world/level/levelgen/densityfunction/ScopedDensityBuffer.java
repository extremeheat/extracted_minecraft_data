package net.minecraft.world.level.levelgen.densityfunction;

public class ScopedDensityBuffer extends DensityBuffer implements AutoCloseable {
   private final DensityBufferArena arena;
   private int age;
   private boolean closed;

   ScopedDensityBuffer(final DensityBufferArena arena, final int capacity, final int size) {
      super(capacity);
      this.size = size;
      this.arena = arena;
   }

   void restore(final int size) {
      if (size > this.values.length) {
         throw new IllegalArgumentException("Cannot set size larger than buffer capacity");
      } else if (!this.closed) {
         throw new IllegalArgumentException("Buffer is already in use");
      } else {
         this.age = 0;
         this.closed = false;
         this.size = size;
      }
   }

   int incrementAge() {
      return ++this.age;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.arena.release(this);
      }
   }
}
