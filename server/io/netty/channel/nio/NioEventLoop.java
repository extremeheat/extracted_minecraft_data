package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.EventLoopException;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
   private static final int CLEANUP_INTERVAL = 256;
   private static final boolean DISABLE_KEYSET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
   private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
   private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
   private final IntSupplier selectNowSupplier = new IntSupplier() {
      public int get() throws Exception {
         return NioEventLoop.this.selectNow();
      }
   };
   private final Callable<Integer> pendingTasksCallable = new Callable<Integer>() {
      public Integer call() throws Exception {
         return NioEventLoop.super.pendingTasks();
      }
   };
   static final long MAX_SCHEDULED_DAYS = 1095L;
   private Selector selector;
   private Selector unwrappedSelector;
   private SelectedSelectionKeySet selectedKeys;
   private final SelectorProvider provider;
   private final AtomicBoolean wakenUp = new AtomicBoolean();
   private final SelectStrategy selectStrategy;
   private volatile int ioRatio = 50;
   private int cancelledKeys;
   private boolean needsToSelectAgain;

   NioEventLoop(NioEventLoopGroup var1, Executor var2, SelectorProvider var3, SelectStrategy var4, RejectedExecutionHandler var5) {
      super(var1, (Executor)var2, false, DEFAULT_MAX_PENDING_TASKS, var5);
      if (var3 == null) {
         throw new NullPointerException("selectorProvider");
      } else if (var4 == null) {
         throw new NullPointerException("selectStrategy");
      } else {
         this.provider = var3;
         NioEventLoop.SelectorTuple var6 = this.openSelector();
         this.selector = var6.selector;
         this.unwrappedSelector = var6.unwrappedSelector;
         this.selectStrategy = var4;
      }
   }

   private NioEventLoop.SelectorTuple openSelector() {
      final AbstractSelector var1;
      try {
         var1 = this.provider.openSelector();
      } catch (IOException var7) {
         throw new ChannelException("failed to open a new selector", var7);
      }

      if (DISABLE_KEYSET_OPTIMIZATION) {
         return new NioEventLoop.SelectorTuple(var1);
      } else {
         final SelectedSelectionKeySet var2 = new SelectedSelectionKeySet();
         Object var3 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               try {
                  return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
               } catch (Throwable var2) {
                  return var2;
               }
            }
         });
         if (var3 instanceof Class && ((Class)var3).isAssignableFrom(var1.getClass())) {
            final Class var8 = (Class)var3;
            Object var5 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  try {
                     Field var1x = var8.getDeclaredField("selectedKeys");
                     Field var2x = var8.getDeclaredField("publicSelectedKeys");
                     Throwable var3 = ReflectionUtil.trySetAccessible(var1x, true);
                     if (var3 != null) {
                        return var3;
                     } else {
                        var3 = ReflectionUtil.trySetAccessible(var2x, true);
                        if (var3 != null) {
                           return var3;
                        } else {
                           var1x.set(var1, var2);
                           var2x.set(var1, var2);
                           return null;
                        }
                     }
                  } catch (NoSuchFieldException var4) {
                     return var4;
                  } catch (IllegalAccessException var5) {
                     return var5;
                  }
               }
            });
            if (var5 instanceof Exception) {
               this.selectedKeys = null;
               Exception var6 = (Exception)var5;
               logger.trace("failed to instrument a special java.util.Set into: {}", var1, var6);
               return new NioEventLoop.SelectorTuple(var1);
            } else {
               this.selectedKeys = var2;
               logger.trace("instrumented a special java.util.Set into: {}", (Object)var1);
               return new NioEventLoop.SelectorTuple(var1, new SelectedSelectionKeySetSelector(var1, var2));
            }
         } else {
            if (var3 instanceof Throwable) {
               Throwable var4 = (Throwable)var3;
               logger.trace("failed to instrument a special java.util.Set into: {}", var1, var4);
            }

            return new NioEventLoop.SelectorTuple(var1);
         }
      }
   }

   public SelectorProvider selectorProvider() {
      return this.provider;
   }

   protected Queue<Runnable> newTaskQueue(int var1) {
      return var1 == 2147483647 ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(var1);
   }

   public int pendingTasks() {
      return this.inEventLoop() ? super.pendingTasks() : (Integer)this.submit(this.pendingTasksCallable).syncUninterruptibly().getNow();
   }

   public void register(SelectableChannel var1, int var2, NioTask<?> var3) {
      if (var1 == null) {
         throw new NullPointerException("ch");
      } else if (var2 == 0) {
         throw new IllegalArgumentException("interestOps must be non-zero.");
      } else if ((var2 & ~var1.validOps()) != 0) {
         throw new IllegalArgumentException("invalid interestOps: " + var2 + "(validOps: " + var1.validOps() + ')');
      } else if (var3 == null) {
         throw new NullPointerException("task");
      } else if (this.isShutdown()) {
         throw new IllegalStateException("event loop shut down");
      } else {
         try {
            var1.register(this.selector, var2, var3);
         } catch (Exception var5) {
            throw new EventLoopException("failed to register a channel", var5);
         }
      }
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

   public void rebuildSelector() {
      if (!this.inEventLoop()) {
         this.execute(new Runnable() {
            public void run() {
               NioEventLoop.this.rebuildSelector0();
            }
         });
      } else {
         this.rebuildSelector0();
      }
   }

   private void rebuildSelector0() {
      Selector var1 = this.selector;
      if (var1 != null) {
         NioEventLoop.SelectorTuple var2;
         try {
            var2 = this.openSelector();
         } catch (Exception var9) {
            logger.warn("Failed to create a new Selector.", (Throwable)var9);
            return;
         }

         int var3 = 0;
         Iterator var4 = var1.keys().iterator();

         while(var4.hasNext()) {
            SelectionKey var5 = (SelectionKey)var4.next();
            Object var6 = var5.attachment();

            try {
               if (var5.isValid() && var5.channel().keyFor(var2.unwrappedSelector) == null) {
                  int var7 = var5.interestOps();
                  var5.cancel();
                  SelectionKey var13 = var5.channel().register(var2.unwrappedSelector, var7, var6);
                  if (var6 instanceof AbstractNioChannel) {
                     ((AbstractNioChannel)var6).selectionKey = var13;
                  }

                  ++var3;
               }
            } catch (Exception var11) {
               logger.warn("Failed to re-register a Channel to the new Selector.", (Throwable)var11);
               if (var6 instanceof AbstractNioChannel) {
                  AbstractNioChannel var12 = (AbstractNioChannel)var6;
                  var12.unsafe().close(var12.unsafe().voidPromise());
               } else {
                  NioTask var8 = (NioTask)var6;
                  invokeChannelUnregistered(var8, var5, var11);
               }
            }
         }

         this.selector = var2.selector;
         this.unwrappedSelector = var2.unwrappedSelector;

         try {
            var1.close();
         } catch (Throwable var10) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to close the old Selector.", var10);
            }
         }

         logger.info("Migrated " + var3 + " channel(s) to the new Selector.");
      }
   }

   protected void run() {
      while(true) {
         while(true) {
            try {
               switch(this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks())) {
               case -2:
                  continue;
               case -1:
                  this.select(this.wakenUp.getAndSet(false));
                  if (this.wakenUp.get()) {
                     this.selector.wakeup();
                  }
               default:
                  this.cancelledKeys = 0;
                  this.needsToSelectAgain = false;
                  int var1 = this.ioRatio;
                  if (var1 == 100) {
                     try {
                        this.processSelectedKeys();
                     } finally {
                        this.runAllTasks();
                     }
                  } else {
                     long var2 = System.nanoTime();
                     boolean var13 = false;

                     try {
                        var13 = true;
                        this.processSelectedKeys();
                        var13 = false;
                     } finally {
                        if (var13) {
                           long var7 = System.nanoTime() - var2;
                           this.runAllTasks(var7 * (long)(100 - var1) / (long)var1);
                        }
                     }

                     long var4 = System.nanoTime() - var2;
                     this.runAllTasks(var4 * (long)(100 - var1) / (long)var1);
                  }
               }
            } catch (Throwable var21) {
               handleLoopException(var21);
            }

            try {
               if (this.isShuttingDown()) {
                  this.closeAll();
                  if (this.confirmShutdown()) {
                     return;
                  }
               }
            } catch (Throwable var18) {
               handleLoopException(var18);
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

   private void processSelectedKeys() {
      if (this.selectedKeys != null) {
         this.processSelectedKeysOptimized();
      } else {
         this.processSelectedKeysPlain(this.selector.selectedKeys());
      }

   }

   protected void cleanup() {
      try {
         this.selector.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a selector.", (Throwable)var2);
      }

   }

   void cancel(SelectionKey var1) {
      var1.cancel();
      ++this.cancelledKeys;
      if (this.cancelledKeys >= 256) {
         this.cancelledKeys = 0;
         this.needsToSelectAgain = true;
      }

   }

   protected Runnable pollTask() {
      Runnable var1 = super.pollTask();
      if (this.needsToSelectAgain) {
         this.selectAgain();
      }

      return var1;
   }

   private void processSelectedKeysPlain(Set<SelectionKey> var1) {
      if (!var1.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(true) {
            SelectionKey var3 = (SelectionKey)var2.next();
            Object var4 = var3.attachment();
            var2.remove();
            if (var4 instanceof AbstractNioChannel) {
               this.processSelectedKey(var3, (AbstractNioChannel)var4);
            } else {
               NioTask var5 = (NioTask)var4;
               processSelectedKey(var3, var5);
            }

            if (!var2.hasNext()) {
               break;
            }

            if (this.needsToSelectAgain) {
               this.selectAgain();
               var1 = this.selector.selectedKeys();
               if (var1.isEmpty()) {
                  break;
               }

               var2 = var1.iterator();
            }
         }

      }
   }

   private void processSelectedKeysOptimized() {
      for(int var1 = 0; var1 < this.selectedKeys.size; ++var1) {
         SelectionKey var2 = this.selectedKeys.keys[var1];
         this.selectedKeys.keys[var1] = null;
         Object var3 = var2.attachment();
         if (var3 instanceof AbstractNioChannel) {
            this.processSelectedKey(var2, (AbstractNioChannel)var3);
         } else {
            NioTask var4 = (NioTask)var3;
            processSelectedKey(var2, var4);
         }

         if (this.needsToSelectAgain) {
            this.selectedKeys.reset(var1 + 1);
            this.selectAgain();
            var1 = -1;
         }
      }

   }

   private void processSelectedKey(SelectionKey var1, AbstractNioChannel var2) {
      AbstractNioChannel.NioUnsafe var3 = var2.unsafe();
      if (!var1.isValid()) {
         NioEventLoop var8;
         try {
            var8 = var2.eventLoop();
         } catch (Throwable var6) {
            return;
         }

         if (var8 == this && var8 != null) {
            var3.close(var3.voidPromise());
         }
      } else {
         try {
            int var4 = var1.readyOps();
            if ((var4 & 8) != 0) {
               int var5 = var1.interestOps();
               var5 &= -9;
               var1.interestOps(var5);
               var3.finishConnect();
            }

            if ((var4 & 4) != 0) {
               var2.unsafe().forceFlush();
            }

            if ((var4 & 17) != 0 || var4 == 0) {
               var3.read();
            }
         } catch (CancelledKeyException var7) {
            var3.close(var3.voidPromise());
         }

      }
   }

   private static void processSelectedKey(SelectionKey var0, NioTask<SelectableChannel> var1) {
      byte var2 = 0;
      boolean var7 = false;

      label91: {
         try {
            var7 = true;
            var1.channelReady(var0.channel(), var0);
            var2 = 1;
            var7 = false;
            break label91;
         } catch (Exception var8) {
            var0.cancel();
            invokeChannelUnregistered(var1, var0, var8);
            var2 = 2;
            var7 = false;
         } finally {
            if (var7) {
               switch(var2) {
               case 0:
                  var0.cancel();
                  invokeChannelUnregistered(var1, var0, (Throwable)null);
                  break;
               case 1:
                  if (!var0.isValid()) {
                     invokeChannelUnregistered(var1, var0, (Throwable)null);
                  }
               }

            }
         }

         switch(var2) {
         case 0:
            var0.cancel();
            invokeChannelUnregistered(var1, var0, (Throwable)null);
            return;
         case 1:
            if (!var0.isValid()) {
               invokeChannelUnregistered(var1, var0, (Throwable)null);
            }

            return;
         default:
            return;
         }
      }

      switch(var2) {
      case 0:
         var0.cancel();
         invokeChannelUnregistered(var1, var0, (Throwable)null);
         break;
      case 1:
         if (!var0.isValid()) {
            invokeChannelUnregistered(var1, var0, (Throwable)null);
         }
      }

   }

   private void closeAll() {
      this.selectAgain();
      Set var1 = this.selector.keys();
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         SelectionKey var4 = (SelectionKey)var3.next();
         Object var5 = var4.attachment();
         if (var5 instanceof AbstractNioChannel) {
            var2.add((AbstractNioChannel)var5);
         } else {
            var4.cancel();
            NioTask var6 = (NioTask)var5;
            invokeChannelUnregistered(var6, var4, (Throwable)null);
         }
      }

      var3 = var2.iterator();

      while(var3.hasNext()) {
         AbstractNioChannel var7 = (AbstractNioChannel)var3.next();
         var7.unsafe().close(var7.unsafe().voidPromise());
      }

   }

   private static void invokeChannelUnregistered(NioTask<SelectableChannel> var0, SelectionKey var1, Throwable var2) {
      try {
         var0.channelUnregistered(var1.channel(), var2);
      } catch (Exception var4) {
         logger.warn("Unexpected exception while running NioTask.channelUnregistered()", (Throwable)var4);
      }

   }

   protected void wakeup(boolean var1) {
      if (!var1 && this.wakenUp.compareAndSet(false, true)) {
         this.selector.wakeup();
      }

   }

   Selector unwrappedSelector() {
      return this.unwrappedSelector;
   }

   int selectNow() throws IOException {
      int var1;
      try {
         var1 = this.selector.selectNow();
      } finally {
         if (this.wakenUp.get()) {
            this.selector.wakeup();
         }

      }

      return var1;
   }

   private void select(boolean var1) throws IOException {
      Selector var2 = this.selector;

      try {
         int var3 = 0;
         long var4 = System.nanoTime();
         long var6 = var4 + this.delayNanos(var4);

         while(true) {
            long var8 = (var6 - var4 + 500000L) / 1000000L;
            if (var8 <= 0L) {
               if (var3 == 0) {
                  var2.selectNow();
                  var3 = 1;
               }
               break;
            }

            if (this.hasTasks() && this.wakenUp.compareAndSet(false, true)) {
               var2.selectNow();
               var3 = 1;
               break;
            }

            int var10 = var2.select(var8);
            ++var3;
            if (var10 != 0 || var1 || this.wakenUp.get() || this.hasTasks() || this.hasScheduledTasks()) {
               break;
            }

            if (Thread.interrupted()) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
               }

               var3 = 1;
               break;
            }

            long var11 = System.nanoTime();
            if (var11 - TimeUnit.MILLISECONDS.toNanos(var8) >= var4) {
               var3 = 1;
            } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && var3 >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
               logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", var3, var2);
               this.rebuildSelector();
               var2 = this.selector;
               var2.selectNow();
               var3 = 1;
               break;
            }

            var4 = var11;
         }

         if (var3 > 3 && logger.isDebugEnabled()) {
            logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", var3 - 1, var2);
         }
      } catch (CancelledKeyException var13) {
         if (logger.isDebugEnabled()) {
            logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", var2, var13);
         }
      }

   }

   private void selectAgain() {
      this.needsToSelectAgain = false;

      try {
         this.selector.selectNow();
      } catch (Throwable var2) {
         logger.warn("Failed to update SelectionKeys.", var2);
      }

   }

   protected void validateScheduled(long var1, TimeUnit var3) {
      long var4 = var3.toDays(var1);
      if (var4 > 1095L) {
         throw new IllegalArgumentException("days: " + var4 + " (expected: < " + 1095L + ')');
      }
   }

   static {
      String var0 = "sun.nio.ch.bugLevel";
      String var1 = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
      if (var1 == null) {
         try {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  System.setProperty("sun.nio.ch.bugLevel", "");
                  return null;
               }
            });
         } catch (SecurityException var3) {
            logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", (Throwable)var3);
         }
      }

      int var2 = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
      if (var2 < 3) {
         var2 = 0;
      }

      SELECTOR_AUTO_REBUILD_THRESHOLD = var2;
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object)DISABLE_KEYSET_OPTIMIZATION);
         logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)SELECTOR_AUTO_REBUILD_THRESHOLD);
      }

   }

   private static final class SelectorTuple {
      final Selector unwrappedSelector;
      final Selector selector;

      SelectorTuple(Selector var1) {
         super();
         this.unwrappedSelector = var1;
         this.selector = var1;
      }

      SelectorTuple(Selector var1, Selector var2) {
         super();
         this.unwrappedSelector = var1;
         this.selector = var2;
      }
   }
}
