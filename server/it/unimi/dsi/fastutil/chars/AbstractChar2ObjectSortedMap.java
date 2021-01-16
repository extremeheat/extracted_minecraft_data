package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractChar2ObjectSortedMap<V> extends AbstractChar2ObjectMap<V> implements Char2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2ObjectSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractChar2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Char2ObjectMap.Entry)this.i.next()).getValue();
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
         return new AbstractChar2ObjectSortedMap.ValuesIterator(Char2ObjectSortedMaps.fastIterator(AbstractChar2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractChar2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2ObjectMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2ObjectMap.Entry)this.i.previous()).getCharKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractCharSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(char var1) {
         return AbstractChar2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ObjectSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2ObjectSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2ObjectSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2ObjectSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2ObjectSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2ObjectSortedMap.KeySetIterator(AbstractChar2ObjectSortedMap.this.char2ObjectEntrySet().iterator(new AbstractChar2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2ObjectSortedMap.KeySetIterator(Char2ObjectSortedMaps.fastIterator(AbstractChar2ObjectSortedMap.this));
      }
   }
}
