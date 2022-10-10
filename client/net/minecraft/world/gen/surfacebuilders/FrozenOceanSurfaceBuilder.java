package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class FrozenOceanSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   protected static final IBlockState field_205192_a;
   protected static final IBlockState field_205193_b;
   private static final IBlockState field_205195_d;
   private static final IBlockState field_205196_e;
   private static final IBlockState field_205197_f;
   private NoiseGeneratorPerlin field_205199_h;
   private NoiseGeneratorPerlin field_205200_i;
   private long field_205201_j;

   public FrozenOceanSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      double var15 = 0.0D;
      double var17 = 0.0D;
      BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();
      float var20 = var3.func_180626_a(var19.func_181079_c(var4, 63, var5));
      double var21 = Math.min(Math.abs(var7), this.field_205199_h.func_151601_a((double)var4 * 0.1D, (double)var5 * 0.1D));
      if (var21 > 1.8D) {
         double var23 = 0.09765625D;
         double var25 = Math.abs(this.field_205200_i.func_151601_a((double)var4 * 0.09765625D, (double)var5 * 0.09765625D));
         var15 = var21 * var21 * 1.2D;
         double var27 = Math.ceil(var25 * 40.0D) + 14.0D;
         if (var15 > var27) {
            var15 = var27;
         }

         if (var20 > 0.1F) {
            var15 -= 2.0D;
         }

         if (var15 > 2.0D) {
            var17 = (double)var11 - var15 - 7.0D;
            var15 += (double)var11;
         } else {
            var15 = 0.0D;
         }
      }

      int var34 = var4 & 15;
      int var24 = var5 & 15;
      IBlockState var35 = var3.func_203944_q().func_204109_b();
      IBlockState var26 = var3.func_203944_q().func_204108_a();
      int var36 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      int var28 = -1;
      int var29 = 0;
      int var30 = 2 + var1.nextInt(4);
      int var31 = var11 + 18 + var1.nextInt(10);

      for(int var32 = Math.max(var6, (int)var15 + 1); var32 >= 0; --var32) {
         var19.func_181079_c(var34, var32, var24);
         if (var2.func_180495_p(var19).func_196958_f() && var32 < (int)var15 && var1.nextDouble() > 0.01D) {
            var2.func_177436_a(var19, field_205192_a, false);
         } else if (var2.func_180495_p(var19).func_185904_a() == Material.field_151586_h && var32 > (int)var17 && var32 < var11 && var17 != 0.0D && var1.nextDouble() > 0.15D) {
            var2.func_177436_a(var19, field_205192_a, false);
         }

         IBlockState var33 = var2.func_180495_p(var19);
         if (var33.func_196958_f()) {
            var28 = -1;
         } else if (var33.func_177230_c() != var9.func_177230_c()) {
            if (var33.func_177230_c() == Blocks.field_150403_cj && var29 <= var30 && var32 > var31) {
               var2.func_177436_a(var19, field_205193_b, false);
               ++var29;
            }
         } else if (var28 == -1) {
            if (var36 <= 0) {
               var26 = field_205195_d;
               var35 = var9;
            } else if (var32 >= var11 - 4 && var32 <= var11 + 1) {
               var26 = var3.func_203944_q().func_204108_a();
               var35 = var3.func_203944_q().func_204109_b();
            }

            if (var32 < var11 && (var26 == null || var26.func_196958_f())) {
               if (var3.func_180626_a(var19.func_181079_c(var4, var32, var5)) < 0.15F) {
                  var26 = field_205197_f;
               } else {
                  var26 = var10;
               }
            }

            var28 = var36;
            if (var32 >= var11 - 1) {
               var2.func_177436_a(var19, var26, false);
            } else if (var32 < var11 - 7 - var36) {
               var26 = field_205195_d;
               var35 = var9;
               var2.func_177436_a(var19, field_205196_e, false);
            } else {
               var2.func_177436_a(var19, var35, false);
            }
         } else if (var28 > 0) {
            --var28;
            var2.func_177436_a(var19, var35, false);
            if (var28 == 0 && var35.func_177230_c() == Blocks.field_150354_m && var36 > 1) {
               var28 = var1.nextInt(4) + Math.max(0, var32 - 63);
               var35 = var35.func_177230_c() == Blocks.field_196611_F ? Blocks.field_180395_cM.func_176223_P() : Blocks.field_150322_A.func_176223_P();
            }
         }
      }

   }

   public void func_205548_a(long var1) {
      if (this.field_205201_j != var1 || this.field_205199_h == null || this.field_205200_i == null) {
         SharedSeedRandom var3 = new SharedSeedRandom(var1);
         this.field_205199_h = new NoiseGeneratorPerlin(var3, 4);
         this.field_205200_i = new NoiseGeneratorPerlin(var3, 1);
      }

      this.field_205201_j = var1;
   }

   static {
      field_205192_a = Blocks.field_150403_cj.func_176223_P();
      field_205193_b = Blocks.field_196604_cC.func_176223_P();
      field_205195_d = Blocks.field_150350_a.func_176223_P();
      field_205196_e = Blocks.field_150351_n.func_176223_P();
      field_205197_f = Blocks.field_150432_aD.func_176223_P();
   }
}
