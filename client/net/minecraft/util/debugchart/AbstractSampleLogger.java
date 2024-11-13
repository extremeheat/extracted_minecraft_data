package net.minecraft.util.debugchart;

public abstract class AbstractSampleLogger implements SampleLogger {
   protected final long[] defaults;
   protected final long[] sample;

   protected AbstractSampleLogger(int var1, long[] var2) {
      super();
      if (var2.length != var1) {
         throw new IllegalArgumentException("defaults have incorrect length of " + var2.length);
      } else {
         this.sample = new long[var1];
         this.defaults = var2;
      }
   }

   public void logFullSample(long[] var1) {
      System.arraycopy(var1, 0, this.sample, 0, var1.length);
      this.useSample();
      this.resetSample();
   }

   public void logSample(long var1) {
      this.sample[0] = var1;
      this.useSample();
      this.resetSample();
   }

   public void logPartialSample(long var1, int var3) {
      if (var3 >= 1 && var3 < this.sample.length) {
         this.sample[var3] = var1;
      } else {
         throw new IndexOutOfBoundsException(var3 + " out of bounds for dimensions " + this.sample.length);
      }
   }

   protected abstract void useSample();

   protected void resetSample() {
      System.arraycopy(this.defaults, 0, this.sample, 0, this.defaults.length);
   }
}
