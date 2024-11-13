package net.minecraft.util.debugchart;

public class LocalSampleLogger extends AbstractSampleLogger implements SampleStorage {
   public static final int CAPACITY = 240;
   private final long[][] samples;
   private int start;
   private int size;

   public LocalSampleLogger(int var1) {
      this(var1, new long[var1]);
   }

   public LocalSampleLogger(int var1, long[] var2) {
      super(var1, var2);
      this.samples = new long[240][var1];
   }

   protected void useSample() {
      int var1 = this.wrapIndex(this.start + this.size);
      System.arraycopy(this.sample, 0, this.samples[var1], 0, this.sample.length);
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
      return this.get(var1, 0);
   }

   public long get(int var1, int var2) {
      if (var1 >= 0 && var1 < this.size) {
         long[] var3 = this.samples[this.wrapIndex(this.start + var1)];
         if (var2 >= 0 && var2 < var3.length) {
            return var3[var2];
         } else {
            throw new IndexOutOfBoundsException(var2 + " out of bounds for dimensions " + var3.length);
         }
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
