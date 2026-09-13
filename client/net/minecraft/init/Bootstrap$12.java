package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$12 extends BehaviorDefaultDispenseItem {
   private boolean field_150839_b = true;

   Bootstrap$12() {
      super();
   }

   @Override
   protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      World var4 = var1.func_82618_k();
      int var5 = var1.func_82623_d() + var3.func_82601_c();
      int var6 = var1.func_82622_e() + var3.func_96559_d();
      int var7 = var1.func_82621_f() + var3.func_82599_e();
      if (var4.func_147437_c(var5, var6, var7)) {
         var4.func_147449_b(var5, var6, var7, Blocks.field_150480_ab);
         if (var2.func_96631_a(1, var4.field_73012_v)) {
            var2.field_77994_a = 0;
         }
      } else if (var4.func_147439_a(var5, var6, var7) == Blocks.field_150335_W) {
         Blocks.field_150335_W.func_149664_b(var4, var5, var6, var7, 1);
         var4.func_147468_f(var5, var6, var7);
      } else {
         this.field_150839_b = false;
      }

      return var2;
   }

   @Override
   protected void func_82485_a(IBlockSource var1) {
      if (this.field_150839_b) {
         var1.func_82618_k().func_72926_e(1000, var1.func_82623_d(), var1.func_82622_e(), var1.func_82621_f(), 0);
      } else {
         var1.func_82618_k().func_72926_e(1001, var1.func_82623_d(), var1.func_82622_e(), var1.func_82621_f(), 0);
      }
   }
}
