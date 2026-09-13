package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemRedstone extends Item {
   public ItemRedstone() {
      super();
      this.func_77637_a(CreativeTabs.field_78028_d);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var3.func_147439_a(var4, var5, var6) != Blocks.field_150431_aC) {
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

         if (!var3.func_147437_c(var4, var5, var6)) {
            return false;
         }
      }

      if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
         return false;
      } else {
         if (Blocks.field_150488_af.func_149742_c(var3, var4, var5, var6)) {
            --var1.field_77994_a;
            var3.func_147449_b(var4, var5, var6, Blocks.field_150488_af);
         }

         return true;
      }
   }
}
