package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;

public class DripstoneClusterFeature extends Feature<DripstoneClusterConfiguration> {
   public DripstoneClusterFeature(Codec<DripstoneClusterConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<DripstoneClusterConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      DripstoneClusterConfiguration var4 = (DripstoneClusterConfiguration)var1.config();
      RandomSource var5 = var1.random();
      if (!DripstoneUtils.isEmptyOrWater(var2, var3)) {
         return false;
      } else {
         int var6 = var4.height.sample(var5);
         float var7 = var4.wetness.sample(var5);
         float var8 = var4.density.sample(var5);
         int var9 = var4.radius.sample(var5);
         int var10 = var4.radius.sample(var5);

         for (int var11 = -var9; var11 <= var9; var11++) {
            for (int var12 = -var10; var12 <= var10; var12++) {
               double var13 = this.getChanceOfStalagmiteOrStalactite(var9, var10, var11, var12, var4);
               BlockPos var15 = var3.offset(var11, 0, var12);
               this.placeColumn(var2, var5, var15, var11, var12, var7, var13, var6, var8, var4);
            }
         }

         return true;
      }
   }

   private void placeColumn(
      WorldGenLevel var1,
      RandomSource var2,
      BlockPos var3,
      int var4,
      int var5,
      float var6,
      double var7,
      int var9,
      float var10,
      DripstoneClusterConfiguration var11
   ) {
      Optional var12 = Column.scan(var1, var3, var11.floorToCeilingSearchRange, DripstoneUtils::isEmptyOrWater, DripstoneUtils::isNeitherEmptyNorWater);
      if (!var12.isEmpty()) {
         OptionalInt var13 = ((Column)var12.get()).getCeiling();
         OptionalInt var14 = ((Column)var12.get()).getFloor();
         if (!var13.isEmpty() || !var14.isEmpty()) {
            boolean var15 = var2.nextFloat() < var6;
            Column var16;
            if (var15 && var14.isPresent() && this.canPlacePool(var1, var3.atY(var14.getAsInt()))) {
               int var17 = var14.getAsInt();
               var16 = ((Column)var12.get()).withFloor(OptionalInt.of(var17 - 1));
               var1.setBlock(var3.atY(var17), Blocks.WATER.defaultBlockState(), 2);
            } else {
               var16 = (Column)var12.get();
            }

            OptionalInt var30 = var16.getFloor();
            boolean var19 = var2.nextDouble() < var7;
            int var18;
            if (var13.isPresent() && var19 && !this.isLava(var1, var3.atY(var13.getAsInt()))) {
               int var20 = var11.dripstoneBlockLayerThickness.sample(var2);
               this.replaceBlocksWithDripstoneBlocks(var1, var3.atY(var13.getAsInt()), var20, Direction.UP);
               int var21;
               if (var30.isPresent()) {
                  var21 = Math.min(var9, var13.getAsInt() - var30.getAsInt());
               } else {
                  var21 = var9;
               }

               var18 = this.getDripstoneHeight(var2, var4, var5, var10, var21, var11);
            } else {
               var18 = 0;
            }

            boolean var32 = var2.nextDouble() < var7;
            int var31;
            if (var30.isPresent() && var32 && !this.isLava(var1, var3.atY(var30.getAsInt()))) {
               int var22 = var11.dripstoneBlockLayerThickness.sample(var2);
               this.replaceBlocksWithDripstoneBlocks(var1, var3.atY(var30.getAsInt()), var22, Direction.DOWN);
               if (var13.isPresent()) {
                  var31 = Math.max(
                     0, var18 + Mth.randomBetweenInclusive(var2, -var11.maxStalagmiteStalactiteHeightDiff, var11.maxStalagmiteStalactiteHeightDiff)
                  );
               } else {
                  var31 = this.getDripstoneHeight(var2, var4, var5, var10, var9, var11);
               }
            } else {
               var31 = 0;
            }

            int var23;
            int var33;
            if (var13.isPresent() && var30.isPresent() && var13.getAsInt() - var18 <= var30.getAsInt() + var31) {
               int var24 = var30.getAsInt();
               int var25 = var13.getAsInt();
               int var26 = Math.max(var25 - var18, var24 + 1);
               int var27 = Math.min(var24 + var31, var25 - 1);
               int var28 = Mth.randomBetweenInclusive(var2, var26, var27 + 1);
               int var29 = var28 - 1;
               var33 = var25 - var28;
               var23 = var29 - var24;
            } else {
               var33 = var18;
               var23 = var31;
            }

            boolean var34 = var2.nextBoolean() && var33 > 0 && var23 > 0 && var16.getHeight().isPresent() && var33 + var23 == var16.getHeight().getAsInt();
            if (var13.isPresent()) {
               DripstoneUtils.growPointedDripstone(var1, var3.atY(var13.getAsInt() - 1), Direction.DOWN, var33, var34);
            }

            if (var30.isPresent()) {
               DripstoneUtils.growPointedDripstone(var1, var3.atY(var30.getAsInt() + 1), Direction.UP, var23, var34);
            }
         }
      }
   }

   private boolean isLava(LevelReader var1, BlockPos var2) {
      return var1.getBlockState(var2).is(Blocks.LAVA);
   }

   private int getDripstoneHeight(RandomSource var1, int var2, int var3, float var4, int var5, DripstoneClusterConfiguration var6) {
      if (var1.nextFloat() > var4) {
         return 0;
      } else {
         int var7 = Math.abs(var2) + Math.abs(var3);
         float var8 = (float)Mth.clampedMap((double)var7, 0.0, (double)var6.maxDistanceFromCenterAffectingHeightBias, (double)var5 / 2.0, 0.0);
         return (int)randomBetweenBiased(var1, 0.0F, (float)var5, var8, (float)var6.heightDeviation);
      }
   }

   private boolean canPlacePool(WorldGenLevel var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      if (!var3.is(Blocks.WATER) && !var3.is(Blocks.DRIPSTONE_BLOCK) && !var3.is(Blocks.POINTED_DRIPSTONE)) {
         if (var1.getBlockState(var2.above()).getFluidState().is(FluidTags.WATER)) {
            return false;
         } else {
            for (Direction var5 : Direction.Plane.HORIZONTAL) {
               if (!this.canBeAdjacentToWater(var1, var2.relative(var5))) {
                  return false;
               }
            }

            return this.canBeAdjacentToWater(var1, var2.below());
         }
      } else {
         return false;
      }
   }

   private boolean canBeAdjacentToWater(LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      return var3.is(BlockTags.BASE_STONE_OVERWORLD) || var3.getFluidState().is(FluidTags.WATER);
   }

   private void replaceBlocksWithDripstoneBlocks(WorldGenLevel var1, BlockPos var2, int var3, Direction var4) {
      BlockPos.MutableBlockPos var5 = var2.mutable();

      for (int var6 = 0; var6 < var3; var6++) {
         if (!DripstoneUtils.placeDripstoneBlockIfPossible(var1, var5)) {
            return;
         }

         var5.move(var4);
      }
   }

   private double getChanceOfStalagmiteOrStalactite(int var1, int var2, int var3, int var4, DripstoneClusterConfiguration var5) {
      int var6 = var1 - Math.abs(var3);
      int var7 = var2 - Math.abs(var4);
      int var8 = Math.min(var6, var7);
      return (double)Mth.clampedMap(
         (float)var8, 0.0F, (float)var5.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn, var5.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0F
      );
   }

   private static float randomBetweenBiased(RandomSource var0, float var1, float var2, float var3, float var4) {
      return ClampedNormalFloat.sample(var0, var3, var4, var1, var2);
   }
}
