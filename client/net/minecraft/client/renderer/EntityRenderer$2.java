package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import org.lwjgl.input.Mouse;

class EntityRenderer$2 implements Callable {
   EntityRenderer$2(EntityRenderer var1, int var2, int var3) {
      super();
      this.field_90025_c = var1;
      this.field_90026_a = var2;
      this.field_90024_b = var3;
   }

   public String call() {
      return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", this.field_90026_a, this.field_90024_b, Mouse.getX(), Mouse.getY());
   }
}
