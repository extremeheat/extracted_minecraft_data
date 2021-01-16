package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V> implements NavigableMap<K, V> {
   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
   private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new ImmutableSortedMap(ImmutableSortedSet.emptySet(Ordering.natural()), ImmutableList.of());
   private final transient RegularImmutableSortedSet<K> keySet;
   private final transient ImmutableList<V> valueList;
   private transient ImmutableSortedMap<K, V> descendingMap;
   private static final long serialVersionUID = 0L;

   @Beta
   public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> var0, Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2) {
      return CollectCollectors.toImmutableSortedMap(var0, var1, var2);
   }

   @Beta
   public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> var0, Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2, BinaryOperator<V> var3) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return Collectors.collectingAndThen(Collectors.toMap(var1, var2, var3, () -> {
         return new TreeMap(var0);
      }), ImmutableSortedMap::copyOfSorted);
   }

   static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> var0) {
      return Ordering.natural().equals(var0) ? of() : new ImmutableSortedMap(ImmutableSortedSet.emptySet(var0), ImmutableList.of());
   }

   public static <K, V> ImmutableSortedMap<K, V> of() {
      return NATURAL_EMPTY_MAP;
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1) {
      return of(Ordering.natural(), var0, var1);
   }

   private static <K, V> ImmutableSortedMap<K, V> of(Comparator<? super K> var0, K var1, V var2) {
      return new ImmutableSortedMap(new RegularImmutableSortedSet(ImmutableList.of(var1), (Comparator)Preconditions.checkNotNull(var0)), ImmutableList.of(var2));
   }

   private static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> ofEntries(ImmutableMapEntry<K, V>... var0) {
      return fromEntries(Ordering.natural(), false, var0, var0.length);
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3) {
      return ofEntries(entryOf(var0, var1), entryOf(var2, var3));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return ofEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return ofEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      return ofEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9));
   }

   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> var0) {
      Ordering var1 = (Ordering)NATURAL_ORDER;
      return copyOfInternal(var0, var1);
   }

   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> var0, Comparator<? super K> var1) {
      return copyOfInternal(var0, (Comparator)Preconditions.checkNotNull(var1));
   }

   @Beta
   public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      Ordering var1 = (Ordering)NATURAL_ORDER;
      return copyOf((Iterable)var0, var1);
   }

   @Beta
   public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0, Comparator<? super K> var1) {
      return fromEntries((Comparator)Preconditions.checkNotNull(var1), false, var0);
   }

   public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> var0) {
      Comparator var1 = var0.comparator();
      if (var1 == null) {
         var1 = NATURAL_ORDER;
      }

      if (var0 instanceof ImmutableSortedMap) {
         ImmutableSortedMap var2 = (ImmutableSortedMap)var0;
         if (!var2.isPartialView()) {
            return var2;
         }
      }

      return fromEntries(var1, true, var0.entrySet());
   }

   private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> var0, Comparator<? super K> var1) {
      boolean var2 = false;
      if (var0 instanceof SortedMap) {
         SortedMap var3 = (SortedMap)var0;
         Comparator var4 = var3.comparator();
         var2 = var4 == null ? var1 == NATURAL_ORDER : var1.equals(var4);
      }

      if (var2 && var0 instanceof ImmutableSortedMap) {
         ImmutableSortedMap var5 = (ImmutableSortedMap)var0;
         if (!var5.isPartialView()) {
            return var5;
         }
      }

      return fromEntries(var1, var2, var0.entrySet());
   }

   private static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> var0, boolean var1, Iterable<? extends Entry<? extends K, ? extends V>> var2) {
      Entry[] var3 = (Entry[])((Entry[])Iterables.toArray(var2, (Object[])EMPTY_ENTRY_ARRAY));
      return fromEntries(var0, var1, var3, var3.length);
   }

   private static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> var0, boolean var1, Entry<K, V>[] var2, int var3) {
      switch(var3) {
      case 0:
         return emptyMap(var0);
      case 1:
         return of(var0, var2[0].getKey(), var2[0].getValue());
      default:
         Object[] var4 = new Object[var3];
         Object[] var5 = new Object[var3];
         Object var8;
         if (var1) {
            for(int var6 = 0; var6 < var3; ++var6) {
               Object var7 = var2[var6].getKey();
               var8 = var2[var6].getValue();
               CollectPreconditions.checkEntryNotNull(var7, var8);
               var4[var6] = var7;
               var5[var6] = var8;
            }
         } else {
            Arrays.sort(var2, 0, var3, Ordering.from(var0).onKeys());
            Object var10 = var2[0].getKey();
            var4[0] = var10;
            var5[0] = var2[0].getValue();

            for(int var11 = 1; var11 < var3; ++var11) {
               var8 = var2[var11].getKey();
               Object var9 = var2[var11].getValue();
               CollectPreconditions.checkEntryNotNull(var8, var9);
               var4[var11] = var8;
               var5[var11] = var9;
               checkNoConflict(var0.compare(var10, var8) != 0, "key", var2[var11 - 1], var2[var11]);
               var10 = var8;
            }
         }

         return new ImmutableSortedMap(new RegularImmutableSortedSet(new RegularImmutableList(var4), var0), new RegularImmutableList(var5));
      }
   }

   public static <K extends Comparable<?>, V> ImmutableSortedMap.Builder<K, V> naturalOrder() {
      return new ImmutableSortedMap.Builder(Ordering.natural());
   }

   public static <K, V> ImmutableSortedMap.Builder<K, V> orderedBy(Comparator<K> var0) {
      return new ImmutableSortedMap.Builder(var0);
   }

   public static <K extends Comparable<?>, V> ImmutableSortedMap.Builder<K, V> reverseOrder() {
      return new ImmutableSortedMap.Builder(Ordering.natural().reverse());
   }

   ImmutableSortedMap(RegularImmutableSortedSet<K> var1, ImmutableList<V> var2) {
      this(var1, var2, (ImmutableSortedMap)null);
   }

   ImmutableSortedMap(RegularImmutableSortedSet<K> var1, ImmutableList<V> var2, ImmutableSortedMap<K, V> var3) {
      super();
      this.keySet = var1;
      this.valueList = var2;
      this.descendingMap = var3;
   }

   public int size() {
      return this.valueList.size();
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      ImmutableList var2 = this.keySet.asList();

      for(int var3 = 0; var3 < this.size(); ++var3) {
         var1.accept(var2.get(var3), this.valueList.get(var3));
      }

   }

   public V get(@Nullable Object var1) {
      int var2 = this.keySet.indexOf(var1);
      return var2 == -1 ? null : this.valueList.get(var2);
   }

   boolean isPartialView() {
      return this.keySet.isPartialView() || this.valueList.isPartialView();
   }

   public ImmutableSet<Entry<K, V>> entrySet() {
      return super.entrySet();
   }

   ImmutableSet<Entry<K, V>> createEntrySet() {
      class 1EntrySet extends ImmutableMapEntrySet<K, V> {
         _EntrySet/* $FF was: 1EntrySet*/() {
            super();
         }

         public UnmodifiableIterator<Entry<K, V>> iterator() {
            return this.asList().iterator();
         }

         public Spliterator<Entry<K, V>> spliterator() {
            return this.asList().spliterator();
         }

         public void forEach(Consumer<? super Entry<K, V>> var1) {
            this.asList().forEach(var1);
         }

         ImmutableList<Entry<K, V>> createAsList() {
            return new ImmutableAsList<Entry<K, V>>() {
               public Entry<K, V> get(int var1) {
                  return Maps.immutableEntry(ImmutableSortedMap.this.keySet.asList().get(var1), ImmutableSortedMap.this.valueList.get(var1));
               }

               public Spliterator<Entry<K, V>> spliterator() {
                  return CollectSpliterators.indexed(this.size(), 1297, this::get);
               }

               ImmutableCollection<Entry<K, V>> delegateCollection() {
                  return 1EntrySet.this;
               }
            };
         }

         ImmutableMap<K, V> map() {
            return ImmutableSortedMap.this;
         }
      }

      return (ImmutableSet)(this.isEmpty() ? ImmutableSet.of() : new 1EntrySet());
   }

   public ImmutableSortedSet<K> keySet() {
      return this.keySet;
   }

   public ImmutableCollection<V> values() {
      return this.valueList;
   }

   public Comparator<? super K> comparator() {
      return this.keySet().comparator();
   }

   public K firstKey() {
      return this.keySet().first();
   }

   public K lastKey() {
      return this.keySet().last();
   }

   private ImmutableSortedMap<K, V> getSubMap(int var1, int var2) {
      if (var1 == 0 && var2 == this.size()) {
         return this;
      } else {
         return var1 == var2 ? emptyMap(this.comparator()) : new ImmutableSortedMap(this.keySet.getSubSet(var1, var2), this.valueList.subList(var1, var2));
      }
   }

   public ImmutableSortedMap<K, V> headMap(K var1) {
      return this.headMap(var1, false);
   }

   public ImmutableSortedMap<K, V> headMap(K var1, boolean var2) {
      return this.getSubMap(0, this.keySet.headIndex(Preconditions.checkNotNull(var1), var2));
   }

   public ImmutableSortedMap<K, V> subMap(K var1, K var2) {
      return this.subMap(var1, true, var2, false);
   }

   public ImmutableSortedMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Preconditions.checkArgument(this.comparator().compare(var1, var3) <= 0, "expected fromKey <= toKey but %s > %s", var1, var3);
      return this.headMap(var3, var4).tailMap(var1, var2);
   }

   public ImmutableSortedMap<K, V> tailMap(K var1) {
      return this.tailMap(var1, true);
   }

   public ImmutableSortedMap<K, V> tailMap(K var1, boolean var2) {
      return this.getSubMap(this.keySet.tailIndex(Preconditions.checkNotNull(var1), var2), this.size());
   }

   public Entry<K, V> lowerEntry(K var1) {
      return this.headMap(var1, false).lastEntry();
   }

   public K lowerKey(K var1) {
      return Maps.keyOrNull(this.lowerEntry(var1));
   }

   public Entry<K, V> floorEntry(K var1) {
      return this.headMap(var1, true).lastEntry();
   }

   public K floorKey(K var1) {
      return Maps.keyOrNull(this.floorEntry(var1));
   }

   public Entry<K, V> ceilingEntry(K var1) {
      return this.tailMap(var1, true).firstEntry();
   }

   public K ceilingKey(K var1) {
      return Maps.keyOrNull(this.ceilingEntry(var1));
   }

   public Entry<K, V> higherEntry(K var1) {
      return this.tailMap(var1, false).firstEntry();
   }

   public K higherKey(K var1) {
      return Maps.keyOrNull(this.higherEntry(var1));
   }

   public Entry<K, V> firstEntry() {
      return this.isEmpty() ? null : (Entry)this.entrySet().asList().get(0);
   }

   public Entry<K, V> lastEntry() {
      return this.isEmpty() ? null : (Entry)this.entrySet().asList().get(this.size() - 1);
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final Entry<K, V> pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final Entry<K, V> pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public ImmutableSortedMap<K, V> descendingMap() {
      ImmutableSortedMap var1 = this.descendingMap;
      if (var1 == null) {
         return this.isEmpty() ? emptyMap(Ordering.from(this.comparator()).reverse()) : new ImmutableSortedMap((RegularImmutableSortedSet)this.keySet.descendingSet(), this.valueList.reverse(), this);
      } else {
         return var1;
      }
   }

   public ImmutableSortedSet<K> navigableKeySet() {
      return this.keySet;
   }

   public ImmutableSortedSet<K> descendingKeySet() {
      return this.keySet.descendingSet();
   }

   Object writeReplace() {
      return new ImmutableSortedMap.SerializedForm(this);
   }

   private static class SerializedForm extends ImmutableMap.SerializedForm {
      private final Comparator<Object> comparator;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableSortedMap<?, ?> var1) {
         super(var1);
         this.comparator = var1.comparator();
      }

      Object readResolve() {
         ImmutableSortedMap.Builder var1 = new ImmutableSortedMap.Builder(this.comparator);
         return this.createMap(var1);
      }
   }

   public static class Builder<K, V> extends ImmutableMap.Builder<K, V> {
      private final Comparator<? super K> comparator;

      public Builder(Comparator<? super K> var1) {
         super();
         this.comparator = (Comparator)Preconditions.checkNotNull(var1);
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMap.Builder<K, V> put(K var1, V var2) {
         super.put(var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         super.put(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMap.Builder<K, V> putAll(Map<? extends K, ? extends V> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableSortedMap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         super.putAll(var1);
         return this;
      }

      /** @deprecated */
      @Deprecated
      @CanIgnoreReturnValue
      @Beta
      public ImmutableSortedMap.Builder<K, V> orderEntriesByValue(Comparator<? super V> var1) {
         throw new UnsupportedOperationException("Not available on ImmutableSortedMap.Builder");
      }

      ImmutableSortedMap.Builder<K, V> combine(ImmutableMap.Builder<K, V> var1) {
         super.combine(var1);
         return this;
      }

      public ImmutableSortedMap<K, V> build() {
         switch(this.size) {
         case 0:
            return ImmutableSortedMap.emptyMap(this.comparator);
         case 1:
            return ImmutableSortedMap.of(this.comparator, this.entries[0].getKey(), this.entries[0].getValue());
         default:
            return ImmutableSortedMap.fromEntries(this.comparator, false, this.entries, this.size);
         }
      }
   }
}
