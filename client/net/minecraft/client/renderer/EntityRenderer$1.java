package net.minecraft.client.renderer;

import java.util.concurrent.Callable;

class EntityRenderer$1 implements Callable {
   EntityRenderer$1(EntityRenderer var1) {
      super();
      this.field_90032_a = var1;
   }

   public String call() {
      return EntityRenderer.access$000(this.field_90032_a).field_71462_r.getClass().getCanonicalName();
   }
}
