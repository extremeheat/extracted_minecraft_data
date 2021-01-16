package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import java.util.Spliterator;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class ImmutableSortedAsList<E> extends RegularImmutableAsList<E> implements SortedIterable<E> {
   ImmutableSortedAsList(ImmutableSortedSet<E> var1, ImmutableList<E> var2) {
      super(var1, (ImmutableList)var2);
   }

   ImmutableSortedSet<E> delegateCollection() {
      return (ImmutableSortedSet)super.delegateCollection();
   }

   public Comparator<? super E> comparator() {
      return this.delegateCollection().comparator();
   }

   @GwtIncompatible
   public int indexOf(@Nullable Object var1) {
      int var2 = this.delegateCollection().indexOf(var1);
      return var2 >= 0 && this.get(var2).equals(var1) ? var2 : -1;
   }

   @GwtIncompatible
   public int lastIndexOf(@Nullable Object var1) {
      return this.indexOf(var1);
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1) >= 0;
   }

   @GwtIncompatible
   ImmutableList<E> subListUnchecked(int var1, int var2) {
      ImmutableList var3 = super.subListUnchecked(var1, var2);
      return (new RegularImmutableSortedSet(var3, this.comparator())).asList();
   }

   public Spliterator<E> spliterator() {
      return CollectSpliterators.indexed(this.size(), 1301, this.delegateList()::get, this.comparator());
   }
}
