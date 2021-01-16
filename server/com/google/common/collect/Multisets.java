package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import javax.annotation.Nullable;

@GwtCompatible
public final class Multisets {
   private static final Ordering<Multiset.Entry<?>> DECREASING_COUNT_ORDERING = new Ordering<Multiset.Entry<?>>() {
      public int compare(Multiset.Entry<?> var1, Multiset.Entry<?> var2) {
         return Ints.compare(var2.getCount(), var1.getCount());
      }
   };

   private Multisets() {
      super();
   }

   public static <E> Multiset<E> unmodifiableMultiset(Multiset<? extends E> var0) {
      return (Multiset)(!(var0 instanceof Multisets.UnmodifiableMultiset) && !(var0 instanceof ImmutableMultiset) ? new Multisets.UnmodifiableMultiset((Multiset)Preconditions.checkNotNull(var0)) : var0);
   }

   /** @deprecated */
   @Deprecated
   public static <E> Multiset<E> unmodifiableMultiset(ImmutableMultiset<E> var0) {
      return (Multiset)Preconditions.checkNotNull(var0);
   }

   @Beta
   public static <E> SortedMultiset<E> unmodifiableSortedMultiset(SortedMultiset<E> var0) {
      return new UnmodifiableSortedMultiset((SortedMultiset)Preconditions.checkNotNull(var0));
   }

   public static <E> Multiset.Entry<E> immutableEntry(@Nullable E var0, int var1) {
      return new Multisets.ImmutableEntry(var0, var1);
   }

   @Beta
   public static <E> Multiset<E> filter(Multiset<E> var0, Predicate<? super E> var1) {
      if (var0 instanceof Multisets.FilteredMultiset) {
         Multisets.FilteredMultiset var2 = (Multisets.FilteredMultiset)var0;
         Predicate var3 = Predicates.and(var2.predicate, var1);
         return new Multisets.FilteredMultiset(var2.unfiltered, var3);
      } else {
         return new Multisets.FilteredMultiset(var0, var1);
      }
   }

   static int inferDistinctElements(Iterable<?> var0) {
      return var0 instanceof Multiset ? ((Multiset)var0).elementSet().size() : 11;
   }

