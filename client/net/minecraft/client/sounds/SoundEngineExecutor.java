package net.minecraft.client.sounds;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;

public class SoundEngineExecutor extends BlockableEventLoop<Runnable> {
   private Thread thread = this.createThread();
   private volatile boolean shutdown;

   public SoundEngineExecutor() {
      super("Sound executor", false);
   }

   private Thread createThread() {
      Thread thread = new Thread(this::run);
      thread.setDaemon(true);
      thread.setName("Sound engine");
      thread.setUncaughtExceptionHandler((t, e) -> Minecraft.getInstance().delayCrash(CrashReport.forThrowable(e, "Uncaught exception on thread: " + t.getName())));
      thread.start();
      return thread;
   }

   public Runnable wrapRunnable(final Runnable runnable) {
      return runnable;
   }

   public void schedule(final Runnable runnable) {
      if (!this.shutdown) {
         super.schedule(runnable);
      }

   }

   protected boolean shouldRun(final Runnable task) {
      return !this.shutdown;
   }

   protected Thread getRunningThread() {
      return this.thread;
   }

   private void run() {
      while(!this.shutdown) {
         this.managedBlock(() -> this.shutdown);
      }

   }

   protected void waitForTasks() {
      LockSupport.park("waiting for tasks");
   }

   public void shutDown() {
      this.shutdown = true;
      this.dropAllTasks();
      this.thread.interrupt();

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      }

   }

   public void startUp() {
      this.shutdown = false;
      this.thread = this.createThread();
   }
}
