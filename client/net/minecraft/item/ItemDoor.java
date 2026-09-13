package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDoor extends Item {
   private Material field_77870_a;

   public ItemDoor(Material var1) {
      super();
      this.field_77870_a = var1;
      this.field_77777_bU = 1;
      this.func_77637_a(CreativeTabs.field_78028_d);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var7 != 1) {
         return false;
      } else {
         ++var5;
         Block var11;
         if (this.field_77870_a == Material.field_151575_d) {
            var11 = Blocks.field_150466_ao;
         } else {
            var11 = Blocks.field_150454_av;
         }

         if (!var2.func_82247_a(var4, var5, var6, var7, var1) || !var2.func_82247_a(var4, var5 + 1, var6, var7, var1)) {
            return false;
         } else if (!var11.func_149742_c(var3, var4, var5, var6)) {
            return false;
         } else {
            int var12 = MathHelper.func_76128_c((double)((var2.field_70177_z + 180.0F) * 4.0F / 360.0F) - 0.5) & 3;
            func_150924_a(var3, var4, var5, var6, var12, var11);
            --var1.field_77994_a;
            return true;
         }
      }
   }

   public static void func_150924_a(World var0, int var1, int var2, int var3, int var4, Block var5) {
      byte var6 = 0;
      byte var7 = 0;
      if (var4 == 0) {
         var7 = 1;
      }

      if (var4 == 1) {
         var6 = -1;
      }

      if (var4 == 2) {
         var7 = -1;
      }

      if (var4 == 3) {
         var6 = 1;
      }

      int var8 = (var0.func_147439_a(var1 - var6, var2, var3 - var7).func_149721_r() ? 1 : 0)
         + (var0.func_147439_a(var1 - var6, var2 + 1, var3 - var7).func_149721_r() ? 1 : 0);
      int var9 = (var0.func_147439_a(var1 + var6, var2, var3 + var7).func_149721_r() ? 1 : 0)
         + (var0.func_147439_a(var1 + var6, var2 + 1, var3 + var7).func_149721_r() ? 1 : 0);
      boolean var10 = var0.func_147439_a(var1 - var6, var2, var3 - var7) == var5 || var0.func_147439_a(var1 - var6, var2 + 1, var3 - var7) == var5;
      boolean var11 = var0.func_147439_a(var1 + var6, var2, var3 + var7) == var5 || var0.func_147439_a(var1 + var6, var2 + 1, var3 + var7) == var5;
      boolean var12 = false;
      if (var10 && !var11) {
         var12 = true;
      } else if (var9 > var8) {
         var12 = true;
      }

      var0.func_147465_d(var1, var2, var3, var5, var4, 2);
      var0.func_147465_d(var1, var2 + 1, var3, var5, 8 | (var12 ? 1 : 0), 2);
      var0.func_147459_d(var1, var2, var3, var5);
      var0.func_147459_d(var1, var2 + 1, var3, var5);
   }
}
