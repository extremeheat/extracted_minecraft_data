package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2CharSortedMap extends AbstractShort2CharMap implements Short2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2CharSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractShort2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Short2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Short2CharMap.Entry)this.i.next()).getCharValue();
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
         return new AbstractShort2CharSortedMap.ValuesIterator(Short2CharSortedMaps.fastIterator(AbstractShort2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractShort2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2CharMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2CharMap.Entry)this.i.previous()).getShortKey();
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
         return AbstractShort2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2CharSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2CharSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2CharSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2CharSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2CharSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2CharSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2CharSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2CharSortedMap.KeySetIterator(AbstractShort2CharSortedMap.this.short2CharEntrySet().iterator(new AbstractShort2CharMap.BasicEntry(var1, '\u0000')));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2CharSortedMap.KeySetIterator(Short2CharSortedMaps.fastIterator(AbstractShort2CharSortedMap.this));
      }
   }
}
