package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class Synchronized {
   private Synchronized() {
      super();
   }

   private static <E> Collection<E> collection(Collection<E> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedCollection(var0, var1);
   }

   @VisibleForTesting
   static <E> Set<E> set(Set<E> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedSet(var0, var1);
   }

   private static <E> SortedSet<E> sortedSet(SortedSet<E> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedSortedSet(var0, var1);
   }

   private static <E> List<E> list(List<E> var0, @Nullable Object var1) {
      return (List)(var0 instanceof RandomAccess ? new Synchronized.SynchronizedRandomAccessList(var0, var1) : new Synchronized.SynchronizedList(var0, var1));
   }

   static <E> Multiset<E> multiset(Multiset<E> var0, @Nullable Object var1) {
      return (Multiset)(!(var0 instanceof Synchronized.SynchronizedMultiset) && !(var0 instanceof ImmutableMultiset) ? new Synchronized.SynchronizedMultiset(var0, var1) : var0);
   }

   static <K, V> Multimap<K, V> multimap(Multimap<K, V> var0, @Nullable Object var1) {
      return (Multimap)(!(var0 instanceof Synchronized.SynchronizedMultimap) && !(var0 instanceof ImmutableMultimap) ? new Synchronized.SynchronizedMultimap(var0, var1) : var0);
   }

   static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> var0, @Nullable Object var1) {
      return (ListMultimap)(!(var0 instanceof Synchronized.SynchronizedListMultimap) && !(var0 instanceof ImmutableListMultimap) ? new Synchronized.SynchronizedListMultimap(var0, var1) : var0);
   }

   static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> var0, @Nullable Object var1) {
      return (SetMultimap)(!(var0 instanceof Synchronized.SynchronizedSetMultimap) && !(var0 instanceof ImmutableSetMultimap) ? new Synchronized.SynchronizedSetMultimap(var0, var1) : var0);
   }

   static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> var0, @Nullable Object var1) {
      return (SortedSetMultimap)(var0 instanceof Synchronized.SynchronizedSortedSetMultimap ? var0 : new Synchronized.SynchronizedSortedSetMultimap(var0, var1));
   }

   private static <E> Collection<E> typePreservingCollection(Collection<E> var0, @Nullable Object var1) {
      if (var0 instanceof SortedSet) {
         return sortedSet((SortedSet)var0, var1);
      } else if (var0 instanceof Set) {
         return set((Set)var0, var1);
      } else {
         return (Collection)(var0 instanceof List ? list((List)var0, var1) : collection(var0, var1));
      }
   }

   private static <E> Set<E> typePreservingSet(Set<E> var0, @Nullable Object var1) {
      return (Set)(var0 instanceof SortedSet ? sortedSet((SortedSet)var0, var1) : set(var0, var1));
   }

   @VisibleForTesting
   static <K, V> Map<K, V> map(Map<K, V> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedMap(var0, var1);
   }

   static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedSortedMap(var0, var1);
   }

   static <K, V> BiMap<K, V> biMap(BiMap<K, V> var0, @Nullable Object var1) {
      return (BiMap)(!(var0 instanceof Synchronized.SynchronizedBiMap) && !(var0 instanceof ImmutableBiMap) ? new Synchronized.SynchronizedBiMap(var0, var1, (BiMap)null) : var0);
   }

   @GwtIncompatible
   static <E> NavigableSet<E> navigableSet(NavigableSet<E> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedNavigableSet(var0, var1);
   }

   @GwtIncompatible
   static <E> NavigableSet<E> navigableSet(NavigableSet<E> var0) {
      return navigableSet(var0, (Object)null);
   }

   @GwtIncompatible
   static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> var0) {
      return navigableMap(var0, (Object)null);
   }

   @GwtIncompatible
   static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedNavigableMap(var0, var1);
   }

   @GwtIncompatible
   private static <K, V> Entry<K, V> nullableSynchronizedEntry(@Nullable Entry<K, V> var0, @Nullable Object var1) {
      return var0 == null ? null : new Synchronized.SynchronizedEntry(var0, var1);
   }

   static <E> Queue<E> queue(Queue<E> var0, @Nullable Object var1) {
      return (Queue)(var0 instanceof Synchronized.SynchronizedQueue ? var0 : new Synchronized.SynchronizedQueue(var0, var1));
   }

   static <E> Deque<E> deque(Deque<E> var0, @Nullable Object var1) {
      return new Synchronized.SynchronizedDeque(var0, var1);
   }

   private static final class SynchronizedDeque<E> extends Synchronized.SynchronizedQueue<E> implements Deque<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedDeque(Deque<E> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      Deque<E> delegate() {
         return (Deque)super.delegate();
      }

      public void addFirst(E var1) {
         synchronized(this.mutex) {
            this.delegate().addFirst(var1);
         }
      }

      public void addLast(E var1) {
         synchronized(this.mutex) {
            this.delegate().addLast(var1);
         }
      }

      public boolean offerFirst(E var1) {
         synchronized(this.mutex) {
            return this.delegate().offerFirst(var1);
         }
      }

      public boolean offerLast(E var1) {
         synchronized(this.mutex) {
            return this.delegate().offerLast(var1);
         }
      }

      public E removeFirst() {
         synchronized(this.mutex) {
            return this.delegate().removeFirst();
         }
      }

      public E removeLast() {
         synchronized(this.mutex) {
            return this.delegate().removeLast();
         }
      }

      public E pollFirst() {
         synchronized(this.mutex) {
            return this.delegate().pollFirst();
         }
      }

      public E pollLast() {
         synchronized(this.mutex) {
            return this.delegate().pollLast();
         }
      }

      public E getFirst() {
         synchronized(this.mutex) {
            return this.delegate().getFirst();
         }
      }

      public E getLast() {
         synchronized(this.mutex) {
            return this.delegate().getLast();
         }
      }

      public E peekFirst() {
         synchronized(this.mutex) {
            return this.delegate().peekFirst();
         }
      }

      public E peekLast() {
         synchronized(this.mutex) {
            return this.delegate().peekLast();
         }
      }

      public boolean removeFirstOccurrence(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().removeFirstOccurrence(var1);
         }
      }

      public boolean removeLastOccurrence(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().removeLastOccurrence(var1);
         }
      }

      public void push(E var1) {
         synchronized(this.mutex) {
            this.delegate().push(var1);
         }
      }

      public E pop() {
         synchronized(this.mutex) {
            return this.delegate().pop();
         }
      }

      public Iterator<E> descendingIterator() {
         synchronized(this.mutex) {
            return this.delegate().descendingIterator();
         }
      }
   }

   private static class SynchronizedQueue<E> extends Synchronized.SynchronizedCollection<E> implements Queue<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedQueue(Queue<E> var1, @Nullable Object var2) {
         super(var1, var2, null);
      }

      Queue<E> delegate() {
         return (Queue)super.delegate();
      }

      public E element() {
         synchronized(this.mutex) {
            return this.delegate().element();
         }
      }

      public boolean offer(E var1) {
         synchronized(this.mutex) {
            return this.delegate().offer(var1);
         }
      }

      public E peek() {
         synchronized(this.mutex) {
            return this.delegate().peek();
         }
      }

      public E poll() {
         synchronized(this.mutex) {
            return this.delegate().poll();
         }
      }

      public E remove() {
         synchronized(this.mutex) {
            return this.delegate().remove();
         }
      }
   }

   @GwtIncompatible
   private static class SynchronizedEntry<K, V> extends Synchronized.SynchronizedObject implements Entry<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedEntry(Entry<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      Entry<K, V> delegate() {
         return (Entry)super.delegate();
      }

      public boolean equals(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().equals(var1);
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }

      public K getKey() {
         synchronized(this.mutex) {
            return this.delegate().getKey();
         }
      }

      public V getValue() {
         synchronized(this.mutex) {
            return this.delegate().getValue();
         }
      }

      public V setValue(V var1) {
         synchronized(this.mutex) {
            return this.delegate().setValue(var1);
         }
      }
   }

   @GwtIncompatible
   @VisibleForTesting
   static class SynchronizedNavigableMap<K, V> extends Synchronized.SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {
      transient NavigableSet<K> descendingKeySet;
      transient NavigableMap<K, V> descendingMap;
      transient NavigableSet<K> navigableKeySet;
      private static final long serialVersionUID = 0L;

      SynchronizedNavigableMap(NavigableMap<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      NavigableMap<K, V> delegate() {
         return (NavigableMap)super.delegate();
      }

      public Entry<K, V> ceilingEntry(K var1) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().ceilingEntry(var1), this.mutex);
         }
      }

      public K ceilingKey(K var1) {
         synchronized(this.mutex) {
            return this.delegate().ceilingKey(var1);
         }
      }

      public NavigableSet<K> descendingKeySet() {
         synchronized(this.mutex) {
            return this.descendingKeySet == null ? (this.descendingKeySet = Synchronized.navigableSet(this.delegate().descendingKeySet(), this.mutex)) : this.descendingKeySet;
         }
      }

      public NavigableMap<K, V> descendingMap() {
         synchronized(this.mutex) {
            return this.descendingMap == null ? (this.descendingMap = Synchronized.navigableMap(this.delegate().descendingMap(), this.mutex)) : this.descendingMap;
         }
      }

      public Entry<K, V> firstEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().firstEntry(), this.mutex);
         }
      }

      public Entry<K, V> floorEntry(K var1) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().floorEntry(var1), this.mutex);
         }
      }

      public K floorKey(K var1) {
         synchronized(this.mutex) {
            return this.delegate().floorKey(var1);
         }
      }

      public NavigableMap<K, V> headMap(K var1, boolean var2) {
         synchronized(this.mutex) {
            return Synchronized.navigableMap(this.delegate().headMap(var1, var2), this.mutex);
         }
      }

      public Entry<K, V> higherEntry(K var1) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().higherEntry(var1), this.mutex);
         }
      }

      public K higherKey(K var1) {
         synchronized(this.mutex) {
            return this.delegate().higherKey(var1);
         }
      }

      public Entry<K, V> lastEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().lastEntry(), this.mutex);
         }
      }

      public Entry<K, V> lowerEntry(K var1) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().lowerEntry(var1), this.mutex);
         }
      }

      public K lowerKey(K var1) {
         synchronized(this.mutex) {
            return this.delegate().lowerKey(var1);
         }
      }

      public Set<K> keySet() {
         return this.navigableKeySet();
      }

      public NavigableSet<K> navigableKeySet() {
         synchronized(this.mutex) {
            return this.navigableKeySet == null ? (this.navigableKeySet = Synchronized.navigableSet(this.delegate().navigableKeySet(), this.mutex)) : this.navigableKeySet;
         }
      }

      public Entry<K, V> pollFirstEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().pollFirstEntry(), this.mutex);
         }
      }

      public Entry<K, V> pollLastEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().pollLastEntry(), this.mutex);
         }
      }

      public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         synchronized(this.mutex) {
            return Synchronized.navigableMap(this.delegate().subMap(var1, var2, var3, var4), this.mutex);
         }
      }

      public NavigableMap<K, V> tailMap(K var1, boolean var2) {
         synchronized(this.mutex) {
            return Synchronized.navigableMap(this.delegate().tailMap(var1, var2), this.mutex);
         }
      }

      public SortedMap<K, V> headMap(K var1) {
         return this.headMap(var1, false);
      }

      public SortedMap<K, V> subMap(K var1, K var2) {
         return this.subMap(var1, true, var2, false);
      }

      public SortedMap<K, V> tailMap(K var1) {
         return this.tailMap(var1, true);
      }
   }

   @GwtIncompatible
   @VisibleForTesting
   static class SynchronizedNavigableSet<E> extends Synchronized.SynchronizedSortedSet<E> implements NavigableSet<E> {
      transient NavigableSet<E> descendingSet;
      private static final long serialVersionUID = 0L;

      SynchronizedNavigableSet(NavigableSet<E> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      NavigableSet<E> delegate() {
         return (NavigableSet)super.delegate();
      }

      public E ceiling(E var1) {
         synchronized(this.mutex) {
            return this.delegate().ceiling(var1);
         }
      }

      public Iterator<E> descendingIterator() {
         return this.delegate().descendingIterator();
      }

      public NavigableSet<E> descendingSet() {
         synchronized(this.mutex) {
            if (this.descendingSet == null) {
               NavigableSet var2 = Synchronized.navigableSet(this.delegate().descendingSet(), this.mutex);
               this.descendingSet = var2;
               return var2;
            } else {
               return this.descendingSet;
            }
         }
      }

      public E floor(E var1) {
         synchronized(this.mutex) {
            return this.delegate().floor(var1);
         }
      }

      public NavigableSet<E> headSet(E var1, boolean var2) {
         synchronized(this.mutex) {
            return Synchronized.navigableSet(this.delegate().headSet(var1, var2), this.mutex);
         }
      }

      public E higher(E var1) {
         synchronized(this.mutex) {
            return this.delegate().higher(var1);
         }
      }

      public E lower(E var1) {
         synchronized(this.mutex) {
            return this.delegate().lower(var1);
         }
      }

      public E pollFirst() {
         synchronized(this.mutex) {
            return this.delegate().pollFirst();
         }
      }

      public E pollLast() {
         synchronized(this.mutex) {
            return this.delegate().pollLast();
         }
      }

      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         synchronized(this.mutex) {
            return Synchronized.navigableSet(this.delegate().subSet(var1, var2, var3, var4), this.mutex);
         }
      }

      public NavigableSet<E> tailSet(E var1, boolean var2) {
         synchronized(this.mutex) {
            return Synchronized.navigableSet(this.delegate().tailSet(var1, var2), this.mutex);
         }
      }

      public SortedSet<E> headSet(E var1) {
         return this.headSet(var1, false);
      }

      public SortedSet<E> subSet(E var1, E var2) {
         return this.subSet(var1, true, var2, false);
      }

      public SortedSet<E> tailSet(E var1) {
         return this.tailSet(var1, true);
      }
   }

   private static class SynchronizedAsMapValues<V> extends Synchronized.SynchronizedCollection<Collection<V>> {
      private static final long serialVersionUID = 0L;

      SynchronizedAsMapValues(Collection<Collection<V>> var1, @Nullable Object var2) {
         super(var1, var2, null);
      }

      public Iterator<Collection<V>> iterator() {
         return new TransformedIterator<Collection<V>, Collection<V>>(super.iterator()) {
            Collection<V> transform(Collection<V> var1) {
               return Synchronized.typePreservingCollection(var1, SynchronizedAsMapValues.this.mutex);
            }
         };
      }
   }

   private static class SynchronizedAsMap<K, V> extends Synchronized.SynchronizedMap<K, Collection<V>> {
      transient Set<Entry<K, Collection<V>>> asMapEntrySet;
      transient Collection<Collection<V>> asMapValues;
      private static final long serialVersionUID = 0L;

      SynchronizedAsMap(Map<K, Collection<V>> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      public Collection<V> get(Object var1) {
         synchronized(this.mutex) {
            Collection var3 = (Collection)super.get(var1);
            return var3 == null ? null : Synchronized.typePreservingCollection(var3, this.mutex);
         }
      }

      public Set<Entry<K, Collection<V>>> entrySet() {
         synchronized(this.mutex) {
            if (this.asMapEntrySet == null) {
               this.asMapEntrySet = new Synchronized.SynchronizedAsMapEntries(this.delegate().entrySet(), this.mutex);
            }

            return this.asMapEntrySet;
         }
      }

      public Collection<Collection<V>> values() {
         synchronized(this.mutex) {
            if (this.asMapValues == null) {
               this.asMapValues = new Synchronized.SynchronizedAsMapValues(this.delegate().values(), this.mutex);
            }

            return this.asMapValues;
         }
      }

      public boolean containsValue(Object var1) {
         return this.values().contains(var1);
      }
   }

   @VisibleForTesting
   static class SynchronizedBiMap<K, V> extends Synchronized.SynchronizedMap<K, V> implements BiMap<K, V>, Serializable {
      private transient Set<V> valueSet;
      @RetainedWith
      private transient BiMap<V, K> inverse;
      private static final long serialVersionUID = 0L;

      private SynchronizedBiMap(BiMap<K, V> var1, @Nullable Object var2, @Nullable BiMap<V, K> var3) {
         super(var1, var2);
         this.inverse = var3;
      }

      BiMap<K, V> delegate() {
         return (BiMap)super.delegate();
      }

      public Set<V> values() {
         synchronized(this.mutex) {
            if (this.valueSet == null) {
               this.valueSet = Synchronized.set(this.delegate().values(), this.mutex);
            }

            return this.valueSet;
         }
      }

      public V forcePut(K var1, V var2) {
         synchronized(this.mutex) {
            return this.delegate().forcePut(var1, var2);
         }
      }

      public BiMap<V, K> inverse() {
         synchronized(this.mutex) {
            if (this.inverse == null) {
               this.inverse = new Synchronized.SynchronizedBiMap(this.delegate().inverse(), this.mutex, this);
            }

            return this.inverse;
         }
      }

      // $FF: synthetic method
      SynchronizedBiMap(BiMap var1, Object var2, BiMap var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   static class SynchronizedSortedMap<K, V> extends Synchronized.SynchronizedMap<K, V> implements SortedMap<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedMap(SortedMap<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      SortedMap<K, V> delegate() {
         return (SortedMap)super.delegate();
      }

      public Comparator<? super K> comparator() {
         synchronized(this.mutex) {
            return this.delegate().comparator();
         }
      }

      public K firstKey() {
         synchronized(this.mutex) {
            return this.delegate().firstKey();
         }
      }

      public SortedMap<K, V> headMap(K var1) {
         synchronized(this.mutex) {
            return Synchronized.sortedMap(this.delegate().headMap(var1), this.mutex);
         }
      }

      public K lastKey() {
         synchronized(this.mutex) {
            return this.delegate().lastKey();
         }
      }

      public SortedMap<K, V> subMap(K var1, K var2) {
         synchronized(this.mutex) {
            return Synchronized.sortedMap(this.delegate().subMap(var1, var2), this.mutex);
         }
      }

      public SortedMap<K, V> tailMap(K var1) {
         synchronized(this.mutex) {
            return Synchronized.sortedMap(this.delegate().tailMap(var1), this.mutex);
         }
      }
   }

   private static class SynchronizedMap<K, V> extends Synchronized.SynchronizedObject implements Map<K, V> {
      transient Set<K> keySet;
      transient Collection<V> values;
      transient Set<Entry<K, V>> entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedMap(Map<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      Map<K, V> delegate() {
         return (Map)super.delegate();
      }

      public void clear() {
         synchronized(this.mutex) {
            this.delegate().clear();
         }
      }

      public boolean containsKey(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().containsKey(var1);
         }
      }

      public boolean containsValue(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().containsValue(var1);
         }
      }

      public Set<Entry<K, V>> entrySet() {
         synchronized(this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = Synchronized.set(this.delegate().entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         synchronized(this.mutex) {
            this.delegate().forEach(var1);
         }
      }

      public V get(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().get(var1);
         }
      }

      public V getOrDefault(Object var1, V var2) {
         synchronized(this.mutex) {
            return this.delegate().getOrDefault(var1, var2);
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      public Set<K> keySet() {
         synchronized(this.mutex) {
            if (this.keySet == null) {
               this.keySet = Synchronized.set(this.delegate().keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      public V put(K var1, V var2) {
         synchronized(this.mutex) {
            return this.delegate().put(var1, var2);
         }
      }

      public V putIfAbsent(K var1, V var2) {
         synchronized(this.mutex) {
            return this.delegate().putIfAbsent(var1, var2);
         }
      }

      public boolean replace(K var1, V var2, V var3) {
         synchronized(this.mutex) {
            return this.delegate().replace(var1, var2, var3);
         }
      }

      public V replace(K var1, V var2) {
         synchronized(this.mutex) {
            return this.delegate().replace(var1, var2);
         }
      }

      public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().computeIfAbsent(var1, var2);
         }
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().computeIfPresent(var1, var2);
         }
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().compute(var1, var2);
         }
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.mutex) {
            return this.delegate().merge(var1, var2, var3);
         }
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         synchronized(this.mutex) {
            this.delegate().putAll(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         synchronized(this.mutex) {
            this.delegate().replaceAll(var1);
         }
      }

      public V remove(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().remove(var1);
         }
      }

      public boolean remove(Object var1, Object var2) {
         synchronized(this.mutex) {
            return this.delegate().remove(var1, var2);
         }
      }

      public int size() {
         synchronized(this.mutex) {
            return this.delegate().size();
         }
      }

      public Collection<V> values() {
         synchronized(this.mutex) {
            if (this.values == null) {
               this.values = Synchronized.collection(this.delegate().values(), this.mutex);
            }

            return this.values;
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedAsMapEntries<K, V> extends Synchronized.SynchronizedSet<Entry<K, Collection<V>>> {
      private static final long serialVersionUID = 0L;

      SynchronizedAsMapEntries(Set<Entry<K, Collection<V>>> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      public Iterator<Entry<K, Collection<V>>> iterator() {
         return new TransformedIterator<Entry<K, Collection<V>>, Entry<K, Collection<V>>>(super.iterator()) {
            Entry<K, Collection<V>> transform(final Entry<K, Collection<V>> var1) {
               return new ForwardingMapEntry<K, Collection<V>>() {
                  protected Entry<K, Collection<V>> delegate() {
                     return var1;
                  }

                  public Collection<V> getValue() {
                     return Synchronized.typePreservingCollection((Collection)var1.getValue(), SynchronizedAsMapEntries.this.mutex);
                  }
               };
            }
         };
      }

      public Object[] toArray() {
         synchronized(this.mutex) {
            return ObjectArrays.toArrayImpl(this.delegate());
         }
      }

      public <T> T[] toArray(T[] var1) {
         synchronized(this.mutex) {
            return ObjectArrays.toArrayImpl(this.delegate(), var1);
         }
      }

      public boolean contains(Object var1) {
         synchronized(this.mutex) {
            return Maps.containsEntryImpl(this.delegate(), var1);
         }
      }

      public boolean containsAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return Collections2.containsAllImpl(this.delegate(), var1);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return Sets.equalsImpl(this.delegate(), var1);
            }
         }
      }

      public boolean remove(Object var1) {
         synchronized(this.mutex) {
            return Maps.removeEntryImpl(this.delegate(), var1);
         }
      }

      public boolean removeAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return Iterators.removeAll(this.delegate().iterator(), var1);
         }
      }

      public boolean retainAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return Iterators.retainAll(this.delegate().iterator(), var1);
         }
      }
   }

   private static class SynchronizedSortedSetMultimap<K, V> extends Synchronized.SynchronizedSetMultimap<K, V> implements SortedSetMultimap<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedSetMultimap(SortedSetMultimap<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      SortedSetMultimap<K, V> delegate() {
         return (SortedSetMultimap)super.delegate();
      }

      public SortedSet<V> get(K var1) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().get(var1), this.mutex);
         }
      }

      public SortedSet<V> removeAll(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      public SortedSet<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(var1, var2);
         }
      }

      public Comparator<? super V> valueComparator() {
         synchronized(this.mutex) {
            return this.delegate().valueComparator();
         }
      }
   }

   private static class SynchronizedSetMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V> implements SetMultimap<K, V> {
      transient Set<Entry<K, V>> entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedSetMultimap(SetMultimap<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      SetMultimap<K, V> delegate() {
         return (SetMultimap)super.delegate();
      }

      public Set<V> get(K var1) {
         synchronized(this.mutex) {
            return Synchronized.set(this.delegate().get(var1), this.mutex);
         }
      }

      public Set<V> removeAll(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      public Set<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(var1, var2);
         }
      }

      public Set<Entry<K, V>> entries() {
         synchronized(this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = Synchronized.set(this.delegate().entries(), this.mutex);
            }

            return this.entrySet;
         }
      }
   }

   private static class SynchronizedListMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V> implements ListMultimap<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedListMultimap(ListMultimap<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      ListMultimap<K, V> delegate() {
         return (ListMultimap)super.delegate();
      }

      public List<V> get(K var1) {
         synchronized(this.mutex) {
            return Synchronized.list(this.delegate().get(var1), this.mutex);
         }
      }

      public List<V> removeAll(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      public List<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(var1, var2);
         }
      }
   }

   private static class SynchronizedMultimap<K, V> extends Synchronized.SynchronizedObject implements Multimap<K, V> {
      transient Set<K> keySet;
      transient Collection<V> valuesCollection;
      transient Collection<Entry<K, V>> entries;
      transient Map<K, Collection<V>> asMap;
      transient Multiset<K> keys;
      private static final long serialVersionUID = 0L;

      Multimap<K, V> delegate() {
         return (Multimap)super.delegate();
      }

      SynchronizedMultimap(Multimap<K, V> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      public int size() {
         synchronized(this.mutex) {
            return this.delegate().size();
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      public boolean containsKey(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().containsKey(var1);
         }
      }

      public boolean containsValue(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().containsValue(var1);
         }
      }

      public boolean containsEntry(Object var1, Object var2) {
         synchronized(this.mutex) {
            return this.delegate().containsEntry(var1, var2);
         }
      }

      public Collection<V> get(K var1) {
         synchronized(this.mutex) {
            return Synchronized.typePreservingCollection(this.delegate().get(var1), this.mutex);
         }
      }

      public boolean put(K var1, V var2) {
         synchronized(this.mutex) {
            return this.delegate().put(var1, var2);
         }
      }

      public boolean putAll(K var1, Iterable<? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().putAll(var1, var2);
         }
      }

      public boolean putAll(Multimap<? extends K, ? extends V> var1) {
         synchronized(this.mutex) {
            return this.delegate().putAll(var1);
         }
      }

      public Collection<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(var1, var2);
         }
      }

      public boolean remove(Object var1, Object var2) {
         synchronized(this.mutex) {
            return this.delegate().remove(var1, var2);
         }
      }

      public Collection<V> removeAll(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      public void clear() {
         synchronized(this.mutex) {
            this.delegate().clear();
         }
      }

      public Set<K> keySet() {
         synchronized(this.mutex) {
            if (this.keySet == null) {
               this.keySet = Synchronized.typePreservingSet(this.delegate().keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      public Collection<V> values() {
         synchronized(this.mutex) {
            if (this.valuesCollection == null) {
               this.valuesCollection = Synchronized.collection(this.delegate().values(), this.mutex);
            }

            return this.valuesCollection;
         }
      }

      public Collection<Entry<K, V>> entries() {
         synchronized(this.mutex) {
            if (this.entries == null) {
               this.entries = Synchronized.typePreservingCollection(this.delegate().entries(), this.mutex);
            }

            return this.entries;
         }
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         synchronized(this.mutex) {
            this.delegate().forEach(var1);
         }
      }

      public Map<K, Collection<V>> asMap() {
         synchronized(this.mutex) {
            if (this.asMap == null) {
               this.asMap = new Synchronized.SynchronizedAsMap(this.delegate().asMap(), this.mutex);
            }

            return this.asMap;
         }
      }

      public Multiset<K> keys() {
         synchronized(this.mutex) {
            if (this.keys == null) {
               this.keys = Synchronized.multiset(this.delegate().keys(), this.mutex);
            }

            return this.keys;
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedMultiset<E> extends Synchronized.SynchronizedCollection<E> implements Multiset<E> {
      transient Set<E> elementSet;
      transient Set<Multiset.Entry<E>> entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedMultiset(Multiset<E> var1, @Nullable Object var2) {
         super(var1, var2, null);
      }

      Multiset<E> delegate() {
         return (Multiset)super.delegate();
      }

      public int count(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().count(var1);
         }
      }

      public int add(E var1, int var2) {
         synchronized(this.mutex) {
            return this.delegate().add(var1, var2);
         }
      }

      public int remove(Object var1, int var2) {
         synchronized(this.mutex) {
            return this.delegate().remove(var1, var2);
         }
      }

      public int setCount(E var1, int var2) {
         synchronized(this.mutex) {
            return this.delegate().setCount(var1, var2);
         }
      }

      public boolean setCount(E var1, int var2, int var3) {
         synchronized(this.mutex) {
            return this.delegate().setCount(var1, var2, var3);
         }
      }

      public Set<E> elementSet() {
         synchronized(this.mutex) {
            if (this.elementSet == null) {
               this.elementSet = Synchronized.typePreservingSet(this.delegate().elementSet(), this.mutex);
            }

            return this.elementSet;
         }
      }

      public Set<Multiset.Entry<E>> entrySet() {
         synchronized(this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = Synchronized.typePreservingSet(this.delegate().entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedRandomAccessList<E> extends Synchronized.SynchronizedList<E> implements RandomAccess {
      private static final long serialVersionUID = 0L;

      SynchronizedRandomAccessList(List<E> var1, @Nullable Object var2) {
         super(var1, var2);
      }
   }

   private static class SynchronizedList<E> extends Synchronized.SynchronizedCollection<E> implements List<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedList(List<E> var1, @Nullable Object var2) {
         super(var1, var2, null);
      }

      List<E> delegate() {
         return (List)super.delegate();
      }

      public void add(int var1, E var2) {
         synchronized(this.mutex) {
            this.delegate().add(var1, var2);
         }
      }

      public boolean addAll(int var1, Collection<? extends E> var2) {
         synchronized(this.mutex) {
            return this.delegate().addAll(var1, var2);
         }
      }

      public E get(int var1) {
         synchronized(this.mutex) {
            return this.delegate().get(var1);
         }
      }

      public int indexOf(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().indexOf(var1);
         }
      }

      public int lastIndexOf(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().lastIndexOf(var1);
         }
      }

      public ListIterator<E> listIterator() {
         return this.delegate().listIterator();
      }

      public ListIterator<E> listIterator(int var1) {
         return this.delegate().listIterator(var1);
      }

      public E remove(int var1) {
         synchronized(this.mutex) {
            return this.delegate().remove(var1);
         }
      }

      public E set(int var1, E var2) {
         synchronized(this.mutex) {
            return this.delegate().set(var1, var2);
         }
      }

      public void replaceAll(UnaryOperator<E> var1) {
         synchronized(this.mutex) {
            this.delegate().replaceAll(var1);
         }
      }

      public void sort(Comparator<? super E> var1) {
         synchronized(this.mutex) {
            this.delegate().sort(var1);
         }
      }

      public List<E> subList(int var1, int var2) {
         synchronized(this.mutex) {
            return Synchronized.list(this.delegate().subList(var1, var2), this.mutex);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   static class SynchronizedSortedSet<E> extends Synchronized.SynchronizedSet<E> implements SortedSet<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedSet(SortedSet<E> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      SortedSet<E> delegate() {
         return (SortedSet)super.delegate();
      }

      public Comparator<? super E> comparator() {
         synchronized(this.mutex) {
            return this.delegate().comparator();
         }
      }

      public SortedSet<E> subSet(E var1, E var2) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().subSet(var1, var2), this.mutex);
         }
      }

      public SortedSet<E> headSet(E var1) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().headSet(var1), this.mutex);
         }
      }

      public SortedSet<E> tailSet(E var1) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().tailSet(var1), this.mutex);
         }
      }

      public E first() {
         synchronized(this.mutex) {
            return this.delegate().first();
         }
      }

      public E last() {
         synchronized(this.mutex) {
            return this.delegate().last();
         }
      }
   }

   static class SynchronizedSet<E> extends Synchronized.SynchronizedCollection<E> implements Set<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedSet(Set<E> var1, @Nullable Object var2) {
         super(var1, var2, null);
      }

      Set<E> delegate() {
         return (Set)super.delegate();
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   @VisibleForTesting
   static class SynchronizedCollection<E> extends Synchronized.SynchronizedObject implements Collection<E> {
      private static final long serialVersionUID = 0L;

      private SynchronizedCollection(Collection<E> var1, @Nullable Object var2) {
         super(var1, var2);
      }

      Collection<E> delegate() {
         return (Collection)super.delegate();
      }

      public boolean add(E var1) {
         synchronized(this.mutex) {
            return this.delegate().add(var1);
         }
      }

      public boolean addAll(Collection<? extends E> var1) {
         synchronized(this.mutex) {
            return this.delegate().addAll(var1);
         }
      }

      public void clear() {
         synchronized(this.mutex) {
            this.delegate().clear();
         }
      }

      public boolean contains(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().contains(var1);
         }
      }

      public boolean containsAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return this.delegate().containsAll(var1);
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      public Iterator<E> iterator() {
         return this.delegate().iterator();
      }

      public Spliterator<E> spliterator() {
         synchronized(this.mutex) {
            return this.delegate().spliterator();
         }
      }

      public Stream<E> stream() {
         synchronized(this.mutex) {
            return this.delegate().stream();
         }
      }

      public Stream<E> parallelStream() {
         synchronized(this.mutex) {
            return this.delegate().parallelStream();
         }
      }

      public void forEach(Consumer<? super E> var1) {
         synchronized(this.mutex) {
            this.delegate().forEach(var1);
         }
      }

      public boolean remove(Object var1) {
         synchronized(this.mutex) {
            return this.delegate().remove(var1);
         }
      }

      public boolean removeAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      public boolean retainAll(Collection<?> var1) {
         synchronized(this.mutex) {
            return this.delegate().retainAll(var1);
         }
      }

      public boolean removeIf(Predicate<? super E> var1) {
         synchronized(this.mutex) {
            return this.delegate().removeIf(var1);
         }
      }

      public int size() {
         synchronized(this.mutex) {
            return this.delegate().size();
         }
      }

      public Object[] toArray() {
         synchronized(this.mutex) {
            return this.delegate().toArray();
         }
      }

      public <T> T[] toArray(T[] var1) {
         synchronized(this.mutex) {
            return this.delegate().toArray(var1);
         }
      }

      // $FF: synthetic method
      SynchronizedCollection(Collection var1, Object var2, Object var3) {
         this(var1, var2);
      }
   }

   static class SynchronizedObject implements Serializable {
      final Object delegate;
      final Object mutex;
      @GwtIncompatible
      private static final long serialVersionUID = 0L;

      SynchronizedObject(Object var1, @Nullable Object var2) {
         super();
         this.delegate = Preconditions.checkNotNull(var1);
         this.mutex = var2 == null ? this : var2;
      }

      Object delegate() {
         return this.delegate;
      }

      public String toString() {
         synchronized(this.mutex) {
            return this.delegate.toString();
         }
      }

      @GwtIncompatible
      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.mutex) {
            var1.defaultWriteObject();
         }
      }
   }
}
