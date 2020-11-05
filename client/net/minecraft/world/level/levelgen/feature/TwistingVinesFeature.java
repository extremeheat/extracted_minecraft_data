package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TwistingVinesFeature extends Feature<NoneFeatureConfiguration> {
   public TwistingVinesFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      return place(var1, var3, var4, 8, 4, 8);
   }

   public static boolean place(LevelAccessor var0, Random var1, BlockPos var2, int var3, int var4, int var5) {
      if (isInvalidPlacementLocation(var0, var2)) {
         return false;
      } else {
         placeTwistingVines(var0, var1, var2, var3, var4, var5);
         return true;
      }
   }

   private static void placeTwistingVines(LevelAccessor var0, Random var1, BlockPos var2, int var3, int var4, int var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(int var7 = 0; var7 < var3 * var3; ++var7) {
         var6.set(var2).move(Mth.nextInt(var1, -var3, var3), Mth.nextInt(var1, -var4, var4), Mth.nextInt(var1, -var3, var3));
         if (findFirstAirBlockAboveGround(var0, var6) && !isInvalidPlacementLocation(var0, var6)) {
            int var8 = Mth.nextInt(var1, 1, var5);
            if (var1.nextInt(6) == 0) {
               var8 *= 2;
            }

            if (var1.nextInt(5) == 0) {
               var8 = 1;
            }

            boolean var9 = true;
            boolean var10 = true;
            placeWeepingVinesColumn(var0, var1, var6, var8, 17, 25);
         }
      }

   }

   private static boolean findFirstAirBlockAboveGround(LevelAccessor var0, BlockPos.MutableBlockPos var1) {
      do {
         var1.move(0, -1, 0);
         if (Level.isOutsideBuildHeight(var1)) {
            return false;
         }
      } while(var0.getBlockState(var1).isAir());

      var1.move(0, 1, 0);
      return true;
   }

   public static void placeWeepingVinesColumn(LevelAccessor var0, Random var1, BlockPos.MutableBlockPos var2, int var3, int var4, int var5) {
      for(int var6 = 1; var6 <= var3; ++var6) {
         if (var0.isEmptyBlock(var2)) {
            if (var6 == var3 || !var0.isEmptyBlock(var2.above())) {
               var0.setBlock(var2, (BlockState)Blocks.TWISTING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(var1, var4, var5)), 2);
               break;
            }

            var0.setBlock(var2, Blocks.TWISTING_VINES_PLANT.defaultBlockState(), 2);
         }

         var2.move(Direction.UP);
      }

   }

   private static boolean isInvalidPlacementLocation(LevelAccessor var0, BlockPos var1) {
      if (!var0.isEmptyBlock(var1)) {
         return true;
      } else {
         BlockState var2 = var0.getBlockState(var1.below());
         return !var2.is(Blocks.NETHERRACK) && !var2.is(Blocks.WARPED_NYLIUM) && !var2.is(Blocks.WARPED_WART_BLOCK);
      }
   }
}
