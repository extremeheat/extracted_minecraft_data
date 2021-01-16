package org.apache.logging.log4j.core.appender.db;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;

public abstract class AbstractDatabaseAppender<T extends AbstractDatabaseManager> extends AbstractAppender {
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock readLock;
   private final Lock writeLock;
   private T manager;

   protected AbstractDatabaseAppender(String var1, Filter var2, boolean var3, T var4) {
      super(var1, var2, (Layout)null, var3);
      this.readLock = this.lock.readLock();
      this.writeLock = this.lock.writeLock();
      this.manager = var4;
   }

   public final Layout<LogEvent> getLayout() {
      return null;
   }

   public final T getManager() {
      return this.manager;
   }

   public final void start() {
      if (this.getManager() == null) {
         LOGGER.error((String)"No AbstractDatabaseManager set for the appender named [{}].", (Object)this.getName());
      }

      super.start();
      if (this.getManager() != null) {
         this.getManager().startup();
      }

   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      if (this.getManager() != null) {
         var4 &= this.getManager().stop(var1, var3);
      }

      this.setStopped();
      return var4;
   }

   public final void append(LogEvent var1) {
      this.readLock.lock();

      try {
         this.getManager().write(var1);
      } catch (LoggingException var7) {
         LOGGER.error((String)"Unable to write to database [{}] for appender [{}].", (Object)this.getManager().getName(), this.getName(), var7);
         throw var7;
      } catch (Exception var8) {
         LOGGER.error((String)"Unable to write to database [{}] for appender [{}].", (Object)this.getManager().getName(), this.getName(), var8);
         throw new AppenderLoggingException("Unable to write to database in appender: " + var8.getMessage(), var8);
      } finally {
         this.readLock.unlock();
      }

   }

   protected final void replaceManager(T var1) {
      this.writeLock.lock();

      try {
         AbstractDatabaseManager var2 = this.getManager();
         if (!var1.isRunning()) {
            var1.startup();
         }

         this.manager = var1;
         var2.close();
      } finally {
         this.writeLock.unlock();
      }

   }
}
