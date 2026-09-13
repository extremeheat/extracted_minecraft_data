package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

public class BlockMushroom extends BlockBush implements IGrowable {
   protected BlockMushroom() {
      super();
      float var1 = 0.2F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var1 * 2.0F, 0.5F + var1);
      this.func_149675_a(true);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (var5.nextInt(25) == 0) {
         byte var6 = 4;
         int var7 = 5;

         for(int var8 = var2 - var6; var8 <= var2 + var6; ++var8) {
            for(int var9 = var4 - var6; var9 <= var4 + var6; ++var9) {
               for(int var10 = var3 - 1; var10 <= var3 + 1; ++var10) {
                  if (var1.func_147439_a(var8, var10, var9) == this) {
                     if (--var7 <= 0) {
                        return;
                     }
                  }
               }
            }
         }

         int var12 = var2 + var5.nextInt(3) - 1;
         int var13 = var3 + var5.nextInt(2) - var5.nextInt(2);
         int var14 = var4 + var5.nextInt(3) - 1;

         for(int var11 = 0; var11 < 4; ++var11) {
            if (var1.func_147437_c(var12, var13, var14) && this.func_149718_j(var1, var12, var13, var14)) {
               var2 = var12;
               var3 = var13;
               var4 = var14;
            }

            var12 = var2 + var5.nextInt(3) - 1;
            var13 = var3 + var5.nextInt(2) - var5.nextInt(2);
            var14 = var4 + var5.nextInt(3) - 1;
         }

         if (var1.func_147437_c(var12, var13, var14) && this.func_149718_j(var1, var12, var13, var14)) {
            var1.func_147465_d(var12, var13, var14, this, 0, 2);
         }
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return super.func_149742_c(var1, var2, var3, var4) && this.func_149718_j(var1, var2, var3, var4);
   }

   @Override
   protected boolean func_149854_a(Block var1) {
      return var1.func_149730_j();
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var3 < 256) {
         Block var5 = var1.func_147439_a(var2, var3 - 1, var4);
         return var5 == Blocks.field_150391_bh
            || var5 == Blocks.field_150346_d && var1.func_72805_g(var2, var3 - 1, var4) == 2
            || var1.func_72883_k(var2, var3, var4) < 13 && this.func_149854_a(var5);
      } else {
         return false;
      }
   }

   public boolean func_149884_c(World var1, int var2, int var3, int var4, Random var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      var1.func_147468_f(var2, var3, var4);
      WorldGenBigMushroom var7 = null;
      if (this == Blocks.field_150338_P) {
         var7 = new WorldGenBigMushroom(0);
      } else if (this == Blocks.field_150337_Q) {
         var7 = new WorldGenBigMushroom(1);
      }

      if (var7 != null && var7.func_76484_a(var1, var5, var2, var3, var4)) {
         return true;
      } else {
         var1.func_147465_d(var2, var3, var4, this, var6, 3);
         return false;
      }
   }

   @Override
   public boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5) {
      return true;
   }

   @Override
   public boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5) {
      return (double)var2.nextFloat() < 0.4;
   }

   @Override
   public void func_149853_b(World var1, Random var2, int var3, int var4, int var5) {
      this.func_149884_c(var1, var3, var4, var5, var2);
   }
}
