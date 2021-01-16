package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class ShortSortedSets {
   public static final ShortSortedSets.EmptySet EMPTY_SET = new ShortSortedSets.EmptySet();

   private ShortSortedSets() {
      super();
   }

   public static ShortSortedSet singleton(short var0) {
      return new ShortSortedSets.Singleton(var0);
   }

   public static ShortSortedSet singleton(short var0, ShortComparator var1) {
      return new ShortSortedSets.Singleton(var0, var1);
   }

   public static ShortSortedSet singleton(Object var0) {
      return new ShortSortedSets.Singleton((Short)var0);
   }

   public static ShortSortedSet singleton(Object var0, ShortComparator var1) {
      return new ShortSortedSets.Singleton((Short)var0, var1);
   }

   public static ShortSortedSet synchronize(ShortSortedSet var0) {
      return new ShortSortedSets.SynchronizedSortedSet(var0);
   }

   public static ShortSortedSet synchronize(ShortSortedSet var0, Object var1) {
      return new ShortSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static ShortSortedSet unmodifiable(ShortSortedSet var0) {
      return new ShortSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet extends ShortSets.UnmodifiableSet implements ShortSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortSortedSet sortedSet;

      protected UnmodifiableSortedSet(ShortSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public ShortComparator comparator() {
         return this.sortedSet.comparator();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      public ShortSortedSet headSet(short var1) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public ShortSortedSet tailSet(short var1) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public ShortBidirectionalIterator iterator() {
         return ShortIterators.unmodifiable(this.sortedSet.iterator());
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return ShortIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public short firstShort() {
         return this.sortedSet.firstShort();
      }

      public short lastShort() {
         return this.sortedSet.lastShort();
      }

      /** @deprecated */
      @Deprecated
      public Short first() {
         return this.sortedSet.first();
      }

      /** @deprecated */
      @Deprecated
      public Short last() {
         return this.sortedSet.last();
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet subSet(Short var1, Short var2) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet headSet(Short var1) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet tailSet(Short var1) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }
   }

   public static class SynchronizedSortedSet extends ShortSets.SynchronizedSet implements ShortSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortSortedSet sortedSet;

      protected SynchronizedSortedSet(ShortSortedSet var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(ShortSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public ShortComparator comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      public ShortSortedSet headSet(short var1) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public ShortSortedSet tailSet(short var1) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public ShortBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return this.sortedSet.iterator(var1);
      }

      public short firstShort() {
         synchronized(this.sync) {
            return this.sortedSet.firstShort();
         }
      }

      public short lastShort() {
         synchronized(this.sync) {
            return this.sortedSet.lastShort();
         }
      }

      /** @deprecated */
      @Deprecated
      public Short first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Short last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet subSet(Short var1, Short var2) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet headSet(Short var1) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet tailSet(Short var1) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }
   }

   public static class Singleton extends ShortSets.Singleton implements ShortSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final ShortComparator comparator;

      protected Singleton(short var1, ShortComparator var2) {
         super(var1);
         this.comparator = var2;
      }

      private Singleton(short var1) {
         this(var1, (ShortComparator)null);
      }

      final int compare(short var1, short var2) {
         return this.comparator == null ? Short.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ShortBidirectionalIterator iterator(short var1) {
         ShortListIterator var2 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var2.nextShort();
         }

         return var2;
      }

      public ShortComparator comparator() {
         return this.comparator;
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return (ShortSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var2) < 0 ? this : ShortSortedSets.EMPTY_SET);
      }

      public ShortSortedSet headSet(short var1) {
         return (ShortSortedSet)(this.compare(this.element, var1) < 0 ? this : ShortSortedSets.EMPTY_SET);
      }

      public ShortSortedSet tailSet(short var1) {
         return (ShortSortedSet)(this.compare(var1, this.element) <= 0 ? this : ShortSortedSets.EMPTY_SET);
      }

      public short firstShort() {
         return this.element;
      }

      public short lastShort() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet subSet(Short var1, Short var2) {
         return this.subSet(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet headSet(Short var1) {
         return this.headSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet tailSet(Short var1) {
         return this.tailSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Short last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(short var1, Object var2) {
         this(var1);
      }
   }

   public static class EmptySet extends ShortSets.EmptySet implements ShortSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return ShortIterators.EMPTY_ITERATOR;
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return ShortSortedSets.EMPTY_SET;
      }

      public ShortSortedSet headSet(short var1) {
         return ShortSortedSets.EMPTY_SET;
      }

      public ShortSortedSet tailSet(short var1) {
         return ShortSortedSets.EMPTY_SET;
      }

      public short firstShort() {
         throw new NoSuchElementException();
      }

      public short lastShort() {
         throw new NoSuchElementException();
      }

      public ShortComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet subSet(Short var1, Short var2) {
         return ShortSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet headSet(Short var1) {
         return ShortSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ShortSortedSet tailSet(Short var1) {
         return ShortSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Short first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Short last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return ShortSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return ShortSortedSets.EMPTY_SET;
      }
   }
}
