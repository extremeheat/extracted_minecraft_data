package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class BooleanSets {
   public static final BooleanSets.EmptySet EMPTY_SET = new BooleanSets.EmptySet();

   private BooleanSets() {
      super();
   }

   public static BooleanSet singleton(boolean var0) {
      return new BooleanSets.Singleton(var0);
   }

   public static BooleanSet singleton(Boolean var0) {
      return new BooleanSets.Singleton(var0);
   }

   public static BooleanSet synchronize(BooleanSet var0) {
      return new BooleanSets.SynchronizedSet(var0);
   }

   public static BooleanSet synchronize(BooleanSet var0, Object var1) {
      return new BooleanSets.SynchronizedSet(var0, var1);
   }

   public static BooleanSet unmodifiable(BooleanSet var0) {
      return new BooleanSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends BooleanCollections.UnmodifiableCollection implements BooleanSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(BooleanSet var1) {
         super(var1);
      }

      public boolean remove(boolean var1) {
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
      public boolean rem(boolean var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends BooleanCollections.SynchronizedCollection implements BooleanSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(BooleanSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(BooleanSet var1) {
         super(var1);
      }

      public boolean remove(boolean var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(boolean var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractBooleanSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final boolean element;

      protected Singleton(boolean var1) {
         super();
         this.element = var1;
      }

      public boolean contains(boolean var1) {
         return var1 == this.element;
      }

      public boolean remove(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public BooleanListIterator iterator() {
         return BooleanIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends BooleanCollections.EmptyCollection implements BooleanSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return BooleanSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(boolean var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return BooleanSets.EMPTY_SET;
      }
   }
}
