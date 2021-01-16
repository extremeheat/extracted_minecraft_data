package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class IntSets {
   public static final IntSets.EmptySet EMPTY_SET = new IntSets.EmptySet();

   private IntSets() {
      super();
   }

   public static IntSet singleton(int var0) {
      return new IntSets.Singleton(var0);
   }

   public static IntSet singleton(Integer var0) {
      return new IntSets.Singleton(var0);
   }

   public static IntSet synchronize(IntSet var0) {
      return new IntSets.SynchronizedSet(var0);
   }

   public static IntSet synchronize(IntSet var0, Object var1) {
      return new IntSets.SynchronizedSet(var0, var1);
   }

   public static IntSet unmodifiable(IntSet var0) {
      return new IntSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends IntCollections.UnmodifiableCollection implements IntSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(IntSet var1) {
         super(var1);
      }

      public boolean remove(int var1) {
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
      public boolean rem(int var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends IntCollections.SynchronizedCollection implements IntSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(IntSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(IntSet var1) {
         super(var1);
      }

      public boolean remove(int var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(int var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractIntSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int element;

      protected Singleton(int var1) {
         super();
         this.element = var1;
      }

      public boolean contains(int var1) {
         return var1 == this.element;
      }

      public boolean remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public IntListIterator iterator() {
         return IntIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends IntCollections.EmptyCollection implements IntSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return IntSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(int var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return IntSets.EMPTY_SET;
      }
   }
}
