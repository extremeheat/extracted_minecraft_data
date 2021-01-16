package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2CharSortedMap extends AbstractFloat2CharMap implements Float2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2CharSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractFloat2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Float2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Float2CharMap.Entry)this.i.next()).getCharValue();
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
         return new AbstractFloat2CharSortedMap.ValuesIterator(Float2CharSortedMaps.fastIterator(AbstractFloat2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractFloat2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2CharMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2CharMap.Entry)this.i.previous()).getFloatKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractFloatSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(float var1) {
         return AbstractFloat2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2CharSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2CharSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2CharSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2CharSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2CharSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2CharSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2CharSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2CharSortedMap.KeySetIterator(AbstractFloat2CharSortedMap.this.float2CharEntrySet().iterator(new AbstractFloat2CharMap.BasicEntry(var1, '\u0000')));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2CharSortedMap.KeySetIterator(Float2CharSortedMaps.fastIterator(AbstractFloat2CharSortedMap.this));
      }
   }
}
