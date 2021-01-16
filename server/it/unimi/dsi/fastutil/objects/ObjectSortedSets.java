package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public final class ObjectSortedSets {
   public static final ObjectSortedSets.EmptySet EMPTY_SET = new ObjectSortedSets.EmptySet();

   private ObjectSortedSets() {
      super();
   }

   public static <K> ObjectSet<K> emptySet() {
      return EMPTY_SET;
   }

   public static <K> ObjectSortedSet<K> singleton(K var0) {
      return new ObjectSortedSets.Singleton(var0);
   }

   public static <K> ObjectSortedSet<K> singleton(K var0, Comparator<? super K> var1) {
      return new ObjectSortedSets.Singleton(var0, var1);
   }

   public static <K> ObjectSortedSet<K> synchronize(ObjectSortedSet<K> var0) {
      return new ObjectSortedSets.SynchronizedSortedSet(var0);
   }

   public static <K> ObjectSortedSet<K> synchronize(ObjectSortedSet<K> var0, Object var1) {
      return new ObjectSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static <K> ObjectSortedSet<K> unmodifiable(ObjectSortedSet<K> var0) {
      return new ObjectSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet<K> extends ObjectSets.UnmodifiableSet<K> implements ObjectSortedSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ObjectSortedSet<K> sortedSet;

      protected UnmodifiableSortedSet(ObjectSortedSet<K> var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedSet.comparator();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return new ObjectSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return new ObjectSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return new ObjectSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
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

   public static class SynchronizedSortedSet<K> extends ObjectSets.SynchronizedSet<K> implements ObjectSortedSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ObjectSortedSet<K> sortedSet;

      protected SynchronizedSortedSet(ObjectSortedSet<K> var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(ObjectSortedSet<K> var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return new ObjectSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return new ObjectSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return new ObjectSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
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

   public static class Singleton<K> extends ObjectSets.Singleton<K> implements ObjectSortedSet<K>, Serializable, Cloneable {
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

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return (ObjectSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var2) < 0 ? this : ObjectSortedSets.EMPTY_SET);
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return (ObjectSortedSet)(this.compare(this.element, var1) < 0 ? this : ObjectSortedSets.EMPTY_SET);
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return (ObjectSortedSet)(this.compare(var1, this.element) <= 0 ? this : ObjectSortedSets.EMPTY_SET);
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

   public static class EmptySet<K> extends ObjectSets.EmptySet<K> implements ObjectSortedSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return ObjectSortedSets.EMPTY_SET;
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
         return ObjectSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return ObjectSortedSets.EMPTY_SET;
      }
   }
}
