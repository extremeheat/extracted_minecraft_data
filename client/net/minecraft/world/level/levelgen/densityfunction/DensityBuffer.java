package net.minecraft.world.level.levelgen.densityfunction;

import java.util.Arrays;

public class DensityBuffer {
   protected final float[] values;
   protected int size;

   protected DensityBuffer(final int size) {
      super();
      this.values = new float[size];
      this.size = size;
   }

   public static DensityBuffer createUnpooled(final int size) {
      return new DensityBuffer(size);
   }

   public void set(final int index, final float value) {
      this.values[index] = value;
   }

   public void setRange(final int index, final int size, final float value) {
      Arrays.fill(this.values, index, index + size, value);
   }

   public void addTo(final int index, final float value) {
      float[] var10000 = this.values;
      var10000[index] += value;
   }

   public float get(final int index) {
      return this.values[index];
   }

   public void fill(final float value) {
      Arrays.fill(this.values, 0, this.size, value);
   }

   public void copyFrom(final DensityBuffer other) {
      if (this.size() != other.size()) {
         int var10002 = other.size();
         throw new IllegalArgumentException("Cannot copy from buffer with size=" + var10002 + ", expected" + this.size());
      } else {
         System.arraycopy(other.values, 0, this.values, 0, this.size());
      }
   }

   public int capacity() {
      return this.values.length;
   }

   public int size() {
      return this.size;
   }
}
