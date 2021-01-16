package io.netty.util.concurrent;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {
   private final EventExecutor[] children;
   private final Set<EventExecutor> readonlyChildren;
   private final AtomicInteger terminatedChildren;
   private final Promise<?> terminationFuture;
   private final EventExecutorChooserFactory.EventExecutorChooser chooser;

   protected MultithreadEventExecutorGroup(int var1, ThreadFactory var2, Object... var3) {
      this(var1, (Executor)(var2 == null ? null : new ThreadPerTaskExecutor(var2)), var3);
   }

   protected MultithreadEventExecutorGroup(int var1, Executor var2, Object... var3) {
      this(var1, var2, DefaultEventExecutorChooserFactory.INSTANCE, var3);
   }

   protected MultithreadEventExecutorGroup(int var1, Executor var2, EventExecutorChooserFactory var3, Object... var4) {
      super();
      this.terminatedChildren = new AtomicInteger();
      this.terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
      if (var1 <= 0) {
         throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", var1));
      } else {
         if (var2 == null) {
            var2 = new ThreadPerTaskExecutor(this.newDefaultThreadFactory());
         }

         this.children = new EventExecutor[var1];

         int var7;
         for(int var5 = 0; var5 < var1; ++var5) {
            boolean var6 = false;
            boolean var18 = false;

            try {
               var18 = true;
               this.children[var5] = this.newChild((Executor)var2, var4);
               var6 = true;
               var18 = false;
            } catch (Exception var19) {
               throw new IllegalStateException("failed to create a child event loop", var19);
            } finally {
               if (var18) {
                  if (!var6) {
                     int var11;
                     for(var11 = 0; var11 < var5; ++var11) {
                        this.children[var11].shutdownGracefully();
                     }

                     for(var11 = 0; var11 < var5; ++var11) {
                        EventExecutor var12 = this.children[var11];

                        try {
                           while(!var12.isTerminated()) {
                              var12.awaitTermination(2147483647L, TimeUnit.SECONDS);
                           }
                        } catch (InterruptedException var20) {
                           Thread.currentThread().interrupt();
                           break;
                        }
                     }
                  }

               }
            }

            if (!var6) {
               for(var7 = 0; var7 < var5; ++var7) {
                  this.children[var7].shutdownGracefully();
               }

               for(var7 = 0; var7 < var5; ++var7) {
                  EventExecutor var8 = this.children[var7];

                  try {
                     while(!var8.isTerminated()) {
                        var8.awaitTermination(2147483647L, TimeUnit.SECONDS);
                     }
                  } catch (InterruptedException var22) {
                     Thread.currentThread().interrupt();
                     break;
                  }
               }
            }
         }

         this.chooser = var3.newChooser(this.children);
         FutureListener var23 = new FutureListener<Object>() {
            public void operationComplete(Future<Object> var1) throws Exception {
               if (MultithreadEventExecutorGroup.this.terminatedChildren.incrementAndGet() == MultithreadEventExecutorGroup.this.children.length) {
                  MultithreadEventExecutorGroup.this.terminationFuture.setSuccess((Object)null);
               }

            }
         };
         EventExecutor[] var24 = this.children;
         var7 = var24.length;

         for(int var26 = 0; var26 < var7; ++var26) {
            EventExecutor var9 = var24[var26];
            var9.terminationFuture().addListener(var23);
         }

         LinkedHashSet var25 = new LinkedHashSet(this.children.length);
         Collections.addAll(var25, this.children);
         this.readonlyChildren = Collections.unmodifiableSet(var25);
      }
   }

   protected ThreadFactory newDefaultThreadFactory() {
      return new DefaultThreadFactory(this.getClass());
   }

   public EventExecutor next() {
      return this.chooser.next();
   }

   public Iterator<EventExecutor> iterator() {
      return this.readonlyChildren.iterator();
   }

   public final int executorCount() {
      return this.children.length;
   }

   protected abstract EventExecutor newChild(Executor var1, Object... var2) throws Exception;

   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      EventExecutor[] var6 = this.children;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EventExecutor var9 = var6[var8];
         var9.shutdownGracefully(var1, var3, var5);
      }

      return this.terminationFuture();
   }

   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      EventExecutor[] var1 = this.children;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EventExecutor var4 = var1[var3];
         var4.shutdown();
      }

   }

   public boolean isShuttingDown() {
      EventExecutor[] var1 = this.children;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EventExecutor var4 = var1[var3];
         if (!var4.isShuttingDown()) {
            return false;
         }
      }

      return true;
   }

   public boolean isShutdown() {
      EventExecutor[] var1 = this.children;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EventExecutor var4 = var1[var3];
         if (!var4.isShutdown()) {
            return false;
         }
      }

      return true;
   }

   public boolean isTerminated() {
      EventExecutor[] var1 = this.children;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EventExecutor var4 = var1[var3];
         if (!var4.isTerminated()) {
            return false;
         }
      }

      return true;
   }

   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = System.nanoTime() + var3.toNanos(var1);
      EventExecutor[] var6 = this.children;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EventExecutor var9 = var6[var8];

         long var10;
         do {
            var10 = var4 - System.nanoTime();
            if (var10 <= 0L) {
               return this.isTerminated();
            }
         } while(!var9.awaitTermination(var10, TimeUnit.NANOSECONDS));
      }

      return this.isTerminated();
   }
}
