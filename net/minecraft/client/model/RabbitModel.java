package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitModel extends EntityModel {
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
      this.rearFootLeft.addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F);
      this.rearFootLeft.setPos(3.0F, 17.5F, 3.7F);
      this.rearFootLeft.mirror = true;
      this.setRotation(this.rearFootLeft, 0.0F, 0.0F, 0.0F);
      this.rearFootRight = new ModelPart(this, 8, 24);
      this.rearFootRight.addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F);
      this.rearFootRight.setPos(-3.0F, 17.5F, 3.7F);
      this.rearFootRight.mirror = true;
      this.setRotation(this.rearFootRight, 0.0F, 0.0F, 0.0F);
      this.haunchLeft = new ModelPart(this, 30, 15);
      this.haunchLeft.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F);
      this.haunchLeft.setPos(3.0F, 17.5F, 3.7F);
      this.haunchLeft.mirror = true;
      this.setRotation(this.haunchLeft, -0.34906584F, 0.0F, 0.0F);
      this.haunchRight = new ModelPart(this, 16, 15);
      this.haunchRight.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F);
      this.haunchRight.setPos(-3.0F, 17.5F, 3.7F);
      this.haunchRight.mirror = true;
      this.setRotation(this.haunchRight, -0.34906584F, 0.0F, 0.0F);
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F);
      this.body.setPos(0.0F, 19.0F, 8.0F);
      this.body.mirror = true;
      this.setRotation(this.body, -0.34906584F, 0.0F, 0.0F);
      this.frontLegLeft = new ModelPart(this, 8, 15);
      this.frontLegLeft.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
      this.frontLegLeft.setPos(3.0F, 17.0F, -1.0F);
      this.frontLegLeft.mirror = true;
      this.setRotation(this.frontLegLeft, -0.17453292F, 0.0F, 0.0F);
      this.frontLegRight = new ModelPart(this, 0, 15);
      this.frontLegRight.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
      this.frontLegRight.setPos(-3.0F, 17.0F, -1.0F);
      this.frontLegRight.mirror = true;
      this.setRotation(this.frontLegRight, -0.17453292F, 0.0F, 0.0F);
      this.head = new ModelPart(this, 32, 0);
      this.head.addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F);
      this.head.setPos(0.0F, 16.0F, -1.0F);
      this.head.mirror = true;
      this.setRotation(this.head, 0.0F, 0.0F, 0.0F);
      this.earRight = new ModelPart(this, 52, 0);
      this.earRight.addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F);
      this.earRight.setPos(0.0F, 16.0F, -1.0F);
      this.earRight.mirror = true;
      this.setRotation(this.earRight, 0.0F, -0.2617994F, 0.0F);
      this.earLeft = new ModelPart(this, 58, 0);
      this.earLeft.addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F);
      this.earLeft.setPos(0.0F, 16.0F, -1.0F);
      this.earLeft.mirror = true;
      this.setRotation(this.earLeft, 0.0F, 0.2617994F, 0.0F);
      this.tail = new ModelPart(this, 52, 6);
      this.tail.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F);
      this.tail.setPos(0.0F, 20.0F, 7.0F);
      this.tail.mirror = true;
      this.setRotation(this.tail, -0.3490659F, 0.0F, 0.0F);
      this.nose = new ModelPart(this, 32, 9);
      this.nose.addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F);
      this.nose.setPos(0.0F, 16.0F, -1.0F);
      this.nose.mirror = true;
      this.setRotation(this.nose, 0.0F, 0.0F, 0.0F);
   }

   private void setRotation(ModelPart var1, float var2, float var3, float var4) {
      var1.xRot = var2;
      var1.yRot = var3;
      var1.zRot = var4;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      if (this.young) {
         float var9 = 1.5F;
         var1.pushPose();
         var1.scale(0.56666666F, 0.56666666F, 0.56666666F);
         var1.translate(0.0D, 1.375D, 0.125D);
         ImmutableList.of(this.head, this.earLeft, this.earRight, this.nose).forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
         var1.popPose();
         var1.pushPose();
         var1.scale(0.4F, 0.4F, 0.4F);
         var1.translate(0.0D, 2.25D, 0.0D);
         ImmutableList.of(this.rearFootLeft, this.rearFootRight, this.haunchLeft, this.haunchRight, this.body, this.frontLegLeft, this.frontLegRight, this.tail).forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
         var1.popPose();
      } else {
         var1.pushPose();
         var1.scale(0.6F, 0.6F, 0.6F);
         var1.translate(0.0D, 1.0D, 0.0D);
         ImmutableList.of(this.rearFootLeft, this.rearFootRight, this.haunchLeft, this.haunchRight, this.body, this.frontLegLeft, this.frontLegRight, this.head, this.earRight, this.earLeft, this.tail, this.nose, new ModelPart[0]).forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
         var1.popPose();
      }

   }

   public void setupAnim(Rabbit var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 - (float)var1.tickCount;
      this.nose.xRot = var6 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      this.earRight.xRot = var6 * 0.017453292F;
      this.earLeft.xRot = var6 * 0.017453292F;
      this.nose.yRot = var5 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.earRight.yRot = this.nose.yRot - 0.2617994F;
      this.earLeft.yRot = this.nose.yRot + 0.2617994F;
      this.jumpRotation = Mth.sin(var1.getJumpCompletion(var7) * 3.1415927F);
      this.haunchLeft.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
      this.haunchRight.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
      this.rearFootLeft.xRot = this.jumpRotation * 50.0F * 0.017453292F;
      this.rearFootRight.xRot = this.jumpRotation * 50.0F * 0.017453292F;
      this.frontLegLeft.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
      this.frontLegRight.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
   }

   public void prepareMobModel(Rabbit var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      this.jumpRotation = Mth.sin(var1.getJumpCompletion(var4) * 3.1415927F);
   }
}
