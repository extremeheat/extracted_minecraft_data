package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
final class FunctionalEquivalence<F, T> extends Equivalence<F> implements Serializable {
   private static final long serialVersionUID = 0L;
   private final Function<F, ? extends T> function;
   private final Equivalence<T> resultEquivalence;

   FunctionalEquivalence(Function<F, ? extends T> var1, Equivalence<T> var2) {
      super();
      this.function = (Function)Preconditions.checkNotNull(var1);
      this.resultEquivalence = (Equivalence)Preconditions.checkNotNull(var2);
   }

   protected boolean doEquivalent(F var1, F var2) {
      return this.resultEquivalence.equivalent(this.function.apply(var1), this.function.apply(var2));
   }

   protected int doHash(F var1) {
      return this.resultEquivalence.hash(this.function.apply(var1));
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof FunctionalEquivalence)) {
         return false;
      } else {
         FunctionalEquivalence var2 = (FunctionalEquivalence)var1;
         return this.function.equals(var2.function) && this.resultEquivalence.equals(var2.resultEquivalence);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.function, this.resultEquivalence);
   }

   public String toString() {
      return this.resultEquivalence + ".onResultOf(" + this.function + ")";
   }
}
