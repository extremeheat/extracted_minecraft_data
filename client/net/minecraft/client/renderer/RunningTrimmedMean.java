package net.minecraft.client.renderer;

public class RunningTrimmedMean {
   private final long[] values;
   private int count;
   private int cursor;

   public RunningTrimmedMean(int var1) {
      super();
      this.values = new long[var1];
   }

   public long registerValueAndGetMean(long var1) {
      if (this.count < this.values.length) {
         ++this.count;
      }

      this.values[this.cursor] = var1;
      this.cursor = (this.cursor + 1) % this.values.length;
      long var3 = 9223372036854775807L;
      long var5 = -9223372036854775808L;
      long var7 = 0L;

      for(int var9 = 0; var9 < this.count; ++var9) {
         long var10 = this.values[var9];
         var7 += var10;
         var3 = Math.min(var3, var10);
         var5 = Math.max(var5, var10);
      }

      if (this.count > 2) {
         var7 -= var3 + var5;
         return var7 / (long)(this.count - 2);
      } else {
         return var7 > 0L ? (long)this.count / var7 : 0L;
      }
   }
}
