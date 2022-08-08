package net.minecraft.util;

import java.util.Arrays;
import java.util.function.IntConsumer;
import org.apache.commons.lang3.Validate;

public class ZeroBitStorage implements BitStorage {
   public static final long[] RAW = new long[0];
   private final int size;

   public ZeroBitStorage(int var1) {
      super();
      this.size = var1;
   }

   public int getAndSet(int var1, int var2) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      Validate.inclusiveBetween(0L, 0L, (long)var2);
      return 0;
   }

   public void set(int var1, int var2) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      Validate.inclusiveBetween(0L, 0L, (long)var2);
   }

   public int get(int var1) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
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

   public void getAll(IntConsumer var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.accept(0);
      }

   }

   public void unpack(int[] var1) {
      Arrays.fill(var1, 0, this.size, 0);
   }

   public BitStorage copy() {
      return this;
   }
}
