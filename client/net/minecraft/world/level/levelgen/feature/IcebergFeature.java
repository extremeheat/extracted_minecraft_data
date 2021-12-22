package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Material;

public class IcebergFeature extends Feature<BlockStateConfiguration> {
   public IcebergFeature(Codec<BlockStateConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<BlockStateConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      var2 = new BlockPos(var2.getX(), var1.chunkGenerator().getSeaLevel(), var2.getZ());
      Random var4 = var1.random();
      boolean var5 = var4.nextDouble() > 0.7D;
      BlockState var6 = ((BlockStateConfiguration)var1.config()).state;
      double var7 = var4.nextDouble() * 2.0D * 3.141592653589793D;
      int var9 = 11 - var4.nextInt(5);
      int var10 = 3 + var4.nextInt(3);
      boolean var11 = var4.nextDouble() > 0.7D;
      boolean var12 = true;
      int var13 = var11 ? var4.nextInt(6) + 6 : var4.nextInt(15) + 3;
      if (!var11 && var4.nextDouble() > 0.9D) {
         var13 += var4.nextInt(19) + 7;
      }

      int var14 = Math.min(var13 + var4.nextInt(11), 18);
      int var15 = Math.min(var13 + var4.nextInt(7) - var4.nextInt(5), 11);
      int var16 = var11 ? var9 : 11;

      int var17;
      int var18;
      int var19;
      int var20;
      for(var17 = -var16; var17 < var16; ++var17) {
         for(var18 = -var16; var18 < var16; ++var18) {
            for(var19 = 0; var19 < var13; ++var19) {
               var20 = var11 ? this.heightDependentRadiusEllipse(var19, var13, var15) : this.heightDependentRadiusRound(var4, var19, var13, var15);
               if (var11 || var17 < var20) {
                  this.generateIcebergBlock(var3, var4, var2, var13, var17, var19, var18, var20, var16, var11, var10, var7, var5, var6);
               }
            }
         }
      }

      this.smooth(var3, var2, var15, var13, var11, var9);

      for(var17 = -var16; var17 < var16; ++var17) {
         for(var18 = -var16; var18 < var16; ++var18) {
            for(var19 = -1; var19 > -var14; --var19) {
               var20 = var11 ? Mth.ceil((float)var16 * (1.0F - (float)Math.pow((double)var19, 2.0D) / ((float)var14 * 8.0F))) : var16;
               int var21 = this.heightDependentRadiusSteep(var4, -var19, var14, var15);
               if (var17 < var21) {
                  this.generateIcebergBlock(var3, var4, var2, var14, var17, var19, var18, var21, var20, var11, var10, var7, var5, var6);
               }
            }
         }
      }

      boolean var22 = var11 ? var4.nextDouble() > 0.1D : var4.nextDouble() > 0.7D;
      if (var22) {
         this.generateCutOut(var4, var3, var15, var13, var2, var11, var9, var7, var10);
      }

      return true;
   }

   private void generateCutOut(Random var1, LevelAccessor var2, int var3, int var4, BlockPos var5, boolean var6, int var7, double var8, int var10) {
      int var11 = var1.nextBoolean() ? -1 : 1;
      int var12 = var1.nextBoolean() ? -1 : 1;
      int var13 = var1.nextInt(Math.max(var3 / 2 - 2, 1));
      if (var1.nextBoolean()) {
         var13 = var3 / 2 + 1 - var1.nextInt(Math.max(var3 - var3 / 2 - 1, 1));
      }

      int var14 = var1.nextInt(Math.max(var3 / 2 - 2, 1));
      if (var1.nextBoolean()) {
         var14 = var3 / 2 + 1 - var1.nextInt(Math.max(var3 - var3 / 2 - 1, 1));
      }

      if (var6) {
         var13 = var14 = var1.nextInt(Math.max(var7 - 5, 1));
      }

      BlockPos var15 = new BlockPos(var11 * var13, 0, var12 * var14);
      double var16 = var6 ? var8 + 1.5707963267948966D : var1.nextDouble() * 2.0D * 3.141592653589793D;

      int var18;
      int var19;
      for(var18 = 0; var18 < var4 - 3; ++var18) {
         var19 = this.heightDependentRadiusRound(var1, var18, var4, var3);
         this.carve(var19, var18, var5, var2, false, var16, var15, var7, var10);
      }

      for(var18 = -1; var18 > -var4 + var1.nextInt(5); --var18) {
         var19 = this.heightDependentRadiusSteep(var1, -var18, var4, var3);
         this.carve(var19, var18, var5, var2, true, var16, var15, var7, var10);
      }

   }

