package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public final class ImmediateEventExecutor extends AbstractEventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ImmediateEventExecutor.class);
   public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
   private static final FastThreadLocal<Queue<Runnable>> DELAYED_RUNNABLES = new FastThreadLocal<Queue<Runnable>>() {
      protected Queue<Runnable> initialValue() throws Exception {
         return new ArrayDeque();
      }
   };
   private static final FastThreadLocal<Boolean> RUNNING = new FastThreadLocal<Boolean>() {
      protected Boolean initialValue() throws Exception {
         return false;
      }
   };
   private final Future<?> terminationFuture;

   private ImmediateEventExecutor() {
      super();
      this.terminationFuture = new FailedFuture(GlobalEventExecutor.INSTANCE, new UnsupportedOperationException());
   }

   public boolean inEventLoop() {
      return true;
   }

   public boolean inEventLoop(Thread var1) {
      return true;
   }

   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      return this.terminationFuture();
   }

   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
   }

   public boolean isShuttingDown() {
      return false;
   }

   public boolean isShutdown() {
      return false;
   }

   public boolean isTerminated() {
      return false;
   }

   public boolean awaitTermination(long var1, TimeUnit var3) {
      return false;
   }

   public void execute(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException("command");
      } else {
         if (!(Boolean)RUNNING.get()) {
            RUNNING.set(true);
            boolean var14 = false;

            Queue var2;
            Runnable var3;
            label143: {
               try {
                  var14 = true;
                  var1.run();
                  var14 = false;
                  break label143;
               } catch (Throwable var18) {
                  logger.info("Throwable caught while executing Runnable {}", var1, var18);
                  var14 = false;
               } finally {
                  if (var14) {
                     Queue var6 = (Queue)DELAYED_RUNNABLES.get();

                     Runnable var7;
                     while((var7 = (Runnable)var6.poll()) != null) {
                        try {
                           var7.run();
                        } catch (Throwable var15) {
                           logger.info("Throwable caught while executing Runnable {}", var7, var15);
                        }
                     }

                     RUNNING.set(false);
                  }
               }

               var2 = (Queue)DELAYED_RUNNABLES.get();

               while((var3 = (Runnable)var2.poll()) != null) {
                  try {
                     var3.run();
                  } catch (Throwable var16) {
                     logger.info("Throwable caught while executing Runnable {}", var3, var16);
                  }
               }

               RUNNING.set(false);
               return;
            }

            var2 = (Queue)DELAYED_RUNNABLES.get();

            while((var3 = (Runnable)var2.poll()) != null) {
               try {
                  var3.run();
               } catch (Throwable var17) {
                  logger.info("Throwable caught while executing Runnable {}", var3, var17);
               }
            }

            RUNNING.set(false);
         } else {
            ((Queue)DELAYED_RUNNABLES.get()).add(var1);
         }

      }
   }

   public <V> Promise<V> newPromise() {
      return new ImmediateEventExecutor.ImmediatePromise(this);
   }

   public <V> ProgressivePromise<V> newProgressivePromise() {
      return new ImmediateEventExecutor.ImmediateProgressivePromise(this);
   }

   static class ImmediateProgressivePromise<V> extends DefaultProgressivePromise<V> {
      ImmediateProgressivePromise(EventExecutor var1) {
         super(var1);
      }

      protected void checkDeadLock() {
      }
   }

   static class ImmediatePromise<V> extends DefaultPromise<V> {
      ImmediatePromise(EventExecutor var1) {
         super(var1);
      }

      protected void checkDeadLock() {
      }
   }
}
