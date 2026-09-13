package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$9 extends BehaviorDefaultDispenseItem {
   private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();

   Bootstrap$9() {
      super();
   }

   @Override
   public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      World var4 = var1.func_82618_k();
      double var5 = var1.func_82615_a() + (double)((float)var3.func_82601_c() * 1.125F);
      double var7 = var1.func_82617_b() + (double)((float)var3.func_96559_d() * 1.125F);
      double var9 = var1.func_82616_c() + (double)((float)var3.func_82599_e() * 1.125F);
      int var11 = var1.func_82623_d() + var3.func_82601_c();
      int var12 = var1.func_82622_e() + var3.func_96559_d();
      int var13 = var1.func_82621_f() + var3.func_82599_e();
      Material var14 = var4.func_147439_a(var11, var12, var13).func_149688_o();
      double var15;
      if (Material.field_151586_h.equals(var14)) {
         var15 = 1.0;
      } else {
         if (!Material.field_151579_a.equals(var14) || !Material.field_151586_h.equals(var4.func_147439_a(var11, var12 - 1, var13).func_149688_o())) {
            return this.field_150842_b.func_82482_a(var1, var2);
         }

         var15 = 0.0;
      }

      EntityBoat var17 = new EntityBoat(var4, var5, var7 + var15, var9);
      var4.func_72838_d(var17);
      var2.func_77979_a(1);
      return var2;
   }

   @Override
   protected void func_82485_a(IBlockSource var1) {
      var1.func_82618_k().func_72926_e(1000, var1.func_82623_d(), var1.func_82622_e(), var1.func_82621_f(), 0);
   }
}
