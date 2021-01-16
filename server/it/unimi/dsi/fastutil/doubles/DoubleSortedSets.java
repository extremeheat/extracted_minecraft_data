package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class DoubleSortedSets {
   public static final DoubleSortedSets.EmptySet EMPTY_SET = new DoubleSortedSets.EmptySet();

   private DoubleSortedSets() {
      super();
   }

   public static DoubleSortedSet singleton(double var0) {
      return new DoubleSortedSets.Singleton(var0);
   }

   public static DoubleSortedSet singleton(double var0, DoubleComparator var2) {
      return new DoubleSortedSets.Singleton(var0, var2);
   }

   public static DoubleSortedSet singleton(Object var0) {
      return new DoubleSortedSets.Singleton((Double)var0);
   }

   public static DoubleSortedSet singleton(Object var0, DoubleComparator var1) {
      return new DoubleSortedSets.Singleton((Double)var0, var1);
   }

   public static DoubleSortedSet synchronize(DoubleSortedSet var0) {
      return new DoubleSortedSets.SynchronizedSortedSet(var0);
   }

   public static DoubleSortedSet synchronize(DoubleSortedSet var0, Object var1) {
      return new DoubleSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static DoubleSortedSet unmodifiable(DoubleSortedSet var0) {
      return new DoubleSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet extends DoubleSets.UnmodifiableSet implements DoubleSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleSortedSet sortedSet;

      protected UnmodifiableSortedSet(DoubleSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public DoubleComparator comparator() {
         return this.sortedSet.comparator();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var3));
      }

      public DoubleSortedSet headSet(double var1) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public DoubleSortedSet tailSet(double var1) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public DoubleBidirectionalIterator iterator() {
         return DoubleIterators.unmodifiable(this.sortedSet.iterator());
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return DoubleIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public double firstDouble() {
         return this.sortedSet.firstDouble();
      }

      public double lastDouble() {
         return this.sortedSet.lastDouble();
      }

      /** @deprecated */
      @Deprecated
      public Double first() {
         return this.sortedSet.first();
      }

      /** @deprecated */
      @Deprecated
      public Double last() {
         return this.sortedSet.last();
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet subSet(Double var1, Double var2) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet headSet(Double var1) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet tailSet(Double var1) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }
   }

   public static class SynchronizedSortedSet extends DoubleSets.SynchronizedSet implements DoubleSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleSortedSet sortedSet;

      protected SynchronizedSortedSet(DoubleSortedSet var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(DoubleSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public DoubleComparator comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var3), this.sync);
      }

      public DoubleSortedSet headSet(double var1) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public DoubleSortedSet tailSet(double var1) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public DoubleBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return this.sortedSet.iterator(var1);
      }

      public double firstDouble() {
         synchronized(this.sync) {
            return this.sortedSet.firstDouble();
         }
      }

      public double lastDouble() {
         synchronized(this.sync) {
            return this.sortedSet.lastDouble();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet subSet(Double var1, Double var2) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet headSet(Double var1) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet tailSet(Double var1) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }
   }

   public static class Singleton extends DoubleSets.Singleton implements DoubleSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final DoubleComparator comparator;

      protected Singleton(double var1, DoubleComparator var3) {
         super(var1);
         this.comparator = var3;
      }

      private Singleton(double var1) {
         this(var1, (DoubleComparator)null);
      }

      final int compare(double var1, double var3) {
         return this.comparator == null ? Double.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         DoubleListIterator var3 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var3.nextDouble();
         }

         return var3;
      }

      public DoubleComparator comparator() {
         return this.comparator;
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return (DoubleSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var3) < 0 ? this : DoubleSortedSets.EMPTY_SET);
      }

      public DoubleSortedSet headSet(double var1) {
         return (DoubleSortedSet)(this.compare(this.element, var1) < 0 ? this : DoubleSortedSets.EMPTY_SET);
      }

      public DoubleSortedSet tailSet(double var1) {
         return (DoubleSortedSet)(this.compare(var1, this.element) <= 0 ? this : DoubleSortedSets.EMPTY_SET);
      }

      public double firstDouble() {
         return this.element;
      }

      public double lastDouble() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet subSet(Double var1, Double var2) {
         return this.subSet(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet headSet(Double var1) {
         return this.headSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet tailSet(Double var1) {
         return this.tailSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public Double first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Double last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(double var1, Object var3) {
         this(var1);
      }
   }

   public static class EmptySet extends DoubleSets.EmptySet implements DoubleSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return DoubleIterators.EMPTY_ITERATOR;
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return DoubleSortedSets.EMPTY_SET;
      }

      public DoubleSortedSet headSet(double var1) {
         return DoubleSortedSets.EMPTY_SET;
      }

      public DoubleSortedSet tailSet(double var1) {
         return DoubleSortedSets.EMPTY_SET;
      }

      public double firstDouble() {
         throw new NoSuchElementException();
      }

      public double lastDouble() {
         throw new NoSuchElementException();
      }

      public DoubleComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet subSet(Double var1, Double var2) {
         return DoubleSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet headSet(Double var1) {
         return DoubleSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public DoubleSortedSet tailSet(Double var1) {
         return DoubleSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Double first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Double last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return DoubleSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return DoubleSortedSets.EMPTY_SET;
      }
   }
}
