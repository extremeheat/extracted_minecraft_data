package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2LongSortedMap extends AbstractInt2LongMap implements Int2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2LongSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractInt2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Int2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Int2LongMap.Entry)this.i.next()).getLongValue();
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
         return new AbstractInt2LongSortedMap.ValuesIterator(Int2LongSortedMaps.fastIterator(AbstractInt2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractInt2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2LongMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2LongMap.Entry)this.i.previous()).getIntKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractIntSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(int var1) {
         return AbstractInt2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2LongSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2LongSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2LongSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2LongSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2LongSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2LongSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2LongSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2LongSortedMap.KeySetIterator(AbstractInt2LongSortedMap.this.int2LongEntrySet().iterator(new AbstractInt2LongMap.BasicEntry(var1, 0L)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2LongSortedMap.KeySetIterator(Int2LongSortedMaps.fastIterator(AbstractInt2LongSortedMap.this));
      }
   }
}
