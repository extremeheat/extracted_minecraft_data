package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class ComparatorOrdering<T> extends Ordering<T> implements Serializable {
   final Comparator<T> comparator;
   private static final long serialVersionUID = 0L;

   ComparatorOrdering(Comparator<T> var1) {
      super();
      this.comparator = (Comparator)Preconditions.checkNotNull(var1);
   }

   public int compare(T var1, T var2) {
      return this.comparator.compare(var1, var2);
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof ComparatorOrdering) {
         ComparatorOrdering var2 = (ComparatorOrdering)var1;
         return this.comparator.equals(var2.comparator);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.comparator.hashCode();
   }

   public String toString() {
      return this.comparator.toString();
   }
}
