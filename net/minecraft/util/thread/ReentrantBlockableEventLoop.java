package net.minecraft.util.thread;

public abstract class ReentrantBlockableEventLoop extends BlockableEventLoop {
   private int reentrantCount;

   public ReentrantBlockableEventLoop(String var1) {
      super(var1);
   }

   protected boolean scheduleExecutables() {
      return this.runningTask() || super.scheduleExecutables();
   }

   protected boolean runningTask() {
      return this.reentrantCount != 0;
   }

   protected void doRunTask(Runnable var1) {
      ++this.reentrantCount;

      try {
         super.doRunTask(var1);
      } finally {
         --this.reentrantCount;
      }

   }
}
