package io.netty.channel.kqueue;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class KQueueEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(KQueueEventLoop.class);
   private static final AtomicIntegerFieldUpdater<KQueueEventLoop> WAKEN_UP_UPDATER = AtomicIntegerFieldUpdater.newUpdater(KQueueEventLoop.class, "wakenUp");
   private static final int KQUEUE_WAKE_UP_IDENT = 0;
   private final NativeLongArray jniChannelPointers;
   private final boolean allowGrowing;
   private final FileDescriptor kqueueFd;
   private final KQueueEventArray changeList;
   private final KQueueEventArray eventList;
   private final SelectStrategy selectStrategy;
   private final IovArray iovArray = new IovArray();
   private final IntSupplier selectNowSupplier = new IntSupplier() {
      public int get() throws Exception {
         return KQueueEventLoop.this.kqueueWaitNow();
      }
   };
   private final Callable<Integer> pendingTasksCallable = new Callable<Integer>() {
      public Integer call() throws Exception {
         return KQueueEventLoop.super.pendingTasks();
      }
   };
   private volatile int wakenUp;
   private volatile int ioRatio = 50;
   static final long MAX_SCHEDULED_DAYS = 1095L;

   KQueueEventLoop(EventLoopGroup var1, Executor var2, int var3, SelectStrategy var4, RejectedExecutionHandler var5) {
      super(var1, var2, false, DEFAULT_MAX_PENDING_TASKS, var5);
      this.selectStrategy = (SelectStrategy)ObjectUtil.checkNotNull(var4, "strategy");
      this.kqueueFd = Native.newKQueue();
      if (var3 == 0) {
         this.allowGrowing = true;
         var3 = 4096;
      } else {
         this.allowGrowing = false;
      }

      this.changeList = new KQueueEventArray(var3);
      this.eventList = new KQueueEventArray(var3);
      this.jniChannelPointers = new NativeLongArray(4096);
      int var6 = Native.keventAddUserEvent(this.kqueueFd.intValue(), 0);
      if (var6 < 0) {
         this.cleanup();
         throw new IllegalStateException("kevent failed to add user event with errno: " + -var6);
      }
   }

   void evSet(AbstractKQueueChannel var1, short var2, short var3, int var4) {
      this.changeList.evSet(var1, var2, var3, var4);
   }

   void remove(AbstractKQueueChannel var1) throws IOException {
      assert this.inEventLoop();

      if (var1.jniSelfPtr != 0L) {
         this.jniChannelPointers.add(var1.jniSelfPtr);
         var1.jniSelfPtr = 0L;
      }
   }

   IovArray cleanArray() {
      this.iovArray.clear();
      return this.iovArray;
   }

   protected void wakeup(boolean var1) {
      if (!var1 && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
         this.wakeup();
      }

   }

   private void wakeup() {
      Native.keventTriggerUserEvent(this.kqueueFd.intValue(), 0);
   }

   private int kqueueWait(boolean var1) throws IOException {
      if (var1 && this.hasTasks()) {
         return this.kqueueWaitNow();
      } else {
         long var2 = this.delayNanos(System.nanoTime());
         int var4 = (int)Math.min(var2 / 1000000000L, 2147483647L);
         return this.kqueueWait(var4, (int)Math.min(var2 - (long)var4 * 1000000000L, 2147483647L));
      }
   }

   private int kqueueWaitNow() throws IOException {
      return this.kqueueWait(0, 0);
   }

   private int kqueueWait(int var1, int var2) throws IOException {
      this.deleteJniChannelPointers();
      int var3 = Native.keventWait(this.kqueueFd.intValue(), this.changeList, this.eventList, var1, var2);
      this.changeList.clear();
      return var3;
   }

   private void deleteJniChannelPointers() {
      if (!this.jniChannelPointers.isEmpty()) {
         KQueueEventArray.deleteGlobalRefs(this.jniChannelPointers.memoryAddress(), this.jniChannelPointers.memoryAddressEnd());
         this.jniChannelPointers.clear();
      }

   }

   private void processReady(int var1) {
      for(int var2 = 0; var2 < var1; ++var2) {
         short var3 = this.eventList.filter(var2);
         short var4 = this.eventList.flags(var2);
         if (var3 != Native.EVFILT_USER && (var4 & Native.EV_ERROR) == 0) {
            AbstractKQueueChannel var5 = this.eventList.channel(var2);
            if (var5 == null) {
               logger.warn("events[{}]=[{}, {}] had no channel!", var2, this.eventList.fd(var2), var3);
            } else {
               AbstractKQueueChannel.AbstractKQueueUnsafe var6 = (AbstractKQueueChannel.AbstractKQueueUnsafe)var5.unsafe();
               if (var3 == Native.EVFILT_WRITE) {
                  var6.writeReady();
               } else if (var3 == Native.EVFILT_READ) {
                  var6.readReady(this.eventList.data(var2));
               } else if (var3 == Native.EVFILT_SOCK && (this.eventList.fflags(var2) & Native.NOTE_RDHUP) != 0) {
                  var6.readEOF();
               }

               if ((var4 & Native.EV_EOF) != 0) {
                  var6.readEOF();
               }
            }
         } else {
            assert var3 != Native.EVFILT_USER || var3 == Native.EVFILT_USER && this.eventList.fd(var2) == 0;
         }
      }

   }

   protected void run() {
      while(true) {
         while(true) {
            try {
               int var1 = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
               switch(var1) {
               case -2:
                  continue;
               case -1:
                  var1 = this.kqueueWait(WAKEN_UP_UPDATER.getAndSet(this, 0) == 1);
                  if (this.wakenUp == 1) {
                     this.wakeup();
                  }
               default:
                  int var2 = this.ioRatio;
                  if (var2 == 100) {
                     try {
                        if (var1 > 0) {
                           this.processReady(var1);
                        }
                     } finally {
                        this.runAllTasks();
                     }
                  } else {
                     long var3 = System.nanoTime();
                     boolean var14 = false;

                     try {
                        var14 = true;
                        if (var1 > 0) {
                           this.processReady(var1);
                           var14 = false;
                        } else {
                           var14 = false;
                        }
                     } finally {
                        if (var14) {
                           long var8 = System.nanoTime() - var3;
                           this.runAllTasks(var8 * (long)(100 - var2) / (long)var2);
                        }
                     }

                     long var5 = System.nanoTime() - var3;
                     this.runAllTasks(var5 * (long)(100 - var2) / (long)var2);
                  }

                  if (this.allowGrowing && var1 == this.eventList.capacity()) {
                     this.eventList.realloc(false);
                  }
               }
            } catch (Throwable var22) {
               handleLoopException(var22);
            }

            try {
               if (this.isShuttingDown()) {
                  this.closeAll();
                  if (this.confirmShutdown()) {
                     return;
                  }
               }
            } catch (Throwable var19) {
               handleLoopException(var19);
            }
         }
      }
   }

   protected Queue<Runnable> newTaskQueue(int var1) {
      return var1 == 2147483647 ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(var1);
   }

   public int pendingTasks() {
      return this.inEventLoop() ? super.pendingTasks() : (Integer)this.submit(this.pendingTasksCallable).syncUninterruptibly().getNow();
   }

   public int getIoRatio() {
      return this.ioRatio;
   }

   public void setIoRatio(int var1) {
      if (var1 > 0 && var1 <= 100) {
         this.ioRatio = var1;
      } else {
         throw new IllegalArgumentException("ioRatio: " + var1 + " (expected: 0 < ioRatio <= 100)");
      }
   }

   protected void cleanup() {
      try {
         this.kqueueFd.close();
      } catch (IOException var5) {
         logger.warn("Failed to close the kqueue fd.", (Throwable)var5);
      } finally {
         this.deleteJniChannelPointers();
         this.jniChannelPointers.free();
         this.changeList.free();
         this.eventList.free();
      }

   }

   private void closeAll() {
      try {
         this.kqueueWaitNow();
      } catch (IOException var2) {
      }

   }

   private static void handleLoopException(Throwable var0) {
      logger.warn("Unexpected exception in the selector loop.", var0);

      try {
         Thread.sleep(1000L);
      } catch (InterruptedException var2) {
      }

   }

   protected void validateScheduled(long var1, TimeUnit var3) {
      long var4 = var3.toDays(var1);
      if (var4 > 1095L) {
         throw new IllegalArgumentException("days: " + var4 + " (expected: < " + 1095L + ')');
      }
   }

   static {
      KQueue.ensureAvailability();
   }
}
