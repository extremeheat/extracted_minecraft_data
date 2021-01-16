package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class LongSortedSets {
   public static final LongSortedSets.EmptySet EMPTY_SET = new LongSortedSets.EmptySet();

   private LongSortedSets() {
      super();
   }

   public static LongSortedSet singleton(long var0) {
      return new LongSortedSets.Singleton(var0);
   }

   public static LongSortedSet singleton(long var0, LongComparator var2) {
      return new LongSortedSets.Singleton(var0, var2);
   }

   public static LongSortedSet singleton(Object var0) {
      return new LongSortedSets.Singleton((Long)var0);
   }

   public static LongSortedSet singleton(Object var0, LongComparator var1) {
      return new LongSortedSets.Singleton((Long)var0, var1);
   }

   public static LongSortedSet synchronize(LongSortedSet var0) {
      return new LongSortedSets.SynchronizedSortedSet(var0);
   }

   public static LongSortedSet synchronize(LongSortedSet var0, Object var1) {
      return new LongSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static LongSortedSet unmodifiable(LongSortedSet var0) {
      return new LongSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet extends LongSets.UnmodifiableSet implements LongSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongSortedSet sortedSet;

      protected UnmodifiableSortedSet(LongSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public LongComparator comparator() {
         return this.sortedSet.comparator();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return new LongSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var3));
      }

      public LongSortedSet headSet(long var1) {
         return new LongSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public LongSortedSet tailSet(long var1) {
         return new LongSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public LongBidirectionalIterator iterator() {
         return LongIterators.unmodifiable(this.sortedSet.iterator());
      }

      public LongBidirectionalIterator iterator(long var1) {
         return LongIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public long firstLong() {
         return this.sortedSet.firstLong();
      }

      public long lastLong() {
         return this.sortedSet.lastLong();
      }

      /** @deprecated */
      @Deprecated
      public Long first() {
         return this.sortedSet.first();
      }

      /** @deprecated */
      @Deprecated
      public Long last() {
         return this.sortedSet.last();
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet subSet(Long var1, Long var2) {
         return new LongSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet headSet(Long var1) {
         return new LongSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet tailSet(Long var1) {
         return new LongSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }
   }

   public static class SynchronizedSortedSet extends LongSets.SynchronizedSet implements LongSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongSortedSet sortedSet;

      protected SynchronizedSortedSet(LongSortedSet var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(LongSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public LongComparator comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public LongSortedSet subSet(long var1, long var3) {
         return new LongSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var3), this.sync);
      }

      public LongSortedSet headSet(long var1) {
         return new LongSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public LongSortedSet tailSet(long var1) {
         return new LongSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public LongBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return this.sortedSet.iterator(var1);
      }

      public long firstLong() {
         synchronized(this.sync) {
            return this.sortedSet.firstLong();
         }
      }

      public long lastLong() {
         synchronized(this.sync) {
            return this.sortedSet.lastLong();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet subSet(Long var1, Long var2) {
         return new LongSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet headSet(Long var1) {
         return new LongSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet tailSet(Long var1) {
         return new LongSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }
   }

   public static class Singleton extends LongSets.Singleton implements LongSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final LongComparator comparator;

      protected Singleton(long var1, LongComparator var3) {
         super(var1);
         this.comparator = var3;
      }

      private Singleton(long var1) {
         this(var1, (LongComparator)null);
      }

      final int compare(long var1, long var3) {
         return this.comparator == null ? Long.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public LongBidirectionalIterator iterator(long var1) {
         LongListIterator var3 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var3.nextLong();
         }

         return var3;
      }

      public LongComparator comparator() {
         return this.comparator;
      }

      public LongSortedSet subSet(long var1, long var3) {
         return (LongSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var3) < 0 ? this : LongSortedSets.EMPTY_SET);
      }

      public LongSortedSet headSet(long var1) {
         return (LongSortedSet)(this.compare(this.element, var1) < 0 ? this : LongSortedSets.EMPTY_SET);
      }

      public LongSortedSet tailSet(long var1) {
         return (LongSortedSet)(this.compare(var1, this.element) <= 0 ? this : LongSortedSets.EMPTY_SET);
      }

      public long firstLong() {
         return this.element;
      }

      public long lastLong() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet subSet(Long var1, Long var2) {
         return this.subSet(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet headSet(Long var1) {
         return this.headSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet tailSet(Long var1) {
         return this.tailSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Long last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(long var1, Object var3) {
         this(var1);
      }
   }

   public static class EmptySet extends LongSets.EmptySet implements LongSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return LongIterators.EMPTY_ITERATOR;
      }

      public LongSortedSet subSet(long var1, long var3) {
         return LongSortedSets.EMPTY_SET;
      }

      public LongSortedSet headSet(long var1) {
         return LongSortedSets.EMPTY_SET;
      }

      public LongSortedSet tailSet(long var1) {
         return LongSortedSets.EMPTY_SET;
      }

      public long firstLong() {
         throw new NoSuchElementException();
      }

      public long lastLong() {
         throw new NoSuchElementException();
      }

      public LongComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet subSet(Long var1, Long var2) {
         return LongSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet headSet(Long var1) {
         return LongSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public LongSortedSet tailSet(Long var1) {
         return LongSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Long first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Long last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return LongSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return LongSortedSets.EMPTY_SET;
      }
   }
}
