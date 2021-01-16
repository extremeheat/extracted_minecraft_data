package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class ExplicitOrdering<T> extends Ordering<T> implements Serializable {
   final ImmutableMap<T, Integer> rankMap;
   private static final long serialVersionUID = 0L;

   ExplicitOrdering(List<T> var1) {
      this(Maps.indexMap(var1));
   }

   ExplicitOrdering(ImmutableMap<T, Integer> var1) {
      super();
      this.rankMap = var1;
   }

   public int compare(T var1, T var2) {
      return this.rank(var1) - this.rank(var2);
   }

   private int rank(T var1) {
      Integer var2 = (Integer)this.rankMap.get(var1);
      if (var2 == null) {
         throw new Ordering.IncomparableValueException(var1);
      } else {
         return var2;
      }
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 instanceof ExplicitOrdering) {
         ExplicitOrdering var2 = (ExplicitOrdering)var1;
         return this.rankMap.equals(var2.rankMap);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.rankMap.hashCode();
   }

   public String toString() {
      return "Ordering.explicit(" + this.rankMap.keySet() + ")";
   }
}
