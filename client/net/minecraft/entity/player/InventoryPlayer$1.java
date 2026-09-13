package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;

class InventoryPlayer$1 implements Callable {
   InventoryPlayer$1(InventoryPlayer var1, ItemStack var2) {
      super();
      this.field_96633_b = var1;
      this.field_96634_a = var2;
   }

   public String call() {
      return this.field_96634_a.func_82833_r();
   }
}
