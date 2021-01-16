package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class DoubleSets {
   public static final DoubleSets.EmptySet EMPTY_SET = new DoubleSets.EmptySet();

   private DoubleSets() {
      super();
   }

   public static DoubleSet singleton(double var0) {
      return new DoubleSets.Singleton(var0);
   }

   public static DoubleSet singleton(Double var0) {
      return new DoubleSets.Singleton(var0);
   }

   public static DoubleSet synchronize(DoubleSet var0) {
      return new DoubleSets.SynchronizedSet(var0);
   }

   public static DoubleSet synchronize(DoubleSet var0, Object var1) {
      return new DoubleSets.SynchronizedSet(var0, var1);
   }

   public static DoubleSet unmodifiable(DoubleSet var0) {
      return new DoubleSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends DoubleCollections.UnmodifiableCollection implements DoubleSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(DoubleSet var1) {
         super(var1);
      }

      public boolean remove(double var1) {
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
      public boolean rem(double var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends DoubleCollections.SynchronizedCollection implements DoubleSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(DoubleSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(DoubleSet var1) {
         super(var1);
      }

      public boolean remove(double var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(double var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractDoubleSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double element;

      protected Singleton(double var1) {
         super();
         this.element = var1;
      }

      public boolean contains(double var1) {
         return Double.doubleToLongBits(var1) == Double.doubleToLongBits(this.element);
      }

      public boolean remove(double var1) {
         throw new UnsupportedOperationException();
      }

      public DoubleListIterator iterator() {
         return DoubleIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(DoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends DoubleCollections.EmptyCollection implements DoubleSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(double var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return DoubleSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(double var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return DoubleSets.EMPTY_SET;
      }
   }
}
