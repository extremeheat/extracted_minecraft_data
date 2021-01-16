package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.util.stream.Collector;

@GwtIncompatible
abstract class ImmutableSortedMultisetFauxverideShim<E> extends ImmutableMultiset<E> {
   ImmutableSortedMultisetFauxverideShim() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static <E> Collector<E, ?, ImmutableMultiset<E>> toImmutableMultiset() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset.Builder<E> builder() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset<E> of(E var0) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedMultiset<E> copyOf(E[] var0) {
      throw new UnsupportedOperationException();
   }
}
