package net.minecraft.world.entity.vehicle;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
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
   public static int[][] offsetsForDirection(Direction var0) {
      Direction var1 = var0.getClockWise();
      Direction var2 = var1.getOpposite();
      Direction var3 = var0.getOpposite();
      return new int[][]{{var1.getStepX(), var1.getStepZ()}, {var2.getStepX(), var2.getStepZ()}, {var3.getStepX() + var1.getStepX(), var3.getStepZ() + var1.getStepZ()}, {var3.getStepX() + var2.getStepX(), var3.getStepZ() + var2.getStepZ()}, {var0.getStepX() + var1.getStepX(), var0.getStepZ() + var1.getStepZ()}, {var0.getStepX() + var2.getStepX(), var0.getStepZ() + var2.getStepZ()}, {var3.getStepX(), var3.getStepZ()}, {var0.getStepX(), var0.getStepZ()}};
   }

   public static boolean isBlockFloorValid(double var0) {
      return !Double.isInfinite(var0) && var0 < 1.0D;
   }

   public static boolean canDismountTo(CollisionGetter var0, LivingEntity var1, AABB var2) {
      return var0.getBlockCollisions(var1, var2).allMatch(VoxelShape::isEmpty);
   }

   @Nullable
   public static Vec3 findDismountLocation(CollisionGetter var0, double var1, double var3, double var5, LivingEntity var7, Pose var8) {
      if (isBlockFloorValid(var3)) {
         Vec3 var9 = new Vec3(var1, var3, var5);
         if (canDismountTo(var0, var7, var7.getLocalBoundsForPose(var8).move(var9))) {
            return var9;
         }
      }

      return null;
   }

   public static VoxelShape nonClimbableShape(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return !var2.is(BlockTags.CLIMBABLE) && (!(var2.getBlock() instanceof TrapDoorBlock) || !(Boolean)var2.getValue(TrapDoorBlock.OPEN)) ? var2.getCollisionShape(var0, var1) : Shapes.empty();
   }

   public static double findCeilingFrom(BlockPos var0, int var1, Function<BlockPos, VoxelShape> var2) {
      BlockPos.MutableBlockPos var3 = var0.mutable();
      int var4 = 0;

      while(var4 < var1) {
         VoxelShape var5 = (VoxelShape)var2.apply(var3);
         if (!var5.isEmpty()) {
            return (double)(var0.getY() + var4) + var5.min(Direction.Axis.Y);
         }

         ++var4;
         var3.move(Direction.UP);
      }

      return 1.0D / 0.0;
   }

   @Nullable
   public static Vec3 findSafeDismountLocation(EntityType<?> var0, CollisionGetter var1, BlockPos var2, boolean var3) {
      if (var3 && var0.isBlockDangerous(var1.getBlockState(var2))) {
         return null;
      } else {
         double var4 = var1.getBlockFloorHeight(nonClimbableShape(var1, var2), () -> {
            return nonClimbableShape(var1, var2.below());
         });
         if (!isBlockFloorValid(var4)) {
            return null;
         } else if (var3 && var4 <= 0.0D && var0.isBlockDangerous(var1.getBlockState(var2.below()))) {
            return null;
         } else {
            Vec3 var6 = Vec3.upFromBottomCenterOf(var2, var4);
            return var1.getBlockCollisions((Entity)null, var0.getDimensions().makeBoundingBox(var6)).allMatch(VoxelShape::isEmpty) ? var6 : null;
         }
      }
   }
}
