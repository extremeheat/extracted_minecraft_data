package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BasaltPillarFeature extends Feature<NoneFeatureConfiguration> {
   public BasaltPillarFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();
      if (var3.isEmptyBlock(var2) && !var3.isEmptyBlock(var2.above())) {
         BlockPos.MutableBlockPos var5 = var2.mutable();
         BlockPos.MutableBlockPos var6 = var2.mutable();
         boolean var7 = true;
         boolean var8 = true;
         boolean var9 = true;
         boolean var10 = true;

         while(var3.isEmptyBlock(var5)) {
            if (var3.isOutsideBuildHeight(var5)) {
               return true;
            }

            var3.setBlock(var5, Blocks.BASALT.defaultBlockState(), 2);
            var7 = var7 && this.placeHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.NORTH));
            var8 = var8 && this.placeHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.SOUTH));
            var9 = var9 && this.placeHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.WEST));
            var10 = var10 && this.placeHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.EAST));
            var5.move(Direction.DOWN);
         }

         var5.move(Direction.UP);
         this.placeBaseHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.NORTH));
         this.placeBaseHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.SOUTH));
         this.placeBaseHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.WEST));
         this.placeBaseHangOff(var3, var4, var6.setWithOffset(var5, (Direction)Direction.EAST));
         var5.move(Direction.DOWN);
         BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

         for(int var12 = -3; var12 < 4; ++var12) {
            for(int var13 = -3; var13 < 4; ++var13) {
               int var14 = Mth.abs(var12) * Mth.abs(var13);
               if (var4.nextInt(10) < 10 - var14) {
                  var11.set(var5.offset(var12, 0, var13));
                  int var15 = 3;

                  while(var3.isEmptyBlock(var6.setWithOffset(var11, (Direction)Direction.DOWN))) {
                     var11.move(Direction.DOWN);
                     --var15;
                     if (var15 <= 0) {
                        break;
                     }
                  }

                  if (!var3.isEmptyBlock(var6.setWithOffset(var11, (Direction)Direction.DOWN))) {
                     var3.setBlock(var11, Blocks.BASALT.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void placeBaseHangOff(LevelAccessor var1, RandomSource var2, BlockPos var3) {
      if (var2.nextBoolean()) {
         var1.setBlock(var3, Blocks.BASALT.defaultBlockState(), 2);
      }

   }

   private boolean placeHangOff(LevelAccessor var1, RandomSource var2, BlockPos var3) {
      if (var2.nextInt(10) != 0) {
         var1.setBlock(var3, Blocks.BASALT.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }
}
