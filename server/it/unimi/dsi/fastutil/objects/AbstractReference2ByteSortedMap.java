package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Comparator;

public abstract class AbstractReference2ByteSortedMap<K> extends AbstractReference2ByteMap<K> implements Reference2ByteSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2ByteSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractReference2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements ByteIterator {
      protected final ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Reference2ByteMap.Entry)this.i.next()).getByteValue();
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
         return new AbstractReference2ByteSortedMap.ValuesIterator(Reference2ByteSortedMaps.fastIterator(AbstractReference2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractReference2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2ByteMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2ByteMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ByteSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2ByteSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2ByteSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2ByteSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2ByteSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2ByteSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2ByteSortedMap.KeySetIterator(AbstractReference2ByteSortedMap.this.reference2ByteEntrySet().iterator(new AbstractReference2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2ByteSortedMap.KeySetIterator(Reference2ByteSortedMaps.fastIterator(AbstractReference2ByteSortedMap.this));
      }
   }
}
