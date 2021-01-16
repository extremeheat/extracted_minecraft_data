package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2LongSortedMap extends AbstractShort2LongMap implements Short2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2LongSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractShort2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Short2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Short2LongMap.Entry)this.i.next()).getLongValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractLongCollection {
      protected ValuesCollection() {
         super();
      }

      public LongIterator iterator() {
         return new AbstractShort2LongSortedMap.ValuesIterator(Short2LongSortedMaps.fastIterator(AbstractShort2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractShort2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2LongMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2LongMap.Entry)this.i.previous()).getShortKey();
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
         return AbstractShort2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2LongSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2LongSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2LongSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2LongSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2LongSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2LongSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2LongSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2LongSortedMap.KeySetIterator(AbstractShort2LongSortedMap.this.short2LongEntrySet().iterator(new AbstractShort2LongMap.BasicEntry(var1, 0L)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2LongSortedMap.KeySetIterator(Short2LongSortedMaps.fastIterator(AbstractShort2LongSortedMap.this));
      }
   }
}
