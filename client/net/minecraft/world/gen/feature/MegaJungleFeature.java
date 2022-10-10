package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class MegaJungleFeature extends HugeTreesFeature<NoFeatureConfig> {
   public MegaJungleFeature(boolean var1, int var2, int var3, IBlockState var4, IBlockState var5) {
      super(var1, var2, var3, var4, var5);
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = this.func_150533_a(var3);
      if (!this.func_203427_a(var2, var4, var5)) {
         return false;
      } else {
         this.func_202408_c(var2, var4.func_177981_b(var5), 2);

         for(int var6 = var4.func_177956_o() + var5 - 2 - var3.nextInt(4); var6 > var4.func_177956_o() + var5 / 2; var6 -= 2 + var3.nextInt(4)) {
            float var7 = var3.nextFloat() * 6.2831855F;
            int var8 = var4.func_177958_n() + (int)(0.5F + MathHelper.func_76134_b(var7) * 4.0F);
            int var9 = var4.func_177952_p() + (int)(0.5F + MathHelper.func_76126_a(var7) * 4.0F);

            int var10;
            for(var10 = 0; var10 < 5; ++var10) {
               var8 = var4.func_177958_n() + (int)(1.5F + MathHelper.func_76134_b(var7) * (float)var10);
               var9 = var4.func_177952_p() + (int)(1.5F + MathHelper.func_76126_a(var7) * (float)var10);
               this.func_208520_a(var1, var2, new BlockPos(var8, var6 - 3 + var10 / 2, var9), this.field_76520_b);
            }

            var10 = 1 + var3.nextInt(2);
            int var11 = var6;

            for(int var12 = var6 - var10; var12 <= var11; ++var12) {
               int var13 = var12 - var11;
               this.func_175928_b(var2, new BlockPos(var8, var12, var9), 1 - var13);
            }
         }

         for(int var14 = 0; var14 < var5; ++var14) {
            BlockPos var15 = var4.func_177981_b(var14);
            if (this.func_150523_a(var2.func_180495_p(var15).func_177230_c())) {
               this.func_208520_a(var1, var2, var15, this.field_76520_b);
               if (var14 > 0) {
                  this.func_202407_a(var2, var3, var15.func_177976_e(), BlockVine.field_176278_M);
                  this.func_202407_a(var2, var3, var15.func_177978_c(), BlockVine.field_176279_N);
               }
            }

            if (var14 < var5 - 1) {
               BlockPos var16 = var15.func_177974_f();
               if (this.func_150523_a(var2.func_180495_p(var16).func_177230_c())) {
                  this.func_208520_a(var1, var2, var16, this.field_76520_b);
                  if (var14 > 0) {
                     this.func_202407_a(var2, var3, var16.func_177974_f(), BlockVine.field_176280_O);
                     this.func_202407_a(var2, var3, var16.func_177978_c(), BlockVine.field_176279_N);
                  }
               }

               BlockPos var17 = var15.func_177968_d().func_177974_f();
               if (this.func_150523_a(var2.func_180495_p(var17).func_177230_c())) {
                  this.func_208520_a(var1, var2, var17, this.field_76520_b);
                  if (var14 > 0) {
                     this.func_202407_a(var2, var3, var17.func_177974_f(), BlockVine.field_176280_O);
                     this.func_202407_a(var2, var3, var17.func_177968_d(), BlockVine.field_176273_b);
                  }
               }

               BlockPos var18 = var15.func_177968_d();
               if (this.func_150523_a(var2.func_180495_p(var18).func_177230_c())) {
                  this.func_208520_a(var1, var2, var18, this.field_76520_b);
                  if (var14 > 0) {
                     this.func_202407_a(var2, var3, var18.func_177976_e(), BlockVine.field_176278_M);
                     this.func_202407_a(var2, var3, var18.func_177968_d(), BlockVine.field_176273_b);
                  }
               }
            }
         }

         return true;
      }
   }

   private void func_202407_a(IWorld var1, Random var2, BlockPos var3, BooleanProperty var4) {
      if (var2.nextInt(3) > 0 && var1.func_175623_d(var3)) {
         this.func_202278_a(var1, var3, (IBlockState)Blocks.field_150395_bd.func_176223_P().func_206870_a(var4, true));
      }

   }

   private void func_202408_c(IWorld var1, BlockPos var2, int var3) {
      boolean var4 = true;

      for(int var5 = -2; var5 <= 0; ++var5) {
         this.func_175925_a(var1, var2.func_177981_b(var5), var3 + 1 - var5);
      }

   }
}
