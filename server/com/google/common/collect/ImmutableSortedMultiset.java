package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

@GwtIncompatible
public abstract class ImmutableSortedMultiset<E> extends ImmutableSortedMultisetFauxverideShim<E> implements SortedMultiset<E> {
   @LazyInit
   transient ImmutableSortedMultiset<E> descendingMultiset;

   @Beta
   public static <E> Collector<E, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(Comparator<? super E> var0) {
      return toImmutableSortedMultiset(var0, Function.identity(), (var0x) -> {
         return 1;
      });
   }

   private static <T, E> Collector<T, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(Comparator<? super E> var0, Function<? super T, ? extends E> var1, ToIntFunction<? super T> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collector.of(() -> {
         return TreeMultiset.create(var0);
      }, (var2x, var3) -> {
         var2x.add(var1.apply(var3), var2.applyAsInt(var3));
      }, (var0x, var1x) -> {
         var0x.addAll(var1x);
         return var0x;
      }, (var1x) -> {
         return copyOfSortedEntries(var0, var1x.entrySet());
      });
   }

   public static <E> ImmutableSortedMultiset<E> of() {
      return RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0) {
      RegularImmutableSortedSet var1 = (RegularImmutableSortedSet)ImmutableSortedSet.of(var0);
      long[] var2 = new long[]{0L, 1L};
      return new RegularImmutableSortedMultiset(var1, var2, 0, 1);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(var0, var1));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(var0, var1, var2));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(var0, var1, var2, var3));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(var0, var1, var2, var3, var4));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      int var7 = var6.length + 6;
      ArrayList var8 = Lists.newArrayListWithCapacity(var7);
      Collections.addAll(var8, new Comparable[]{var0, var1, var2, var3, var4, var5});
      Collections.addAll(var8, var6);
      return copyOf(Ordering.natural(), (Iterable)var8);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] var0) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(var0));
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, (Iterable)var0);
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, (Iterator)var0);
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> var0, Iterator<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      return (new ImmutableSortedMultiset.Builder(var0)).addAll(var1).build();
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> var0, Iterable<? extends E> var1) {
      if (var1 instanceof ImmutableSortedMultiset) {
         ImmutableSortedMultiset var2 = (ImmutableSortedMultiset)var1;
         if (var0.equals(var2.comparator())) {
            if (var2.isPartialView()) {
               return copyOfSortedEntries(var0, var2.entrySet().asList());
            }

            return var2;
         }
      }

      ArrayList var3 = Lists.newArrayList(var1);
      TreeMultiset var4 = TreeMultiset.create((Comparator)Preconditions.checkNotNull(var0));
      Iterables.addAll(var4, var3);
      return copyOfSortedEntries(var0, var4.entrySet());
   }

   public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> var0) {
      return copyOfSortedEntries(var0.comparator(), Lists.newArrayList((Iterable)var0.entrySet()));
   }

   private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(Comparator<? super E> var0, Collection<Multiset.Entry<E>> var1) {
      if (var1.isEmpty()) {
         return emptyMultiset(var0);
      } else {
         ImmutableList.Builder var2 = new ImmutableList.Builder(var1.size());
         long[] var3 = new long[var1.size() + 1];
         int var4 = 0;

         for(Iterator var5 = var1.iterator(); var5.hasNext(); ++var4) {
            Multiset.Entry var6 = (Multiset.Entry)var5.next();
            var2.add(var6.getElement());
            var3[var4 + 1] = var3[var4] + (long)var6.getCount();
         }

         return new RegularImmutableSortedMultiset(new RegularImmutableSortedSet(var2.build(), var0), var3, 0, var1.size());
      }
   }

   static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> var0) {
      return (ImmutableSortedMultiset)(Ordering.natural().equals(var0) ? RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET : new RegularImmutableSortedMultiset(var0));
   }

   ImmutableSortedMultiset() {
      super();
   }

   public final Comparator<? super E> comparator() {
      return this.elementSet().comparator();
   }

   public abstract ImmutableSortedSet<E> elementSet();

   public ImmutableSortedMultiset<E> descendingMultiset() {
      ImmutableSortedMultiset var1 = this.descendingMultiset;
      return var1 == null ? (this.descendingMultiset = (ImmutableSortedMultiset)(this.isEmpty() ? emptyMultiset(Ordering.from(this.comparator()).reverse()) : new DescendingImmutableSortedMultiset(this))) : var1;
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final Multiset.Entry<E> pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final Multiset.Entry<E> pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public abstract ImmutableSortedMultiset<E> headMultiset(E var1, BoundType var2);

   public ImmutableSortedMultiset<E> subMultiset(E var1, BoundType var2, E var3, BoundType var4) {
      Preconditions.checkArgument(this.comparator().compare(var1, var3) <= 0, "Expected lowerBound <= upperBound but %s > %s", var1, var3);
      return this.tailMultiset(var1, var2).headMultiset(var3, var4);
   }

   public abstract ImmutableSortedMultiset<E> tailMultiset(E var1, BoundType var2);

   public static <E> ImmutableSortedMultiset.Builder<E> orderedBy(Comparator<E> var0) {
      return new ImmutableSortedMultiset.Builder(var0);
   }

   public static <E extends Comparable<?>> ImmutableSortedMultiset.Builder<E> reverseOrder() {
      return new ImmutableSortedMultiset.Builder(Ordering.natural().reverse());
   }

   public static <E extends Comparable<?>> ImmutableSortedMultiset.Builder<E> naturalOrder() {
      return new ImmutableSortedMultiset.Builder(Ordering.natural());
   }

   Object writeReplace() {
      return new ImmutableSortedMultiset.SerializedForm(this);
   }

   private static final class SerializedForm<E> implements Serializable {
      final Comparator<? super E> comparator;
      final E[] elements;
      final int[] counts;

      SerializedForm(SortedMultiset<E> var1) {
         super();
         this.comparator = var1.comparator();
         int var2 = var1.entrySet().size();
         this.elements = (Object[])(new Object[var2]);
         this.counts = new int[var2];
         int var3 = 0;

         for(Iterator var4 = var1.entrySet().iterator(); var4.hasNext(); ++var3) {
            Multiset.Entry var5 = (Multiset.Entry)var4.next();
            this.elements[var3] = var5.getElement();
            this.counts[var3] = var5.getCount();
         }

      }

      Object readResolve() {
         int var1 = this.elements.length;
         ImmutableSortedMultiset.Builder var2 = new ImmutableSortedMultiset.Builder(this.comparator);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.addCopies(this.elements[var3], this.counts[var3]);
         }

         return var2.build();
      }
   }

   public static class Builder<E> extends ImmutableMultiset.Builder<E> {
      public Builder(Comparator<? super E> var1) {
         super(TreeMultiset.create((Comparator)Preconditions.checkNotNull(var1)));
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> add(E var1) {
         super.add(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> addCopies(E var1, int var2) {
         super.addCopies(var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> setCount(E var1, int var2) {
         super.setCount(var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> add(E... var1) {
         super.add(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> addAll(Iterable<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      public ImmutableSortedMultiset<E> build() {
         return ImmutableSortedMultiset.copyOfSorted((SortedMultiset)this.contents);
      }
   }
}
