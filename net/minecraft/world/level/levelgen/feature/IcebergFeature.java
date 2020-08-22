package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Material;

public class IcebergFeature extends Feature {
   public IcebergFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, BlockStateConfiguration var5) {
      var4 = new BlockPos(var4.getX(), var1.getSeaLevel(), var4.getZ());
      boolean var6 = var3.nextDouble() > 0.7D;
      BlockState var7 = var5.state;
      double var8 = var3.nextDouble() * 2.0D * 3.141592653589793D;
      int var10 = 11 - var3.nextInt(5);
      int var11 = 3 + var3.nextInt(3);
      boolean var12 = var3.nextDouble() > 0.7D;
      boolean var13 = true;
      int var14 = var12 ? var3.nextInt(6) + 6 : var3.nextInt(15) + 3;
      if (!var12 && var3.nextDouble() > 0.9D) {
         var14 += var3.nextInt(19) + 7;
      }

      int var15 = Math.min(var14 + var3.nextInt(11), 18);
      int var16 = Math.min(var14 + var3.nextInt(7) - var3.nextInt(5), 11);
      int var17 = var12 ? var10 : 11;

      int var18;
      int var19;
      int var20;
      int var21;
      for(var18 = -var17; var18 < var17; ++var18) {
         for(var19 = -var17; var19 < var17; ++var19) {
            for(var20 = 0; var20 < var14; ++var20) {
               var21 = var12 ? this.heightDependentRadiusEllipse(var20, var14, var16) : this.heightDependentRadiusRound(var3, var20, var14, var16);
               if (var12 || var18 < var21) {
                  this.generateIcebergBlock(var1, var3, var4, var14, var18, var20, var19, var21, var17, var12, var11, var8, var6, var7);
               }
            }
         }
      }

      this.smooth(var1, var4, var16, var14, var12, var10);

      for(var18 = -var17; var18 < var17; ++var18) {
         for(var19 = -var17; var19 < var17; ++var19) {
            for(var20 = -1; var20 > -var15; --var20) {
               var21 = var12 ? Mth.ceil((float)var17 * (1.0F - (float)Math.pow((double)var20, 2.0D) / ((float)var15 * 8.0F))) : var17;
               int var22 = this.heightDependentRadiusSteep(var3, -var20, var15, var16);
               if (var18 < var22) {
                  this.generateIcebergBlock(var1, var3, var4, var15, var18, var20, var19, var22, var21, var12, var11, var8, var6, var7);
               }
            }
         }
      }

      boolean var23 = var12 ? var3.nextDouble() > 0.1D : var3.nextDouble() > 0.7D;
      if (var23) {
         this.generateCutOut(var3, var1, var16, var14, var4, var12, var10, var8, var11);
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
               Block var18 = var4.getBlockState(var17).getBlock();
               if (this.isIcebergBlock(var18) || var18 == Blocks.SNOW_BLOCK) {
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
      if (var1.getBlockState(var2.above()).getBlock() == Blocks.SNOW) {
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
      Block var10 = var9.getBlock();
      if (var9.getMaterial() == Material.AIR || var10 == Blocks.SNOW_BLOCK || var10 == Blocks.ICE || var10 == Blocks.WATER) {
         boolean var11 = !var6 || var3.nextDouble() > 0.05D;
         int var12 = var6 ? 3 : 2;
         if (var7 && var10 != Blocks.WATER && (double)var4 <= (double)var3.nextInt(Math.max(1, var5 / var12)) + (double)var5 * 0.6D && var11) {
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

   private boolean isIcebergBlock(Block var1) {
      return var1 == Blocks.PACKED_ICE || var1 == Blocks.SNOW_BLOCK || var1 == Blocks.BLUE_ICE;
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
               Block var12 = var1.getBlockState(var11).getBlock();
               if (this.isIcebergBlock(var12) || var12 == Blocks.SNOW) {
                  if (this.belowIsAir(var1, var11)) {
                     this.setBlock(var1, var11, Blocks.AIR.defaultBlockState());
                     this.setBlock(var1, var11.above(), Blocks.AIR.defaultBlockState());
                  } else if (this.isIcebergBlock(var12)) {
                     Block[] var13 = new Block[]{var1.getBlockState(var11.west()).getBlock(), var1.getBlockState(var11.east()).getBlock(), var1.getBlockState(var11.north()).getBlock(), var1.getBlockState(var11.south()).getBlock()};
                     int var14 = 0;
                     Block[] var15 = var13;
                     int var16 = var13.length;

                     for(int var17 = 0; var17 < var16; ++var17) {
                        Block var18 = var15[var17];
                        if (!this.isIcebergBlock(var18)) {
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
