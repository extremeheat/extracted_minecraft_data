package net.minecraft.server.rcon.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import org.slf4j.Logger;

public abstract class GenericThread implements Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final int MAX_STOP_WAIT = 5;
   protected volatile boolean running;
   protected final String name;
   @Nullable
   protected Thread thread;

   protected GenericThread(String var1) {
      super();
      this.name = var1;
   }

   public synchronized boolean start() {
      if (this.running) {
         return true;
      } else {
         this.running = true;
         String var10004 = this.name;
         this.thread = new Thread(this, var10004 + " #" + UNIQUE_THREAD_ID.incrementAndGet());
         this.thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
         this.thread.start();
         LOGGER.info("Thread {} started", this.name);
         return true;
      }
   }

   public synchronized void stop() {
      this.running = false;
      if (null != this.thread) {
         int var1 = 0;

         while(this.thread.isAlive()) {
            try {
               this.thread.join(1000L);
               ++var1;
               if (var1 >= 5) {
                  LOGGER.warn("Waited {} seconds attempting force stop!", var1);
               } else if (this.thread.isAlive()) {
                  LOGGER.warn("Thread {} ({}) failed to exit after {} second(s)", new Object[]{this, this.thread.getState(), var1, new Exception("Stack:")});
                  this.thread.interrupt();
               }
            } catch (InterruptedException var3) {
            }
         }

         LOGGER.info("Thread {} stopped", this.name);
         this.thread = null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }
}
