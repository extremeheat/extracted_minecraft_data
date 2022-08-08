package net.minecraft.util;

public class FrameTimer {
   public static final int LOGGING_LENGTH = 240;
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

   public long getAverageDuration(int var1) {
      int var2 = (this.logStart + var1) % 240;
      int var3 = this.logStart;

      long var4;
      for(var4 = 0L; var3 != var2; ++var3) {
         var4 += this.loggedTimes[var3];
      }

      return var4 / (long)var1;
   }

   public int scaleAverageDurationTo(int var1, int var2) {
      return this.scaleSampleTo(this.getAverageDuration(var1), var2, 60);
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
