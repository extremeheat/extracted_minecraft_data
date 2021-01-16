package com.mojang.datafixers.functions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class Apply<A, B> extends PointFree<B> {
   protected final PointFree<Function<A, B>> func;
   protected final PointFree<A> arg;
   protected final Type<A> argType;

   public Apply(PointFree<Function<A, B>> var1, PointFree<A> var2, Type<A> var3) {
      super();
      this.func = var1;
      this.arg = var2;
      this.argType = var3;
   }

   public Function<DynamicOps<?>, B> eval() {
      return (var1) -> {
         return ((Function)this.func.evalCached().apply(var1)).apply(this.arg.evalCached().apply(var1));
      };
   }

   public String toString(int var1) {
      return "(ap " + this.func.toString(var1 + 1) + "\n" + indent(var1 + 1) + this.arg.toString(var1 + 1) + "\n" + indent(var1) + ")";
   }

   public Optional<? extends PointFree<B>> all(PointFreeRule var1, Type<B> var2) {
      return Optional.of(Functions.app((PointFree)var1.rewrite(DSL.func(this.argType, var2), this.func).map((var0) -> {
         return var0;
      }).orElse(this.func), (PointFree)var1.rewrite(this.argType, this.arg).map((var0) -> {
         return var0;
      }).orElse(this.arg), this.argType));
   }

   public Optional<? extends PointFree<B>> one(PointFreeRule var1, Type<B> var2) {
      return (Optional)var1.rewrite(DSL.func(this.argType, var2), this.func).map((var1x) -> {
         return Optional.of(Functions.app(var1x, this.arg, this.argType));
      }).orElseGet(() -> {
         return var1.rewrite(this.argType, this.arg).map((var1x) -> {
            return Functions.app(this.func, var1x, this.argType);
         });
      });
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Apply)) {
         return false;
      } else {
         Apply var2 = (Apply)var1;
         return Objects.equals(this.func, var2.func) && Objects.equals(this.arg, var2.arg);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.func, this.arg});
   }
}
