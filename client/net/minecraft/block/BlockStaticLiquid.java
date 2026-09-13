package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid {
   protected BlockStaticLiquid(Material var1) {
      super(var1);
      this.func_149675_a(false);
      if (var1 == Material.field_151587_i) {
         this.func_149675_a(true);
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      super.func_149695_a(var1, var2, var3, var4, var5);
      if (var1.func_147439_a(var2, var3, var4) == this) {
         this.func_149818_n(var1, var2, var3, var4);
      }
   }

   private void func_149818_n(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      var1.func_147465_d(var2, var3, var4, Block.func_149729_e(Block.func_149682_b(this) - 1), var5, 2);
      var1.func_147464_a(var2, var3, var4, Block.func_149729_e(Block.func_149682_b(this) - 1), this.func_149738_a(var1));
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (this.field_149764_J == Material.field_151587_i) {
         int var6 = var5.nextInt(3);

         for(int var7 = 0; var7 < var6; ++var7) {
            var2 += var5.nextInt(3) - 1;
            ++var3;
            var4 += var5.nextInt(3) - 1;
            Block var8 = var1.func_147439_a(var2, var3, var4);
            if (var8.field_149764_J == Material.field_151579_a) {
               if (this.func_149817_o(var1, var2 - 1, var3, var4)
                  || this.func_149817_o(var1, var2 + 1, var3, var4)
                  || this.func_149817_o(var1, var2, var3, var4 - 1)
                  || this.func_149817_o(var1, var2, var3, var4 + 1)
                  || this.func_149817_o(var1, var2, var3 - 1, var4)
                  || this.func_149817_o(var1, var2, var3 + 1, var4)) {
                  var1.func_147449_b(var2, var3, var4, Blocks.field_150480_ab);
                  return;
               }
            } else if (var8.field_149764_J.func_76230_c()) {
               return;
            }
         }

         if (var6 == 0) {
            int var12 = var2;
            int var13 = var4;

            for(int var9 = 0; var9 < 3; ++var9) {
               var2 = var12 + var5.nextInt(3) - 1;
               var4 = var13 + var5.nextInt(3) - 1;
               if (var1.func_147437_c(var2, var3 + 1, var4) && this.func_149817_o(var1, var2, var3, var4)) {
                  var1.func_147449_b(var2, var3 + 1, var4, Blocks.field_150480_ab);
               }
            }
         }
      }
   }

   private boolean func_149817_o(World var1, int var2, int var3, int var4) {
      return var1.func_147439_a(var2, var3, var4).func_149688_o().func_76217_h();
   }
}
