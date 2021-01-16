package com.mojang.datafixers.optics;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import java.util.Iterator;
import java.util.List;

public final class ListTraversal<A, B> implements Traversal<List<A>, List<B>, A, B> {
   public ListTraversal() {
      super();
   }

   public <F extends K1> FunctionType<List<A>, App<F, List<B>>> wander(Applicative<F, ?> var1, FunctionType<A, App<F, B>> var2) {
      return (var2x) -> {
         App var3 = var1.point(ImmutableList.builder());

         Object var5;
         for(Iterator var4 = var2x.iterator(); var4.hasNext(); var3 = var1.ap2(var1.point(ImmutableList.Builder::add), var3, (App)var2.apply(var5))) {
            var5 = var4.next();
         }

         return var1.map(ImmutableList.Builder::build, var3);
      };
   }

   public boolean equals(Object var1) {
      return var1 instanceof ListTraversal;
   }

   public String toString() {
      return "ListTraversal";
   }
}
