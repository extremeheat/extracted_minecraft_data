package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$11 extends BehaviorDefaultDispenseItem {
   private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();

   Bootstrap$11() {
      super();
   }

   @Override
   public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      World var4 = var1.func_82618_k();
      int var5 = var1.func_82623_d() + var3.func_82601_c();
      int var6 = var1.func_82622_e() + var3.func_96559_d();
      int var7 = var1.func_82621_f() + var3.func_82599_e();
      Material var8 = var4.func_147439_a(var5, var6, var7).func_149688_o();
      int var9 = var4.func_72805_g(var5, var6, var7);
      Item var10;
      if (Material.field_151586_h.equals(var8) && var9 == 0) {
         var10 = Items.field_151131_as;
      } else {
         if (!Material.field_151587_i.equals(var8) || var9 != 0) {
            return super.func_82487_b(var1, var2);
         }

         var10 = Items.field_151129_at;
      }

      var4.func_147468_f(var5, var6, var7);
      if (--var2.field_77994_a == 0) {
         var2.func_150996_a(var10);
         var2.field_77994_a = 1;
      } else if (((TileEntityDispenser)var1.func_150835_j()).func_146019_a(new ItemStack(var10)) < 0) {
         this.field_150840_b.func_82482_a(var1, new ItemStack(var10));
      }

      return var2;
   }
}
