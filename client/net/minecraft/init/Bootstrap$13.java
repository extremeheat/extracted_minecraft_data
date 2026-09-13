package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$13 extends BehaviorDefaultDispenseItem {
   private boolean field_150838_b = true;

   Bootstrap$13() {
      super();
   }

   @Override
   protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      if (var2.func_77960_j() == 15) {
         EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
         World var4 = var1.func_82618_k();
         int var5 = var1.func_82623_d() + var3.func_82601_c();
         int var6 = var1.func_82622_e() + var3.func_96559_d();
         int var7 = var1.func_82621_f() + var3.func_82599_e();
         if (ItemDye.func_150919_a(var2, var4, var5, var6, var7)) {
            if (!var4.field_72995_K) {
               var4.func_72926_e(2005, var5, var6, var7, 0);
            }
         } else {
            this.field_150838_b = false;
         }

         return var2;
      } else {
         return super.func_82487_b(var1, var2);
      }
   }

   @Override
   protected void func_82485_a(IBlockSource var1) {
      if (this.field_150838_b) {
         var1.func_82618_k().func_72926_e(1000, var1.func_82623_d(), var1.func_82622_e(), var1.func_82621_f(), 0);
      } else {
         var1.func_82618_k().func_72926_e(1001, var1.func_82623_d(), var1.func_82622_e(), var1.func_82621_f(), 0);
      }
   }
}
