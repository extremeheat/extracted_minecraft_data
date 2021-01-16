package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2CharSortedMap extends AbstractLong2CharMap implements Long2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2CharSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractLong2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Long2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Long2CharMap.Entry)this.i.next()).getCharValue();
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
         return new AbstractLong2CharSortedMap.ValuesIterator(Long2CharSortedMaps.fastIterator(AbstractLong2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractLong2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2CharMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2CharMap.Entry)this.i.previous()).getLongKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractLongSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(long var1) {
         return AbstractLong2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2CharSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2CharSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2CharSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2CharSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2CharSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2CharSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2CharSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2CharSortedMap.KeySetIterator(AbstractLong2CharSortedMap.this.long2CharEntrySet().iterator(new AbstractLong2CharMap.BasicEntry(var1, '\u0000')));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2CharSortedMap.KeySetIterator(Long2CharSortedMaps.fastIterator(AbstractLong2CharSortedMap.this));
      }
   }
}
