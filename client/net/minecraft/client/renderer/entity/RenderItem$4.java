package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;

class RenderItem$4 implements Callable {
   RenderItem$4(RenderItem var1, ItemStack var2) {
      super();
      this.field_147931_b = var1;
      this.field_147932_a = var2;
   }

   public String call() {
      return String.valueOf(this.field_147932_a.func_77962_s());
   }
}
