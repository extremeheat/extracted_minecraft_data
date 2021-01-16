package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2FloatSortedMap extends AbstractShort2FloatMap implements Short2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2FloatSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractShort2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Short2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Short2FloatMap.Entry)this.i.next()).getFloatValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractFloatCollection {
      protected ValuesCollection() {
         super();
      }

      public FloatIterator iterator() {
         return new AbstractShort2FloatSortedMap.ValuesIterator(Short2FloatSortedMaps.fastIterator(AbstractShort2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractShort2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2FloatMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2FloatMap.Entry)this.i.previous()).getShortKey();
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
         return AbstractShort2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2FloatSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2FloatSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2FloatSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2FloatSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2FloatSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2FloatSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2FloatSortedMap.KeySetIterator(AbstractShort2FloatSortedMap.this.short2FloatEntrySet().iterator(new AbstractShort2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2FloatSortedMap.KeySetIterator(Short2FloatSortedMaps.fastIterator(AbstractShort2FloatSortedMap.this));
      }
   }
}
