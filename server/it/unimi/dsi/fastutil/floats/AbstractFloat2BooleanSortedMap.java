package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2BooleanSortedMap extends AbstractFloat2BooleanMap implements Float2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2BooleanSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractFloat2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Float2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Float2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
         return new AbstractFloat2BooleanSortedMap.ValuesIterator(Float2BooleanSortedMaps.fastIterator(AbstractFloat2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractFloat2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2BooleanMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2BooleanMap.Entry)this.i.previous()).getFloatKey();
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
         return AbstractFloat2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2BooleanSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2BooleanSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2BooleanSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2BooleanSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2BooleanSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2BooleanSortedMap.KeySetIterator(AbstractFloat2BooleanSortedMap.this.float2BooleanEntrySet().iterator(new AbstractFloat2BooleanMap.BasicEntry(var1, false)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2BooleanSortedMap.KeySetIterator(Float2BooleanSortedMaps.fastIterator(AbstractFloat2BooleanSortedMap.this));
      }
   }
}
