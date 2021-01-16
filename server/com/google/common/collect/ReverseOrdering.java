package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class ReverseOrdering<T> extends Ordering<T> implements Serializable {
   final Ordering<? super T> forwardOrder;
   private static final long serialVersionUID = 0L;

   ReverseOrdering(Ordering<? super T> var1) {
      super();
      this.forwardOrder = (Ordering)Preconditions.checkNotNull(var1);
   }

   public int compare(T var1, T var2) {
      return this.forwardOrder.compare(var2, var1);
   }

   public <S extends T> Ordering<S> reverse() {
      return this.forwardOrder;
   }

   public <E extends T> E min(E var1, E var2) {
      return this.forwardOrder.max(var1, var2);
   }

   public <E extends T> E min(E var1, E var2, E var3, E... var4) {
      return this.forwardOrder.max(var1, var2, var3, var4);
   }

   public <E extends T> E min(Iterator<E> var1) {
      return this.forwardOrder.max(var1);
   }

   public <E extends T> E min(Iterable<E> var1) {
      return this.forwardOrder.max(var1);
   }

   public <E extends T> E max(E var1, E var2) {
      return this.forwardOrder.min(var1, var2);
   }

   public <E extends T> E max(E var1, E var2, E var3, E... var4) {
      return this.forwardOrder.min(var1, var2, var3, var4);
   }

   public <E extends T> E max(Iterator<E> var1) {
      return this.forwardOrder.min(var1);
   }

   public <E extends T> E max(Iterable<E> var1) {
      return this.forwardOrder.min(var1);
   }

   public int hashCode() {
      return -this.forwardOrder.hashCode();
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof ReverseOrdering) {
         ReverseOrdering var2 = (ReverseOrdering)var1;
         return this.forwardOrder.equals(var2.forwardOrder);
      } else {
         return false;
      }
   }

   public String toString() {
      return this.forwardOrder + ".reverse()";
   }
}
