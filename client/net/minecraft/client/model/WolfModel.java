package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;

public class WolfModel<T extends Wolf> extends EntityModel<T> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;
   private final ModelPart tail;
   private final ModelPart upperBody;

   public WolfModel() {
      super();
      float var1 = 0.0F;
      float var2 = 13.5F;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
      this.head.setPos(-1.0F, 13.5F, -7.0F);
      this.body = new ModelPart(this, 18, 14);
      this.body.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
      this.body.setPos(0.0F, 14.0F, 2.0F);
      this.upperBody = new ModelPart(this, 21, 0);
      this.upperBody.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
      this.upperBody.setPos(-1.0F, 14.0F, 2.0F);
      this.leg0 = new ModelPart(this, 0, 18);
      this.leg0.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.leg0.setPos(-2.5F, 16.0F, 7.0F);
      this.leg1 = new ModelPart(this, 0, 18);
      this.leg1.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.leg1.setPos(0.5F, 16.0F, 7.0F);
      this.leg2 = new ModelPart(this, 0, 18);
      this.leg2.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.leg2.setPos(-2.5F, 16.0F, -4.0F);
      this.leg3 = new ModelPart(this, 0, 18);
      this.leg3.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.leg3.setPos(0.5F, 16.0F, -4.0F);
      this.tail = new ModelPart(this, 9, 18);
      this.tail.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.tail.setPos(-1.0F, 12.0F, 8.0F);
      this.head.texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
      this.head.texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
      this.head.texOffs(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render(var1, var2, var3, var4, var5, var6, var7);
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * var7, 2.0F * var7);
         this.head.renderRollable(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * var7, 0.0F);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
         this.tail.renderRollable(var7);
         this.upperBody.render(var7);
         GlStateManager.popMatrix();
      } else {
         this.head.renderRollable(var7);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
         this.tail.renderRollable(var7);
         this.upperBody.render(var7);
      }

   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      if (var1.isAngry()) {
         this.tail.yRot = 0.0F;
      } else {
         this.tail.yRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      }

      if (var1.isSitting()) {
         this.upperBody.setPos(-1.0F, 16.0F, -3.0F);
         this.upperBody.xRot = 1.2566371F;
         this.upperBody.yRot = 0.0F;
         this.body.setPos(0.0F, 18.0F, 0.0F);
         this.body.xRot = 0.7853982F;
         this.tail.setPos(-1.0F, 21.0F, 6.0F);
         this.leg0.setPos(-2.5F, 22.0F, 2.0F);
         this.leg0.xRot = 4.712389F;
         this.leg1.setPos(0.5F, 22.0F, 2.0F);
         this.leg1.xRot = 4.712389F;
         this.leg2.xRot = 5.811947F;
         this.leg2.setPos(-2.49F, 17.0F, -4.0F);
         this.leg3.xRot = 5.811947F;
         this.leg3.setPos(0.51F, 17.0F, -4.0F);
      } else {
         this.body.setPos(0.0F, 14.0F, 2.0F);
         this.body.xRot = 1.5707964F;
         this.upperBody.setPos(-1.0F, 14.0F, -3.0F);
         this.upperBody.xRot = this.body.xRot;
         this.tail.setPos(-1.0F, 12.0F, 8.0F);
         this.leg0.setPos(-2.5F, 16.0F, 7.0F);
         this.leg1.setPos(0.5F, 16.0F, 7.0F);
         this.leg2.setPos(-2.5F, 16.0F, -4.0F);
         this.leg3.setPos(0.5F, 16.0F, -4.0F);
         this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
         this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.leg2.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.leg3.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      }

      this.head.zRot = var1.getHeadRollAngle(var4) + var1.getBodyRollAngle(var4, 0.0F);
      this.upperBody.zRot = var1.getBodyRollAngle(var4, -0.08F);
      this.body.zRot = var1.getBodyRollAngle(var4, -0.16F);
      this.tail.zRot = var1.getBodyRollAngle(var4, -0.2F);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.tail.xRot = var4;
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Wolf)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Wolf)var1, var2, var3, var4, var5, var6, var7);
   }
}
