package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSign extends Item {
   public ItemSign() {
      super();
      this.field_77777_bU = 16;
      this.func_77637_a(CreativeTabs.field_78031_c);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var7 == 0) {
         return false;
      } else if (!var3.func_147439_a(var4, var5, var6).func_149688_o().func_76220_a()) {
         return false;
      } else {
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
         } else if (!Blocks.field_150472_an.func_149742_c(var3, var4, var5, var6)) {
            return false;
         } else if (var3.field_72995_K) {
            return true;
         } else {
            if (var7 == 1) {
               int var11 = MathHelper.func_76128_c((double)((var2.field_70177_z + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
               var3.func_147465_d(var4, var5, var6, Blocks.field_150472_an, var11, 3);
            } else {
               var3.func_147465_d(var4, var5, var6, Blocks.field_150444_as, var7, 3);
            }

            --var1.field_77994_a;
            TileEntitySign var12 = (TileEntitySign)var3.func_147438_o(var4, var5, var6);
            if (var12 != null) {
               var2.func_146100_a(var12);
            }

            return true;
         }
      }
   }
}
