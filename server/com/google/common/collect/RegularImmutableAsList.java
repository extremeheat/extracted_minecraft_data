package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.function.Consumer;

@GwtCompatible(
   emulated = true
)
class RegularImmutableAsList<E> extends ImmutableAsList<E> {
   private final ImmutableCollection<E> delegate;
   private final ImmutableList<? extends E> delegateList;

   RegularImmutableAsList(ImmutableCollection<E> var1, ImmutableList<? extends E> var2) {
      super();
      this.delegate = var1;
      this.delegateList = var2;
   }

   RegularImmutableAsList(ImmutableCollection<E> var1, Object[] var2) {
      this(var1, ImmutableList.asImmutableList(var2));
   }

   ImmutableCollection<E> delegateCollection() {
      return this.delegate;
   }

   ImmutableList<? extends E> delegateList() {
      return this.delegateList;
   }

   public UnmodifiableListIterator<E> listIterator(int var1) {
      return this.delegateList.listIterator(var1);
   }

   @GwtIncompatible
   public void forEach(Consumer<? super E> var1) {
      this.delegateList.forEach(var1);
   }

   @GwtIncompatible
   int copyIntoArray(Object[] var1, int var2) {
      return this.delegateList.copyIntoArray(var1, var2);
   }

   public E get(int var1) {
      return this.delegateList.get(var1);
   }
}
