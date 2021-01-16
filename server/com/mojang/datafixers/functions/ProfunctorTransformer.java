package com.mojang.datafixers.functions;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.optics.Optic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

final class ProfunctorTransformer<S, T, A, B> extends PointFree<Function<Function<A, B>, Function<S, T>>> {
   protected final Optic<? super FunctionType.Instance.Mu, S, T, A, B> optic;
   protected final Function<App2<FunctionType.Mu, A, B>, App2<FunctionType.Mu, S, T>> func;
   private final Function<Function<A, B>, Function<S, T>> unwrappedFunction;

   public ProfunctorTransformer(Optic<? super FunctionType.Instance.Mu, S, T, A, B> var1) {
      super();
      this.optic = var1;
      this.func = var1.eval(FunctionType.Instance.INSTANCE);
      this.unwrappedFunction = (var1x) -> {
         return FunctionType.unbox((App2)this.func.apply(FunctionType.create(var1x)));
      };
   }

   public String toString(int var1) {
      return "Optic[" + this.optic + "]";
   }

   public Function<DynamicOps<?>, Function<Function<A, B>, Function<S, T>>> eval() {
      return (var1) -> {
         return this.unwrappedFunction;
      };
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ProfunctorTransformer var2 = (ProfunctorTransformer)var1;
         return Objects.equals(this.optic, var2.optic);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.optic});
   }
}
