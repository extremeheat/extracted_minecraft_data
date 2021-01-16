package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractLong2ObjectSortedMap<V> extends AbstractLong2ObjectMap<V> implements Long2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2ObjectSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractLong2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Long2ObjectMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      protected ValuesCollection() {
         super();
      }

      public ObjectIterator<V> iterator() {
         return new AbstractLong2ObjectSortedMap.ValuesIterator(Long2ObjectSortedMaps.fastIterator(AbstractLong2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractLong2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2ObjectMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2ObjectMap.Entry)this.i.previous()).getLongKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractLongSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(long var1) {
         return AbstractLong2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ObjectSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2ObjectSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2ObjectSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2ObjectSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2ObjectSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2ObjectSortedMap.KeySetIterator(AbstractLong2ObjectSortedMap.this.long2ObjectEntrySet().iterator(new AbstractLong2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2ObjectSortedMap.KeySetIterator(Long2ObjectSortedMaps.fastIterator(AbstractLong2ObjectSortedMap.this));
      }
   }
}
