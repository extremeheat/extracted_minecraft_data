package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Comparator;

public abstract class AbstractReference2BooleanSortedMap<K> extends AbstractReference2BooleanMap<K> implements Reference2BooleanSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2BooleanSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractReference2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Reference2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
         return new AbstractReference2BooleanSortedMap.ValuesIterator(Reference2BooleanSortedMaps.fastIterator(AbstractReference2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractReference2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2BooleanMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2BooleanMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2BooleanSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2BooleanSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2BooleanSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2BooleanSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2BooleanSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2BooleanSortedMap.KeySetIterator(AbstractReference2BooleanSortedMap.this.reference2BooleanEntrySet().iterator(new AbstractReference2BooleanMap.BasicEntry(var1, false)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2BooleanSortedMap.KeySetIterator(Reference2BooleanSortedMaps.fastIterator(AbstractReference2BooleanSortedMap.this));
      }
   }
}
