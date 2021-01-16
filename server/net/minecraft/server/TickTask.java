package net.minecraft.server;

public class TickTask implements Runnable {
   private final int tick;
   private final Runnable runnable;

   public TickTask(int var1, Runnable var2) {
      super();
      this.tick = var1;
      this.runnable = var2;
   }

   public int getTick() {
      return this.tick;
   }

   public void run() {
      this.runnable.run();
   }
}
