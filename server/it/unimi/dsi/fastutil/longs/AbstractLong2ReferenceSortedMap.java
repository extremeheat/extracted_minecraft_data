package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractLong2ReferenceSortedMap<V> extends AbstractLong2ReferenceMap<V> implements Long2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2ReferenceSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractLong2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Long2ReferenceMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractReferenceCollection<V> {
      protected ValuesCollection() {
         super();
      }

      public ObjectIterator<V> iterator() {
         return new AbstractLong2ReferenceSortedMap.ValuesIterator(Long2ReferenceSortedMaps.fastIterator(AbstractLong2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractLong2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2ReferenceMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2ReferenceMap.Entry)this.i.previous()).getLongKey();
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
         return AbstractLong2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ReferenceSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2ReferenceSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2ReferenceSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2ReferenceSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2ReferenceSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2ReferenceSortedMap.KeySetIterator(AbstractLong2ReferenceSortedMap.this.long2ReferenceEntrySet().iterator(new AbstractLong2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2ReferenceSortedMap.KeySetIterator(Long2ReferenceSortedMaps.fastIterator(AbstractLong2ReferenceSortedMap.this));
      }
   }
}
