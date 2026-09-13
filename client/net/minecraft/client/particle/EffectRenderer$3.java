package net.minecraft.client.particle;

import java.util.concurrent.Callable;

class EffectRenderer$3 implements Callable {
   EffectRenderer$3(EffectRenderer var1, EntityFX var2) {
      super();
      this.field_147900_b = var1;
      this.field_147901_a = var2;
   }

   public String call() {
      return this.field_147901_a.toString();
   }
}
