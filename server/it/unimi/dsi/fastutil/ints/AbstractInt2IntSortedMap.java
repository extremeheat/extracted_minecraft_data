package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2IntSortedMap extends AbstractInt2IntMap implements Int2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2IntSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractInt2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Int2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2IntMap.Entry)this.i.next()).getIntValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractIntCollection {
      protected ValuesCollection() {
         super();
      }

      public IntIterator iterator() {
         return new AbstractInt2IntSortedMap.ValuesIterator(Int2IntSortedMaps.fastIterator(AbstractInt2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractInt2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2IntMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2IntMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2IntSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2IntSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2IntSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2IntSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2IntSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2IntSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2IntSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2IntSortedMap.KeySetIterator(AbstractInt2IntSortedMap.this.int2IntEntrySet().iterator(new AbstractInt2IntMap.BasicEntry(var1, 0)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2IntSortedMap.KeySetIterator(Int2IntSortedMaps.fastIterator(AbstractInt2IntSortedMap.this));
      }
   }
}
