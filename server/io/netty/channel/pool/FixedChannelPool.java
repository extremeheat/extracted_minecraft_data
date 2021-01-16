package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FixedChannelPool extends SimpleChannelPool {
   private static final IllegalStateException FULL_EXCEPTION = (IllegalStateException)ThrowableUtil.unknownStackTrace(new IllegalStateException("Too many outstanding acquire operations"), FixedChannelPool.class, "acquire0(...)");
   private static final TimeoutException TIMEOUT_EXCEPTION = (TimeoutException)ThrowableUtil.unknownStackTrace(new TimeoutException("Acquire operation took longer then configured maximum time"), FixedChannelPool.class, "<init>(...)");
   static final IllegalStateException POOL_CLOSED_ON_RELEASE_EXCEPTION = (IllegalStateException)ThrowableUtil.unknownStackTrace(new IllegalStateException("FixedChannelPool was closed"), FixedChannelPool.class, "release(...)");
   static final IllegalStateException POOL_CLOSED_ON_ACQUIRE_EXCEPTION = (IllegalStateException)ThrowableUtil.unknownStackTrace(new IllegalStateException("FixedChannelPool was closed"), FixedChannelPool.class, "acquire0(...)");
   private final EventExecutor executor;
   private final long acquireTimeoutNanos;
   private final Runnable timeoutTask;
   private final Queue<FixedChannelPool.AcquireTask> pendingAcquireQueue;
   private final int maxConnections;
   private final int maxPendingAcquires;
   private int acquiredChannelCount;
   private int pendingAcquireCount;
   private boolean closed;

   public FixedChannelPool(Bootstrap var1, ChannelPoolHandler var2, int var3) {
      this(var1, var2, var3, 2147483647);
   }

   public FixedChannelPool(Bootstrap var1, ChannelPoolHandler var2, int var3, int var4) {
      this(var1, var2, ChannelHealthChecker.ACTIVE, (FixedChannelPool.AcquireTimeoutAction)null, -1L, var3, var4);
   }

   public FixedChannelPool(Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3, FixedChannelPool.AcquireTimeoutAction var4, long var5, int var7, int var8) {
      this(var1, var2, var3, var4, var5, var7, var8, true);
   }

   public FixedChannelPool(Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3, FixedChannelPool.AcquireTimeoutAction var4, long var5, int var7, int var8, boolean var9) {
      this(var1, var2, var3, var4, var5, var7, var8, var9, true);
   }

   public FixedChannelPool(Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3, FixedChannelPool.AcquireTimeoutAction var4, long var5, int var7, int var8, boolean var9, boolean var10) {
      super(var1, var2, var3, var9, var10);
      this.pendingAcquireQueue = new ArrayDeque();
      if (var7 < 1) {
         throw new IllegalArgumentException("maxConnections: " + var7 + " (expected: >= 1)");
      } else if (var8 < 1) {
         throw new IllegalArgumentException("maxPendingAcquires: " + var8 + " (expected: >= 1)");
      } else {
         if (var4 == null && var5 == -1L) {
            this.timeoutTask = null;
            this.acquireTimeoutNanos = -1L;
         } else {
            if (var4 == null && var5 != -1L) {
               throw new NullPointerException("action");
            }

            if (var4 != null && var5 < 0L) {
               throw new IllegalArgumentException("acquireTimeoutMillis: " + var5 + " (expected: >= 0)");
            }

            this.acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(var5);
            switch(var4) {
            case FAIL:
               this.timeoutTask = new FixedChannelPool.TimeoutTask() {
                  public void onTimeout(FixedChannelPool.AcquireTask var1) {
                     var1.promise.setFailure(FixedChannelPool.TIMEOUT_EXCEPTION);
                  }
               };
               break;
            case NEW:
               this.timeoutTask = new FixedChannelPool.TimeoutTask() {
                  public void onTimeout(FixedChannelPool.AcquireTask var1) {
                     var1.acquired();
                     FixedChannelPool.super.acquire(var1.promise);
                  }
               };
               break;
            default:
               throw new Error();
            }
         }

         this.executor = var1.config().group().next();
         this.maxConnections = var7;
         this.maxPendingAcquires = var8;
      }
   }

   public Future<Channel> acquire(final Promise<Channel> var1) {
      try {
         if (this.executor.inEventLoop()) {
            this.acquire0(var1);
         } else {
            this.executor.execute(new Runnable() {
               public void run() {
                  FixedChannelPool.this.acquire0(var1);
               }
            });
         }
      } catch (Throwable var3) {
         var1.setFailure(var3);
      }

      return var1;
   }

   private void acquire0(Promise<Channel> var1) {
      assert this.executor.inEventLoop();

      if (this.closed) {
         var1.setFailure(POOL_CLOSED_ON_ACQUIRE_EXCEPTION);
      } else {
         if (this.acquiredChannelCount < this.maxConnections) {
            assert this.acquiredChannelCount >= 0;

            Promise var2 = this.executor.newPromise();
            FixedChannelPool.AcquireListener var3 = new FixedChannelPool.AcquireListener(var1);
            var3.acquired();
            var2.addListener(var3);
            super.acquire(var2);
         } else {
            if (this.pendingAcquireCount >= this.maxPendingAcquires) {
               var1.setFailure(FULL_EXCEPTION);
            } else {
               FixedChannelPool.AcquireTask var4 = new FixedChannelPool.AcquireTask(var1);
               if (this.pendingAcquireQueue.offer(var4)) {
                  ++this.pendingAcquireCount;
                  if (this.timeoutTask != null) {
                     var4.timeoutFuture = this.executor.schedule(this.timeoutTask, this.acquireTimeoutNanos, TimeUnit.NANOSECONDS);
                  }
               } else {
                  var1.setFailure(FULL_EXCEPTION);
               }
            }

            assert this.pendingAcquireCount > 0;
         }

      }
   }

   public Future<Void> release(final Channel var1, final Promise<Void> var2) {
      ObjectUtil.checkNotNull(var2, "promise");
      Promise var3 = this.executor.newPromise();
      super.release(var1, var3.addListener(new FutureListener<Void>() {
         public void operationComplete(Future<Void> var1x) throws Exception {
            assert FixedChannelPool.this.executor.inEventLoop();

            if (FixedChannelPool.this.closed) {
               var1.close();
               var2.setFailure(FixedChannelPool.POOL_CLOSED_ON_RELEASE_EXCEPTION);
            } else {
               if (var1x.isSuccess()) {
                  FixedChannelPool.this.decrementAndRunTaskQueue();
                  var2.setSuccess((Object)null);
               } else {
                  Throwable var2x = var1x.cause();
                  if (!(var2x instanceof IllegalArgumentException)) {
                     FixedChannelPool.this.decrementAndRunTaskQueue();
                  }

                  var2.setFailure(var1x.cause());
               }

            }
         }
      }));
      return var2;
   }

   private void decrementAndRunTaskQueue() {
      --this.acquiredChannelCount;

      assert this.acquiredChannelCount >= 0;

      this.runTaskQueue();
   }

   private void runTaskQueue() {
      while(true) {
         if (this.acquiredChannelCount < this.maxConnections) {
            FixedChannelPool.AcquireTask var1 = (FixedChannelPool.AcquireTask)this.pendingAcquireQueue.poll();
            if (var1 != null) {
               ScheduledFuture var2 = var1.timeoutFuture;
               if (var2 != null) {
                  var2.cancel(false);
               }

               --this.pendingAcquireCount;
               var1.acquired();
               super.acquire(var1.promise);
               continue;
            }
         }

         assert this.pendingAcquireCount >= 0;

         assert this.acquiredChannelCount >= 0;

         return;
      }
   }

   public void close() {
      this.executor.execute(new Runnable() {
         public void run() {
            if (!FixedChannelPool.this.closed) {
               FixedChannelPool.this.closed = true;

               while(true) {
                  FixedChannelPool.AcquireTask var1 = (FixedChannelPool.AcquireTask)FixedChannelPool.this.pendingAcquireQueue.poll();
                  if (var1 == null) {
                     FixedChannelPool.this.acquiredChannelCount = 0;
                     FixedChannelPool.this.pendingAcquireCount = 0;
                     FixedChannelPool.super.close();
                     break;
                  }

                  ScheduledFuture var2 = var1.timeoutFuture;
                  if (var2 != null) {
                     var2.cancel(false);
                  }

                  var1.promise.setFailure(new ClosedChannelException());
               }
            }

         }
      });
   }

   private class AcquireListener implements FutureListener<Channel> {
      private final Promise<Channel> originalPromise;
      protected boolean acquired;

      AcquireListener(Promise<Channel> var2) {
         super();
         this.originalPromise = var2;
      }

      public void operationComplete(Future<Channel> var1) throws Exception {
         assert FixedChannelPool.this.executor.inEventLoop();

         if (FixedChannelPool.this.closed) {
            if (var1.isSuccess()) {
               ((Channel)var1.getNow()).close();
            }

            this.originalPromise.setFailure(FixedChannelPool.POOL_CLOSED_ON_ACQUIRE_EXCEPTION);
         } else {
            if (var1.isSuccess()) {
               this.originalPromise.setSuccess(var1.getNow());
            } else {
               if (this.acquired) {
                  FixedChannelPool.this.decrementAndRunTaskQueue();
               } else {
                  FixedChannelPool.this.runTaskQueue();
               }

               this.originalPromise.setFailure(var1.cause());
            }

         }
      }

      public void acquired() {
         if (!this.acquired) {
            FixedChannelPool.this.acquiredChannelCount++;
            this.acquired = true;
         }
      }
   }

   private abstract class TimeoutTask implements Runnable {
      private TimeoutTask() {
         super();
      }

      public final void run() {
         assert FixedChannelPool.this.executor.inEventLoop();

         long var1 = System.nanoTime();

         while(true) {
            FixedChannelPool.AcquireTask var3 = (FixedChannelPool.AcquireTask)FixedChannelPool.this.pendingAcquireQueue.peek();
            if (var3 == null || var1 - var3.expireNanoTime < 0L) {
               return;
            }

            FixedChannelPool.this.pendingAcquireQueue.remove();
            --FixedChannelPool.this.pendingAcquireCount;
            this.onTimeout(var3);
         }
      }

      public abstract void onTimeout(FixedChannelPool.AcquireTask var1);

      // $FF: synthetic method
      TimeoutTask(Object var2) {
         this();
      }
   }

   private final class AcquireTask extends FixedChannelPool.AcquireListener {
      final Promise<Channel> promise;
      final long expireNanoTime;
      ScheduledFuture<?> timeoutFuture;

      public AcquireTask(Promise<Channel> var2) {
         super(var2);
         this.expireNanoTime = System.nanoTime() + FixedChannelPool.this.acquireTimeoutNanos;
         this.promise = FixedChannelPool.this.executor.newPromise().addListener(this);
      }
   }

   public static enum AcquireTimeoutAction {
      NEW,
      FAIL;

      private AcquireTimeoutAction() {
      }
   }
}
