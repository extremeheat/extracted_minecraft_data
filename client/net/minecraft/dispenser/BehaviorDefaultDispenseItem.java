package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
   public BehaviorDefaultDispenseItem() {
      super();
   }

   public final ItemStack dispense(IBlockSource var1, ItemStack var2) {
      ItemStack var3 = this.func_82487_b(var1, var2);
      this.func_82485_a(var1);
      this.func_82489_a(var1, (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
      return var3;
   }

   protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
      IPosition var4 = BlockDispenser.func_149939_a(var1);
      ItemStack var5 = var2.func_77979_a(1);
      func_82486_a(var1.func_197524_h(), var5, 6, var3, var4);
      return var2;
   }

   public static void func_82486_a(World var0, ItemStack var1, int var2, EnumFacing var3, IPosition var4) {
      double var5 = var4.func_82615_a();
      double var7 = var4.func_82617_b();
      double var9 = var4.func_82616_c();
      if (var3.func_176740_k() == EnumFacing.Axis.Y) {
         var7 -= 0.125D;
      } else {
         var7 -= 0.15625D;
      }

      EntityItem var11 = new EntityItem(var0, var5, var7, var9, var1);
      double var12 = var0.field_73012_v.nextDouble() * 0.1D + 0.2D;
      var11.field_70159_w = (double)var3.func_82601_c() * var12;
      var11.field_70181_x = 0.20000000298023224D;
      var11.field_70179_y = (double)var3.func_82599_e() * var12;
      var11.field_70159_w += var0.field_73012_v.nextGaussian() * 0.007499999832361937D * (double)var2;
      var11.field_70181_x += var0.field_73012_v.nextGaussian() * 0.007499999832361937D * (double)var2;
      var11.field_70179_y += var0.field_73012_v.nextGaussian() * 0.007499999832361937D * (double)var2;
      var0.func_72838_d(var11);
   }

   protected void func_82485_a(IBlockSource var1) {
      var1.func_197524_h().func_175718_b(1000, var1.func_180699_d(), 0);
   }

   protected void func_82489_a(IBlockSource var1, EnumFacing var2) {
      var1.func_197524_h().func_175718_b(2000, var1.func_180699_d(), var2.func_176745_a());
   }
}
