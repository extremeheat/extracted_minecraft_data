package com.mojang.datafixers.optics;

import com.mojang.datafixers.util.Pair;

public final class Proj2<F, G, G2> implements Lens<Pair<F, G>, Pair<F, G2>, G, G2> {
   public Proj2() {
      super();
   }

   public G view(Pair<F, G> var1) {
      return var1.getSecond();
   }

   public Pair<F, G2> update(G2 var1, Pair<F, G> var2) {
      return Pair.of(var2.getFirst(), var1);
   }

   public String toString() {
      return "\u03c02";
   }

   public boolean equals(Object var1) {
      return var1 instanceof Proj2;
   }
}