   @Beta
   public static <E> Multiset<E> union(final Multiset<? extends E> var0, final Multiset<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AbstractMultiset<E>() {
         public boolean contains(@Nullable Object var1x) {
            return var0.contains(var1x) || var1.contains(var1x);
         }

         public boolean isEmpty() {
            return var0.isEmpty() && var1.isEmpty();
         }

         public int count(Object var1x) {
            return Math.max(var0.count(var1x), var1.count(var1x));
         }

         Set<E> createElementSet() {
            return Sets.union(var0.elementSet(), var1.elementSet());
         }

         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            final Iterator var2 = var1.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  Multiset.Entry var1xx;
                  Object var2x;
                  if (var1x.hasNext()) {
                     var1xx = (Multiset.Entry)var1x.next();
                     var2x = var1xx.getElement();
                     int var3 = Math.max(var1xx.getCount(), var1.count(var2x));
                     return Multisets.immutableEntry(var2x, var3);
                  } else {
                     do {
                        if (!var2.hasNext()) {
                           return (Multiset.Entry)this.endOfData();
                        }

                        var1xx = (Multiset.Entry)var2.next();
                        var2x = var1xx.getElement();
                     } while(var0.contains(var2x));

                     return Multisets.immutableEntry(var2x, var1xx.getCount());
                  }
               }
            };
         }

         int distinctElements() {
            return this.elementSet().size();
         }
      };
   }

   public static <E> Multiset<E> intersection(final Multiset<E> var0, final Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AbstractMultiset<E>() {
         public int count(Object var1x) {
            int var2 = var0.count(var1x);
            return var2 == 0 ? 0 : Math.min(var2, var1.count(var1x));
         }

         Set<E> createElementSet() {
            return Sets.intersection(var0.elementSet(), var1.elementSet());
         }

         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  while(true) {
                     if (var1x.hasNext()) {
                        Multiset.Entry var1xx = (Multiset.Entry)var1x.next();
                        Object var2 = var1xx.getElement();
                        int var3 = Math.min(var1xx.getCount(), var1.count(var2));
                        if (var3 <= 0) {
                           continue;
                        }

                        return Multisets.immutableEntry(var2, var3);
                     }

                     return (Multiset.Entry)this.endOfData();
                  }
               }
            };
         }

         int distinctElements() {
            return this.elementSet().size();
         }
      };
   }

   @Beta
   public static <E> Multiset<E> sum(final Multiset<? extends E> var0, final Multiset<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AbstractMultiset<E>() {
         public boolean contains(@Nullable Object var1x) {
            return var0.contains(var1x) || var1.contains(var1x);
         }

         public boolean isEmpty() {
            return var0.isEmpty() && var1.isEmpty();
         }

         public int size() {
            return IntMath.saturatedAdd(var0.size(), var1.size());
         }

         public int count(Object var1x) {
            return var0.count(var1x) + var1.count(var1x);
         }

         Set<E> createElementSet() {
            return Sets.union(var0.elementSet(), var1.elementSet());
         }

         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            final Iterator var2 = var1.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  Multiset.Entry var1xx;
                  Object var2x;
                  if (var1x.hasNext()) {
                     var1xx = (Multiset.Entry)var1x.next();
                     var2x = var1xx.getElement();
                     int var3 = var1xx.getCount() + var1.count(var2x);
                     return Multisets.immutableEntry(var2x, var3);
                  } else {
                     do {
                        if (!var2.hasNext()) {
                           return (Multiset.Entry)this.endOfData();
                        }

                        var1xx = (Multiset.Entry)var2.next();
                        var2x = var1xx.getElement();
                     } while(var0.contains(var2x));

                     return Multisets.immutableEntry(var2x, var1xx.getCount());
                  }
               }
            };
         }

         int distinctElements() {
            return this.elementSet().size();
         }
      };
   }

   @Beta
   public static <E> Multiset<E> difference(final Multiset<E> var0, final Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AbstractMultiset<E>() {
         public int count(@Nullable Object var1x) {
            int var2 = var0.count(var1x);
            return var2 == 0 ? 0 : Math.max(0, var2 - var1.count(var1x));
         }

         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  while(true) {
                     if (var1x.hasNext()) {
                        Multiset.Entry var1xx = (Multiset.Entry)var1x.next();
                        Object var2 = var1xx.getElement();
                        int var3 = var1xx.getCount() - var1.count(var2);
                        if (var3 <= 0) {
                           continue;
                        }

                        return Multisets.immutableEntry(var2, var3);
                     }

                     return (Multiset.Entry)this.endOfData();
                  }
               }
            };
         }

         int distinctElements() {
            return Iterators.size(this.entryIterator());
         }
      };
   }

   @CanIgnoreReturnValue
   public static boolean containsOccurrences(Multiset<?> var0, Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Iterator var2 = var1.entrySet().iterator();

      Multiset.Entry var3;
      int var4;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Multiset.Entry)var2.next();
         var4 = var0.count(var3.getElement());
      } while(var4 >= var3.getCount());

      return false;
   }

   @CanIgnoreReturnValue
   public static boolean retainOccurrences(Multiset<?> var0, Multiset<?> var1) {
      return retainOccurrencesImpl(var0, var1);
   }

   private static <E> boolean retainOccurrencesImpl(Multiset<E> var0, Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Iterator var2 = var0.entrySet().iterator();
      boolean var3 = false;

      while(var2.hasNext()) {
         Multiset.Entry var4 = (Multiset.Entry)var2.next();
         int var5 = var1.count(var4.getElement());
         if (var5 == 0) {
            var2.remove();
            var3 = true;
         } else if (var5 < var4.getCount()) {
            var0.setCount(var4.getElement(), var5);
            var3 = true;
         }
      }

      return var3;
   }

   @CanIgnoreReturnValue
   public static boolean removeOccurrences(Multiset<?> var0, Iterable<?> var1) {
      if (var1 instanceof Multiset) {
         return removeOccurrences(var0, (Multiset)var1);
      } else {
         Preconditions.checkNotNull(var0);
         Preconditions.checkNotNull(var1);
         boolean var2 = false;

         Object var4;
         for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 |= var0.remove(var4)) {
            var4 = var3.next();
         }

         return var2;
      }
   }

   @CanIgnoreReturnValue
   public static boolean removeOccurrences(Multiset<?> var0, Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = false;
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Multiset.Entry var4 = (Multiset.Entry)var3.next();
         int var5 = var1.count(var4.getElement());
         if (var5 >= var4.getCount()) {
            var3.remove();
            var2 = true;
         } else if (var5 > 0) {
            var0.remove(var4.getElement(), var5);
            var2 = true;
         }
      }

      return var2;
   }

   static boolean equalsImpl(Multiset<?> var0, @Nullable Object var1) {
      if (var1 == var0) {
         return true;
      } else if (var1 instanceof Multiset) {
         Multiset var2 = (Multiset)var1;
         if (var0.size() == var2.size() && var0.entrySet().size() == var2.entrySet().size()) {
            Iterator var3 = var2.entrySet().iterator();

            Multiset.Entry var4;
            do {
               if (!var3.hasNext()) {
                  return true;
               }

               var4 = (Multiset.Entry)var3.next();
            } while(var0.count(var4.getElement()) == var4.getCount());

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   static <E> boolean addAllImpl(Multiset<E> var0, Collection<? extends E> var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         if (var1 instanceof Multiset) {
            Multiset var2 = cast(var1);
            Iterator var3 = var2.entrySet().iterator();

            while(var3.hasNext()) {
               Multiset.Entry var4 = (Multiset.Entry)var3.next();
               var0.add(var4.getElement(), var4.getCount());
            }
         } else {
            Iterators.addAll(var0, var1.iterator());
         }

         return true;
      }
   }

   static boolean removeAllImpl(Multiset<?> var0, Collection<?> var1) {
      Object var2 = var1 instanceof Multiset ? ((Multiset)var1).elementSet() : var1;
      return var0.elementSet().removeAll((Collection)var2);
   }

   static boolean retainAllImpl(Multiset<?> var0, Collection<?> var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = var1 instanceof Multiset ? ((Multiset)var1).elementSet() : var1;
      return var0.elementSet().retainAll((Collection)var2);
   }

   static <E> int setCountImpl(Multiset<E> var0, E var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "count");
      int var3 = var0.count(var1);
      int var4 = var2 - var3;
      if (var4 > 0) {
         var0.add(var1, var4);
      } else if (var4 < 0) {
         var0.remove(var1, -var4);
      }

      return var3;
   }

   static <E> boolean setCountImpl(Multiset<E> var0, E var1, int var2, int var3) {
      CollectPreconditions.checkNonnegative(var2, "oldCount");
      CollectPreconditions.checkNonnegative(var3, "newCount");
      if (var0.count(var1) == var2) {
         var0.setCount(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   static <E> Iterator<E> iteratorImpl(Multiset<E> var0) {
      return new Multisets.MultisetIteratorImpl(var0, var0.entrySet().iterator());
   }

   static <E> Spliterator<E> spliteratorImpl(Multiset<E> var0) {
      Spliterator var1 = var0.entrySet().spliterator();
      return CollectSpliterators.flatMap(var1, (var0x) -> {
         return Collections.nCopies(var0x.getCount(), var0x.getElement()).spliterator();
      }, 64 | var1.characteristics() & 1296, (long)var0.size());
   }

   static int sizeImpl(Multiset<?> var0) {
      long var1 = 0L;

      Multiset.Entry var4;
      for(Iterator var3 = var0.entrySet().iterator(); var3.hasNext(); var1 += (long)var4.getCount()) {
         var4 = (Multiset.Entry)var3.next();
      }

      return Ints.saturatedCast(var1);
   }

   static <T> Multiset<T> cast(Iterable<T> var0) {
      return (Multiset)var0;
   }

   @Beta
   public static <E> ImmutableMultiset<E> copyHighestCountFirst(Multiset<E> var0) {
      ImmutableList var1 = DECREASING_COUNT_ORDERING.immutableSortedCopy(var0.entrySet());
      return ImmutableMultiset.copyFromEntries(var1);
   }

   static final class MultisetIteratorImpl<E> implements Iterator<E> {
      private final Multiset<E> multiset;
      private final Iterator<Multiset.Entry<E>> entryIterator;
      private Multiset.Entry<E> currentEntry;
      private int laterCount;
      private int totalCount;
      private boolean canRemove;

      MultisetIteratorImpl(Multiset<E> var1, Iterator<Multiset.Entry<E>> var2) {
         super();
         this.multiset = var1;
         this.entryIterator = var2;
      }

      public boolean hasNext() {
         return this.laterCount > 0 || this.entryIterator.hasNext();
      }

      public E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            if (this.laterCount == 0) {
               this.currentEntry = (Multiset.Entry)this.entryIterator.next();
               this.totalCount = this.laterCount = this.currentEntry.getCount();
            }

            --this.laterCount;
            this.canRemove = true;
            return this.currentEntry.getElement();
         }
      }

      public void remove() {
         CollectPreconditions.checkRemove(this.canRemove);
         if (this.totalCount == 1) {
            this.entryIterator.remove();
         } else {
            this.multiset.remove(this.currentEntry.getElement());
         }

         --this.totalCount;
         this.canRemove = false;
      }
   }

   abstract static class EntrySet<E> extends Sets.ImprovedAbstractSet<Multiset.Entry<E>> {
      EntrySet() {
         super();
      }

      abstract Multiset<E> multiset();

      public boolean contains(@Nullable Object var1) {
         if (var1 instanceof Multiset.Entry) {
            Multiset.Entry var2 = (Multiset.Entry)var1;
            if (var2.getCount() <= 0) {
               return false;
            } else {
               int var3 = this.multiset().count(var2.getElement());
               return var3 == var2.getCount();
            }
         } else {
            return false;
         }
      }

      public boolean remove(Object var1) {
         if (var1 instanceof Multiset.Entry) {
            Multiset.Entry var2 = (Multiset.Entry)var1;
            Object var3 = var2.getElement();
            int var4 = var2.getCount();
            if (var4 != 0) {
               Multiset var5 = this.multiset();
               return var5.setCount(var3, var4, 0);
            }
         }

         return false;
      }

      public void clear() {
         this.multiset().clear();
      }
   }

   abstract static class ElementSet<E> extends Sets.ImprovedAbstractSet<E> {
      ElementSet() {
         super();
      }

      abstract Multiset<E> multiset();

      public void clear() {
         this.multiset().clear();
      }

      public boolean contains(Object var1) {
         return this.multiset().contains(var1);
      }

      public boolean containsAll(Collection<?> var1) {
         return this.multiset().containsAll(var1);
      }

      public boolean isEmpty() {
         return this.multiset().isEmpty();
      }

      public Iterator<E> iterator() {
         return new TransformedIterator<Multiset.Entry<E>, E>(this.multiset().entrySet().iterator()) {
            E transform(Multiset.Entry<E> var1) {
               return var1.getElement();
            }
         };
      }

      public boolean remove(Object var1) {
         return this.multiset().remove(var1, 2147483647) > 0;
      }

      public int size() {
         return this.multiset().entrySet().size();
      }
   }

   abstract static class AbstractEntry<E> implements Multiset.Entry<E> {
      AbstractEntry() {
         super();
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Multiset.Entry)) {
            return false;
         } else {
            Multiset.Entry var2 = (Multiset.Entry)var1;
            return this.getCount() == var2.getCount() && Objects.equal(this.getElement(), var2.getElement());
         }
      }

      public int hashCode() {
         Object var1 = this.getElement();
         return (var1 == null ? 0 : var1.hashCode()) ^ this.getCount();
      }

      public String toString() {
         String var1 = String.valueOf(this.getElement());
         int var2 = this.getCount();
         return var2 == 1 ? var1 : var1 + " x " + var2;
      }
   }

   private static final class FilteredMultiset<E> extends AbstractMultiset<E> {
      final Multiset<E> unfiltered;
      final Predicate<? super E> predicate;

      FilteredMultiset(Multiset<E> var1, Predicate<? super E> var2) {
         super();
         this.unfiltered = (Multiset)Preconditions.checkNotNull(var1);
         this.predicate = (Predicate)Preconditions.checkNotNull(var2);
      }

      public UnmodifiableIterator<E> iterator() {
         return Iterators.filter(this.unfiltered.iterator(), this.predicate);
      }

      Set<E> createElementSet() {
         return Sets.filter(this.unfiltered.elementSet(), this.predicate);
      }

      Set<Multiset.Entry<E>> createEntrySet() {
         return Sets.filter(this.unfiltered.entrySet(), new Predicate<Multiset.Entry<E>>() {
            public boolean apply(Multiset.Entry<E> var1) {
               return FilteredMultiset.this.predicate.apply(var1.getElement());
            }
         });
      }

      Iterator<Multiset.Entry<E>> entryIterator() {
         throw new AssertionError("should never be called");
      }

      int distinctElements() {
         return this.elementSet().size();
      }

      public int count(@Nullable Object var1) {
         int var2 = this.unfiltered.count(var1);
         if (var2 > 0) {
            return this.predicate.apply(var1) ? var2 : 0;
         } else {
            return 0;
         }
      }

      public int add(@Nullable E var1, int var2) {
         Preconditions.checkArgument(this.predicate.apply(var1), "Element %s does not match predicate %s", var1, this.predicate);
         return this.unfiltered.add(var1, var2);
      }

      public int remove(@Nullable Object var1, int var2) {
         CollectPreconditions.checkNonnegative(var2, "occurrences");
         if (var2 == 0) {
            return this.count(var1);
         } else {
            return this.contains(var1) ? this.unfiltered.remove(var1, var2) : 0;
         }
      }

      public void clear() {
         this.elementSet().clear();
      }
   }

   static class ImmutableEntry<E> extends Multisets.AbstractEntry<E> implements Serializable {
      @Nullable
      private final E element;
      private final int count;
      private static final long serialVersionUID = 0L;

      ImmutableEntry(@Nullable E var1, int var2) {
         super();
         this.element = var1;
         this.count = var2;
         CollectPreconditions.checkNonnegative(var2, "count");
      }

      @Nullable
      public final E getElement() {
         return this.element;
      }

      public final int getCount() {
         return this.count;
      }

      public Multisets.ImmutableEntry<E> nextInBucket() {
         return null;
      }
   }

   static class UnmodifiableMultiset<E> extends ForwardingMultiset<E> implements Serializable {
      final Multiset<? extends E> delegate;
      transient Set<E> elementSet;
      transient Set<Multiset.Entry<E>> entrySet;
      private static final long serialVersionUID = 0L;

      UnmodifiableMultiset(Multiset<? extends E> var1) {
         super();
         this.delegate = var1;
      }

      protected Multiset<E> delegate() {
         return this.delegate;
      }

      Set<E> createElementSet() {
         return Collections.unmodifiableSet(this.delegate.elementSet());
      }

      public Set<E> elementSet() {
         Set var1 = this.elementSet;
         return var1 == null ? (this.elementSet = this.createElementSet()) : var1;
      }

      public Set<Multiset.Entry<E>> entrySet() {
         Set var1 = this.entrySet;
         return var1 == null ? (this.entrySet = Collections.unmodifiableSet(this.delegate.entrySet())) : var1;
      }

      public Iterator<E> iterator() {
         return Iterators.unmodifiableIterator(this.delegate.iterator());
      }

      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      public int add(E var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public int remove(Object var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public int setCount(E var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean setCount(E var1, int var2, int var3) {
         throw new UnsupportedOperationException();
      }
   }
}
