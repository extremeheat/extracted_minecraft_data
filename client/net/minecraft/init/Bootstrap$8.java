package net.minecraft.init;

import java.util.Random;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$8 extends BehaviorDefaultDispenseItem {
   Bootstrap$8() {
      super();
   }

   @Override
   public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      IPosition var4 = BlockDispenser.func_149939_a(var1);
      double var5 = var4.func_82615_a() + (double)((float)var3.func_82601_c() * 0.3F);
      double var7 = var4.func_82617_b() + (double)((float)var3.func_82601_c() * 0.3F);
      double var9 = var4.func_82616_c() + (double)((float)var3.func_82599_e() * 0.3F);
      World var11 = var1.func_82618_k();
      Random var12 = var11.field_73012_v;
      double var13 = var12.nextGaussian() * 0.05 + (double)var3.func_82601_c();
      double var15 = var12.nextGaussian() * 0.05 + (double)var3.func_96559_d();
      double var17 = var12.nextGaussian() * 0.05 + (double)var3.func_82599_e();
      var11.func_72838_d(new EntitySmallFireball(var11, var5, var7, var9, var13, var15, var17));
      var2.func_77979_a(1);
      return var2;
   }

   @Override
   protected void func_82485_a(IBlockSource var1) {
      var1.func_82618_k().func_72926_e(1009, var1.func_82623_d(), var1.func_82622_e(), var1.func_82621_f(), 0);
   }
}
