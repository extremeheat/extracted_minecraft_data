package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class ShortSets {
   public static final ShortSets.EmptySet EMPTY_SET = new ShortSets.EmptySet();

   private ShortSets() {
      super();
   }

   public static ShortSet singleton(short var0) {
      return new ShortSets.Singleton(var0);
   }

   public static ShortSet singleton(Short var0) {
      return new ShortSets.Singleton(var0);
   }

   public static ShortSet synchronize(ShortSet var0) {
      return new ShortSets.SynchronizedSet(var0);
   }

   public static ShortSet synchronize(ShortSet var0, Object var1) {
      return new ShortSets.SynchronizedSet(var0, var1);
   }

   public static ShortSet unmodifiable(ShortSet var0) {
      return new ShortSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends ShortCollections.UnmodifiableCollection implements ShortSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(ShortSet var1) {
         super(var1);
      }

      public boolean remove(short var1) {
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
      public boolean rem(short var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends ShortCollections.SynchronizedCollection implements ShortSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(ShortSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(ShortSet var1) {
         super(var1);
      }

      public boolean remove(short var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(short var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractShortSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short element;

      protected Singleton(short var1) {
         super();
         this.element = var1;
      }

      public boolean contains(short var1) {
         return var1 == this.element;
      }

      public boolean remove(short var1) {
         throw new UnsupportedOperationException();
      }

      public ShortListIterator iterator() {
         return ShortIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends ShortCollections.EmptyCollection implements ShortSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(short var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return ShortSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(short var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return ShortSets.EMPTY_SET;
      }
   }
}
