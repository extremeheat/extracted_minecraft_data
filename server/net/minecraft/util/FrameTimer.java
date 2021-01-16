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

   public int wrapIndex(int var1) {
      return var1 % 240;
   }
}
