package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class IceSpikeFeature extends Feature<NoneFeatureConfiguration> {
   public IceSpikeFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      while(var1.isEmptyBlock(var4) && var4.getY() > var1.getMinBuildHeight() + 2) {
         var4 = var4.below();
      }

      if (!var1.getBlockState(var4).is(Blocks.SNOW_BLOCK)) {
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
                     if (var15.isAir() || isDirt(var15) || var15.is(Blocks.SNOW_BLOCK) || var15.is(Blocks.ICE)) {
                        this.setBlock(var1, var4.offset(var11, var8, var13), Blocks.PACKED_ICE.defaultBlockState());
                     }

                     if (var8 != 0 && var10 > 1) {
                        var15 = var1.getBlockState(var4.offset(var11, -var8, var13));
                        if (var15.isAir() || isDirt(var15) || var15.is(Blocks.SNOW_BLOCK) || var15.is(Blocks.ICE)) {
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

         for(int var16 = -var8; var16 <= var8; ++var16) {
            for(var10 = -var8; var10 <= var8; ++var10) {
               BlockPos var17 = var4.offset(var16, -1, var10);
               int var18 = 50;
               if (Math.abs(var16) == 1 && Math.abs(var10) == 1) {
                  var18 = var3.nextInt(5);
               }

               while(var17.getY() > 50) {
                  BlockState var19 = var1.getBlockState(var17);
                  if (!var19.isAir() && !isDirt(var19) && !var19.is(Blocks.SNOW_BLOCK) && !var19.is(Blocks.ICE) && !var19.is(Blocks.PACKED_ICE)) {
                     break;
                  }

                  this.setBlock(var1, var17, Blocks.PACKED_ICE.defaultBlockState());
                  var17 = var17.below();
                  --var18;
                  if (var18 <= 0) {
                     var17 = var17.below(var3.nextInt(5) + 1);
                     var18 = var3.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
