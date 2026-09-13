package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemHoe extends Item {
   protected Item$ToolMaterial field_77843_a;

   public ItemHoe(Item$ToolMaterial var1) {
      super();
      this.field_77843_a = var1;
      this.field_77777_bU = 1;
      this.func_77656_e(var1.func_77997_a());
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
         return false;
      } else {
         Block var11 = var3.func_147439_a(var4, var5, var6);
         if (var7 != 0
            && var3.func_147439_a(var4, var5 + 1, var6).func_149688_o() == Material.field_151579_a
            && (var11 == Blocks.field_150349_c || var11 == Blocks.field_150346_d)) {
            Block var12 = Blocks.field_150458_ak;
            var3.func_72908_a(
               (double)((float)var4 + 0.5F),
               (double)((float)var5 + 0.5F),
               (double)((float)var6 + 0.5F),
               var12.field_149762_H.func_150498_e(),
               (var12.field_149762_H.func_150497_c() + 1.0F) / 2.0F,
               var12.field_149762_H.func_150494_d() * 0.8F
            );
            if (var3.field_72995_K) {
               return true;
            } else {
               var3.func_147449_b(var4, var5, var6, var12);
               var1.func_77972_a(1, var2);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean func_77662_d() {
      return true;
   }

   public String func_77842_f() {
      return this.field_77843_a.toString();
   }
}
