package net.minecraft.world.gen.surfacebuilders;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class MesaSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private static final IBlockState field_202620_f;
   private static final IBlockState field_202621_g;
   private static final IBlockState field_202622_h;
   private static final IBlockState field_202623_i;
   private static final IBlockState field_202624_j;
   private static final IBlockState field_202625_k;
   private static final IBlockState field_202626_l;
   protected IBlockState[] field_202615_a;
   protected long field_202616_b;
   protected NoiseGeneratorPerlin field_202617_c;
   protected NoiseGeneratorPerlin field_202618_d;
   protected NoiseGeneratorPerlin field_202619_e;

   public MesaSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      int var15 = var4 & 15;
      int var16 = var5 & 15;
      IBlockState var17 = field_202620_f;
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
                     var17 = field_202620_f;
                     var18 = var3.func_203944_q().func_204109_b();
                  }

                  if (var25 < var11 && (var17 == null || var17.func_196958_f())) {
                     var17 = var10;
                  }

                  var21 = var19 + Math.max(0, var25 - var11);
                  if (var25 >= var11 - 1) {
                     if (var25 > var11 + 3 + var19) {
                        IBlockState var27;
                        if (var25 >= 64 && var25 <= 127) {
                           if (var20) {
                              var27 = field_202622_h;
                           } else {
                              var27 = this.func_202614_a(var4, var25, var5);
                           }
                        } else {
                           var27 = field_202621_g;
                        }

                        var2.func_177436_a(var24, var27, false);
                     } else {
                        var2.func_177436_a(var24, var3.func_203944_q().func_204108_a(), false);
                        var22 = true;
                     }
                  } else {
                     var2.func_177436_a(var24, var18, false);
                     Block var28 = var18.func_177230_c();
                     if (var28 == Blocks.field_196777_fo || var28 == Blocks.field_196778_fp || var28 == Blocks.field_196780_fq || var28 == Blocks.field_196782_fr || var28 == Blocks.field_196783_fs || var28 == Blocks.field_196785_ft || var28 == Blocks.field_196787_fu || var28 == Blocks.field_196789_fv || var28 == Blocks.field_196791_fw || var28 == Blocks.field_196793_fx || var28 == Blocks.field_196795_fy || var28 == Blocks.field_196797_fz || var28 == Blocks.field_196719_fA || var28 == Blocks.field_196720_fB || var28 == Blocks.field_196721_fC || var28 == Blocks.field_196722_fD) {
                        var2.func_177436_a(var24, field_202621_g, false);
                     }
                  }
               } else if (var21 > 0) {
                  --var21;
                  if (var22) {
                     var2.func_177436_a(var24, field_202621_g, false);
                  } else {
                     var2.func_177436_a(var24, this.func_202614_a(var4, var25, var5), false);
                  }
               }

               ++var23;
            }
         }
      }

   }

   public void func_205548_a(long var1) {
      if (this.field_202616_b != var1 || this.field_202615_a == null) {
         this.func_202613_a(var1);
      }

      if (this.field_202616_b != var1 || this.field_202617_c == null || this.field_202618_d == null) {
         SharedSeedRandom var3 = new SharedSeedRandom(var1);
         this.field_202617_c = new NoiseGeneratorPerlin(var3, 4);
         this.field_202618_d = new NoiseGeneratorPerlin(var3, 1);
      }

      this.field_202616_b = var1;
   }

   protected void func_202613_a(long var1) {
      this.field_202615_a = new IBlockState[64];
      Arrays.fill(this.field_202615_a, field_202622_h);
      SharedSeedRandom var3 = new SharedSeedRandom(var1);
      this.field_202619_e = new NoiseGeneratorPerlin(var3, 1);

      int var4;
      for(var4 = 0; var4 < 64; ++var4) {
         var4 += var3.nextInt(5) + 1;
         if (var4 < 64) {
            this.field_202615_a[var4] = field_202621_g;
         }
      }

      var4 = var3.nextInt(4) + 2;

      int var5;
      int var6;
      int var7;
      int var8;
      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3.nextInt(3) + 1;
         var7 = var3.nextInt(64);

         for(var8 = 0; var7 + var8 < 64 && var8 < var6; ++var8) {
            this.field_202615_a[var7 + var8] = field_202623_i;
         }
      }

      var5 = var3.nextInt(4) + 2;

      int var9;
      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var3.nextInt(3) + 2;
         var8 = var3.nextInt(64);

         for(var9 = 0; var8 + var9 < 64 && var9 < var7; ++var9) {
            this.field_202615_a[var8 + var9] = field_202624_j;
         }
      }

      var6 = var3.nextInt(4) + 2;

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var3.nextInt(3) + 1;
         var9 = var3.nextInt(64);

         for(int var10 = 0; var9 + var10 < 64 && var10 < var8; ++var10) {
            this.field_202615_a[var9 + var10] = field_202625_k;
         }
      }

      var7 = var3.nextInt(3) + 3;
      var8 = 0;

      for(var9 = 0; var9 < var7; ++var9) {
         boolean var12 = true;
         var8 += var3.nextInt(16) + 4;

         for(int var11 = 0; var8 + var11 < 64 && var11 < 1; ++var11) {
            this.field_202615_a[var8 + var11] = field_202620_f;
            if (var8 + var11 > 1 && var3.nextBoolean()) {
               this.field_202615_a[var8 + var11 - 1] = field_202626_l;
            }

            if (var8 + var11 < 63 && var3.nextBoolean()) {
               this.field_202615_a[var8 + var11 + 1] = field_202626_l;
            }
         }
      }

   }

   protected IBlockState func_202614_a(int var1, int var2, int var3) {
      int var4 = (int)Math.round(this.field_202619_e.func_151601_a((double)var1 / 512.0D, (double)var3 / 512.0D) * 2.0D);
      return this.field_202615_a[(var2 + var4 + 64) % 64];
   }

   static {
      field_202620_f = Blocks.field_196777_fo.func_176223_P();
      field_202621_g = Blocks.field_196778_fp.func_176223_P();
      field_202622_h = Blocks.field_150405_ch.func_176223_P();
      field_202623_i = Blocks.field_196783_fs.func_176223_P();
      field_202624_j = Blocks.field_196719_fA.func_176223_P();
      field_202625_k = Blocks.field_196721_fC.func_176223_P();
      field_202626_l = Blocks.field_196791_fw.func_176223_P();
   }
}
