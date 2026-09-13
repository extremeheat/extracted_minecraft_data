package net.minecraft.client.particle;

import java.util.concurrent.Callable;

class EffectRenderer$2 implements Callable {
   EffectRenderer$2(EffectRenderer var1, int var2) {
      super();
      this.field_147897_b = var1;
      this.field_147898_a = var2;
   }

   public String call() {
      if (this.field_147898_a == 0) {
         return "MISC_TEXTURE";
      } else if (this.field_147898_a == 1) {
         return "TERRAIN_TEXTURE";
      } else if (this.field_147898_a == 2) {
         return "ITEM_TEXTURE";
      } else {
         return this.field_147898_a == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + this.field_147898_a;
      }
   }
}
