package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class OreFeature extends Feature<OreConfiguration> {
   public OreFeature(Codec<OreConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<OreConfiguration> var1) {
      RandomSource var2 = var1.random();
      BlockPos var3 = var1.origin();
      WorldGenLevel var4 = var1.level();
      OreConfiguration var5 = (OreConfiguration)var1.config();
      float var6 = var2.nextFloat() * 3.1415927F;
      float var7 = (float)var5.size / 8.0F;
      int var8 = Mth.ceil(((float)var5.size / 16.0F * 2.0F + 1.0F) / 2.0F);
      double var9 = (double)var3.getX() + Math.sin((double)var6) * (double)var7;
      double var11 = (double)var3.getX() - Math.sin((double)var6) * (double)var7;
      double var13 = (double)var3.getZ() + Math.cos((double)var6) * (double)var7;
      double var15 = (double)var3.getZ() - Math.cos((double)var6) * (double)var7;
      boolean var17 = true;
      double var18 = (double)(var3.getY() + var2.nextInt(3) - 2);
      double var20 = (double)(var3.getY() + var2.nextInt(3) - 2);
      int var22 = var3.getX() - Mth.ceil(var7) - var8;
      int var23 = var3.getY() - 2 - var8;
      int var24 = var3.getZ() - Mth.ceil(var7) - var8;
      int var25 = 2 * (Mth.ceil(var7) + var8);
      int var26 = 2 * (2 + var8);

      for(int var27 = var22; var27 <= var22 + var25; ++var27) {
         for(int var28 = var24; var28 <= var24 + var25; ++var28) {
            if (var23 <= var4.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var27, var28)) {
               return this.doPlace(var4, var2, var5, var9, var11, var13, var15, var18, var20, var22, var23, var24, var25, var26);
            }
         }
      }

      return false;
   }

   protected boolean doPlace(WorldGenLevel var1, RandomSource var2, OreConfiguration var3, double var4, double var6, double var8, double var10, double var12, double var14, int var16, int var17, int var18, int var19, int var20) {
      int var21 = 0;
      BitSet var22 = new BitSet(var19 * var20 * var19);
      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      int var24 = var3.size;
      double[] var25 = new double[var24 * 4];

      for(int var26 = 0; var26 < var24; ++var26) {
         float var27 = (float)var26 / (float)var24;
         double var28 = Mth.lerp((double)var27, var4, var6);
         double var30 = Mth.lerp((double)var27, var12, var14);
         double var32 = Mth.lerp((double)var27, var8, var10);
         double var34 = var2.nextDouble() * (double)var24 / 16.0;
         double var36 = ((double)(Mth.sin(3.1415927F * var27) + 1.0F) * var34 + 1.0) / 2.0;
         var25[var26 * 4 + 0] = var28;
         var25[var26 * 4 + 1] = var30;
         var25[var26 * 4 + 2] = var32;
         var25[var26 * 4 + 3] = var36;
      }

      for(int var61 = 0; var61 < var24 - 1; ++var61) {
         if (!(var25[var61 * 4 + 3] <= 0.0)) {
            for(int var63 = var61 + 1; var63 < var24; ++var63) {
               if (!(var25[var63 * 4 + 3] <= 0.0)) {
                  double var65 = var25[var61 * 4 + 0] - var25[var63 * 4 + 0];
                  double var67 = var25[var61 * 4 + 1] - var25[var63 * 4 + 1];
                  double var69 = var25[var61 * 4 + 2] - var25[var63 * 4 + 2];
                  double var71 = var25[var61 * 4 + 3] - var25[var63 * 4 + 3];
                  if (var71 * var71 > var65 * var65 + var67 * var67 + var69 * var69) {
                     if (var71 > 0.0) {
                        var25[var63 * 4 + 3] = -1.0;
                     } else {
                        var25[var61 * 4 + 3] = -1.0;
                     }
                  }
               }
            }
         }
      }

      try (BulkSectionAccess var62 = new BulkSectionAccess(var1)) {
         for(int var64 = 0; var64 < var24; ++var64) {
            double var66 = var25[var64 * 4 + 3];
            if (!(var66 < 0.0)) {
               double var68 = var25[var64 * 4 + 0];
               double var70 = var25[var64 * 4 + 1];
               double var72 = var25[var64 * 4 + 2];
               int var73 = Math.max(Mth.floor(var68 - var66), var16);
               int var37 = Math.max(Mth.floor(var70 - var66), var17);
               int var38 = Math.max(Mth.floor(var72 - var66), var18);
               int var39 = Math.max(Mth.floor(var68 + var66), var73);
               int var40 = Math.max(Mth.floor(var70 + var66), var37);
               int var41 = Math.max(Mth.floor(var72 + var66), var38);

               for(int var42 = var73; var42 <= var39; ++var42) {
                  double var43 = ((double)var42 + 0.5 - var68) / var66;
                  if (var43 * var43 < 1.0) {
                     for(int var45 = var37; var45 <= var40; ++var45) {
                        double var46 = ((double)var45 + 0.5 - var70) / var66;
                        if (var43 * var43 + var46 * var46 < 1.0) {
                           for(int var48 = var38; var48 <= var41; ++var48) {
                              double var49 = ((double)var48 + 0.5 - var72) / var66;
                              if (var43 * var43 + var46 * var46 + var49 * var49 < 1.0 && !var1.isOutsideBuildHeight(var45)) {
                                 int var51 = var42 - var16 + (var45 - var17) * var19 + (var48 - var18) * var19 * var20;
                                 if (!var22.get(var51)) {
                                    var22.set(var51);
                                    var23.set(var42, var45, var48);
                                    if (var1.ensureCanWrite(var23)) {
                                       LevelChunkSection var52 = var62.getSection(var23);
                                       if (var52 != null) {
                                          int var53 = SectionPos.sectionRelative(var42);
                                          int var54 = SectionPos.sectionRelative(var45);
                                          int var55 = SectionPos.sectionRelative(var48);
                                          BlockState var56 = var52.getBlockState(var53, var54, var55);

                                          for(OreConfiguration.TargetBlockState var58 : var3.targetStates) {
                                             Objects.requireNonNull(var62);
                                             if (canPlaceOre(var56, var62::getBlockState, var2, var3, var58, var23)) {
                                                var52.setBlockState(var53, var54, var55, var58.state, false);
                                                ++var21;
                                                break;
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
               }
            }
         }
      }

      return var21 > 0;
   }

   public static boolean canPlaceOre(BlockState var0, Function<BlockPos, BlockState> var1, RandomSource var2, OreConfiguration var3, OreConfiguration.TargetBlockState var4, BlockPos.MutableBlockPos var5) {
      if (!var4.target.test(var0, var2)) {
         return false;
      } else if (shouldSkipAirCheck(var2, var3.discardChanceOnAirExposure)) {
         return true;
      } else {
         return !isAdjacentToAir(var1, var5);
      }
   }

   protected static boolean shouldSkipAirCheck(RandomSource var0, float var1) {
      if (var1 <= 0.0F) {
         return true;
      } else if (var1 >= 1.0F) {
         return false;
      } else {
         return var0.nextFloat() >= var1;
      }
   }
}
