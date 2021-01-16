package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public abstract class ImmutableCollection<E> extends AbstractCollection<E> implements Serializable {
   static final int SPLITERATOR_CHARACTERISTICS = 1296;

   ImmutableCollection() {
      super();
   }

   public abstract UnmodifiableIterator<E> iterator();

   public Spliterator<E> spliterator() {
      return Spliterators.spliterator(this, 1296);
   }

   public final Object[] toArray() {
      int var1 = this.size();
      if (var1 == 0) {
         return ObjectArrays.EMPTY_ARRAY;
      } else {
         Object[] var2 = new Object[var1];
         this.copyIntoArray(var2, 0);
         return var2;
      }
   }

   @CanIgnoreReturnValue
   public final <T> T[] toArray(T[] var1) {
      Preconditions.checkNotNull(var1);
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = ObjectArrays.newArray(var1, var2);
      } else if (var1.length > var2) {
         var1[var2] = null;
      }

      this.copyIntoArray(var1, 0);
      return var1;
   }

   public abstract boolean contains(@Nullable Object var1);

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final boolean add(E var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final boolean remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final boolean addAll(Collection<? extends E> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final boolean removeIf(Predicate<? super E> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   public ImmutableList<E> asList() {
      switch(this.size()) {
      case 0:
         return ImmutableList.of();
      case 1:
         return ImmutableList.of(this.iterator().next());
      default:
         return new RegularImmutableAsList(this, this.toArray());
      }
   }

   abstract boolean isPartialView();

   @CanIgnoreReturnValue
   int copyIntoArray(Object[] var1, int var2) {
      Object var4;
      for(UnmodifiableIterator var3 = this.iterator(); var3.hasNext(); var1[var2++] = var4) {
         var4 = var3.next();
      }

      return var2;
   }

   Object writeReplace() {
      return new ImmutableList.SerializedForm(this.toArray());
   }

   abstract static class ArrayBasedBuilder<E> extends ImmutableCollection.Builder<E> {
      Object[] contents;
      int size;

      ArrayBasedBuilder(int var1) {
         super();
         CollectPreconditions.checkNonnegative(var1, "initialCapacity");
         this.contents = new Object[var1];
         this.size = 0;
      }

      private void ensureCapacity(int var1) {
         if (this.contents.length < var1) {
            this.contents = Arrays.copyOf(this.contents, expandedCapacity(this.contents.length, var1));
         }

      }

      @CanIgnoreReturnValue
      public ImmutableCollection.ArrayBasedBuilder<E> add(E var1) {
         Preconditions.checkNotNull(var1);
         this.ensureCapacity(this.size + 1);
         this.contents[this.size++] = var1;
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> add(E... var1) {
         ObjectArrays.checkElementsNotNull(var1);
         this.ensureCapacity(this.size + var1.length);
         System.arraycopy(var1, 0, this.contents, this.size, var1.length);
         this.size += var1.length;
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> addAll(Iterable<? extends E> var1) {
         if (var1 instanceof Collection) {
            Collection var2 = (Collection)var1;
            this.ensureCapacity(this.size + var2.size());
         }

         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableCollection.ArrayBasedBuilder<E> combine(ImmutableCollection.ArrayBasedBuilder<E> var1) {
         Preconditions.checkNotNull(var1);
         this.ensureCapacity(this.size + var1.size);
         System.arraycopy(var1.contents, 0, this.contents, this.size, var1.size);
         this.size += var1.size;
         return this;
      }
   }

   public abstract static class Builder<E> {
      static final int DEFAULT_INITIAL_CAPACITY = 4;

      static int expandedCapacity(int var0, int var1) {
         if (var1 < 0) {
            throw new AssertionError("cannot store more than MAX_VALUE elements");
         } else {
            int var2 = var0 + (var0 >> 1) + 1;
            if (var2 < var1) {
               var2 = Integer.highestOneBit(var1 - 1) << 1;
            }

            if (var2 < 0) {
               var2 = 2147483647;
            }

            return var2;
         }
      }

      Builder() {
         super();
      }

      @CanIgnoreReturnValue
      public abstract ImmutableCollection.Builder<E> add(E var1);

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> add(E... var1) {
         Object[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            this.add(var5);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> addAll(Iterable<? extends E> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            this.add(var3);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> addAll(Iterator<? extends E> var1) {
         while(var1.hasNext()) {
            this.add(var1.next());
         }

         return this;
      }

      public abstract ImmutableCollection<E> build();
   }
}
