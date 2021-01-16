package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Comparator;

public abstract class AbstractObject2BooleanSortedMap<K> extends AbstractObject2BooleanMap<K> implements Object2BooleanSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2BooleanSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractObject2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Object2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
         return new AbstractObject2BooleanSortedMap.ValuesIterator(Object2BooleanSortedMaps.fastIterator(AbstractObject2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractObject2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2BooleanMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2BooleanMap.Entry)this.i.previous()).getKey();
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
         return AbstractObject2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2BooleanSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2BooleanSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2BooleanSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2BooleanSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2BooleanSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2BooleanSortedMap.KeySetIterator(AbstractObject2BooleanSortedMap.this.object2BooleanEntrySet().iterator(new AbstractObject2BooleanMap.BasicEntry(var1, false)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2BooleanSortedMap.KeySetIterator(Object2BooleanSortedMaps.fastIterator(AbstractObject2BooleanSortedMap.this));
      }
   }
}
