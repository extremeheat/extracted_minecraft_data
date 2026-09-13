package net.minecraft.client.particle;

import java.util.concurrent.Callable;

class EffectRenderer$4 implements Callable {
   EffectRenderer$4(EffectRenderer var1, int var2) {
      super();
      this.field_147903_b = var1;
      this.field_147904_a = var2;
   }

   public String call() {
      if (this.field_147904_a == 0) {
         return "MISC_TEXTURE";
      } else if (this.field_147904_a == 1) {
         return "TERRAIN_TEXTURE";
      } else if (this.field_147904_a == 2) {
         return "ITEM_TEXTURE";
      } else {
         return this.field_147904_a == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + this.field_147904_a;
      }
   }
}
