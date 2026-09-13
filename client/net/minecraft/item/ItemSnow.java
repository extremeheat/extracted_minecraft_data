package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlockWithMetadata {
   public ItemSnow(Block var1, Block var2) {
      super(var1, var2);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var1.field_77994_a == 0) {
         return false;
      } else if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
         return false;
      } else {
         Block var11 = var3.func_147439_a(var4, var5, var6);
         if (var11 == Blocks.field_150431_aC) {
            int var12 = var3.func_72805_g(var4, var5, var6);
            int var13 = var12 & 7;
            if (var13 <= 6
               && var3.func_72855_b(this.field_150939_a.func_149668_a(var3, var4, var5, var6))
               && var3.func_72921_c(var4, var5, var6, var13 + 1 | var12 & -8, 2)) {
               var3.func_72908_a(
                  (double)((float)var4 + 0.5F),
                  (double)((float)var5 + 0.5F),
                  (double)((float)var6 + 0.5F),
                  this.field_150939_a.field_149762_H.func_150496_b(),
                  (this.field_150939_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F,
                  this.field_150939_a.field_149762_H.func_150494_d() * 0.8F
               );
               --var1.field_77994_a;
               return true;
            }
         }

         return super.func_77648_a(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }
}
