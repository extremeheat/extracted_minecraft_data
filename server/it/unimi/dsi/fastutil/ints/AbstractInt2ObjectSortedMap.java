package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractInt2ObjectSortedMap<V> extends AbstractInt2ObjectMap<V> implements Int2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2ObjectSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractInt2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Int2ObjectMap.Entry)this.i.next()).getValue();
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
         return new AbstractInt2ObjectSortedMap.ValuesIterator(Int2ObjectSortedMaps.fastIterator(AbstractInt2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractInt2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2ObjectMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2ObjectMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ObjectSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2ObjectSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2ObjectSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2ObjectSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2ObjectSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2ObjectSortedMap.KeySetIterator(AbstractInt2ObjectSortedMap.this.int2ObjectEntrySet().iterator(new AbstractInt2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2ObjectSortedMap.KeySetIterator(Int2ObjectSortedMaps.fastIterator(AbstractInt2ObjectSortedMap.this));
      }
   }
}
