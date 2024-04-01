package net.minecraft.world.level.levelgen.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

public class DripstoneUtilsFlex {
   public DripstoneUtilsFlex() {
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

         for(float var5 = 0.0F; var5 < 6.2831855F; var5 += var4) {
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
      return var0.isStateAtPosition(var1, DripstoneUtilsFlex::isEmptyOrWater);
   }

   protected static boolean isEmptyOrWaterOrLava(LevelAccessor var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, DripstoneUtilsFlex::isEmptyOrWaterOrLava);
   }

   protected static void buildBaseToTipColumn(PointedDripstoneBlock var0, Direction var1, int var2, boolean var3, Consumer<BlockState> var4) {
      if (var2 >= 3) {
         var4.accept(createPointedDripstone(var0, var1, DripstoneThickness.BASE));

         for(int var5 = 0; var5 < var2 - 3; ++var5) {
            var4.accept(createPointedDripstone(var0, var1, DripstoneThickness.MIDDLE));
         }
      }

      if (var2 >= 2) {
         var4.accept(createPointedDripstone(var0, var1, DripstoneThickness.FRUSTUM));
      }

      if (var2 >= 1) {
         var4.accept(createPointedDripstone(var0, var1, var3 ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
      }
   }

   protected static void growPointedDripstone(Block var0, LevelAccessor var1, BlockPos var2, Direction var3, int var4, boolean var5) {
      if (var0 instanceof PointedDripstoneBlock var6) {
         if (isDripstoneBase((PointedDripstoneBlock)var6, var1.getBlockState(var2.relative(var3.getOpposite())))) {
            BlockPos.MutableBlockPos var7 = var2.mutable();
            buildBaseToTipColumn((PointedDripstoneBlock)var6, var3, var4, var5, var3x -> {
               if (var3x.getBlock() instanceof PointedDripstoneBlock) {
                  var3x = var3x.setValue(PointedDripstoneBlock.WATERLOGGED, Boolean.valueOf(var1.isWaterAt(var7)));
               }

               var1.setBlock(var7, var3x, 2);
               var7.move(var3);
            });
         }
      }
   }

   protected static boolean placeDripstoneBlockIfPossible(LevelAccessor var0, BlockPos var1, Block var2) {
      BlockState var3 = var0.getBlockState(var1);
      if (var3.is(BlockTags.DRIPSTONE_REPLACEABLE)) {
         var0.setBlock(var1, var2.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }

   private static BlockState createPointedDripstone(PointedDripstoneBlock var0, Direction var1, DripstoneThickness var2) {
      return var0.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, var1).setValue(PointedDripstoneBlock.THICKNESS, var2);
   }

   public static boolean isDripstoneBaseOrLava(PointedDripstoneBlock var0, BlockState var1) {
      return isDripstoneBase(var0, var1) || var1.is(Blocks.LAVA);
   }

   public static boolean isDripstoneBase(PointedDripstoneBlock var0, BlockState var1) {
      return var0.isBase(var1) || var1.is(BlockTags.DRIPSTONE_REPLACEABLE);
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
