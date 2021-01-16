package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2ByteSortedMap extends AbstractInt2ByteMap implements Int2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2ByteSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractInt2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Int2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Int2ByteMap.Entry)this.i.next()).getByteValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractByteCollection {
      protected ValuesCollection() {
         super();
      }

      public ByteIterator iterator() {
         return new AbstractInt2ByteSortedMap.ValuesIterator(Int2ByteSortedMaps.fastIterator(AbstractInt2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractInt2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2ByteMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2ByteMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ByteSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2ByteSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2ByteSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2ByteSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2ByteSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2ByteSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2ByteSortedMap.KeySetIterator(AbstractInt2ByteSortedMap.this.int2ByteEntrySet().iterator(new AbstractInt2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2ByteSortedMap.KeySetIterator(Int2ByteSortedMaps.fastIterator(AbstractInt2ByteSortedMap.this));
      }
   }
}
