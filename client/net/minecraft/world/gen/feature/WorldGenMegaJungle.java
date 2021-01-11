package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {
   public WorldGenMegaJungle(boolean var1, int var2, int var3, IBlockState var4, IBlockState var5) {
      super(var1, var2, var3, var4, var5);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4 = this.func_150533_a(var2);
      if (!this.func_175929_a(var1, var2, var3, var4)) {
         return false;
      } else {
         this.func_175930_c(var1, var3.func_177981_b(var4), 2);

         for(int var5 = var3.func_177956_o() + var4 - 2 - var2.nextInt(4); var5 > var3.func_177956_o() + var4 / 2; var5 -= 2 + var2.nextInt(4)) {
            float var6 = var2.nextFloat() * 3.1415927F * 2.0F;
            int var7 = var3.func_177958_n() + (int)(0.5F + MathHelper.func_76134_b(var6) * 4.0F);
            int var8 = var3.func_177952_p() + (int)(0.5F + MathHelper.func_76126_a(var6) * 4.0F);

            int var9;
            for(var9 = 0; var9 < 5; ++var9) {
               var7 = var3.func_177958_n() + (int)(1.5F + MathHelper.func_76134_b(var6) * (float)var9);
               var8 = var3.func_177952_p() + (int)(1.5F + MathHelper.func_76126_a(var6) * (float)var9);
               this.func_175903_a(var1, new BlockPos(var7, var5 - 3 + var9 / 2, var8), this.field_76520_b);
            }

            var9 = 1 + var2.nextInt(2);
            int var10 = var5;

            for(int var11 = var5 - var9; var11 <= var10; ++var11) {
               int var12 = var11 - var10;
               this.func_175928_b(var1, new BlockPos(var7, var11, var8), 1 - var12);
            }
         }

         for(int var13 = 0; var13 < var4; ++var13) {
            BlockPos var14 = var3.func_177981_b(var13);
            if (this.func_150523_a(var1.func_180495_p(var14).func_177230_c())) {
               this.func_175903_a(var1, var14, this.field_76520_b);
               if (var13 > 0) {
                  this.func_181632_a(var1, var2, var14.func_177976_e(), BlockVine.field_176278_M);
                  this.func_181632_a(var1, var2, var14.func_177978_c(), BlockVine.field_176279_N);
               }
            }

            if (var13 < var4 - 1) {
               BlockPos var15 = var14.func_177974_f();
               if (this.func_150523_a(var1.func_180495_p(var15).func_177230_c())) {
                  this.func_175903_a(var1, var15, this.field_76520_b);
                  if (var13 > 0) {
                     this.func_181632_a(var1, var2, var15.func_177974_f(), BlockVine.field_176280_O);
                     this.func_181632_a(var1, var2, var15.func_177978_c(), BlockVine.field_176279_N);
                  }
               }

               BlockPos var16 = var14.func_177968_d().func_177974_f();
               if (this.func_150523_a(var1.func_180495_p(var16).func_177230_c())) {
                  this.func_175903_a(var1, var16, this.field_76520_b);
                  if (var13 > 0) {
                     this.func_181632_a(var1, var2, var16.func_177974_f(), BlockVine.field_176280_O);
                     this.func_181632_a(var1, var2, var16.func_177968_d(), BlockVine.field_176273_b);
                  }
               }

               BlockPos var17 = var14.func_177968_d();
               if (this.func_150523_a(var1.func_180495_p(var17).func_177230_c())) {
                  this.func_175903_a(var1, var17, this.field_76520_b);
                  if (var13 > 0) {
                     this.func_181632_a(var1, var2, var17.func_177976_e(), BlockVine.field_176278_M);
                     this.func_181632_a(var1, var2, var17.func_177968_d(), BlockVine.field_176273_b);
                  }
               }
            }
         }

         return true;
      }
   }

   private void func_181632_a(World var1, Random var2, BlockPos var3, PropertyBool var4) {
      if (var2.nextInt(3) > 0 && var1.func_175623_d(var3)) {
         this.func_175903_a(var1, var3, Blocks.field_150395_bd.func_176223_P().func_177226_a(var4, true));
      }

   }

   private void func_175930_c(World var1, BlockPos var2, int var3) {
      byte var4 = 2;

      for(int var5 = -var4; var5 <= 0; ++var5) {
         this.func_175925_a(var1, var2.func_177981_b(var5), var3 + 1 - var5);
      }

   }
}
