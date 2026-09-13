package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemSeedFood extends ItemFood {
   private Block field_150908_b;
   private Block field_82809_c;

   public ItemSeedFood(int var1, float var2, Block var3, Block var4) {
      super(var1, var2, false);
      this.field_150908_b = var3;
      this.field_82809_c = var4;
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var7 != 1) {
         return false;
      } else if (!var2.func_82247_a(var4, var5, var6, var7, var1) || !var2.func_82247_a(var4, var5 + 1, var6, var7, var1)) {
         return false;
      } else if (var3.func_147439_a(var4, var5, var6) == this.field_82809_c && var3.func_147437_c(var4, var5 + 1, var6)) {
         var3.func_147449_b(var4, var5 + 1, var6, this.field_150908_b);
         --var1.field_77994_a;
         return true;
      } else {
         return false;
      }
   }
}
