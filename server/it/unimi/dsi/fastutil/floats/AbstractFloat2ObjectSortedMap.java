package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractFloat2ObjectSortedMap<V> extends AbstractFloat2ObjectMap<V> implements Float2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2ObjectSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractFloat2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Float2ObjectMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      protected ValuesCollection() {
         super();
      }

      public ObjectIterator<V> iterator() {
         return new AbstractFloat2ObjectSortedMap.ValuesIterator(Float2ObjectSortedMaps.fastIterator(AbstractFloat2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractFloat2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2ObjectMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2ObjectMap.Entry)this.i.previous()).getFloatKey();
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
         return AbstractFloat2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2ObjectSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2ObjectSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2ObjectSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2ObjectSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2ObjectSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2ObjectSortedMap.KeySetIterator(AbstractFloat2ObjectSortedMap.this.float2ObjectEntrySet().iterator(new AbstractFloat2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2ObjectSortedMap.KeySetIterator(Float2ObjectSortedMaps.fastIterator(AbstractFloat2ObjectSortedMap.this));
      }
   }
}
