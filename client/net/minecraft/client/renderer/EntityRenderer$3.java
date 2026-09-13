package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import net.minecraft.client.gui.ScaledResolution;

class EntityRenderer$3 implements Callable {
   EntityRenderer$3(EntityRenderer var1, ScaledResolution var2) {
      super();
      this.field_90028_b = var1;
      this.field_90029_a = var2;
   }

   public String call() {
      return String.format(
         "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
         this.field_90029_a.func_78326_a(),
         this.field_90029_a.func_78328_b(),
         EntityRenderer.access$000(this.field_90028_b).field_71443_c,
         EntityRenderer.access$000(this.field_90028_b).field_71440_d,
         this.field_90029_a.func_78325_e()
      );
   }
}
