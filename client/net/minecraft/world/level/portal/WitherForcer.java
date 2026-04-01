package net.minecraft.world.level.portal;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

public class WitherForcer {
   private static final int SEARCH_RADIUS = 16;
   private static final int STRUCTURE_TOTAL_HEIGHT = 4;
   private static final int CROSS_BAR_HALF_WIDTH = 1;
   private static final int NOTHING_FOUND = -1;
   private final ServerLevel level;

   public WitherForcer(final ServerLevel level) {
      super();
      this.level = level;
   }

   public Optional<BlockUtil.FoundRectangle> createWither(final BlockPos origin, final Direction.Axis crossAxis) {
      Direction crossDirection = Direction.get(Direction.AxisDirection.POSITIVE, crossAxis);
      double closestFullDistanceSqr = -1.0;
      BlockPos closestFullPosition = null;
      double closestPartialDistanceSqr = -1.0;
      BlockPos closestPartialPosition = null;
      WorldBorder worldBorder = this.level.getWorldBorder();
      int maxPlaceableY = Math.min(this.level.getMaxY(), this.level.getMinY() + this.level.getLogicalHeight() - 1);
      BlockPos.MutableBlockPos mutable = origin.mutable();

      for(BlockPos.MutableBlockPos columnPos : BlockPos.spiralAround(origin, 16, Direction.EAST, Direction.SOUTH)) {
         int surfaceHeight = Math.min(maxPlaceableY, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, columnPos.getX(), columnPos.getZ()));
         if (worldBorder.isWithinBounds((BlockPos)columnPos) && worldBorder.isWithinBounds((BlockPos)columnPos.move(crossDirection, 1)) && worldBorder.isWithinBounds((BlockPos)columnPos.move(crossDirection.getOpposite(), 2))) {
            columnPos.move(crossDirection, 1);

            for(int y = surfaceHeight; y >= this.level.getMinY(); --y) {
               columnPos.setY(y);
               if (this.canWitherReplaceBlock(columnPos)) {
                  int firstEmptyY;
                  for(firstEmptyY = y; y > this.level.getMinY() && this.canWitherReplaceBlock(columnPos.move(Direction.DOWN)); --y) {
                  }

                  if (y + 4 - 1 <= maxPlaceableY) {
                     int availableHeight = firstEmptyY - y;
                     if (availableHeight <= 0 || availableHeight >= 4) {
                        columnPos.setY(y);
                        if (this.canHostWitherCenter(columnPos, mutable, crossDirection)) {
                           double distance = origin.distSqr(columnPos);
                           if (this.canHostWitherCrossBar(columnPos, mutable, crossDirection) && (closestFullDistanceSqr == -1.0 || closestFullDistanceSqr > distance)) {
                              closestFullDistanceSqr = distance;
                              closestFullPosition = columnPos.immutable();
                           }

                           if (closestFullDistanceSqr == -1.0 && (closestPartialDistanceSqr == -1.0 || closestPartialDistanceSqr > distance)) {
                              closestPartialDistanceSqr = distance;
                              closestPartialPosition = columnPos.immutable();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (closestFullDistanceSqr == -1.0 && closestPartialDistanceSqr != -1.0) {
         closestFullPosition = closestPartialPosition;
      }

      if (closestFullPosition == null) {
         int minStartY = Math.max(this.level.getMinY(), 70);
         int maxStartY = maxPlaceableY - 4;
         if (maxStartY < minStartY) {
            return Optional.empty();
         }

         BlockPos var21 = (new BlockPos(origin.getX(), Mth.clamp(origin.getY(), minStartY, maxStartY), origin.getZ())).immutable();
         closestFullPosition = worldBorder.clampToBounds(var21);
         this.clearSpaceForWither(closestFullPosition, mutable, crossDirection);
      }

      this.placeWitherStructure(closestFullPosition, mutable, crossDirection);
      return Optional.of(new BlockUtil.FoundRectangle(closestFullPosition.immutable(), 3, 3));
   }

   private boolean canHostWitherCenter(final BlockPos origin, final BlockPos.MutableBlockPos mutable, final Direction crossDirection) {
      if (!this.level.getBlockState(origin).isSolid()) {
         return false;
      } else {
         for(int dy = 1; dy <= 3; ++dy) {
            mutable.setWithOffset(origin, 0, dy, 0);
            if (!this.canWitherReplaceBlock(mutable)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canHostWitherCrossBar(final BlockPos origin, final BlockPos.MutableBlockPos mutable, final Direction crossDirection) {
      for(int offset = -1; offset <= 1; ++offset) {
         if (offset != 0) {
            for(int dy = 2; dy <= 3; ++dy) {
               mutable.setWithOffset(origin, crossDirection.getStepX() * offset, dy, crossDirection.getStepZ() * offset);
               if (!this.canWitherReplaceBlock(mutable)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void clearSpaceForWither(final BlockPos origin, final BlockPos.MutableBlockPos mutable, final Direction crossDirection) {
      for(int offset = -1; offset <= 1; ++offset) {
         mutable.setWithOffset(origin, crossDirection.getStepX() * offset, 0, crossDirection.getStepZ() * offset);
         this.level.setBlockAndUpdate(mutable, Blocks.SOUL_SAND.defaultBlockState());
      }

      for(int offset = -1; offset <= 1; ++offset) {
         for(int dy = 1; dy <= 3; ++dy) {
            mutable.setWithOffset(origin, crossDirection.getStepX() * offset, dy, crossDirection.getStepZ() * offset);
            this.level.setBlockAndUpdate(mutable, Blocks.AIR.defaultBlockState());
         }
      }

   }

   private void placeWitherStructure(final BlockPos origin, final BlockPos.MutableBlockPos mutable, final Direction crossDirection) {
      mutable.setWithOffset(origin, 0, 1, 0);
      this.level.setBlock(mutable, Blocks.SOUL_SAND.defaultBlockState(), 3);

      for(int offset = -1; offset <= 1; ++offset) {
         mutable.setWithOffset(origin, crossDirection.getStepX() * offset, 2, crossDirection.getStepZ() * offset);
         this.level.setBlock(mutable, Blocks.SOUL_SAND.defaultBlockState(), 3);
      }

      for(int offset = -1; offset <= 1; ++offset) {
         mutable.setWithOffset(origin, crossDirection.getStepX() * offset, 3, crossDirection.getStepZ() * offset);
         this.level.setBlock(mutable, Blocks.WITHER_SKELETON_SKULL.defaultBlockState(), 3);
      }

   }

   private boolean canWitherReplaceBlock(final BlockPos.MutableBlockPos pos) {
      BlockState blockState = this.level.getBlockState(pos);
      return blockState.canBeReplaced() && blockState.getFluidState().isEmpty();
   }
}
