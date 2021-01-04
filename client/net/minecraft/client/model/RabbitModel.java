package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitModel<T extends Rabbit> extends EntityModel<T> {
   private final ModelPart rearFootLeft = new ModelPart(this, 26, 24);
   private final ModelPart rearFootRight;
   private final ModelPart haunchLeft;
   private final ModelPart haunchRight;
   private final ModelPart body;
   private final ModelPart frontLegLeft;
   private final ModelPart frontLegRight;
   private final ModelPart head;
   private final ModelPart earRight;
   private final ModelPart earLeft;
   private final ModelPart tail;
   private final ModelPart nose;
   private float jumpRotation;

   public RabbitModel() {
      super();
      this.rearFootLeft.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.rearFootLeft.setPos(3.0F, 17.5F, 3.7F);
      this.rearFootLeft.mirror = true;
      this.setRotation(this.rearFootLeft, 0.0F, 0.0F, 0.0F);
      this.rearFootRight = new ModelPart(this, 8, 24);
      this.rearFootRight.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.rearFootRight.setPos(-3.0F, 17.5F, 3.7F);
      this.rearFootRight.mirror = true;
      this.setRotation(this.rearFootRight, 0.0F, 0.0F, 0.0F);
      this.haunchLeft = new ModelPart(this, 30, 15);
      this.haunchLeft.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.haunchLeft.setPos(3.0F, 17.5F, 3.7F);
      this.haunchLeft.mirror = true;
      this.setRotation(this.haunchLeft, -0.34906584F, 0.0F, 0.0F);
      this.haunchRight = new ModelPart(this, 16, 15);
      this.haunchRight.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.haunchRight.setPos(-3.0F, 17.5F, 3.7F);
      this.haunchRight.mirror = true;
      this.setRotation(this.haunchRight, -0.34906584F, 0.0F, 0.0F);
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-3.0F, -2.0F, -10.0F, 6, 5, 10);
      this.body.setPos(0.0F, 19.0F, 8.0F);
      this.body.mirror = true;
      this.setRotation(this.body, -0.34906584F, 0.0F, 0.0F);
      this.frontLegLeft = new ModelPart(this, 8, 15);
      this.frontLegLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.frontLegLeft.setPos(3.0F, 17.0F, -1.0F);
      this.frontLegLeft.mirror = true;
      this.setRotation(this.frontLegLeft, -0.17453292F, 0.0F, 0.0F);
      this.frontLegRight = new ModelPart(this, 0, 15);
      this.frontLegRight.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.frontLegRight.setPos(-3.0F, 17.0F, -1.0F);
      this.frontLegRight.mirror = true;
      this.setRotation(this.frontLegRight, -0.17453292F, 0.0F, 0.0F);
      this.head = new ModelPart(this, 32, 0);
      this.head.addBox(-2.5F, -4.0F, -5.0F, 5, 4, 5);
      this.head.setPos(0.0F, 16.0F, -1.0F);
      this.head.mirror = true;
      this.setRotation(this.head, 0.0F, 0.0F, 0.0F);
      this.earRight = new ModelPart(this, 52, 0);
      this.earRight.addBox(-2.5F, -9.0F, -1.0F, 2, 5, 1);
      this.earRight.setPos(0.0F, 16.0F, -1.0F);
      this.earRight.mirror = true;
      this.setRotation(this.earRight, 0.0F, -0.2617994F, 0.0F);
      this.earLeft = new ModelPart(this, 58, 0);
      this.earLeft.addBox(0.5F, -9.0F, -1.0F, 2, 5, 1);
      this.earLeft.setPos(0.0F, 16.0F, -1.0F);
      this.earLeft.mirror = true;
      this.setRotation(this.earLeft, 0.0F, 0.2617994F, 0.0F);
      this.tail = new ModelPart(this, 52, 6);
      this.tail.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2);
      this.tail.setPos(0.0F, 20.0F, 7.0F);
      this.tail.mirror = true;
      this.setRotation(this.tail, -0.3490659F, 0.0F, 0.0F);
      this.nose = new ModelPart(this, 32, 9);
      this.nose.addBox(-0.5F, -2.5F, -5.5F, 1, 1, 1);
      this.nose.setPos(0.0F, 16.0F, -1.0F);
      this.nose.mirror = true;
      this.setRotation(this.nose, 0.0F, 0.0F, 0.0F);
   }

   private void setRotation(ModelPart var1, float var2, float var3, float var4) {
      var1.xRot = var2;
      var1.yRot = var3;
      var1.zRot = var4;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var8 = 1.5F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.56666666F, 0.56666666F, 0.56666666F);
         GlStateManager.translatef(0.0F, 22.0F * var7, 2.0F * var7);
         this.head.render(var7);
         this.earLeft.render(var7);
         this.earRight.render(var7);
         this.nose.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.4F, 0.4F, 0.4F);
         GlStateManager.translatef(0.0F, 36.0F * var7, 0.0F);
         this.rearFootLeft.render(var7);
         this.rearFootRight.render(var7);
         this.haunchLeft.render(var7);
         this.haunchRight.render(var7);
         this.body.render(var7);
         this.frontLegLeft.render(var7);
         this.frontLegRight.render(var7);
         this.tail.render(var7);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6F, 0.6F, 0.6F);
         GlStateManager.translatef(0.0F, 16.0F * var7, 0.0F);
         this.rearFootLeft.render(var7);
         this.rearFootRight.render(var7);
         this.haunchLeft.render(var7);
         this.haunchRight.render(var7);
         this.body.render(var7);
         this.frontLegLeft.render(var7);
         this.frontLegRight.render(var7);
         this.head.render(var7);
         this.earRight.render(var7);
         this.earLeft.render(var7);
         this.tail.render(var7);
         this.nose.render(var7);
         GlStateManager.popMatrix();
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var4 - (float)var1.tickCount;
      this.nose.xRot = var6 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      this.earRight.xRot = var6 * 0.017453292F;
      this.earLeft.xRot = var6 * 0.017453292F;
      this.nose.yRot = var5 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.earRight.yRot = this.nose.yRot - 0.2617994F;
      this.earLeft.yRot = this.nose.yRot + 0.2617994F;
      this.jumpRotation = Mth.sin(var1.getJumpCompletion(var8) * 3.1415927F);
      this.haunchLeft.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
      this.haunchRight.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
      this.rearFootLeft.xRot = this.jumpRotation * 50.0F * 0.017453292F;
      this.rearFootRight.xRot = this.jumpRotation * 50.0F * 0.017453292F;
      this.frontLegLeft.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
      this.frontLegRight.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      this.jumpRotation = Mth.sin(var1.getJumpCompletion(var4) * 3.1415927F);
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Rabbit)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Rabbit)var1, var2, var3, var4, var5, var6, var7);
   }
}
