package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.NavigableSet;

@GwtCompatible(
   emulated = true
)
final class UnmodifiableSortedMultiset<E> extends Multisets.UnmodifiableMultiset<E> implements SortedMultiset<E> {
   private transient UnmodifiableSortedMultiset<E> descendingMultiset;
   private static final long serialVersionUID = 0L;

   UnmodifiableSortedMultiset(SortedMultiset<E> var1) {
      super(var1);
   }

   protected SortedMultiset<E> delegate() {
      return (SortedMultiset)super.delegate();
   }

   public Comparator<? super E> comparator() {
      return this.delegate().comparator();
   }

   NavigableSet<E> createElementSet() {
      return Sets.unmodifiableNavigableSet(this.delegate().elementSet());
   }

   public NavigableSet<E> elementSet() {
      return (NavigableSet)super.elementSet();
   }

   public SortedMultiset<E> descendingMultiset() {
      UnmodifiableSortedMultiset var1 = this.descendingMultiset;
      if (var1 == null) {
         var1 = new UnmodifiableSortedMultiset(this.delegate().descendingMultiset());
         var1.descendingMultiset = this;
         return this.descendingMultiset = var1;
      } else {
         return var1;
      }
   }

   public Multiset.Entry<E> firstEntry() {
      return this.delegate().firstEntry();
   }

   public Multiset.Entry<E> lastEntry() {
      return this.delegate().lastEntry();
   }

   public Multiset.Entry<E> pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   public Multiset.Entry<E> pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public SortedMultiset<E> headMultiset(E var1, BoundType var2) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().headMultiset(var1, var2));
   }

   public SortedMultiset<E> subMultiset(E var1, BoundType var2, E var3, BoundType var4) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().subMultiset(var1, var2, var3, var4));
   }

   public SortedMultiset<E> tailMultiset(E var1, BoundType var2) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().tailMultiset(var1, var2));
   }
}
