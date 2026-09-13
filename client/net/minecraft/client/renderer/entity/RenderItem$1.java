package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;

class RenderItem$1 implements Callable {
   RenderItem$1(RenderItem var1, ItemStack var2) {
      super();
      this.field_147928_b = var1;
      this.field_147929_a = var2;
   }

   public String call() {
      return String.valueOf(this.field_147929_a.func_77973_b());
   }
}
