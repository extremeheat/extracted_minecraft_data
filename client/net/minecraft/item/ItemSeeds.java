package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemSeeds extends Item {
   private Block field_150925_a;
   private Block field_77838_b;

   public ItemSeeds(Block var1, Block var2) {
      super();
      this.field_150925_a = var1;
      this.field_77838_b = var2;
      this.func_77637_a(CreativeTabs.field_78035_l);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var7 != 1) {
         return false;
      } else if (!var2.func_82247_a(var4, var5, var6, var7, var1) || !var2.func_82247_a(var4, var5 + 1, var6, var7, var1)) {
         return false;
      } else if (var3.func_147439_a(var4, var5, var6) == this.field_77838_b && var3.func_147437_c(var4, var5 + 1, var6)) {
         var3.func_147449_b(var4, var5 + 1, var6, this.field_150925_a);
         --var1.field_77994_a;
         return true;
      } else {
         return false;
      }
   }
}
