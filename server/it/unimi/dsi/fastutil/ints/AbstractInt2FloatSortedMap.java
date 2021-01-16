package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2FloatSortedMap extends AbstractInt2FloatMap implements Int2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2FloatSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractInt2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Int2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Int2FloatMap.Entry)this.i.next()).getFloatValue();
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
         return new AbstractInt2FloatSortedMap.ValuesIterator(Int2FloatSortedMaps.fastIterator(AbstractInt2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractInt2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2FloatMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2FloatMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2FloatSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2FloatSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2FloatSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2FloatSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2FloatSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2FloatSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2FloatSortedMap.KeySetIterator(AbstractInt2FloatSortedMap.this.int2FloatEntrySet().iterator(new AbstractInt2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2FloatSortedMap.KeySetIterator(Int2FloatSortedMaps.fastIterator(AbstractInt2FloatSortedMap.this));
      }
   }
}
