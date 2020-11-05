package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BasaltPillarFeature extends Feature<NoneFeatureConfiguration> {
   public BasaltPillarFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      if (var1.isEmptyBlock(var4) && !var1.isEmptyBlock(var4.above())) {
         BlockPos.MutableBlockPos var6 = var4.mutable();
         BlockPos.MutableBlockPos var7 = var4.mutable();
         boolean var8 = true;
         boolean var9 = true;
         boolean var10 = true;
         boolean var11 = true;

         while(var1.isEmptyBlock(var6)) {
            if (var1.isOutsideBuildHeight(var6)) {
               return true;
            }

            var1.setBlock(var6, Blocks.BASALT.defaultBlockState(), 2);
            var8 = var8 && this.placeHangOff(var1, var3, var7.setWithOffset(var6, Direction.NORTH));
            var9 = var9 && this.placeHangOff(var1, var3, var7.setWithOffset(var6, Direction.SOUTH));
            var10 = var10 && this.placeHangOff(var1, var3, var7.setWithOffset(var6, Direction.WEST));
            var11 = var11 && this.placeHangOff(var1, var3, var7.setWithOffset(var6, Direction.EAST));
            var6.move(Direction.DOWN);
         }

         var6.move(Direction.UP);
         this.placeBaseHangOff(var1, var3, var7.setWithOffset(var6, Direction.NORTH));
         this.placeBaseHangOff(var1, var3, var7.setWithOffset(var6, Direction.SOUTH));
         this.placeBaseHangOff(var1, var3, var7.setWithOffset(var6, Direction.WEST));
         this.placeBaseHangOff(var1, var3, var7.setWithOffset(var6, Direction.EAST));
         var6.move(Direction.DOWN);
         BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

         for(int var13 = -3; var13 < 4; ++var13) {
            for(int var14 = -3; var14 < 4; ++var14) {
               int var15 = Mth.abs(var13) * Mth.abs(var14);
               if (var3.nextInt(10) < 10 - var15) {
                  var12.set(var6.offset(var13, 0, var14));
                  int var16 = 3;

                  while(var1.isEmptyBlock(var7.setWithOffset(var12, Direction.DOWN))) {
                     var12.move(Direction.DOWN);
                     --var16;
                     if (var16 <= 0) {
                        break;
                     }
                  }

                  if (!var1.isEmptyBlock(var7.setWithOffset(var12, Direction.DOWN))) {
                     var1.setBlock(var12, Blocks.BASALT.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void placeBaseHangOff(LevelAccessor var1, Random var2, BlockPos var3) {
      if (var2.nextBoolean()) {
         var1.setBlock(var3, Blocks.BASALT.defaultBlockState(), 2);
      }

   }

   private boolean placeHangOff(LevelAccessor var1, Random var2, BlockPos var3) {
      if (var2.nextInt(10) != 0) {
         var1.setBlock(var3, Blocks.BASALT.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }
}
