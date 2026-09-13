package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class Bootstrap$10 extends BehaviorDefaultDispenseItem {
   private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();

   Bootstrap$10() {
      super();
   }

   @Override
   public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      ItemBucket var3 = (ItemBucket)var2.func_77973_b();
      int var4 = var1.func_82623_d();
      int var5 = var1.func_82622_e();
      int var6 = var1.func_82621_f();
      EnumFacing var7 = BlockDispenser.func_149937_b(var1.func_82620_h());
      if (var3.func_77875_a(var1.func_82618_k(), var4 + var7.func_82601_c(), var5 + var7.func_96559_d(), var6 + var7.func_82599_e())) {
         var2.func_150996_a(Items.field_151133_ar);
         var2.field_77994_a = 1;
         return var2;
      } else {
         return this.field_150841_b.func_82482_a(var1, var2);
      }
   }
}
