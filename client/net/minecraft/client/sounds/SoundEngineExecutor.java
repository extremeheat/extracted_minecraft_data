package net.minecraft.client.sounds;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.util.thread.BlockableEventLoop;

public class SoundEngineExecutor extends BlockableEventLoop<Runnable> {
   private Thread thread = this.createThread();
   private volatile boolean shutdown;

   public SoundEngineExecutor() {
      super("Sound executor");
   }

   private Thread createThread() {
      Thread var1 = new Thread(this::run);
      var1.setDaemon(true);
      var1.setName("Sound engine");
      var1.start();
      return var1;
   }

   protected Runnable wrapRunnable(Runnable var1) {
      return var1;
   }

   protected boolean shouldRun(Runnable var1) {
      return !this.shutdown;
   }

   protected Thread getRunningThread() {
      return this.thread;
   }

   private void run() {
      while(!this.shutdown) {
         this.managedBlock(() -> {
            return this.shutdown;
         });
      }

   }

   protected void waitForTasks() {
      LockSupport.park("waiting for tasks");
   }

   public void flush() {
      this.shutdown = true;
      this.thread.interrupt();

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      }

      this.dropAllTasks();
      this.shutdown = false;
      this.thread = this.createThread();
   }
}
