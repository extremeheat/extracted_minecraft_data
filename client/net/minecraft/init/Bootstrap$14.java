package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$14 extends BehaviorDefaultDispenseItem {
   Bootstrap$14() {
      super();
   }

   @Override
   protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      World var4 = var1.func_82618_k();
      int var5 = var1.func_82623_d() + var3.func_82601_c();
      int var6 = var1.func_82622_e() + var3.func_96559_d();
      int var7 = var1.func_82621_f() + var3.func_82599_e();
      EntityTNTPrimed var8 = new EntityTNTPrimed(var4, (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), (double)((float)var7 + 0.5F), null);
      var4.func_72838_d(var8);
      --var2.field_77994_a;
      return var2;
   }
}
