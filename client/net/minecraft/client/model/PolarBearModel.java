package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearModel<T extends PolarBear> extends QuadrupedModel<T> {
   public PolarBearModel() {
      super(12, 0.0F);
      this.texWidth = 128;
      this.texHeight = 64;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
      this.head.setPos(0.0F, 10.0F, -16.0F);
      this.head.texOffs(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
      this.head.texOffs(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      ModelPart var1 = this.head.texOffs(26, 0);
      var1.mirror = true;
      var1.addBox(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      this.body = new ModelPart(this);
      this.body.texOffs(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
      this.body.texOffs(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
      this.body.setPos(-2.0F, 9.0F, 12.0F);
      boolean var2 = true;
      this.leg0 = new ModelPart(this, 50, 22);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.leg0.setPos(-3.5F, 14.0F, 6.0F);
      this.leg1 = new ModelPart(this, 50, 22);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.leg1.setPos(3.5F, 14.0F, 6.0F);
      this.leg2 = new ModelPart(this, 50, 40);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.leg2.setPos(-2.5F, 14.0F, -7.0F);
      this.leg3 = new ModelPart(this, 50, 40);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.leg3.setPos(2.5F, 14.0F, -7.0F);
      --this.leg0.x;
      ++this.leg1.x;
      ModelPart var10000 = this.leg0;
      var10000.z += 0.0F;
      var10000 = this.leg1;
      var10000.z += 0.0F;
      --this.leg2.x;
      ++this.leg3.x;
      --this.leg2.z;
      --this.leg3.z;
      this.zHeadOffs += 2.0F;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var8 = 2.0F;
         this.yHeadOffs = 16.0F;
         this.zHeadOffs = 4.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6666667F, 0.6666667F, 0.6666667F);
         GlStateManager.translatef(0.0F, this.yHeadOffs * var7, this.zHeadOffs * var7);
         this.head.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * var7, 0.0F);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
         GlStateManager.popMatrix();
      } else {
         this.head.render(var7);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      float var8 = var4 - (float)var1.tickCount;
      float var9 = var1.getStandingAnimationScale(var8);
      var9 *= var9;
      float var10 = 1.0F - var9;
      this.body.xRot = 1.5707964F - var9 * 3.1415927F * 0.35F;
      this.body.y = 9.0F * var10 + 11.0F * var9;
      this.leg2.y = 14.0F * var10 - 6.0F * var9;
      this.leg2.z = -8.0F * var10 - 4.0F * var9;
      ModelPart var10000 = this.leg2;
      var10000.xRot -= var9 * 3.1415927F * 0.45F;
      this.leg3.y = this.leg2.y;
      this.leg3.z = this.leg2.z;
      var10000 = this.leg3;
      var10000.xRot -= var9 * 3.1415927F * 0.45F;
      if (this.young) {
         this.head.y = 10.0F * var10 - 9.0F * var9;
         this.head.z = -16.0F * var10 - 7.0F * var9;
      } else {
         this.head.y = 10.0F * var10 - 14.0F * var9;
         this.head.z = -16.0F * var10 - 3.0F * var9;
      }

      var10000 = this.head;
      var10000.xRot += var9 * 3.1415927F * 0.15F;
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((PolarBear)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((PolarBear)var1, var2, var3, var4, var5, var6, var7);
   }
}
