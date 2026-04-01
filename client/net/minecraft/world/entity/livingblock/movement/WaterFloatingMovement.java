package net.minecraft.world.entity.livingblock.movement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WaterFloatingMovement extends RollingMovement {
   private static final double FLOAT_IMPULSE = 0.1;
   private static final double WATER_SPEED = 0.16;
   private static final double LANDING_SPEED = 0.1;
   private static final double LANDING_IMPULSE = 0.25;

   public WaterFloatingMovement() {
      super();
   }

   public boolean moveTowardsTarget(final LivingBlock entity, final Target target, final Vec3 targetPos) {
      if (!entity.isInWater()) {
         return super.moveTowardsTarget(entity, target, targetPos);
      } else {
         moveInWater(entity, target, targetPos);
         return true;
      }
   }

   public static void moveInWater(final LivingBlock entity, final Target target, final Vec3 targetPos) {
      Vec3 movement = entity.getDeltaMovement();
      entity.setDeltaMovement(movement.x, movement.y + 0.1, movement.z);
      Vec3 position = entity.position();
      Vec3 delta = targetPos.subtract(position).horizontal();
      if (delta.lengthSqr() > Mth.square(target.distance())) {
         Vec3 direction = delta.normalize();
         Vec3 lookAhead = position.add(direction.horizontal().add(0.0, -0.1, 0.0));
         BlockPos lookaheadBlockPos = BlockPos.containing(lookAhead);
         Level level = entity.level();
         BlockState lookaheadBlockState = level.getBlockState(lookaheadBlockPos);
         BlockState aboveBlockState = level.getBlockState(lookaheadBlockPos.above());
         if (lookaheadBlockState.isFaceSturdy(level, lookaheadBlockPos, Direction.UP) && aboveBlockState.getCollisionShape(level, lookaheadBlockPos.above()).isEmpty()) {
            entity.addDeltaMovement(new Vec3(direction.x * 0.1, 0.25, direction.z * 0.1));
         } else {
            entity.addDeltaMovement(new Vec3(direction.x * 0.16, 0.0, direction.z * 0.16));
         }
      }

   }
}
