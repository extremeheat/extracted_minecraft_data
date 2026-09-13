package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {
   protected BlockTorch() {
      super(Material.field_151594_q);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 2;
   }

   private boolean func_150107_m(World var1, int var2, int var3, int var4) {
      if (World.func_147466_a(var1, var2, var3, var4)) {
         return true;
      } else {
         Block var5 = var1.func_147439_a(var2, var3, var4);
         return var5 == Blocks.field_150422_aJ || var5 == Blocks.field_150386_bk || var5 == Blocks.field_150359_w || var5 == Blocks.field_150463_bK;
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      if (var1.func_147445_c(var2 - 1, var3, var4, true)) {
         return true;
      } else if (var1.func_147445_c(var2 + 1, var3, var4, true)) {
         return true;
      } else if (var1.func_147445_c(var2, var3, var4 - 1, true)) {
         return true;
      } else if (var1.func_147445_c(var2, var3, var4 + 1, true)) {
         return true;
      } else {
         return this.func_150107_m(var1, var2, var3 - 1, var4);
      }
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      int var10 = var9;
      if (var5 == 1 && this.func_150107_m(var1, var2, var3 - 1, var4)) {
         var10 = 5;
      }

      if (var5 == 2 && var1.func_147445_c(var2, var3, var4 + 1, true)) {
         var10 = 4;
      }

      if (var5 == 3 && var1.func_147445_c(var2, var3, var4 - 1, true)) {
         var10 = 3;
      }

      if (var5 == 4 && var1.func_147445_c(var2 + 1, var3, var4, true)) {
         var10 = 2;
      }

      if (var5 == 5 && var1.func_147445_c(var2 - 1, var3, var4, true)) {
         var10 = 1;
      }

      return var10;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      super.func_149674_a(var1, var2, var3, var4, var5);
      if (var1.func_72805_g(var2, var3, var4) == 0) {
         this.func_149726_b(var1, var2, var3, var4);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      if (var1.func_72805_g(var2, var3, var4) == 0) {
         if (var1.func_147445_c(var2 - 1, var3, var4, true)) {
            var1.func_72921_c(var2, var3, var4, 1, 2);
         } else if (var1.func_147445_c(var2 + 1, var3, var4, true)) {
            var1.func_72921_c(var2, var3, var4, 2, 2);
         } else if (var1.func_147445_c(var2, var3, var4 - 1, true)) {
            var1.func_72921_c(var2, var3, var4, 3, 2);
         } else if (var1.func_147445_c(var2, var3, var4 + 1, true)) {
            var1.func_72921_c(var2, var3, var4, 4, 2);
         } else if (this.func_150107_m(var1, var2, var3 - 1, var4)) {
            var1.func_72921_c(var2, var3, var4, 5, 2);
         }
      }

      this.func_150109_e(var1, var2, var3, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      this.func_150108_b(var1, var2, var3, var4, var5);
   }

   protected boolean func_150108_b(World var1, int var2, int var3, int var4, Block var5) {
      if (this.func_150109_e(var1, var2, var3, var4)) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         boolean var7 = false;
         if (!var1.func_147445_c(var2 - 1, var3, var4, true) && var6 == 1) {
            var7 = true;
         }

         if (!var1.func_147445_c(var2 + 1, var3, var4, true) && var6 == 2) {
            var7 = true;
         }

         if (!var1.func_147445_c(var2, var3, var4 - 1, true) && var6 == 3) {
            var7 = true;
         }

         if (!var1.func_147445_c(var2, var3, var4 + 1, true) && var6 == 4) {
            var7 = true;
         }

         if (!this.func_150107_m(var1, var2, var3 - 1, var4) && var6 == 5) {
            var7 = true;
         }

         if (var7) {
            this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
            var1.func_147468_f(var2, var3, var4);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   protected boolean func_150109_e(World var1, int var2, int var3, int var4) {
      if (!this.func_149742_c(var1, var2, var3, var4)) {
         if (var1.func_147439_a(var2, var3, var4) == this) {
            this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
            var1.func_147468_f(var2, var3, var4);
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public MovingObjectPosition func_149731_a(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
      int var7 = var1.func_72805_g(var2, var3, var4) & 7;
      float var8 = 0.15F;
      if (var7 == 1) {
         this.func_149676_a(0.0F, 0.2F, 0.5F - var8, var8 * 2.0F, 0.8F, 0.5F + var8);
      } else if (var7 == 2) {
         this.func_149676_a(1.0F - var8 * 2.0F, 0.2F, 0.5F - var8, 1.0F, 0.8F, 0.5F + var8);
      } else if (var7 == 3) {
         this.func_149676_a(0.5F - var8, 0.2F, 0.0F, 0.5F + var8, 0.8F, var8 * 2.0F);
      } else if (var7 == 4) {
         this.func_149676_a(0.5F - var8, 0.2F, 1.0F - var8 * 2.0F, 0.5F + var8, 0.8F, 1.0F);
      } else {
         var8 = 0.1F;
         this.func_149676_a(0.5F - var8, 0.0F, 0.5F - var8, 0.5F + var8, 0.6F, 0.5F + var8);
      }

      return super.func_149731_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      double var7 = (double)((float)var2 + 0.5F);
      double var9 = (double)((float)var3 + 0.7F);
      double var11 = (double)((float)var4 + 0.5F);
      double var13 = 0.2199999988079071;
      double var15 = 0.27000001072883606;
      if (var6 == 1) {
         var1.func_72869_a("smoke", var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         var1.func_72869_a("flame", var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
      } else if (var6 == 2) {
         var1.func_72869_a("smoke", var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         var1.func_72869_a("flame", var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
      } else if (var6 == 3) {
         var1.func_72869_a("smoke", var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
         var1.func_72869_a("flame", var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
      } else if (var6 == 4) {
         var1.func_72869_a("smoke", var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
         var1.func_72869_a("flame", var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
      } else {
         var1.func_72869_a("smoke", var7, var9, var11, 0.0, 0.0, 0.0);
         var1.func_72869_a("flame", var7, var9, var11, 0.0, 0.0, 0.0);
      }
   }
}
