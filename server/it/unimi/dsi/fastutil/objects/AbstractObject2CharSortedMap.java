package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Comparator;

public abstract class AbstractObject2CharSortedMap<K> extends AbstractObject2CharMap<K> implements Object2CharSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2CharSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractObject2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements CharIterator {
      protected final ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2CharMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Object2CharMap.Entry)this.i.next()).getCharValue();
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
         return new AbstractObject2CharSortedMap.ValuesIterator(Object2CharSortedMaps.fastIterator(AbstractObject2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractObject2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2CharMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2CharMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2CharMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2CharMap.Entry)this.i.previous()).getKey();
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
         return AbstractObject2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2CharSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2CharSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2CharSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2CharSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2CharSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2CharSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2CharSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2CharSortedMap.KeySetIterator(AbstractObject2CharSortedMap.this.object2CharEntrySet().iterator(new AbstractObject2CharMap.BasicEntry(var1, '\u0000')));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2CharSortedMap.KeySetIterator(Object2CharSortedMaps.fastIterator(AbstractObject2CharSortedMap.this));
      }
   }
}
