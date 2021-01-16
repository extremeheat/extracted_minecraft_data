package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;

@GwtIncompatible
public abstract class ForwardingNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V> {
   protected ForwardingNavigableMap() {
      super();
   }

   protected abstract NavigableMap<K, V> delegate();

   public Entry<K, V> lowerEntry(K var1) {
      return this.delegate().lowerEntry(var1);
   }

   protected Entry<K, V> standardLowerEntry(K var1) {
      return this.headMap(var1, false).lastEntry();
   }

   public K lowerKey(K var1) {
      return this.delegate().lowerKey(var1);
   }

   protected K standardLowerKey(K var1) {
      return Maps.keyOrNull(this.lowerEntry(var1));
   }

   public Entry<K, V> floorEntry(K var1) {
      return this.delegate().floorEntry(var1);
   }

   protected Entry<K, V> standardFloorEntry(K var1) {
      return this.headMap(var1, true).lastEntry();
   }

   public K floorKey(K var1) {
      return this.delegate().floorKey(var1);
   }

   protected K standardFloorKey(K var1) {
      return Maps.keyOrNull(this.floorEntry(var1));
   }

   public Entry<K, V> ceilingEntry(K var1) {
      return this.delegate().ceilingEntry(var1);
   }

   protected Entry<K, V> standardCeilingEntry(K var1) {
      return this.tailMap(var1, true).firstEntry();
   }

   public K ceilingKey(K var1) {
      return this.delegate().ceilingKey(var1);
   }

   protected K standardCeilingKey(K var1) {
      return Maps.keyOrNull(this.ceilingEntry(var1));
   }

   public Entry<K, V> higherEntry(K var1) {
      return this.delegate().higherEntry(var1);
   }

   protected Entry<K, V> standardHigherEntry(K var1) {
      return this.tailMap(var1, false).firstEntry();
   }

   public K higherKey(K var1) {
      return this.delegate().higherKey(var1);
   }

   protected K standardHigherKey(K var1) {
      return Maps.keyOrNull(this.higherEntry(var1));
   }

   public Entry<K, V> firstEntry() {
      return this.delegate().firstEntry();
   }

   protected Entry<K, V> standardFirstEntry() {
      return (Entry)Iterables.getFirst(this.entrySet(), (Object)null);
   }

   protected K standardFirstKey() {
      Entry var1 = this.firstEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1.getKey();
      }
   }

   public Entry<K, V> lastEntry() {
      return this.delegate().lastEntry();
   }

   protected Entry<K, V> standardLastEntry() {
      return (Entry)Iterables.getFirst(this.descendingMap().entrySet(), (Object)null);
   }

   protected K standardLastKey() {
      Entry var1 = this.lastEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1.getKey();
      }
   }

   public Entry<K, V> pollFirstEntry() {
      return this.delegate().pollFirstEntry();
   }

   protected Entry<K, V> standardPollFirstEntry() {
      return (Entry)Iterators.pollNext(this.entrySet().iterator());
   }

   public Entry<K, V> pollLastEntry() {
      return this.delegate().pollLastEntry();
   }

   protected Entry<K, V> standardPollLastEntry() {
      return (Entry)Iterators.pollNext(this.descendingMap().entrySet().iterator());
   }

   public NavigableMap<K, V> descendingMap() {
      return this.delegate().descendingMap();
   }

   public NavigableSet<K> navigableKeySet() {
      return this.delegate().navigableKeySet();
   }

   public NavigableSet<K> descendingKeySet() {
      return this.delegate().descendingKeySet();
   }

   @Beta
   protected NavigableSet<K> standardDescendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   protected SortedMap<K, V> standardSubMap(K var1, K var2) {
      return this.subMap(var1, true, var2, false);
   }

   public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
      return this.delegate().subMap(var1, var2, var3, var4);
   }

   public NavigableMap<K, V> headMap(K var1, boolean var2) {
      return this.delegate().headMap(var1, var2);
   }

   public NavigableMap<K, V> tailMap(K var1, boolean var2) {
      return this.delegate().tailMap(var1, var2);
   }

   protected SortedMap<K, V> standardHeadMap(K var1) {
      return this.headMap(var1, false);
   }

   protected SortedMap<K, V> standardTailMap(K var1) {
      return this.tailMap(var1, true);
   }

   @Beta
   protected class StandardNavigableKeySet extends Maps.NavigableKeySet<K, V> {
      public StandardNavigableKeySet() {
         super(ForwardingNavigableMap.this);
      }
   }

   @Beta
   protected class StandardDescendingMap extends Maps.DescendingMap<K, V> {
      public StandardDescendingMap() {
         super();
      }

      NavigableMap<K, V> forward() {
         return ForwardingNavigableMap.this;
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         this.forward().replaceAll(var1);
      }

      protected Iterator<Entry<K, V>> entryIterator() {
         return new Iterator<Entry<K, V>>() {
            private Entry<K, V> toRemove = null;
            private Entry<K, V> nextOrNull = StandardDescendingMap.this.forward().lastEntry();

            public boolean hasNext() {
               return this.nextOrNull != null;
            }

            public Entry<K, V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  Entry var1;
                  try {
                     var1 = this.nextOrNull;
                  } finally {
                     this.toRemove = this.nextOrNull;
                     this.nextOrNull = StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
                  }

                  return var1;
               }
            }

            public void remove() {
               CollectPreconditions.checkRemove(this.toRemove != null);
               StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
               this.toRemove = null;
            }
         };
      }
   }
}
