package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2ByteSortedMap extends AbstractFloat2ByteMap implements Float2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2ByteSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractFloat2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Float2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Float2ByteMap.Entry)this.i.next()).getByteValue();
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
         return new AbstractFloat2ByteSortedMap.ValuesIterator(Float2ByteSortedMaps.fastIterator(AbstractFloat2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractFloat2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2ByteMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2ByteMap.Entry)this.i.previous()).getFloatKey();
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
         return AbstractFloat2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ByteSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2ByteSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2ByteSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2ByteSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2ByteSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2ByteSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2ByteSortedMap.KeySetIterator(AbstractFloat2ByteSortedMap.this.float2ByteEntrySet().iterator(new AbstractFloat2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2ByteSortedMap.KeySetIterator(Float2ByteSortedMaps.fastIterator(AbstractFloat2ByteSortedMap.this));
      }
   }
}
