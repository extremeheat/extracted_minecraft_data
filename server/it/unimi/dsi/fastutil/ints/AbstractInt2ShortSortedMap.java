package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractInt2ShortSortedMap extends AbstractInt2ShortMap implements Int2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2ShortSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractInt2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Int2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Int2ShortMap.Entry)this.i.next()).getShortValue();
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
         return new AbstractInt2ShortSortedMap.ValuesIterator(Int2ShortSortedMaps.fastIterator(AbstractInt2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractInt2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2ShortMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2ShortMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ShortSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2ShortSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2ShortSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2ShortSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2ShortSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2ShortSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2ShortSortedMap.KeySetIterator(AbstractInt2ShortSortedMap.this.int2ShortEntrySet().iterator(new AbstractInt2ShortMap.BasicEntry(var1, (short)0)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2ShortSortedMap.KeySetIterator(Int2ShortSortedMaps.fastIterator(AbstractInt2ShortSortedMap.this));
      }
   }
}