   private void carve(int var1, int var2, BlockPos var3, LevelAccessor var4, boolean var5, double var6, BlockPos var8, int var9, int var10) {
      int var11 = var1 + 1 + var9 / 3;
      int var12 = Math.min(var1 - 3, 3) + var10 / 2 - 1;

      for(int var13 = -var11; var13 < var11; ++var13) {
         for(int var14 = -var11; var14 < var11; ++var14) {
            double var15 = this.signedDistanceEllipse(var13, var14, var8, var11, var12, var6);
            if (var15 < 0.0D) {
               BlockPos var17 = var3.offset(var13, var2, var14);
               BlockState var18 = var4.getBlockState(var17);
               if (isIcebergState(var18) || var18.is(Blocks.SNOW_BLOCK)) {
                  if (var5) {
                     this.setBlock(var4, var17, Blocks.WATER.defaultBlockState());
                  } else {
                     this.setBlock(var4, var17, Blocks.AIR.defaultBlockState());
                     this.removeFloatingSnowLayer(var4, var17);
                  }
               }
            }
         }
      }

   }

   private void removeFloatingSnowLayer(LevelAccessor var1, BlockPos var2) {
      if (var1.getBlockState(var2.above()).is(Blocks.SNOW)) {
         this.setBlock(var1, var2.above(), Blocks.AIR.defaultBlockState());
      }

   }

   private void generateIcebergBlock(LevelAccessor var1, Random var2, BlockPos var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, int var11, double var12, boolean var14, BlockState var15) {
      double var16 = var10 ? this.signedDistanceEllipse(var5, var7, BlockPos.ZERO, var9, this.getEllipseC(var6, var4, var11), var12) : this.signedDistanceCircle(var5, var7, BlockPos.ZERO, var8, var2);
      if (var16 < 0.0D) {
         BlockPos var18 = var3.offset(var5, var6, var7);
         double var19 = var10 ? -0.5D : (double)(-6 - var2.nextInt(3));
         if (var16 > var19 && var2.nextDouble() > 0.9D) {
            return;
         }

         this.setIcebergBlock(var18, var1, var2, var4 - var6, var4, var10, var14, var15);
      }

   }

   private void setIcebergBlock(BlockPos var1, LevelAccessor var2, Random var3, int var4, int var5, boolean var6, boolean var7, BlockState var8) {
      BlockState var9 = var2.getBlockState(var1);
      if (var9.getMaterial() == Material.AIR || var9.is(Blocks.SNOW_BLOCK) || var9.is(Blocks.ICE) || var9.is(Blocks.WATER)) {
         boolean var10 = !var6 || var3.nextDouble() > 0.05D;
         int var11 = var6 ? 3 : 2;
         if (var7 && !var9.is(Blocks.WATER) && (double)var4 <= (double)var3.nextInt(Math.max(1, var5 / var11)) + (double)var5 * 0.6D && var10) {
            this.setBlock(var2, var1, Blocks.SNOW_BLOCK.defaultBlockState());
         } else {
            this.setBlock(var2, var1, var8);
         }
      }

   }

   private int getEllipseC(int var1, int var2, int var3) {
      int var4 = var3;
      if (var1 > 0 && var2 - var1 <= 3) {
         var4 = var3 - (4 - (var2 - var1));
      }

      return var4;
   }

