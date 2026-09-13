package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored {
   public ItemLilyPad(Block var1) {
      super(var1, false);
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var4 = this.func_77621_a(var2, var3, true);
      if (var4 == null) {
         return var1;
      } else {
         if (var4.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
            int var5 = var4.field_72311_b;
            int var6 = var4.field_72312_c;
            int var7 = var4.field_72309_d;
            if (!var2.func_72962_a(var3, var5, var6, var7)) {
               return var1;
            }

            if (!var3.func_82247_a(var5, var6, var7, var4.field_72310_e, var1)) {
               return var1;
            }

            if (var2.func_147439_a(var5, var6, var7).func_149688_o() == Material.field_151586_h
               && var2.func_72805_g(var5, var6, var7) == 0
               && var2.func_147437_c(var5, var6 + 1, var7)) {
               var2.func_147449_b(var5, var6 + 1, var7, Blocks.field_150392_bi);
               if (!var3.field_71075_bZ.field_75098_d) {
                  --var1.field_77994_a;
               }
            }
         }

         return var1;
      }
   }

   @Override
   public int func_82790_a(ItemStack var1, int var2) {
      return Blocks.field_150392_bi.func_149741_i(var1.func_77960_j());
   }
}
