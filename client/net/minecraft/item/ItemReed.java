package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemReed extends Item {
   private Block field_150935_a;

   public ItemReed(Block var1) {
      super();
      this.field_150935_a = var1;
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      Block var11 = var3.func_147439_a(var4, var5, var6);
      if (var11 == Blocks.field_150431_aC && (var3.func_72805_g(var4, var5, var6) & 7) < 1) {
         var7 = 1;
      } else if (var11 != Blocks.field_150395_bd && var11 != Blocks.field_150329_H && var11 != Blocks.field_150330_I) {
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
      }

      if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
         return false;
      } else if (var1.field_77994_a == 0) {
         return false;
      } else {
         if (var3.func_147472_a(this.field_150935_a, var4, var5, var6, false, var7, null, var1)) {
            int var12 = this.field_150935_a.func_149660_a(var3, var4, var5, var6, var7, var8, var9, var10, 0);
            if (var3.func_147465_d(var4, var5, var6, this.field_150935_a, var12, 3)) {
               if (var3.func_147439_a(var4, var5, var6) == this.field_150935_a) {
                  this.field_150935_a.func_149689_a(var3, var4, var5, var6, var2, var1);
                  this.field_150935_a.func_149714_e(var3, var4, var5, var6, var12);
               }

               var3.func_72908_a(
                  (double)((float)var4 + 0.5F),
                  (double)((float)var5 + 0.5F),
                  (double)((float)var6 + 0.5F),
                  this.field_150935_a.field_149762_H.func_150496_b(),
                  (this.field_150935_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F,
                  this.field_150935_a.field_149762_H.func_150494_d() * 0.8F
               );
               --var1.field_77994_a;
            }
         }

         return true;
      }
   }
}
