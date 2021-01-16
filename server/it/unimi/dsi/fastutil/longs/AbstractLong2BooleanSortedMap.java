package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2BooleanSortedMap extends AbstractLong2BooleanMap implements Long2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2BooleanSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractLong2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Long2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Long2BooleanMap.Entry)this.i.next()).getBooleanValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractBooleanCollection {
      protected ValuesCollection() {
         super();
      }

      public BooleanIterator iterator() {
         return new AbstractLong2BooleanSortedMap.ValuesIterator(Long2BooleanSortedMaps.fastIterator(AbstractLong2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractLong2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2BooleanMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2BooleanMap.Entry)this.i.previous()).getLongKey();
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
         return AbstractLong2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2BooleanSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2BooleanSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2BooleanSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2BooleanSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2BooleanSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2BooleanSortedMap.KeySetIterator(AbstractLong2BooleanSortedMap.this.long2BooleanEntrySet().iterator(new AbstractLong2BooleanMap.BasicEntry(var1, false)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2BooleanSortedMap.KeySetIterator(Long2BooleanSortedMaps.fastIterator(AbstractLong2BooleanSortedMap.this));
      }
   }
}
