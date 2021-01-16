package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.Spliterator;
import java.util.Spliterators;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableSet<E> extends ImmutableSet.Indexed<E> {
   static final RegularImmutableSet<Object> EMPTY;
   private final transient Object[] elements;
   @VisibleForTesting
   final transient Object[] table;
   private final transient int mask;
   private final transient int hashCode;

   RegularImmutableSet(Object[] var1, int var2, Object[] var3, int var4) {
      super();
      this.elements = var1;
      this.table = var3;
      this.mask = var4;
      this.hashCode = var2;
   }

   public boolean contains(@Nullable Object var1) {
      Object[] var2 = this.table;
      if (var1 != null && var2 != null) {
         int var3 = Hashing.smearedHash(var1);

         while(true) {
            var3 &= this.mask;
            Object var4 = var2[var3];
            if (var4 == null) {
               return false;
            }

            if (var4.equals(var1)) {
               return true;
            }

            ++var3;
         }
      } else {
         return false;
      }
   }

   public int size() {
      return this.elements.length;
   }

   E get(int var1) {
      return this.elements[var1];
   }

   public Spliterator<E> spliterator() {
      return Spliterators.spliterator(this.elements, 1297);
   }

   int copyIntoArray(Object[] var1, int var2) {
      System.arraycopy(this.elements, 0, var1, var2, this.elements.length);
      return var2 + this.elements.length;
   }

   ImmutableList<E> createAsList() {
      return (ImmutableList)(this.table == null ? ImmutableList.of() : new RegularImmutableAsList(this, this.elements));
   }

   boolean isPartialView() {
      return false;
   }

   public int hashCode() {
      return this.hashCode;
   }

   boolean isHashCodeFast() {
      return true;
   }

   static {
      EMPTY = new RegularImmutableSet(ObjectArrays.EMPTY_ARRAY, 0, (Object[])null, 0);
   }
}
