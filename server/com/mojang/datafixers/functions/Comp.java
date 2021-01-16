package com.mojang.datafixers.functions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class Comp<A, B, C> extends PointFree<Function<A, C>> {
   protected final Type<B> middleType;
   protected final PointFree<Function<B, C>> first;
   protected final PointFree<Function<A, B>> second;

   public Comp(Type<B> var1, PointFree<Function<B, C>> var2, PointFree<Function<A, B>> var3) {
      super();
      this.middleType = var1;
      this.first = var2;
      this.second = var3;
   }

   public String toString(int var1) {
      return "(\n" + indent(var1 + 1) + this.first.toString(var1 + 1) + "\n" + indent(var1 + 1) + "\u25e6\n" + indent(var1 + 1) + this.second.toString(var1 + 1) + "\n" + indent(var1) + ")";
   }

   public Optional<? extends PointFree<Function<A, C>>> all(PointFreeRule var1, Type<Function<A, C>> var2) {
      Func var3 = (Func)var2;
      return Optional.of(Functions.comp(this.middleType, (PointFree)var1.rewrite(DSL.func(this.middleType, var3.second()), this.first).map((var0) -> {
         return var0;
      }).orElse(this.first), (PointFree)var1.rewrite(DSL.func(var3.first(), this.middleType), this.second).map((var0) -> {
         return var0;
      }).orElse(this.second)));
   }

   public Optional<? extends PointFree<Function<A, C>>> one(PointFreeRule var1, Type<Function<A, C>> var2) {
      Func var3 = (Func)var2;
      return (Optional)var1.rewrite(DSL.func(this.middleType, var3.second()), this.first).map((var1x) -> {
         return Optional.of(Functions.comp(this.middleType, var1x, this.second));
      }).orElseGet(() -> {
         return var1.rewrite(DSL.func(var3.first(), this.middleType), this.second).map((var1x) -> {
            return Functions.comp(this.middleType, this.first, var1x);
         });
      });
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Comp var2 = (Comp)var1;
         return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.first, this.second});
   }

   public Function<DynamicOps<?>, Function<A, C>> eval() {
      return (var1) -> {
         return (var2) -> {
            Function var3 = (Function)this.second.evalCached().apply(var1);
            Function var4 = (Function)this.first.evalCached().apply(var1);
            return var4.apply(var3.apply(var2));
         };
      };
   }
}
