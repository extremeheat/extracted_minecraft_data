package com.mojang.datafixers.optics;

import com.mojang.datafixers.util.Pair;

public final class Proj1<F, G, F2> implements Lens<Pair<F, G>, Pair<F2, G>, F, F2> {
   public Proj1() {
      super();
   }

   public F view(Pair<F, G> var1) {
      return var1.getFirst();
   }

   public Pair<F2, G> update(F2 var1, Pair<F, G> var2) {
      return Pair.of(var1, var2.getSecond());
   }

   public String toString() {
      return "\u03c01";
   }

   public boolean equals(Object var1) {
      return var1 instanceof Proj1;
   }
}
