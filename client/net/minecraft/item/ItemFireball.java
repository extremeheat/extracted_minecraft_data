package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemFireball extends Item {
   public ItemFireball() {
      super();
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var3.field_72995_K) {
         return true;
      } else {
         if (var7 == 0) {
            --var5;
         }

         if (var7 == 1) {
            ++var5;
         }

         if (var7 == 2) {
            --var6;
         }

         if (var7 == 3) {
            ++var6;
         }

         if (var7 == 4) {
            --var4;
         }

         if (var7 == 5) {
            ++var4;
         }

         if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
            return false;
         } else {
            if (var3.func_147439_a(var4, var5, var6).func_149688_o() == Material.field_151579_a) {
               var3.func_72908_a((double)var4 + 0.5, (double)var5 + 0.5, (double)var6 + 0.5, "fire.ignite", 1.0F, field_77697_d.nextFloat() * 0.4F + 0.8F);
               var3.func_147449_b(var4, var5, var6, Blocks.field_150480_ab);
            }

            if (!var2.field_71075_bZ.field_75098_d) {
               --var1.field_77994_a;
            }

            return true;
         }
      }
   }
}
