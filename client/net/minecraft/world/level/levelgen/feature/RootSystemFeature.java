package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RootSystemFeature extends Feature<RootSystemConfiguration> {
   public RootSystemFeature(Codec<RootSystemConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<RootSystemConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      if (!var2.getBlockState(var3).isAir()) {
         return false;
      } else {
         RandomSource var4 = var1.random();
         BlockPos var5 = var1.origin();
         RootSystemConfiguration var6 = (RootSystemConfiguration)var1.config();
         BlockPos.MutableBlockPos var7 = var5.mutable();
         if (placeDirtAndTree(var2, var1.chunkGenerator(), var6, var4, var7, var5)) {
            placeRoots(var2, var6, var4, var5, var7);
         }

         return true;
      }
   }

   private static boolean spaceForTree(WorldGenLevel var0, RootSystemConfiguration var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      for(int var4 = 1; var4 <= var1.requiredVerticalSpaceForTree; ++var4) {
         var3.move(Direction.UP);
         BlockState var5 = var0.getBlockState(var3);
         if (!isAllowedTreeSpace(var5, var4, var1.allowedVerticalWaterForTree)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isAllowedTreeSpace(BlockState var0, int var1, int var2) {
      if (var0.isAir()) {
         return true;
      } else {
         int var3 = var1 + 1;
         return var3 <= var2 && var0.getFluidState().is(FluidTags.WATER);
      }
   }

   private static boolean placeDirtAndTree(WorldGenLevel var0, ChunkGenerator var1, RootSystemConfiguration var2, RandomSource var3, BlockPos.MutableBlockPos var4, BlockPos var5) {
      for(int var6 = 0; var6 < var2.rootColumnMaxHeight; ++var6) {
         var4.move(Direction.UP);
         if (var2.allowedTreePosition.test(var0, var4) && spaceForTree(var0, var2, var4)) {
            BlockPos var7 = var4.below();
            if (var0.getFluidState(var7).is(FluidTags.LAVA) || !var0.getBlockState(var7).isSolid()) {
               return false;
            }

            if (((PlacedFeature)var2.treeFeature.value()).place(var0, var1, var3, var4)) {
               placeDirt(var5, var5.getY() + var6, var0, var2, var3);
               return true;
            }
         }
      }

      return false;
   }

   private static void placeDirt(BlockPos var0, int var1, WorldGenLevel var2, RootSystemConfiguration var3, RandomSource var4) {
      int var5 = var0.getX();
      int var6 = var0.getZ();
      BlockPos.MutableBlockPos var7 = var0.mutable();

      for(int var8 = var0.getY(); var8 < var1; ++var8) {
         placeRootedDirt(var2, var3, var4, var5, var6, var7.set(var5, var8, var6));
      }

   }

   private static void placeRootedDirt(WorldGenLevel var0, RootSystemConfiguration var1, RandomSource var2, int var3, int var4, BlockPos.MutableBlockPos var5) {
      int var6 = var1.rootRadius;
      Predicate var7 = (var1x) -> {
         return var1x.is(var1.rootReplaceable);
      };

      for(int var8 = 0; var8 < var1.rootPlacementAttempts; ++var8) {
         var5.setWithOffset(var5, var2.nextInt(var6) - var2.nextInt(var6), 0, var2.nextInt(var6) - var2.nextInt(var6));
         if (var7.test(var0.getBlockState(var5))) {
            var0.setBlock(var5, var1.rootStateProvider.getState(var2, var5), 2);
         }

         var5.setX(var3);
         var5.setZ(var4);
      }

   }

   private static void placeRoots(WorldGenLevel var0, RootSystemConfiguration var1, RandomSource var2, BlockPos var3, BlockPos.MutableBlockPos var4) {
      int var5 = var1.hangingRootRadius;
      int var6 = var1.hangingRootsVerticalSpan;

      for(int var7 = 0; var7 < var1.hangingRootPlacementAttempts; ++var7) {
         var4.setWithOffset(var3, var2.nextInt(var5) - var2.nextInt(var5), var2.nextInt(var6) - var2.nextInt(var6), var2.nextInt(var5) - var2.nextInt(var5));
         if (var0.isEmptyBlock(var4)) {
            BlockState var8 = var1.hangingRootStateProvider.getState(var2, var4);
            if (var8.canSurvive(var0, var4) && var0.getBlockState(var4.above()).isFaceSturdy(var0, var4, Direction.DOWN)) {
               var0.setBlock(var4, var8, 2);
            }
         }
      }

   }
}
