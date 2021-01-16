package com.mojang.datafixers;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Optic;
import java.util.Set;

public final class OpticParts<A, B> {
   private final Set<TypeToken<? extends K1>> bounds;
   private final Optic<?, ?, ?, A, B> optic;

   public OpticParts(Set<TypeToken<? extends K1>> var1, Optic<?, ?, ?, A, B> var2) {
      super();
      this.bounds = var1;
      this.optic = var2;
   }

   public Set<TypeToken<? extends K1>> bounds() {
      return this.bounds;
   }

   public Optic<?, ?, ?, A, B> optic() {
      return this.optic;
   }
}
