package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableMap<K, V> implements Map<K, V>, Serializable {
   static final Entry<?, ?>[] EMPTY_ENTRY_ARRAY = new Entry[0];
   @LazyInit
   private transient ImmutableSet<Entry<K, V>> entrySet;
   @LazyInit
   private transient ImmutableSet<K> keySet;
   @LazyInit
   private transient ImmutableCollection<V> values;
   @LazyInit
   private transient ImmutableSetMultimap<K, V> multimapView;

   @Beta
   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      return CollectCollectors.toImmutableMap(var0, var1);
   }

   @Beta
   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, BinaryOperator<V> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collectors.collectingAndThen(Collectors.toMap(var0, var1, var2, LinkedHashMap::new), ImmutableMap::copyOf);
   }

   public static <K, V> ImmutableMap<K, V> of() {
      return ImmutableBiMap.of();
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1) {
      return ImmutableBiMap.of(var0, var1);
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3));
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5));
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7));
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9));
   }

   static <K, V> ImmutableMapEntry<K, V> entryOf(K var0, V var1) {
      return new ImmutableMapEntry(var0, var1);
   }

   public static <K, V> ImmutableMap.Builder<K, V> builder() {
      return new ImmutableMap.Builder();
   }

   static void checkNoConflict(boolean var0, String var1, Entry<?, ?> var2, Entry<?, ?> var3) {
      if (!var0) {
         throw new IllegalArgumentException("Multiple entries with same " + var1 + ": " + var2 + " and " + var3);
      }
   }

   public static <K, V> ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> var0) {
      ImmutableMap var1;
      if (var0 instanceof ImmutableMap && !(var0 instanceof ImmutableSortedMap)) {
         var1 = (ImmutableMap)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      } else if (var0 instanceof EnumMap) {
         var1 = copyOfEnumMap((EnumMap)var0);
         return var1;
      }

      return copyOf((Iterable)var0.entrySet());
   }

   @Beta
   public static <K, V> ImmutableMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      Entry[] var1 = (Entry[])((Entry[])Iterables.toArray(var0, (Object[])EMPTY_ENTRY_ARRAY));
      switch(var1.length) {
      case 0:
         return of();
      case 1:
         Entry var2 = var1[0];
         return of(var2.getKey(), var2.getValue());
      default:
         return RegularImmutableMap.fromEntries(var1);
      }
   }

   private static <K extends Enum<K>, V> ImmutableMap<K, V> copyOfEnumMap(EnumMap<K, ? extends V> var0) {
      EnumMap var1 = new EnumMap(var0);
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         CollectPreconditions.checkEntryNotNull(var3.getKey(), var3.getValue());
      }

      return ImmutableEnumMap.asImmutable(var1);
   }

   ImmutableMap() {
      super();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final V put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final V putIfAbsent(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final boolean replace(K var1, V var2, V var3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final V replace(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void putAll(Map<? extends K, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final V remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final boolean remove(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.get(var1) != null;
   }

   public boolean containsValue(@Nullable Object var1) {
      return this.values().contains(var1);
   }

   public abstract V get(@Nullable Object var1);

   public final V getOrDefault(@Nullable Object var1, @Nullable V var2) {
      Object var3 = this.get(var1);
      return var3 != null ? var3 : var2;
   }

   public ImmutableSet<Entry<K, V>> entrySet() {
      ImmutableSet var1 = this.entrySet;
      return var1 == null ? (this.entrySet = this.createEntrySet()) : var1;
   }

   abstract ImmutableSet<Entry<K, V>> createEntrySet();

   public ImmutableSet<K> keySet() {
      ImmutableSet var1 = this.keySet;
      return var1 == null ? (this.keySet = this.createKeySet()) : var1;
   }

   ImmutableSet<K> createKeySet() {
      return (ImmutableSet)(this.isEmpty() ? ImmutableSet.of() : new ImmutableMapKeySet(this));
   }

   UnmodifiableIterator<K> keyIterator() {
      final UnmodifiableIterator var1 = this.entrySet().iterator();
      return new UnmodifiableIterator<K>() {
         public boolean hasNext() {
            return var1.hasNext();
         }

         public K next() {
            return ((Entry)var1.next()).getKey();
         }
      };
   }

   Spliterator<K> keySpliterator() {
      return CollectSpliterators.map(this.entrySet().spliterator(), Entry::getKey);
   }

   public ImmutableCollection<V> values() {
      ImmutableCollection var1 = this.values;
      return var1 == null ? (this.values = this.createValues()) : var1;
   }

   ImmutableCollection<V> createValues() {
      return new ImmutableMapValues(this);
   }

   public ImmutableSetMultimap<K, V> asMultimap() {
      if (this.isEmpty()) {
         return ImmutableSetMultimap.of();
      } else {
         ImmutableSetMultimap var1 = this.multimapView;
         return var1 == null ? (this.multimapView = new ImmutableSetMultimap(new ImmutableMap.MapViewOfValuesAsSingletonSets(), this.size(), (Comparator)null)) : var1;
      }
   }

   public boolean equals(@Nullable Object var1) {
      return Maps.equalsImpl(this, var1);
   }

   abstract boolean isPartialView();

   public int hashCode() {
      return Sets.hashCodeImpl(this.entrySet());
   }

   boolean isHashCodeFast() {
      return false;
   }

   public String toString() {
      return Maps.toStringImpl(this);
   }

   Object writeReplace() {
      return new ImmutableMap.SerializedForm(this);
   }

   static class SerializedForm implements Serializable {
      private final Object[] keys;
      private final Object[] values;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableMap<?, ?> var1) {
         super();
         this.keys = new Object[var1.size()];
         this.values = new Object[var1.size()];
         int var2 = 0;

         for(UnmodifiableIterator var3 = var1.entrySet().iterator(); var3.hasNext(); ++var2) {
            Entry var4 = (Entry)var3.next();
            this.keys[var2] = var4.getKey();
            this.values[var2] = var4.getValue();
         }

      }

      Object readResolve() {
         ImmutableMap.Builder var1 = new ImmutableMap.Builder(this.keys.length);
         return this.createMap(var1);
      }

      Object createMap(ImmutableMap.Builder<Object, Object> var1) {
         for(int var2 = 0; var2 < this.keys.length; ++var2) {
            var1.put(this.keys[var2], this.values[var2]);
         }

         return var1.build();
      }
   }

   private final class MapViewOfValuesAsSingletonSets extends ImmutableMap.IteratorBasedImmutableMap<K, ImmutableSet<V>> {
      private MapViewOfValuesAsSingletonSets() {
         super();
      }

      public int size() {
         return ImmutableMap.this.size();
      }

      public ImmutableSet<K> keySet() {
         return ImmutableMap.this.keySet();
      }

      public boolean containsKey(@Nullable Object var1) {
         return ImmutableMap.this.containsKey(var1);
      }

      public ImmutableSet<V> get(@Nullable Object var1) {
         Object var2 = ImmutableMap.this.get(var1);
         return var2 == null ? null : ImmutableSet.of(var2);
      }

      boolean isPartialView() {
         return ImmutableMap.this.isPartialView();
      }

      public int hashCode() {
         return ImmutableMap.this.hashCode();
      }

      boolean isHashCodeFast() {
         return ImmutableMap.this.isHashCodeFast();
      }

      UnmodifiableIterator<Entry<K, ImmutableSet<V>>> entryIterator() {
         final UnmodifiableIterator var1 = ImmutableMap.this.entrySet().iterator();
         return new UnmodifiableIterator<Entry<K, ImmutableSet<V>>>() {
            public boolean hasNext() {
               return var1.hasNext();
            }

            public Entry<K, ImmutableSet<V>> next() {
               final Entry var1x = (Entry)var1.next();
               return new AbstractMapEntry<K, ImmutableSet<V>>() {
                  public K getKey() {
                     return var1x.getKey();
                  }

                  public ImmutableSet<V> getValue() {
                     return ImmutableSet.of(var1x.getValue());
                  }
               };
            }
         };
      }

      // $FF: synthetic method
      MapViewOfValuesAsSingletonSets(Object var2) {
         this();
      }
   }

   abstract static class IteratorBasedImmutableMap<K, V> extends ImmutableMap<K, V> {
      IteratorBasedImmutableMap() {
         super();
      }

      abstract UnmodifiableIterator<Entry<K, V>> entryIterator();

      Spliterator<Entry<K, V>> entrySpliterator() {
         return Spliterators.spliterator(this.entryIterator(), (long)this.size(), 1297);
      }

      ImmutableSet<Entry<K, V>> createEntrySet() {
         class 1EntrySetImpl extends ImmutableMapEntrySet<K, V> {
            _EntrySetImpl/* $FF was: 1EntrySetImpl*/() {
               super();
            }

            ImmutableMap<K, V> map() {
               return IteratorBasedImmutableMap.this;
            }

            public UnmodifiableIterator<Entry<K, V>> iterator() {
               return IteratorBasedImmutableMap.this.entryIterator();
            }
         }

         return new 1EntrySetImpl();
      }
   }

   public static class Builder<K, V> {
      Comparator<? super V> valueComparator;
      ImmutableMapEntry<K, V>[] entries;
      int size;
      boolean entriesUsed;

      public Builder() {
         this(4);
      }

      Builder(int var1) {
         super();
         this.entries = new ImmutableMapEntry[var1];
         this.size = 0;
         this.entriesUsed = false;
      }

      private void ensureCapacity(int var1) {
         if (var1 > this.entries.length) {
            this.entries = (ImmutableMapEntry[])Arrays.copyOf(this.entries, ImmutableCollection.Builder.expandedCapacity(this.entries.length, var1));
            this.entriesUsed = false;
         }

      }

      @CanIgnoreReturnValue
      public ImmutableMap.Builder<K, V> put(K var1, V var2) {
         this.ensureCapacity(this.size + 1);
         ImmutableMapEntry var3 = ImmutableMap.entryOf(var1, var2);
         this.entries[this.size++] = var3;
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         return this.put(var1.getKey(), var1.getValue());
      }

      @CanIgnoreReturnValue
      public ImmutableMap.Builder<K, V> putAll(Map<? extends K, ? extends V> var1) {
         return this.putAll((Iterable)var1.entrySet());
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableMap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         if (var1 instanceof Collection) {
            this.ensureCapacity(this.size + ((Collection)var1).size());
         }

         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.put(var3);
         }

         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableMap.Builder<K, V> orderEntriesByValue(Comparator<? super V> var1) {
         Preconditions.checkState(this.valueComparator == null, "valueComparator was already set");
         this.valueComparator = (Comparator)Preconditions.checkNotNull(var1, "valueComparator");
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableMap.Builder<K, V> combine(ImmutableMap.Builder<K, V> var1) {
         Preconditions.checkNotNull(var1);
         this.ensureCapacity(this.size + var1.size);
         System.arraycopy(var1.entries, 0, this.entries, this.size, var1.size);
         this.size += var1.size;
         return this;
      }

      public ImmutableMap<K, V> build() {
         switch(this.size) {
         case 0:
            return ImmutableMap.of();
         case 1:
            return ImmutableMap.of(this.entries[0].getKey(), this.entries[0].getValue());
         default:
            if (this.valueComparator != null) {
               if (this.entriesUsed) {
                  this.entries = (ImmutableMapEntry[])Arrays.copyOf(this.entries, this.size);
               }

               Arrays.sort(this.entries, 0, this.size, Ordering.from(this.valueComparator).onResultOf(Maps.valueFunction()));
            }

            this.entriesUsed = this.size == this.entries.length;
            return RegularImmutableMap.fromEntryArray(this.size, this.entries);
         }
      }
   }
}
