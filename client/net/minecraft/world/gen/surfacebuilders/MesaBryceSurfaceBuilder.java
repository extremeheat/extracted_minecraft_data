package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class MesaBryceSurfaceBuilder extends MesaSurfaceBuilder {
   private static final IBlockState field_202634_f;
   private static final IBlockState field_202635_g;
   private static final IBlockState field_202636_h;

   public MesaBryceSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      double var15 = 0.0D;
      double var17 = Math.min(Math.abs(var7), this.field_202617_c.func_151601_a((double)var4 * 0.25D, (double)var5 * 0.25D));
      if (var17 > 0.0D) {
         double var19 = 0.001953125D;
         double var21 = Math.abs(this.field_202618_d.func_151601_a((double)var4 * 0.001953125D, (double)var5 * 0.001953125D));
         var15 = var17 * var17 * 2.5D;
         double var23 = Math.ceil(var21 * 50.0D) + 14.0D;
         if (var15 > var23) {
            var15 = var23;
         }

         var15 += 64.0D;
      }

      int var31 = var4 & 15;
      int var20 = var5 & 15;
      IBlockState var32 = field_202634_f;
      IBlockState var22 = var3.func_203944_q().func_204109_b();
      int var33 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      boolean var24 = Math.cos(var7 / 3.0D * 3.141592653589793D) > 0.0D;
      int var25 = -1;
      boolean var26 = false;
      BlockPos.MutableBlockPos var27 = new BlockPos.MutableBlockPos();

      for(int var28 = Math.max(var6, (int)var15 + 1); var28 >= 0; --var28) {
         var27.func_181079_c(var31, var28, var20);
         if (var2.func_180495_p(var27).func_196958_f() && var28 < (int)var15) {
            var2.func_177436_a(var27, var9, false);
         }

         IBlockState var29 = var2.func_180495_p(var27);
         if (var29.func_196958_f()) {
            var25 = -1;
         } else if (var29.func_177230_c() == var9.func_177230_c()) {
            if (var25 == -1) {
               var26 = false;
               if (var33 <= 0) {
                  var32 = Blocks.field_150350_a.func_176223_P();
                  var22 = var9;
               } else if (var28 >= var11 - 4 && var28 <= var11 + 1) {
                  var32 = field_202634_f;
                  var22 = var3.func_203944_q().func_204109_b();
               }

               if (var28 < var11 && (var32 == null || var32.func_196958_f())) {
                  var32 = var10;
               }

               var25 = var33 + Math.max(0, var28 - var11);
               if (var28 >= var11 - 1) {
                  if (var28 <= var11 + 3 + var33) {
                     var2.func_177436_a(var27, var3.func_203944_q().func_204108_a(), false);
                     var26 = true;
                  } else {
                     IBlockState var34;
                     if (var28 >= 64 && var28 <= 127) {
                        if (var24) {
                           var34 = field_202636_h;
                        } else {
                           var34 = this.func_202614_a(var4, var28, var5);
                        }
                     } else {
                        var34 = field_202635_g;
                     }

                     var2.func_177436_a(var27, var34, false);
                  }
               } else {
                  var2.func_177436_a(var27, var22, false);
                  Block var30 = var22.func_177230_c();
                  if (var30 == Blocks.field_196777_fo || var30 == Blocks.field_196778_fp || var30 == Blocks.field_196780_fq || var30 == Blocks.field_196782_fr || var30 == Blocks.field_196783_fs || var30 == Blocks.field_196785_ft || var30 == Blocks.field_196787_fu || var30 == Blocks.field_196789_fv || var30 == Blocks.field_196791_fw || var30 == Blocks.field_196793_fx || var30 == Blocks.field_196795_fy || var30 == Blocks.field_196797_fz || var30 == Blocks.field_196719_fA || var30 == Blocks.field_196720_fB || var30 == Blocks.field_196721_fC || var30 == Blocks.field_196722_fD) {
                     var2.func_177436_a(var27, field_202635_g, false);
                  }
               }
            } else if (var25 > 0) {
               --var25;
               if (var26) {
                  var2.func_177436_a(var27, field_202635_g, false);
               } else {
                  var2.func_177436_a(var27, this.func_202614_a(var4, var28, var5), false);
               }
            }
         }
      }

   }

   static {
      field_202634_f = Blocks.field_196777_fo.func_176223_P();
      field_202635_g = Blocks.field_196778_fp.func_176223_P();
      field_202636_h = Blocks.field_150405_ch.func_176223_P();
   }
}
