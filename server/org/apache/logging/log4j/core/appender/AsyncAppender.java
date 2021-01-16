package org.apache.logging.log4j.core.appender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLogEvent;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.ArrayBlockingQueueFactory;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicy;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicyFactory;
import org.apache.logging.log4j.core.async.BlockingQueueFactory;
import org.apache.logging.log4j.core.async.DiscardingAsyncQueueFullPolicy;
import org.apache.logging.log4j.core.async.EventRoute;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "Async",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class AsyncAppender extends AbstractAppender {
   private static final int DEFAULT_QUEUE_SIZE = 128;
   private static final LogEvent SHUTDOWN_LOG_EVENT = new AbstractLogEvent() {
   };
   private static final AtomicLong THREAD_SEQUENCE = new AtomicLong(1L);
   private final BlockingQueue<LogEvent> queue;
   private final int queueSize;
   private final boolean blocking;
   private final long shutdownTimeout;
   private final Configuration config;
   private final AppenderRef[] appenderRefs;
   private final String errorRef;
   private final boolean includeLocation;
   private AppenderControl errorAppender;
   private AsyncAppender.AsyncThread thread;
   private AsyncQueueFullPolicy asyncQueueFullPolicy;

   private AsyncAppender(String var1, Filter var2, AppenderRef[] var3, String var4, int var5, boolean var6, boolean var7, long var8, Configuration var10, boolean var11, BlockingQueueFactory<LogEvent> var12) {
      super(var1, var2, (Layout)null, var7);
      this.queue = var12.create(var5);
      this.queueSize = var5;
      this.blocking = var6;
      this.shutdownTimeout = var8;
      this.config = var10;
      this.appenderRefs = var3;
      this.errorRef = var4;
      this.includeLocation = var11;
   }

   public void start() {
      Map var1 = this.config.getAppenders();
      ArrayList var2 = new ArrayList();
      AppenderRef[] var3 = this.appenderRefs;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         AppenderRef var6 = var3[var5];
         Appender var7 = (Appender)var1.get(var6.getRef());
         if (var7 != null) {
            var2.add(new AppenderControl(var7, var6.getLevel(), var6.getFilter()));
         } else {
            LOGGER.error((String)"No appender named {} was configured", (Object)var6);
         }
      }

      if (this.errorRef != null) {
         Appender var8 = (Appender)var1.get(this.errorRef);
         if (var8 != null) {
            this.errorAppender = new AppenderControl(var8, (Level)null, (Filter)null);
         } else {
            LOGGER.error((String)"Unable to set up error Appender. No appender named {} was configured", (Object)this.errorRef);
         }
      }

      if (var2.size() > 0) {
         this.thread = new AsyncAppender.AsyncThread(var2, this.queue);
         this.thread.setName("AsyncAppender-" + this.getName());
      } else if (this.errorRef == null) {
         throw new ConfigurationException("No appenders are available for AsyncAppender " + this.getName());
      }

      this.asyncQueueFullPolicy = AsyncQueueFullPolicyFactory.create();
      this.thread.start();
      super.start();
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      LOGGER.trace((String)"AsyncAppender stopping. Queue still has {} events.", (Object)this.queue.size());
      this.thread.shutdown();

      try {
         this.thread.join(this.shutdownTimeout);
      } catch (InterruptedException var5) {
         LOGGER.warn((String)"Interrupted while stopping AsyncAppender {}", (Object)this.getName());
      }

      LOGGER.trace((String)"AsyncAppender stopped. Queue has {} events.", (Object)this.queue.size());
      if (DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy) > 0L) {
         LOGGER.trace((String)"AsyncAppender: {} discarded {} events.", (Object)this.asyncQueueFullPolicy, (Object)DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy));
      }

      this.setStopped();
      return true;
   }

   public void append(LogEvent var1) {
      if (!this.isStarted()) {
         throw new IllegalStateException("AsyncAppender " + this.getName() + " is not active");
      } else {
         if (!this.canFormatMessageInBackground(var1.getMessage())) {
            var1.getMessage().getFormattedMessage();
         }

         Log4jLogEvent var2 = Log4jLogEvent.createMemento(var1, this.includeLocation);
         if (!this.transfer(var2)) {
            if (this.blocking) {
               EventRoute var3 = this.asyncQueueFullPolicy.getRoute(this.thread.getId(), var2.getLevel());
               var3.logMessage((AsyncAppender)this, var2);
            } else {
               this.error("Appender " + this.getName() + " is unable to write primary appenders. queue is full");
               this.logToErrorAppenderIfNecessary(false, var2);
            }
         }

      }
   }

   private boolean canFormatMessageInBackground(Message var1) {
      return Constants.FORMAT_MESSAGES_IN_BACKGROUND || var1.getClass().isAnnotationPresent(AsynchronouslyFormattable.class);
   }

   private boolean transfer(LogEvent var1) {
      return this.queue instanceof TransferQueue ? ((TransferQueue)this.queue).tryTransfer(var1) : this.queue.offer(var1);
   }

   public void logMessageInCurrentThread(LogEvent var1) {
      var1.setEndOfBatch(this.queue.isEmpty());
      boolean var2 = this.thread.callAppenders(var1);
      this.logToErrorAppenderIfNecessary(var2, var1);
   }

   public void logMessageInBackgroundThread(LogEvent var1) {
      try {
         this.queue.put(var1);
      } catch (InterruptedException var4) {
         boolean var3 = this.handleInterruptedException(var1);
         this.logToErrorAppenderIfNecessary(var3, var1);
      }

   }

   private boolean handleInterruptedException(LogEvent var1) {
      boolean var2 = this.queue.offer(var1);
      if (!var2) {
         LOGGER.warn((String)"Interrupted while waiting for a free slot in the AsyncAppender LogEvent-queue {}", (Object)this.getName());
      }

      Thread.currentThread().interrupt();
      return var2;
   }

   private void logToErrorAppenderIfNecessary(boolean var1, LogEvent var2) {
      if (!var1 && this.errorAppender != null) {
         this.errorAppender.callAppender(var2);
      }

   }

   /** @deprecated */
   @Deprecated
   public static AsyncAppender createAppender(AppenderRef[] var0, String var1, boolean var2, long var3, int var5, String var6, boolean var7, Filter var8, Configuration var9, boolean var10) {
      if (var6 == null) {
         LOGGER.error("No name provided for AsyncAppender");
         return null;
      } else {
         if (var0 == null) {
            LOGGER.error((String)"No appender references provided to AsyncAppender {}", (Object)var6);
         }

         return new AsyncAppender(var6, var8, var0, var1, var5, var2, var10, var3, var9, var7, new ArrayBlockingQueueFactory());
      }
   }

   @PluginBuilderFactory
   public static AsyncAppender.Builder newBuilder() {
      return new AsyncAppender.Builder();
   }

   public String[] getAppenderRefStrings() {
      String[] var1 = new String[this.appenderRefs.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = this.appenderRefs[var2].getRef();
      }

      return var1;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   public boolean isBlocking() {
      return this.blocking;
   }

   public String getErrorRef() {
      return this.errorRef;
   }

   public int getQueueCapacity() {
      return this.queueSize;
   }

   public int getQueueRemainingCapacity() {
      return this.queue.remainingCapacity();
   }

   // $FF: synthetic method
   AsyncAppender(String var1, Filter var2, AppenderRef[] var3, String var4, int var5, boolean var6, boolean var7, long var8, Configuration var10, boolean var11, BlockingQueueFactory var12, Object var13) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var10, var11, var12);
   }

   private class AsyncThread extends Log4jThread {
      private volatile boolean shutdown = false;
      private final List<AppenderControl> appenders;
      private final BlockingQueue<LogEvent> queue;

      public AsyncThread(List<AppenderControl> var2, BlockingQueue<LogEvent> var3) {
         super("AsyncAppender-" + AsyncAppender.THREAD_SEQUENCE.getAndIncrement());
         this.appenders = var2;
         this.queue = var3;
         this.setDaemon(true);
      }

      public void run() {
         while(true) {
            if (!this.shutdown) {
               label49: {
                  LogEvent var8;
                  try {
                     var8 = (LogEvent)this.queue.take();
                     if (var8 == AsyncAppender.SHUTDOWN_LOG_EVENT) {
                        this.shutdown = true;
                        continue;
                     }
                  } catch (InterruptedException var7) {
                     break label49;
                  }

                  var8.setEndOfBatch(this.queue.isEmpty());
                  boolean var9 = this.callAppenders(var8);
                  if (var9 || AsyncAppender.this.errorAppender == null) {
                     continue;
                  }

                  try {
                     AsyncAppender.this.errorAppender.callAppender(var8);
                  } catch (Exception var5) {
                  }
                  continue;
               }
            }

            AsyncAppender.LOGGER.trace((String)"AsyncAppender.AsyncThread shutting down. Processing remaining {} queue events.", (Object)this.queue.size());
            int var1 = 0;
            int var2 = 0;

            while(!this.queue.isEmpty()) {
               try {
                  LogEvent var3 = (LogEvent)this.queue.take();
                  if (var3 instanceof Log4jLogEvent) {
                     Log4jLogEvent var4 = (Log4jLogEvent)var3;
                     var4.setEndOfBatch(this.queue.isEmpty());
                     this.callAppenders(var4);
                     ++var1;
                  } else {
                     ++var2;
                     AsyncAppender.LOGGER.trace((String)"Ignoring event of class {}", (Object)var3.getClass().getName());
                  }
               } catch (InterruptedException var6) {
               }
            }

            AsyncAppender.LOGGER.trace((String)"AsyncAppender.AsyncThread stopped. Queue has {} events remaining. Processed {} and ignored {} events since shutdown started.", (Object)this.queue.size(), var1, var2);
            return;
         }
      }

      boolean callAppenders(LogEvent var1) {
         boolean var2 = false;
         Iterator var3 = this.appenders.iterator();

         while(var3.hasNext()) {
            AppenderControl var4 = (AppenderControl)var3.next();

            try {
               var4.callAppender(var1);
               var2 = true;
            } catch (Exception var6) {
            }
         }

         return var2;
      }

      public void shutdown() {
         this.shutdown = true;
         if (this.queue.isEmpty()) {
            this.queue.offer(AsyncAppender.SHUTDOWN_LOG_EVENT);
         }

         if (this.getState() == java.lang.Thread.State.TIMED_WAITING || this.getState() == java.lang.Thread.State.WAITING) {
            this.interrupt();
         }

      }
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<AsyncAppender> {
      @PluginElement("AppenderRef")
      @Required(
         message = "No appender references provided to AsyncAppender"
      )
      private AppenderRef[] appenderRefs;
      @PluginBuilderAttribute
      @PluginAliases({"error-ref"})
      private String errorRef;
      @PluginBuilderAttribute
      private boolean blocking = true;
      @PluginBuilderAttribute
      private long shutdownTimeout = 0L;
      @PluginBuilderAttribute
      private int bufferSize = 128;
      @PluginBuilderAttribute
      @Required(
         message = "No name provided for AsyncAppender"
      )
      private String name;
      @PluginBuilderAttribute
      private boolean includeLocation = false;
      @PluginElement("Filter")
      private Filter filter;
      @PluginConfiguration
      private Configuration configuration;
      @PluginBuilderAttribute
      private boolean ignoreExceptions = true;
      @PluginElement("BlockingQueueFactory")
      private BlockingQueueFactory<LogEvent> blockingQueueFactory = new ArrayBlockingQueueFactory();

      public Builder() {
         super();
      }

      public AsyncAppender.Builder setAppenderRefs(AppenderRef[] var1) {
         this.appenderRefs = var1;
         return this;
      }

      public AsyncAppender.Builder setErrorRef(String var1) {
         this.errorRef = var1;
         return this;
      }

      public AsyncAppender.Builder setBlocking(boolean var1) {
         this.blocking = var1;
         return this;
      }

      public AsyncAppender.Builder setShutdownTimeout(long var1) {
         this.shutdownTimeout = var1;
         return this;
      }

      public AsyncAppender.Builder setBufferSize(int var1) {
         this.bufferSize = var1;
         return this;
      }

      public AsyncAppender.Builder setName(String var1) {
         this.name = var1;
         return this;
      }

      public AsyncAppender.Builder setIncludeLocation(boolean var1) {
         this.includeLocation = var1;
         return this;
      }

      public AsyncAppender.Builder setFilter(Filter var1) {
         this.filter = var1;
         return this;
      }

      public AsyncAppender.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public AsyncAppender.Builder setIgnoreExceptions(boolean var1) {
         this.ignoreExceptions = var1;
         return this;
      }

      public AsyncAppender.Builder setBlockingQueueFactory(BlockingQueueFactory<LogEvent> var1) {
         this.blockingQueueFactory = var1;
         return this;
      }

      public AsyncAppender build() {
         return new AsyncAppender(this.name, this.filter, this.appenderRefs, this.errorRef, this.bufferSize, this.blocking, this.ignoreExceptions, this.shutdownTimeout, this.configuration, this.includeLocation, this.blockingQueueFactory);
      }
   }
}
