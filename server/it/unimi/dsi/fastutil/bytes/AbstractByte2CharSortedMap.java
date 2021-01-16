package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2CharSortedMap extends AbstractByte2CharMap implements Byte2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2CharSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractByte2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Byte2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Byte2CharMap.Entry)this.i.next()).getCharValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractCharCollection {
      protected ValuesCollection() {
         super();
      }

      public CharIterator iterator() {
         return new AbstractByte2CharSortedMap.ValuesIterator(Byte2CharSortedMaps.fastIterator(AbstractByte2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractByte2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2CharMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2CharMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2CharSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2CharSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2CharSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2CharSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2CharSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2CharSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2CharSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2CharSortedMap.KeySetIterator(AbstractByte2CharSortedMap.this.byte2CharEntrySet().iterator(new AbstractByte2CharMap.BasicEntry(var1, '\u0000')));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2CharSortedMap.KeySetIterator(Byte2CharSortedMaps.fastIterator(AbstractByte2CharSortedMap.this));
      }
   }
}
