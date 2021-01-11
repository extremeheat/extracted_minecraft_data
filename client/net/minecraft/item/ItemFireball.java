package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFireball extends Item {
   public ItemFireball() {
      super();
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var3.field_72995_K) {
         return true;
      } else {
         var4 = var4.func_177972_a(var5);
         if (!var2.func_175151_a(var4, var5, var1)) {
            return false;
         } else {
            if (var3.func_180495_p(var4).func_177230_c().func_149688_o() == Material.field_151579_a) {
               var3.func_72908_a((double)var4.func_177958_n() + 0.5D, (double)var4.func_177956_o() + 0.5D, (double)var4.func_177952_p() + 0.5D, "item.fireCharge.use", 1.0F, (field_77697_d.nextFloat() - field_77697_d.nextFloat()) * 0.2F + 1.0F);
               var3.func_175656_a(var4, Blocks.field_150480_ab.func_176223_P());
            }

            if (!var2.field_71075_bZ.field_75098_d) {
               --var1.field_77994_a;
            }

            return true;
         }
      }
   }
}
