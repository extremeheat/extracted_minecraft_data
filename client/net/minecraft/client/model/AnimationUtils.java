package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;

public class AnimationUtils {
   public AnimationUtils() {
      super();
   }

   public static void animateCrossbowHold(ModelPart var0, ModelPart var1, ModelPart var2, boolean var3) {
      ModelPart var4 = var3 ? var0 : var1;
      ModelPart var5 = var3 ? var1 : var0;
      var4.yRot = (var3 ? -0.3F : 0.3F) + var2.yRot;
      var5.yRot = (var3 ? 0.6F : -0.6F) + var2.yRot;
      var4.xRot = -1.5707964F + var2.xRot + 0.1F;
      var5.xRot = -1.5F + var2.xRot;
   }

   public static void animateCrossbowCharge(ModelPart var0, ModelPart var1, LivingEntity var2, boolean var3) {
      ModelPart var4 = var3 ? var0 : var1;
      ModelPart var5 = var3 ? var1 : var0;
      var4.yRot = var3 ? -0.8F : 0.8F;
      var4.xRot = -0.97079635F;
      var5.xRot = var4.xRot;
      float var6 = (float)CrossbowItem.getChargeDuration(var2.getUseItem(), var2);
      float var7 = Mth.clamp((float)var2.getTicksUsingItem(), 0.0F, var6);
      float var8 = var7 / var6;
      var5.yRot = Mth.lerp(var8, 0.4F, 0.85F) * (float)(var3 ? 1 : -1);
      var5.xRot = Mth.lerp(var8, var5.xRot, -1.5707964F);
   }

   public static <T extends Mob> void swingWeaponDown(ModelPart var0, ModelPart var1, T var2, float var3, float var4) {
      float var5 = Mth.sin(var3 * 3.1415927F);
      float var6 = Mth.sin((1.0F - (1.0F - var3) * (1.0F - var3)) * 3.1415927F);
      var0.zRot = 0.0F;
      var1.zRot = 0.0F;
      var0.yRot = 0.15707964F;
      var1.yRot = -0.15707964F;
      if (var2.getMainArm() == HumanoidArm.RIGHT) {
         var0.xRot = -1.8849558F + Mth.cos(var4 * 0.09F) * 0.15F;
         var1.xRot = -0.0F + Mth.cos(var4 * 0.19F) * 0.5F;
         var0.xRot += var5 * 2.2F - var6 * 0.4F;
         var1.xRot += var5 * 1.2F - var6 * 0.4F;
      } else {
         var0.xRot = -0.0F + Mth.cos(var4 * 0.19F) * 0.5F;
         var1.xRot = -1.8849558F + Mth.cos(var4 * 0.09F) * 0.15F;
         var0.xRot += var5 * 1.2F - var6 * 0.4F;
         var1.xRot += var5 * 2.2F - var6 * 0.4F;
      }

      bobArms(var0, var1, var4);
   }

   public static void bobModelPart(ModelPart var0, float var1, float var2) {
      var0.zRot += var2 * (Mth.cos(var1 * 0.09F) * 0.05F + 0.05F);
      var0.xRot += var2 * Mth.sin(var1 * 0.067F) * 0.05F;
   }

   public static void bobArms(ModelPart var0, ModelPart var1, float var2) {
      bobModelPart(var0, var2, 1.0F);
      bobModelPart(var1, var2, -1.0F);
   }

   public static void animateZombieArms(ModelPart var0, ModelPart var1, boolean var2, float var3, float var4) {
      float var5 = Mth.sin(var3 * 3.1415927F);
      float var6 = Mth.sin((1.0F - (1.0F - var3) * (1.0F - var3)) * 3.1415927F);
      var1.zRot = 0.0F;
      var0.zRot = 0.0F;
      var1.yRot = -(0.1F - var5 * 0.6F);
      var0.yRot = 0.1F - var5 * 0.6F;
      float var7 = -3.1415927F / (var2 ? 1.5F : 2.25F);
      var1.xRot = var7;
      var0.xRot = var7;
      var1.xRot += var5 * 1.2F - var6 * 0.4F;
      var0.xRot += var5 * 1.2F - var6 * 0.4F;
      bobArms(var1, var0, var4);
   }
}
