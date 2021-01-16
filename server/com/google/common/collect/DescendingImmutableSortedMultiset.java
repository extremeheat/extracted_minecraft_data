package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

@GwtIncompatible
final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {
   private final transient ImmutableSortedMultiset<E> forward;

   DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> var1) {
      super();
      this.forward = var1;
   }

   public int count(@Nullable Object var1) {
      return this.forward.count(var1);
   }

   public Multiset.Entry<E> firstEntry() {
      return this.forward.lastEntry();
   }

   public Multiset.Entry<E> lastEntry() {
      return this.forward.firstEntry();
   }

   public int size() {
      return this.forward.size();
   }

   public ImmutableSortedSet<E> elementSet() {
      return this.forward.elementSet().descendingSet();
   }

   Multiset.Entry<E> getEntry(int var1) {
      return (Multiset.Entry)this.forward.entrySet().asList().reverse().get(var1);
   }

   public ImmutableSortedMultiset<E> descendingMultiset() {
      return this.forward;
   }

   public ImmutableSortedMultiset<E> headMultiset(E var1, BoundType var2) {
      return this.forward.tailMultiset(var1, var2).descendingMultiset();
   }

   public ImmutableSortedMultiset<E> tailMultiset(E var1, BoundType var2) {
      return this.forward.headMultiset(var1, var2).descendingMultiset();
   }

   boolean isPartialView() {
      return this.forward.isPartialView();
   }
}
