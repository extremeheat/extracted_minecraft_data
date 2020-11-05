package net.minecraft.util;

public class FrameTimer {
   private final long[] loggedTimes = new long[240];
   private int logStart;
   private int logLength;
   private int logEnd;

   public FrameTimer() {
      super();
   }

   public void logFrameDuration(long var1) {
      this.loggedTimes[this.logEnd] = var1;
      ++this.logEnd;
      if (this.logEnd == 240) {
         this.logEnd = 0;
      }

      if (this.logLength < 240) {
         this.logStart = 0;
         ++this.logLength;
      } else {
         this.logStart = this.wrapIndex(this.logEnd + 1);
      }

   }

   public int scaleSampleTo(long var1, int var3, int var4) {
      double var5 = (double)var1 / (double)(1000000000L / (long)var4);
      return (int)(var5 * (double)var3);
   }

   public int getLogStart() {
      return this.logStart;
   }

   public int getLogEnd() {
      return this.logEnd;
   }

   public int wrapIndex(int var1) {
      return var1 % 240;
   }

   public long[] getLog() {
      return this.loggedTimes;
   }
}
