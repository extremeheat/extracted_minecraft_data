package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class Bootstrap$6 extends BehaviorDefaultDispenseItem {
   Bootstrap$6() {
      super();
   }

   @Override
   public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      double var4 = var1.func_82615_a() + (double)var3.func_82601_c();
      double var6 = (double)((float)var1.func_82622_e() + 0.2F);
      double var8 = var1.func_82616_c() + (double)var3.func_82599_e();
      Entity var10 = ItemMonsterPlacer.func_77840_a(var1.func_82618_k(), var2.func_77960_j(), var4, var6, var8);
      if (var10 instanceof EntityLivingBase && var2.func_82837_s()) {
         ((EntityLiving)var10).func_94058_c(var2.func_82833_r());
      }

      var2.func_77979_a(1);
      return var2;
   }
}
