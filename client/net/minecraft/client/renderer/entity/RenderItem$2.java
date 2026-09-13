package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;

class RenderItem$2 implements Callable {
   RenderItem$2(RenderItem var1, ItemStack var2) {
      super();
      this.field_147925_b = var1;
      this.field_147926_a = var2;
   }

   public String call() {
      return String.valueOf(this.field_147926_a.func_77960_j());
   }
}
