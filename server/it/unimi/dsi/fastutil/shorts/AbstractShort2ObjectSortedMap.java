package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractShort2ObjectSortedMap<V> extends AbstractShort2ObjectMap<V> implements Short2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2ObjectSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractShort2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Short2ObjectMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      protected ValuesCollection() {
         super();
      }

      public ObjectIterator<V> iterator() {
         return new AbstractShort2ObjectSortedMap.ValuesIterator(Short2ObjectSortedMaps.fastIterator(AbstractShort2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractShort2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2ObjectMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2ObjectMap.Entry)this.i.previous()).getShortKey();
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
         return AbstractShort2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ObjectSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2ObjectSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2ObjectSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2ObjectSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2ObjectSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2ObjectSortedMap.KeySetIterator(AbstractShort2ObjectSortedMap.this.short2ObjectEntrySet().iterator(new AbstractShort2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2ObjectSortedMap.KeySetIterator(Short2ObjectSortedMaps.fastIterator(AbstractShort2ObjectSortedMap.this));
      }
   }
}
