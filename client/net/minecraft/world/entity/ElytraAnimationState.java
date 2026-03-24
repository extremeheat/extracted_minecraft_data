package net.minecraft.world.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ElytraAnimationState {
   private static final float DEFAULT_X_ROT = 0.2617994F;
   private static final float DEFAULT_Z_ROT = -0.2617994F;
   private float rotX;
   private float rotY;
   private float rotZ;
   private float rotXOld;
   private float rotYOld;
   private float rotZOld;
   private final LivingEntity entity;

   public ElytraAnimationState(final LivingEntity entity) {
      super();
      this.entity = entity;
   }

   public void tick() {
      this.rotXOld = this.rotX;
      this.rotYOld = this.rotY;
      this.rotZOld = this.rotZ;
      float targetXRot;
      float targetZRot;
      float targetYRot;
      if (this.entity.isFallFlying()) {
         float ratio = 1.0F;
         Vec3 movement = this.entity.getDeltaMovement();
         if (movement.y < 0.0) {
            Vec3 vec = movement.normalize();
            ratio = 1.0F - (float)Math.pow(-vec.y, 1.5);
         }

         targetXRot = Mth.lerp(ratio, 0.2617994F, 0.34906584F);
         targetZRot = Mth.lerp(ratio, -0.2617994F, -1.5707964F);
         targetYRot = 0.0F;
      } else if (this.entity.isCrouching()) {
         targetXRot = 0.6981317F;
         targetZRot = -0.7853982F;
         targetYRot = 0.08726646F;
      } else {
         targetXRot = 0.2617994F;
         targetZRot = -0.2617994F;
         targetYRot = 0.0F;
      }

      this.rotX += (targetXRot - this.rotX) * 0.3F;
      this.rotY += (targetYRot - this.rotY) * 0.3F;
      this.rotZ += (targetZRot - this.rotZ) * 0.3F;
   }

   public float getRotX(final float partialTicks) {
      return Mth.lerp(partialTicks, this.rotXOld, this.rotX);
   }

   public float getRotY(final float partialTicks) {
      return Mth.lerp(partialTicks, this.rotYOld, this.rotY);
   }

   public float getRotZ(final float partialTicks) {
      return Mth.lerp(partialTicks, this.rotZOld, this.rotZ);
   }
}
