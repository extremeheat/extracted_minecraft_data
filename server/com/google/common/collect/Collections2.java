package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible
public final class Collections2 {
   static final Joiner STANDARD_JOINER = Joiner.on(", ").useForNull("null");

   private Collections2() {
      super();
   }

   public static <E> Collection<E> filter(Collection<E> var0, Predicate<? super E> var1) {
      return var0 instanceof Collections2.FilteredCollection ? ((Collections2.FilteredCollection)var0).createCombined(var1) : new Collections2.FilteredCollection((Collection)Preconditions.checkNotNull(var0), (Predicate)Preconditions.checkNotNull(var1));
   }

   static boolean safeContains(Collection<?> var0, @Nullable Object var1) {
      Preconditions.checkNotNull(var0);

      try {
         return var0.contains(var1);
      } catch (ClassCastException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }
   }

   static boolean safeRemove(Collection<?> var0, @Nullable Object var1) {
      Preconditions.checkNotNull(var0);

      try {
         return var0.remove(var1);
      } catch (ClassCastException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }
   }

   public static <F, T> Collection<T> transform(Collection<F> var0, Function<? super F, T> var1) {
      return new Collections2.TransformedCollection(var0, var1);
   }

   static boolean containsAllImpl(Collection<?> var0, Collection<?> var1) {
      return Iterables.all(var1, Predicates.in(var0));
   }

   static String toStringImpl(final Collection<?> var0) {
      StringBuilder var1 = newStringBuilderForCollection(var0.size()).append('[');
      STANDARD_JOINER.appendTo(var1, Iterables.transform(var0, new Function<Object, Object>() {
         public Object apply(Object var1) {
            return var1 == var0 ? "(this Collection)" : var1;
         }
      }));
      return var1.append(']').toString();
   }

   static StringBuilder newStringBuilderForCollection(int var0) {
      CollectPreconditions.checkNonnegative(var0, "size");
      return new StringBuilder((int)Math.min((long)var0 * 8L, 1073741824L));
   }

   static <T> Collection<T> cast(Iterable<T> var0) {
      return (Collection)var0;
   }

   @Beta
   public static <E extends Comparable<? super E>> Collection<List<E>> orderedPermutations(Iterable<E> var0) {
      return orderedPermutations(var0, Ordering.natural());
   }

   @Beta
   public static <E> Collection<List<E>> orderedPermutations(Iterable<E> var0, Comparator<? super E> var1) {
      return new Collections2.OrderedPermutationCollection(var0, var1);
   }

   @Beta
   public static <E> Collection<List<E>> permutations(Collection<E> var0) {
      return new Collections2.PermutationCollection(ImmutableList.copyOf(var0));
   }

   private static boolean isPermutation(List<?> var0, List<?> var1) {
      if (var0.size() != var1.size()) {
         return false;
      } else {
         HashMultiset var2 = HashMultiset.create(var0);
         HashMultiset var3 = HashMultiset.create(var1);
         return var2.equals(var3);
      }
   }

   private static boolean isPositiveInt(long var0) {
      return var0 >= 0L && var0 <= 2147483647L;
   }

   private static class PermutationIterator<E> extends AbstractIterator<List<E>> {
      final List<E> list;
      final int[] c;
      final int[] o;
      int j;

      PermutationIterator(List<E> var1) {
         super();
         this.list = new ArrayList(var1);
         int var2 = var1.size();
         this.c = new int[var2];
         this.o = new int[var2];
         Arrays.fill(this.c, 0);
         Arrays.fill(this.o, 1);
         this.j = 2147483647;
      }

      protected List<E> computeNext() {
         if (this.j <= 0) {
            return (List)this.endOfData();
         } else {
            ImmutableList var1 = ImmutableList.copyOf((Collection)this.list);
            this.calculateNextPermutation();
            return var1;
         }
      }

      void calculateNextPermutation() {
         this.j = this.list.size() - 1;
         int var1 = 0;
         if (this.j != -1) {
            while(true) {
               while(true) {
                  int var2 = this.c[this.j] + this.o[this.j];
                  if (var2 >= 0) {
                     if (var2 != this.j + 1) {
                        Collections.swap(this.list, this.j - this.c[this.j] + var1, this.j - var2 + var1);
                        this.c[this.j] = var2;
                        return;
                     }

                     if (this.j == 0) {
                        return;
                     }

                     ++var1;
                     this.switchDirection();
                  } else {
                     this.switchDirection();
                  }
               }
            }
         }
      }

