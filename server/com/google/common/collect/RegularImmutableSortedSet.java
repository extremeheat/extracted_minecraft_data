package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E> {
   static final RegularImmutableSortedSet<Comparable> NATURAL_EMPTY_SET = new RegularImmutableSortedSet(ImmutableList.of(), Ordering.natural());
   private final transient ImmutableList<E> elements;

   RegularImmutableSortedSet(ImmutableList<E> var1, Comparator<? super E> var2) {
      super(var2);
      this.elements = var1;
   }

   public UnmodifiableIterator<E> iterator() {
      return this.elements.iterator();
   }

   @GwtIncompatible
   public UnmodifiableIterator<E> descendingIterator() {
      return this.elements.reverse().iterator();
   }

   public Spliterator<E> spliterator() {
      return this.asList().spliterator();
   }

   public void forEach(Consumer<? super E> var1) {
      this.elements.forEach(var1);
   }

   public int size() {
      return this.elements.size();
   }

   public boolean contains(@Nullable Object var1) {
      try {
         return var1 != null && this.unsafeBinarySearch(var1) >= 0;
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public boolean containsAll(Collection<?> var1) {
      if (var1 instanceof Multiset) {
         var1 = ((Multiset)var1).elementSet();
      }

      if (SortedIterables.hasSameComparator(this.comparator(), (Iterable)var1) && ((Collection)var1).size() > 1) {
         PeekingIterator var2 = Iterators.peekingIterator((Iterator)this.iterator());
         Iterator var3 = ((Collection)var1).iterator();
         Object var4 = var3.next();

         try {
            while(var2.hasNext()) {
               int var5 = this.unsafeCompare(var2.peek(), var4);
               if (var5 < 0) {
                  var2.next();
               } else if (var5 == 0) {
                  if (!var3.hasNext()) {
                     return true;
                  }

                  var4 = var3.next();
               } else if (var5 > 0) {
                  return false;
               }
            }

            return false;
         } catch (NullPointerException var6) {
            return false;
         } catch (ClassCastException var7) {
            return false;
         }
      } else {
         return super.containsAll((Collection)var1);
      }
   }

   private int unsafeBinarySearch(Object var1) throws ClassCastException {
      return Collections.binarySearch(this.elements, var1, this.unsafeComparator());
   }

   boolean isPartialView() {
      return this.elements.isPartialView();
   }

   int copyIntoArray(Object[] var1, int var2) {
      return this.elements.copyIntoArray(var1, var2);
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Set)) {
         return false;
      } else {
         Set var2 = (Set)var1;
         if (this.size() != var2.size()) {
            return false;
         } else if (this.isEmpty()) {
            return true;
         } else if (SortedIterables.hasSameComparator(this.comparator, var2)) {
            Iterator var3 = var2.iterator();

            try {
               UnmodifiableIterator var4 = this.iterator();

               Object var5;
               Object var6;
               do {
                  if (!var4.hasNext()) {
                     return true;
                  }

                  var5 = var4.next();
                  var6 = var3.next();
               } while(var6 != null && this.unsafeCompare(var5, var6) == 0);

               return false;
            } catch (ClassCastException var7) {
               return false;
            } catch (NoSuchElementException var8) {
               return false;
            }
         } else {
            return this.containsAll(var2);
         }
      }
   }

   public E first() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.elements.get(0);
      }
   }

   public E last() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.elements.get(this.size() - 1);
      }
   }

   public E lower(E var1) {
      int var2 = this.headIndex(var1, false) - 1;
      return var2 == -1 ? null : this.elements.get(var2);
   }

   public E floor(E var1) {
      int var2 = this.headIndex(var1, true) - 1;
      return var2 == -1 ? null : this.elements.get(var2);
   }

   public E ceiling(E var1) {
      int var2 = this.tailIndex(var1, true);
      return var2 == this.size() ? null : this.elements.get(var2);
   }

   public E higher(E var1) {
      int var2 = this.tailIndex(var1, false);
      return var2 == this.size() ? null : this.elements.get(var2);
   }

   ImmutableSortedSet<E> headSetImpl(E var1, boolean var2) {
      return this.getSubSet(0, this.headIndex(var1, var2));
   }

   int headIndex(E var1, boolean var2) {
      return SortedLists.binarySearch(this.elements, (Object)Preconditions.checkNotNull(var1), (Comparator)this.comparator(), var2 ? SortedLists.KeyPresentBehavior.FIRST_AFTER : SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
   }

   ImmutableSortedSet<E> subSetImpl(E var1, boolean var2, E var3, boolean var4) {
      return this.tailSetImpl(var1, var2).headSetImpl(var3, var4);
   }

   ImmutableSortedSet<E> tailSetImpl(E var1, boolean var2) {
      return this.getSubSet(this.tailIndex(var1, var2), this.size());
   }

   int tailIndex(E var1, boolean var2) {
      return SortedLists.binarySearch(this.elements, (Object)Preconditions.checkNotNull(var1), (Comparator)this.comparator(), var2 ? SortedLists.KeyPresentBehavior.FIRST_PRESENT : SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
   }

   Comparator<Object> unsafeComparator() {
      return this.comparator;
   }

   RegularImmutableSortedSet<E> getSubSet(int var1, int var2) {
      if (var1 == 0 && var2 == this.size()) {
         return this;
      } else {
         return var1 < var2 ? new RegularImmutableSortedSet(this.elements.subList(var1, var2), this.comparator) : emptySet(this.comparator);
      }
   }

   int indexOf(@Nullable Object var1) {
      if (var1 == null) {
         return -1;
      } else {
         int var2;
         try {
            var2 = SortedLists.binarySearch(this.elements, (Object)var1, (Comparator)this.unsafeComparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
         } catch (ClassCastException var4) {
            return -1;
         }

         return var2 >= 0 ? var2 : -1;
      }
   }

   ImmutableList<E> createAsList() {
      return (ImmutableList)(this.size() <= 1 ? this.elements : new ImmutableSortedAsList(this, this.elements));
   }

   ImmutableSortedSet<E> createDescendingSet() {
      Ordering var1 = Ordering.from(this.comparator).reverse();
      return this.isEmpty() ? emptySet(var1) : new RegularImmutableSortedSet(this.elements.reverse(), var1);
   }
}
