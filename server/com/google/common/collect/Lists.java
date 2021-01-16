package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Lists {
   private Lists() {
      super();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList() {
      return new ArrayList();
   }

   @SafeVarargs
   @CanIgnoreReturnValue
   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList(E... var0) {
      Preconditions.checkNotNull(var0);
      int var1 = computeArrayListCapacity(var0.length);
      ArrayList var2 = new ArrayList(var1);
      Collections.addAll(var2, var0);
      return var2;
   }

   @VisibleForTesting
   static int computeArrayListCapacity(int var0) {
      CollectPreconditions.checkNonnegative(var0, "arraySize");
      return Ints.saturatedCast(5L + (long)var0 + (long)(var0 / 10));
   }

   @CanIgnoreReturnValue
   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList(Iterable<? extends E> var0) {
      Preconditions.checkNotNull(var0);
      return var0 instanceof Collection ? new ArrayList(Collections2.cast(var0)) : newArrayList(var0.iterator());
   }

   @CanIgnoreReturnValue
   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList(Iterator<? extends E> var0) {
      ArrayList var1 = newArrayList();
      Iterators.addAll(var1, var0);
      return var1;
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayListWithCapacity(int var0) {
      CollectPreconditions.checkNonnegative(var0, "initialArraySize");
      return new ArrayList(var0);
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayListWithExpectedSize(int var0) {
      return new ArrayList(computeArrayListCapacity(var0));
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> LinkedList<E> newLinkedList() {
      return new LinkedList();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> var0) {
      LinkedList var1 = newLinkedList();
      Iterables.addAll(var1, var0);
      return var1;
   }

   @GwtIncompatible
   public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
      return new CopyOnWriteArrayList();
   }

   @GwtIncompatible
   public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> var0) {
      Object var1 = var0 instanceof Collection ? Collections2.cast(var0) : newArrayList(var0);
      return new CopyOnWriteArrayList((Collection)var1);
   }

   public static <E> List<E> asList(@Nullable E var0, E[] var1) {
      return new Lists.OnePlusArrayList(var0, var1);
   }

   public static <E> List<E> asList(@Nullable E var0, @Nullable E var1, E[] var2) {
      return new Lists.TwoPlusArrayList(var0, var1, var2);
   }

   public static <B> List<List<B>> cartesianProduct(List<? extends List<? extends B>> var0) {
      return CartesianList.create(var0);
   }

   @SafeVarargs
   public static <B> List<List<B>> cartesianProduct(List<? extends B>... var0) {
      return cartesianProduct(Arrays.asList(var0));
   }

   public static <F, T> List<T> transform(List<F> var0, Function<? super F, ? extends T> var1) {
      return (List)(var0 instanceof RandomAccess ? new Lists.TransformingRandomAccessList(var0, var1) : new Lists.TransformingSequentialList(var0, var1));
   }

   public static <T> List<List<T>> partition(List<T> var0, int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 > 0);
      return (List)(var0 instanceof RandomAccess ? new Lists.RandomAccessPartition(var0, var1) : new Lists.Partition(var0, var1));
   }

   public static ImmutableList<Character> charactersOf(String var0) {
      return new Lists.StringAsImmutableList((String)Preconditions.checkNotNull(var0));
   }

   @Beta
   public static List<Character> charactersOf(CharSequence var0) {
      return new Lists.CharSequenceAsList((CharSequence)Preconditions.checkNotNull(var0));
   }

   public static <T> List<T> reverse(List<T> var0) {
      if (var0 instanceof ImmutableList) {
         return ((ImmutableList)var0).reverse();
      } else if (var0 instanceof Lists.ReverseList) {
         return ((Lists.ReverseList)var0).getForwardList();
      } else {
         return (List)(var0 instanceof RandomAccess ? new Lists.RandomAccessReverseList(var0) : new Lists.ReverseList(var0));
      }
   }

   static int hashCodeImpl(List<?> var0) {
      int var1 = 1;

      for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 = ~(~var1)) {
         Object var3 = var2.next();
         var1 = 31 * var1 + (var3 == null ? 0 : var3.hashCode());
      }

      return var1;
   }

   static boolean equalsImpl(List<?> var0, @Nullable Object var1) {
      if (var1 == Preconditions.checkNotNull(var0)) {
         return true;
      } else if (!(var1 instanceof List)) {
         return false;
      } else {
         List var2 = (List)var1;
         int var3 = var0.size();
         if (var3 != var2.size()) {
            return false;
         } else if (var0 instanceof RandomAccess && var2 instanceof RandomAccess) {
            for(int var4 = 0; var4 < var3; ++var4) {
               if (!Objects.equal(var0.get(var4), var2.get(var4))) {
                  return false;
               }
            }

            return true;
         } else {
            return Iterators.elementsEqual(var0.iterator(), var2.iterator());
         }
      }
   }

   static <E> boolean addAllImpl(List<E> var0, int var1, Iterable<? extends E> var2) {
      boolean var3 = false;
      ListIterator var4 = var0.listIterator(var1);

      for(Iterator var5 = var2.iterator(); var5.hasNext(); var3 = true) {
         Object var6 = var5.next();
         var4.add(var6);
      }

      return var3;
   }

   static int indexOfImpl(List<?> var0, @Nullable Object var1) {
      if (var0 instanceof RandomAccess) {
         return indexOfRandomAccess(var0, var1);
      } else {
         ListIterator var2 = var0.listIterator();

         do {
            if (!var2.hasNext()) {
               return -1;
            }
         } while(!Objects.equal(var1, var2.next()));

         return var2.previousIndex();
      }
   }

   private static int indexOfRandomAccess(List<?> var0, @Nullable Object var1) {
      int var2 = var0.size();
      int var3;
      if (var1 == null) {
         for(var3 = 0; var3 < var2; ++var3) {
            if (var0.get(var3) == null) {
               return var3;
            }
         }
      } else {
         for(var3 = 0; var3 < var2; ++var3) {
            if (var1.equals(var0.get(var3))) {
               return var3;
            }
         }
      }

      return -1;
   }

   static int lastIndexOfImpl(List<?> var0, @Nullable Object var1) {
      if (var0 instanceof RandomAccess) {
         return lastIndexOfRandomAccess(var0, var1);
      } else {
         ListIterator var2 = var0.listIterator(var0.size());

         do {
            if (!var2.hasPrevious()) {
               return -1;
            }
         } while(!Objects.equal(var1, var2.previous()));

         return var2.nextIndex();
      }
   }

   private static int lastIndexOfRandomAccess(List<?> var0, @Nullable Object var1) {
      int var2;
      if (var1 == null) {
         for(var2 = var0.size() - 1; var2 >= 0; --var2) {
            if (var0.get(var2) == null) {
               return var2;
            }
         }
      } else {
         for(var2 = var0.size() - 1; var2 >= 0; --var2) {
            if (var1.equals(var0.get(var2))) {
               return var2;
            }
         }
      }

      return -1;
   }

   static <E> ListIterator<E> listIteratorImpl(List<E> var0, int var1) {
      return (new Lists.AbstractListWrapper(var0)).listIterator(var1);
   }

   static <E> List<E> subListImpl(List<E> var0, int var1, int var2) {
      Object var3;
      if (var0 instanceof RandomAccess) {
         var3 = new Lists.RandomAccessListWrapper<E>(var0) {
            private static final long serialVersionUID = 0L;

            public ListIterator<E> listIterator(int var1) {
               return this.backingList.listIterator(var1);
            }
         };
      } else {
         var3 = new Lists.AbstractListWrapper<E>(var0) {
            private static final long serialVersionUID = 0L;

            public ListIterator<E> listIterator(int var1) {
               return this.backingList.listIterator(var1);
            }
         };
      }

      return ((List)var3).subList(var1, var2);
   }

   static <T> List<T> cast(Iterable<T> var0) {
      return (List)var0;
   }

   private static class RandomAccessListWrapper<E> extends Lists.AbstractListWrapper<E> implements RandomAccess {
      RandomAccessListWrapper(List<E> var1) {
         super(var1);
      }
   }

   private static class AbstractListWrapper<E> extends AbstractList<E> {
      final List<E> backingList;

      AbstractListWrapper(List<E> var1) {
         super();
         this.backingList = (List)Preconditions.checkNotNull(var1);
      }

      public void add(int var1, E var2) {
         this.backingList.add(var1, var2);
      }

      public boolean addAll(int var1, Collection<? extends E> var2) {
         return this.backingList.addAll(var1, var2);
      }

      public E get(int var1) {
         return this.backingList.get(var1);
      }

      public E remove(int var1) {
         return this.backingList.remove(var1);
      }

      public E set(int var1, E var2) {
         return this.backingList.set(var1, var2);
      }

      public boolean contains(Object var1) {
         return this.backingList.contains(var1);
      }

      public int size() {
         return this.backingList.size();
      }
   }

   private static class RandomAccessReverseList<T> extends Lists.ReverseList<T> implements RandomAccess {
      RandomAccessReverseList(List<T> var1) {
         super(var1);
      }
   }

   private static class ReverseList<T> extends AbstractList<T> {
      private final List<T> forwardList;

      ReverseList(List<T> var1) {
         super();
         this.forwardList = (List)Preconditions.checkNotNull(var1);
      }

      List<T> getForwardList() {
         return this.forwardList;
      }

      private int reverseIndex(int var1) {
         int var2 = this.size();
         Preconditions.checkElementIndex(var1, var2);
         return var2 - 1 - var1;
      }

      private int reversePosition(int var1) {
         int var2 = this.size();
         Preconditions.checkPositionIndex(var1, var2);
         return var2 - var1;
      }

      public void add(int var1, @Nullable T var2) {
         this.forwardList.add(this.reversePosition(var1), var2);
      }

      public void clear() {
         this.forwardList.clear();
      }

      public T remove(int var1) {
         return this.forwardList.remove(this.reverseIndex(var1));
      }

      protected void removeRange(int var1, int var2) {
         this.subList(var1, var2).clear();
      }

      public T set(int var1, @Nullable T var2) {
         return this.forwardList.set(this.reverseIndex(var1), var2);
      }

      public T get(int var1) {
         return this.forwardList.get(this.reverseIndex(var1));
      }

      public int size() {
         return this.forwardList.size();
      }

      public List<T> subList(int var1, int var2) {
         Preconditions.checkPositionIndexes(var1, var2, this.size());
         return Lists.reverse(this.forwardList.subList(this.reversePosition(var2), this.reversePosition(var1)));
      }

      public Iterator<T> iterator() {
         return this.listIterator();
      }

      public ListIterator<T> listIterator(int var1) {
         int var2 = this.reversePosition(var1);
         final ListIterator var3 = this.forwardList.listIterator(var2);
         return new ListIterator<T>() {
            boolean canRemoveOrSet;

            public void add(T var1) {
               var3.add(var1);
               var3.previous();
               this.canRemoveOrSet = false;
            }

            public boolean hasNext() {
               return var3.hasPrevious();
            }

            public boolean hasPrevious() {
               return var3.hasNext();
            }

            public T next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.canRemoveOrSet = true;
                  return var3.previous();
               }
            }

            public int nextIndex() {
               return ReverseList.this.reversePosition(var3.nextIndex());
            }

            public T previous() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  this.canRemoveOrSet = true;
                  return var3.next();
               }
            }

            public int previousIndex() {
               return this.nextIndex() - 1;
            }

            public void remove() {
               CollectPreconditions.checkRemove(this.canRemoveOrSet);
               var3.remove();
               this.canRemoveOrSet = false;
            }

            public void set(T var1) {
               Preconditions.checkState(this.canRemoveOrSet);
               var3.set(var1);
            }
         };
      }
   }

   private static final class CharSequenceAsList extends AbstractList<Character> {
      private final CharSequence sequence;

      CharSequenceAsList(CharSequence var1) {
         super();
         this.sequence = var1;
      }

      public Character get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return this.sequence.charAt(var1);
      }

      public int size() {
         return this.sequence.length();
      }
   }

   private static final class StringAsImmutableList extends ImmutableList<Character> {
      private final String string;

      StringAsImmutableList(String var1) {
         super();
         this.string = var1;
      }

      public int indexOf(@Nullable Object var1) {
         return var1 instanceof Character ? this.string.indexOf((Character)var1) : -1;
      }

      public int lastIndexOf(@Nullable Object var1) {
         return var1 instanceof Character ? this.string.lastIndexOf((Character)var1) : -1;
      }

      public ImmutableList<Character> subList(int var1, int var2) {
         Preconditions.checkPositionIndexes(var1, var2, this.size());
         return Lists.charactersOf(this.string.substring(var1, var2));
      }

      boolean isPartialView() {
         return false;
      }

      public Character get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return this.string.charAt(var1);
      }

      public int size() {
         return this.string.length();
      }
   }

   private static class RandomAccessPartition<T> extends Lists.Partition<T> implements RandomAccess {
      RandomAccessPartition(List<T> var1, int var2) {
         super(var1, var2);
      }
   }

   private static class Partition<T> extends AbstractList<List<T>> {
      final List<T> list;
      final int size;

      Partition(List<T> var1, int var2) {
         super();
         this.list = var1;
         this.size = var2;
      }

      public List<T> get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         int var2 = var1 * this.size;
         int var3 = Math.min(var2 + this.size, this.list.size());
         return this.list.subList(var2, var3);
      }

      public int size() {
         return IntMath.divide(this.list.size(), this.size, RoundingMode.CEILING);
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }
   }

   private static class TransformingRandomAccessList<F, T> extends AbstractList<T> implements RandomAccess, Serializable {
      final List<F> fromList;
      final Function<? super F, ? extends T> function;
      private static final long serialVersionUID = 0L;

      TransformingRandomAccessList(List<F> var1, Function<? super F, ? extends T> var2) {
         super();
         this.fromList = (List)Preconditions.checkNotNull(var1);
         this.function = (Function)Preconditions.checkNotNull(var2);
      }

      public void clear() {
         this.fromList.clear();
      }

      public T get(int var1) {
         return this.function.apply(this.fromList.get(var1));
      }

      public Iterator<T> iterator() {
         return this.listIterator();
      }

      public ListIterator<T> listIterator(int var1) {
         return new TransformedListIterator<F, T>(this.fromList.listIterator(var1)) {
            T transform(F var1) {
               return TransformingRandomAccessList.this.function.apply(var1);
            }
         };
      }

      public boolean isEmpty() {
         return this.fromList.isEmpty();
      }

      public boolean removeIf(Predicate<? super T> var1) {
         Preconditions.checkNotNull(var1);
         return this.fromList.removeIf((var2) -> {
            return var1.test(this.function.apply(var2));
         });
      }

      public T remove(int var1) {
         return this.function.apply(this.fromList.remove(var1));
      }

      public int size() {
         return this.fromList.size();
      }
   }

   private static class TransformingSequentialList<F, T> extends AbstractSequentialList<T> implements Serializable {
      final List<F> fromList;
      final Function<? super F, ? extends T> function;
      private static final long serialVersionUID = 0L;

      TransformingSequentialList(List<F> var1, Function<? super F, ? extends T> var2) {
         super();
         this.fromList = (List)Preconditions.checkNotNull(var1);
         this.function = (Function)Preconditions.checkNotNull(var2);
      }

      public void clear() {
         this.fromList.clear();
      }

      public int size() {
         return this.fromList.size();
      }

      public ListIterator<T> listIterator(int var1) {
         return new TransformedListIterator<F, T>(this.fromList.listIterator(var1)) {
            T transform(F var1) {
               return TransformingSequentialList.this.function.apply(var1);
            }
         };
      }

      public boolean removeIf(Predicate<? super T> var1) {
         Preconditions.checkNotNull(var1);
         return this.fromList.removeIf((var2) -> {
            return var1.test(this.function.apply(var2));
         });
      }
   }

   private static class TwoPlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {
      final E first;
      final E second;
      final E[] rest;
      private static final long serialVersionUID = 0L;

      TwoPlusArrayList(@Nullable E var1, @Nullable E var2, E[] var3) {
         super();
         this.first = var1;
         this.second = var2;
         this.rest = (Object[])Preconditions.checkNotNull(var3);
      }

      public int size() {
         return IntMath.saturatedAdd(this.rest.length, 2);
      }

      public E get(int var1) {
         switch(var1) {
         case 0:
            return this.first;
         case 1:
            return this.second;
         default:
            Preconditions.checkElementIndex(var1, this.size());
            return this.rest[var1 - 2];
         }
      }
   }

   private static class OnePlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {
      final E first;
      final E[] rest;
      private static final long serialVersionUID = 0L;

      OnePlusArrayList(@Nullable E var1, E[] var2) {
         super();
         this.first = var1;
         this.rest = (Object[])Preconditions.checkNotNull(var2);
      }

      public int size() {
         return IntMath.saturatedAdd(this.rest.length, 1);
      }

      public E get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return var1 == 0 ? this.first : this.rest[var1 - 1];
      }
   }
}
