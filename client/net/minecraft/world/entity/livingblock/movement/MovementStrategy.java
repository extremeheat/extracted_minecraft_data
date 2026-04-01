package net.minecraft.world.entity.livingblock.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.phys.Vec3;

public interface MovementStrategy<DataType extends MovementData> {
   DataType initData();

   default DataType getData(final LivingBlock entity) {
      return (DataType)entity.getMovementData();
   }

   boolean moveTowardsTarget(LivingBlock entity, Target target, Vec3 targetPos);

   void resetMovement(LivingBlock entity);

   default Vec3 adjustStepUpMovement(final LivingBlock entity, final Vec3 movement) {
      return movement;
   }

   default boolean normalStepSounds() {
      return true;
   }

   static void resetVelocity(final LivingBlock entity, final boolean resetVertical) {
      Vec3 movement = entity.getDeltaMovement();
      if (movement.lengthSqr() > Mth.square(1.0E-6)) {
         if (resetVertical) {
            entity.setDeltaMovement(movement.horizontal());
         } else {
            entity.setDeltaMovement(0.0, movement.y, 0.0);
         }

         entity.needsSync = true;
      }

   }
}
