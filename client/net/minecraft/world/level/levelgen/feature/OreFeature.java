package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;

public class OreFeature extends Feature<OreConfiguration> {
   public OreFeature(Function<Dynamic<?>, ? extends OreConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, OreConfiguration var5) {
      float var6 = var3.nextFloat() * 3.1415927F;
      float var7 = (float)var5.size / 8.0F;
      int var8 = Mth.ceil(((float)var5.size / 16.0F * 2.0F + 1.0F) / 2.0F);
      double var9 = (double)((float)var4.getX() + Mth.sin(var6) * var7);
      double var11 = (double)((float)var4.getX() - Mth.sin(var6) * var7);
      double var13 = (double)((float)var4.getZ() + Mth.cos(var6) * var7);
      double var15 = (double)((float)var4.getZ() - Mth.cos(var6) * var7);
      boolean var17 = true;
      double var18 = (double)(var4.getY() + var3.nextInt(3) - 2);
      double var20 = (double)(var4.getY() + var3.nextInt(3) - 2);
      int var22 = var4.getX() - Mth.ceil(var7) - var8;
      int var23 = var4.getY() - 2 - var8;
      int var24 = var4.getZ() - Mth.ceil(var7) - var8;
      int var25 = 2 * (Mth.ceil(var7) + var8);
      int var26 = 2 * (2 + var8);

      for(int var27 = var22; var27 <= var22 + var25; ++var27) {
         for(int var28 = var24; var28 <= var24 + var25; ++var28) {
            if (var23 <= var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var27, var28)) {
               return this.doPlace(var1, var3, var5, var9, var11, var13, var15, var18, var20, var22, var23, var24, var25, var26);
            }
         }
      }

      return false;
   }

   protected boolean doPlace(LevelAccessor var1, Random var2, OreConfiguration var3, double var4, double var6, double var8, double var10, double var12, double var14, int var16, int var17, int var18, int var19, int var20) {
      int var21 = 0;
      BitSet var22 = new BitSet(var19 * var20 * var19);
      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      double[] var24 = new double[var3.size * 4];

      int var25;
      double var27;
      double var29;
      double var31;
      double var33;
      for(var25 = 0; var25 < var3.size; ++var25) {
         float var26 = (float)var25 / (float)var3.size;
         var27 = Mth.lerp((double)var26, var4, var6);
         var29 = Mth.lerp((double)var26, var12, var14);
         var31 = Mth.lerp((double)var26, var8, var10);
         var33 = var2.nextDouble() * (double)var3.size / 16.0D;
         double var35 = ((double)(Mth.sin(3.1415927F * var26) + 1.0F) * var33 + 1.0D) / 2.0D;
         var24[var25 * 4 + 0] = var27;
         var24[var25 * 4 + 1] = var29;
         var24[var25 * 4 + 2] = var31;
         var24[var25 * 4 + 3] = var35;
      }

      for(var25 = 0; var25 < var3.size - 1; ++var25) {
         if (var24[var25 * 4 + 3] > 0.0D) {
            for(int var50 = var25 + 1; var50 < var3.size; ++var50) {
               if (var24[var50 * 4 + 3] > 0.0D) {
                  var27 = var24[var25 * 4 + 0] - var24[var50 * 4 + 0];
                  var29 = var24[var25 * 4 + 1] - var24[var50 * 4 + 1];
                  var31 = var24[var25 * 4 + 2] - var24[var50 * 4 + 2];
                  var33 = var24[var25 * 4 + 3] - var24[var50 * 4 + 3];
                  if (var33 * var33 > var27 * var27 + var29 * var29 + var31 * var31) {
                     if (var33 > 0.0D) {
                        var24[var50 * 4 + 3] = -1.0D;
                     } else {
                        var24[var25 * 4 + 3] = -1.0D;
                     }
                  }
               }
            }
         }
      }

      for(var25 = 0; var25 < var3.size; ++var25) {
         double var51 = var24[var25 * 4 + 3];
         if (var51 >= 0.0D) {
            double var28 = var24[var25 * 4 + 0];
            double var30 = var24[var25 * 4 + 1];
            double var32 = var24[var25 * 4 + 2];
            int var34 = Math.max(Mth.floor(var28 - var51), var16);
            int var52 = Math.max(Mth.floor(var30 - var51), var17);
            int var36 = Math.max(Mth.floor(var32 - var51), var18);
            int var37 = Math.max(Mth.floor(var28 + var51), var34);
            int var38 = Math.max(Mth.floor(var30 + var51), var52);
            int var39 = Math.max(Mth.floor(var32 + var51), var36);

            for(int var40 = var34; var40 <= var37; ++var40) {
               double var41 = ((double)var40 + 0.5D - var28) / var51;
               if (var41 * var41 < 1.0D) {
                  for(int var43 = var52; var43 <= var38; ++var43) {
                     double var44 = ((double)var43 + 0.5D - var30) / var51;
                     if (var41 * var41 + var44 * var44 < 1.0D) {
                        for(int var46 = var36; var46 <= var39; ++var46) {
                           double var47 = ((double)var46 + 0.5D - var32) / var51;
                           if (var41 * var41 + var44 * var44 + var47 * var47 < 1.0D) {
                              int var49 = var40 - var16 + (var43 - var17) * var19 + (var46 - var18) * var19 * var20;
                              if (!var22.get(var49)) {
                                 var22.set(var49);
                                 var23.set(var40, var43, var46);
                                 if (var3.target.getPredicate().test(var1.getBlockState(var23))) {
                                    var1.setBlock(var23, var3.state, 2);
                                    ++var21;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var21 > 0;
   }
}
