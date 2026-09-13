package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {
   int field_149815_a;
   boolean[] field_149814_b = new boolean[4];
   int[] field_149816_M = new int[4];

   protected BlockDynamicLiquid(Material var1) {
      super(var1);
   }

   private void func_149811_n(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      var1.func_147465_d(var2, var3, var4, Block.func_149729_e(Block.func_149682_b(this) + 1), var5, 2);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      int var6 = this.func_149804_e(var1, var2, var3, var4);
      byte var7 = 1;
      if (this.field_149764_J == Material.field_151587_i && !var1.field_73011_w.field_76575_d) {
         var7 = 2;
      }

      boolean var8 = true;
      int var9 = this.func_149738_a(var1);
      if (var6 > 0) {
         int var10 = -100;
         this.field_149815_a = 0;
         var10 = this.func_149810_a(var1, var2 - 1, var3, var4, var10);
         var10 = this.func_149810_a(var1, var2 + 1, var3, var4, var10);
         var10 = this.func_149810_a(var1, var2, var3, var4 - 1, var10);
         var10 = this.func_149810_a(var1, var2, var3, var4 + 1, var10);
         int var11 = var10 + var7;
         if (var11 >= 8 || var10 < 0) {
            var11 = -1;
         }

         if (this.func_149804_e(var1, var2, var3 + 1, var4) >= 0) {
            int var12 = this.func_149804_e(var1, var2, var3 + 1, var4);
            if (var12 >= 8) {
               var11 = var12;
            } else {
               var11 = var12 + 8;
            }
         }

         if (this.field_149815_a >= 2 && this.field_149764_J == Material.field_151586_h) {
            if (var1.func_147439_a(var2, var3 - 1, var4).func_149688_o().func_76220_a()) {
               var11 = 0;
            } else if (var1.func_147439_a(var2, var3 - 1, var4).func_149688_o() == this.field_149764_J && var1.func_72805_g(var2, var3 - 1, var4) == 0) {
               var11 = 0;
            }
         }

         if (this.field_149764_J == Material.field_151587_i && var6 < 8 && var11 < 8 && var11 > var6 && var5.nextInt(4) != 0) {
            var9 *= 4;
         }

         if (var11 == var6) {
            if (var8) {
               this.func_149811_n(var1, var2, var3, var4);
            }
         } else {
            var6 = var11;
            if (var11 < 0) {
               var1.func_147468_f(var2, var3, var4);
            } else {
               var1.func_72921_c(var2, var3, var4, var11, 2);
               var1.func_147464_a(var2, var3, var4, this, var9);
               var1.func_147459_d(var2, var3, var4, this);
            }
         }
      } else {
         this.func_149811_n(var1, var2, var3, var4);
      }

      if (this.func_149809_q(var1, var2, var3 - 1, var4)) {
         if (this.field_149764_J == Material.field_151587_i && var1.func_147439_a(var2, var3 - 1, var4).func_149688_o() == Material.field_151586_h) {
            var1.func_147449_b(var2, var3 - 1, var4, Blocks.field_150348_b);
            this.func_149799_m(var1, var2, var3 - 1, var4);
            return;
         }

         if (var6 >= 8) {
            this.func_149813_h(var1, var2, var3 - 1, var4, var6);
         } else {
            this.func_149813_h(var1, var2, var3 - 1, var4, var6 + 8);
         }
      } else if (var6 >= 0 && (var6 == 0 || this.func_149807_p(var1, var2, var3 - 1, var4))) {
         boolean[] var17 = this.func_149808_o(var1, var2, var3, var4);
         int var18 = var6 + var7;
         if (var6 >= 8) {
            var18 = 1;
         }

         if (var18 >= 8) {
            return;
         }

         if (var17[0]) {
            this.func_149813_h(var1, var2 - 1, var3, var4, var18);
         }

         if (var17[1]) {
            this.func_149813_h(var1, var2 + 1, var3, var4, var18);
         }

         if (var17[2]) {
            this.func_149813_h(var1, var2, var3, var4 - 1, var18);
         }

         if (var17[3]) {
            this.func_149813_h(var1, var2, var3, var4 + 1, var18);
         }
      }
   }

   private void func_149813_h(World var1, int var2, int var3, int var4, int var5) {
      if (this.func_149809_q(var1, var2, var3, var4)) {
         Block var6 = var1.func_147439_a(var2, var3, var4);
         if (this.field_149764_J == Material.field_151587_i) {
            this.func_149799_m(var1, var2, var3, var4);
         } else {
            var6.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         }

         var1.func_147465_d(var2, var3, var4, this, var5, 3);
      }
   }

   private int func_149812_c(World var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = 1000;

      for(int var8 = 0; var8 < 4; ++var8) {
         if ((var8 != 0 || var6 != 1) && (var8 != 1 || var6 != 0) && (var8 != 2 || var6 != 3) && (var8 != 3 || var6 != 2)) {
            int var9 = var2;
            int var11 = var4;
            if (var8 == 0) {
               var9 = var2 - 1;
            }

            if (var8 == 1) {
               ++var9;
            }

            if (var8 == 2) {
               var11 = var4 - 1;
            }

            if (var8 == 3) {
               ++var11;
            }

            if (!this.func_149807_p(var1, var9, var3, var11)
               && (var1.func_147439_a(var9, var3, var11).func_149688_o() != this.field_149764_J || var1.func_72805_g(var9, var3, var11) != 0)) {
               if (!this.func_149807_p(var1, var9, var3 - 1, var11)) {
                  return var5;
               }

               if (var5 < 4) {
                  int var12 = this.func_149812_c(var1, var9, var3, var11, var5 + 1, var8);
                  if (var12 < var7) {
                     var7 = var12;
                  }
               }
            }
         }
      }

      return var7;
   }

   private boolean[] func_149808_o(World var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < 4; ++var5) {
         this.field_149816_M[var5] = 1000;
         int var6 = var2;
         int var8 = var4;
         if (var5 == 0) {
            var6 = var2 - 1;
         }

         if (var5 == 1) {
            ++var6;
         }

         if (var5 == 2) {
            var8 = var4 - 1;
         }

         if (var5 == 3) {
            ++var8;
         }

         if (!this.func_149807_p(var1, var6, var3, var8)
            && (var1.func_147439_a(var6, var3, var8).func_149688_o() != this.field_149764_J || var1.func_72805_g(var6, var3, var8) != 0)) {
            if (this.func_149807_p(var1, var6, var3 - 1, var8)) {
               this.field_149816_M[var5] = this.func_149812_c(var1, var6, var3, var8, 1, var5);
            } else {
               this.field_149816_M[var5] = 0;
            }
         }
      }

      int var9 = this.field_149816_M[0];

      for(int var10 = 1; var10 < 4; ++var10) {
         if (this.field_149816_M[var10] < var9) {
            var9 = this.field_149816_M[var10];
         }
      }

      for(int var11 = 0; var11 < 4; ++var11) {
         this.field_149814_b[var11] = this.field_149816_M[var11] == var9;
      }

      return this.field_149814_b;
   }

   private boolean func_149807_p(World var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      if (var5 == Blocks.field_150466_ao
         || var5 == Blocks.field_150454_av
         || var5 == Blocks.field_150472_an
         || var5 == Blocks.field_150468_ap
         || var5 == Blocks.field_150436_aH) {
         return true;
      } else {
         return var5.field_149764_J == Material.field_151567_E ? true : var5.field_149764_J.func_76230_c();
      }
   }

   protected int func_149810_a(World var1, int var2, int var3, int var4, int var5) {
      int var6 = this.func_149804_e(var1, var2, var3, var4);
      if (var6 < 0) {
         return var5;
      } else {
         if (var6 == 0) {
            ++this.field_149815_a;
         }

         if (var6 >= 8) {
            var6 = 0;
         }

         return var5 >= 0 && var6 >= var5 ? var5 : var6;
      }
   }

   private boolean func_149809_q(World var1, int var2, int var3, int var4) {
      Material var5 = var1.func_147439_a(var2, var3, var4).func_149688_o();
      if (var5 == this.field_149764_J) {
         return false;
      } else if (var5 == Material.field_151587_i) {
         return false;
      } else {
         return !this.func_149807_p(var1, var2, var3, var4);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      if (var1.func_147439_a(var2, var3, var4) == this) {
         var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
      }
   }

   @Override
   public boolean func_149698_L() {
      return true;
   }
}
