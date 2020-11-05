package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class OreFeature extends Feature<OreConfiguration> {
   public OreFeature(Codec<OreConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, OreConfiguration var5) {
      float var6 = var3.nextFloat() * 3.1415927F;
      float var7 = (float)var5.size / 8.0F;
      int var8 = Mth.ceil(((float)var5.size / 16.0F * 2.0F + 1.0F) / 2.0F);
      double var9 = (double)var4.getX() + Math.sin((double)var6) * (double)var7;
      double var11 = (double)var4.getX() - Math.sin((double)var6) * (double)var7;
      double var13 = (double)var4.getZ() + Math.cos((double)var6) * (double)var7;
      double var15 = (double)var4.getZ() - Math.cos((double)var6) * (double)var7;
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
      int var24 = var3.size;
      double[] var25 = new double[var24 * 4];

      int var26;
      double var28;
      double var30;
      double var32;
      double var34;
      for(var26 = 0; var26 < var24; ++var26) {
         float var27 = (float)var26 / (float)var24;
         var28 = Mth.lerp((double)var27, var4, var6);
         var30 = Mth.lerp((double)var27, var12, var14);
         var32 = Mth.lerp((double)var27, var8, var10);
         var34 = var2.nextDouble() * (double)var24 / 16.0D;
         double var36 = ((double)(Mth.sin(3.1415927F * var27) + 1.0F) * var34 + 1.0D) / 2.0D;
         var25[var26 * 4 + 0] = var28;
         var25[var26 * 4 + 1] = var30;
         var25[var26 * 4 + 2] = var32;
         var25[var26 * 4 + 3] = var36;
      }

      for(var26 = 0; var26 < var24 - 1; ++var26) {
         if (var25[var26 * 4 + 3] > 0.0D) {
            for(int var51 = var26 + 1; var51 < var24; ++var51) {
               if (var25[var51 * 4 + 3] > 0.0D) {
                  var28 = var25[var26 * 4 + 0] - var25[var51 * 4 + 0];
                  var30 = var25[var26 * 4 + 1] - var25[var51 * 4 + 1];
                  var32 = var25[var26 * 4 + 2] - var25[var51 * 4 + 2];
                  var34 = var25[var26 * 4 + 3] - var25[var51 * 4 + 3];
                  if (var34 * var34 > var28 * var28 + var30 * var30 + var32 * var32) {
                     if (var34 > 0.0D) {
                        var25[var51 * 4 + 3] = -1.0D;
                     } else {
                        var25[var26 * 4 + 3] = -1.0D;
                     }
                  }
               }
            }
         }
      }

      for(var26 = 0; var26 < var24; ++var26) {
         double var52 = var25[var26 * 4 + 3];
         if (var52 >= 0.0D) {
            double var29 = var25[var26 * 4 + 0];
            double var31 = var25[var26 * 4 + 1];
            double var33 = var25[var26 * 4 + 2];
            int var35 = Math.max(Mth.floor(var29 - var52), var16);
            int var53 = Math.max(Mth.floor(var31 - var52), var17);
            int var37 = Math.max(Mth.floor(var33 - var52), var18);
            int var38 = Math.max(Mth.floor(var29 + var52), var35);
            int var39 = Math.max(Mth.floor(var31 + var52), var53);
            int var40 = Math.max(Mth.floor(var33 + var52), var37);

            for(int var41 = var35; var41 <= var38; ++var41) {
               double var42 = ((double)var41 + 0.5D - var29) / var52;
               if (var42 * var42 < 1.0D) {
                  for(int var44 = var53; var44 <= var39; ++var44) {
                     double var45 = ((double)var44 + 0.5D - var31) / var52;
                     if (var42 * var42 + var45 * var45 < 1.0D) {
                        for(int var47 = var37; var47 <= var40; ++var47) {
                           double var48 = ((double)var47 + 0.5D - var33) / var52;
                           if (var42 * var42 + var45 * var45 + var48 * var48 < 1.0D) {
                              int var50 = var41 - var16 + (var44 - var17) * var19 + (var47 - var18) * var19 * var20;
                              if (!var22.get(var50)) {
                                 var22.set(var50);
                                 var23.set(var41, var44, var47);
                                 if (var3.target.test(var1.getBlockState(var23), var2)) {
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
