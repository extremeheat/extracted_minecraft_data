package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class MesaForestSurfaceBuilder extends MesaSurfaceBuilder {
   private static final IBlockState field_202627_f;
   private static final IBlockState field_202628_g;
   private static final IBlockState field_202629_h;

   public MesaForestSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      int var15 = var4 & 15;
      int var16 = var5 & 15;
      IBlockState var17 = field_202627_f;
      IBlockState var18 = var3.func_203944_q().func_204109_b();
      int var19 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      boolean var20 = Math.cos(var7 / 3.0D * 3.141592653589793D) > 0.0D;
      int var21 = -1;
      boolean var22 = false;
      int var23 = 0;
      BlockPos.MutableBlockPos var24 = new BlockPos.MutableBlockPos();

      for(int var25 = var6; var25 >= 0; --var25) {
         if (var23 < 15) {
            var24.func_181079_c(var15, var25, var16);
            IBlockState var26 = var2.func_180495_p(var24);
            if (var26.func_196958_f()) {
               var21 = -1;
            } else if (var26.func_177230_c() == var9.func_177230_c()) {
               if (var21 == -1) {
                  var22 = false;
                  if (var19 <= 0) {
                     var17 = Blocks.field_150350_a.func_176223_P();
                     var18 = var9;
                  } else if (var25 >= var11 - 4 && var25 <= var11 + 1) {
                     var17 = field_202627_f;
                     var18 = var3.func_203944_q().func_204109_b();
                  }

                  if (var25 < var11 && (var17 == null || var17.func_196958_f())) {
                     var17 = var10;
                  }

                  var21 = var19 + Math.max(0, var25 - var11);
                  if (var25 >= var11 - 1) {
                     if (var25 > 86 + var19 * 2) {
                        if (var20) {
                           var2.func_177436_a(var24, Blocks.field_196660_k.func_176223_P(), false);
                        } else {
                           var2.func_177436_a(var24, Blocks.field_196658_i.func_176223_P(), false);
                        }
                     } else if (var25 > var11 + 3 + var19) {
                        IBlockState var27;
                        if (var25 >= 64 && var25 <= 127) {
                           if (var20) {
                              var27 = field_202629_h;
                           } else {
                              var27 = this.func_202614_a(var4, var25, var5);
                           }
                        } else {
                           var27 = field_202628_g;
                        }

                        var2.func_177436_a(var24, var27, false);
                     } else {
                        var2.func_177436_a(var24, var3.func_203944_q().func_204108_a(), false);
                        var22 = true;
                     }
                  } else {
                     var2.func_177436_a(var24, var18, false);
                     if (var18.func_177230_c() == field_202627_f) {
                        var2.func_177436_a(var24, field_202628_g, false);
                     }
                  }
               } else if (var21 > 0) {
                  --var21;
                  if (var22) {
                     var2.func_177436_a(var24, field_202628_g, false);
                  } else {
                     var2.func_177436_a(var24, this.func_202614_a(var4, var25, var5), false);
                  }
               }

               ++var23;
            }
         }
      }

   }

   static {
      field_202627_f = Blocks.field_196777_fo.func_176223_P();
      field_202628_g = Blocks.field_196778_fp.func_176223_P();
      field_202629_h = Blocks.field_150405_ch.func_176223_P();
   }
}