      void switchDirection() {
         this.o[this.j] = -this.o[this.j];
         --this.j;
      }
   }

   private static final class PermutationCollection<E> extends AbstractCollection<List<E>> {
      final ImmutableList<E> inputList;

      PermutationCollection(ImmutableList<E> var1) {
         super();
         this.inputList = var1;
      }

      public int size() {
         return IntMath.factorial(this.inputList.size());
      }

      public boolean isEmpty() {
         return false;
      }

      public Iterator<List<E>> iterator() {
         return new Collections2.PermutationIterator(this.inputList);
      }

      public boolean contains(@Nullable Object var1) {
         if (var1 instanceof List) {
            List var2 = (List)var1;
            return Collections2.isPermutation(this.inputList, var2);
         } else {
            return false;
         }
      }

      public String toString() {
         return "permutations(" + this.inputList + ")";
      }
   }

   private static final class OrderedPermutationIterator<E> extends AbstractIterator<List<E>> {
      List<E> nextPermutation;
      final Comparator<? super E> comparator;

      OrderedPermutationIterator(List<E> var1, Comparator<? super E> var2) {
         super();
         this.nextPermutation = Lists.newArrayList((Iterable)var1);
         this.comparator = var2;
      }

      protected List<E> computeNext() {
         if (this.nextPermutation == null) {
            return (List)this.endOfData();
         } else {
            ImmutableList var1 = ImmutableList.copyOf((Collection)this.nextPermutation);
            this.calculateNextPermutation();
            return var1;
         }
      }

      void calculateNextPermutation() {
         int var1 = this.findNextJ();
         if (var1 == -1) {
            this.nextPermutation = null;
         } else {
            int var2 = this.findNextL(var1);
            Collections.swap(this.nextPermutation, var1, var2);
            int var3 = this.nextPermutation.size();
            Collections.reverse(this.nextPermutation.subList(var1 + 1, var3));
         }
      }

      int findNextJ() {
         for(int var1 = this.nextPermutation.size() - 2; var1 >= 0; --var1) {
            if (this.comparator.compare(this.nextPermutation.get(var1), this.nextPermutation.get(var1 + 1)) < 0) {
               return var1;
            }
         }

         return -1;
      }

      int findNextL(int var1) {
         Object var2 = this.nextPermutation.get(var1);

         for(int var3 = this.nextPermutation.size() - 1; var3 > var1; --var3) {
            if (this.comparator.compare(var2, this.nextPermutation.get(var3)) < 0) {
               return var3;
            }
         }

         throw new AssertionError("this statement should be unreachable");
      }
   }

   private static final class OrderedPermutationCollection<E> extends AbstractCollection<List<E>> {
      final ImmutableList<E> inputList;
      final Comparator<? super E> comparator;
      final int size;

      OrderedPermutationCollection(Iterable<E> var1, Comparator<? super E> var2) {
         super();
         this.inputList = Ordering.from(var2).immutableSortedCopy(var1);
         this.comparator = var2;
         this.size = calculateSize(this.inputList, var2);
      }

      private static <E> int calculateSize(List<E> var0, Comparator<? super E> var1) {
         long var2 = 1L;
         int var4 = 1;

         int var5;
         for(var5 = 1; var4 < var0.size(); ++var5) {
            int var6 = var1.compare(var0.get(var4 - 1), var0.get(var4));
            if (var6 < 0) {
               var2 *= LongMath.binomial(var4, var5);
               var5 = 0;
               if (!Collections2.isPositiveInt(var2)) {
                  return 2147483647;
               }
            }

            ++var4;
         }

         var2 *= LongMath.binomial(var4, var5);
         if (!Collections2.isPositiveInt(var2)) {
            return 2147483647;
         } else {
            return (int)var2;
         }
      }

      public int size() {
         return this.size;
      }

      public boolean isEmpty() {
         return false;
      }

      public Iterator<List<E>> iterator() {
         return new Collections2.OrderedPermutationIterator(this.inputList, this.comparator);
      }

