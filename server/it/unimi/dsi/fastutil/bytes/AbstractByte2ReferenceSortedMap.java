package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractByte2ReferenceSortedMap<V> extends AbstractByte2ReferenceMap<V> implements Byte2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2ReferenceSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractByte2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Byte2ReferenceMap.Entry)this.i.next()).getValue();
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
         return new AbstractByte2ReferenceSortedMap.ValuesIterator(Byte2ReferenceSortedMaps.fastIterator(AbstractByte2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractByte2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2ReferenceMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2ReferenceMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ReferenceSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2ReferenceSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2ReferenceSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2ReferenceSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2ReferenceSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2ReferenceSortedMap.KeySetIterator(AbstractByte2ReferenceSortedMap.this.byte2ReferenceEntrySet().iterator(new AbstractByte2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2ReferenceSortedMap.KeySetIterator(Byte2ReferenceSortedMaps.fastIterator(AbstractByte2ReferenceSortedMap.this));
      }
   }
}
