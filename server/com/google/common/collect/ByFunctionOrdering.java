package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class ByFunctionOrdering<F, T> extends Ordering<F> implements Serializable {
   final Function<F, ? extends T> function;
   final Ordering<T> ordering;
   private static final long serialVersionUID = 0L;

   ByFunctionOrdering(Function<F, ? extends T> var1, Ordering<T> var2) {
      super();
      this.function = (Function)Preconditions.checkNotNull(var1);
      this.ordering = (Ordering)Preconditions.checkNotNull(var2);
   }

   public int compare(F var1, F var2) {
      return this.ordering.compare(this.function.apply(var1), this.function.apply(var2));
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ByFunctionOrdering)) {
         return false;
      } else {
         ByFunctionOrdering var2 = (ByFunctionOrdering)var1;
         return this.function.equals(var2.function) && this.ordering.equals(var2.ordering);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.function, this.ordering);
   }

   public String toString() {
      return this.ordering + ".onResultOf(" + this.function + ")";
   }
}
