package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class ByteSets {
   public static final ByteSets.EmptySet EMPTY_SET = new ByteSets.EmptySet();

   private ByteSets() {
      super();
   }

   public static ByteSet singleton(byte var0) {
      return new ByteSets.Singleton(var0);
   }

   public static ByteSet singleton(Byte var0) {
      return new ByteSets.Singleton(var0);
   }

   public static ByteSet synchronize(ByteSet var0) {
      return new ByteSets.SynchronizedSet(var0);
   }

   public static ByteSet synchronize(ByteSet var0, Object var1) {
      return new ByteSets.SynchronizedSet(var0, var1);
   }

   public static ByteSet unmodifiable(ByteSet var0) {
      return new ByteSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends ByteCollections.UnmodifiableCollection implements ByteSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(ByteSet var1) {
         super(var1);
      }

      public boolean remove(byte var1) {
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
      public boolean rem(byte var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends ByteCollections.SynchronizedCollection implements ByteSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(ByteSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(ByteSet var1) {
         super(var1);
      }

      public boolean remove(byte var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(byte var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractByteSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final byte element;

      protected Singleton(byte var1) {
         super();
         this.element = var1;
      }

      public boolean contains(byte var1) {
         return var1 == this.element;
      }

      public boolean remove(byte var1) {
         throw new UnsupportedOperationException();
      }

      public ByteListIterator iterator() {
         return ByteIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(ByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(ByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends ByteCollections.EmptyCollection implements ByteSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(byte var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return ByteSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(byte var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return ByteSets.EMPTY_SET;
      }
   }
}
