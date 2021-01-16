package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class EmptyContiguousSet<C extends Comparable> extends ContiguousSet<C> {
   EmptyContiguousSet(DiscreteDomain<C> var1) {
      super(var1);
   }

   public C first() {
      throw new NoSuchElementException();
   }

   public C last() {
      throw new NoSuchElementException();
   }

   public int size() {
      return 0;
   }

   public ContiguousSet<C> intersection(ContiguousSet<C> var1) {
      return this;
   }

   public Range<C> range() {
      throw new NoSuchElementException();
   }

   public Range<C> range(BoundType var1, BoundType var2) {
      throw new NoSuchElementException();
   }

   ContiguousSet<C> headSetImpl(C var1, boolean var2) {
      return this;
   }

   ContiguousSet<C> subSetImpl(C var1, boolean var2, C var3, boolean var4) {
      return this;
   }

   ContiguousSet<C> tailSetImpl(C var1, boolean var2) {
      return this;
   }

   public boolean contains(Object var1) {
      return false;
   }

   @GwtIncompatible
   int indexOf(Object var1) {
      return -1;
   }

   public UnmodifiableIterator<C> iterator() {
      return Iterators.emptyIterator();
   }

   @GwtIncompatible
   public UnmodifiableIterator<C> descendingIterator() {
      return Iterators.emptyIterator();
   }

   boolean isPartialView() {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   public ImmutableList<C> asList() {
      return ImmutableList.of();
   }

   public String toString() {
      return "[]";
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 instanceof Set) {
         Set var2 = (Set)var1;
         return var2.isEmpty();
      } else {
         return false;
      }
   }

   @GwtIncompatible
   boolean isHashCodeFast() {
      return true;
   }

   public int hashCode() {
      return 0;
   }

   @GwtIncompatible
   Object writeReplace() {
      return new EmptyContiguousSet.SerializedForm(this.domain);
   }

   @GwtIncompatible
   ImmutableSortedSet<C> createDescendingSet() {
      return ImmutableSortedSet.emptySet(Ordering.natural().reverse());
   }

   @GwtIncompatible
   private static final class SerializedForm<C extends Comparable> implements Serializable {
      private final DiscreteDomain<C> domain;
      private static final long serialVersionUID = 0L;

      private SerializedForm(DiscreteDomain<C> var1) {
         super();
         this.domain = var1;
      }

      private Object readResolve() {
         return new EmptyContiguousSet(this.domain);
      }

      // $FF: synthetic method
      SerializedForm(DiscreteDomain var1, Object var2) {
         this(var1);
      }
   }
}
