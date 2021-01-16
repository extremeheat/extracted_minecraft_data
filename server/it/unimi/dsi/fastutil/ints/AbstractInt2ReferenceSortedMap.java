package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractInt2ReferenceSortedMap<V> extends AbstractInt2ReferenceMap<V> implements Int2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2ReferenceSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractInt2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Int2ReferenceMap.Entry)this.i.next()).getValue();
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
         return new AbstractInt2ReferenceSortedMap.ValuesIterator(Int2ReferenceSortedMaps.fastIterator(AbstractInt2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractInt2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2ReferenceMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2ReferenceMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2ReferenceSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2ReferenceSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2ReferenceSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2ReferenceSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2ReferenceSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2ReferenceSortedMap.KeySetIterator(AbstractInt2ReferenceSortedMap.this.int2ReferenceEntrySet().iterator(new AbstractInt2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2ReferenceSortedMap.KeySetIterator(Int2ReferenceSortedMaps.fastIterator(AbstractInt2ReferenceSortedMap.this));
      }
   }
}
