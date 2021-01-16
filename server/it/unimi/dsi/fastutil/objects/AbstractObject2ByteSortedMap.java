package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Comparator;

public abstract class AbstractObject2ByteSortedMap<K> extends AbstractObject2ByteMap<K> implements Object2ByteSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ByteSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractObject2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements ByteIterator {
      protected final ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Object2ByteMap.Entry)this.i.next()).getByteValue();
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
         return new AbstractObject2ByteSortedMap.ValuesIterator(Object2ByteSortedMaps.fastIterator(AbstractObject2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractObject2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2ByteMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2ByteMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      protected KeySet() {
         super();
      }

      public boolean contains(Object var1) {
         return AbstractObject2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ByteSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2ByteSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2ByteSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2ByteSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2ByteSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2ByteSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2ByteSortedMap.KeySetIterator(AbstractObject2ByteSortedMap.this.object2ByteEntrySet().iterator(new AbstractObject2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ByteSortedMap.KeySetIterator(Object2ByteSortedMaps.fastIterator(AbstractObject2ByteSortedMap.this));
      }
   }
}
