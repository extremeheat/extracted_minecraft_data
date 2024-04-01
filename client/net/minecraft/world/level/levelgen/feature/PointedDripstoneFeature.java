package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

public class PointedDripstoneFeature extends Feature<PointedDripstoneConfiguration> {
   public PointedDripstoneFeature(Codec<PointedDripstoneConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<PointedDripstoneConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      RandomSource var4 = var1.random();
      PointedDripstoneConfiguration var5 = (PointedDripstoneConfiguration)var1.config();
      Optional var6 = getTipDirection(var2, var3, var4);
      if (var6.isEmpty()) {
         return false;
      } else {
         BlockPos var7 = var3.relative(((Direction)var6.get()).getOpposite());
         createPatchOfDripstoneBlocks(var2, var4, var7, var5);
         int var8 = var4.nextFloat() < var5.chanceOfTallerDripstone && DripstoneUtils.isEmptyOrWater(var2.getBlockState(var3.relative((Direction)var6.get())))
            ? 2
            : 1;
         DripstoneUtils.growPointedDripstone(var2, var3, (Direction)var6.get(), var8, false);
         return true;
      }
   }

   private static Optional<Direction> getTipDirection(LevelAccessor var0, BlockPos var1, RandomSource var2) {
      PointedDripstoneBlock var3 = (PointedDripstoneBlock)Blocks.POINTED_DRIPSTONE;
      boolean var4 = DripstoneUtils.isDripstoneBase(var0.getBlockState(var1.above()));
      boolean var5 = DripstoneUtils.isDripstoneBase(var0.getBlockState(var1.below()));
      if (var4 && var5) {
         return Optional.of(var2.nextBoolean() ? Direction.DOWN : Direction.UP);
      } else if (var4) {
         return Optional.of(Direction.DOWN);
      } else {
         return var5 ? Optional.of(Direction.UP) : Optional.empty();
      }
   }

   private static void createPatchOfDripstoneBlocks(LevelAccessor var0, RandomSource var1, BlockPos var2, PointedDripstoneConfiguration var3) {
      Block var4 = Blocks.POINTED_DRIPSTONE;
      DripstoneUtils.placeDripstoneBlockIfPossible(var0, var2);

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         if (!(var1.nextFloat() > var3.chanceOfDirectionalSpread)) {
            BlockPos var7 = var2.relative(var6);
            DripstoneUtils.placeDripstoneBlockIfPossible(var0, var7);
            if (!(var1.nextFloat() > var3.chanceOfSpreadRadius2)) {
               BlockPos var8 = var7.relative(Direction.getRandom(var1));
               DripstoneUtils.placeDripstoneBlockIfPossible(var0, var8);
               if (!(var1.nextFloat() > var3.chanceOfSpreadRadius3)) {
                  BlockPos var9 = var8.relative(Direction.getRandom(var1));
                  DripstoneUtils.placeDripstoneBlockIfPossible(var0, var9);
               }
            }
         }
      }
   }
}
