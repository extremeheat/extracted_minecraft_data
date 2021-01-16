package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Collector;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E> implements NavigableSet<E>, SortedIterable<E> {
   static final int SPLITERATOR_CHARACTERISTICS = 1301;
   final transient Comparator<? super E> comparator;
   @LazyInit
   @GwtIncompatible
   transient ImmutableSortedSet<E> descendingSet;

   @Beta
   public static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(Comparator<? super E> var0) {
      return CollectCollectors.toImmutableSortedSet(var0);
   }

   static <E> RegularImmutableSortedSet<E> emptySet(Comparator<? super E> var0) {
      return Ordering.natural().equals(var0) ? RegularImmutableSortedSet.NATURAL_EMPTY_SET : new RegularImmutableSortedSet(ImmutableList.of(), var0);
   }

   public static <E> ImmutableSortedSet<E> of() {
      return RegularImmutableSortedSet.NATURAL_EMPTY_SET;
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0) {
      return new RegularImmutableSortedSet(ImmutableList.of(var0), Ordering.natural());
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1) {
      return construct(Ordering.natural(), 2, var0, var1);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2) {
      return construct(Ordering.natural(), 3, var0, var1, var2);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3) {
      return construct(Ordering.natural(), 4, var0, var1, var2, var3);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3, E var4) {
      return construct(Ordering.natural(), 5, var0, var1, var2, var3, var4);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      Comparable[] var7 = new Comparable[6 + var6.length];
      var7[0] = var0;
      var7[1] = var1;
      var7[2] = var2;
      var7[3] = var3;
      var7[4] = var4;
      var7[5] = var5;
      System.arraycopy(var6, 0, var7, 6, var6.length);
      return construct(Ordering.natural(), var7.length, (Comparable[])var7);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] var0) {
      return construct(Ordering.natural(), var0.length, (Object[])var0.clone());
   }

   public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, (Iterable)var0);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, (Collection)var0);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, (Iterator)var0);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> var0, Iterator<? extends E> var1) {
      return (new ImmutableSortedSet.Builder(var0)).addAll(var1).build();
   }

   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> var0, Iterable<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      boolean var2 = SortedIterables.hasSameComparator(var0, var1);
      if (var2 && var1 instanceof ImmutableSortedSet) {
         ImmutableSortedSet var3 = (ImmutableSortedSet)var1;
         if (!var3.isPartialView()) {
            return var3;
         }
      }

      Object[] var4 = (Object[])Iterables.toArray(var1);
      return construct(var0, var4.length, var4);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> var0, Collection<? extends E> var1) {
      return copyOf(var0, (Iterable)var1);
   }

   public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> var0) {
      Comparator var1 = SortedIterables.comparator(var0);
      ImmutableList var2 = ImmutableList.copyOf((Collection)var0);
      return var2.isEmpty() ? emptySet(var1) : new RegularImmutableSortedSet(var2, var1);
   }

   static <E> ImmutableSortedSet<E> construct(Comparator<? super E> var0, int var1, E... var2) {
      if (var1 == 0) {
         return emptySet(var0);
      } else {
         ObjectArrays.checkElementsNotNull(var2, var1);
         Arrays.sort(var2, 0, var1, var0);
         int var3 = 1;

         for(int var4 = 1; var4 < var1; ++var4) {
            Object var5 = var2[var4];
            Object var6 = var2[var3 - 1];
            if (var0.compare(var5, var6) != 0) {
               var2[var3++] = var5;
            }
         }

         Arrays.fill(var2, var3, var1, (Object)null);
         return new RegularImmutableSortedSet(ImmutableList.asImmutableList(var2, var3), var0);
      }
   }

   public static <E> ImmutableSortedSet.Builder<E> orderedBy(Comparator<E> var0) {
      return new ImmutableSortedSet.Builder(var0);
   }

   public static <E extends Comparable<?>> ImmutableSortedSet.Builder<E> reverseOrder() {
      return new ImmutableSortedSet.Builder(Ordering.natural().reverse());
   }

   public static <E extends Comparable<?>> ImmutableSortedSet.Builder<E> naturalOrder() {
      return new ImmutableSortedSet.Builder(Ordering.natural());
   }

   int unsafeCompare(Object var1, Object var2) {
      return unsafeCompare(this.comparator, var1, var2);
   }

   static int unsafeCompare(Comparator<?> var0, Object var1, Object var2) {
      return var0.compare(var1, var2);
   }

   ImmutableSortedSet(Comparator<? super E> var1) {
      super();
      this.comparator = var1;
   }

   public Comparator<? super E> comparator() {
      return this.comparator;
   }

   public abstract UnmodifiableIterator<E> iterator();

   public ImmutableSortedSet<E> headSet(E var1) {
      return this.headSet(var1, false);
   }

   @GwtIncompatible
   public ImmutableSortedSet<E> headSet(E var1, boolean var2) {
      return this.headSetImpl(Preconditions.checkNotNull(var1), var2);
   }

   public ImmutableSortedSet<E> subSet(E var1, E var2) {
      return this.subSet(var1, true, var2, false);
   }

   @GwtIncompatible
   public ImmutableSortedSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Preconditions.checkArgument(this.comparator.compare(var1, var3) <= 0);
      return this.subSetImpl(var1, var2, var3, var4);
   }

   public ImmutableSortedSet<E> tailSet(E var1) {
      return this.tailSet(var1, true);
   }

   @GwtIncompatible
   public ImmutableSortedSet<E> tailSet(E var1, boolean var2) {
      return this.tailSetImpl(Preconditions.checkNotNull(var1), var2);
   }

   abstract ImmutableSortedSet<E> headSetImpl(E var1, boolean var2);

   abstract ImmutableSortedSet<E> subSetImpl(E var1, boolean var2, E var3, boolean var4);

   abstract ImmutableSortedSet<E> tailSetImpl(E var1, boolean var2);

   @GwtIncompatible
   public E lower(E var1) {
      return Iterators.getNext(this.headSet(var1, false).descendingIterator(), (Object)null);
   }

   @GwtIncompatible
   public E floor(E var1) {
      return Iterators.getNext(this.headSet(var1, true).descendingIterator(), (Object)null);
   }

   @GwtIncompatible
   public E ceiling(E var1) {
      return Iterables.getFirst(this.tailSet(var1, true), (Object)null);
   }

   @GwtIncompatible
   public E higher(E var1) {
      return Iterables.getFirst(this.tailSet(var1, false), (Object)null);
   }

   public E first() {
      return this.iterator().next();
   }

   public E last() {
      return this.descendingIterator().next();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   @GwtIncompatible
   public final E pollFirst() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   @GwtIncompatible
   public final E pollLast() {
      throw new UnsupportedOperationException();
   }

   @GwtIncompatible
   public ImmutableSortedSet<E> descendingSet() {
      ImmutableSortedSet var1 = this.descendingSet;
      if (var1 == null) {
         var1 = this.descendingSet = this.createDescendingSet();
         var1.descendingSet = this;
      }

      return var1;
   }

   @GwtIncompatible
   ImmutableSortedSet<E> createDescendingSet() {
      return new DescendingImmutableSortedSet(this);
   }

   public Spliterator<E> spliterator() {
      return new AbstractSpliterator<E>((long)this.size(), 1365) {
         final UnmodifiableIterator<E> iterator = ImmutableSortedSet.this.iterator();

         public boolean tryAdvance(Consumer<? super E> var1) {
            if (this.iterator.hasNext()) {
               var1.accept(this.iterator.next());
               return true;
            } else {
               return false;
            }
         }

         public Comparator<? super E> getComparator() {
            return ImmutableSortedSet.this.comparator;
         }
      };
   }

   @GwtIncompatible
   public abstract UnmodifiableIterator<E> descendingIterator();

   abstract int indexOf(@Nullable Object var1);

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Use SerializedForm");
   }

   Object writeReplace() {
      return new ImmutableSortedSet.SerializedForm(this.comparator, this.toArray());
   }

   private static class SerializedForm<E> implements Serializable {
      final Comparator<? super E> comparator;
      final Object[] elements;
      private static final long serialVersionUID = 0L;

      public SerializedForm(Comparator<? super E> var1, Object[] var2) {
         super();
         this.comparator = var1;
         this.elements = var2;
      }

      Object readResolve() {
         return (new ImmutableSortedSet.Builder(this.comparator)).add((Object[])this.elements).build();
      }
   }

   public static final class Builder<E> extends ImmutableSet.Builder<E> {
      private final Comparator<? super E> comparator;

      public Builder(Comparator<? super E> var1) {
         super();
         this.comparator = (Comparator)Preconditions.checkNotNull(var1);
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> add(E var1) {
         super.add(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> add(E... var1) {
         super.add(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> addAll(Iterable<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableSortedSet.Builder<E> combine(ImmutableCollection.ArrayBasedBuilder<E> var1) {
         super.combine(var1);
         return this;
      }

      public ImmutableSortedSet<E> build() {
         Object[] var1 = (Object[])this.contents;
         ImmutableSortedSet var2 = ImmutableSortedSet.construct(this.comparator, this.size, var1);
         this.size = var2.size();
         return var2;
      }
   }
}