   private double signedDistanceCircle(int var1, int var2, BlockPos var3, int var4, Random var5) {
      float var6 = 10.0F * Mth.clamp(var5.nextFloat(), 0.2F, 0.8F) / (float)var4;
      return (double)var6 + Math.pow((double)(var1 - var3.getX()), 2.0D) + Math.pow((double)(var2 - var3.getZ()), 2.0D) - Math.pow((double)var4, 2.0D);
   }

   private double signedDistanceEllipse(int var1, int var2, BlockPos var3, int var4, int var5, double var6) {
      return Math.pow(((double)(var1 - var3.getX()) * Math.cos(var6) - (double)(var2 - var3.getZ()) * Math.sin(var6)) / (double)var4, 2.0D) + Math.pow(((double)(var1 - var3.getX()) * Math.sin(var6) + (double)(var2 - var3.getZ()) * Math.cos(var6)) / (double)var5, 2.0D) - 1.0D;
   }

   private int heightDependentRadiusRound(Random var1, int var2, int var3, int var4) {
      float var5 = 3.5F - var1.nextFloat();
      float var6 = (1.0F - (float)Math.pow((double)var2, 2.0D) / ((float)var3 * var5)) * (float)var4;
      if (var3 > 15 + var1.nextInt(5)) {
         int var7 = var2 < 3 + var1.nextInt(6) ? var2 / 2 : var2;
         var6 = (1.0F - (float)var7 / ((float)var3 * var5 * 0.4F)) * (float)var4;
      }

      return Mth.ceil(var6 / 2.0F);
   }

   private int heightDependentRadiusEllipse(int var1, int var2, int var3) {
      float var4 = 1.0F;
      float var5 = (1.0F - (float)Math.pow((double)var1, 2.0D) / ((float)var2 * 1.0F)) * (float)var3;
      return Mth.ceil(var5 / 2.0F);
   }

   private int heightDependentRadiusSteep(Random var1, int var2, int var3, int var4) {
      float var5 = 1.0F + var1.nextFloat() / 2.0F;
      float var6 = (1.0F - (float)var2 / ((float)var3 * var5)) * (float)var4;
      return Mth.ceil(var6 / 2.0F);
   }

   private static boolean isIcebergState(BlockState var0) {
      return var0.is(Blocks.PACKED_ICE) || var0.is(Blocks.SNOW_BLOCK) || var0.is(Blocks.BLUE_ICE);
   }

   private boolean belowIsAir(BlockGetter var1, BlockPos var2) {
      return var1.getBlockState(var2.below()).getMaterial() == Material.AIR;
   }

   private void smooth(LevelAccessor var1, BlockPos var2, int var3, int var4, boolean var5, int var6) {
      int var7 = var5 ? var6 : var3 / 2;

      for(int var8 = -var7; var8 <= var7; ++var8) {
         for(int var9 = -var7; var9 <= var7; ++var9) {
            for(int var10 = 0; var10 <= var4; ++var10) {
               BlockPos var11 = var2.offset(var8, var10, var9);
               BlockState var12 = var1.getBlockState(var11);
               if (isIcebergState(var12) || var12.is(Blocks.SNOW)) {
                  if (this.belowIsAir(var1, var11)) {
                     this.setBlock(var1, var11, Blocks.AIR.defaultBlockState());
                     this.setBlock(var1, var11.above(), Blocks.AIR.defaultBlockState());
                  } else if (isIcebergState(var12)) {
                     BlockState[] var13 = new BlockState[]{var1.getBlockState(var11.west()), var1.getBlockState(var11.east()), var1.getBlockState(var11.north()), var1.getBlockState(var11.south())};
                     int var14 = 0;
                     BlockState[] var15 = var13;
                     int var16 = var13.length;

                     for(int var17 = 0; var17 < var16; ++var17) {
                        BlockState var18 = var15[var17];
                        if (!isIcebergState(var18)) {
                           ++var14;
                        }
                     }

                     if (var14 >= 3) {
                        this.setBlock(var1, var11, Blocks.AIR.defaultBlockState());
                     }
                  }
               }
            }
         }
      }

   }
}
