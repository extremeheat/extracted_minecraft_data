package net.minecraft.util;

public class SampleLogger {
   public static final int CAPACITY = 240;
   private final long[] samples = new long[240];
   private int start;
   private int size;

   public SampleLogger() {
      super();
   }

   public void logSample(long var1) {
      int var3 = this.wrapIndex(this.start + this.size);
      this.samples[var3] = var1;
      if (this.size < 240) {
         ++this.size;
      } else {
         this.start = this.wrapIndex(this.start + 1);
      }
   }

   public int capacity() {
      return this.samples.length;
   }

   public int size() {
      return this.size;
   }

   public long get(int var1) {
      if (var1 >= 0 && var1 < this.size) {
         return this.samples[this.wrapIndex(this.start + var1)];
      } else {
         throw new IndexOutOfBoundsException(var1 + " out of bounds for length " + this.size);
      }
   }

   private int wrapIndex(int var1) {
      return var1 % 240;
   }

   public void reset() {
      this.start = 0;
      this.size = 0;
   }
}
