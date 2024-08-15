package net.minecraft.world.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ElytraAnimationState {
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
      if (this.entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         float var1 = 0.2617994F;
         float var2 = -0.2617994F;
         float var3 = 0.0F;
         if (this.entity.isFallFlying()) {
            float var4 = 1.0F;
            Vec3 var5 = this.entity.getDeltaMovement();
            if (var5.y < 0.0) {
               Vec3 var6 = var5.normalize();
               var4 = 1.0F - (float)Math.pow(-var6.y, 1.5);
            }

            var1 = var4 * 0.34906584F + (1.0F - var4) * var1;
            var2 = var4 * -1.5707964F + (1.0F - var4) * var2;
         } else if (this.entity.isCrouching()) {
            var1 = 0.6981317F;
            var2 = -0.7853982F;
            var3 = 0.08726646F;
         }

         this.rotX = this.rotX + (var1 - this.rotX) * 0.3F;
         this.rotY = this.rotY + (var3 - this.rotY) * 0.3F;
         this.rotZ = this.rotZ + (var2 - this.rotZ) * 0.3F;
      } else {
         this.rotX = 0.0F;
         this.rotY = 0.0F;
         this.rotZ = 0.0F;
      }
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
