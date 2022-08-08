package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

public class MultifaceGrowthFeature extends Feature<MultifaceGrowthConfiguration> {
   public MultifaceGrowthFeature(Codec<MultifaceGrowthConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<MultifaceGrowthConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      RandomSource var4 = var1.random();
      MultifaceGrowthConfiguration var5 = (MultifaceGrowthConfiguration)var1.config();
      if (!isAirOrWater(var2.getBlockState(var3))) {
         return false;
      } else {
         List var6 = var5.getShuffledDirections(var4);
         if (placeGrowthIfPossible(var2, var3, var2.getBlockState(var3), var5, var4, var6)) {
            return true;
         } else {
            BlockPos.MutableBlockPos var7 = var3.mutable();
            Iterator var8 = var6.iterator();

            while(var8.hasNext()) {
               Direction var9 = (Direction)var8.next();
               var7.set(var3);
               List var10 = var5.getShuffledDirectionsExcept(var4, var9.getOpposite());

               for(int var11 = 0; var11 < var5.searchRange; ++var11) {
                  var7.setWithOffset(var3, (Direction)var9);
                  BlockState var12 = var2.getBlockState(var7);
                  if (!isAirOrWater(var12) && !var12.is(var5.placeBlock)) {
                     break;
                  }

                  if (placeGrowthIfPossible(var2, var7, var12, var5, var4, var10)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public static boolean placeGrowthIfPossible(WorldGenLevel var0, BlockPos var1, BlockState var2, MultifaceGrowthConfiguration var3, RandomSource var4, List<Direction> var5) {
      BlockPos.MutableBlockPos var6 = var1.mutable();
      Iterator var7 = var5.iterator();

      Direction var8;
      BlockState var9;
      do {
         if (!var7.hasNext()) {
            return false;
         }

         var8 = (Direction)var7.next();
         var9 = var0.getBlockState(var6.setWithOffset(var1, (Direction)var8));
      } while(!var9.is(var3.canBePlacedOn));

      BlockState var10 = var3.placeBlock.getStateForPlacement(var2, var0, var1, var8);
      if (var10 == null) {
         return false;
      } else {
         var0.setBlock(var1, var10, 3);
         var0.getChunk(var1).markPosForPostprocessing(var1);
         if (var4.nextFloat() < var3.chanceOfSpreading) {
            var3.placeBlock.getSpreader().spreadFromFaceTowardRandomDirection(var10, var0, var1, var8, var4, true);
         }

         return true;
      }
   }

   private static boolean isAirOrWater(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER);
   }
}
