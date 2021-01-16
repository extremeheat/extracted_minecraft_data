package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public final class ReferenceSortedSets {
   public static final ReferenceSortedSets.EmptySet EMPTY_SET = new ReferenceSortedSets.EmptySet();

   private ReferenceSortedSets() {
      super();
   }

   public static <K> ReferenceSet<K> emptySet() {
      return EMPTY_SET;
   }

   public static <K> ReferenceSortedSet<K> singleton(K var0) {
      return new ReferenceSortedSets.Singleton(var0);
   }

   public static <K> ReferenceSortedSet<K> singleton(K var0, Comparator<? super K> var1) {
      return new ReferenceSortedSets.Singleton(var0, var1);
   }

   public static <K> ReferenceSortedSet<K> synchronize(ReferenceSortedSet<K> var0) {
      return new ReferenceSortedSets.SynchronizedSortedSet(var0);
   }

   public static <K> ReferenceSortedSet<K> synchronize(ReferenceSortedSet<K> var0, Object var1) {
      return new ReferenceSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static <K> ReferenceSortedSet<K> unmodifiable(ReferenceSortedSet<K> var0) {
      return new ReferenceSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet<K> extends ReferenceSets.UnmodifiableSet<K> implements ReferenceSortedSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceSortedSet<K> sortedSet;

      protected UnmodifiableSortedSet(ReferenceSortedSet<K> var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedSet.comparator();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return new ReferenceSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return new ReferenceSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return new ReferenceSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return ObjectIterators.unmodifiable(this.sortedSet.iterator());
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return ObjectIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public K first() {
         return this.sortedSet.first();
      }

      public K last() {
         return this.sortedSet.last();
      }
   }

   public static class SynchronizedSortedSet<K> extends ReferenceSets.SynchronizedSet<K> implements ReferenceSortedSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceSortedSet<K> sortedSet;

      protected SynchronizedSortedSet(ReferenceSortedSet<K> var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(ReferenceSortedSet<K> var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return new ReferenceSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return new ReferenceSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return new ReferenceSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return this.sortedSet.iterator();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return this.sortedSet.iterator(var1);
      }

      public K first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      public K last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }
   }

   public static class Singleton<K> extends ReferenceSets.Singleton<K> implements ReferenceSortedSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final Comparator<? super K> comparator;

      protected Singleton(K var1, Comparator<? super K> var2) {
         super(var1);
         this.comparator = var2;
      }

      private Singleton(K var1) {
         this(var1, (Comparator)null);
      }

      final int compare(K var1, K var2) {
         return this.comparator == null ? ((Comparable)var1).compareTo(var2) : this.comparator.compare(var1, var2);
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         ObjectListIterator var2 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var2.next();
         }

         return var2;
      }

      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return (ReferenceSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var2) < 0 ? this : ReferenceSortedSets.EMPTY_SET);
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return (ReferenceSortedSet)(this.compare(this.element, var1) < 0 ? this : ReferenceSortedSets.EMPTY_SET);
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return (ReferenceSortedSet)(this.compare(var1, this.element) <= 0 ? this : ReferenceSortedSets.EMPTY_SET);
      }

      public K first() {
         return this.element;
      }

      public K last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(Object var1, Object var2) {
         this(var1);
      }
   }

   public static class EmptySet<K> extends ReferenceSets.EmptySet<K> implements ReferenceSortedSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public K first() {
         throw new NoSuchElementException();
      }

      public K last() {
         throw new NoSuchElementException();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public Object clone() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return ReferenceSortedSets.EMPTY_SET;
      }
   }
}
