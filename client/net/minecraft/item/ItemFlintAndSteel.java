package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFlintAndSteel extends Item {
   public ItemFlintAndSteel() {
      super();
      this.field_77777_bU = 1;
      this.func_77656_e(64);
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      var4 = var4.func_177972_a(var5);
      if (!var2.func_175151_a(var4, var5, var1)) {
         return false;
      } else {
         if (var3.func_180495_p(var4).func_177230_c().func_149688_o() == Material.field_151579_a) {
            var3.func_72908_a((double)var4.func_177958_n() + 0.5D, (double)var4.func_177956_o() + 0.5D, (double)var4.func_177952_p() + 0.5D, "fire.ignite", 1.0F, field_77697_d.nextFloat() * 0.4F + 0.8F);
            var3.func_175656_a(var4, Blocks.field_150480_ab.func_176223_P());
         }

         var1.func_77972_a(1, var2);
         return true;
      }
   }
}
