package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Iterators {
   static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR = new UnmodifiableListIterator<Object>() {
      public boolean hasNext() {
         return false;
      }

      public Object next() {
         throw new NoSuchElementException();
      }

      public boolean hasPrevious() {
         return false;
      }

      public Object previous() {
         throw new NoSuchElementException();
      }

      public int nextIndex() {
         return 0;
      }

      public int previousIndex() {
         return -1;
      }
   };
   private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
      public boolean hasNext() {
         return false;
      }

      public Object next() {
         throw new NoSuchElementException();
      }

      public void remove() {
         CollectPreconditions.checkRemove(false);
      }
   };

   private Iterators() {
      super();
   }

   static <T> UnmodifiableIterator<T> emptyIterator() {
      return emptyListIterator();
   }

   static <T> UnmodifiableListIterator<T> emptyListIterator() {
      return EMPTY_LIST_ITERATOR;
   }

   static <T> Iterator<T> emptyModifiableIterator() {
      return EMPTY_MODIFIABLE_ITERATOR;
   }

   public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<? extends T> var0) {
      Preconditions.checkNotNull(var0);
      if (var0 instanceof UnmodifiableIterator) {
         UnmodifiableIterator var1 = (UnmodifiableIterator)var0;
         return var1;
      } else {
         return new UnmodifiableIterator<T>() {
            public boolean hasNext() {
               return var0.hasNext();
            }

            public T next() {
               return var0.next();
            }
         };
      }
   }

   /** @deprecated */
   @Deprecated
   public static <T> UnmodifiableIterator<T> unmodifiableIterator(UnmodifiableIterator<T> var0) {
      return (UnmodifiableIterator)Preconditions.checkNotNull(var0);
   }

   public static int size(Iterator<?> var0) {
      long var1;
      for(var1 = 0L; var0.hasNext(); ++var1) {
         var0.next();
      }

      return Ints.saturatedCast(var1);
   }

   public static boolean contains(Iterator<?> var0, @Nullable Object var1) {
      return any(var0, Predicates.equalTo(var1));
   }

   @CanIgnoreReturnValue
   public static boolean removeAll(Iterator<?> var0, Collection<?> var1) {
      return removeIf(var0, Predicates.in(var1));
   }

   @CanIgnoreReturnValue
   public static <T> boolean removeIf(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var1);
      boolean var2 = false;

      while(var0.hasNext()) {
         if (var1.apply(var0.next())) {
            var0.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @CanIgnoreReturnValue
   public static boolean retainAll(Iterator<?> var0, Collection<?> var1) {
      return removeIf(var0, Predicates.not(Predicates.in(var1)));
   }

   public static boolean elementsEqual(Iterator<?> var0, Iterator<?> var1) {
      while(true) {
         if (var0.hasNext()) {
            if (!var1.hasNext()) {
               return false;
            }

            Object var2 = var0.next();
            Object var3 = var1.next();
            if (Objects.equal(var2, var3)) {
               continue;
            }

            return false;
         }

         return !var1.hasNext();
      }
   }

   public static String toString(Iterator<?> var0) {
      return Collections2.STANDARD_JOINER.appendTo((new StringBuilder()).append('['), var0).append(']').toString();
   }

   @CanIgnoreReturnValue
   public static <T> T getOnlyElement(Iterator<T> var0) {
      Object var1 = var0.next();
      if (!var0.hasNext()) {
         return var1;
      } else {
         StringBuilder var2 = (new StringBuilder()).append("expected one element but was: <").append(var1);

         for(int var3 = 0; var3 < 4 && var0.hasNext(); ++var3) {
            var2.append(", ").append(var0.next());
         }

         if (var0.hasNext()) {
            var2.append(", ...");
         }

         var2.append('>');
         throw new IllegalArgumentException(var2.toString());
      }
   }

   @Nullable
   @CanIgnoreReturnValue
   public static <T> T getOnlyElement(Iterator<? extends T> var0, @Nullable T var1) {
      return var0.hasNext() ? getOnlyElement(var0) : var1;
   }

   @GwtIncompatible
   public static <T> T[] toArray(Iterator<? extends T> var0, Class<T> var1) {
      ArrayList var2 = Lists.newArrayList(var0);
      return Iterables.toArray(var2, (Class)var1);
   }

   @CanIgnoreReturnValue
   public static <T> boolean addAll(Collection<T> var0, Iterator<? extends T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      boolean var2;
      for(var2 = false; var1.hasNext(); var2 |= var0.add(var1.next())) {
      }

      return var2;
   }

   public static int frequency(Iterator<?> var0, @Nullable Object var1) {
      return size(filter(var0, Predicates.equalTo(var1)));
   }

   public static <T> Iterator<T> cycle(final Iterable<T> var0) {
      Preconditions.checkNotNull(var0);
      return new Iterator<T>() {
         Iterator<T> iterator = Iterators.emptyModifiableIterator();

         public boolean hasNext() {
            return this.iterator.hasNext() || var0.iterator().hasNext();
         }

         public T next() {
            if (!this.iterator.hasNext()) {
               this.iterator = var0.iterator();
               if (!this.iterator.hasNext()) {
                  throw new NoSuchElementException();
               }
            }

            return this.iterator.next();
         }

         public void remove() {
            this.iterator.remove();
         }
      };
   }

   @SafeVarargs
   public static <T> Iterator<T> cycle(T... var0) {
      return cycle((Iterable)Lists.newArrayList(var0));
   }

   public static <T> Iterator<T> concat(Iterator<? extends T> var0, Iterator<? extends T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return concat((Iterator)(new ConsumingQueueIterator(new Iterator[]{var0, var1})));
   }

   public static <T> Iterator<T> concat(Iterator<? extends T> var0, Iterator<? extends T> var1, Iterator<? extends T> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return concat((Iterator)(new ConsumingQueueIterator(new Iterator[]{var0, var1, var2})));
   }

   public static <T> Iterator<T> concat(Iterator<? extends T> var0, Iterator<? extends T> var1, Iterator<? extends T> var2, Iterator<? extends T> var3) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return concat((Iterator)(new ConsumingQueueIterator(new Iterator[]{var0, var1, var2, var3})));
   }

   public static <T> Iterator<T> concat(Iterator<? extends T>... var0) {
      Iterator[] var1 = (Iterator[])Preconditions.checkNotNull(var0);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Iterator var4 = var1[var3];
         Preconditions.checkNotNull(var4);
      }

      return concat((Iterator)(new ConsumingQueueIterator(var0)));
   }

   public static <T> Iterator<T> concat(Iterator<? extends Iterator<? extends T>> var0) {
      return new Iterators.ConcatenatedIterator(var0);
   }

   public static <T> UnmodifiableIterator<List<T>> partition(Iterator<T> var0, int var1) {
      return partitionImpl(var0, var1, false);
   }

   public static <T> UnmodifiableIterator<List<T>> paddedPartition(Iterator<T> var0, int var1) {
      return partitionImpl(var0, var1, true);
   }

   private static <T> UnmodifiableIterator<List<T>> partitionImpl(final Iterator<T> var0, final int var1, final boolean var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 > 0);
      return new UnmodifiableIterator<List<T>>() {
         public boolean hasNext() {
            return var0.hasNext();
         }

         public List<T> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Object[] var1x = new Object[var1];

               int var2x;
               for(var2x = 0; var2x < var1 && var0.hasNext(); ++var2x) {
                  var1x[var2x] = var0.next();
               }

               for(int var3 = var2x; var3 < var1; ++var3) {
                  var1x[var3] = null;
               }

               List var4 = Collections.unmodifiableList(Arrays.asList(var1x));
               return !var2 && var2x != var1 ? var4.subList(0, var2x) : var4;
            }
         }
      };
   }

   public static <T> UnmodifiableIterator<T> filter(final Iterator<T> var0, final Predicate<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AbstractIterator<T>() {
         protected T computeNext() {
            while(true) {
               if (var0.hasNext()) {
                  Object var1x = var0.next();
                  if (!var1.apply(var1x)) {
                     continue;
                  }

                  return var1x;
               }

               return this.endOfData();
            }
         }
      };
   }

   @GwtIncompatible
   public static <T> UnmodifiableIterator<T> filter(Iterator<?> var0, Class<T> var1) {
      return filter(var0, Predicates.instanceOf(var1));
   }

   public static <T> boolean any(Iterator<T> var0, Predicate<? super T> var1) {
      return indexOf(var0, var1) != -1;
   }

   public static <T> boolean all(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var1);

      Object var2;
      do {
         if (!var0.hasNext()) {
            return true;
         }

         var2 = var0.next();
      } while(var1.apply(var2));

      return false;
   }

   public static <T> T find(Iterator<T> var0, Predicate<? super T> var1) {
      return filter(var0, var1).next();
   }

   @Nullable
   public static <T> T find(Iterator<? extends T> var0, Predicate<? super T> var1, @Nullable T var2) {
      return getNext(filter(var0, var1), var2);
   }

   public static <T> Optional<T> tryFind(Iterator<T> var0, Predicate<? super T> var1) {
      UnmodifiableIterator var2 = filter(var0, var1);
      return var2.hasNext() ? Optional.of(var2.next()) : Optional.absent();
   }

   public static <T> int indexOf(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var1, "predicate");

      for(int var2 = 0; var0.hasNext(); ++var2) {
         Object var3 = var0.next();
         if (var1.apply(var3)) {
            return var2;
         }
      }

      return -1;
   }

   public static <F, T> Iterator<T> transform(Iterator<F> var0, final Function<? super F, ? extends T> var1) {
      Preconditions.checkNotNull(var1);
      return new TransformedIterator<F, T>(var0) {
         T transform(F var1x) {
            return var1.apply(var1x);
         }
      };
   }

   public static <T> T get(Iterator<T> var0, int var1) {
      checkNonnegative(var1);
      int var2 = advance(var0, var1);
      if (!var0.hasNext()) {
         throw new IndexOutOfBoundsException("position (" + var1 + ") must be less than the number of elements that remained (" + var2 + ")");
      } else {
         return var0.next();
      }
   }

   static void checkNonnegative(int var0) {
      if (var0 < 0) {
         throw new IndexOutOfBoundsException("position (" + var0 + ") must not be negative");
      }
   }

   @Nullable
   public static <T> T get(Iterator<? extends T> var0, int var1, @Nullable T var2) {
      checkNonnegative(var1);
      advance(var0, var1);
      return getNext(var0, var2);
   }

   @Nullable
   public static <T> T getNext(Iterator<? extends T> var0, @Nullable T var1) {
      return var0.hasNext() ? var0.next() : var1;
   }

   public static <T> T getLast(Iterator<T> var0) {
      Object var1;
      do {
         var1 = var0.next();
      } while(var0.hasNext());

      return var1;
   }

   @Nullable
   public static <T> T getLast(Iterator<? extends T> var0, @Nullable T var1) {
      return var0.hasNext() ? getLast(var0) : var1;
   }

   @CanIgnoreReturnValue
   public static int advance(Iterator<?> var0, int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0, "numberToAdvance must be nonnegative");

      int var2;
      for(var2 = 0; var2 < var1 && var0.hasNext(); ++var2) {
         var0.next();
      }

      return var2;
   }

   public static <T> Iterator<T> limit(final Iterator<T> var0, final int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0, "limit is negative");
      return new Iterator<T>() {
         private int count;

         public boolean hasNext() {
            return this.count < var1 && var0.hasNext();
         }

         public T next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               ++this.count;
               return var0.next();
            }
         }

         public void remove() {
            var0.remove();
         }
      };
   }

   public static <T> Iterator<T> consumingIterator(final Iterator<T> var0) {
      Preconditions.checkNotNull(var0);
      return new UnmodifiableIterator<T>() {
         public boolean hasNext() {
            return var0.hasNext();
         }

         public T next() {
            Object var1 = var0.next();
            var0.remove();
            return var1;
         }

         public String toString() {
            return "Iterators.consumingIterator(...)";
         }
      };
   }

   @Nullable
   static <T> T pollNext(Iterator<T> var0) {
      if (var0.hasNext()) {
         Object var1 = var0.next();
         var0.remove();
         return var1;
      } else {
         return null;
      }
   }

   static void clear(Iterator<?> var0) {
      Preconditions.checkNotNull(var0);

      while(var0.hasNext()) {
         var0.next();
         var0.remove();
      }

   }

   @SafeVarargs
   public static <T> UnmodifiableIterator<T> forArray(T... var0) {
      return forArray(var0, 0, var0.length, 0);
   }

   static <T> UnmodifiableListIterator<T> forArray(final T[] var0, final int var1, int var2, int var3) {
      Preconditions.checkArgument(var2 >= 0);
      int var4 = var1 + var2;
      Preconditions.checkPositionIndexes(var1, var4, var0.length);
      Preconditions.checkPositionIndex(var3, var2);
      return (UnmodifiableListIterator)(var2 == 0 ? emptyListIterator() : new AbstractIndexedListIterator<T>(var2, var3) {
         protected T get(int var1x) {
            return var0[var1 + var1x];
         }
      });
   }

   public static <T> UnmodifiableIterator<T> singletonIterator(@Nullable final T var0) {
      return new UnmodifiableIterator<T>() {
         boolean done;

         public boolean hasNext() {
            return !this.done;
         }

         public T next() {
            if (this.done) {
               throw new NoSuchElementException();
            } else {
               this.done = true;
               return var0;
            }
         }
      };
   }

   public static <T> UnmodifiableIterator<T> forEnumeration(final Enumeration<T> var0) {
      Preconditions.checkNotNull(var0);
      return new UnmodifiableIterator<T>() {
         public boolean hasNext() {
            return var0.hasMoreElements();
         }

         public T next() {
            return var0.nextElement();
         }
      };
   }

   public static <T> Enumeration<T> asEnumeration(final Iterator<T> var0) {
      Preconditions.checkNotNull(var0);
      return new Enumeration<T>() {
         public boolean hasMoreElements() {
            return var0.hasNext();
         }

         public T nextElement() {
            return var0.next();
         }
      };
   }

   public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> var0) {
      if (var0 instanceof Iterators.PeekingImpl) {
         Iterators.PeekingImpl var1 = (Iterators.PeekingImpl)var0;
         return var1;
      } else {
         return new Iterators.PeekingImpl(var0);
      }
   }

   /** @deprecated */
   @Deprecated
   public static <T> PeekingIterator<T> peekingIterator(PeekingIterator<T> var0) {
      return (PeekingIterator)Preconditions.checkNotNull(var0);
   }

   @Beta
   public static <T> UnmodifiableIterator<T> mergeSorted(Iterable<? extends Iterator<? extends T>> var0, Comparator<? super T> var1) {
      Preconditions.checkNotNull(var0, "iterators");
      Preconditions.checkNotNull(var1, "comparator");
      return new Iterators.MergingIterator(var0, var1);
   }

   static <T> ListIterator<T> cast(Iterator<T> var0) {
      return (ListIterator)var0;
   }

   private static class ConcatenatedIterator<T> extends MultitransformedIterator<Iterator<? extends T>, T> {
      public ConcatenatedIterator(Iterator<? extends Iterator<? extends T>> var1) {
         super(getComponentIterators(var1));
      }

      Iterator<? extends T> transform(Iterator<? extends T> var1) {
         return var1;
      }

      private static <T> Iterator<Iterator<? extends T>> getComponentIterators(Iterator<? extends Iterator<? extends T>> var0) {
         return new MultitransformedIterator<Iterator<? extends T>, Iterator<? extends T>>(var0) {
            Iterator<? extends Iterator<? extends T>> transform(Iterator<? extends T> var1) {
               if (var1 instanceof Iterators.ConcatenatedIterator) {
                  Iterators.ConcatenatedIterator var2 = (Iterators.ConcatenatedIterator)var1;
                  return Iterators.ConcatenatedIterator.getComponentIterators(var2.backingIterator);
               } else {
                  return Iterators.singletonIterator(var1);
               }
            }
         };
      }
   }

   private static class MergingIterator<T> extends UnmodifiableIterator<T> {
      final Queue<PeekingIterator<T>> queue;

      public MergingIterator(Iterable<? extends Iterator<? extends T>> var1, final Comparator<? super T> var2) {
         super();
         Comparator var3 = new Comparator<PeekingIterator<T>>() {
            public int compare(PeekingIterator<T> var1, PeekingIterator<T> var2x) {
               return var2.compare(var1.peek(), var2x.peek());
            }
         };
         this.queue = new PriorityQueue(2, var3);
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Iterator var5 = (Iterator)var4.next();
            if (var5.hasNext()) {
               this.queue.add(Iterators.peekingIterator(var5));
            }
         }

      }

      public boolean hasNext() {
         return !this.queue.isEmpty();
      }

      public T next() {
         PeekingIterator var1 = (PeekingIterator)this.queue.remove();
         Object var2 = var1.next();
         if (var1.hasNext()) {
            this.queue.add(var1);
         }

         return var2;
      }
   }

   private static class PeekingImpl<E> implements PeekingIterator<E> {
      private final Iterator<? extends E> iterator;
      private boolean hasPeeked;
      private E peekedElement;

      public PeekingImpl(Iterator<? extends E> var1) {
         super();
         this.iterator = (Iterator)Preconditions.checkNotNull(var1);
      }

      public boolean hasNext() {
         return this.hasPeeked || this.iterator.hasNext();
      }

      public E next() {
         if (!this.hasPeeked) {
            return this.iterator.next();
         } else {
            Object var1 = this.peekedElement;
            this.hasPeeked = false;
            this.peekedElement = null;
            return var1;
         }
      }

      public void remove() {
         Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
         this.iterator.remove();
      }

      public E peek() {
         if (!this.hasPeeked) {
            this.peekedElement = this.iterator.next();
            this.hasPeeked = true;
         }

         return this.peekedElement;
      }
   }
}
