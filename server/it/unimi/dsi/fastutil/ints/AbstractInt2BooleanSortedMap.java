package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2BooleanSortedMap extends AbstractInt2BooleanMap implements Int2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2BooleanSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractInt2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Int2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Int2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
         return new AbstractInt2BooleanSortedMap.ValuesIterator(Int2BooleanSortedMaps.fastIterator(AbstractInt2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractInt2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2BooleanMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2BooleanMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2BooleanSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2BooleanSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2BooleanSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2BooleanSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2BooleanSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2BooleanSortedMap.KeySetIterator(AbstractInt2BooleanSortedMap.this.int2BooleanEntrySet().iterator(new AbstractInt2BooleanMap.BasicEntry(var1, false)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2BooleanSortedMap.KeySetIterator(Int2BooleanSortedMaps.fastIterator(AbstractInt2BooleanSortedMap.this));
      }
   }
}
