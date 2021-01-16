package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Comparator;

public abstract class AbstractObject2LongSortedMap<K> extends AbstractObject2LongMap<K> implements Object2LongSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2LongSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractObject2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements LongIterator {
      protected final ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2LongMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Object2LongMap.Entry)this.i.next()).getLongValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractLongCollection {
      protected ValuesCollection() {
         super();
      }

      public LongIterator iterator() {
         return new AbstractObject2LongSortedMap.ValuesIterator(Object2LongSortedMaps.fastIterator(AbstractObject2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractObject2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2LongMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2LongMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2LongMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      protected KeySet() {
         super();
      }

      public boolean contains(Object var1) {
         return AbstractObject2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2LongSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2LongSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2LongSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2LongSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2LongSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2LongSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2LongSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2LongSortedMap.KeySetIterator(AbstractObject2LongSortedMap.this.object2LongEntrySet().iterator(new AbstractObject2LongMap.BasicEntry(var1, 0L)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2LongSortedMap.KeySetIterator(Object2LongSortedMaps.fastIterator(AbstractObject2LongSortedMap.this));
      }
   }
}
