package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class IntSortedSets {
   public static final IntSortedSets.EmptySet EMPTY_SET = new IntSortedSets.EmptySet();

   private IntSortedSets() {
      super();
   }

   public static IntSortedSet singleton(int var0) {
      return new IntSortedSets.Singleton(var0);
   }

   public static IntSortedSet singleton(int var0, IntComparator var1) {
      return new IntSortedSets.Singleton(var0, var1);
   }

   public static IntSortedSet singleton(Object var0) {
      return new IntSortedSets.Singleton((Integer)var0);
   }

   public static IntSortedSet singleton(Object var0, IntComparator var1) {
      return new IntSortedSets.Singleton((Integer)var0, var1);
   }

   public static IntSortedSet synchronize(IntSortedSet var0) {
      return new IntSortedSets.SynchronizedSortedSet(var0);
   }

   public static IntSortedSet synchronize(IntSortedSet var0, Object var1) {
      return new IntSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static IntSortedSet unmodifiable(IntSortedSet var0) {
      return new IntSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet extends IntSets.UnmodifiableSet implements IntSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntSortedSet sortedSet;

      protected UnmodifiableSortedSet(IntSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public IntComparator comparator() {
         return this.sortedSet.comparator();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      public IntSortedSet headSet(int var1) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public IntSortedSet tailSet(int var1) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public IntBidirectionalIterator iterator() {
         return IntIterators.unmodifiable(this.sortedSet.iterator());
      }

      public IntBidirectionalIterator iterator(int var1) {
         return IntIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public int firstInt() {
         return this.sortedSet.firstInt();
      }

      public int lastInt() {
         return this.sortedSet.lastInt();
      }

      /** @deprecated */
      @Deprecated
      public Integer first() {
         return this.sortedSet.first();
      }

      /** @deprecated */
      @Deprecated
      public Integer last() {
         return this.sortedSet.last();
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet subSet(Integer var1, Integer var2) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet headSet(Integer var1) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet tailSet(Integer var1) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }
   }

   public static class SynchronizedSortedSet extends IntSets.SynchronizedSet implements IntSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntSortedSet sortedSet;

      protected SynchronizedSortedSet(IntSortedSet var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(IntSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public IntComparator comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public IntSortedSet subSet(int var1, int var2) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      public IntSortedSet headSet(int var1) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public IntSortedSet tailSet(int var1) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public IntBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return this.sortedSet.iterator(var1);
      }

      public int firstInt() {
         synchronized(this.sync) {
            return this.sortedSet.firstInt();
         }
      }

      public int lastInt() {
         synchronized(this.sync) {
            return this.sortedSet.lastInt();
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet subSet(Integer var1, Integer var2) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet headSet(Integer var1) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet tailSet(Integer var1) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }
   }

   public static class Singleton extends IntSets.Singleton implements IntSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final IntComparator comparator;

      protected Singleton(int var1, IntComparator var2) {
         super(var1);
         this.comparator = var2;
      }

      private Singleton(int var1) {
         this(var1, (IntComparator)null);
      }

      final int compare(int var1, int var2) {
         return this.comparator == null ? Integer.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public IntBidirectionalIterator iterator(int var1) {
         IntListIterator var2 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var2.nextInt();
         }

         return var2;
      }

      public IntComparator comparator() {
         return this.comparator;
      }

      public IntSortedSet subSet(int var1, int var2) {
         return (IntSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var2) < 0 ? this : IntSortedSets.EMPTY_SET);
      }

      public IntSortedSet headSet(int var1) {
         return (IntSortedSet)(this.compare(this.element, var1) < 0 ? this : IntSortedSets.EMPTY_SET);
      }

      public IntSortedSet tailSet(int var1) {
         return (IntSortedSet)(this.compare(var1, this.element) <= 0 ? this : IntSortedSets.EMPTY_SET);
      }

      public int firstInt() {
         return this.element;
      }

      public int lastInt() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet subSet(Integer var1, Integer var2) {
         return this.subSet(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet headSet(Integer var1) {
         return this.headSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet tailSet(Integer var1) {
         return this.tailSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public Integer first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Integer last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(int var1, Object var2) {
         this(var1);
      }
   }

   public static class EmptySet extends IntSets.EmptySet implements IntSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return IntIterators.EMPTY_ITERATOR;
      }

      public IntSortedSet subSet(int var1, int var2) {
         return IntSortedSets.EMPTY_SET;
      }

      public IntSortedSet headSet(int var1) {
         return IntSortedSets.EMPTY_SET;
      }

      public IntSortedSet tailSet(int var1) {
         return IntSortedSets.EMPTY_SET;
      }

      public int firstInt() {
         throw new NoSuchElementException();
      }

      public int lastInt() {
         throw new NoSuchElementException();
      }

      public IntComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet subSet(Integer var1, Integer var2) {
         return IntSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet headSet(Integer var1) {
         return IntSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet tailSet(Integer var1) {
         return IntSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Integer first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Integer last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return IntSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return IntSortedSets.EMPTY_SET;
      }
   }
}
