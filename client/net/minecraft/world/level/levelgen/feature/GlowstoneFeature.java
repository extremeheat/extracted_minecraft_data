package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GlowstoneFeature extends Feature<NoneFeatureConfiguration> {
   public GlowstoneFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      RandomSource var4 = var1.random();
      if (!var2.isEmptyBlock(var3)) {
         return false;
      } else {
         BlockState var5 = var2.getBlockState(var3.above());
         if (!var5.is(Blocks.NETHERRACK) && !var5.is(Blocks.BASALT) && !var5.is(Blocks.BLACKSTONE)) {
            return false;
         } else {
            var2.setBlock(var3, Blocks.GLOWSTONE.defaultBlockState(), 2);

            for(int var6 = 0; var6 < 1500; ++var6) {
               BlockPos var7 = var3.offset(var4.nextInt(8) - var4.nextInt(8), -var4.nextInt(12), var4.nextInt(8) - var4.nextInt(8));
               if (var2.getBlockState(var7).isAir()) {
                  int var8 = 0;
                  Direction[] var9 = Direction.values();
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     Direction var12 = var9[var11];
                     if (var2.getBlockState(var7.relative(var12)).is(Blocks.GLOWSTONE)) {
                        ++var8;
                     }

                     if (var8 > 1) {
                        break;
                     }
                  }

                  if (var8 == 1) {
                     var2.setBlock(var7, Blocks.GLOWSTONE.defaultBlockState(), 2);
                  }
               }
            }

            return true;
         }
      }
   }
}
