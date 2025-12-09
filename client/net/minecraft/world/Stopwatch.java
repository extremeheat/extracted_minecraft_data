package net.minecraft.world;

public record Stopwatch(long creationTime, long accumulatedElapsedTime) {
   public Stopwatch(long var1) {
      this(var1, 0L);
   }

   public Stopwatch(long var1, long var3) {
      super();
      this.creationTime = var1;
      this.accumulatedElapsedTime = var3;
   }

   public long elapsedMilliseconds(long var1) {
      long var3 = var1 - this.creationTime;
      return this.accumulatedElapsedTime + var3;
   }

   public double elapsedSeconds(long var1) {
      return (double)this.elapsedMilliseconds(var1) / 1000.0;
   }
}
