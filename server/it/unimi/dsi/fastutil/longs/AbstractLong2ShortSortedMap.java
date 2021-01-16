package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractLong2ShortSortedMap extends AbstractLong2ShortMap implements Long2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2ShortSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractLong2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Long2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Long2ShortMap.Entry)this.i.next()).getShortValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractShortCollection {
      protected ValuesCollection() {
         super();
      }

      public ShortIterator iterator() {
         return new AbstractLong2ShortSortedMap.ValuesIterator(Long2ShortSortedMaps.fastIterator(AbstractLong2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractLong2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2ShortMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2ShortMap.Entry)this.i.previous()).getLongKey();
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
         return AbstractLong2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ShortSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2ShortSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2ShortSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2ShortSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2ShortSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2ShortSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2ShortSortedMap.KeySetIterator(AbstractLong2ShortSortedMap.this.long2ShortEntrySet().iterator(new AbstractLong2ShortMap.BasicEntry(var1, (short)0)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2ShortSortedMap.KeySetIterator(Long2ShortSortedMaps.fastIterator(AbstractLong2ShortSortedMap.this));
      }
   }
}
