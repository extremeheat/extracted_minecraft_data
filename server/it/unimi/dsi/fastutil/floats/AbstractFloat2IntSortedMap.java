package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2IntSortedMap extends AbstractFloat2IntMap implements Float2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2IntSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractFloat2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Float2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Float2IntMap.Entry)this.i.next()).getIntValue();
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
         return new AbstractFloat2IntSortedMap.ValuesIterator(Float2IntSortedMaps.fastIterator(AbstractFloat2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractFloat2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2IntMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2IntMap.Entry)this.i.previous()).getFloatKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractFloatSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(float var1) {
         return AbstractFloat2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2IntSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2IntSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2IntSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2IntSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2IntSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2IntSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2IntSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2IntSortedMap.KeySetIterator(AbstractFloat2IntSortedMap.this.float2IntEntrySet().iterator(new AbstractFloat2IntMap.BasicEntry(var1, 0)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2IntSortedMap.KeySetIterator(Float2IntSortedMaps.fastIterator(AbstractFloat2IntSortedMap.this));
      }
   }
}
