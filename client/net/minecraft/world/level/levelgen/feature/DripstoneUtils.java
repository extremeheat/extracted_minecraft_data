package net.minecraft.world.level.levelgen.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

public class DripstoneUtils {
   public DripstoneUtils() {
      super();
   }

   protected static double getDripstoneHeight(double var0, double var2, double var4, double var6) {
      if (var0 < var6) {
         var0 = var6;
      }

      double var8 = 0.384;
      double var10 = var0 / var2 * 0.384;
      double var12 = 0.75 * Math.pow(var10, 1.3333333333333333);
      double var14 = Math.pow(var10, 0.6666666666666666);
      double var16 = 0.3333333333333333 * Math.log(var10);
      double var18 = var4 * (var12 - var14 - var16);
      var18 = Math.max(var18, 0.0);
      return var18 / 0.384 * var2;
   }

   protected static boolean isCircleMostlyEmbeddedInStone(WorldGenLevel var0, BlockPos var1, int var2) {
      if (isEmptyOrWaterOrLava(var0, var1)) {
         return false;
      } else {
         float var3 = 6.0F;
         float var4 = 6.0F / (float)var2;

         for (float var5 = 0.0F; var5 < 6.2831855F; var5 += var4) {
            int var6 = (int)(Mth.cos(var5) * (float)var2);
            int var7 = (int)(Mth.sin(var5) * (float)var2);
            if (isEmptyOrWaterOrLava(var0, var1.offset(var6, 0, var7))) {
               return false;
            }
         }

         return true;
      }
   }

   protected static boolean isEmptyOrWater(LevelAccessor var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, DripstoneUtils::isEmptyOrWater);
   }

   protected static boolean isEmptyOrWaterOrLava(LevelAccessor var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, DripstoneUtils::isEmptyOrWaterOrLava);
   }

   protected static void buildBaseToTipColumn(Direction var0, int var1, boolean var2, Consumer<BlockState> var3) {
      if (var1 >= 3) {
         var3.accept(createPointedDripstone(var0, DripstoneThickness.BASE));

         for (int var4 = 0; var4 < var1 - 3; var4++) {
            var3.accept(createPointedDripstone(var0, DripstoneThickness.MIDDLE));
         }
      }

      if (var1 >= 2) {
         var3.accept(createPointedDripstone(var0, DripstoneThickness.FRUSTUM));
      }

      if (var1 >= 1) {
         var3.accept(createPointedDripstone(var0, var2 ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
      }
   }

   protected static void growPointedDripstone(LevelAccessor var0, BlockPos var1, Direction var2, int var3, boolean var4) {
      if (isDripstoneBase(var0.getBlockState(var1.relative(var2.getOpposite())))) {
         BlockPos.MutableBlockPos var5 = var1.mutable();
         buildBaseToTipColumn(var2, var3, var4, var3x -> {
            if (var3x.is(Blocks.POINTED_DRIPSTONE)) {
               var3x = var3x.setValue(PointedDripstoneBlock.WATERLOGGED, Boolean.valueOf(var0.isWaterAt(var5)));
            }

            var0.setBlock(var5, var3x, 2);
            var5.move(var2);
         });
      }
   }

   protected static boolean placeDripstoneBlockIfPossible(LevelAccessor var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.is(BlockTags.DRIPSTONE_REPLACEABLE)) {
         var0.setBlock(var1, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }

   private static BlockState createPointedDripstone(Direction var0, DripstoneThickness var1) {
      return Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, var0).setValue(PointedDripstoneBlock.THICKNESS, var1);
   }

   public static boolean isDripstoneBaseOrLava(BlockState var0) {
      return isDripstoneBase(var0) || var0.is(Blocks.LAVA);
   }

   public static boolean isDripstoneBase(BlockState var0) {
      return var0.is(Blocks.DRIPSTONE_BLOCK) || var0.is(BlockTags.DRIPSTONE_REPLACEABLE);
   }

   public static boolean isEmptyOrWater(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER);
   }

   public static boolean isNeitherEmptyNorWater(BlockState var0) {
      return !var0.isAir() && !var0.is(Blocks.WATER);
   }

   public static boolean isEmptyOrWaterOrLava(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER) || var0.is(Blocks.LAVA);
   }
}
