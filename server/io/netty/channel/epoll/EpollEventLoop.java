package io.netty.channel.epoll;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class EpollEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
   private static final AtomicIntegerFieldUpdater<EpollEventLoop> WAKEN_UP_UPDATER = AtomicIntegerFieldUpdater.newUpdater(EpollEventLoop.class, "wakenUp");
   private final FileDescriptor epollFd;
   private final FileDescriptor eventFd;
   private final FileDescriptor timerFd;
   private final IntObjectMap<AbstractEpollChannel> channels = new IntObjectHashMap(4096);
   private final boolean allowGrowing;
   private final EpollEventArray events;
   private final IovArray iovArray = new IovArray();
   private final SelectStrategy selectStrategy;
   private final IntSupplier selectNowSupplier = new IntSupplier() {
      public int get() throws Exception {
         return EpollEventLoop.this.epollWaitNow();
      }
   };
   private final Callable<Integer> pendingTasksCallable = new Callable<Integer>() {
      public Integer call() throws Exception {
         return EpollEventLoop.super.pendingTasks();
      }
   };
   private volatile int wakenUp;
   private volatile int ioRatio = 50;
   static final long MAX_SCHEDULED_DAYS;

   EpollEventLoop(EventLoopGroup var1, Executor var2, int var3, SelectStrategy var4, RejectedExecutionHandler var5) {
      super(var1, var2, false, DEFAULT_MAX_PENDING_TASKS, var5);
      this.selectStrategy = (SelectStrategy)ObjectUtil.checkNotNull(var4, "strategy");
      if (var3 == 0) {
         this.allowGrowing = true;
         this.events = new EpollEventArray(4096);
      } else {
         this.allowGrowing = false;
         this.events = new EpollEventArray(var3);
      }

      boolean var6 = false;
      FileDescriptor var7 = null;
      FileDescriptor var8 = null;
      FileDescriptor var9 = null;

      try {
         this.epollFd = var7 = Native.newEpollCreate();
         this.eventFd = var8 = Native.newEventFd();

         try {
            Native.epollCtlAdd(var7.intValue(), var8.intValue(), Native.EPOLLIN);
         } catch (IOException var26) {
            throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", var26);
         }

         this.timerFd = var9 = Native.newTimerFd();

         try {
            Native.epollCtlAdd(var7.intValue(), var9.intValue(), Native.EPOLLIN | Native.EPOLLET);
         } catch (IOException var25) {
            throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", var25);
         }

         var6 = true;
      } finally {
         if (!var6) {
            if (var7 != null) {
               try {
                  var7.close();
               } catch (Exception var24) {
               }
            }

            if (var8 != null) {
               try {
                  var8.close();
               } catch (Exception var23) {
               }
            }

            if (var9 != null) {
               try {
                  var9.close();
               } catch (Exception var22) {
               }
            }
         }

      }

   }

   IovArray cleanArray() {
      this.iovArray.clear();
      return this.iovArray;
   }

   protected void wakeup(boolean var1) {
      if (!var1 && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
         Native.eventFdWrite(this.eventFd.intValue(), 1L);
      }

   }

   void add(AbstractEpollChannel var1) throws IOException {
      assert this.inEventLoop();

      int var2 = var1.socket.intValue();
      Native.epollCtlAdd(this.epollFd.intValue(), var2, var1.flags);
      this.channels.put(var2, var1);
   }

   void modify(AbstractEpollChannel var1) throws IOException {
      assert this.inEventLoop();

      Native.epollCtlMod(this.epollFd.intValue(), var1.socket.intValue(), var1.flags);
   }

   void remove(AbstractEpollChannel var1) throws IOException {
      assert this.inEventLoop();

      if (var1.isOpen()) {
         int var2 = var1.socket.intValue();
         if (this.channels.remove(var2) != null) {
            Native.epollCtlDel(this.epollFd.intValue(), var1.fd().intValue());
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

   private int epollWait(boolean var1) throws IOException {
      if (var1 && this.hasTasks()) {
         return this.epollWaitNow();
      } else {
         long var2 = this.delayNanos(System.nanoTime());
         int var4 = (int)Math.min(var2 / 1000000000L, 2147483647L);
         return Native.epollWait(this.epollFd, this.events, this.timerFd, var4, (int)Math.min(var2 - (long)var4 * 1000000000L, 2147483647L));
      }
   }

   private int epollWaitNow() throws IOException {
      return Native.epollWait(this.epollFd, this.events, this.timerFd, 0, 0);
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
                  var1 = this.epollWait(WAKEN_UP_UPDATER.getAndSet(this, 0) == 1);
                  if (this.wakenUp == 1) {
                     Native.eventFdWrite(this.eventFd.intValue(), 1L);
                  }
               default:
                  int var2 = this.ioRatio;
                  if (var2 == 100) {
                     try {
                        if (var1 > 0) {
                           this.processReady(this.events, var1);
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
                           this.processReady(this.events, var1);
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

                  if (this.allowGrowing && var1 == this.events.length()) {
                     this.events.increase();
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

   private static void handleLoopException(Throwable var0) {
      logger.warn("Unexpected exception in the selector loop.", var0);

      try {
         Thread.sleep(1000L);
      } catch (InterruptedException var2) {
      }

   }

   private void closeAll() {
      try {
         this.epollWaitNow();
      } catch (IOException var4) {
      }

      ArrayList var1 = new ArrayList(this.channels.size());
      Iterator var2 = this.channels.values().iterator();

      AbstractEpollChannel var3;
      while(var2.hasNext()) {
         var3 = (AbstractEpollChannel)var2.next();
         var1.add(var3);
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         var3 = (AbstractEpollChannel)var2.next();
         var3.unsafe().close(var3.unsafe().voidPromise());
      }

   }

   private void processReady(EpollEventArray var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.fd(var3);
         if (var4 == this.eventFd.intValue()) {
            Native.eventFdRead(var4);
         } else if (var4 == this.timerFd.intValue()) {
            Native.timerFdRead(var4);
         } else {
            long var5 = (long)var1.events(var3);
            AbstractEpollChannel var7 = (AbstractEpollChannel)this.channels.get(var4);
            if (var7 != null) {
               AbstractEpollChannel.AbstractEpollUnsafe var8 = (AbstractEpollChannel.AbstractEpollUnsafe)var7.unsafe();
               if ((var5 & (long)(Native.EPOLLERR | Native.EPOLLOUT)) != 0L) {
                  var8.epollOutReady();
               }

               if ((var5 & (long)(Native.EPOLLERR | Native.EPOLLIN)) != 0L) {
                  var8.epollInReady();
               }

               if ((var5 & (long)Native.EPOLLRDHUP) != 0L) {
                  var8.epollRdHupReady();
               }
            } else {
               try {
                  Native.epollCtlDel(this.epollFd.intValue(), var4);
               } catch (IOException var9) {
               }
            }
         }
      }

   }

   protected void cleanup() {
      try {
         try {
            this.epollFd.close();
         } catch (IOException var9) {
            logger.warn("Failed to close the epoll fd.", (Throwable)var9);
         }

         try {
            this.eventFd.close();
         } catch (IOException var8) {
            logger.warn("Failed to close the event fd.", (Throwable)var8);
         }

         try {
            this.timerFd.close();
         } catch (IOException var7) {
            logger.warn("Failed to close the timer fd.", (Throwable)var7);
         }
      } finally {
         this.iovArray.release();
         this.events.free();
      }

   }

   protected void validateScheduled(long var1, TimeUnit var3) {
      long var4 = var3.toDays(var1);
      if (var4 > MAX_SCHEDULED_DAYS) {
         throw new IllegalArgumentException("days: " + var4 + " (expected: < " + MAX_SCHEDULED_DAYS + ')');
      }
   }

   static {
      Epoll.ensureAvailability();
      MAX_SCHEDULED_DAYS = TimeUnit.SECONDS.toDays(999999999L);
   }
}
