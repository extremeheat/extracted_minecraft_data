package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BlueIceFeature extends Feature<NoneFeatureConfiguration> {
   public BlueIceFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();
      if (var2.getY() > var3.getSeaLevel() - 1) {
         return false;
      } else if (!var3.getBlockState(var2).is(Blocks.WATER) && !var3.getBlockState(var2.below()).is(Blocks.WATER)) {
         return false;
      } else {
         boolean var5 = false;
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         int var8;
         for(var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            if (var9 != Direction.DOWN && var3.getBlockState(var2.relative(var9)).is(Blocks.PACKED_ICE)) {
               var5 = true;
               break;
            }
         }

         if (!var5) {
            return false;
         } else {
            var3.setBlock(var2, Blocks.BLUE_ICE.defaultBlockState(), 2);

            for(int var16 = 0; var16 < 200; ++var16) {
               var7 = var4.nextInt(5) - var4.nextInt(6);
               var8 = 3;
               if (var7 < 2) {
                  var8 += var7 / 2;
               }

               if (var8 >= 1) {
                  BlockPos var17 = var2.offset(var4.nextInt(var8) - var4.nextInt(var8), var7, var4.nextInt(var8) - var4.nextInt(var8));
                  BlockState var10 = var3.getBlockState(var17);
                  if (var10.isAir() || var10.is(Blocks.WATER) || var10.is(Blocks.PACKED_ICE) || var10.is(Blocks.ICE)) {
                     Direction[] var11 = Direction.values();
                     int var12 = var11.length;

                     for(int var13 = 0; var13 < var12; ++var13) {
                        Direction var14 = var11[var13];
                        BlockState var15 = var3.getBlockState(var17.relative(var14));
                        if (var15.is(Blocks.BLUE_ICE)) {
                           var3.setBlock(var17, Blocks.BLUE_ICE.defaultBlockState(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
