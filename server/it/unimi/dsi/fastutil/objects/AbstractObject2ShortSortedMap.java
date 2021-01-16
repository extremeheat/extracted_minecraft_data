package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Comparator;

public abstract class AbstractObject2ShortSortedMap<K> extends AbstractObject2ShortMap<K> implements Object2ShortSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ShortSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractObject2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements ShortIterator {
      protected final ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Object2ShortMap.Entry)this.i.next()).getShortValue();
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
         return new AbstractObject2ShortSortedMap.ValuesIterator(Object2ShortSortedMaps.fastIterator(AbstractObject2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractObject2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2ShortMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2ShortMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      protected KeySet() {
         super();
      }

      public boolean contains(Object var1) {
         return AbstractObject2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ShortSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2ShortSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2ShortSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2ShortSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2ShortSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2ShortSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2ShortSortedMap.KeySetIterator(AbstractObject2ShortSortedMap.this.object2ShortEntrySet().iterator(new AbstractObject2ShortMap.BasicEntry(var1, (short)0)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ShortSortedMap.KeySetIterator(Object2ShortSortedMaps.fastIterator(AbstractObject2ShortSortedMap.this));
      }
   }
}
