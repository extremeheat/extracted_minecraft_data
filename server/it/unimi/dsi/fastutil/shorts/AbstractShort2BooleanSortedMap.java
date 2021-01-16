package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2BooleanSortedMap extends AbstractShort2BooleanMap implements Short2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2BooleanSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractShort2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Short2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Short2BooleanMap.Entry)this.i.next()).getBooleanValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractBooleanCollection {
      protected ValuesCollection() {
         super();
      }

      public BooleanIterator iterator() {
         return new AbstractShort2BooleanSortedMap.ValuesIterator(Short2BooleanSortedMaps.fastIterator(AbstractShort2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractShort2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2BooleanMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2BooleanMap.Entry)this.i.previous()).getShortKey();
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
         return AbstractShort2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2BooleanSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2BooleanSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2BooleanSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2BooleanSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2BooleanSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2BooleanSortedMap.KeySetIterator(AbstractShort2BooleanSortedMap.this.short2BooleanEntrySet().iterator(new AbstractShort2BooleanMap.BasicEntry(var1, false)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2BooleanSortedMap.KeySetIterator(Short2BooleanSortedMaps.fastIterator(AbstractShort2BooleanSortedMap.this));
      }
   }
}
