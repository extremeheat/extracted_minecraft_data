package net.minecraft.client.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientAvatarState {
   private Vec3 deltaMovementOnPreviousTick;
   private float walkDist;
   private float walkDistO;
   private double xCloak;
   private double yCloak;
   private double zCloak;
   private double xCloakO;
   private double yCloakO;
   private double zCloakO;
   private float bob;
   private float bobO;

   public ClientAvatarState() {
      super();
      this.deltaMovementOnPreviousTick = Vec3.ZERO;
   }

   public void tick(Vec3 var1, Vec3 var2) {
      this.walkDistO = this.walkDist;
      this.deltaMovementOnPreviousTick = var2;
      this.moveCloak(var1);
   }

   public void addWalkDistance(float var1) {
      this.walkDist += var1;
   }

   public Vec3 deltaMovementOnPreviousTick() {
      return this.deltaMovementOnPreviousTick;
   }

   private void moveCloak(Vec3 var1) {
      this.xCloakO = this.xCloak;
      this.yCloakO = this.yCloak;
      this.zCloakO = this.zCloak;
      double var2 = var1.x() - this.xCloak;
      double var4 = var1.y() - this.yCloak;
      double var6 = var1.z() - this.zCloak;
      double var8 = 10.0;
      if (!(var2 > 10.0) && !(var2 < -10.0)) {
         this.xCloak += var2 * 0.25;
      } else {
         this.xCloak = var1.x();
         this.xCloakO = this.xCloak;
      }

      if (!(var4 > 10.0) && !(var4 < -10.0)) {
         this.yCloak += var4 * 0.25;
      } else {
         this.yCloak = var1.y();
         this.yCloakO = this.yCloak;
      }

      if (!(var6 > 10.0) && !(var6 < -10.0)) {
         this.zCloak += var6 * 0.25;
      } else {
         this.zCloak = var1.z();
         this.zCloakO = this.zCloak;
      }

   }

   public double getInterpolatedCloakX(float var1) {
      return Mth.lerp((double)var1, this.xCloakO, this.xCloak);
   }

   public double getInterpolatedCloakY(float var1) {
      return Mth.lerp((double)var1, this.yCloakO, this.yCloak);
   }

   public double getInterpolatedCloakZ(float var1) {
      return Mth.lerp((double)var1, this.zCloakO, this.zCloak);
   }

   public void updateBob(float var1) {
      this.bobO = this.bob;
      this.bob += (var1 - this.bob) * 0.4F;
   }

   public void resetBob() {
      this.bobO = this.bob;
      this.bob = 0.0F;
   }

   public float getInterpolatedBob(float var1) {
      return Mth.lerp(var1, this.bobO, this.bob);
   }

   public float getBackwardsInterpolatedWalkDistance(float var1) {
      float var2 = this.walkDist - this.walkDistO;
      return -(this.walkDist + var2 * var1);
   }

   public float getInterpolatedWalkDistance(float var1) {
      return Mth.lerp(var1, this.walkDistO, this.walkDist);
   }
}
