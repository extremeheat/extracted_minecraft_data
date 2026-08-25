package net.minecraft.util;

import java.util.Arrays;
import java.util.function.IntConsumer;
import org.apache.commons.lang3.Validate;

public class ZeroBitStorage implements BitStorage {
   public static final long[] RAW = new long[0];
   private final int size;

   public ZeroBitStorage(final int size) {
      super();
      this.size = size;
   }

   public int getAndSet(final int index, final int value) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
      Validate.inclusiveBetween(0L, 0L, (long)value);
      return 0;
   }

   public void set(final int index, final int value) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
      Validate.inclusiveBetween(0L, 0L, (long)value);
   }

   public void fill(final int value) {
      Validate.inclusiveBetween(0L, 0L, (long)value);
   }

   public int get(final int index) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
      return 0;
   }

   public long[] getRaw() {
      return RAW;
   }

   public int getSize() {
      return this.size;
   }

   public int getBits() {
      return 0;
   }

   public void getAll(final IntConsumer output) {
      for(int i = 0; i < this.size; ++i) {
         output.accept(0);
      }

   }

   public void unpack(final int[] output) {
      Arrays.fill(output, 0, this.size, 0);
   }

   public BitStorage copy() {
      return this;
   }
}
