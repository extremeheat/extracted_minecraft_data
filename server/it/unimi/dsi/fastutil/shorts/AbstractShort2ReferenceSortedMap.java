package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractShort2ReferenceSortedMap<V> extends AbstractShort2ReferenceMap<V> implements Short2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2ReferenceSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractShort2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Short2ReferenceMap.Entry)this.i.next()).getValue();
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
         return new AbstractShort2ReferenceSortedMap.ValuesIterator(Short2ReferenceSortedMaps.fastIterator(AbstractShort2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractShort2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2ReferenceMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2ReferenceMap.Entry)this.i.previous()).getShortKey();
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
         return AbstractShort2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ReferenceSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2ReferenceSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2ReferenceSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2ReferenceSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2ReferenceSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2ReferenceSortedMap.KeySetIterator(AbstractShort2ReferenceSortedMap.this.short2ReferenceEntrySet().iterator(new AbstractShort2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2ReferenceSortedMap.KeySetIterator(Short2ReferenceSortedMaps.fastIterator(AbstractShort2ReferenceSortedMap.this));
      }
   }
}
