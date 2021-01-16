package org.apache.commons.lang3.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class BasicThreadFactory implements ThreadFactory {
   private final AtomicLong threadCounter;
   private final ThreadFactory wrappedFactory;
   private final UncaughtExceptionHandler uncaughtExceptionHandler;
   private final String namingPattern;
   private final Integer priority;
   private final Boolean daemonFlag;

   private BasicThreadFactory(BasicThreadFactory.Builder var1) {
      super();
      if (var1.wrappedFactory == null) {
         this.wrappedFactory = Executors.defaultThreadFactory();
      } else {
         this.wrappedFactory = var1.wrappedFactory;
      }

      this.namingPattern = var1.namingPattern;
      this.priority = var1.priority;
      this.daemonFlag = var1.daemonFlag;
      this.uncaughtExceptionHandler = var1.exceptionHandler;
      this.threadCounter = new AtomicLong();
   }

   public final ThreadFactory getWrappedFactory() {
      return this.wrappedFactory;
   }

   public final String getNamingPattern() {
      return this.namingPattern;
   }

   public final Boolean getDaemonFlag() {
      return this.daemonFlag;
   }

   public final Integer getPriority() {
      return this.priority;
   }

   public final UncaughtExceptionHandler getUncaughtExceptionHandler() {
      return this.uncaughtExceptionHandler;
   }

   public long getThreadCount() {
      return this.threadCounter.get();
   }

   public Thread newThread(Runnable var1) {
      Thread var2 = this.getWrappedFactory().newThread(var1);
      this.initializeThread(var2);
      return var2;
   }

   private void initializeThread(Thread var1) {
      if (this.getNamingPattern() != null) {
         Long var2 = this.threadCounter.incrementAndGet();
         var1.setName(String.format(this.getNamingPattern(), var2));
      }

      if (this.getUncaughtExceptionHandler() != null) {
         var1.setUncaughtExceptionHandler(this.getUncaughtExceptionHandler());
      }

      if (this.getPriority() != null) {
         var1.setPriority(this.getPriority());
      }

      if (this.getDaemonFlag() != null) {
         var1.setDaemon(this.getDaemonFlag());
      }

   }

   // $FF: synthetic method
   BasicThreadFactory(BasicThreadFactory.Builder var1, Object var2) {
      this(var1);
   }

   public static class Builder implements org.apache.commons.lang3.builder.Builder<BasicThreadFactory> {
      private ThreadFactory wrappedFactory;
      private UncaughtExceptionHandler exceptionHandler;
      private String namingPattern;
      private Integer priority;
      private Boolean daemonFlag;

      public Builder() {
         super();
      }

      public BasicThreadFactory.Builder wrappedFactory(ThreadFactory var1) {
         if (var1 == null) {
            throw new NullPointerException("Wrapped ThreadFactory must not be null!");
         } else {
            this.wrappedFactory = var1;
            return this;
         }
      }

      public BasicThreadFactory.Builder namingPattern(String var1) {
         if (var1 == null) {
            throw new NullPointerException("Naming pattern must not be null!");
         } else {
            this.namingPattern = var1;
            return this;
         }
      }

      public BasicThreadFactory.Builder daemon(boolean var1) {
         this.daemonFlag = var1;
         return this;
      }

      public BasicThreadFactory.Builder priority(int var1) {
         this.priority = var1;
         return this;
      }

      public BasicThreadFactory.Builder uncaughtExceptionHandler(UncaughtExceptionHandler var1) {
         if (var1 == null) {
            throw new NullPointerException("Uncaught exception handler must not be null!");
         } else {
            this.exceptionHandler = var1;
            return this;
         }
      }

      public void reset() {
         this.wrappedFactory = null;
         this.exceptionHandler = null;
         this.namingPattern = null;
         this.priority = null;
         this.daemonFlag = null;
      }

      public BasicThreadFactory build() {
         BasicThreadFactory var1 = new BasicThreadFactory(this);
         this.reset();
         return var1;
      }
   }
}
