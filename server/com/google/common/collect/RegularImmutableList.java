package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Spliterator;
import java.util.Spliterators;

@GwtCompatible(
   serializable = true,
   emulated = true
)
class RegularImmutableList<E> extends ImmutableList<E> {
   static final ImmutableList<Object> EMPTY;
   private final transient Object[] array;

   RegularImmutableList(Object[] var1) {
      super();
      this.array = var1;
   }

   public int size() {
      return this.array.length;
   }

   boolean isPartialView() {
      return false;
   }

   int copyIntoArray(Object[] var1, int var2) {
      System.arraycopy(this.array, 0, var1, var2, this.array.length);
      return var2 + this.array.length;
   }

   public E get(int var1) {
      return this.array[var1];
   }

   public UnmodifiableListIterator<E> listIterator(int var1) {
      return Iterators.forArray(this.array, 0, this.array.length, var1);
   }

   public Spliterator<E> spliterator() {
      return Spliterators.spliterator(this.array, 1296);
   }

   static {
      EMPTY = new RegularImmutableList(ObjectArrays.EMPTY_ARRAY);
   }
}
