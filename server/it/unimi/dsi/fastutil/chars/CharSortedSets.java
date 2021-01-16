package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class CharSortedSets {
   public static final CharSortedSets.EmptySet EMPTY_SET = new CharSortedSets.EmptySet();

   private CharSortedSets() {
      super();
   }

   public static CharSortedSet singleton(char var0) {
      return new CharSortedSets.Singleton(var0);
   }

   public static CharSortedSet singleton(char var0, CharComparator var1) {
      return new CharSortedSets.Singleton(var0, var1);
   }

   public static CharSortedSet singleton(Object var0) {
      return new CharSortedSets.Singleton((Character)var0);
   }

   public static CharSortedSet singleton(Object var0, CharComparator var1) {
      return new CharSortedSets.Singleton((Character)var0, var1);
   }

   public static CharSortedSet synchronize(CharSortedSet var0) {
      return new CharSortedSets.SynchronizedSortedSet(var0);
   }

   public static CharSortedSet synchronize(CharSortedSet var0, Object var1) {
      return new CharSortedSets.SynchronizedSortedSet(var0, var1);
   }

   public static CharSortedSet unmodifiable(CharSortedSet var0) {
      return new CharSortedSets.UnmodifiableSortedSet(var0);
   }

   public static class UnmodifiableSortedSet extends CharSets.UnmodifiableSet implements CharSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharSortedSet sortedSet;

      protected UnmodifiableSortedSet(CharSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public CharComparator comparator() {
         return this.sortedSet.comparator();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      public CharSortedSet headSet(char var1) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      public CharSortedSet tailSet(char var1) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }

      public CharBidirectionalIterator iterator() {
         return CharIterators.unmodifiable(this.sortedSet.iterator());
      }

      public CharBidirectionalIterator iterator(char var1) {
         return CharIterators.unmodifiable(this.sortedSet.iterator(var1));
      }

      public char firstChar() {
         return this.sortedSet.firstChar();
      }

      public char lastChar() {
         return this.sortedSet.lastChar();
      }

      /** @deprecated */
      @Deprecated
      public Character first() {
         return this.sortedSet.first();
      }

      /** @deprecated */
      @Deprecated
      public Character last() {
         return this.sortedSet.last();
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet subSet(Character var1, Character var2) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet headSet(Character var1) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(var1));
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet tailSet(Character var1) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(var1));
      }
   }

   public static class SynchronizedSortedSet extends CharSets.SynchronizedSet implements CharSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharSortedSet sortedSet;

      protected SynchronizedSortedSet(CharSortedSet var1, Object var2) {
         super(var1, var2);
         this.sortedSet = var1;
      }

      protected SynchronizedSortedSet(CharSortedSet var1) {
         super(var1);
         this.sortedSet = var1;
      }

      public CharComparator comparator() {
         synchronized(this.sync) {
            return this.sortedSet.comparator();
         }
      }

      public CharSortedSet subSet(char var1, char var2) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      public CharSortedSet headSet(char var1) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      public CharSortedSet tailSet(char var1) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }

      public CharBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return this.sortedSet.iterator(var1);
      }

      public char firstChar() {
         synchronized(this.sync) {
            return this.sortedSet.firstChar();
         }
      }

      public char lastChar() {
         synchronized(this.sync) {
            return this.sortedSet.lastChar();
         }
      }

      /** @deprecated */
      @Deprecated
      public Character first() {
         synchronized(this.sync) {
            return this.sortedSet.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Character last() {
         synchronized(this.sync) {
            return this.sortedSet.last();
         }
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet subSet(Character var1, Character var2) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet headSet(Character var1) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet tailSet(Character var1) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(var1), this.sync);
      }
   }

   public static class Singleton extends CharSets.Singleton implements CharSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final CharComparator comparator;

      protected Singleton(char var1, CharComparator var2) {
         super(var1);
         this.comparator = var2;
      }

      private Singleton(char var1) {
         this(var1, (CharComparator)null);
      }

      final int compare(char var1, char var2) {
         return this.comparator == null ? Character.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public CharBidirectionalIterator iterator(char var1) {
         CharListIterator var2 = this.iterator();
         if (this.compare(this.element, var1) <= 0) {
            var2.nextChar();
         }

         return var2;
      }

      public CharComparator comparator() {
         return this.comparator;
      }

      public CharSortedSet subSet(char var1, char var2) {
         return (CharSortedSet)(this.compare(var1, this.element) <= 0 && this.compare(this.element, var2) < 0 ? this : CharSortedSets.EMPTY_SET);
      }

      public CharSortedSet headSet(char var1) {
         return (CharSortedSet)(this.compare(this.element, var1) < 0 ? this : CharSortedSets.EMPTY_SET);
      }

      public CharSortedSet tailSet(char var1) {
         return (CharSortedSet)(this.compare(var1, this.element) <= 0 ? this : CharSortedSets.EMPTY_SET);
      }

      public char firstChar() {
         return this.element;
      }

      public char lastChar() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet subSet(Character var1, Character var2) {
         return this.subSet(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet headSet(Character var1) {
         return this.headSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet tailSet(Character var1) {
         return this.tailSet(var1);
      }

      /** @deprecated */
      @Deprecated
      public Character first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Character last() {
         return this.element;
      }

      // $FF: synthetic method
      Singleton(char var1, Object var2) {
         this(var1);
      }
   }

   public static class EmptySet extends CharSets.EmptySet implements CharSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return CharIterators.EMPTY_ITERATOR;
      }

      public CharSortedSet subSet(char var1, char var2) {
         return CharSortedSets.EMPTY_SET;
      }

      public CharSortedSet headSet(char var1) {
         return CharSortedSets.EMPTY_SET;
      }

      public CharSortedSet tailSet(char var1) {
         return CharSortedSets.EMPTY_SET;
      }

      public char firstChar() {
         throw new NoSuchElementException();
      }

      public char lastChar() {
         throw new NoSuchElementException();
      }

      public CharComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet subSet(Character var1, Character var2) {
         return CharSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet headSet(Character var1) {
         return CharSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public CharSortedSet tailSet(Character var1) {
         return CharSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Character first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Character last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return CharSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return CharSortedSets.EMPTY_SET;
      }
   }
}
