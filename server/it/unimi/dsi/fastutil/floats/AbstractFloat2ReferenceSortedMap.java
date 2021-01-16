package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractFloat2ReferenceSortedMap<V> extends AbstractFloat2ReferenceMap<V> implements Float2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2ReferenceSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractFloat2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Float2ReferenceMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractReferenceCollection<V> {
      protected ValuesCollection() {
         super();
      }

      public ObjectIterator<V> iterator() {
         return new AbstractFloat2ReferenceSortedMap.ValuesIterator(Float2ReferenceSortedMaps.fastIterator(AbstractFloat2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractFloat2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2ReferenceMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2ReferenceMap.Entry)this.i.previous()).getFloatKey();
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
         return AbstractFloat2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ReferenceSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2ReferenceSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2ReferenceSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2ReferenceSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2ReferenceSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2ReferenceSortedMap.KeySetIterator(AbstractFloat2ReferenceSortedMap.this.float2ReferenceEntrySet().iterator(new AbstractFloat2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2ReferenceSortedMap.KeySetIterator(Float2ReferenceSortedMaps.fastIterator(AbstractFloat2ReferenceSortedMap.this));
      }
   }
}
