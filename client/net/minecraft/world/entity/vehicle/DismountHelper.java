package net.minecraft.world.entity.vehicle;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DismountHelper {
   public DismountHelper() {
      super();
   }

   public static int[][] offsetsForDirection(Direction var0) {
      Direction var1 = var0.getClockWise();
      Direction var2 = var1.getOpposite();
      Direction var3 = var0.getOpposite();
      return new int[][]{
         {var1.getStepX(), var1.getStepZ()},
         {var2.getStepX(), var2.getStepZ()},
         {var3.getStepX() + var1.getStepX(), var3.getStepZ() + var1.getStepZ()},
         {var3.getStepX() + var2.getStepX(), var3.getStepZ() + var2.getStepZ()},
         {var0.getStepX() + var1.getStepX(), var0.getStepZ() + var1.getStepZ()},
         {var0.getStepX() + var2.getStepX(), var0.getStepZ() + var2.getStepZ()},
         {var3.getStepX(), var3.getStepZ()},
         {var0.getStepX(), var0.getStepZ()}
      };
   }

   public static boolean isBlockFloorValid(double var0) {
      return !Double.isInfinite(var0) && var0 < 1.0;
   }

   public static boolean canDismountTo(CollisionGetter var0, LivingEntity var1, AABB var2) {
      for (VoxelShape var5 : var0.getBlockCollisions(var1, var2)) {
         if (!var5.isEmpty()) {
            return false;
         }
      }

      return var0.getWorldBorder().isWithinBounds(var2);
   }

   public static boolean canDismountTo(CollisionGetter var0, Vec3 var1, LivingEntity var2, Pose var3) {
      return canDismountTo(var0, var2, var2.getLocalBoundsForPose(var3).move(var1));
   }

   public static VoxelShape nonClimbableShape(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return !var2.is(BlockTags.CLIMBABLE) && (!(var2.getBlock() instanceof TrapDoorBlock) || !var2.getValue(TrapDoorBlock.OPEN))
         ? var2.getCollisionShape(var0, var1)
         : Shapes.empty();
   }

   public static double findCeilingFrom(BlockPos var0, int var1, Function<BlockPos, VoxelShape> var2) {
      BlockPos.MutableBlockPos var3 = var0.mutable();
      int var4 = 0;

      while (var4 < var1) {
         VoxelShape var5 = (VoxelShape)var2.apply(var3);
         if (!var5.isEmpty()) {
            return (double)(var0.getY() + var4) + var5.min(Direction.Axis.Y);
         }

         var4++;
         var3.move(Direction.UP);
      }

      return 1.0 / 0.0;
   }

   @Nullable
   public static Vec3 findSafeDismountLocation(EntityType<?> var0, CollisionGetter var1, BlockPos var2, boolean var3) {
      if (var3 && var0.isBlockDangerous(var1.getBlockState(var2))) {
         return null;
      } else {
         double var4 = var1.getBlockFloorHeight(nonClimbableShape(var1, var2), () -> nonClimbableShape(var1, var2.below()));
         if (!isBlockFloorValid(var4)) {
            return null;
         } else if (var3 && var4 <= 0.0 && var0.isBlockDangerous(var1.getBlockState(var2.below()))) {
            return null;
         } else {
            Vec3 var6 = Vec3.upFromBottomCenterOf(var2, var4);
            AABB var7 = var0.getDimensions().makeBoundingBox(var6);

            for (VoxelShape var10 : var1.getBlockCollisions(null, var7)) {
               if (!var10.isEmpty()) {
                  return null;
               }
            }

            if (var0 != EntityType.PLAYER
               || !var1.getBlockState(var2).is(BlockTags.INVALID_SPAWN_INSIDE) && !var1.getBlockState(var2.above()).is(BlockTags.INVALID_SPAWN_INSIDE)) {
               return !var1.getWorldBorder().isWithinBounds(var7) ? null : var6;
            } else {
               return null;
            }
         }
      }
   }
}
