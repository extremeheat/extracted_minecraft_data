package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class FloatSortedSets {
   public static final FloatSortedSets.EmptySet EMPTY_SET = new FloatSortedSets.EmptySet();

   private FloatSortedSets() {
      super();
   }

   public static FloatSortedSet singleton(float var0) {
      return new FloatSortedSets.Singleton(var0);
   }

   public static FloatSortedSet singleton(float var0, FloatComparator var1) {
      return new FloatSortedSets.Singleton(var0, var1);
   }

   public static FloatSortedSet singleton(Object var0) {
      return new FloatSortedSets.Singleton((Float)var0);
   }

   public static FloatSortedSet singleton(Object var0, FloatComparator var1) {
      return new FloatSortedSets.Singleton((Float)var0, var1);
   }

   public static FloatSortedSet synchronize(FloatSortedSet var0) {
      return new FloatSortedSets.SynchronizedSortedSet(var0);
   }

   public static FloatSortedSet synchronize(FloatSortedSet var0, Object var1) {
      return new FloatSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static FloatSortedSet unmodifiable(FloatSortedSet var0) {
      return new FloatSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet extends FloatSets.UnmodifiableSet implements FloatSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatSortedSet sortedSet;

      protected UnmodifiableSortedSet(FloatSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public FloatComparator comparator() {
         return this.sortedSet.comparator();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return new FloatSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      public FloatSortedSet headSet(float var1) {
         return new FloatSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public FloatSortedSet tailSet(float var1) {
         return new FloatSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public FloatBidirectionalIterator iterator() {
         return FloatIterators.unmodifiable(this.sortedSet.iterator());
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return FloatIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public float firstFloat() {
         return this.sortedSet.firstFloat();
      }

      public float lastFloat() {
         return this.sortedSet.lastFloat();
      }

      /** @deprecated */
      @Deprecated
      public Float first() {
         return this.sortedSet.first();
      }

      /** @deprecated */
      @Deprecated
      public Float last() {
         return this.sortedSet.last();
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet subSet(Float var1, Float var2) {
         return new FloatSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet headSet(Float var1) {
         return new FloatSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet tailSet(Float var1) {
         return new FloatSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }
   }

   public static class SynchronizedSortedSet extends FloatSets.SynchronizedSet implements FloatSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatSortedSet sortedSet;

      protected SynchronizedSortedSet(FloatSortedSet var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(FloatSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public FloatComparator comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return new FloatSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      public FloatSortedSet headSet(float var1) {
         return new FloatSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public FloatSortedSet tailSet(float var1) {
         return new FloatSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public FloatBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return this.sortedSet.iterator(var1);
      }

      public float firstFloat() {
         synchronized(this.sync) {
            return this.sortedSet.firstFloat();
         }
      }

      public float lastFloat() {
         synchronized(this.sync) {
            return this.sortedSet.lastFloat();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet subSet(Float var1, Float var2) {
         return new FloatSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet headSet(Float var1) {
         return new FloatSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet tailSet(Float var1) {
         return new FloatSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }
   }

   public static class Singleton extends FloatSets.Singleton implements FloatSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final FloatComparator comparator;

      protected Singleton(float var1, FloatComparator var2) {
         super(var1);
         this.comparator = var2;
      }

      private Singleton(float var1) {
         this(var1, (FloatComparator)null);
      }

      final int compare(float var1, float var2) {
         return this.comparator == null ? Float.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public FloatBidirectionalIterator iterator(float var1) {
         FloatListIterator var2 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var2.nextFloat();
         }

         return var2;
      }

      public FloatComparator comparator() {
         return this.comparator;
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return (FloatSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var2) < 0 ? this : FloatSortedSets.EMPTY_SET);
      }

      public FloatSortedSet headSet(float var1) {
         return (FloatSortedSet)(this.compare(this.element, var1) < 0 ? this : FloatSortedSets.EMPTY_SET);
      }

      public FloatSortedSet tailSet(float var1) {
         return (FloatSortedSet)(this.compare(var1, this.element) <= 0 ? this : FloatSortedSets.EMPTY_SET);
      }

      public float firstFloat() {
         return this.element;
      }

      public float lastFloat() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet subSet(Float var1, Float var2) {
         return this.subSet(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet headSet(Float var1) {
         return this.headSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet tailSet(Float var1) {
         return this.tailSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Float last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(float var1, Object var2) {
         this(var1);
      }
   }

   public static class EmptySet extends FloatSets.EmptySet implements FloatSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return FloatIterators.EMPTY_ITERATOR;
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return FloatSortedSets.EMPTY_SET;
      }

      public FloatSortedSet headSet(float var1) {
         return FloatSortedSets.EMPTY_SET;
      }

      public FloatSortedSet tailSet(float var1) {
         return FloatSortedSets.EMPTY_SET;
      }

      public float firstFloat() {
         throw new NoSuchElementException();
      }

      public float lastFloat() {
         throw new NoSuchElementException();
      }

      public FloatComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet subSet(Float var1, Float var2) {
         return FloatSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet headSet(Float var1) {
         return FloatSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public FloatSortedSet tailSet(Float var1) {
         return FloatSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Float first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Float last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return FloatSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return FloatSortedSets.EMPTY_SET;
      }
   }
}
