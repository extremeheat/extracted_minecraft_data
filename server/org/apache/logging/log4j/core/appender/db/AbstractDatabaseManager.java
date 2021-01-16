package org.apache.logging.log4j.core.appender.db;

import java.io.Flushable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;

public abstract class AbstractDatabaseManager extends AbstractManager implements Flushable {
   private final ArrayList<LogEvent> buffer;
   private final int bufferSize;
   private boolean running = false;

   protected AbstractDatabaseManager(String var1, int var2) {
      super((LoggerContext)null, var1);
      this.bufferSize = var2;
      this.buffer = new ArrayList(var2 + 1);
   }

   protected abstract void startupInternal() throws Exception;

   public final synchronized void startup() {
      if (!this.isRunning()) {
         try {
            this.startupInternal();
            this.running = true;
         } catch (Exception var2) {
            this.logError("Could not perform database startup operations", var2);
         }
      }

   }

   protected abstract boolean shutdownInternal() throws Exception;

   public final synchronized boolean shutdown() {
      boolean var1 = true;
      this.flush();
      if (this.isRunning()) {
         try {
            var1 &= this.shutdownInternal();
         } catch (Exception var6) {
            this.logWarn("Caught exception while performing database shutdown operations", var6);
            var1 = false;
         } finally {
            this.running = false;
         }
      }

      return var1;
   }

   public final boolean isRunning() {
      return this.running;
   }

   protected abstract void connectAndStart();

   protected abstract void writeInternal(LogEvent var1);

   protected abstract boolean commitAndClose();

   public final synchronized void flush() {
      if (this.isRunning() && this.buffer.size() > 0) {
         this.connectAndStart();

         try {
            Iterator var1 = this.buffer.iterator();

            while(var1.hasNext()) {
               LogEvent var2 = (LogEvent)var1.next();
               this.writeInternal(var2);
            }
         } finally {
            this.commitAndClose();
            this.buffer.clear();
         }
      }

   }

   public final synchronized void write(LogEvent var1) {
      if (this.bufferSize > 0) {
         this.buffer.add(var1);
         if (this.buffer.size() >= this.bufferSize || var1.isEndOfBatch()) {
            this.flush();
         }
      } else {
         this.connectAndStart();

         try {
            this.writeInternal(var1);
         } finally {
            this.commitAndClose();
         }
      }

   }

   public final boolean releaseSub(long var1, TimeUnit var3) {
      return this.shutdown();
   }

   public final String toString() {
      return this.getName();
   }

   protected static <M extends AbstractDatabaseManager, T extends AbstractDatabaseManager.AbstractFactoryData> M getManager(String var0, T var1, ManagerFactory<M, T> var2) {
      return (AbstractDatabaseManager)AbstractManager.getManager(var0, var2, var1);
   }

   protected abstract static class AbstractFactoryData {
      private final int bufferSize;

      protected AbstractFactoryData(int var1) {
         super();
         this.bufferSize = var1;
      }

      public int getBufferSize() {
         return this.bufferSize;
      }
   }
}
