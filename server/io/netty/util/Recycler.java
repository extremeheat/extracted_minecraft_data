package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectCleaner;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Recycler<T> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
   private static final Recycler.Handle NOOP_HANDLE = new Recycler.Handle() {
      public void recycle(Object var1) {
      }
   };
   private static final AtomicInteger ID_GENERATOR = new AtomicInteger(-2147483648);
   private static final int OWN_THREAD_ID;
   private static final int DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD = 4096;
   private static final int DEFAULT_MAX_CAPACITY_PER_THREAD;
   private static final int INITIAL_CAPACITY;
   private static final int MAX_SHARED_CAPACITY_FACTOR;
   private static final int MAX_DELAYED_QUEUES_PER_THREAD;
   private static final int LINK_CAPACITY;
   private static final int RATIO;
   private final int maxCapacityPerThread;
   private final int maxSharedCapacityFactor;
   private final int ratioMask;
   private final int maxDelayedQueuesPerThread;
   private final FastThreadLocal<Recycler.Stack<T>> threadLocal;
   private static final FastThreadLocal<Map<Recycler.Stack<?>, Recycler.WeakOrderQueue>> DELAYED_RECYCLED;

   protected Recycler() {
      this(DEFAULT_MAX_CAPACITY_PER_THREAD);
   }

   protected Recycler(int var1) {
      this(var1, MAX_SHARED_CAPACITY_FACTOR);
   }

   protected Recycler(int var1, int var2) {
      this(var1, var2, RATIO, MAX_DELAYED_QUEUES_PER_THREAD);
   }

   protected Recycler(int var1, int var2, int var3, int var4) {
      super();
      this.threadLocal = new FastThreadLocal<Recycler.Stack<T>>() {
         protected Recycler.Stack<T> initialValue() {
            return new Recycler.Stack(Recycler.this, Thread.currentThread(), Recycler.this.maxCapacityPerThread, Recycler.this.maxSharedCapacityFactor, Recycler.this.ratioMask, Recycler.this.maxDelayedQueuesPerThread);
         }

         protected void onRemoval(Recycler.Stack<T> var1) {
            if (var1.threadRef.get() == Thread.currentThread() && Recycler.DELAYED_RECYCLED.isSet()) {
               ((Map)Recycler.DELAYED_RECYCLED.get()).remove(var1);
            }

         }
      };
      this.ratioMask = MathUtil.safeFindNextPositivePowerOfTwo(var3) - 1;
      if (var1 <= 0) {
         this.maxCapacityPerThread = 0;
         this.maxSharedCapacityFactor = 1;
         this.maxDelayedQueuesPerThread = 0;
      } else {
         this.maxCapacityPerThread = var1;
         this.maxSharedCapacityFactor = Math.max(1, var2);
         this.maxDelayedQueuesPerThread = Math.max(0, var4);
      }

   }

   public final T get() {
      if (this.maxCapacityPerThread == 0) {
         return this.newObject(NOOP_HANDLE);
      } else {
         Recycler.Stack var1 = (Recycler.Stack)this.threadLocal.get();
         Recycler.DefaultHandle var2 = var1.pop();
         if (var2 == null) {
            var2 = var1.newHandle();
            var2.value = this.newObject(var2);
         }

         return var2.value;
      }
   }

   /** @deprecated */
   @Deprecated
   public final boolean recycle(T var1, Recycler.Handle<T> var2) {
      if (var2 == NOOP_HANDLE) {
         return false;
      } else {
         Recycler.DefaultHandle var3 = (Recycler.DefaultHandle)var2;
         if (var3.stack.parent != this) {
            return false;
         } else {
            var3.recycle(var1);
            return true;
         }
      }
   }

   final int threadLocalCapacity() {
      return ((Recycler.Stack)this.threadLocal.get()).elements.length;
   }

   final int threadLocalSize() {
      return ((Recycler.Stack)this.threadLocal.get()).size;
   }

   protected abstract T newObject(Recycler.Handle<T> var1);

   static {
      OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
      int var0 = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacityPerThread", SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity", 4096));
      if (var0 < 0) {
         var0 = 4096;
      }

      DEFAULT_MAX_CAPACITY_PER_THREAD = var0;
      MAX_SHARED_CAPACITY_FACTOR = Math.max(2, SystemPropertyUtil.getInt("io.netty.recycler.maxSharedCapacityFactor", 2));
      MAX_DELAYED_QUEUES_PER_THREAD = Math.max(0, SystemPropertyUtil.getInt("io.netty.recycler.maxDelayedQueuesPerThread", NettyRuntime.availableProcessors() * 2));
      LINK_CAPACITY = MathUtil.safeFindNextPositivePowerOfTwo(Math.max(SystemPropertyUtil.getInt("io.netty.recycler.linkCapacity", 16), 16));
      RATIO = MathUtil.safeFindNextPositivePowerOfTwo(SystemPropertyUtil.getInt("io.netty.recycler.ratio", 8));
      if (logger.isDebugEnabled()) {
         if (DEFAULT_MAX_CAPACITY_PER_THREAD == 0) {
            logger.debug("-Dio.netty.recycler.maxCapacityPerThread: disabled");
            logger.debug("-Dio.netty.recycler.maxSharedCapacityFactor: disabled");
            logger.debug("-Dio.netty.recycler.linkCapacity: disabled");
            logger.debug("-Dio.netty.recycler.ratio: disabled");
         } else {
            logger.debug("-Dio.netty.recycler.maxCapacityPerThread: {}", (Object)DEFAULT_MAX_CAPACITY_PER_THREAD);
            logger.debug("-Dio.netty.recycler.maxSharedCapacityFactor: {}", (Object)MAX_SHARED_CAPACITY_FACTOR);
            logger.debug("-Dio.netty.recycler.linkCapacity: {}", (Object)LINK_CAPACITY);
            logger.debug("-Dio.netty.recycler.ratio: {}", (Object)RATIO);
         }
      }

      INITIAL_CAPACITY = Math.min(DEFAULT_MAX_CAPACITY_PER_THREAD, 256);
      DELAYED_RECYCLED = new FastThreadLocal<Map<Recycler.Stack<?>, Recycler.WeakOrderQueue>>() {
         protected Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> initialValue() {
            return new WeakHashMap();
         }
      };
   }

   static final class Stack<T> {
      final Recycler<T> parent;
      final WeakReference<Thread> threadRef;
      final AtomicInteger availableSharedCapacity;
      final int maxDelayedQueues;
      private final int maxCapacity;
      private final int ratioMask;
      private Recycler.DefaultHandle<?>[] elements;
      private int size;
      private int handleRecycleCount = -1;
      private Recycler.WeakOrderQueue cursor;
      private Recycler.WeakOrderQueue prev;
      private volatile Recycler.WeakOrderQueue head;

      Stack(Recycler<T> var1, Thread var2, int var3, int var4, int var5, int var6) {
         super();
         this.parent = var1;
         this.threadRef = new WeakReference(var2);
         this.maxCapacity = var3;
         this.availableSharedCapacity = new AtomicInteger(Math.max(var3 / var4, Recycler.LINK_CAPACITY));
         this.elements = new Recycler.DefaultHandle[Math.min(Recycler.INITIAL_CAPACITY, var3)];
         this.ratioMask = var5;
         this.maxDelayedQueues = var6;
      }

      synchronized void setHead(Recycler.WeakOrderQueue var1) {
         var1.setNext(this.head);
         this.head = var1;
      }

      int increaseCapacity(int var1) {
         int var2 = this.elements.length;
         int var3 = this.maxCapacity;

         do {
            var2 <<= 1;
         } while(var2 < var1 && var2 < var3);

         var2 = Math.min(var2, var3);
         if (var2 != this.elements.length) {
            this.elements = (Recycler.DefaultHandle[])Arrays.copyOf(this.elements, var2);
         }

         return var2;
      }

      Recycler.DefaultHandle<T> pop() {
         int var1 = this.size;
         if (var1 == 0) {
            if (!this.scavenge()) {
               return null;
            }

            var1 = this.size;
         }

         --var1;
         Recycler.DefaultHandle var2 = this.elements[var1];
         this.elements[var1] = null;
         if (var2.lastRecycledId != var2.recycleId) {
            throw new IllegalStateException("recycled multiple times");
         } else {
            var2.recycleId = 0;
            var2.lastRecycledId = 0;
            this.size = var1;
            return var2;
         }
      }

      boolean scavenge() {
         if (this.scavengeSome()) {
            return true;
         } else {
            this.prev = null;
            this.cursor = this.head;
            return false;
         }
      }

      boolean scavengeSome() {
         Recycler.WeakOrderQueue var2 = this.cursor;
         Recycler.WeakOrderQueue var1;
         if (var2 == null) {
            var1 = null;
            var2 = this.head;
            if (var2 == null) {
               return false;
            }
         } else {
            var1 = this.prev;
         }

         boolean var3 = false;

         Recycler.WeakOrderQueue var4;
         do {
            if (var2.transfer(this)) {
               var3 = true;
               break;
            }

            var4 = var2.next;
            if (var2.owner.get() == null) {
               if (var2.hasFinalData()) {
                  while(var2.transfer(this)) {
                     var3 = true;
                  }
               }

               if (var1 != null) {
                  var1.setNext(var4);
               }
            } else {
               var1 = var2;
            }

            var2 = var4;
         } while(var4 != null && !var3);

         this.prev = var1;
         this.cursor = var2;
         return var3;
      }

      void push(Recycler.DefaultHandle<?> var1) {
         Thread var2 = Thread.currentThread();
         if (this.threadRef.get() == var2) {
            this.pushNow(var1);
         } else {
            this.pushLater(var1, var2);
         }

      }

      private void pushNow(Recycler.DefaultHandle<?> var1) {
         if ((var1.recycleId | var1.lastRecycledId) != 0) {
            throw new IllegalStateException("recycled already");
         } else {
            var1.recycleId = var1.lastRecycledId = Recycler.OWN_THREAD_ID;
            int var2 = this.size;
            if (var2 < this.maxCapacity && !this.dropHandle(var1)) {
               if (var2 == this.elements.length) {
                  this.elements = (Recycler.DefaultHandle[])Arrays.copyOf(this.elements, Math.min(var2 << 1, this.maxCapacity));
               }

               this.elements[var2] = var1;
               this.size = var2 + 1;
            }
         }
      }

      private void pushLater(Recycler.DefaultHandle<?> var1, Thread var2) {
         Map var3 = (Map)Recycler.DELAYED_RECYCLED.get();
         Recycler.WeakOrderQueue var4 = (Recycler.WeakOrderQueue)var3.get(this);
         if (var4 == null) {
            if (var3.size() >= this.maxDelayedQueues) {
               var3.put(this, Recycler.WeakOrderQueue.DUMMY);
               return;
            }

            if ((var4 = Recycler.WeakOrderQueue.allocate(this, var2)) == null) {
               return;
            }

            var3.put(this, var4);
         } else if (var4 == Recycler.WeakOrderQueue.DUMMY) {
            return;
         }

         var4.add(var1);
      }

      boolean dropHandle(Recycler.DefaultHandle<?> var1) {
         if (!var1.hasBeenRecycled) {
            if ((++this.handleRecycleCount & this.ratioMask) != 0) {
               return true;
            }

            var1.hasBeenRecycled = true;
         }

         return false;
      }

      Recycler.DefaultHandle<T> newHandle() {
         return new Recycler.DefaultHandle(this);
      }
   }

   private static final class WeakOrderQueue {
      static final Recycler.WeakOrderQueue DUMMY = new Recycler.WeakOrderQueue();
      private final Recycler.WeakOrderQueue.Head head;
      private Recycler.WeakOrderQueue.Link tail;
      private Recycler.WeakOrderQueue next;
      private final WeakReference<Thread> owner;
      private final int id;

      private WeakOrderQueue() {
         super();
         this.id = Recycler.ID_GENERATOR.getAndIncrement();
         this.owner = null;
         this.head = new Recycler.WeakOrderQueue.Head((AtomicInteger)null);
      }

      private WeakOrderQueue(Recycler.Stack<?> var1, Thread var2) {
         super();
         this.id = Recycler.ID_GENERATOR.getAndIncrement();
         this.tail = new Recycler.WeakOrderQueue.Link();
         this.head = new Recycler.WeakOrderQueue.Head(var1.availableSharedCapacity);
         this.head.link = this.tail;
         this.owner = new WeakReference(var2);
      }

      static Recycler.WeakOrderQueue newQueue(Recycler.Stack<?> var0, Thread var1) {
         Recycler.WeakOrderQueue var2 = new Recycler.WeakOrderQueue(var0, var1);
         var0.setHead(var2);
         Recycler.WeakOrderQueue.Head var3 = var2.head;
         ObjectCleaner.register(var2, var3);
         return var2;
      }

      private void setNext(Recycler.WeakOrderQueue var1) {
         assert var1 != this;

         this.next = var1;
      }

      static Recycler.WeakOrderQueue allocate(Recycler.Stack<?> var0, Thread var1) {
         return Recycler.WeakOrderQueue.Head.reserveSpace(var0.availableSharedCapacity, Recycler.LINK_CAPACITY) ? newQueue(var0, var1) : null;
      }

      void add(Recycler.DefaultHandle<?> var1) {
         var1.lastRecycledId = this.id;
         Recycler.WeakOrderQueue.Link var2 = this.tail;
         int var3;
         if ((var3 = var2.get()) == Recycler.LINK_CAPACITY) {
            if (!this.head.reserveSpace(Recycler.LINK_CAPACITY)) {
               return;
            }

            this.tail = var2 = var2.next = new Recycler.WeakOrderQueue.Link();
            var3 = var2.get();
         }

         var2.elements[var3] = var1;
         var1.stack = null;
         var2.lazySet(var3 + 1);
      }

      boolean hasFinalData() {
         return this.tail.readIndex != this.tail.get();
      }

      boolean transfer(Recycler.Stack<?> var1) {
         Recycler.WeakOrderQueue.Link var2 = this.head.link;
         if (var2 == null) {
            return false;
         } else {
            if (var2.readIndex == Recycler.LINK_CAPACITY) {
               if (var2.next == null) {
                  return false;
               }

               this.head.link = var2 = var2.next;
            }

            int var3 = var2.readIndex;
            int var4 = var2.get();
            int var5 = var4 - var3;
            if (var5 == 0) {
               return false;
            } else {
               int var6 = var1.size;
               int var7 = var6 + var5;
               if (var7 > var1.elements.length) {
                  int var8 = var1.increaseCapacity(var7);
                  var4 = Math.min(var3 + var8 - var6, var4);
               }

               if (var3 != var4) {
                  Recycler.DefaultHandle[] var13 = var2.elements;
                  Recycler.DefaultHandle[] var9 = var1.elements;
                  int var10 = var6;

                  for(int var11 = var3; var11 < var4; ++var11) {
                     Recycler.DefaultHandle var12 = var13[var11];
                     if (var12.recycleId == 0) {
                        var12.recycleId = var12.lastRecycledId;
                     } else if (var12.recycleId != var12.lastRecycledId) {
                        throw new IllegalStateException("recycled already");
                     }

                     var13[var11] = null;
                     if (!var1.dropHandle(var12)) {
                        var12.stack = var1;
                        var9[var10++] = var12;
                     }
                  }

                  if (var4 == Recycler.LINK_CAPACITY && var2.next != null) {
                     this.head.reclaimSpace(Recycler.LINK_CAPACITY);
                     this.head.link = var2.next;
                  }

                  var2.readIndex = var4;
                  if (var1.size == var10) {
                     return false;
                  } else {
                     var1.size = var10;
                     return true;
                  }
               } else {
                  return false;
               }
            }
         }
      }

      static final class Head implements Runnable {
         private final AtomicInteger availableSharedCapacity;
         Recycler.WeakOrderQueue.Link link;

         Head(AtomicInteger var1) {
            super();
            this.availableSharedCapacity = var1;
         }

         public void run() {
            for(Recycler.WeakOrderQueue.Link var1 = this.link; var1 != null; var1 = var1.next) {
               this.reclaimSpace(Recycler.LINK_CAPACITY);
            }

         }

         void reclaimSpace(int var1) {
            assert var1 >= 0;

            this.availableSharedCapacity.addAndGet(var1);
         }

         boolean reserveSpace(int var1) {
            return reserveSpace(this.availableSharedCapacity, var1);
         }

         static boolean reserveSpace(AtomicInteger var0, int var1) {
            assert var1 >= 0;

            int var2;
            do {
               var2 = var0.get();
               if (var2 < var1) {
                  return false;
               }
            } while(!var0.compareAndSet(var2, var2 - var1));

            return true;
         }
      }

      static final class Link extends AtomicInteger {
         private final Recycler.DefaultHandle<?>[] elements;
         private int readIndex;
         Recycler.WeakOrderQueue.Link next;

         Link() {
            super();
            this.elements = new Recycler.DefaultHandle[Recycler.LINK_CAPACITY];
         }
      }
   }

   static final class DefaultHandle<T> implements Recycler.Handle<T> {
      private int lastRecycledId;
      private int recycleId;
      boolean hasBeenRecycled;
      private Recycler.Stack<?> stack;
      private Object value;

      DefaultHandle(Recycler.Stack<?> var1) {
         super();
         this.stack = var1;
      }

      public void recycle(Object var1) {
         if (var1 != this.value) {
            throw new IllegalArgumentException("object does not belong to handle");
         } else {
            this.stack.push(this);
         }
      }
   }

   public interface Handle<T> {
      void recycle(T var1);
   }
}
