package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class IceSpikeFeature extends Feature<NoneFeatureConfiguration> {
   public IceSpikeFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      BlockPos var2 = var1.origin();
      RandomSource var3 = var1.random();

      WorldGenLevel var4;
      for(var4 = var1.level(); var4.isEmptyBlock(var2) && var2.getY() > var4.getMinBuildHeight() + 2; var2 = var2.below()) {
      }

      if (!var4.getBlockState(var2).is(Blocks.SNOW_BLOCK)) {
         return false;
      } else {
         var2 = var2.above(var3.nextInt(4));
         int var5 = var3.nextInt(4) + 7;
         int var6 = var5 / 4 + var3.nextInt(2);
         if (var6 > 1 && var3.nextInt(60) == 0) {
            var2 = var2.above(10 + var3.nextInt(30));
         }

         int var7;
         int var9;
         for(var7 = 0; var7 < var5; ++var7) {
            float var8 = (1.0F - (float)var7 / (float)var5) * (float)var6;
            var9 = Mth.ceil(var8);

            for(int var10 = -var9; var10 <= var9; ++var10) {
               float var11 = (float)Mth.abs(var10) - 0.25F;

               for(int var12 = -var9; var12 <= var9; ++var12) {
                  float var13 = (float)Mth.abs(var12) - 0.25F;
                  if ((var10 == 0 && var12 == 0 || !(var11 * var11 + var13 * var13 > var8 * var8)) && (var10 != -var9 && var10 != var9 && var12 != -var9 && var12 != var9 || !(var3.nextFloat() > 0.75F))) {
                     BlockState var14 = var4.getBlockState(var2.offset(var10, var7, var12));
                     if (var14.isAir() || isDirt(var14) || var14.is(Blocks.SNOW_BLOCK) || var14.is(Blocks.ICE)) {
                        this.setBlock(var4, var2.offset(var10, var7, var12), Blocks.PACKED_ICE.defaultBlockState());
                     }

                     if (var7 != 0 && var9 > 1) {
                        var14 = var4.getBlockState(var2.offset(var10, -var7, var12));
                        if (var14.isAir() || isDirt(var14) || var14.is(Blocks.SNOW_BLOCK) || var14.is(Blocks.ICE)) {
                           this.setBlock(var4, var2.offset(var10, -var7, var12), Blocks.PACKED_ICE.defaultBlockState());
                        }
                     }
                  }
               }
            }
         }

         var7 = var6 - 1;
         if (var7 < 0) {
            var7 = 0;
         } else if (var7 > 1) {
            var7 = 1;
         }

         for(int var15 = -var7; var15 <= var7; ++var15) {
            for(var9 = -var7; var9 <= var7; ++var9) {
               BlockPos var16 = var2.offset(var15, -1, var9);
               int var17 = 50;
               if (Math.abs(var15) == 1 && Math.abs(var9) == 1) {
                  var17 = var3.nextInt(5);
               }

               while(var16.getY() > 50) {
                  BlockState var18 = var4.getBlockState(var16);
                  if (!var18.isAir() && !isDirt(var18) && !var18.is(Blocks.SNOW_BLOCK) && !var18.is(Blocks.ICE) && !var18.is(Blocks.PACKED_ICE)) {
                     break;
                  }

                  this.setBlock(var4, var16, Blocks.PACKED_ICE.defaultBlockState());
                  var16 = var16.below();
                  --var17;
                  if (var17 <= 0) {
                     var16 = var16.below(var3.nextInt(5) + 1);
                     var17 = var3.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
