package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2ByteSortedMap extends AbstractDouble2ByteMap implements Double2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2ByteSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractDouble2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Double2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Double2ByteMap.Entry)this.i.next()).getByteValue();
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
         return new AbstractDouble2ByteSortedMap.ValuesIterator(Double2ByteSortedMaps.fastIterator(AbstractDouble2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractDouble2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2ByteMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2ByteMap.Entry)this.i.previous()).getDoubleKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(double var1) {
         return AbstractDouble2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ByteSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2ByteSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2ByteSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2ByteSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2ByteSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2ByteSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2ByteSortedMap.KeySetIterator(AbstractDouble2ByteSortedMap.this.double2ByteEntrySet().iterator(new AbstractDouble2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2ByteSortedMap.KeySetIterator(Double2ByteSortedMaps.fastIterator(AbstractDouble2ByteSortedMap.this));
      }
   }
}
