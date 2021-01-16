package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class FloatSets {
   public static final FloatSets.EmptySet EMPTY_SET = new FloatSets.EmptySet();

   private FloatSets() {
      super();
   }

   public static FloatSet singleton(float var0) {
      return new FloatSets.Singleton(var0);
   }

   public static FloatSet singleton(Float var0) {
      return new FloatSets.Singleton(var0);
   }

   public static FloatSet synchronize(FloatSet var0) {
      return new FloatSets.SynchronizedSet(var0);
   }

   public static FloatSet synchronize(FloatSet var0, Object var1) {
      return new FloatSets.SynchronizedSet(var0, var1);
   }

   public static FloatSet unmodifiable(FloatSet var0) {
      return new FloatSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends FloatCollections.UnmodifiableCollection implements FloatSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(FloatSet var1) {
         super(var1);
      }

      public boolean remove(float var1) {
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
      public boolean rem(float var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends FloatCollections.SynchronizedCollection implements FloatSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(FloatSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(FloatSet var1) {
         super(var1);
      }

      public boolean remove(float var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(float var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractFloatSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float element;

      protected Singleton(float var1) {
         super();
         this.element = var1;
      }

      public boolean contains(float var1) {
         return Float.floatToIntBits(var1) == Float.floatToIntBits(this.element);
      }

      public boolean remove(float var1) {
         throw new UnsupportedOperationException();
      }

      public FloatListIterator iterator() {
         return FloatIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(FloatCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(FloatCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(FloatCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends FloatCollections.EmptyCollection implements FloatSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(float var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return FloatSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(float var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return FloatSets.EMPTY_SET;
      }
   }
}
