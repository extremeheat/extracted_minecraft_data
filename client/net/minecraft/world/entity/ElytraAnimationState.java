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

   public ElytraAnimationState(LivingEntity var1) {
      super();
      this.entity = var1;
   }

   public void tick() {
      this.rotXOld = this.rotX;
      this.rotYOld = this.rotY;
      this.rotZOld = this.rotZ;
      float var1;
      float var2;
      float var3;
      if (this.entity.isFallFlying()) {
         float var4 = 1.0F;
         Vec3 var5 = this.entity.getDeltaMovement();
         if (var5.y < 0.0) {
            Vec3 var6 = var5.normalize();
            var4 = 1.0F - (float)Math.pow(-var6.y, 1.5);
         }

         var1 = Mth.lerp(var4, 0.2617994F, 0.34906584F);
         var2 = Mth.lerp(var4, -0.2617994F, -1.5707964F);
         var3 = 0.0F;
      } else if (this.entity.isCrouching()) {
         var1 = 0.6981317F;
         var2 = -0.7853982F;
         var3 = 0.08726646F;
      } else {
         var1 = 0.2617994F;
         var2 = -0.2617994F;
         var3 = 0.0F;
      }

      this.rotX = this.rotX + (var1 - this.rotX) * 0.3F;
      this.rotY = this.rotY + (var3 - this.rotY) * 0.3F;
      this.rotZ = this.rotZ + (var2 - this.rotZ) * 0.3F;
   }

   public float getRotX(float var1) {
      return Mth.lerp(var1, this.rotXOld, this.rotX);
   }

   public float getRotY(float var1) {
      return Mth.lerp(var1, this.rotYOld, this.rotY);
   }

   public float getRotZ(float var1) {
      return Mth.lerp(var1, this.rotZOld, this.rotZ);
   }
}
