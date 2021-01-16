package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2ShortSortedMap extends AbstractShort2ShortMap implements Short2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2ShortSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractShort2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Short2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2ShortMap.Entry)this.i.next()).getShortValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractShortCollection {
      protected ValuesCollection() {
         super();
      }

      public ShortIterator iterator() {
         return new AbstractShort2ShortSortedMap.ValuesIterator(Short2ShortSortedMaps.fastIterator(AbstractShort2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractShort2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2ShortMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2ShortMap.Entry)this.i.previous()).getShortKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractShortSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(short var1) {
         return AbstractShort2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ShortSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2ShortSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2ShortSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2ShortSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2ShortSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2ShortSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2ShortSortedMap.KeySetIterator(AbstractShort2ShortSortedMap.this.short2ShortEntrySet().iterator(new AbstractShort2ShortMap.BasicEntry(var1, (short)0)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2ShortSortedMap.KeySetIterator(Short2ShortSortedMaps.fastIterator(AbstractShort2ShortSortedMap.this));
      }
   }
}
