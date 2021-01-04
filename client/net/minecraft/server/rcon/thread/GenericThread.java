package net.minecraft.server.rcon.thread;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.server.ServerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class GenericThread implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   protected boolean running;
   protected final ServerInterface serverInterface;
   protected final String name;
   protected Thread thread;
   protected final int maxStopWait = 5;
   protected final List<DatagramSocket> datagramSockets = Lists.newArrayList();
   protected final List<ServerSocket> serverSockets = Lists.newArrayList();

   protected GenericThread(ServerInterface var1, String var2) {
      super();
      this.serverInterface = var1;
      this.name = var2;
      if (this.serverInterface.isDebugging()) {
         this.warn("Debugging is enabled, performance maybe reduced!");
      }

   }

   public synchronized void start() {
      this.thread = new Thread(this, this.name + " #" + UNIQUE_THREAD_ID.incrementAndGet());
      this.thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
      this.thread.start();
      this.running = true;
   }

   public synchronized void stop() {
      this.running = false;
      if (null != this.thread) {
         int var1 = 0;

         while(this.thread.isAlive()) {
            try {
               this.thread.join(1000L);
               ++var1;
               if (5 <= var1) {
                  this.warn("Waited " + var1 + " seconds attempting force stop!");
                  this.closeSockets(true);
               } else if (this.thread.isAlive()) {
                  this.warn("Thread " + this + " (" + this.thread.getState() + ") failed to exit after " + var1 + " second(s)");
                  this.warn("Stack:");
                  StackTraceElement[] var2 = this.thread.getStackTrace();
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     StackTraceElement var5 = var2[var4];
                     this.warn(var5.toString());
                  }

                  this.thread.interrupt();
               }
            } catch (InterruptedException var6) {
            }
         }

         this.closeSockets(true);
         this.thread = null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   protected void debug(String var1) {
      this.serverInterface.debug(var1);
   }

   protected void info(String var1) {
      this.serverInterface.info(var1);
   }

   protected void warn(String var1) {
      this.serverInterface.warn(var1);
   }

   protected void error(String var1) {
      this.serverInterface.error(var1);
   }

   protected int currentPlayerCount() {
      return this.serverInterface.getPlayerCount();
   }

   protected void registerSocket(DatagramSocket var1) {
      this.debug("registerSocket: " + var1);
      this.datagramSockets.add(var1);
   }

   protected boolean closeSocket(DatagramSocket var1, boolean var2) {
      this.debug("closeSocket: " + var1);
      if (null == var1) {
         return false;
      } else {
         boolean var3 = false;
         if (!var1.isClosed()) {
            var1.close();
            var3 = true;
         }

         if (var2) {
            this.datagramSockets.remove(var1);
         }

         return var3;
      }
   }

   protected boolean closeSocket(ServerSocket var1) {
      return this.closeSocket(var1, true);
   }

   protected boolean closeSocket(ServerSocket var1, boolean var2) {
      this.debug("closeSocket: " + var1);
      if (null == var1) {
         return false;
      } else {
         boolean var3 = false;

         try {
            if (!var1.isClosed()) {
               var1.close();
               var3 = true;
            }
         } catch (IOException var5) {
            this.warn("IO: " + var5.getMessage());
         }

         if (var2) {
            this.serverSockets.remove(var1);
         }

         return var3;
      }
   }

   protected void closeSockets() {
      this.closeSockets(false);
   }

   protected void closeSockets(boolean var1) {
      int var2 = 0;
      Iterator var3 = this.datagramSockets.iterator();

      while(var3.hasNext()) {
         DatagramSocket var4 = (DatagramSocket)var3.next();
         if (this.closeSocket(var4, false)) {
            ++var2;
         }
      }

      this.datagramSockets.clear();
      var3 = this.serverSockets.iterator();

      while(var3.hasNext()) {
         ServerSocket var5 = (ServerSocket)var3.next();
         if (this.closeSocket(var5, false)) {
            ++var2;
         }
      }

      this.serverSockets.clear();
      if (var1 && 0 < var2) {
         this.warn("Force closed " + var2 + " sockets");
      }

   }
}
