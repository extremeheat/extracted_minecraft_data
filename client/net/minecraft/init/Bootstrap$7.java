package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class Bootstrap$7 extends BehaviorDefaultDispenseItem {
   Bootstrap$7() {
      super();
   }

   @Override
   public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      double var4 = var1.func_82615_a() + (double)var3.func_82601_c();
      double var6 = (double)((float)var1.func_82622_e() + 0.2F);
      double var8 = var1.func_82616_c() + (double)var3.func_82599_e();
      EntityFireworkRocket var10 = new EntityFireworkRocket(var1.func_82618_k(), var4, var6, var8, var2);
      var1.func_82618_k().func_72838_d(var10);
      var2.func_77979_a(1);
      return var2;
   }

   @Override
   protected void func_82485_a(IBlockSource var1) {
      var1.func_82618_k().func_72926_e(1002, var1.func_82623_d(), var1.func_82622_e(), var1.func_82621_f(), 0);
   }
}
