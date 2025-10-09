package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.KineticWeapon;
import org.joml.Quaternionfc;

public class SpearAnimations {
   public SpearAnimations() {
      super();
   }

   static float progress(float var0, float var1, float var2) {
      return Mth.clamp(Mth.inverseLerp(var0, var1, var2), 0.0F, 1.0F);
   }

   public static <T extends HumanoidRenderState> void thirdPersonHandUse(ModelPart var0, ModelPart var1, boolean var2, ItemStack var3, T var4) {
      int var5 = var2 ? 1 : -1;
      var0.yRot = -0.1F * (float)var5 + var1.yRot;
      var0.xRot = -1.5707964F + var1.xRot + 0.8F;
      if (var4.isFallFlying || var4.swimAmount > 0.0F) {
         var0.xRot -= 0.9599311F;
      }

      if (!(var4.ticksUsingItem <= 0.0F)) {
         KineticWeapon var6 = (KineticWeapon)var3.get(DataComponents.KINETIC_WEAPON);
         if (var6 != null) {
            UseParams var7 = SpearAnimations.UseParams.fromKineticWeapon(var6, var4.ticksUsingItem);
            var0.yRot += (float)(-var5) * var7.swayScaleFast() * 0.017453292F * var7.swayIntensity() * 1.0F;
            var0.zRot += (float)(-var5) * var7.swayScaleSlow() * 0.017453292F * var7.swayIntensity() * 0.5F;
            var0.xRot += 0.017453292F * (-40.0F * var7.raiseProgressStart() + 30.0F * var7.raiseProgressMiddle() + -20.0F * var7.raiseProgressEnd() + 20.0F * var7.lowerProgress() + 10.0F * var7.raiseBackProgress() + 0.6F * var7.swayScaleSlow() * var7.swayIntensity());
         }
      }
   }

   public static void thirdPersonUseItem(PoseStack var0, float var1, HumanoidArm var2, ItemStack var3) {
      KineticWeapon var4 = (KineticWeapon)var3.get(DataComponents.KINETIC_WEAPON);
      if (var4 != null && var1 != 0.0F) {
         UseParams var5 = SpearAnimations.UseParams.fromKineticWeapon(var4, var1);
         int var6 = var2 == HumanoidArm.RIGHT ? 1 : -1;
         float var7 = 1.0F - Ease.outBack(1.0F - var5.raiseProgress());
         float var8 = 0.125F;
         var0.translate(0.0F, 0.0F, -var4.forwardMovement() * (var7 - var5.raiseBackProgress()));
         var0.rotateAround(Axis.XN.rotationDegrees(var5.raiseProgress() * 70.0F - var5.raiseBackProgress() * 70.0F), 0.0F, -0.03125F, 0.125F);
         var0.rotateAround(Axis.YP.rotationDegrees(var5.raiseProgress() * (float)var6 * 90.0F - var5.swayProgress() * (float)var6 * 90.0F), 0.0F, 0.0F, 0.125F);
      }
   }

   public static <T extends HumanoidRenderState> void thirdPersonAttackHand(HumanoidModel<T> var0, T var1) {
      float var2 = var1.attackTime;
      HumanoidArm var3 = var1.attackArm;
      ModelPart var10000 = var0.rightArm;
      var10000.yRot -= var0.body.yRot;
      var10000 = var0.leftArm;
      var10000.yRot -= var0.body.yRot;
      var10000 = var0.leftArm;
      var10000.xRot -= var0.body.yRot;
      float var4 = Ease.inOutSine(progress(var2, 0.0F, 0.05F));
      float var5 = Ease.inQuad(progress(var2, 0.05F, 0.2F));
      float var6 = Ease.inOutExpo(progress(var2, 0.4F, 1.0F));
      var10000 = var0.getArm(var3);
      var10000.xRot += (90.0F * var4 - 120.0F * var5 + 30.0F * var6) * 0.017453292F;
   }

   public static <S extends ArmedEntityRenderState> void thirdPersonAttackItem(S var0, PoseStack var1) {
      if (!(var0.attackTime <= 0.0F)) {
         KineticWeapon var2 = (KineticWeapon)var0.getMainHandItemStack().get(DataComponents.KINETIC_WEAPON);
         float var3 = var2 != null ? var2.forwardMovement() : 0.0F;
         float var4 = 0.125F;
         float var5 = var0.attackTime;
         float var6 = Ease.inQuad(progress(var5, 0.05F, 0.2F));
         float var7 = Ease.inOutExpo(progress(var5, 0.4F, 1.0F));
         var1.rotateAround(Axis.XN.rotationDegrees(70.0F * (var6 - var7)), 0.0F, -0.125F, 0.125F);
         var1.translate(0.0F, var3 * (var6 - var7), 0.0F);
      }
   }

