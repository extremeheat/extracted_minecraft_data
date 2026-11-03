package net.minecraft.world.entity.ai.control;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface LookControl extends Control {
   default void setLookAt(final Vec3 vec) {
      this.setLookAt(vec.x, vec.y, vec.z);
   }

   default void setLookAt(final Entity target) {
      this.setLookAt(target.getX(), target.getEyeY(), target.getZ());
   }

   default void setLookAt(final Entity target, final float yMaxRotSpeed, final float xMaxRotAngle) {
      this.setLookAt(target.getX(), target.getEyeY(), target.getZ(), yMaxRotSpeed, xMaxRotAngle);
   }

   void setLookAt(double x, double y, double z);

   void setLookAt(double x, double y, double z, float yMaxRotSpeed, float xMaxRotAngle);

   void tick();

   boolean isLookingAtTarget();

   @Nullable Vec3 getWantedPosition();
}
