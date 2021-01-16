package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2LongSortedMap extends AbstractFloat2LongMap implements Float2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2LongSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractFloat2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Float2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Float2LongMap.Entry)this.i.next()).getLongValue();
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
         return new AbstractFloat2LongSortedMap.ValuesIterator(Float2LongSortedMaps.fastIterator(AbstractFloat2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractFloat2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2LongMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2LongMap.Entry)this.i.previous()).getFloatKey();
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
         return AbstractFloat2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2LongSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2LongSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2LongSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2LongSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2LongSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2LongSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2LongSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2LongSortedMap.KeySetIterator(AbstractFloat2LongSortedMap.this.float2LongEntrySet().iterator(new AbstractFloat2LongMap.BasicEntry(var1, 0L)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2LongSortedMap.KeySetIterator(Float2LongSortedMaps.fastIterator(AbstractFloat2LongSortedMap.this));
      }
   }
}
