package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;

@GwtCompatible(
   emulated = true
)
public abstract class ContiguousSet<C extends Comparable> extends ImmutableSortedSet<C> {
   final DiscreteDomain<C> domain;

   public static <C extends Comparable> ContiguousSet<C> create(Range<C> var0, DiscreteDomain<C> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Range var2 = var0;

      try {
         if (!var0.hasLowerBound()) {
            var2 = var2.intersection(Range.atLeast(var1.minValue()));
         }

         if (!var0.hasUpperBound()) {
            var2 = var2.intersection(Range.atMost(var1.maxValue()));
         }
      } catch (NoSuchElementException var4) {
         throw new IllegalArgumentException(var4);
      }

      boolean var3 = var2.isEmpty() || Range.compareOrThrow(var0.lowerBound.leastValueAbove(var1), var0.upperBound.greatestValueBelow(var1)) > 0;
      return (ContiguousSet)(var3 ? new EmptyContiguousSet(var1) : new RegularContiguousSet(var2, var1));
   }

   ContiguousSet(DiscreteDomain<C> var1) {
      super(Ordering.natural());
      this.domain = var1;
   }

   public ContiguousSet<C> headSet(C var1) {
      return this.headSetImpl((Comparable)Preconditions.checkNotNull(var1), false);
   }

   @GwtIncompatible
   public ContiguousSet<C> headSet(C var1, boolean var2) {
      return this.headSetImpl((Comparable)Preconditions.checkNotNull(var1), var2);
   }

   public ContiguousSet<C> subSet(C var1, C var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkArgument(this.comparator().compare(var1, var2) <= 0);
      return this.subSetImpl(var1, true, var2, false);
   }

   @GwtIncompatible
   public ContiguousSet<C> subSet(C var1, boolean var2, C var3, boolean var4) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Preconditions.checkArgument(this.comparator().compare(var1, var3) <= 0);
      return this.subSetImpl(var1, var2, var3, var4);
   }

   public ContiguousSet<C> tailSet(C var1) {
      return this.tailSetImpl((Comparable)Preconditions.checkNotNull(var1), true);
   }

   @GwtIncompatible
   public ContiguousSet<C> tailSet(C var1, boolean var2) {
      return this.tailSetImpl((Comparable)Preconditions.checkNotNull(var1), var2);
   }

   abstract ContiguousSet<C> headSetImpl(C var1, boolean var2);

   abstract ContiguousSet<C> subSetImpl(C var1, boolean var2, C var3, boolean var4);

   abstract ContiguousSet<C> tailSetImpl(C var1, boolean var2);

   public abstract ContiguousSet<C> intersection(ContiguousSet<C> var1);

   public abstract Range<C> range();

   public abstract Range<C> range(BoundType var1, BoundType var2);

   public String toString() {
      return this.range().toString();
   }

   /** @deprecated */
   @Deprecated
   public static <E> ImmutableSortedSet.Builder<E> builder() {
      throw new UnsupportedOperationException();
   }
}
