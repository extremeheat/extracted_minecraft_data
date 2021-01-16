package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Spliterator;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class SingletonImmutableList<E> extends ImmutableList<E> {
   final transient E element;

   SingletonImmutableList(E var1) {
      super();
      this.element = Preconditions.checkNotNull(var1);
   }

   public E get(int var1) {
      Preconditions.checkElementIndex(var1, 1);
      return this.element;
   }

   public UnmodifiableIterator<E> iterator() {
      return Iterators.singletonIterator(this.element);
   }

   public Spliterator<E> spliterator() {
      return Collections.singleton(this.element).spliterator();
   }

   public int size() {
      return 1;
   }

   public ImmutableList<E> subList(int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var2, 1);
      return (ImmutableList)(var1 == var2 ? ImmutableList.of() : this);
   }

   public String toString() {
      return '[' + this.element.toString() + ']';
   }

   boolean isPartialView() {
      return false;
   }
}
