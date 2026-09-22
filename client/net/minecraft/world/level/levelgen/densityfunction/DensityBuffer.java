package net.minecraft.world.level.levelgen.densityfunction;

import java.util.Arrays;

public class DensityBuffer {
   protected final float[] values;
   protected int size;

   protected DensityBuffer(final int size) {
      this(new float[size], size);
   }

   private DensityBuffer(final float[] values, final int size) {
      super();
      this.values = values;
      this.size = size;
   }

   public static DensityBuffer createUnpooled(final int size) {
      return new DensityBuffer(size);
   }

   public DensityBuffer slice(final int size) {
      if (size > this.size) {
         throw new IllegalArgumentException(size + " larger than maximum " + this.size);
      } else {
         return new DensityBuffer(this.values, size);
      }
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

   public void copyFrom(final DensityBuffer other, final int fromIndex, final int toIndex, final int length) {
      if (fromIndex >= 0 && fromIndex + length <= other.size) {
         if (toIndex >= 0 && toIndex + length <= this.size) {
            System.arraycopy(other.values, fromIndex, this.values, toIndex, length);
         } else {
            throw new IllegalArgumentException("Cannot copy " + length + " values to " + toIndex + " in buffer with size=" + this.size);
         }
      } else {
         throw new IllegalArgumentException("Cannot copy " + length + " values from " + fromIndex + " in buffer with size=" + other.size);
      }
   }

   public int capacity() {
      return this.values.length;
   }

   public int size() {
      return this.size;
   }
}
