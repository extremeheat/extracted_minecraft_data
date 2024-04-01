package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;

public class PotatoBudsFeature extends Feature<TwistingVinesConfig> {
   public PotatoBudsFeature(Codec<TwistingVinesConfig> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<TwistingVinesConfig> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      if (isInvalidPlacementLocation(var2, var3)) {
         return false;
      } else {
         RandomSource var4 = var1.random();
         TwistingVinesConfig var5 = (TwistingVinesConfig)var1.config();
         int var6 = var5.spreadWidth();
         int var7 = var5.spreadHeight();
         int var8 = var5.maxHeight();
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

         for(int var10 = 0; var10 < var6 * var6; ++var10) {
            var9.set(var3).move(Mth.nextInt(var4, -var6, var6), Mth.nextInt(var4, -var7, var7), Mth.nextInt(var4, -var6, var6));
            if (findFirstAirBlockAboveGround(var2, var9) && !isInvalidPlacementLocation(var2, var9)) {
               int var11 = Mth.nextInt(var4, 1, var8);
               if (var4.nextInt(6) == 0) {
                  var11 *= 2;
               }

               if (var4.nextInt(5) == 0) {
                  var11 = 1;
               }

               placeWeepingVinesColumn(var2, var4, var9, var11);
            }
         }

         return true;
      }
   }

   private static boolean findFirstAirBlockAboveGround(LevelAccessor var0, BlockPos.MutableBlockPos var1) {
      do {
         var1.move(0, -1, 0);
         if (var0.isOutsideBuildHeight(var1)) {
            return false;
         }
      } while(var0.getBlockState(var1).isAir());

      var1.move(0, 1, 0);
      return true;
   }

   public static void placeWeepingVinesColumn(LevelAccessor var0, RandomSource var1, BlockPos.MutableBlockPos var2, int var3) {
      var0.setBlock(var2, Blocks.POTATO_BUD.defaultBlockState(), 3);
      DripstoneUtilsFlex.growPointedDripstone(Blocks.POTATO_BUD, var0, var2, Direction.UP, var3, false);
   }

   private static boolean isInvalidPlacementLocation(LevelAccessor var0, BlockPos var1) {
      if (!var0.isEmptyBlock(var1)) {
         return true;
      } else {
         BlockState var2 = var0.getBlockState(var1.below());
         return !var2.is(Blocks.CORRUPTED_PEELGRASS_BLOCK);
      }
   }
}
