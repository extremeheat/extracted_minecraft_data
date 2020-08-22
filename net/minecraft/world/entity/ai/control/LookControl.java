package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class LookControl {
   protected final Mob mob;
   protected float yMaxRotSpeed;
   protected float xMaxRotAngle;
   protected boolean hasWanted;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;

   public LookControl(Mob var1) {
      this.mob = var1;
   }

   public void setLookAt(Vec3 var1) {
      this.setLookAt(var1.x, var1.y, var1.z);
   }

   public void setLookAt(Entity var1, float var2, float var3) {
      this.setLookAt(var1.getX(), getWantedY(var1), var1.getZ(), var2, var3);
   }

   public void setLookAt(double var1, double var3, double var5) {
      this.setLookAt(var1, var3, var5, (float)this.mob.getHeadRotSpeed(), (float)this.mob.getMaxHeadXRot());
   }

   public void setLookAt(double var1, double var3, double var5, float var7, float var8) {
      this.wantedX = var1;
      this.wantedY = var3;
      this.wantedZ = var5;
      this.yMaxRotSpeed = var7;
      this.xMaxRotAngle = var8;
      this.hasWanted = true;
   }

   public void tick() {
      if (this.resetXRotOnTick()) {
         this.mob.xRot = 0.0F;
      }

      if (this.hasWanted) {
         this.hasWanted = false;
         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.getYRotD(), this.yMaxRotSpeed);
         this.mob.xRot = this.rotateTowards(this.mob.xRot, this.getXRotD(), this.xMaxRotAngle);
      } else {
         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, 10.0F);
      }

      if (!this.mob.getNavigation().isDone()) {
         this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
      }

   }

   protected boolean resetXRotOnTick() {
      return true;
   }

   public boolean isHasWanted() {
      return this.hasWanted;
   }

   public double getWantedX() {
      return this.wantedX;
   }

   public double getWantedY() {
      return this.wantedY;
   }

   public double getWantedZ() {
      return this.wantedZ;
   }

   protected float getXRotD() {
      double var1 = this.wantedX - this.mob.getX();
      double var3 = this.wantedY - this.mob.getEyeY();
      double var5 = this.wantedZ - this.mob.getZ();
      double var7 = (double)Mth.sqrt(var1 * var1 + var5 * var5);
      return (float)(-(Mth.atan2(var3, var7) * 57.2957763671875D));
   }

   protected float getYRotD() {
      double var1 = this.wantedX - this.mob.getX();
      double var3 = this.wantedZ - this.mob.getZ();
      return (float)(Mth.atan2(var3, var1) * 57.2957763671875D) - 90.0F;
   }

   protected float rotateTowards(float var1, float var2, float var3) {
      float var4 = Mth.degreesDifference(var1, var2);
      float var5 = Mth.clamp(var4, -var3, var3);
      return var1 + var5;
   }

   private static double getWantedY(Entity var0) {
      return var0 instanceof LivingEntity ? var0.getEyeY() : (var0.getBoundingBox().minY + var0.getBoundingBox().maxY) / 2.0D;
   }
}
