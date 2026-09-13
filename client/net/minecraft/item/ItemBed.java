package net.minecraft.item;

import net.minecraft.block.BlockBed;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {
   public ItemBed() {
      super();
      this.func_77637_a(CreativeTabs.field_78031_c);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var3.field_72995_K) {
         return true;
      } else if (var7 != 1) {
         return false;
      } else {
         ++var5;
         BlockBed var11 = (BlockBed)Blocks.field_150324_C;
         int var12 = MathHelper.func_76128_c((double)(var2.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
         byte var13 = 0;
         byte var14 = 0;
         if (var12 == 0) {
            var14 = 1;
         }

         if (var12 == 1) {
            var13 = -1;
         }

         if (var12 == 2) {
            var14 = -1;
         }

         if (var12 == 3) {
            var13 = 1;
         }

         if (!var2.func_82247_a(var4, var5, var6, var7, var1) || !var2.func_82247_a(var4 + var13, var5, var6 + var14, var7, var1)) {
            return false;
         } else if (var3.func_147437_c(var4, var5, var6)
            && var3.func_147437_c(var4 + var13, var5, var6 + var14)
            && World.func_147466_a(var3, var4, var5 - 1, var6)
            && World.func_147466_a(var3, var4 + var13, var5 - 1, var6 + var14)) {
            var3.func_147465_d(var4, var5, var6, var11, var12, 3);
            if (var3.func_147439_a(var4, var5, var6) == var11) {
               var3.func_147465_d(var4 + var13, var5, var6 + var14, var11, var12 + 8, 3);
            }

            --var1.field_77994_a;
            return true;
         } else {
            return false;
         }
      }
   }
}
