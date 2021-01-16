package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

@GwtCompatible
public final class AtomicLongMap<K> implements Serializable {
   private final ConcurrentHashMap<K, Long> map;
   private transient Map<K, Long> asMap;

   private AtomicLongMap(ConcurrentHashMap<K, Long> var1) {
      super();
      this.map = (ConcurrentHashMap)Preconditions.checkNotNull(var1);
   }

   public static <K> AtomicLongMap<K> create() {
      return new AtomicLongMap(new ConcurrentHashMap());
   }

   public static <K> AtomicLongMap<K> create(Map<? extends K, ? extends Long> var0) {
      AtomicLongMap var1 = create();
      var1.putAll(var0);
      return var1;
   }

   public long get(K var1) {
      return (Long)this.map.getOrDefault(var1, 0L);
   }

   @CanIgnoreReturnValue
   public long incrementAndGet(K var1) {
      return this.addAndGet(var1, 1L);
   }

   @CanIgnoreReturnValue
   public long decrementAndGet(K var1) {
      return this.addAndGet(var1, -1L);
   }

   @CanIgnoreReturnValue
   public long addAndGet(K var1, long var2) {
      return this.accumulateAndGet(var1, var2, Long::sum);
   }

   @CanIgnoreReturnValue
   public long getAndIncrement(K var1) {
      return this.getAndAdd(var1, 1L);
   }

   @CanIgnoreReturnValue
   public long getAndDecrement(K var1) {
      return this.getAndAdd(var1, -1L);
   }

   @CanIgnoreReturnValue
   public long getAndAdd(K var1, long var2) {
      return this.getAndAccumulate(var1, var2, Long::sum);
   }

   @CanIgnoreReturnValue
   public long updateAndGet(K var1, LongUnaryOperator var2) {
      Preconditions.checkNotNull(var2);
      return (Long)this.map.compute(var1, (var1x, var2x) -> {
         return var2.applyAsLong(var2x == null ? 0L : var2x);
      });
   }

   @CanIgnoreReturnValue
   public long getAndUpdate(K var1, LongUnaryOperator var2) {
      Preconditions.checkNotNull(var2);
      AtomicLong var3 = new AtomicLong();
      this.map.compute(var1, (var2x, var3x) -> {
         long var4 = var3x == null ? 0L : var3x;
         var3.set(var4);
         return var2.applyAsLong(var4);
      });
      return var3.get();
   }

   @CanIgnoreReturnValue
   public long accumulateAndGet(K var1, long var2, LongBinaryOperator var4) {
      Preconditions.checkNotNull(var4);
      return this.updateAndGet(var1, (var3) -> {
         return var4.applyAsLong(var3, var2);
      });
   }

   @CanIgnoreReturnValue
   public long getAndAccumulate(K var1, long var2, LongBinaryOperator var4) {
      Preconditions.checkNotNull(var4);
      return this.getAndUpdate(var1, (var3) -> {
         return var4.applyAsLong(var3, var2);
      });
   }

   @CanIgnoreReturnValue
   public long put(K var1, long var2) {
      return this.getAndUpdate(var1, (var2x) -> {
         return var2;
      });
   }

   public void putAll(Map<? extends K, ? extends Long> var1) {
      var1.forEach(this::put);
   }

   @CanIgnoreReturnValue
   public long remove(K var1) {
      Long var2 = (Long)this.map.remove(var1);
      return var2 == null ? 0L : var2;
   }

   @Beta
   @CanIgnoreReturnValue
   public boolean removeIfZero(K var1) {
      return this.remove(var1, 0L);
   }

   public void removeAllZeros() {
      this.map.values().removeIf((var0) -> {
         return var0 == 0L;
      });
   }

   public long sum() {
      return this.map.values().stream().mapToLong(Long::longValue).sum();
   }

   public Map<K, Long> asMap() {
      Map var1 = this.asMap;
      return var1 == null ? (this.asMap = this.createAsMap()) : var1;
   }

   private Map<K, Long> createAsMap() {
      return Collections.unmodifiableMap(this.map);
   }

   public boolean containsKey(Object var1) {
      return this.map.containsKey(var1);
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

   public String toString() {
      return this.map.toString();
   }

   long putIfAbsent(K var1, long var2) {
      AtomicBoolean var4 = new AtomicBoolean(false);
      Long var5 = (Long)this.map.compute(var1, (var3, var4x) -> {
         if (var4x != null && var4x != 0L) {
            return var4x;
         } else {
            var4.set(true);
            return var2;
         }
      });
      return var4.get() ? 0L : var5;
   }

   boolean replace(K var1, long var2, long var4) {
      if (var2 == 0L) {
         return this.putIfAbsent(var1, var4) == 0L;
      } else {
         return this.map.replace(var1, var2, var4);
      }
   }

   boolean remove(K var1, long var2) {
      return this.map.remove(var1, var2);
   }
}