   public static void firstPersonUse(PoseStack var0, float var1, HumanoidArm var2, ItemStack var3) {
      KineticWeapon var4 = (KineticWeapon)var3.get(DataComponents.KINETIC_WEAPON);
      if (var4 != null) {
         UseParams var5 = SpearAnimations.UseParams.fromKineticWeapon(var4, var1);
         int var6 = var2 == HumanoidArm.RIGHT ? 1 : -1;
         var0.translate((double)((float)var6 * (var5.raiseProgress() * 0.05F + var5.raiseProgressEnd() * -0.05F + var5.swayScaleSlow() * 0.005F)), (double)(var5.raiseProgress() * -0.075F + var5.raiseProgressMiddle() * 0.075F + var5.swayScaleFast() * 0.01F), (double)var5.raiseProgressStart() * 0.05 + (double)var5.raiseProgressEnd() * -0.05 + (double)(var5.swayScaleSlow() * 0.005F));
         var0.rotateAround(Axis.XP.rotationDegrees(-60.0F * Ease.inOutBack(var5.raiseProgress()) - 25.0F * var5.lowerProgress() + 85.0F * var5.raiseBackProgress() + -0.5F * var5.swayScaleFast()), 0.0F, 0.0F, 0.0F);
         var0.rotateAround(Axis.YN.rotationDegrees(-90.0F * progress(var5.raiseProgress(), 0.5F, 0.55F) + 60.0F * var5.swayProgress() + 30.0F * var5.raiseBackProgress() + 0.5F * var5.swayScaleSlow()), 0.0F, 0.0F, 0.0F);
      }
   }

   public static void firstPersonAttack(float var0, PoseStack var1, int var2, HumanoidArm var3) {
      float var4 = Ease.inOutSine(progress(var0, 0.0F, 0.05F));
      float var5 = Ease.outBack(progress(var0, 0.05F, 0.2F));
      float var6 = Ease.inOutExpo(progress(var0, 0.4F, 1.0F));
      var1.translate((float)var2 * 0.1F * (var4 - var5), -0.075F * (var4 - var6), 0.65F * (var4 - var5));
      var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-70.0F * (var4 - var6)));
      var1.translate(0.0, 0.0, -0.25 * (double)(var6 - var5));
   }

   static record UseParams(float raiseProgress, float raiseProgressStart, float raiseProgressMiddle, float raiseProgressEnd, float swayProgress, float lowerProgress, float raiseBackProgress, float swayIntensity, float swayScaleSlow, float swayScaleFast) {
      UseParams(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
         super();
         this.raiseProgress = var1;
         this.raiseProgressStart = var2;
         this.raiseProgressMiddle = var3;
         this.raiseProgressEnd = var4;
         this.swayProgress = var5;
         this.lowerProgress = var6;
         this.raiseBackProgress = var7;
         this.swayIntensity = var8;
         this.swayScaleSlow = var9;
         this.swayScaleFast = var10;
      }

      public static UseParams fromKineticWeapon(KineticWeapon var0, float var1) {
         int var2 = var0.delayTicks();
         int var3 = (Integer)var0.dismountConditions().map(KineticWeapon.Condition::maxDurationTicks).orElse(0) + var2;
         int var4 = (Integer)var0.knockbackConditions().map(KineticWeapon.Condition::maxDurationTicks).orElse(0) + var2;
         int var5 = (Integer)var0.damageConditions().map(KineticWeapon.Condition::maxDurationTicks).orElse(0) + var2;
         float var6 = SpearAnimations.progress(var1, 0.0F, (float)var2);
         float var7 = SpearAnimations.progress(var6, 0.0F, 0.5F);
         float var8 = SpearAnimations.progress(var6, 0.5F, 0.8F);
         float var9 = SpearAnimations.progress(var6, 0.8F, 1.0F);
         float var10 = SpearAnimations.progress(var1, (float)var3, (float)var4);
         float var11 = Ease.outCubic(Ease.inOutElastic(SpearAnimations.progress(var1, (float)var4, (float)(var5 - 5))));
         float var12 = SpearAnimations.progress(var1, (float)(var5 - 5), (float)var5);
         float var13 = 2.0F * Ease.outCirc(var10) - 2.0F * Ease.inCirc(var12);
         float var14 = Mth.sin(var1 * 19.0F * 0.017453292F) * var13;
         float var15 = Mth.sin(var1 * 30.0F * 0.017453292F) * var13;
         return new UseParams(var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
      }
   }
}
