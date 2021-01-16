package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Comparator;

public abstract class AbstractReference2DoubleSortedMap<K> extends AbstractReference2DoubleMap<K> implements Reference2DoubleSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2DoubleSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractReference2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Reference2DoubleMap.Entry)this.i.next()).getDoubleValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractDoubleCollection {
      protected ValuesCollection() {
         super();
      }

      public DoubleIterator iterator() {
         return new AbstractReference2DoubleSortedMap.ValuesIterator(Reference2DoubleSortedMaps.fastIterator(AbstractReference2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractReference2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2DoubleMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2DoubleMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractReferenceSortedSet<K> {
      protected KeySet() {
         super();
      }

      public boolean contains(Object var1) {
         return AbstractReference2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2DoubleSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2DoubleSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2DoubleSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2DoubleSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2DoubleSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2DoubleSortedMap.KeySetIterator(AbstractReference2DoubleSortedMap.this.reference2DoubleEntrySet().iterator(new AbstractReference2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2DoubleSortedMap.KeySetIterator(Reference2DoubleSortedMaps.fastIterator(AbstractReference2DoubleSortedMap.this));
      }
   }
}
