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
   public RootSystemFeature(final Codec<RootSystemConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<RootSystemConfiguration> context) {
      WorldGenLevel level = context.level();
      BlockPos origin = context.origin();
      if (!level.getBlockState(origin).isAir()) {
         return false;
      } else {
         RandomSource random = context.random();
         BlockPos pos = context.origin();
         RootSystemConfiguration config = context.config();
         BlockPos.MutableBlockPos workingPos = pos.mutable();
         if (placeDirtAndTree(level, context.chunkGenerator(), config, random, workingPos, pos)) {
            placeRoots(level, config, random, pos, workingPos);
         }

         return true;
      }
   }

   private static boolean spaceForTree(final WorldGenLevel level, final RootSystemConfiguration config, final BlockPos pos) {
      BlockPos.MutableBlockPos columnUpPos = pos.mutable();

      for(int i = 1; i <= config.requiredVerticalSpaceForTree; ++i) {
         columnUpPos.move(Direction.UP);
         BlockState state = level.getBlockState(columnUpPos);
         if (!isAllowedTreeSpace(state, i, config.allowedVerticalWaterForTree)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isAllowedTreeSpace(final BlockState state, final int blocksAboveOrigin, final int allowedVerticalWaterHeight) {
      if (state.isAir()) {
         return true;
      } else {
         int blocksAboveGround = blocksAboveOrigin + 1;
         return blocksAboveGround <= allowedVerticalWaterHeight && state.getFluidState().is(FluidTags.WATER);
      }
   }

   private static boolean placeDirtAndTree(final WorldGenLevel level, final ChunkGenerator generator, final RootSystemConfiguration config, final RandomSource random, final BlockPos.MutableBlockPos workingPos, final BlockPos pos) {
      for(int y = 0; y < config.rootColumnMaxHeight; ++y) {
         workingPos.move(Direction.UP);
         if (config.allowedTreePosition.test(level, workingPos) && spaceForTree(level, config, workingPos)) {
            BlockPos belowPos = workingPos.below();
            if (level.getFluidState(belowPos).is(FluidTags.LAVA) || !level.getBlockState(belowPos).isSolid()) {
               return false;
            }

            if (((PlacedFeature)config.treeFeature.value()).place(level, generator, random, workingPos)) {
               placeDirt(pos, pos.getY() + y, level, config, random);
               return true;
            }
         }
      }

      return false;
   }

   private static void placeDirt(final BlockPos origin, final int targetHeight, final WorldGenLevel level, final RootSystemConfiguration config, final RandomSource random) {
      int originX = origin.getX();
      int originZ = origin.getZ();
      BlockPos.MutableBlockPos workingPos = origin.mutable();

      for(int y = origin.getY(); y < targetHeight; ++y) {
         placeRootedDirt(level, config, random, originX, originZ, workingPos.set(originX, y, originZ));
      }

   }

   private static void placeRootedDirt(final WorldGenLevel level, final RootSystemConfiguration config, final RandomSource random, final int originX, final int originZ, final BlockPos.MutableBlockPos workingPos) {
      int rootRadius = config.rootRadius;
      Predicate<BlockState> stateTest = (s) -> s.is(config.rootReplaceable);

      for(int i = 0; i < config.rootPlacementAttempts; ++i) {
         workingPos.setWithOffset(workingPos, random.nextInt(rootRadius) - random.nextInt(rootRadius), 0, random.nextInt(rootRadius) - random.nextInt(rootRadius));
         if (stateTest.test(level.getBlockState(workingPos))) {
            level.setBlock(workingPos, config.rootStateProvider.getState(level, random, workingPos), 2);
         }

         workingPos.setX(originX);
         workingPos.setZ(originZ);
      }

   }

   private static void placeRoots(final WorldGenLevel level, final RootSystemConfiguration config, final RandomSource random, final BlockPos pos, final BlockPos.MutableBlockPos workingPos) {
      int rootRadius = config.hangingRootRadius;
      int verticalSpan = config.hangingRootsVerticalSpan;

      for(int i = 0; i < config.hangingRootPlacementAttempts; ++i) {
         workingPos.setWithOffset(pos, random.nextInt(rootRadius) - random.nextInt(rootRadius), random.nextInt(verticalSpan) - random.nextInt(verticalSpan), random.nextInt(rootRadius) - random.nextInt(rootRadius));
         if (level.isEmptyBlock(workingPos)) {
            BlockState targetState = config.hangingRootStateProvider.getState(level, random, workingPos);
            if (targetState.canSurvive(level, workingPos) && level.getBlockState(workingPos.above()).isFaceSturdy(level, workingPos, Direction.DOWN)) {
               level.setBlock(workingPos, targetState, 2);
            }
         }
      }

   }
}
