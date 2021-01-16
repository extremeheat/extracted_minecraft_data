package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public final class LongSets {
   public static final LongSets.EmptySet EMPTY_SET = new LongSets.EmptySet();

   private LongSets() {
      super();
   }

   public static LongSet singleton(long var0) {
      return new LongSets.Singleton(var0);
   }

   public static LongSet singleton(Long var0) {
      return new LongSets.Singleton(var0);
   }

   public static LongSet synchronize(LongSet var0) {
      return new LongSets.SynchronizedSet(var0);
   }

   public static LongSet synchronize(LongSet var0, Object var1) {
      return new LongSets.SynchronizedSet(var0, var1);
   }

   public static LongSet unmodifiable(LongSet var0) {
      return new LongSets.UnmodifiableSet(var0);
   }

   public static class UnmodifiableSet extends LongCollections.UnmodifiableCollection implements LongSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(LongSet var1) {
         super(var1);
      }

      public boolean remove(long var1) {
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
      public boolean rem(long var1) {
         return super.rem(var1);
      }
   }

   public static class SynchronizedSet extends LongCollections.SynchronizedCollection implements LongSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(LongSet var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedSet(LongSet var1) {
         super(var1);
      }

      public boolean remove(long var1) {
         synchronized(this.sync) {
            return this.collection.rem(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(long var1) {
         return super.rem(var1);
      }
   }

   public static class Singleton extends AbstractLongSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long element;

      protected Singleton(long var1) {
         super();
         this.element = var1;
      }

      public boolean contains(long var1) {
         return var1 == this.element;
      }

      public boolean remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public LongListIterator iterator() {
         return LongIterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public boolean addAll(Collection<? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptySet extends LongCollections.EmptyCollection implements LongSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
         super();
      }

      public boolean remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return LongSets.EMPTY_SET;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Set && ((Set)var1).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(long var1) {
         return super.rem(var1);
      }

      private Object readResolve() {
         return LongSets.EMPTY_SET;
      }
   }
}
