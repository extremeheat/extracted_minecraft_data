package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemRedstone extends Item {
   public ItemRedstone() {
      super();
      this.func_77637_a(CreativeTabs.field_78028_d);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      boolean var9 = var3.func_180495_p(var4).func_177230_c().func_176200_f(var3, var4);
      BlockPos var10 = var9 ? var4 : var4.func_177972_a(var5);
      if (!var2.func_175151_a(var10, var5, var1)) {
         return false;
      } else {
         Block var11 = var3.func_180495_p(var10).func_177230_c();
         if (!var3.func_175716_a(var11, var10, false, var5, (Entity)null, var1)) {
            return false;
         } else if (Blocks.field_150488_af.func_176196_c(var3, var10)) {
            --var1.field_77994_a;
            var3.func_175656_a(var10, Blocks.field_150488_af.func_176223_P());
            return true;
         } else {
            return false;
         }
      }
   }
}
