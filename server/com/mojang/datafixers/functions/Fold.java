package com.mojang.datafixers.functions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.types.families.Algebra;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

final class Fold<A, B> extends PointFree<Function<A, B>> {
   private static final Map<Pair<RecursiveTypeFamily, Algebra>, IntFunction<RewriteResult<?, ?>>> HMAP_CACHE = Maps.newConcurrentMap();
   private static final Map<Pair<IntFunction<RewriteResult<?, ?>>, Integer>, RewriteResult<?, ?>> HMAP_APPLY_CACHE = Maps.newConcurrentMap();
   protected final RecursivePoint.RecursivePointType<A> aType;
   protected final RewriteResult<?, B> function;
   protected final Algebra algebra;
   protected final int index;

   public Fold(RecursivePoint.RecursivePointType<A> var1, RewriteResult<?, B> var2, Algebra var3, int var4) {
      super();
      this.aType = var1;
      this.function = var2;
      this.algebra = var3;
      this.index = var4;
   }

   private <FB> PointFree<Function<A, B>> cap(RewriteResult<?, B> var1, RewriteResult<?, FB> var2) {
      return Functions.comp(var2.view().newType(), var1.view().function(), var2.view().function());
   }

   public Function<DynamicOps<?>, Function<A, B>> eval() {
      return (var1) -> {
         return (var2) -> {
            RecursiveTypeFamily var3 = this.aType.family();
            IntFunction var4 = (IntFunction)HMAP_CACHE.computeIfAbsent(Pair.of(var3, this.algebra), (var0) -> {
               return ((RecursiveTypeFamily)var0.getFirst()).template().hmap((TypeFamily)var0.getFirst(), ((RecursiveTypeFamily)var0.getFirst()).fold((Algebra)var0.getSecond()));
            });
            RewriteResult var5 = (RewriteResult)HMAP_APPLY_CACHE.computeIfAbsent(Pair.of(var4, this.index), (var0) -> {
               return (RewriteResult)((IntFunction)var0.getFirst()).apply((Integer)var0.getSecond());
            });
            PointFree var6 = this.cap(this.function, var5);
            return ((Function)var6.evalCached().apply(var1)).apply(var2);
         };
      };
   }

   public String toString(int var1) {
      return "fold(" + this.aType + ", " + this.index + ", \n" + indent(var1 + 1) + this.algebra.toString(var1 + 1) + "\n" + indent(var1) + ")";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Fold var2 = (Fold)var1;
         return Objects.equals(this.aType, var2.aType) && Objects.equals(this.algebra, var2.algebra);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.aType, this.algebra});
   }
}
