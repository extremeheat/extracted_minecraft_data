package net.minecraft.client.particle;

import java.util.concurrent.Callable;

class EffectRenderer$1 implements Callable {
   EffectRenderer$1(EffectRenderer var1, EntityFX var2) {
      super();
      this.field_147213_b = var1;
      this.field_147214_a = var2;
   }

   public String call() {
      return this.field_147214_a.toString();
   }
}
