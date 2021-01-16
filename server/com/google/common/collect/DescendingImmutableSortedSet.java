package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

@GwtIncompatible
class DescendingImmutableSortedSet<E> extends ImmutableSortedSet<E> {
   private final ImmutableSortedSet<E> forward;

   DescendingImmutableSortedSet(ImmutableSortedSet<E> var1) {
      super(Ordering.from(var1.comparator()).reverse());
      this.forward = var1;
   }

   public boolean contains(@Nullable Object var1) {
      return this.forward.contains(var1);
   }

   public int size() {
      return this.forward.size();
   }

   public UnmodifiableIterator<E> iterator() {
      return this.forward.descendingIterator();
   }

   ImmutableSortedSet<E> headSetImpl(E var1, boolean var2) {
      return this.forward.tailSet(var1, var2).descendingSet();
   }

   ImmutableSortedSet<E> subSetImpl(E var1, boolean var2, E var3, boolean var4) {
      return this.forward.subSet(var3, var4, var1, var2).descendingSet();
   }

   ImmutableSortedSet<E> tailSetImpl(E var1, boolean var2) {
      return this.forward.headSet(var1, var2).descendingSet();
   }

   @GwtIncompatible("NavigableSet")
   public ImmutableSortedSet<E> descendingSet() {
      return this.forward;
   }

   @GwtIncompatible("NavigableSet")
   public UnmodifiableIterator<E> descendingIterator() {
      return this.forward.iterator();
   }

   @GwtIncompatible("NavigableSet")
   ImmutableSortedSet<E> createDescendingSet() {
      throw new AssertionError("should never be called");
   }

   public E lower(E var1) {
      return this.forward.higher(var1);
   }

   public E floor(E var1) {
      return this.forward.ceiling(var1);
   }

   public E ceiling(E var1) {
      return this.forward.floor(var1);
   }

   public E higher(E var1) {
      return this.forward.lower(var1);
   }

   int indexOf(@Nullable Object var1) {
      int var2 = this.forward.indexOf(var1);
      return var2 == -1 ? var2 : this.size() - 1 - var2;
   }

   boolean isPartialView() {
      return this.forward.isPartialView();
   }
}
