package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2FloatSortedMap extends AbstractLong2FloatMap implements Long2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2FloatSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractLong2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Long2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Long2FloatMap.Entry)this.i.next()).getFloatValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractFloatCollection {
      protected ValuesCollection() {
         super();
      }

      public FloatIterator iterator() {
         return new AbstractLong2FloatSortedMap.ValuesIterator(Long2FloatSortedMaps.fastIterator(AbstractLong2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractLong2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2FloatMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2FloatMap.Entry)this.i.previous()).getLongKey();
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
         return AbstractLong2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2FloatSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2FloatSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2FloatSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2FloatSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2FloatSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2FloatSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2FloatSortedMap.KeySetIterator(AbstractLong2FloatSortedMap.this.long2FloatEntrySet().iterator(new AbstractLong2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2FloatSortedMap.KeySetIterator(Long2FloatSortedMaps.fastIterator(AbstractLong2FloatSortedMap.this));
      }
   }
}
