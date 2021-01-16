package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Beta
@GwtIncompatible
public abstract class Striped<L> {
   private static final int LARGE_LAZY_CUTOFF = 1024;
   private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
      public ReadWriteLock get() {
         return new ReentrantReadWriteLock();
      }
   };
   private static final int ALL_SET = -1;

   private Striped() {
      super();
   }

   public abstract L get(Object var1);

   public abstract L getAt(int var1);

   abstract int indexFor(Object var1);

   public abstract int size();

   public Iterable<L> bulkGet(Iterable<?> var1) {
      Object[] var2 = Iterables.toArray(var1, Object.class);
      if (var2.length == 0) {
         return ImmutableList.of();
      } else {
         int[] var3 = new int[var2.length];

         int var4;
         for(var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = this.indexFor(var2[var4]);
         }

         Arrays.sort(var3);
         var4 = var3[0];
         var2[0] = this.getAt(var4);

         for(int var5 = 1; var5 < var2.length; ++var5) {
            int var6 = var3[var5];
            if (var6 == var4) {
               var2[var5] = var2[var5 - 1];
            } else {
               var2[var5] = this.getAt(var6);
               var4 = var6;
            }
         }

         List var7 = Arrays.asList(var2);
         return Collections.unmodifiableList(var7);
      }
   }

   public static Striped<Lock> lock(int var0) {
      return new Striped.CompactStriped(var0, new Supplier<Lock>() {
         public Lock get() {
            return new Striped.PaddedLock();
         }
      });
   }

   public static Striped<Lock> lazyWeakLock(int var0) {
      return lazy(var0, new Supplier<Lock>() {
         public Lock get() {
            return new ReentrantLock(false);
         }
      });
   }

   private static <L> Striped<L> lazy(int var0, Supplier<L> var1) {
      return (Striped)(var0 < 1024 ? new Striped.SmallLazyStriped(var0, var1) : new Striped.LargeLazyStriped(var0, var1));
   }

   public static Striped<Semaphore> semaphore(int var0, final int var1) {
      return new Striped.CompactStriped(var0, new Supplier<Semaphore>() {
         public Semaphore get() {
            return new Striped.PaddedSemaphore(var1);
         }
      });
   }

   public static Striped<Semaphore> lazyWeakSemaphore(int var0, final int var1) {
      return lazy(var0, new Supplier<Semaphore>() {
         public Semaphore get() {
            return new Semaphore(var1, false);
         }
      });
   }

   public static Striped<ReadWriteLock> readWriteLock(int var0) {
      return new Striped.CompactStriped(var0, READ_WRITE_LOCK_SUPPLIER);
   }

   public static Striped<ReadWriteLock> lazyWeakReadWriteLock(int var0) {
      return lazy(var0, READ_WRITE_LOCK_SUPPLIER);
   }

   private static int ceilToPowerOfTwo(int var0) {
      return 1 << IntMath.log2(var0, RoundingMode.CEILING);
   }

   private static int smear(int var0) {
      var0 ^= var0 >>> 20 ^ var0 >>> 12;
      return var0 ^ var0 >>> 7 ^ var0 >>> 4;
   }

   // $FF: synthetic method
   Striped(Object var1) {
      this();
   }

   private static class PaddedSemaphore extends Semaphore {
      long unused1;
      long unused2;
      long unused3;

      PaddedSemaphore(int var1) {
         super(var1, false);
      }
   }

   private static class PaddedLock extends ReentrantLock {
      long unused1;
      long unused2;
      long unused3;

      PaddedLock() {
         super(false);
      }
   }

   @VisibleForTesting
   static class LargeLazyStriped<L> extends Striped.PowerOfTwoStriped<L> {
      final ConcurrentMap<Integer, L> locks;
      final Supplier<L> supplier;
      final int size;

      LargeLazyStriped(int var1, Supplier<L> var2) {
         super(var1);
         this.size = this.mask == -1 ? 2147483647 : this.mask + 1;
         this.supplier = var2;
         this.locks = (new MapMaker()).weakValues().makeMap();
      }

      public L getAt(int var1) {
         if (this.size != 2147483647) {
            Preconditions.checkElementIndex(var1, this.size());
         }

         Object var2 = this.locks.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            Object var3 = this.supplier.get();
            var2 = this.locks.putIfAbsent(var1, var3);
            return MoreObjects.firstNonNull(var2, var3);
         }
      }

      public int size() {
         return this.size;
      }
   }

   @VisibleForTesting
   static class SmallLazyStriped<L> extends Striped.PowerOfTwoStriped<L> {
      final AtomicReferenceArray<Striped.SmallLazyStriped.ArrayReference<? extends L>> locks;
      final Supplier<L> supplier;
      final int size;
      final ReferenceQueue<L> queue = new ReferenceQueue();

      SmallLazyStriped(int var1, Supplier<L> var2) {
         super(var1);
         this.size = this.mask == -1 ? 2147483647 : this.mask + 1;
         this.locks = new AtomicReferenceArray(this.size);
         this.supplier = var2;
      }

      public L getAt(int var1) {
         if (this.size != 2147483647) {
            Preconditions.checkElementIndex(var1, this.size());
         }

         Striped.SmallLazyStriped.ArrayReference var2 = (Striped.SmallLazyStriped.ArrayReference)this.locks.get(var1);
         Object var3 = var2 == null ? null : var2.get();
         if (var3 != null) {
            return var3;
         } else {
            Object var4 = this.supplier.get();
            Striped.SmallLazyStriped.ArrayReference var5 = new Striped.SmallLazyStriped.ArrayReference(var4, var1, this.queue);

            do {
               if (this.locks.compareAndSet(var1, var2, var5)) {
                  this.drainQueue();
                  return var4;
               }

               var2 = (Striped.SmallLazyStriped.ArrayReference)this.locks.get(var1);
               var3 = var2 == null ? null : var2.get();
            } while(var3 == null);

            return var3;
         }
      }

      private void drainQueue() {
         Reference var1;
         while((var1 = this.queue.poll()) != null) {
            Striped.SmallLazyStriped.ArrayReference var2 = (Striped.SmallLazyStriped.ArrayReference)var1;
            this.locks.compareAndSet(var2.index, var2, (Object)null);
         }

      }

      public int size() {
         return this.size;
      }

      private static final class ArrayReference<L> extends WeakReference<L> {
         final int index;

         ArrayReference(L var1, int var2, ReferenceQueue<L> var3) {
            super(var1, var3);
            this.index = var2;
         }
      }
   }

   private static class CompactStriped<L> extends Striped.PowerOfTwoStriped<L> {
      private final Object[] array;

      private CompactStriped(int var1, Supplier<L> var2) {
         super(var1);
         Preconditions.checkArgument(var1 <= 1073741824, "Stripes must be <= 2^30)");
         this.array = new Object[this.mask + 1];

         for(int var3 = 0; var3 < this.array.length; ++var3) {
            this.array[var3] = var2.get();
         }

      }

      public L getAt(int var1) {
         return this.array[var1];
      }

      public int size() {
         return this.array.length;
      }

      // $FF: synthetic method
      CompactStriped(int var1, Supplier var2, Object var3) {
         this(var1, var2);
      }
   }

   private abstract static class PowerOfTwoStriped<L> extends Striped<L> {
      final int mask;

      PowerOfTwoStriped(int var1) {
         super(null);
         Preconditions.checkArgument(var1 > 0, "Stripes must be positive");
         this.mask = var1 > 1073741824 ? -1 : Striped.ceilToPowerOfTwo(var1) - 1;
      }

      final int indexFor(Object var1) {
         int var2 = Striped.smear(var1.hashCode());
         return var2 & this.mask;
      }

      public final L get(Object var1) {
         return this.getAt(this.indexFor(var1));
      }
   }
}
