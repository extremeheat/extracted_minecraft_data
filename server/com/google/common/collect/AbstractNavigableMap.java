package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtIncompatible
abstract class AbstractNavigableMap<K, V> extends Maps.IteratorBasedAbstractMap<K, V> implements NavigableMap<K, V> {
   AbstractNavigableMap() {
      super();
   }

   @Nullable
   public abstract V get(@Nullable Object var1);

   @Nullable
   public Entry<K, V> firstEntry() {
      return (Entry)Iterators.getNext(this.entryIterator(), (Object)null);
   }

   @Nullable
   public Entry<K, V> lastEntry() {
      return (Entry)Iterators.getNext(this.descendingEntryIterator(), (Object)null);
   }

   @Nullable
   public Entry<K, V> pollFirstEntry() {
      return (Entry)Iterators.pollNext(this.entryIterator());
   }

   @Nullable
   public Entry<K, V> pollLastEntry() {
      return (Entry)Iterators.pollNext(this.descendingEntryIterator());
   }

   public K firstKey() {
      Entry var1 = this.firstEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1.getKey();
      }
   }

   public K lastKey() {
      Entry var1 = this.lastEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1.getKey();
      }
   }

   @Nullable
   public Entry<K, V> lowerEntry(K var1) {
      return this.headMap(var1, false).lastEntry();
   }

   @Nullable
   public Entry<K, V> floorEntry(K var1) {
      return this.headMap(var1, true).lastEntry();
   }

   @Nullable
   public Entry<K, V> ceilingEntry(K var1) {
      return this.tailMap(var1, true).firstEntry();
   }

   @Nullable
   public Entry<K, V> higherEntry(K var1) {
      return this.tailMap(var1, false).firstEntry();
   }

   public K lowerKey(K var1) {
      return Maps.keyOrNull(this.lowerEntry(var1));
   }

   public K floorKey(K var1) {
      return Maps.keyOrNull(this.floorEntry(var1));
   }

   public K ceilingKey(K var1) {
      return Maps.keyOrNull(this.ceilingEntry(var1));
   }

   public K higherKey(K var1) {
      return Maps.keyOrNull(this.higherEntry(var1));
   }

   abstract Iterator<Entry<K, V>> descendingEntryIterator();

   public SortedMap<K, V> subMap(K var1, K var2) {
      return this.subMap(var1, true, var2, false);
   }

   public SortedMap<K, V> headMap(K var1) {
      return this.headMap(var1, false);
   }

   public SortedMap<K, V> tailMap(K var1) {
      return this.tailMap(var1, true);
   }

   public NavigableSet<K> navigableKeySet() {
      return new Maps.NavigableKeySet(this);
   }

   public Set<K> keySet() {
      return this.navigableKeySet();
   }

   public NavigableSet<K> descendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   public NavigableMap<K, V> descendingMap() {
      return new AbstractNavigableMap.DescendingMap();
   }

   private final class DescendingMap extends Maps.DescendingMap<K, V> {
      private DescendingMap() {
         super();
      }

      NavigableMap<K, V> forward() {
         return AbstractNavigableMap.this;
      }

      Iterator<Entry<K, V>> entryIterator() {
         return AbstractNavigableMap.this.descendingEntryIterator();
      }

      // $FF: synthetic method
      DescendingMap(Object var2) {
         this();
      }
   }
}
