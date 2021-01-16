package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2IntSortedMap extends AbstractShort2IntMap implements Short2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2IntSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractShort2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Short2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Short2IntMap.Entry)this.i.next()).getIntValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractIntCollection {
      protected ValuesCollection() {
         super();
      }

      public IntIterator iterator() {
         return new AbstractShort2IntSortedMap.ValuesIterator(Short2IntSortedMaps.fastIterator(AbstractShort2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractShort2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2IntMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2IntMap.Entry)this.i.previous()).getShortKey();
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
         return AbstractShort2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2IntSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2IntSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2IntSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2IntSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2IntSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2IntSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2IntSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2IntSortedMap.KeySetIterator(AbstractShort2IntSortedMap.this.short2IntEntrySet().iterator(new AbstractShort2IntMap.BasicEntry(var1, 0)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2IntSortedMap.KeySetIterator(Short2IntSortedMaps.fastIterator(AbstractShort2IntSortedMap.this));
      }
   }
}
