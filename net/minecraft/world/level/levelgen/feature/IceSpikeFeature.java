package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class IceSpikeFeature extends Feature {
   public IceSpikeFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      while(var1.isEmptyBlock(var4) && var4.getY() > 2) {
         var4 = var4.below();
      }

      if (var1.getBlockState(var4).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         var4 = var4.above(var3.nextInt(4));
         int var6 = var3.nextInt(4) + 7;
         int var7 = var6 / 4 + var3.nextInt(2);
         if (var7 > 1 && var3.nextInt(60) == 0) {
            var4 = var4.above(10 + var3.nextInt(30));
         }

         int var8;
         int var10;
         for(var8 = 0; var8 < var6; ++var8) {
            float var9 = (1.0F - (float)var8 / (float)var6) * (float)var7;
            var10 = Mth.ceil(var9);

            for(int var11 = -var10; var11 <= var10; ++var11) {
               float var12 = (float)Mth.abs(var11) - 0.25F;

               for(int var13 = -var10; var13 <= var10; ++var13) {
                  float var14 = (float)Mth.abs(var13) - 0.25F;
                  if ((var11 == 0 && var13 == 0 || var12 * var12 + var14 * var14 <= var9 * var9) && (var11 != -var10 && var11 != var10 && var13 != -var10 && var13 != var10 || var3.nextFloat() <= 0.75F)) {
                     BlockState var15 = var1.getBlockState(var4.offset(var11, var8, var13));
                     Block var16 = var15.getBlock();
                     if (var15.isAir() || isDirt(var16) || var16 == Blocks.SNOW_BLOCK || var16 == Blocks.ICE) {
                        this.setBlock(var1, var4.offset(var11, var8, var13), Blocks.PACKED_ICE.defaultBlockState());
                     }

                     if (var8 != 0 && var10 > 1) {
                        var15 = var1.getBlockState(var4.offset(var11, -var8, var13));
                        var16 = var15.getBlock();
                        if (var15.isAir() || isDirt(var16) || var16 == Blocks.SNOW_BLOCK || var16 == Blocks.ICE) {
                           this.setBlock(var1, var4.offset(var11, -var8, var13), Blocks.PACKED_ICE.defaultBlockState());
                        }
                     }
                  }
               }
            }
         }

         var8 = var7 - 1;
         if (var8 < 0) {
            var8 = 0;
         } else if (var8 > 1) {
            var8 = 1;
         }

         for(int var17 = -var8; var17 <= var8; ++var17) {
            for(var10 = -var8; var10 <= var8; ++var10) {
               BlockPos var18 = var4.offset(var17, -1, var10);
               int var19 = 50;
               if (Math.abs(var17) == 1 && Math.abs(var10) == 1) {
                  var19 = var3.nextInt(5);
               }

               while(var18.getY() > 50) {
                  BlockState var20 = var1.getBlockState(var18);
                  Block var21 = var20.getBlock();
                  if (!var20.isAir() && !isDirt(var21) && var21 != Blocks.SNOW_BLOCK && var21 != Blocks.ICE && var21 != Blocks.PACKED_ICE) {
                     break;
                  }

                  this.setBlock(var1, var18, Blocks.PACKED_ICE.defaultBlockState());
                  var18 = var18.below();
                  --var19;
                  if (var19 <= 0) {
                     var18 = var18.below(var3.nextInt(5) + 1);
                     var19 = var3.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
