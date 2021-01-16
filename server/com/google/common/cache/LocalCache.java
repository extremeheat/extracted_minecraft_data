package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.j2objc.annotations.Weak;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

@GwtCompatible(
   emulated = true
)
class LocalCache<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
   static final int MAXIMUM_CAPACITY = 1073741824;
   static final int MAX_SEGMENTS = 65536;
   static final int CONTAINS_VALUE_RETRIES = 3;
   static final int DRAIN_THRESHOLD = 63;
   static final int DRAIN_MAX = 16;
   static final Logger logger = Logger.getLogger(LocalCache.class.getName());
   final int segmentMask;
   final int segmentShift;
   final LocalCache.Segment<K, V>[] segments;
   final int concurrencyLevel;
   final Equivalence<Object> keyEquivalence;
   final Equivalence<Object> valueEquivalence;
   final LocalCache.Strength keyStrength;
   final LocalCache.Strength valueStrength;
   final long maxWeight;
   final Weigher<K, V> weigher;
   final long expireAfterAccessNanos;
   final long expireAfterWriteNanos;
   final long refreshNanos;
   final Queue<RemovalNotification<K, V>> removalNotificationQueue;
   final RemovalListener<K, V> removalListener;
   final Ticker ticker;
   final LocalCache.EntryFactory entryFactory;
   final AbstractCache.StatsCounter globalStatsCounter;
   @Nullable
   final CacheLoader<? super K, V> defaultLoader;
   static final LocalCache.ValueReference<Object, Object> UNSET = new LocalCache.ValueReference<Object, Object>() {
      public Object get() {
         return null;
      }

      public int getWeight() {
         return 0;
      }

      public LocalCache.ReferenceEntry<Object, Object> getEntry() {
         return null;
      }

      public LocalCache.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> var1, @Nullable Object var2, LocalCache.ReferenceEntry<Object, Object> var3) {
         return this;
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return false;
      }

      public Object waitForValue() {
         return null;
      }

      public void notifyNewValue(Object var1) {
      }
   };
   static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue<Object>() {
      public boolean offer(Object var1) {
         return true;
      }

      public Object peek() {
         return null;
      }

      public Object poll() {
         return null;
      }

      public int size() {
         return 0;
      }

      public Iterator<Object> iterator() {
         return ImmutableSet.of().iterator();
      }
   };
   Set<K> keySet;
   Collection<V> values;
   Set<Entry<K, V>> entrySet;

   LocalCache(CacheBuilder<? super K, ? super V> var1, @Nullable CacheLoader<? super K, V> var2) {
      super();
      this.concurrencyLevel = Math.min(var1.getConcurrencyLevel(), 65536);
      this.keyStrength = var1.getKeyStrength();
      this.valueStrength = var1.getValueStrength();
      this.keyEquivalence = var1.getKeyEquivalence();
      this.valueEquivalence = var1.getValueEquivalence();
      this.maxWeight = var1.getMaximumWeight();
      this.weigher = var1.getWeigher();
      this.expireAfterAccessNanos = var1.getExpireAfterAccessNanos();
      this.expireAfterWriteNanos = var1.getExpireAfterWriteNanos();
      this.refreshNanos = var1.getRefreshNanos();
      this.removalListener = var1.getRemovalListener();
      this.removalNotificationQueue = (Queue)(this.removalListener == CacheBuilder.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
      this.ticker = var1.getTicker(this.recordsTime());
      this.entryFactory = LocalCache.EntryFactory.getFactory(this.keyStrength, this.usesAccessEntries(), this.usesWriteEntries());
      this.globalStatsCounter = (AbstractCache.StatsCounter)var1.getStatsCounterSupplier().get();
      this.defaultLoader = var2;
      int var3 = Math.min(var1.getInitialCapacity(), 1073741824);
      if (this.evictsBySize() && !this.customWeigher()) {
         var3 = Math.min(var3, (int)this.maxWeight);
      }

      int var4 = 0;

      int var5;
      for(var5 = 1; var5 < this.concurrencyLevel && (!this.evictsBySize() || (long)(var5 * 20) <= this.maxWeight); var5 <<= 1) {
         ++var4;
      }

      this.segmentShift = 32 - var4;
      this.segmentMask = var5 - 1;
      this.segments = this.newSegmentArray(var5);
      int var6 = var3 / var5;
      if (var6 * var5 < var3) {
         ++var6;
      }

      int var7;
      for(var7 = 1; var7 < var6; var7 <<= 1) {
      }

      if (this.evictsBySize()) {
         long var8 = this.maxWeight / (long)var5 + 1L;
         long var10 = this.maxWeight % (long)var5;

         for(int var12 = 0; var12 < this.segments.length; ++var12) {
            if ((long)var12 == var10) {
               --var8;
            }

            this.segments[var12] = this.createSegment(var7, var8, (AbstractCache.StatsCounter)var1.getStatsCounterSupplier().get());
         }
      } else {
         for(int var13 = 0; var13 < this.segments.length; ++var13) {
            this.segments[var13] = this.createSegment(var7, -1L, (AbstractCache.StatsCounter)var1.getStatsCounterSupplier().get());
         }
      }

   }

   boolean evictsBySize() {
      return this.maxWeight >= 0L;
   }

   boolean customWeigher() {
      return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
   }

   boolean expires() {
      return this.expiresAfterWrite() || this.expiresAfterAccess();
   }

   boolean expiresAfterWrite() {
      return this.expireAfterWriteNanos > 0L;
   }

   boolean expiresAfterAccess() {
      return this.expireAfterAccessNanos > 0L;
   }

   boolean refreshes() {
      return this.refreshNanos > 0L;
   }

   boolean usesAccessQueue() {
      return this.expiresAfterAccess() || this.evictsBySize();
   }

   boolean usesWriteQueue() {
      return this.expiresAfterWrite();
   }

   boolean recordsWrite() {
      return this.expiresAfterWrite() || this.refreshes();
   }

   boolean recordsAccess() {
      return this.expiresAfterAccess();
   }

   boolean recordsTime() {
      return this.recordsWrite() || this.recordsAccess();
   }

   boolean usesWriteEntries() {
      return this.usesWriteQueue() || this.recordsWrite();
   }

   boolean usesAccessEntries() {
      return this.usesAccessQueue() || this.recordsAccess();
   }

   boolean usesKeyReferences() {
      return this.keyStrength != LocalCache.Strength.STRONG;
   }

   boolean usesValueReferences() {
      return this.valueStrength != LocalCache.Strength.STRONG;
   }

   static <K, V> LocalCache.ValueReference<K, V> unset() {
      return UNSET;
   }

   static <K, V> LocalCache.ReferenceEntry<K, V> nullEntry() {
      return LocalCache.NullEntry.INSTANCE;
   }

   static <E> Queue<E> discardingQueue() {
      return DISCARDING_QUEUE;
   }

   static int rehash(int var0) {
      var0 += var0 << 15 ^ -12931;
      var0 ^= var0 >>> 10;
      var0 += var0 << 3;
      var0 ^= var0 >>> 6;
      var0 += (var0 << 2) + (var0 << 14);
      return var0 ^ var0 >>> 16;
   }

   @VisibleForTesting
   LocalCache.ReferenceEntry<K, V> newEntry(K var1, int var2, @Nullable LocalCache.ReferenceEntry<K, V> var3) {
      LocalCache.Segment var4 = this.segmentFor(var2);
      var4.lock();

      LocalCache.ReferenceEntry var5;
      try {
         var5 = var4.newEntry(var1, var2, var3);
      } finally {
         var4.unlock();
      }

      return var5;
   }

   @VisibleForTesting
   LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.ReferenceEntry<K, V> var1, LocalCache.ReferenceEntry<K, V> var2) {
      int var3 = var1.getHash();
      return this.segmentFor(var3).copyEntry(var1, var2);
   }

   @VisibleForTesting
   LocalCache.ValueReference<K, V> newValueReference(LocalCache.ReferenceEntry<K, V> var1, V var2, int var3) {
      int var4 = var1.getHash();
      return this.valueStrength.referenceValue(this.segmentFor(var4), var1, Preconditions.checkNotNull(var2), var3);
   }

   int hash(@Nullable Object var1) {
      int var2 = this.keyEquivalence.hash(var1);
      return rehash(var2);
   }

   void reclaimValue(LocalCache.ValueReference<K, V> var1) {
      LocalCache.ReferenceEntry var2 = var1.getEntry();
      int var3 = var2.getHash();
      this.segmentFor(var3).reclaimValue(var2.getKey(), var3, var1);
   }

   void reclaimKey(LocalCache.ReferenceEntry<K, V> var1) {
      int var2 = var1.getHash();
      this.segmentFor(var2).reclaimKey(var1, var2);
   }

   @VisibleForTesting
   boolean isLive(LocalCache.ReferenceEntry<K, V> var1, long var2) {
      return this.segmentFor(var1.getHash()).getLiveValue(var1, var2) != null;
   }

   LocalCache.Segment<K, V> segmentFor(int var1) {
      return this.segments[var1 >>> this.segmentShift & this.segmentMask];
   }

   LocalCache.Segment<K, V> createSegment(int var1, long var2, AbstractCache.StatsCounter var4) {
      return new LocalCache.Segment(this, var1, var2, var4);
   }

   @Nullable
   V getLiveValue(LocalCache.ReferenceEntry<K, V> var1, long var2) {
      if (var1.getKey() == null) {
         return null;
      } else {
         Object var4 = var1.getValueReference().get();
         if (var4 == null) {
            return null;
         } else {
            return this.isExpired(var1, var2) ? null : var4;
         }
      }
   }

   boolean isExpired(LocalCache.ReferenceEntry<K, V> var1, long var2) {
      Preconditions.checkNotNull(var1);
      if (this.expiresAfterAccess() && var2 - var1.getAccessTime() >= this.expireAfterAccessNanos) {
         return true;
      } else {
         return this.expiresAfterWrite() && var2 - var1.getWriteTime() >= this.expireAfterWriteNanos;
      }
   }

   static <K, V> void connectAccessOrder(LocalCache.ReferenceEntry<K, V> var0, LocalCache.ReferenceEntry<K, V> var1) {
      var0.setNextInAccessQueue(var1);
      var1.setPreviousInAccessQueue(var0);
   }

   static <K, V> void nullifyAccessOrder(LocalCache.ReferenceEntry<K, V> var0) {
      LocalCache.ReferenceEntry var1 = nullEntry();
      var0.setNextInAccessQueue(var1);
      var0.setPreviousInAccessQueue(var1);
   }

   static <K, V> void connectWriteOrder(LocalCache.ReferenceEntry<K, V> var0, LocalCache.ReferenceEntry<K, V> var1) {
      var0.setNextInWriteQueue(var1);
      var1.setPreviousInWriteQueue(var0);
   }

   static <K, V> void nullifyWriteOrder(LocalCache.ReferenceEntry<K, V> var0) {
      LocalCache.ReferenceEntry var1 = nullEntry();
      var0.setNextInWriteQueue(var1);
      var0.setPreviousInWriteQueue(var1);
   }

   void processPendingNotifications() {
      RemovalNotification var1;
      while((var1 = (RemovalNotification)this.removalNotificationQueue.poll()) != null) {
         try {
            this.removalListener.onRemoval(var1);
         } catch (Throwable var3) {
            logger.log(Level.WARNING, "Exception thrown by removal listener", var3);
         }
      }

   }

   final LocalCache.Segment<K, V>[] newSegmentArray(int var1) {
      return new LocalCache.Segment[var1];
   }

   public void cleanUp() {
      LocalCache.Segment[] var1 = this.segments;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         LocalCache.Segment var4 = var1[var3];
         var4.cleanUp();
      }

   }

   public boolean isEmpty() {
      long var1 = 0L;
      LocalCache.Segment[] var3 = this.segments;

      int var4;
      for(var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4].count != 0) {
            return false;
         }

         var1 += (long)var3[var4].modCount;
      }

      if (var1 != 0L) {
         for(var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4].count != 0) {
               return false;
            }

            var1 -= (long)var3[var4].modCount;
         }

         if (var1 != 0L) {
            return false;
         }
      }

      return true;
   }

   long longSize() {
      LocalCache.Segment[] var1 = this.segments;
      long var2 = 0L;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         var2 += (long)Math.max(0, var1[var4].count);
      }

      return var2;
   }

   public int size() {
      return Ints.saturatedCast(this.longSize());
   }

   @Nullable
   public V get(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).get(var1, var2);
      }
   }

   @Nullable
   public V getIfPresent(Object var1) {
      int var2 = this.hash(Preconditions.checkNotNull(var1));
      Object var3 = this.segmentFor(var2).get(var1, var2);
      if (var3 == null) {
         this.globalStatsCounter.recordMisses(1);
      } else {
         this.globalStatsCounter.recordHits(1);
      }

      return var3;
   }

   @Nullable
   public V getOrDefault(@Nullable Object var1, @Nullable V var2) {
      Object var3 = this.get(var1);
      return var3 != null ? var3 : var2;
   }

   V get(K var1, CacheLoader<? super K, V> var2) throws ExecutionException {
      int var3 = this.hash(Preconditions.checkNotNull(var1));
      return this.segmentFor(var3).get(var1, var3, var2);
   }

   V getOrLoad(K var1) throws ExecutionException {
      return this.get(var1, this.defaultLoader);
   }

   ImmutableMap<K, V> getAllPresent(Iterable<?> var1) {
      int var2 = 0;
      int var3 = 0;
      LinkedHashMap var4 = Maps.newLinkedHashMap();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         Object var7 = this.get(var6);
         if (var7 == null) {
            ++var3;
         } else {
            var4.put(var6, var7);
            ++var2;
         }
      }

      this.globalStatsCounter.recordHits(var2);
      this.globalStatsCounter.recordMisses(var3);
      return ImmutableMap.copyOf((Map)var4);
   }

   ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
      int var2 = 0;
      int var3 = 0;
      LinkedHashMap var4 = Maps.newLinkedHashMap();
      LinkedHashSet var5 = Sets.newLinkedHashSet();
      Iterator var6 = var1.iterator();

      Object var8;
      while(var6.hasNext()) {
         Object var7 = var6.next();
         var8 = this.get(var7);
         if (!var4.containsKey(var7)) {
            var4.put(var7, var8);
            if (var8 == null) {
               ++var3;
               var5.add(var7);
            } else {
               ++var2;
            }
         }
      }

      ImmutableMap var16;
      try {
         if (!var5.isEmpty()) {
            Iterator var17;
            try {
               Map var15 = this.loadAll(var5, this.defaultLoader);
               var17 = var5.iterator();

               while(var17.hasNext()) {
                  var8 = var17.next();
                  Object var9 = var15.get(var8);
                  if (var9 == null) {
                     throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + var8);
                  }

                  var4.put(var8, var9);
               }
            } catch (CacheLoader.UnsupportedLoadingOperationException var13) {
               var17 = var5.iterator();

               while(var17.hasNext()) {
                  var8 = var17.next();
                  --var3;
                  var4.put(var8, this.get(var8, this.defaultLoader));
               }
            }
         }

         var16 = ImmutableMap.copyOf((Map)var4);
      } finally {
         this.globalStatsCounter.recordHits(var2);
         this.globalStatsCounter.recordMisses(var3);
      }

      return var16;
   }

   @Nullable
   Map<K, V> loadAll(Set<? extends K> var1, CacheLoader<? super K, V> var2) throws ExecutionException {
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var1);
      Stopwatch var3 = Stopwatch.createStarted();
      boolean var5 = false;

      Map var4;
      try {
         Map var6 = var2.loadAll(var1);
         var4 = var6;
         var5 = true;
      } catch (CacheLoader.UnsupportedLoadingOperationException var17) {
         var5 = true;
         throw var17;
      } catch (InterruptedException var18) {
         Thread.currentThread().interrupt();
         throw new ExecutionException(var18);
      } catch (RuntimeException var19) {
         throw new UncheckedExecutionException(var19);
      } catch (Exception var20) {
         throw new ExecutionException(var20);
      } catch (Error var21) {
         throw new ExecutionError(var21);
      } finally {
         if (!var5) {
            this.globalStatsCounter.recordLoadException(var3.elapsed(TimeUnit.NANOSECONDS));
         }

      }

      if (var4 == null) {
         this.globalStatsCounter.recordLoadException(var3.elapsed(TimeUnit.NANOSECONDS));
         throw new CacheLoader.InvalidCacheLoadException(var2 + " returned null map from loadAll");
      } else {
         var3.stop();
         boolean var23 = false;
         Iterator var7 = var4.entrySet().iterator();

         while(true) {
            while(var7.hasNext()) {
               Entry var8 = (Entry)var7.next();
               Object var9 = var8.getKey();
               Object var10 = var8.getValue();
               if (var9 != null && var10 != null) {
                  this.put(var9, var10);
               } else {
                  var23 = true;
               }
            }

            if (var23) {
               this.globalStatsCounter.recordLoadException(var3.elapsed(TimeUnit.NANOSECONDS));
               throw new CacheLoader.InvalidCacheLoadException(var2 + " returned null keys or values from loadAll");
            }

            this.globalStatsCounter.recordLoadSuccess(var3.elapsed(TimeUnit.NANOSECONDS));
            return var4;
         }
      }
   }

   LocalCache.ReferenceEntry<K, V> getEntry(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).getEntry(var1, var2);
      }
   }

   void refresh(K var1) {
      int var2 = this.hash(Preconditions.checkNotNull(var1));
      this.segmentFor(var2).refresh(var1, var2, this.defaultLoader, false);
   }

   public boolean containsKey(@Nullable Object var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).containsKey(var1, var2);
      }
   }

   public boolean containsValue(@Nullable Object var1) {
      if (var1 == null) {
         return false;
      } else {
         long var2 = this.ticker.read();
         LocalCache.Segment[] var4 = this.segments;
         long var5 = -1L;

         for(int var7 = 0; var7 < 3; ++var7) {
            long var8 = 0L;
            LocalCache.Segment[] var10 = var4;
            int var11 = var4.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               LocalCache.Segment var13 = var10[var12];
               int var14 = var13.count;
               AtomicReferenceArray var15 = var13.table;

               for(int var16 = 0; var16 < var15.length(); ++var16) {
                  for(LocalCache.ReferenceEntry var17 = (LocalCache.ReferenceEntry)var15.get(var16); var17 != null; var17 = var17.getNext()) {
                     Object var18 = var13.getLiveValue(var17, var2);
                     if (var18 != null && this.valueEquivalence.equivalent(var1, var18)) {
                        return true;
                     }
                  }
               }

               var8 += (long)var13.modCount;
            }

            if (var8 == var5) {
               break;
            }

            var5 = var8;
         }

         return false;
      }
   }

   public V put(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).put(var1, var3, var2, false);
   }

   public V putIfAbsent(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).put(var1, var3, var2, true);
   }

   public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).compute(var1, var3, var2);
   }

   public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return this.compute(var1, (var2x, var3) -> {
         return var3 == null ? var2.apply(var1) : var3;
      });
   }

   public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return this.compute(var1, (var1x, var2x) -> {
         return var2x == null ? null : var2.apply(var1x, var2x);
      });
   }

   public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return this.compute(var1, (var2x, var3x) -> {
         return var3x == null ? var2 : var3.apply(var3x, var2);
      });
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put(var3.getKey(), var3.getValue());
      }

   }

   public V remove(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).remove(var1, var2);
      }
   }

   public boolean remove(@Nullable Object var1, @Nullable Object var2) {
      if (var1 != null && var2 != null) {
         int var3 = this.hash(var1);
         return this.segmentFor(var3).remove(var1, var3, var2);
      } else {
         return false;
      }
   }

   public boolean replace(K var1, @Nullable V var2, V var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      if (var2 == null) {
         return false;
      } else {
         int var4 = this.hash(var1);
         return this.segmentFor(var4).replace(var1, var4, var2, var3);
      }
   }

   public V replace(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).replace(var1, var3, var2);
   }

   public void clear() {
      LocalCache.Segment[] var1 = this.segments;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         LocalCache.Segment var4 = var1[var3];
         var4.clear();
      }

   }

   void invalidateAll(Iterable<?> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         this.remove(var3);
      }

   }

   public Set<K> keySet() {
      Set var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new LocalCache.KeySet(this));
   }

   public Collection<V> values() {
      Collection var1 = this.values;
      return var1 != null ? var1 : (this.values = new LocalCache.Values(this));
   }

   @GwtIncompatible
   public Set<Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new LocalCache.EntrySet(this));
   }

   private static <E> ArrayList<E> toArrayList(Collection<E> var0) {
      ArrayList var1 = new ArrayList(var0.size());
      Iterators.addAll(var1, var0.iterator());
      return var1;
   }

   boolean removeIf(BiPredicate<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      boolean var2 = false;
      Iterator var3 = this.keySet().iterator();

      while(true) {
         label25:
         while(var3.hasNext()) {
            Object var4 = var3.next();

            Object var5;
            do {
               var5 = this.get(var4);
               if (var5 == null || !var1.test(var4, var5)) {
                  continue label25;
               }
            } while(!this.remove(var4, var5));

            var2 = true;
         }

         return var2;
      }
   }

   static class LocalLoadingCache<K, V> extends LocalCache.LocalManualCache<K, V> implements LoadingCache<K, V> {
      private static final long serialVersionUID = 1L;

      LocalLoadingCache(CacheBuilder<? super K, ? super V> var1, CacheLoader<? super K, V> var2) {
         super(new LocalCache(var1, (CacheLoader)Preconditions.checkNotNull(var2)), null);
      }

      public V get(K var1) throws ExecutionException {
         return this.localCache.getOrLoad(var1);
      }

      public V getUnchecked(K var1) {
         try {
            return this.get(var1);
         } catch (ExecutionException var3) {
            throw new UncheckedExecutionException(var3.getCause());
         }
      }

      public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
         return this.localCache.getAll(var1);
      }

      public void refresh(K var1) {
         this.localCache.refresh(var1);
      }

      public final V apply(K var1) {
         return this.getUnchecked(var1);
      }

      Object writeReplace() {
         return new LocalCache.LoadingSerializationProxy(this.localCache);
      }
   }

   static class LocalManualCache<K, V> implements Cache<K, V>, Serializable {
      final LocalCache<K, V> localCache;
      private static final long serialVersionUID = 1L;

      LocalManualCache(CacheBuilder<? super K, ? super V> var1) {
         this(new LocalCache(var1, (CacheLoader)null));
      }

      private LocalManualCache(LocalCache<K, V> var1) {
         super();
         this.localCache = var1;
      }

      @Nullable
      public V getIfPresent(Object var1) {
         return this.localCache.getIfPresent(var1);
      }

      public V get(K var1, final Callable<? extends V> var2) throws ExecutionException {
         Preconditions.checkNotNull(var2);
         return this.localCache.get(var1, new CacheLoader<Object, V>() {
            public V load(Object var1) throws Exception {
               return var2.call();
            }
         });
      }

      public ImmutableMap<K, V> getAllPresent(Iterable<?> var1) {
         return this.localCache.getAllPresent(var1);
      }

      public void put(K var1, V var2) {
         this.localCache.put(var1, var2);
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         this.localCache.putAll(var1);
      }

      public void invalidate(Object var1) {
         Preconditions.checkNotNull(var1);
         this.localCache.remove(var1);
      }

      public void invalidateAll(Iterable<?> var1) {
         this.localCache.invalidateAll(var1);
      }

      public void invalidateAll() {
         this.localCache.clear();
      }

      public long size() {
         return this.localCache.longSize();
      }

      public ConcurrentMap<K, V> asMap() {
         return this.localCache;
      }

      public CacheStats stats() {
         AbstractCache.SimpleStatsCounter var1 = new AbstractCache.SimpleStatsCounter();
         var1.incrementBy(this.localCache.globalStatsCounter);
         LocalCache.Segment[] var2 = this.localCache.segments;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LocalCache.Segment var5 = var2[var4];
            var1.incrementBy(var5.statsCounter);
         }

         return var1.snapshot();
      }

      public void cleanUp() {
         this.localCache.cleanUp();
      }

      Object writeReplace() {
         return new LocalCache.ManualSerializationProxy(this.localCache);
      }

      // $FF: synthetic method
      LocalManualCache(LocalCache var1, Object var2) {
         this(var1);
      }
   }

   static final class LoadingSerializationProxy<K, V> extends LocalCache.ManualSerializationProxy<K, V> implements LoadingCache<K, V>, Serializable {
      private static final long serialVersionUID = 1L;
      transient LoadingCache<K, V> autoDelegate;

      LoadingSerializationProxy(LocalCache<K, V> var1) {
         super(var1);
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         CacheBuilder var2 = this.recreateCacheBuilder();
         this.autoDelegate = var2.build(this.loader);
      }

      public V get(K var1) throws ExecutionException {
         return this.autoDelegate.get(var1);
      }

      public V getUnchecked(K var1) {
         return this.autoDelegate.getUnchecked(var1);
      }

      public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
         return this.autoDelegate.getAll(var1);
      }

      public final V apply(K var1) {
         return this.autoDelegate.apply(var1);
      }

      public void refresh(K var1) {
         this.autoDelegate.refresh(var1);
      }

      private Object readResolve() {
         return this.autoDelegate;
      }
   }

   static class ManualSerializationProxy<K, V> extends ForwardingCache<K, V> implements Serializable {
      private static final long serialVersionUID = 1L;
      final LocalCache.Strength keyStrength;
      final LocalCache.Strength valueStrength;
      final Equivalence<Object> keyEquivalence;
      final Equivalence<Object> valueEquivalence;
      final long expireAfterWriteNanos;
      final long expireAfterAccessNanos;
      final long maxWeight;
      final Weigher<K, V> weigher;
      final int concurrencyLevel;
      final RemovalListener<? super K, ? super V> removalListener;
      final Ticker ticker;
      final CacheLoader<? super K, V> loader;
      transient Cache<K, V> delegate;

      ManualSerializationProxy(LocalCache<K, V> var1) {
         this(var1.keyStrength, var1.valueStrength, var1.keyEquivalence, var1.valueEquivalence, var1.expireAfterWriteNanos, var1.expireAfterAccessNanos, var1.maxWeight, var1.weigher, var1.concurrencyLevel, var1.removalListener, var1.ticker, var1.defaultLoader);
      }

      private ManualSerializationProxy(LocalCache.Strength var1, LocalCache.Strength var2, Equivalence<Object> var3, Equivalence<Object> var4, long var5, long var7, long var9, Weigher<K, V> var11, int var12, RemovalListener<? super K, ? super V> var13, Ticker var14, CacheLoader<? super K, V> var15) {
         super();
         this.keyStrength = var1;
         this.valueStrength = var2;
         this.keyEquivalence = var3;
         this.valueEquivalence = var4;
         this.expireAfterWriteNanos = var5;
         this.expireAfterAccessNanos = var7;
         this.maxWeight = var9;
         this.weigher = var11;
         this.concurrencyLevel = var12;
         this.removalListener = var13;
         this.ticker = var14 != Ticker.systemTicker() && var14 != CacheBuilder.NULL_TICKER ? var14 : null;
         this.loader = var15;
      }

      CacheBuilder<K, V> recreateCacheBuilder() {
         CacheBuilder var1 = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
         var1.strictParsing = false;
         if (this.expireAfterWriteNanos > 0L) {
            var1.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
         }

         if (this.expireAfterAccessNanos > 0L) {
            var1.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
         }

         if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
            var1.weigher(this.weigher);
            if (this.maxWeight != -1L) {
               var1.maximumWeight(this.maxWeight);
            }
         } else if (this.maxWeight != -1L) {
            var1.maximumSize(this.maxWeight);
         }

         if (this.ticker != null) {
            var1.ticker(this.ticker);
         }

         return var1;
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         CacheBuilder var2 = this.recreateCacheBuilder();
         this.delegate = var2.build();
      }

      private Object readResolve() {
         return this.delegate;
      }

      protected Cache<K, V> delegate() {
         return this.delegate;
      }
   }

   final class EntrySet extends LocalCache<K, V>.AbstractCacheSet<Entry<K, V>> {
      EntrySet(ConcurrentMap<?, ?> var2) {
         super(var2);
      }

      public Iterator<Entry<K, V>> iterator() {
         return LocalCache.this.new EntryIterator();
      }

      public boolean removeIf(Predicate<? super Entry<K, V>> var1) {
         Preconditions.checkNotNull(var1);
         return LocalCache.this.removeIf((var1x, var2) -> {
            return var1.test(Maps.immutableEntry(var1x, var2));
         });
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            Object var3 = var2.getKey();
            if (var3 == null) {
               return false;
            } else {
               Object var4 = LocalCache.this.get(var3);
               return var4 != null && LocalCache.this.valueEquivalence.equivalent(var2.getValue(), var4);
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            Object var3 = var2.getKey();
            return var3 != null && LocalCache.this.remove(var3, var2.getValue());
         }
      }
   }

   final class Values extends AbstractCollection<V> {
      private final ConcurrentMap<?, ?> map;

      Values(ConcurrentMap<?, ?> var2) {
         super();
         this.map = var2;
      }

      public int size() {
         return this.map.size();
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public void clear() {
         this.map.clear();
      }

      public Iterator<V> iterator() {
         return LocalCache.this.new ValueIterator();
      }

      public boolean removeIf(Predicate<? super V> var1) {
         Preconditions.checkNotNull(var1);
         return LocalCache.this.removeIf((var1x, var2) -> {
            return var1.test(var2);
         });
      }

      public boolean contains(Object var1) {
         return this.map.containsValue(var1);
      }

      public Object[] toArray() {
         return LocalCache.toArrayList(this).toArray();
      }

      public <E> E[] toArray(E[] var1) {
         return LocalCache.toArrayList(this).toArray(var1);
      }
   }

   final class KeySet extends LocalCache<K, V>.AbstractCacheSet<K> {
      KeySet(ConcurrentMap<?, ?> var2) {
         super(var2);
      }

      public Iterator<K> iterator() {
         return LocalCache.this.new KeyIterator();
      }

      public boolean contains(Object var1) {
         return this.map.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return this.map.remove(var1) != null;
      }
   }

   abstract class AbstractCacheSet<T> extends AbstractSet<T> {
      @Weak
      final ConcurrentMap<?, ?> map;

      AbstractCacheSet(ConcurrentMap<?, ?> var2) {
         super();
         this.map = var2;
      }

      public int size() {
         return this.map.size();
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public void clear() {
         this.map.clear();
      }

      public Object[] toArray() {
         return LocalCache.toArrayList(this).toArray();
      }

      public <E> E[] toArray(E[] var1) {
         return LocalCache.toArrayList(this).toArray(var1);
      }
   }

   final class EntryIterator extends LocalCache<K, V>.HashIterator<Entry<K, V>> {
      EntryIterator() {
         super();
      }

      public Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   final class WriteThroughEntry implements Entry<K, V> {
      final K key;
      V value;

      WriteThroughEntry(K var2, V var3) {
         super();
         this.key = var2;
         this.value = var3;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            return this.key.equals(var2.getKey()) && this.value.equals(var2.getValue());
         }
      }

      public int hashCode() {
         return this.key.hashCode() ^ this.value.hashCode();
      }

      public V setValue(V var1) {
         Object var2 = LocalCache.this.put(this.key, var1);
         this.value = var1;
         return var2;
      }

      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }

   final class ValueIterator extends LocalCache<K, V>.HashIterator<V> {
      ValueIterator() {
         super();
      }

      public V next() {
         return this.nextEntry().getValue();
      }
   }

   final class KeyIterator extends LocalCache<K, V>.HashIterator<K> {
      KeyIterator() {
         super();
      }

      public K next() {
         return this.nextEntry().getKey();
      }
   }

   abstract class HashIterator<T> implements Iterator<T> {
      int nextSegmentIndex;
      int nextTableIndex;
      LocalCache.Segment<K, V> currentSegment;
      AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> currentTable;
      LocalCache.ReferenceEntry<K, V> nextEntry;
      LocalCache<K, V>.WriteThroughEntry nextExternal;
      LocalCache<K, V>.WriteThroughEntry lastReturned;

      HashIterator() {
         super();
         this.nextSegmentIndex = LocalCache.this.segments.length - 1;
         this.nextTableIndex = -1;
         this.advance();
      }

      public abstract T next();

      final void advance() {
         this.nextExternal = null;
         if (!this.nextInChain()) {
            if (!this.nextInTable()) {
               while(this.nextSegmentIndex >= 0) {
                  this.currentSegment = LocalCache.this.segments[this.nextSegmentIndex--];
                  if (this.currentSegment.count != 0) {
                     this.currentTable = this.currentSegment.table;
                     this.nextTableIndex = this.currentTable.length() - 1;
                     if (this.nextInTable()) {
                        return;
                     }
                  }
               }

            }
         }
      }

      boolean nextInChain() {
         if (this.nextEntry != null) {
            for(this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
               if (this.advanceTo(this.nextEntry)) {
                  return true;
               }
            }
         }

         return false;
      }

      boolean nextInTable() {
         while(true) {
            if (this.nextTableIndex >= 0) {
               if ((this.nextEntry = (LocalCache.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) == null || !this.advanceTo(this.nextEntry) && !this.nextInChain()) {
                  continue;
               }

               return true;
            }

            return false;
         }
      }

      boolean advanceTo(LocalCache.ReferenceEntry<K, V> var1) {
         boolean var6;
         try {
            long var2 = LocalCache.this.ticker.read();
            Object var4 = var1.getKey();
            Object var5 = LocalCache.this.getLiveValue(var1, var2);
            if (var5 == null) {
               var6 = false;
               return var6;
            }

            this.nextExternal = LocalCache.this.new WriteThroughEntry(var4, var5);
            var6 = true;
         } finally {
            this.currentSegment.postReadCleanup();
         }

         return var6;
      }

      public boolean hasNext() {
         return this.nextExternal != null;
      }

      LocalCache<K, V>.WriteThroughEntry nextEntry() {
         if (this.nextExternal == null) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.nextExternal;
            this.advance();
            return this.lastReturned;
         }
      }

      public void remove() {
         Preconditions.checkState(this.lastReturned != null);
         LocalCache.this.remove(this.lastReturned.getKey());
         this.lastReturned = null;
      }
   }

   static final class AccessQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>> {
      final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>() {
         LocalCache.ReferenceEntry<K, V> nextAccess = this;
         LocalCache.ReferenceEntry<K, V> previousAccess = this;

         public long getAccessTime() {
            return 9223372036854775807L;
         }

         public void setAccessTime(long var1) {
         }

         public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
         }

         public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
            this.nextAccess = var1;
         }

         public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
         }

         public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
            this.previousAccess = var1;
         }
      };

      AccessQueue() {
         super();
      }

      public boolean offer(LocalCache.ReferenceEntry<K, V> var1) {
         LocalCache.connectAccessOrder(var1.getPreviousInAccessQueue(), var1.getNextInAccessQueue());
         LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), var1);
         LocalCache.connectAccessOrder(var1, this.head);
         return true;
      }

      public LocalCache.ReferenceEntry<K, V> peek() {
         LocalCache.ReferenceEntry var1 = this.head.getNextInAccessQueue();
         return var1 == this.head ? null : var1;
      }

      public LocalCache.ReferenceEntry<K, V> poll() {
         LocalCache.ReferenceEntry var1 = this.head.getNextInAccessQueue();
         if (var1 == this.head) {
            return null;
         } else {
            this.remove(var1);
            return var1;
         }
      }

      public boolean remove(Object var1) {
         LocalCache.ReferenceEntry var2 = (LocalCache.ReferenceEntry)var1;
         LocalCache.ReferenceEntry var3 = var2.getPreviousInAccessQueue();
         LocalCache.ReferenceEntry var4 = var2.getNextInAccessQueue();
         LocalCache.connectAccessOrder(var3, var4);
         LocalCache.nullifyAccessOrder(var2);
         return var4 != LocalCache.NullEntry.INSTANCE;
      }

      public boolean contains(Object var1) {
         LocalCache.ReferenceEntry var2 = (LocalCache.ReferenceEntry)var1;
         return var2.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE;
      }

      public boolean isEmpty() {
         return this.head.getNextInAccessQueue() == this.head;
      }

      public int size() {
         int var1 = 0;

         for(LocalCache.ReferenceEntry var2 = this.head.getNextInAccessQueue(); var2 != this.head; var2 = var2.getNextInAccessQueue()) {
            ++var1;
         }

         return var1;
      }

      public void clear() {
         LocalCache.ReferenceEntry var2;
         for(LocalCache.ReferenceEntry var1 = this.head.getNextInAccessQueue(); var1 != this.head; var1 = var2) {
            var2 = var1.getNextInAccessQueue();
            LocalCache.nullifyAccessOrder(var1);
         }

         this.head.setNextInAccessQueue(this.head);
         this.head.setPreviousInAccessQueue(this.head);
      }

      public Iterator<LocalCache.ReferenceEntry<K, V>> iterator() {
         return new AbstractSequentialIterator<LocalCache.ReferenceEntry<K, V>>(this.peek()) {
            protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> var1) {
               LocalCache.ReferenceEntry var2 = var1.getNextInAccessQueue();
               return var2 == AccessQueue.this.head ? null : var2;
            }
         };
      }
   }

   static final class WriteQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>> {
      final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>() {
         LocalCache.ReferenceEntry<K, V> nextWrite = this;
         LocalCache.ReferenceEntry<K, V> previousWrite = this;

         public long getWriteTime() {
            return 9223372036854775807L;
         }

         public void setWriteTime(long var1) {
         }

         public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
         }

         public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
            this.nextWrite = var1;
         }

         public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
         }

         public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
            this.previousWrite = var1;
         }
      };

      WriteQueue() {
         super();
      }

      public boolean offer(LocalCache.ReferenceEntry<K, V> var1) {
         LocalCache.connectWriteOrder(var1.getPreviousInWriteQueue(), var1.getNextInWriteQueue());
         LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), var1);
         LocalCache.connectWriteOrder(var1, this.head);
         return true;
      }

      public LocalCache.ReferenceEntry<K, V> peek() {
         LocalCache.ReferenceEntry var1 = this.head.getNextInWriteQueue();
         return var1 == this.head ? null : var1;
      }

      public LocalCache.ReferenceEntry<K, V> poll() {
         LocalCache.ReferenceEntry var1 = this.head.getNextInWriteQueue();
         if (var1 == this.head) {
            return null;
         } else {
            this.remove(var1);
            return var1;
         }
      }

      public boolean remove(Object var1) {
         LocalCache.ReferenceEntry var2 = (LocalCache.ReferenceEntry)var1;
         LocalCache.ReferenceEntry var3 = var2.getPreviousInWriteQueue();
         LocalCache.ReferenceEntry var4 = var2.getNextInWriteQueue();
         LocalCache.connectWriteOrder(var3, var4);
         LocalCache.nullifyWriteOrder(var2);
         return var4 != LocalCache.NullEntry.INSTANCE;
      }

      public boolean contains(Object var1) {
         LocalCache.ReferenceEntry var2 = (LocalCache.ReferenceEntry)var1;
         return var2.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE;
      }

      public boolean isEmpty() {
         return this.head.getNextInWriteQueue() == this.head;
      }

      public int size() {
         int var1 = 0;

         for(LocalCache.ReferenceEntry var2 = this.head.getNextInWriteQueue(); var2 != this.head; var2 = var2.getNextInWriteQueue()) {
            ++var1;
         }

         return var1;
      }

      public void clear() {
         LocalCache.ReferenceEntry var2;
         for(LocalCache.ReferenceEntry var1 = this.head.getNextInWriteQueue(); var1 != this.head; var1 = var2) {
            var2 = var1.getNextInWriteQueue();
            LocalCache.nullifyWriteOrder(var1);
         }

         this.head.setNextInWriteQueue(this.head);
         this.head.setPreviousInWriteQueue(this.head);
      }

      public Iterator<LocalCache.ReferenceEntry<K, V>> iterator() {
         return new AbstractSequentialIterator<LocalCache.ReferenceEntry<K, V>>(this.peek()) {
            protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> var1) {
               LocalCache.ReferenceEntry var2 = var1.getNextInWriteQueue();
               return var2 == WriteQueue.this.head ? null : var2;
            }
         };
      }
   }

   static class LoadingValueReference<K, V> implements LocalCache.ValueReference<K, V> {
      volatile LocalCache.ValueReference<K, V> oldValue;
      final SettableFuture<V> futureValue;
      final Stopwatch stopwatch;

      public LoadingValueReference() {
         this((LocalCache.ValueReference)null);
      }

      public LoadingValueReference(LocalCache.ValueReference<K, V> var1) {
         super();
         this.futureValue = SettableFuture.create();
         this.stopwatch = Stopwatch.createUnstarted();
         this.oldValue = var1 == null ? LocalCache.unset() : var1;
      }

      public boolean isLoading() {
         return true;
      }

      public boolean isActive() {
         return this.oldValue.isActive();
      }

      public int getWeight() {
         return this.oldValue.getWeight();
      }

      public boolean set(@Nullable V var1) {
         return this.futureValue.set(var1);
      }

      public boolean setException(Throwable var1) {
         return this.futureValue.setException(var1);
      }

      private ListenableFuture<V> fullyFailedFuture(Throwable var1) {
         return Futures.immediateFailedFuture(var1);
      }

      public void notifyNewValue(@Nullable V var1) {
         if (var1 != null) {
            this.set(var1);
         } else {
            this.oldValue = LocalCache.unset();
         }

      }

      public ListenableFuture<V> loadFuture(K var1, CacheLoader<? super K, V> var2) {
         Object var4;
         try {
            this.stopwatch.start();
            Object var3 = this.oldValue.get();
            if (var3 == null) {
               var4 = var2.load(var1);
               return (ListenableFuture)(this.set(var4) ? this.futureValue : Futures.immediateFuture(var4));
            } else {
               ListenableFuture var6 = var2.reload(var1, var3);
               return var6 == null ? Futures.immediateFuture((Object)null) : Futures.transform(var6, new com.google.common.base.Function<V, V>() {
                  public V apply(V var1) {
                     LoadingValueReference.this.set(var1);
                     return var1;
                  }
               });
            }
         } catch (Throwable var5) {
            var4 = this.setException(var5) ? this.futureValue : this.fullyFailedFuture(var5);
            if (var5 instanceof InterruptedException) {
               Thread.currentThread().interrupt();
            }

            return (ListenableFuture)var4;
         }
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         this.stopwatch.start();

         Object var3;
         try {
            var3 = this.oldValue.waitForValue();
         } catch (ExecutionException var5) {
            var3 = null;
         }

         Object var4 = var2.apply(var1, var3);
         this.set(var4);
         return var4;
      }

      public long elapsedNanos() {
         return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
      }

      public V waitForValue() throws ExecutionException {
         return Uninterruptibles.getUninterruptibly(this.futureValue);
      }

      public V get() {
         return this.oldValue.get();
      }

      public LocalCache.ValueReference<K, V> getOldValue() {
         return this.oldValue;
      }

      public LocalCache.ReferenceEntry<K, V> getEntry() {
         return null;
      }

      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, @Nullable V var2, LocalCache.ReferenceEntry<K, V> var3) {
         return this;
      }
   }

   static class Segment<K, V> extends ReentrantLock {
      @Weak
      final LocalCache<K, V> map;
      volatile int count;
      @GuardedBy("this")
      long totalWeight;
      int modCount;
      int threshold;
      volatile AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table;
      final long maxSegmentWeight;
      final ReferenceQueue<K> keyReferenceQueue;
      final ReferenceQueue<V> valueReferenceQueue;
      final Queue<LocalCache.ReferenceEntry<K, V>> recencyQueue;
      final AtomicInteger readCount = new AtomicInteger();
      @GuardedBy("this")
      final Queue<LocalCache.ReferenceEntry<K, V>> writeQueue;
      @GuardedBy("this")
      final Queue<LocalCache.ReferenceEntry<K, V>> accessQueue;
      final AbstractCache.StatsCounter statsCounter;

      Segment(LocalCache<K, V> var1, int var2, long var3, AbstractCache.StatsCounter var5) {
         super();
         this.map = var1;
         this.maxSegmentWeight = var3;
         this.statsCounter = (AbstractCache.StatsCounter)Preconditions.checkNotNull(var5);
         this.initTable(this.newEntryArray(var2));
         this.keyReferenceQueue = var1.usesKeyReferences() ? new ReferenceQueue() : null;
         this.valueReferenceQueue = var1.usesValueReferences() ? new ReferenceQueue() : null;
         this.recencyQueue = (Queue)(var1.usesAccessQueue() ? new ConcurrentLinkedQueue() : LocalCache.discardingQueue());
         this.writeQueue = (Queue)(var1.usesWriteQueue() ? new LocalCache.WriteQueue() : LocalCache.discardingQueue());
         this.accessQueue = (Queue)(var1.usesAccessQueue() ? new LocalCache.AccessQueue() : LocalCache.discardingQueue());
      }

      AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> newEntryArray(int var1) {
         return new AtomicReferenceArray(var1);
      }

      void initTable(AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> var1) {
         this.threshold = var1.length() * 3 / 4;
         if (!this.map.customWeigher() && (long)this.threshold == this.maxSegmentWeight) {
            ++this.threshold;
         }

         this.table = var1;
      }

      @GuardedBy("this")
      LocalCache.ReferenceEntry<K, V> newEntry(K var1, int var2, @Nullable LocalCache.ReferenceEntry<K, V> var3) {
         return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull(var1), var2, var3);
      }

      @GuardedBy("this")
      LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.ReferenceEntry<K, V> var1, LocalCache.ReferenceEntry<K, V> var2) {
         if (var1.getKey() == null) {
            return null;
         } else {
            LocalCache.ValueReference var3 = var1.getValueReference();
            Object var4 = var3.get();
            if (var4 == null && var3.isActive()) {
               return null;
            } else {
               LocalCache.ReferenceEntry var5 = this.map.entryFactory.copyEntry(this, var1, var2);
               var5.setValueReference(var3.copyFor(this.valueReferenceQueue, var4, var5));
               return var5;
            }
         }
      }

      @GuardedBy("this")
      void setValue(LocalCache.ReferenceEntry<K, V> var1, K var2, V var3, long var4) {
         LocalCache.ValueReference var6 = var1.getValueReference();
         int var7 = this.map.weigher.weigh(var2, var3);
         Preconditions.checkState(var7 >= 0, "Weights must be non-negative");
         LocalCache.ValueReference var8 = this.map.valueStrength.referenceValue(this, var1, var3, var7);
         var1.setValueReference(var8);
         this.recordWrite(var1, var7, var4);
         var6.notifyNewValue(var3);
      }

      V get(K var1, int var2, CacheLoader<? super K, V> var3) throws ExecutionException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var3);

         try {
            if (this.count != 0) {
               LocalCache.ReferenceEntry var4 = this.getEntry(var1, var2);
               if (var4 != null) {
                  long var16 = this.map.ticker.read();
                  Object var7 = this.getLiveValue(var4, var16);
                  if (var7 != null) {
                     this.recordRead(var4, var16);
                     this.statsCounter.recordHits(1);
                     Object var17 = this.scheduleRefresh(var4, var1, var2, var7, var16, var3);
                     return var17;
                  }

                  LocalCache.ValueReference var8 = var4.getValueReference();
                  if (var8.isLoading()) {
                     Object var9 = this.waitForLoadingValue(var4, var1, var8);
                     return var9;
                  }
               }
            }

            Object var15 = this.lockedGetOrLoad(var1, var2, var3);
            return var15;
         } catch (ExecutionException var13) {
            Throwable var5 = var13.getCause();
            if (var5 instanceof Error) {
               throw new ExecutionError((Error)var5);
            } else if (var5 instanceof RuntimeException) {
               throw new UncheckedExecutionException(var5);
            } else {
               throw var13;
            }
         } finally {
            this.postReadCleanup();
         }
      }

      V lockedGetOrLoad(K var1, int var2, CacheLoader<? super K, V> var3) throws ExecutionException {
         LocalCache.ValueReference var5 = null;
         LocalCache.LoadingValueReference var6 = null;
         boolean var7 = true;
         this.lock();

         LocalCache.ReferenceEntry var4;
         try {
            long var8 = this.map.ticker.read();
            this.preWriteCleanup(var8);
            int var10 = this.count - 1;
            AtomicReferenceArray var11 = this.table;
            int var12 = var2 & var11.length() - 1;
            LocalCache.ReferenceEntry var13 = (LocalCache.ReferenceEntry)var11.get(var12);

            for(var4 = var13; var4 != null; var4 = var4.getNext()) {
               Object var14 = var4.getKey();
               if (var4.getHash() == var2 && var14 != null && this.map.keyEquivalence.equivalent(var1, var14)) {
                  var5 = var4.getValueReference();
                  if (var5.isLoading()) {
                     var7 = false;
                  } else {
                     Object var15 = var5.get();
                     if (var15 == null) {
                        this.enqueueNotification(var14, var2, var15, var5.getWeight(), RemovalCause.COLLECTED);
                     } else {
                        if (!this.map.isExpired(var4, var8)) {
                           this.recordLockedRead(var4, var8);
                           this.statsCounter.recordHits(1);
                           Object var16 = var15;
                           return var16;
                        }

                        this.enqueueNotification(var14, var2, var15, var5.getWeight(), RemovalCause.EXPIRED);
                     }

                     this.writeQueue.remove(var4);
                     this.accessQueue.remove(var4);
                     this.count = var10;
                  }
                  break;
               }
            }

            if (var7) {
               var6 = new LocalCache.LoadingValueReference();
               if (var4 == null) {
                  var4 = this.newEntry(var1, var2, var13);
                  var4.setValueReference(var6);
                  var11.set(var12, var4);
               } else {
                  var4.setValueReference(var6);
               }
            }
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }

         if (var7) {
            Object var9;
            try {
               synchronized(var4) {
                  var9 = this.loadSync(var1, var2, var6, var3);
               }
            } finally {
               this.statsCounter.recordMisses(1);
            }

            return var9;
         } else {
            return this.waitForLoadingValue(var4, var1, var5);
         }
      }

      V waitForLoadingValue(LocalCache.ReferenceEntry<K, V> var1, K var2, LocalCache.ValueReference<K, V> var3) throws ExecutionException {
         if (!var3.isLoading()) {
            throw new AssertionError();
         } else {
            Preconditions.checkState(!Thread.holdsLock(var1), "Recursive load of: %s", var2);

            Object var7;
            try {
               Object var4 = var3.waitForValue();
               if (var4 == null) {
                  throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + var2 + ".");
               }

               long var5 = this.map.ticker.read();
               this.recordRead(var1, var5);
               var7 = var4;
            } finally {
               this.statsCounter.recordMisses(1);
            }

            return var7;
         }
      }

      V compute(K var1, int var2, BiFunction<? super K, ? super V, ? extends V> var3) {
         LocalCache.ValueReference var5 = null;
         LocalCache.LoadingValueReference var6 = null;
         boolean var7 = true;
         this.lock();

         LocalCache.ReferenceEntry var4;
         try {
            long var8 = this.map.ticker.read();
            this.preWriteCleanup(var8);
            AtomicReferenceArray var10 = this.table;
            int var11 = var2 & var10.length() - 1;
            LocalCache.ReferenceEntry var12 = (LocalCache.ReferenceEntry)var10.get(var11);

            for(var4 = var12; var4 != null; var4 = var4.getNext()) {
               Object var13 = var4.getKey();
               if (var4.getHash() == var2 && var13 != null && this.map.keyEquivalence.equivalent(var1, var13)) {
                  var5 = var4.getValueReference();
                  if (this.map.isExpired(var4, var8)) {
                     this.enqueueNotification(var13, var2, var5.get(), var5.getWeight(), RemovalCause.EXPIRED);
                  }

                  this.writeQueue.remove(var4);
                  this.accessQueue.remove(var4);
                  var7 = false;
                  break;
               }
            }

            var6 = new LocalCache.LoadingValueReference(var5);
            if (var4 == null) {
               var7 = true;
               var4 = this.newEntry(var1, var2, var12);
               var4.setValueReference(var6);
               var10.set(var11, var4);
            } else {
               var4.setValueReference(var6);
            }
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }

         synchronized(var4) {
            Object var9 = var6.compute(var1, var3);
            if (var9 != null) {
               Object var10000;
               try {
                  var10000 = this.getAndRecordStats(var1, var2, var6, Futures.immediateFuture(var9));
               } catch (ExecutionException var25) {
                  throw new AssertionError("impossible; Futures.immediateFuture can't throw");
               }

               return var10000;
            } else if (var7) {
               this.removeLoadingValue(var1, var2, var6);
               return null;
            } else {
               this.lock();

               try {
                  this.removeEntry(var4, var2, RemovalCause.EXPLICIT);
               } finally {
                  this.unlock();
               }

               return null;
            }
         }
      }

      V loadSync(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3, CacheLoader<? super K, V> var4) throws ExecutionException {
         ListenableFuture var5 = var3.loadFuture(var1, var4);
         return this.getAndRecordStats(var1, var2, var3, var5);
      }

      ListenableFuture<V> loadAsync(final K var1, final int var2, final LocalCache.LoadingValueReference<K, V> var3, CacheLoader<? super K, V> var4) {
         final ListenableFuture var5 = var3.loadFuture(var1, var4);
         var5.addListener(new Runnable() {
            public void run() {
               try {
                  Segment.this.getAndRecordStats(var1, var2, var3, var5);
               } catch (Throwable var2x) {
                  LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", var2x);
                  var3.setException(var2x);
               }

            }
         }, MoreExecutors.directExecutor());
         return var5;
      }

      V getAndRecordStats(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3, ListenableFuture<V> var4) throws ExecutionException {
         Object var5 = null;

         Object var6;
         try {
            var5 = Uninterruptibles.getUninterruptibly(var4);
            if (var5 == null) {
               throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + var1 + ".");
            }

            this.statsCounter.recordLoadSuccess(var3.elapsedNanos());
            this.storeLoadedValue(var1, var2, var3, var5);
            var6 = var5;
         } finally {
            if (var5 == null) {
               this.statsCounter.recordLoadException(var3.elapsedNanos());
               this.removeLoadingValue(var1, var2, var3);
            }

         }

         return var6;
      }

      V scheduleRefresh(LocalCache.ReferenceEntry<K, V> var1, K var2, int var3, V var4, long var5, CacheLoader<? super K, V> var7) {
         if (this.map.refreshes() && var5 - var1.getWriteTime() > this.map.refreshNanos && !var1.getValueReference().isLoading()) {
            Object var8 = this.refresh(var2, var3, var7, true);
            if (var8 != null) {
               return var8;
            }
         }

         return var4;
      }

      @Nullable
      V refresh(K var1, int var2, CacheLoader<? super K, V> var3, boolean var4) {
         LocalCache.LoadingValueReference var5 = this.insertLoadingValueReference(var1, var2, var4);
         if (var5 == null) {
            return null;
         } else {
            ListenableFuture var6 = this.loadAsync(var1, var2, var5, var3);
            if (var6.isDone()) {
               try {
                  return Uninterruptibles.getUninterruptibly(var6);
               } catch (Throwable var8) {
               }
            }

            return null;
         }
      }

      @Nullable
      LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K var1, int var2, boolean var3) {
         LocalCache.ReferenceEntry var4 = null;
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            AtomicReferenceArray var7 = this.table;
            int var8 = var2 & var7.length() - 1;
            LocalCache.ReferenceEntry var9 = (LocalCache.ReferenceEntry)var7.get(var8);

            for(var4 = var9; var4 != null; var4 = var4.getNext()) {
               Object var10 = var4.getKey();
               if (var4.getHash() == var2 && var10 != null && this.map.keyEquivalence.equivalent(var1, var10)) {
                  LocalCache.ValueReference var11 = var4.getValueReference();
                  LocalCache.LoadingValueReference var12;
                  if (!var11.isLoading() && (!var3 || var5 - var4.getWriteTime() >= this.map.refreshNanos)) {
                     ++this.modCount;
                     var12 = new LocalCache.LoadingValueReference(var11);
                     var4.setValueReference(var12);
                     LocalCache.LoadingValueReference var13 = var12;
                     return var13;
                  }

                  var12 = null;
                  return var12;
               }
            }

            ++this.modCount;
            LocalCache.LoadingValueReference var17 = new LocalCache.LoadingValueReference();
            var4 = this.newEntry(var1, var2, var9);
            var4.setValueReference(var17);
            var7.set(var8, var4);
            LocalCache.LoadingValueReference var18 = var17;
            return var18;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      void tryDrainReferenceQueues() {
         if (this.tryLock()) {
            try {
               this.drainReferenceQueues();
            } finally {
               this.unlock();
            }
         }

      }

      @GuardedBy("this")
      void drainReferenceQueues() {
         if (this.map.usesKeyReferences()) {
            this.drainKeyReferenceQueue();
         }

         if (this.map.usesValueReferences()) {
            this.drainValueReferenceQueue();
         }

      }

      @GuardedBy("this")
      void drainKeyReferenceQueue() {
         int var2 = 0;

         Reference var1;
         while((var1 = this.keyReferenceQueue.poll()) != null) {
            LocalCache.ReferenceEntry var3 = (LocalCache.ReferenceEntry)var1;
            this.map.reclaimKey(var3);
            ++var2;
            if (var2 == 16) {
               break;
            }
         }

      }

      @GuardedBy("this")
      void drainValueReferenceQueue() {
         int var2 = 0;

         Reference var1;
         while((var1 = this.valueReferenceQueue.poll()) != null) {
            LocalCache.ValueReference var3 = (LocalCache.ValueReference)var1;
            this.map.reclaimValue(var3);
            ++var2;
            if (var2 == 16) {
               break;
            }
         }

      }

      void clearReferenceQueues() {
         if (this.map.usesKeyReferences()) {
            this.clearKeyReferenceQueue();
         }

         if (this.map.usesValueReferences()) {
            this.clearValueReferenceQueue();
         }

      }

      void clearKeyReferenceQueue() {
         while(this.keyReferenceQueue.poll() != null) {
         }

      }

      void clearValueReferenceQueue() {
         while(this.valueReferenceQueue.poll() != null) {
         }

      }

      void recordRead(LocalCache.ReferenceEntry<K, V> var1, long var2) {
         if (this.map.recordsAccess()) {
            var1.setAccessTime(var2);
         }

         this.recencyQueue.add(var1);
      }

      @GuardedBy("this")
      void recordLockedRead(LocalCache.ReferenceEntry<K, V> var1, long var2) {
         if (this.map.recordsAccess()) {
            var1.setAccessTime(var2);
         }

         this.accessQueue.add(var1);
      }

      @GuardedBy("this")
      void recordWrite(LocalCache.ReferenceEntry<K, V> var1, int var2, long var3) {
         this.drainRecencyQueue();
         this.totalWeight += (long)var2;
         if (this.map.recordsAccess()) {
            var1.setAccessTime(var3);
         }

         if (this.map.recordsWrite()) {
            var1.setWriteTime(var3);
         }

         this.accessQueue.add(var1);
         this.writeQueue.add(var1);
      }

      @GuardedBy("this")
      void drainRecencyQueue() {
         LocalCache.ReferenceEntry var1;
         while((var1 = (LocalCache.ReferenceEntry)this.recencyQueue.poll()) != null) {
            if (this.accessQueue.contains(var1)) {
               this.accessQueue.add(var1);
            }
         }

      }

      void tryExpireEntries(long var1) {
         if (this.tryLock()) {
            try {
               this.expireEntries(var1);
            } finally {
               this.unlock();
            }
         }

      }

      @GuardedBy("this")
      void expireEntries(long var1) {
         this.drainRecencyQueue();

         LocalCache.ReferenceEntry var3;
         while((var3 = (LocalCache.ReferenceEntry)this.writeQueue.peek()) != null && this.map.isExpired(var3, var1)) {
            if (!this.removeEntry(var3, var3.getHash(), RemovalCause.EXPIRED)) {
               throw new AssertionError();
            }
         }

         while((var3 = (LocalCache.ReferenceEntry)this.accessQueue.peek()) != null && this.map.isExpired(var3, var1)) {
            if (!this.removeEntry(var3, var3.getHash(), RemovalCause.EXPIRED)) {
               throw new AssertionError();
            }
         }

      }

      @GuardedBy("this")
      void enqueueNotification(@Nullable K var1, int var2, @Nullable V var3, int var4, RemovalCause var5) {
         this.totalWeight -= (long)var4;
         if (var5.wasEvicted()) {
            this.statsCounter.recordEviction();
         }

         if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
            RemovalNotification var6 = RemovalNotification.create(var1, var3, var5);
            this.map.removalNotificationQueue.offer(var6);
         }

      }

      @GuardedBy("this")
      void evictEntries(LocalCache.ReferenceEntry<K, V> var1) {
         if (this.map.evictsBySize()) {
            this.drainRecencyQueue();
            if ((long)var1.getValueReference().getWeight() > this.maxSegmentWeight && !this.removeEntry(var1, var1.getHash(), RemovalCause.SIZE)) {
               throw new AssertionError();
            } else {
               LocalCache.ReferenceEntry var2;
               do {
                  if (this.totalWeight <= this.maxSegmentWeight) {
                     return;
                  }

                  var2 = this.getNextEvictable();
               } while(this.removeEntry(var2, var2.getHash(), RemovalCause.SIZE));

               throw new AssertionError();
            }
         }
      }

      @GuardedBy("this")
      LocalCache.ReferenceEntry<K, V> getNextEvictable() {
         Iterator var1 = this.accessQueue.iterator();

         LocalCache.ReferenceEntry var2;
         int var3;
         do {
            if (!var1.hasNext()) {
               throw new AssertionError();
            }

            var2 = (LocalCache.ReferenceEntry)var1.next();
            var3 = var2.getValueReference().getWeight();
         } while(var3 <= 0);

         return var2;
      }

      LocalCache.ReferenceEntry<K, V> getFirst(int var1) {
         AtomicReferenceArray var2 = this.table;
         return (LocalCache.ReferenceEntry)var2.get(var1 & var2.length() - 1);
      }

      @Nullable
      LocalCache.ReferenceEntry<K, V> getEntry(Object var1, int var2) {
         for(LocalCache.ReferenceEntry var3 = this.getFirst(var2); var3 != null; var3 = var3.getNext()) {
            if (var3.getHash() == var2) {
               Object var4 = var3.getKey();
               if (var4 == null) {
                  this.tryDrainReferenceQueues();
               } else if (this.map.keyEquivalence.equivalent(var1, var4)) {
                  return var3;
               }
            }
         }

         return null;
      }

      @Nullable
      LocalCache.ReferenceEntry<K, V> getLiveEntry(Object var1, int var2, long var3) {
         LocalCache.ReferenceEntry var5 = this.getEntry(var1, var2);
         if (var5 == null) {
            return null;
         } else if (this.map.isExpired(var5, var3)) {
            this.tryExpireEntries(var3);
            return null;
         } else {
            return var5;
         }
      }

      V getLiveValue(LocalCache.ReferenceEntry<K, V> var1, long var2) {
         if (var1.getKey() == null) {
            this.tryDrainReferenceQueues();
            return null;
         } else {
            Object var4 = var1.getValueReference().get();
            if (var4 == null) {
               this.tryDrainReferenceQueues();
               return null;
            } else if (this.map.isExpired(var1, var2)) {
               this.tryExpireEntries(var2);
               return null;
            } else {
               return var4;
            }
         }
      }

      @Nullable
      V get(Object var1, int var2) {
         try {
            if (this.count != 0) {
               long var3 = this.map.ticker.read();
               LocalCache.ReferenceEntry var5 = this.getLiveEntry(var1, var2, var3);
               Object var6;
               if (var5 == null) {
                  var6 = null;
                  return var6;
               }

               var6 = var5.getValueReference().get();
               if (var6 != null) {
                  this.recordRead(var5, var3);
                  Object var7 = this.scheduleRefresh(var5, var5.getKey(), var2, var6, var3, this.map.defaultLoader);
                  return var7;
               }

               this.tryDrainReferenceQueues();
            }

            Object var11 = null;
            return var11;
         } finally {
            this.postReadCleanup();
         }
      }

      boolean containsKey(Object var1, int var2) {
         boolean var3;
         try {
            if (this.count != 0) {
               long var10 = this.map.ticker.read();
               LocalCache.ReferenceEntry var5 = this.getLiveEntry(var1, var2, var10);
               boolean var6;
               if (var5 == null) {
                  var6 = false;
                  return var6;
               }

               var6 = var5.getValueReference().get() != null;
               return var6;
            }

            var3 = false;
         } finally {
            this.postReadCleanup();
         }

         return var3;
      }

      @VisibleForTesting
      boolean containsValue(Object var1) {
         boolean var13;
         try {
            if (this.count != 0) {
               long var2 = this.map.ticker.read();
               AtomicReferenceArray var4 = this.table;
               int var5 = var4.length();

               for(int var6 = 0; var6 < var5; ++var6) {
                  for(LocalCache.ReferenceEntry var7 = (LocalCache.ReferenceEntry)var4.get(var6); var7 != null; var7 = var7.getNext()) {
                     Object var8 = this.getLiveValue(var7, var2);
                     if (var8 != null && this.map.valueEquivalence.equivalent(var1, var8)) {
                        boolean var9 = true;
                        return var9;
                     }
                  }
               }
            }

            var13 = false;
         } finally {
            this.postReadCleanup();
         }

         return var13;
      }

      @Nullable
      V put(K var1, int var2, V var3, boolean var4) {
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            int var7 = this.count + 1;
            if (var7 > this.threshold) {
               this.expand();
               var7 = this.count + 1;
            }

            AtomicReferenceArray var8 = this.table;
            int var9 = var2 & var8.length() - 1;
            LocalCache.ReferenceEntry var10 = (LocalCache.ReferenceEntry)var8.get(var9);

            LocalCache.ReferenceEntry var11;
            Object var12;
            for(var11 = var10; var11 != null; var11 = var11.getNext()) {
               var12 = var11.getKey();
               if (var11.getHash() == var2 && var12 != null && this.map.keyEquivalence.equivalent(var1, var12)) {
                  LocalCache.ValueReference var13 = var11.getValueReference();
                  Object var14 = var13.get();
                  Object var15;
                  if (var14 == null) {
                     ++this.modCount;
                     if (var13.isActive()) {
                        this.enqueueNotification(var1, var2, var14, var13.getWeight(), RemovalCause.COLLECTED);
                        this.setValue(var11, var1, var3, var5);
                        var7 = this.count;
                     } else {
                        this.setValue(var11, var1, var3, var5);
                        var7 = this.count + 1;
                     }

                     this.count = var7;
                     this.evictEntries(var11);
                     var15 = null;
                     return var15;
                  }

                  if (var4) {
                     this.recordLockedRead(var11, var5);
                     var15 = var14;
                     return var15;
                  }

                  ++this.modCount;
                  this.enqueueNotification(var1, var2, var14, var13.getWeight(), RemovalCause.REPLACED);
                  this.setValue(var11, var1, var3, var5);
                  this.evictEntries(var11);
                  var15 = var14;
                  return var15;
               }
            }

            ++this.modCount;
            var11 = this.newEntry(var1, var2, var10);
            this.setValue(var11, var1, var3, var5);
            var8.set(var9, var11);
            var7 = this.count + 1;
            this.count = var7;
            this.evictEntries(var11);
            var12 = null;
            return var12;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @GuardedBy("this")
      void expand() {
         AtomicReferenceArray var1 = this.table;
         int var2 = var1.length();
         if (var2 < 1073741824) {
            int var3 = this.count;
            AtomicReferenceArray var4 = this.newEntryArray(var2 << 1);
            this.threshold = var4.length() * 3 / 4;
            int var5 = var4.length() - 1;

            for(int var6 = 0; var6 < var2; ++var6) {
               LocalCache.ReferenceEntry var7 = (LocalCache.ReferenceEntry)var1.get(var6);
               if (var7 != null) {
                  LocalCache.ReferenceEntry var8 = var7.getNext();
                  int var9 = var7.getHash() & var5;
                  if (var8 == null) {
                     var4.set(var9, var7);
                  } else {
                     LocalCache.ReferenceEntry var10 = var7;
                     int var11 = var9;

                     LocalCache.ReferenceEntry var12;
                     int var13;
                     for(var12 = var8; var12 != null; var12 = var12.getNext()) {
                        var13 = var12.getHash() & var5;
                        if (var13 != var11) {
                           var11 = var13;
                           var10 = var12;
                        }
                     }

                     var4.set(var11, var10);

                     for(var12 = var7; var12 != var10; var12 = var12.getNext()) {
                        var13 = var12.getHash() & var5;
                        LocalCache.ReferenceEntry var14 = (LocalCache.ReferenceEntry)var4.get(var13);
                        LocalCache.ReferenceEntry var15 = this.copyEntry(var12, var14);
                        if (var15 != null) {
                           var4.set(var13, var15);
                        } else {
                           this.removeCollectedEntry(var12);
                           --var3;
                        }
                     }
                  }
               }
            }

            this.table = var4;
            this.count = var3;
         }
      }

      boolean replace(K var1, int var2, V var3, V var4) {
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            AtomicReferenceArray var7 = this.table;
            int var8 = var2 & var7.length() - 1;
            LocalCache.ReferenceEntry var9 = (LocalCache.ReferenceEntry)var7.get(var8);

            for(LocalCache.ReferenceEntry var10 = var9; var10 != null; var10 = var10.getNext()) {
               Object var11 = var10.getKey();
               if (var10.getHash() == var2 && var11 != null && this.map.keyEquivalence.equivalent(var1, var11)) {
                  LocalCache.ValueReference var12 = var10.getValueReference();
                  Object var13 = var12.get();
                  boolean var20;
                  if (var13 != null) {
                     if (this.map.valueEquivalence.equivalent(var3, var13)) {
                        ++this.modCount;
                        this.enqueueNotification(var1, var2, var13, var12.getWeight(), RemovalCause.REPLACED);
                        this.setValue(var10, var1, var4, var5);
                        this.evictEntries(var10);
                        var20 = true;
                        return var20;
                     }

                     this.recordLockedRead(var10, var5);
                     var20 = false;
                     return var20;
                  }

                  if (var12.isActive()) {
                     int var14 = this.count - 1;
                     ++this.modCount;
                     LocalCache.ReferenceEntry var15 = this.removeValueFromChain(var9, var10, var11, var2, var13, var12, RemovalCause.COLLECTED);
                     var14 = this.count - 1;
                     var7.set(var8, var15);
                     this.count = var14;
                  }

                  var20 = false;
                  return var20;
               }
            }

            boolean var19 = false;
            return var19;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @Nullable
      V replace(K var1, int var2, V var3) {
         this.lock();

         try {
            long var4 = this.map.ticker.read();
            this.preWriteCleanup(var4);
            AtomicReferenceArray var6 = this.table;
            int var7 = var2 & var6.length() - 1;
            LocalCache.ReferenceEntry var8 = (LocalCache.ReferenceEntry)var6.get(var7);

            LocalCache.ReferenceEntry var9;
            for(var9 = var8; var9 != null; var9 = var9.getNext()) {
               Object var10 = var9.getKey();
               if (var9.getHash() == var2 && var10 != null && this.map.keyEquivalence.equivalent(var1, var10)) {
                  LocalCache.ValueReference var11 = var9.getValueReference();
                  Object var12 = var11.get();
                  Object var18;
                  if (var12 != null) {
                     ++this.modCount;
                     this.enqueueNotification(var1, var2, var12, var11.getWeight(), RemovalCause.REPLACED);
                     this.setValue(var9, var1, var3, var4);
                     this.evictEntries(var9);
                     var18 = var12;
                     return var18;
                  }

                  if (var11.isActive()) {
                     int var13 = this.count - 1;
                     ++this.modCount;
                     LocalCache.ReferenceEntry var14 = this.removeValueFromChain(var8, var9, var10, var2, var12, var11, RemovalCause.COLLECTED);
                     var13 = this.count - 1;
                     var6.set(var7, var14);
                     this.count = var13;
                  }

                  var18 = null;
                  return var18;
               }
            }

            var9 = null;
            return var9;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @Nullable
      V remove(Object var1, int var2) {
         this.lock();

         try {
            long var3 = this.map.ticker.read();
            this.preWriteCleanup(var3);
            int var5 = this.count - 1;
            AtomicReferenceArray var6 = this.table;
            int var7 = var2 & var6.length() - 1;
            LocalCache.ReferenceEntry var8 = (LocalCache.ReferenceEntry)var6.get(var7);

            LocalCache.ReferenceEntry var9;
            for(var9 = var8; var9 != null; var9 = var9.getNext()) {
               Object var10 = var9.getKey();
               if (var9.getHash() == var2 && var10 != null && this.map.keyEquivalence.equivalent(var1, var10)) {
                  LocalCache.ValueReference var11 = var9.getValueReference();
                  Object var12 = var11.get();
                  RemovalCause var13;
                  LocalCache.ReferenceEntry var14;
                  if (var12 != null) {
                     var13 = RemovalCause.EXPLICIT;
                  } else {
                     if (!var11.isActive()) {
                        var14 = null;
                        return var14;
                     }

                     var13 = RemovalCause.COLLECTED;
                  }

                  ++this.modCount;
                  var14 = this.removeValueFromChain(var8, var9, var10, var2, var12, var11, var13);
                  var5 = this.count - 1;
                  var6.set(var7, var14);
                  this.count = var5;
                  Object var15 = var12;
                  return var15;
               }
            }

            var9 = null;
            return var9;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean storeLoadedValue(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3, V var4) {
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            int var7 = this.count + 1;
            if (var7 > this.threshold) {
               this.expand();
               var7 = this.count + 1;
            }

            AtomicReferenceArray var8 = this.table;
            int var9 = var2 & var8.length() - 1;
            LocalCache.ReferenceEntry var10 = (LocalCache.ReferenceEntry)var8.get(var9);

            LocalCache.ReferenceEntry var11;
            for(var11 = var10; var11 != null; var11 = var11.getNext()) {
               Object var12 = var11.getKey();
               if (var11.getHash() == var2 && var12 != null && this.map.keyEquivalence.equivalent(var1, var12)) {
                  LocalCache.ValueReference var13 = var11.getValueReference();
                  Object var14 = var13.get();
                  boolean var20;
                  if (var3 != var13 && (var14 != null || var13 == LocalCache.UNSET)) {
                     this.enqueueNotification(var1, var2, var4, 0, RemovalCause.REPLACED);
                     var20 = false;
                     return var20;
                  }

                  ++this.modCount;
                  if (var3.isActive()) {
                     RemovalCause var15 = var14 == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
                     this.enqueueNotification(var1, var2, var14, var3.getWeight(), var15);
                     --var7;
                  }

                  this.setValue(var11, var1, var4, var5);
                  this.count = var7;
                  this.evictEntries(var11);
                  var20 = true;
                  return var20;
               }
            }

            ++this.modCount;
            var11 = this.newEntry(var1, var2, var10);
            this.setValue(var11, var1, var4, var5);
            var8.set(var9, var11);
            this.count = var7;
            this.evictEntries(var11);
            boolean var19 = true;
            return var19;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean remove(Object var1, int var2, Object var3) {
         this.lock();

         try {
            long var4 = this.map.ticker.read();
            this.preWriteCleanup(var4);
            int var6 = this.count - 1;
            AtomicReferenceArray var7 = this.table;
            int var8 = var2 & var7.length() - 1;
            LocalCache.ReferenceEntry var9 = (LocalCache.ReferenceEntry)var7.get(var8);

            for(LocalCache.ReferenceEntry var10 = var9; var10 != null; var10 = var10.getNext()) {
               Object var11 = var10.getKey();
               if (var10.getHash() == var2 && var11 != null && this.map.keyEquivalence.equivalent(var1, var11)) {
                  LocalCache.ValueReference var12 = var10.getValueReference();
                  Object var13 = var12.get();
                  RemovalCause var14;
                  if (this.map.valueEquivalence.equivalent(var3, var13)) {
                     var14 = RemovalCause.EXPLICIT;
                  } else {
                     if (var13 != null || !var12.isActive()) {
                        boolean var21 = false;
                        return var21;
                     }

                     var14 = RemovalCause.COLLECTED;
                  }

                  ++this.modCount;
                  LocalCache.ReferenceEntry var15 = this.removeValueFromChain(var9, var10, var11, var2, var13, var12, var14);
                  var6 = this.count - 1;
                  var7.set(var8, var15);
                  this.count = var6;
                  boolean var16 = var14 == RemovalCause.EXPLICIT;
                  return var16;
               }
            }

            boolean var20 = false;
            return var20;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      void clear() {
         if (this.count != 0) {
            this.lock();

            try {
               long var1 = this.map.ticker.read();
               this.preWriteCleanup(var1);
               AtomicReferenceArray var3 = this.table;

               int var4;
               for(var4 = 0; var4 < var3.length(); ++var4) {
                  for(LocalCache.ReferenceEntry var5 = (LocalCache.ReferenceEntry)var3.get(var4); var5 != null; var5 = var5.getNext()) {
                     if (var5.getValueReference().isActive()) {
                        Object var6 = var5.getKey();
                        Object var7 = var5.getValueReference().get();
                        RemovalCause var8 = var6 != null && var7 != null ? RemovalCause.EXPLICIT : RemovalCause.COLLECTED;
                        this.enqueueNotification(var6, var5.getHash(), var7, var5.getValueReference().getWeight(), var8);
                     }
                  }
               }

               for(var4 = 0; var4 < var3.length(); ++var4) {
                  var3.set(var4, (Object)null);
               }

               this.clearReferenceQueues();
               this.writeQueue.clear();
               this.accessQueue.clear();
               this.readCount.set(0);
               ++this.modCount;
               this.count = 0;
            } finally {
               this.unlock();
               this.postWriteCleanup();
            }
         }

      }

      @Nullable
      @GuardedBy("this")
      LocalCache.ReferenceEntry<K, V> removeValueFromChain(LocalCache.ReferenceEntry<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, @Nullable K var3, int var4, V var5, LocalCache.ValueReference<K, V> var6, RemovalCause var7) {
         this.enqueueNotification(var3, var4, var5, var6.getWeight(), var7);
         this.writeQueue.remove(var2);
         this.accessQueue.remove(var2);
         if (var6.isLoading()) {
            var6.notifyNewValue((Object)null);
            return var1;
         } else {
            return this.removeEntryFromChain(var1, var2);
         }
      }

      @Nullable
      @GuardedBy("this")
      LocalCache.ReferenceEntry<K, V> removeEntryFromChain(LocalCache.ReferenceEntry<K, V> var1, LocalCache.ReferenceEntry<K, V> var2) {
         int var3 = this.count;
         LocalCache.ReferenceEntry var4 = var2.getNext();

         for(LocalCache.ReferenceEntry var5 = var1; var5 != var2; var5 = var5.getNext()) {
            LocalCache.ReferenceEntry var6 = this.copyEntry(var5, var4);
            if (var6 != null) {
               var4 = var6;
            } else {
               this.removeCollectedEntry(var5);
               --var3;
            }
         }

         this.count = var3;
         return var4;
      }

      @GuardedBy("this")
      void removeCollectedEntry(LocalCache.ReferenceEntry<K, V> var1) {
         this.enqueueNotification(var1.getKey(), var1.getHash(), var1.getValueReference().get(), var1.getValueReference().getWeight(), RemovalCause.COLLECTED);
         this.writeQueue.remove(var1);
         this.accessQueue.remove(var1);
      }

      boolean reclaimKey(LocalCache.ReferenceEntry<K, V> var1, int var2) {
         this.lock();

         boolean var13;
         try {
            int var3 = this.count - 1;
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            LocalCache.ReferenceEntry var6 = (LocalCache.ReferenceEntry)var4.get(var5);

            for(LocalCache.ReferenceEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               if (var7 == var1) {
                  ++this.modCount;
                  LocalCache.ReferenceEntry var8 = this.removeValueFromChain(var6, var7, var7.getKey(), var2, var7.getValueReference().get(), var7.getValueReference(), RemovalCause.COLLECTED);
                  var3 = this.count - 1;
                  var4.set(var5, var8);
                  this.count = var3;
                  boolean var9 = true;
                  return var9;
               }
            }

            var13 = false;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }

         return var13;
      }

      boolean reclaimValue(K var1, int var2, LocalCache.ValueReference<K, V> var3) {
         this.lock();

         try {
            int var4 = this.count - 1;
            AtomicReferenceArray var5 = this.table;
            int var6 = var2 & var5.length() - 1;
            LocalCache.ReferenceEntry var7 = (LocalCache.ReferenceEntry)var5.get(var6);

            for(LocalCache.ReferenceEntry var8 = var7; var8 != null; var8 = var8.getNext()) {
               Object var9 = var8.getKey();
               if (var8.getHash() == var2 && var9 != null && this.map.keyEquivalence.equivalent(var1, var9)) {
                  LocalCache.ValueReference var10 = var8.getValueReference();
                  if (var10 != var3) {
                     boolean var17 = false;
                     return var17;
                  }

                  ++this.modCount;
                  LocalCache.ReferenceEntry var11 = this.removeValueFromChain(var7, var8, var9, var2, var3.get(), var3, RemovalCause.COLLECTED);
                  var4 = this.count - 1;
                  var5.set(var6, var11);
                  this.count = var4;
                  boolean var12 = true;
                  return var12;
               }
            }

            boolean var16 = false;
            return var16;
         } finally {
            this.unlock();
            if (!this.isHeldByCurrentThread()) {
               this.postWriteCleanup();
            }

         }
      }

      boolean removeLoadingValue(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3) {
         this.lock();

         try {
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            LocalCache.ReferenceEntry var6 = (LocalCache.ReferenceEntry)var4.get(var5);

            for(LocalCache.ReferenceEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               Object var8 = var7.getKey();
               if (var7.getHash() == var2 && var8 != null && this.map.keyEquivalence.equivalent(var1, var8)) {
                  LocalCache.ValueReference var9 = var7.getValueReference();
                  boolean var10;
                  if (var9 == var3) {
                     if (var3.isActive()) {
                        var7.setValueReference(var3.getOldValue());
                     } else {
                        LocalCache.ReferenceEntry var14 = this.removeEntryFromChain(var6, var7);
                        var4.set(var5, var14);
                     }

                     var10 = true;
                     return var10;
                  }

                  var10 = false;
                  return var10;
               }
            }

            boolean var15 = false;
            return var15;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @VisibleForTesting
      @GuardedBy("this")
      boolean removeEntry(LocalCache.ReferenceEntry<K, V> var1, int var2, RemovalCause var3) {
         int var4 = this.count - 1;
         AtomicReferenceArray var5 = this.table;
         int var6 = var2 & var5.length() - 1;
         LocalCache.ReferenceEntry var7 = (LocalCache.ReferenceEntry)var5.get(var6);

         for(LocalCache.ReferenceEntry var8 = var7; var8 != null; var8 = var8.getNext()) {
            if (var8 == var1) {
               ++this.modCount;
               LocalCache.ReferenceEntry var9 = this.removeValueFromChain(var7, var8, var8.getKey(), var2, var8.getValueReference().get(), var8.getValueReference(), var3);
               var4 = this.count - 1;
               var5.set(var6, var9);
               this.count = var4;
               return true;
            }
         }

         return false;
      }

      void postReadCleanup() {
         if ((this.readCount.incrementAndGet() & 63) == 0) {
            this.cleanUp();
         }

      }

      @GuardedBy("this")
      void preWriteCleanup(long var1) {
         this.runLockedCleanup(var1);
      }

      void postWriteCleanup() {
         this.runUnlockedCleanup();
      }

      void cleanUp() {
         long var1 = this.map.ticker.read();
         this.runLockedCleanup(var1);
         this.runUnlockedCleanup();
      }

      void runLockedCleanup(long var1) {
         if (this.tryLock()) {
            try {
               this.drainReferenceQueues();
               this.expireEntries(var1);
               this.readCount.set(0);
            } finally {
               this.unlock();
            }
         }

      }

      void runUnlockedCleanup() {
         if (!this.isHeldByCurrentThread()) {
            this.map.processPendingNotifications();
         }

      }
   }

   static final class WeightedStrongValueReference<K, V> extends LocalCache.StrongValueReference<K, V> {
      final int weight;

      WeightedStrongValueReference(V var1, int var2) {
         super(var1);
         this.weight = var2;
      }

      public int getWeight() {
         return this.weight;
      }
   }

   static final class WeightedSoftValueReference<K, V> extends LocalCache.SoftValueReference<K, V> {
      final int weight;

      WeightedSoftValueReference(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3, int var4) {
         super(var1, var2, var3);
         this.weight = var4;
      }

      public int getWeight() {
         return this.weight;
      }

      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3) {
         return new LocalCache.WeightedSoftValueReference(var1, var2, var3, this.weight);
      }
   }

   static final class WeightedWeakValueReference<K, V> extends LocalCache.WeakValueReference<K, V> {
      final int weight;

      WeightedWeakValueReference(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3, int var4) {
         super(var1, var2, var3);
         this.weight = var4;
      }

      public int getWeight() {
         return this.weight;
      }

      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3) {
         return new LocalCache.WeightedWeakValueReference(var1, var2, var3, this.weight);
      }
   }

   static class StrongValueReference<K, V> implements LocalCache.ValueReference<K, V> {
      final V referent;

      StrongValueReference(V var1) {
         super();
         this.referent = var1;
      }

      public V get() {
         return this.referent;
      }

      public int getWeight() {
         return 1;
      }

      public LocalCache.ReferenceEntry<K, V> getEntry() {
         return null;
      }

      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3) {
         return this;
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return true;
      }

      public V waitForValue() {
         return this.get();
      }

      public void notifyNewValue(V var1) {
      }
   }

   static class SoftValueReference<K, V> extends SoftReference<V> implements LocalCache.ValueReference<K, V> {
      final LocalCache.ReferenceEntry<K, V> entry;

      SoftValueReference(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3) {
         super(var2, var1);
         this.entry = var3;
      }

      public int getWeight() {
         return 1;
      }

      public LocalCache.ReferenceEntry<K, V> getEntry() {
         return this.entry;
      }

      public void notifyNewValue(V var1) {
      }

      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3) {
         return new LocalCache.SoftValueReference(var1, var2, var3);
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return true;
      }

      public V waitForValue() {
         return this.get();
      }
   }

   static class WeakValueReference<K, V> extends WeakReference<V> implements LocalCache.ValueReference<K, V> {
      final LocalCache.ReferenceEntry<K, V> entry;

      WeakValueReference(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3) {
         super(var2, var1);
         this.entry = var3;
      }

      public int getWeight() {
         return 1;
      }

      public LocalCache.ReferenceEntry<K, V> getEntry() {
         return this.entry;
      }

      public void notifyNewValue(V var1) {
      }

      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, LocalCache.ReferenceEntry<K, V> var3) {
         return new LocalCache.WeakValueReference(var1, var2, var3);
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return true;
      }

      public V waitForValue() {
         return this.get();
      }
   }

   static final class WeakAccessWriteEntry<K, V> extends LocalCache.WeakEntry<K, V> {
      volatile long accessTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
      volatile long writeTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      WeakAccessWriteEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
         super(var1, var2, var3, var4);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   static final class WeakWriteEntry<K, V> extends LocalCache.WeakEntry<K, V> {
      volatile long writeTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      WeakWriteEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
         super(var1, var2, var3, var4);
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   static final class WeakAccessEntry<K, V> extends LocalCache.WeakEntry<K, V> {
      volatile long accessTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();

      WeakAccessEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
         super(var1, var2, var3, var4);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }
   }

   static class WeakEntry<K, V> extends WeakReference<K> implements LocalCache.ReferenceEntry<K, V> {
      final int hash;
      final LocalCache.ReferenceEntry<K, V> next;
      volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();

      WeakEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
         super(var2, var1);
         this.hash = var3;
         this.next = var4;
      }

      public K getKey() {
         return this.get();
      }

      public long getAccessTime() {
         throw new UnsupportedOperationException();
      }

      public void setAccessTime(long var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public long getWriteTime() {
         throw new UnsupportedOperationException();
      }

      public void setWriteTime(long var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ValueReference<K, V> getValueReference() {
         return this.valueReference;
      }

      public void setValueReference(LocalCache.ValueReference<K, V> var1) {
         this.valueReference = var1;
      }

      public int getHash() {
         return this.hash;
      }

      public LocalCache.ReferenceEntry<K, V> getNext() {
         return this.next;
      }
   }

   static final class StrongAccessWriteEntry<K, V> extends LocalCache.StrongEntry<K, V> {
      volatile long accessTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
      volatile long writeTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      StrongAccessWriteEntry(K var1, int var2, @Nullable LocalCache.ReferenceEntry<K, V> var3) {
         super(var1, var2, var3);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   static final class StrongWriteEntry<K, V> extends LocalCache.StrongEntry<K, V> {
      volatile long writeTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      StrongWriteEntry(K var1, int var2, @Nullable LocalCache.ReferenceEntry<K, V> var3) {
         super(var1, var2, var3);
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   static final class StrongAccessEntry<K, V> extends LocalCache.StrongEntry<K, V> {
      volatile long accessTime = 9223372036854775807L;
      LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();

      StrongAccessEntry(K var1, int var2, @Nullable LocalCache.ReferenceEntry<K, V> var3) {
         super(var1, var2, var3);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }
   }

   static class StrongEntry<K, V> extends LocalCache.AbstractReferenceEntry<K, V> {
      final K key;
      final int hash;
      final LocalCache.ReferenceEntry<K, V> next;
      volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();

      StrongEntry(K var1, int var2, @Nullable LocalCache.ReferenceEntry<K, V> var3) {
         super();
         this.key = var1;
         this.hash = var2;
         this.next = var3;
      }

      public K getKey() {
         return this.key;
      }

      public LocalCache.ValueReference<K, V> getValueReference() {
         return this.valueReference;
      }

      public void setValueReference(LocalCache.ValueReference<K, V> var1) {
         this.valueReference = var1;
      }

      public int getHash() {
         return this.hash;
      }

      public LocalCache.ReferenceEntry<K, V> getNext() {
         return this.next;
      }
   }

   abstract static class AbstractReferenceEntry<K, V> implements LocalCache.ReferenceEntry<K, V> {
      AbstractReferenceEntry() {
         super();
      }

      public LocalCache.ValueReference<K, V> getValueReference() {
         throw new UnsupportedOperationException();
      }

      public void setValueReference(LocalCache.ValueReference<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getNext() {
         throw new UnsupportedOperationException();
      }

      public int getHash() {
         throw new UnsupportedOperationException();
      }

      public K getKey() {
         throw new UnsupportedOperationException();
      }

      public long getAccessTime() {
         throw new UnsupportedOperationException();
      }

      public void setAccessTime(long var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public long getWriteTime() {
         throw new UnsupportedOperationException();
      }

      public void setWriteTime(long var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }
   }

   private static enum NullEntry implements LocalCache.ReferenceEntry<Object, Object> {
      INSTANCE;

      private NullEntry() {
      }

      public LocalCache.ValueReference<Object, Object> getValueReference() {
         return null;
      }

      public void setValueReference(LocalCache.ValueReference<Object, Object> var1) {
      }

      public LocalCache.ReferenceEntry<Object, Object> getNext() {
         return null;
      }

      public int getHash() {
         return 0;
      }

      public Object getKey() {
         return null;
      }

      public long getAccessTime() {
         return 0L;
      }

      public void setAccessTime(long var1) {
      }

      public LocalCache.ReferenceEntry<Object, Object> getNextInAccessQueue() {
         return this;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry<Object, Object> var1) {
      }

      public LocalCache.ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
         return this;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<Object, Object> var1) {
      }

      public long getWriteTime() {
         return 0L;
      }

      public void setWriteTime(long var1) {
      }

      public LocalCache.ReferenceEntry<Object, Object> getNextInWriteQueue() {
         return this;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry<Object, Object> var1) {
      }

      public LocalCache.ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
         return this;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<Object, Object> var1) {
      }
   }

   interface ReferenceEntry<K, V> {
      LocalCache.ValueReference<K, V> getValueReference();

      void setValueReference(LocalCache.ValueReference<K, V> var1);

      @Nullable
      LocalCache.ReferenceEntry<K, V> getNext();

      int getHash();

      @Nullable
      K getKey();

      long getAccessTime();

      void setAccessTime(long var1);

      LocalCache.ReferenceEntry<K, V> getNextInAccessQueue();

      void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> var1);

      LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue();

      void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> var1);

      long getWriteTime();

      void setWriteTime(long var1);

      LocalCache.ReferenceEntry<K, V> getNextInWriteQueue();

      void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> var1);

      LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue();

      void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> var1);
   }

   interface ValueReference<K, V> {
      @Nullable
      V get();

      V waitForValue() throws ExecutionException;

      int getWeight();

      @Nullable
      LocalCache.ReferenceEntry<K, V> getEntry();

      LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, @Nullable V var2, LocalCache.ReferenceEntry<K, V> var3);

      void notifyNewValue(@Nullable V var1);

      boolean isLoading();

      boolean isActive();
   }

   static enum EntryFactory {
      STRONG {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongEntry(var2, var3, var4);
         }
      },
      STRONG_ACCESS {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongAccessEntry(var2, var3, var4);
         }

         <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, LocalCache.ReferenceEntry<K, V> var3) {
            LocalCache.ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            return var4;
         }
      },
      STRONG_WRITE {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongWriteEntry(var2, var3, var4);
         }

         <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, LocalCache.ReferenceEntry<K, V> var3) {
            LocalCache.ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      },
      STRONG_ACCESS_WRITE {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongAccessWriteEntry(var2, var3, var4);
         }

         <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, LocalCache.ReferenceEntry<K, V> var3) {
            LocalCache.ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      },
      WEAK {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakEntry(var1.keyReferenceQueue, var2, var3, var4);
         }
      },
      WEAK_ACCESS {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakAccessEntry(var1.keyReferenceQueue, var2, var3, var4);
         }

         <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, LocalCache.ReferenceEntry<K, V> var3) {
            LocalCache.ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            return var4;
         }
      },
      WEAK_WRITE {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakWriteEntry(var1.keyReferenceQueue, var2, var3, var4);
         }

         <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, LocalCache.ReferenceEntry<K, V> var3) {
            LocalCache.ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      },
      WEAK_ACCESS_WRITE {
         <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakAccessWriteEntry(var1.keyReferenceQueue, var2, var3, var4);
         }

         <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, LocalCache.ReferenceEntry<K, V> var3) {
            LocalCache.ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      };

      static final int ACCESS_MASK = 1;
      static final int WRITE_MASK = 2;
      static final int WEAK_MASK = 4;
      static final LocalCache.EntryFactory[] factories = new LocalCache.EntryFactory[]{STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE};

      private EntryFactory() {
      }

      static LocalCache.EntryFactory getFactory(LocalCache.Strength var0, boolean var1, boolean var2) {
         int var3 = (var0 == LocalCache.Strength.WEAK ? 4 : 0) | (var1 ? 1 : 0) | (var2 ? 2 : 0);
         return factories[var3];
      }

      abstract <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable LocalCache.ReferenceEntry<K, V> var4);

      <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, LocalCache.ReferenceEntry<K, V> var3) {
         return this.newEntry(var1, var2.getKey(), var2.getHash(), var3);
      }

      <K, V> void copyAccessEntry(LocalCache.ReferenceEntry<K, V> var1, LocalCache.ReferenceEntry<K, V> var2) {
         var2.setAccessTime(var1.getAccessTime());
         LocalCache.connectAccessOrder(var1.getPreviousInAccessQueue(), var2);
         LocalCache.connectAccessOrder(var2, var1.getNextInAccessQueue());
         LocalCache.nullifyAccessOrder(var1);
      }

      <K, V> void copyWriteEntry(LocalCache.ReferenceEntry<K, V> var1, LocalCache.ReferenceEntry<K, V> var2) {
         var2.setWriteTime(var1.getWriteTime());
         LocalCache.connectWriteOrder(var1.getPreviousInWriteQueue(), var2);
         LocalCache.connectWriteOrder(var2, var1.getNextInWriteQueue());
         LocalCache.nullifyWriteOrder(var1);
      }

      // $FF: synthetic method
      EntryFactory(Object var3) {
         this();
      }
   }

   static enum Strength {
      STRONG {
         <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, V var3, int var4) {
            return (LocalCache.ValueReference)(var4 == 1 ? new LocalCache.StrongValueReference(var3) : new LocalCache.WeightedStrongValueReference(var3, var4));
         }

         Equivalence<Object> defaultEquivalence() {
            return Equivalence.equals();
         }
      },
      SOFT {
         <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, V var3, int var4) {
            return (LocalCache.ValueReference)(var4 == 1 ? new LocalCache.SoftValueReference(var1.valueReferenceQueue, var3, var2) : new LocalCache.WeightedSoftValueReference(var1.valueReferenceQueue, var3, var2, var4));
         }

         Equivalence<Object> defaultEquivalence() {
            return Equivalence.identity();
         }
      },
      WEAK {
         <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, V var3, int var4) {
            return (LocalCache.ValueReference)(var4 == 1 ? new LocalCache.WeakValueReference(var1.valueReferenceQueue, var3, var2) : new LocalCache.WeightedWeakValueReference(var1.valueReferenceQueue, var3, var2, var4));
         }

         Equivalence<Object> defaultEquivalence() {
            return Equivalence.identity();
         }
      };

      private Strength() {
      }

      abstract <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, LocalCache.ReferenceEntry<K, V> var2, V var3, int var4);

      abstract Equivalence<Object> defaultEquivalence();

      // $FF: synthetic method
      Strength(Object var3) {
         this();
      }
   }
}
