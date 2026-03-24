package net.minecraft.server;

public class TickTask implements Runnable {
   private final int tick;
   private final Runnable runnable;

   public TickTask(final int tick, final Runnable runnable) {
      super();
      this.tick = tick;
      this.runnable = runnable;
   }

   public int getTick() {
      return this.tick;
   }

   public void run() {
      this.runnable.run();
   }
}
