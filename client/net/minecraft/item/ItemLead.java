package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemLead extends Item {
   public ItemLead() {
      super();
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      Block var11 = var3.func_147439_a(var4, var5, var6);
      if (var11.func_149645_b() == 11) {
         if (var3.field_72995_K) {
            return true;
         } else {
            func_150909_a(var2, var3, var4, var5, var6);
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean func_150909_a(EntityPlayer var0, World var1, int var2, int var3, int var4) {
      EntityLeashKnot var5 = EntityLeashKnot.func_110130_b(var1, var2, var3, var4);
      boolean var6 = false;
      double var7 = 7.0;
      List var9 = var1.func_72872_a(
         EntityLiving.class,
         AxisAlignedBB.func_72330_a(
            (double)var2 - var7, (double)var3 - var7, (double)var4 - var7, (double)var2 + var7, (double)var3 + var7, (double)var4 + var7
         )
      );
      if (var9 != null) {
         for(EntityLiving var11 : var9) {
            if (var11.func_110167_bD() && var11.func_110166_bE() == var0) {
               if (var5 == null) {
                  var5 = EntityLeashKnot.func_110129_a(var1, var2, var3, var4);
               }

               var11.func_110162_b(var5, true);
               var6 = true;
            }
         }
      }

      return var6;
   }
}
