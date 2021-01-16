package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractFloat2ShortSortedMap extends AbstractFloat2ShortMap implements Float2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2ShortSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractFloat2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Float2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Float2ShortMap.Entry)this.i.next()).getShortValue();
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
         return new AbstractFloat2ShortSortedMap.ValuesIterator(Float2ShortSortedMaps.fastIterator(AbstractFloat2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractFloat2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2ShortMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2ShortMap.Entry)this.i.previous()).getFloatKey();
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
         return AbstractFloat2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ShortSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2ShortSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2ShortSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2ShortSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2ShortSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2ShortSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2ShortSortedMap.KeySetIterator(AbstractFloat2ShortSortedMap.this.float2ShortEntrySet().iterator(new AbstractFloat2ShortMap.BasicEntry(var1, (short)0)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2ShortSortedMap.KeySetIterator(Float2ShortSortedMaps.fastIterator(AbstractFloat2ShortSortedMap.this));
      }
   }
}
