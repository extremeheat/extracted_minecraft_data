package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FallenTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

public class FallenTreeFeature extends Feature<FallenTreeConfiguration> {
   private static final int STUMP_HEIGHT = 1;
   private static final int STUMP_HEIGHT_PLUS_EMPTY_SPACE = 2;
   private static final int FALLEN_LOG_MAX_FALL_HEIGHT_TO_GROUND = 5;
   private static final int FALLEN_LOG_MAX_GROUND_GAP = 2;
   private static final int FALLEN_LOG_MAX_SPACE_FROM_STUMP = 2;
   private static final int BLOCK_UPDATE_FLAGS = 19;

   public FallenTreeFeature(Codec<FallenTreeConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<FallenTreeConfiguration> var1) {
      this.placeFallenTree((FallenTreeConfiguration)var1.config(), var1.origin(), var1.level(), var1.random());
      return true;
   }

   private void placeFallenTree(FallenTreeConfiguration var1, BlockPos var2, WorldGenLevel var3, RandomSource var4) {
      this.placeStump(var1, var3, var4, var2.mutable());
      Direction var5 = Direction.Plane.HORIZONTAL.getRandomDirection(var4);
      int var6 = var1.logLength.sample(var4) - 2;
      BlockPos.MutableBlockPos var7 = var2.relative(var5, 2 + var4.nextInt(2)).mutable();
      this.setGroundHeightForFallenLogStartPos(var3, var7);
      if (this.canPlaceEntireFallenLog(var3, var6, var7, var5)) {
         this.placeFallenLog(var1, var3, var4, var6, var7, var5);
      }

   }

   private void setGroundHeightForFallenLogStartPos(WorldGenLevel var1, BlockPos.MutableBlockPos var2) {
      var2.move(Direction.UP, 1);

      for(int var3 = 0; var3 < 6; ++var3) {
         if (this.mayPlaceOn(var1, var2)) {
            return;
         }

         var2.move(Direction.DOWN);
      }

   }

   private void placeStump(FallenTreeConfiguration var1, WorldGenLevel var2, RandomSource var3, BlockPos.MutableBlockPos var4) {
      BlockPos var5 = placeLogBlock(var1, var2, var3, var4, Function.identity());
      this.decorateLogs(var2, var3, Set.of(var5), var1.stumpDecorators);
   }

   private boolean canPlaceEntireFallenLog(WorldGenLevel var1, int var2, BlockPos.MutableBlockPos var3, Direction var4) {
      int var5 = 0;

      for(int var6 = 0; var6 < var2; ++var6) {
         if (!TreeFeature.validTreePos(var1, var3)) {
            return false;
         }

         if (!this.isOverSolidGround(var1, var3)) {
            ++var5;
            if (var5 > 2) {
               return false;
            }
         } else {
            var5 = 0;
         }

         var3.move(var4);
      }

      var3.move(var4.getOpposite(), var2);
      return true;
   }

   private void placeFallenLog(FallenTreeConfiguration var1, WorldGenLevel var2, RandomSource var3, int var4, BlockPos.MutableBlockPos var5, Direction var6) {
      HashSet var7 = new HashSet();

      for(int var8 = 0; var8 < var4; ++var8) {
         var7.add(placeLogBlock(var1, var2, var3, var5, getSidewaysStateModifier(var6)));
         var5.move(var6);
      }

      this.decorateLogs(var2, var3, var7, var1.logDecorators);
   }

   private boolean mayPlaceOn(LevelAccessor var1, BlockPos var2) {
      return TreeFeature.validTreePos(var1, var2) && this.isOverSolidGround(var1, var2);
   }

   private boolean isOverSolidGround(LevelAccessor var1, BlockPos var2) {
      return var1.getBlockState(var2.below()).isFaceSturdy(var1, var2, Direction.UP);
   }

   private static BlockPos placeLogBlock(FallenTreeConfiguration var0, WorldGenLevel var1, RandomSource var2, BlockPos.MutableBlockPos var3, Function<BlockState, BlockState> var4) {
      var1.setBlock(var3, (BlockState)var4.apply(var0.trunkProvider.getState(var2, var3)), 19);
      return var3.immutable();
   }

   private void decorateLogs(WorldGenLevel var1, RandomSource var2, Set<BlockPos> var3, List<TreeDecorator> var4) {
      if (!var4.isEmpty()) {
         TreeDecorator.Context var5 = new TreeDecorator.Context(var1, this.getDecorationSetter(var1), var2, var3, Set.of(), Set.of());
         var4.forEach((var1x) -> var1x.place(var5));
      }

   }

   private BiConsumer<BlockPos, BlockState> getDecorationSetter(WorldGenLevel var1) {
      return (var1x, var2) -> var1.setBlock(var1x, var2, 19);
   }

   private static Function<BlockState, BlockState> getSidewaysStateModifier(Direction var0) {
      return (var1) -> (BlockState)var1.trySetValue(RotatedPillarBlock.AXIS, var0.getAxis());
   }
}
