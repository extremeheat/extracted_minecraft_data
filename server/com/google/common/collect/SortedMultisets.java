package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.j2objc.annotations.Weak;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class SortedMultisets {
   private SortedMultisets() {
      super();
   }

   private static <E> E getElementOrThrow(Multiset.Entry<E> var0) {
      if (var0 == null) {
         throw new NoSuchElementException();
      } else {
         return var0.getElement();
      }
   }

   private static <E> E getElementOrNull(@Nullable Multiset.Entry<E> var0) {
      return var0 == null ? null : var0.getElement();
   }

   @GwtIncompatible
   static class NavigableElementSet<E> extends SortedMultisets.ElementSet<E> implements NavigableSet<E> {
      NavigableElementSet(SortedMultiset<E> var1) {
         super(var1);
      }

      public E lower(E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().headMultiset(var1, BoundType.OPEN).lastEntry());
      }

      public E floor(E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().headMultiset(var1, BoundType.CLOSED).lastEntry());
      }

      public E ceiling(E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().tailMultiset(var1, BoundType.CLOSED).firstEntry());
      }

      public E higher(E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().tailMultiset(var1, BoundType.OPEN).firstEntry());
      }

      public NavigableSet<E> descendingSet() {
         return new SortedMultisets.NavigableElementSet(this.multiset().descendingMultiset());
      }

      public Iterator<E> descendingIterator() {
         return this.descendingSet().iterator();
      }

      public E pollFirst() {
         return SortedMultisets.getElementOrNull(this.multiset().pollFirstEntry());
      }

      public E pollLast() {
         return SortedMultisets.getElementOrNull(this.multiset().pollLastEntry());
      }

      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         return new SortedMultisets.NavigableElementSet(this.multiset().subMultiset(var1, BoundType.forBoolean(var2), var3, BoundType.forBoolean(var4)));
      }

      public NavigableSet<E> headSet(E var1, boolean var2) {
         return new SortedMultisets.NavigableElementSet(this.multiset().headMultiset(var1, BoundType.forBoolean(var2)));
      }

      public NavigableSet<E> tailSet(E var1, boolean var2) {
         return new SortedMultisets.NavigableElementSet(this.multiset().tailMultiset(var1, BoundType.forBoolean(var2)));
      }
   }

   static class ElementSet<E> extends Multisets.ElementSet<E> implements SortedSet<E> {
      @Weak
      private final SortedMultiset<E> multiset;

      ElementSet(SortedMultiset<E> var1) {
         super();
         this.multiset = var1;
      }

      final SortedMultiset<E> multiset() {
         return this.multiset;
      }

      public Comparator<? super E> comparator() {
         return this.multiset().comparator();
      }

      public SortedSet<E> subSet(E var1, E var2) {
         return this.multiset().subMultiset(var1, BoundType.CLOSED, var2, BoundType.OPEN).elementSet();
      }

      public SortedSet<E> headSet(E var1) {
         return this.multiset().headMultiset(var1, BoundType.OPEN).elementSet();
      }

      public SortedSet<E> tailSet(E var1) {
         return this.multiset().tailMultiset(var1, BoundType.CLOSED).elementSet();
      }

      public E first() {
         return SortedMultisets.getElementOrThrow(this.multiset().firstEntry());
      }

      public E last() {
         return SortedMultisets.getElementOrThrow(this.multiset().lastEntry());
      }
   }
}
