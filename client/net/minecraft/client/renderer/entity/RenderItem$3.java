package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;

class RenderItem$3 implements Callable {
   RenderItem$3(RenderItem var1, ItemStack var2) {
      super();
      this.field_147934_b = var1;
      this.field_147935_a = var2;
   }

   public String call() {
      return String.valueOf(this.field_147935_a.func_77978_p());
   }
}
