package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class CharSets {
   public static final CharSets.EmptySet EMPTY_SET = new CharSets.EmptySet();

   private CharSets() {
      super();
   }

   public static CharSet singleton(char var0) {
      return new CharSets.Singleton(var0);
   }

   public static CharSet singleton(Character var0) {
      return new CharSets.Singleton(var0);
   }

   public static CharSet synchronize(CharSet var0) {
      return new CharSets.SynchronizedSet(var0);
   }

   public static CharSet synchronize(CharSet var0, Object var1) {
      return new CharSets.SynchronizedSet(var0, var1);
   }

   public static CharSet unmodifiable(CharSet var0) {
      return new CharSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends CharCollections.UnmodifiableCollection implements CharSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(CharSet var1) {
         super(var1);
      }

      public boolean remove(char var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(char var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends CharCollections.SynchronizedCollection implements CharSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(CharSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(CharSet var1) {
         super(var1);
      }

      public boolean remove(char var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(char var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractCharSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char element;

      protected Singleton(char var1) {
         super();
         this.element = var1;
      }

      public boolean contains(char var1) {
         return var1 == this.element;
      }

      public boolean remove(char var1) {
         throw new UnsupportedOperationException();
      }

      public CharListIterator iterator() {
         return CharIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(CharCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(CharCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(CharCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends CharCollections.EmptyCollection implements CharSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(char var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return CharSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(char var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return CharSets.EMPTY_SET;
      }
   }
}
