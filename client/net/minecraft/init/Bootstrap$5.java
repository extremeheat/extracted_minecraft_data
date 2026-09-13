package net.minecraft.init;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

final class Bootstrap$5 implements IBehaviorDispenseItem {
   private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();

   Bootstrap$5() {
      super();
   }

   @Override
   public ItemStack func_82482_a(IBlockSource var1, ItemStack var2) {
      return ItemPotion.func_77831_g(var2.func_77960_j())
         ? new Bootstrap$5$1(this, var2).func_82482_a(var1, var2)
         : this.field_150843_b.func_82482_a(var1, var2);
   }
}
