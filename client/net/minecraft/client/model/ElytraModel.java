package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ElytraModel<T extends LivingEntity> extends EntityModel<T> {
   private final ModelPart rightWing;
   private final ModelPart leftWing = new ModelPart(this, 22, 0);

   public ElytraModel() {
      super();
      this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
      this.rightWing = new ModelPart(this, 22, 0);
      this.rightWing.mirror = true;
      this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableCull();
      if (var1.isBaby()) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 1.5F, -0.1F);
         this.leftWing.render(var7);
         this.rightWing.render(var7);
         GlStateManager.popMatrix();
      } else {
         this.leftWing.render(var7);
         this.rightWing.render(var7);
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      float var8 = 0.2617994F;
      float var9 = -0.2617994F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      if (var1.isFallFlying()) {
         float var12 = 1.0F;
         Vec3 var13 = var1.getDeltaMovement();
         if (var13.y < 0.0D) {
            Vec3 var14 = var13.normalize();
            var12 = 1.0F - (float)Math.pow(-var14.y, 1.5D);
         }

         var8 = var12 * 0.34906584F + (1.0F - var12) * var8;
         var9 = var12 * -1.5707964F + (1.0F - var12) * var9;
      } else if (var1.isVisuallySneaking()) {
         var8 = 0.6981317F;
         var9 = -0.7853982F;
         var10 = 3.0F;
         var11 = 0.08726646F;
      }

      this.leftWing.x = 5.0F;
      this.leftWing.y = var10;
      if (var1 instanceof AbstractClientPlayer) {
         AbstractClientPlayer var15 = (AbstractClientPlayer)var1;
         var15.elytraRotX = (float)((double)var15.elytraRotX + (double)(var8 - var15.elytraRotX) * 0.1D);
         var15.elytraRotY = (float)((double)var15.elytraRotY + (double)(var11 - var15.elytraRotY) * 0.1D);
         var15.elytraRotZ = (float)((double)var15.elytraRotZ + (double)(var9 - var15.elytraRotZ) * 0.1D);
         this.leftWing.xRot = var15.elytraRotX;
         this.leftWing.yRot = var15.elytraRotY;
         this.leftWing.zRot = var15.elytraRotZ;
      } else {
         this.leftWing.xRot = var8;
         this.leftWing.zRot = var9;
         this.leftWing.yRot = var11;
      }

      this.rightWing.x = -this.leftWing.x;
      this.rightWing.yRot = -this.leftWing.yRot;
      this.rightWing.y = this.leftWing.y;
      this.rightWing.xRot = this.leftWing.xRot;
      this.rightWing.zRot = -this.leftWing.zRot;
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((LivingEntity)var1, var2, var3, var4, var5, var6, var7);
   }
}
