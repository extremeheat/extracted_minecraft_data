package net.minecraft.world;

public record Stopwatch(long creationTime, long accumulatedElapsedTime) {
   public Stopwatch(final long creationTime) {
      this(creationTime, 0L);
   }

   public Stopwatch {
      super();
   }

   public long elapsedMilliseconds(final long currentTime) {
      long timeSinceInstanceCreation = currentTime - this.creationTime;
      return this.accumulatedElapsedTime + timeSinceInstanceCreation;
   }

   public double elapsedSeconds(final long currentTime) {
      return (double)this.elapsedMilliseconds(currentTime) / 1000.0;
   }
}
