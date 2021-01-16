package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractByte2ObjectSortedMap<V> extends AbstractByte2ObjectMap<V> implements Byte2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2ObjectSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractByte2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Byte2ObjectMap.Entry)this.i.next()).getValue();
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
         return new AbstractByte2ObjectSortedMap.ValuesIterator(Byte2ObjectSortedMaps.fastIterator(AbstractByte2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractByte2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2ObjectMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2ObjectMap.Entry)this.i.previous()).getByteKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractByteSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(byte var1) {
         return AbstractByte2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ObjectSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2ObjectSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2ObjectSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2ObjectSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2ObjectSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2ObjectSortedMap.KeySetIterator(AbstractByte2ObjectSortedMap.this.byte2ObjectEntrySet().iterator(new AbstractByte2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2ObjectSortedMap.KeySetIterator(Byte2ObjectSortedMaps.fastIterator(AbstractByte2ObjectSortedMap.this));
      }
   }
}