      public boolean contains(@Nullable Object var1) {
         if (var1 instanceof List) {
            List var2 = (List)var1;
            return Collections2.isPermutation(this.inputList, var2);
         } else {
            return false;
         }
      }

      public String toString() {
         return "orderedPermutationCollection(" + this.inputList + ")";
      }
   }

   static class TransformedCollection<F, T> extends AbstractCollection<T> {
      final Collection<F> fromCollection;
      final Function<? super F, ? extends T> function;

      TransformedCollection(Collection<F> var1, Function<? super F, ? extends T> var2) {
         super();
         this.fromCollection = (Collection)Preconditions.checkNotNull(var1);
         this.function = (Function)Preconditions.checkNotNull(var2);
      }

      public void clear() {
         this.fromCollection.clear();
      }

      public boolean isEmpty() {
         return this.fromCollection.isEmpty();
      }

      public Iterator<T> iterator() {
         return Iterators.transform(this.fromCollection.iterator(), this.function);
      }

      public Spliterator<T> spliterator() {
         return CollectSpliterators.map(this.fromCollection.spliterator(), this.function);
      }

      public void forEach(Consumer<? super T> var1) {
         Preconditions.checkNotNull(var1);
         this.fromCollection.forEach((var2) -> {
            var1.accept(this.function.apply(var2));
         });
      }

      public boolean removeIf(java.util.function.Predicate<? super T> var1) {
         Preconditions.checkNotNull(var1);
         return this.fromCollection.removeIf((var2) -> {
            return var1.test(this.function.apply(var2));
         });
      }

      public int size() {
         return this.fromCollection.size();
      }
   }

   static class FilteredCollection<E> extends AbstractCollection<E> {
      final Collection<E> unfiltered;
      final Predicate<? super E> predicate;

      FilteredCollection(Collection<E> var1, Predicate<? super E> var2) {
         super();
         this.unfiltered = var1;
         this.predicate = var2;
      }

      Collections2.FilteredCollection<E> createCombined(Predicate<? super E> var1) {
         return new Collections2.FilteredCollection(this.unfiltered, Predicates.and(this.predicate, var1));
      }

      public boolean add(E var1) {
         Preconditions.checkArgument(this.predicate.apply(var1));
         return this.unfiltered.add(var1);
      }

      public boolean addAll(Collection<? extends E> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            Preconditions.checkArgument(this.predicate.apply(var3));
         }

         return this.unfiltered.addAll(var1);
      }

      public void clear() {
         Iterables.removeIf(this.unfiltered, this.predicate);
      }

      public boolean contains(@Nullable Object var1) {
         return Collections2.safeContains(this.unfiltered, var1) ? this.predicate.apply(var1) : false;
      }

      public boolean containsAll(Collection<?> var1) {
         return Collections2.containsAllImpl(this, var1);
      }

      public boolean isEmpty() {
         return !Iterables.any(this.unfiltered, this.predicate);
      }

      public Iterator<E> iterator() {
         return Iterators.filter(this.unfiltered.iterator(), this.predicate);
      }

      public Spliterator<E> spliterator() {
         return CollectSpliterators.filter(this.unfiltered.spliterator(), this.predicate);
      }

      public void forEach(Consumer<? super E> var1) {
         Preconditions.checkNotNull(var1);
         this.unfiltered.forEach((var2) -> {
            if (this.predicate.test(var2)) {
               var1.accept(var2);
            }

         });
      }

      public boolean remove(Object var1) {
         return this.contains(var1) && this.unfiltered.remove(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         var1.getClass();
         return this.removeIf(var1::contains);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.removeIf((var1x) -> {
            return !var1.contains(var1x);
         });
      }

      public boolean removeIf(java.util.function.Predicate<? super E> var1) {
         Preconditions.checkNotNull(var1);
         return this.unfiltered.removeIf((var2) -> {
            return this.predicate.apply(var2) && var1.test(var2);
         });
      }

      public int size() {
         return Iterators.size(this.iterator());
      }

      public Object[] toArray() {
         return Lists.newArrayList(this.iterator()).toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return Lists.newArrayList(this.iterator()).toArray(var1);
      }
   }
}
