package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleModel<T extends Turtle> extends QuadrupedModel<T> {
   private final ModelPart eggBelly;

   public TurtleModel(float var1) {
      super(12, var1);
      this.texWidth = 128;
      this.texHeight = 64;
      this.head = new ModelPart(this, 3, 0);
      this.head.addBox(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
      this.head.setPos(0.0F, 19.0F, -10.0F);
      this.body = new ModelPart(this);
      this.body.texOffs(7, 37).addBox(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
      this.body.texOffs(31, 1).addBox(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
      this.body.setPos(0.0F, 11.0F, -10.0F);
      this.eggBelly = new ModelPart(this);
      this.eggBelly.texOffs(70, 33).addBox(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
      this.eggBelly.setPos(0.0F, 11.0F, -10.0F);
      boolean var2 = true;
      this.leg0 = new ModelPart(this, 1, 23);
      this.leg0.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.leg0.setPos(-3.5F, 22.0F, 11.0F);
      this.leg1 = new ModelPart(this, 1, 12);
      this.leg1.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.leg1.setPos(3.5F, 22.0F, 11.0F);
      this.leg2 = new ModelPart(this, 27, 30);
      this.leg2.addBox(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.leg2.setPos(-5.0F, 21.0F, -4.0F);
      this.leg3 = new ModelPart(this, 27, 24);
      this.leg3.addBox(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.leg3.setPos(5.0F, 21.0F, -4.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var8 = 6.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.16666667F, 0.16666667F, 0.16666667F);
         GlStateManager.translatef(0.0F, 120.0F * var7, 0.0F);
         this.head.render(var7);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         if (var1.hasEgg()) {
            GlStateManager.translatef(0.0F, -0.08F, 0.0F);
         }

         this.head.render(var7);
         this.body.render(var7);
         GlStateManager.pushMatrix();
         this.leg0.render(var7);
         this.leg1.render(var7);
         GlStateManager.popMatrix();
         this.leg2.render(var7);
         this.leg3.render(var7);
         if (var1.hasEgg()) {
            this.eggBelly.render(var7);
         }

         GlStateManager.popMatrix();
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.leg0.xRot = Mth.cos(var2 * 0.6662F * 0.6F) * 0.5F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var3;
      this.leg2.zRot = Mth.cos(var2 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var3;
      this.leg3.zRot = Mth.cos(var2 * 0.6662F * 0.6F) * 0.5F * var3;
      this.leg2.xRot = 0.0F;
      this.leg3.xRot = 0.0F;
      this.leg2.yRot = 0.0F;
      this.leg3.yRot = 0.0F;
      this.leg0.yRot = 0.0F;
      this.leg1.yRot = 0.0F;
      this.eggBelly.xRot = 1.5707964F;
      if (!var1.isInWater() && var1.onGround) {
         float var8 = var1.isLayingEgg() ? 4.0F : 1.0F;
         float var9 = var1.isLayingEgg() ? 2.0F : 1.0F;
         float var10 = 5.0F;
         this.leg2.yRot = Mth.cos(var8 * var2 * 5.0F + 3.1415927F) * 8.0F * var3 * var9;
         this.leg2.zRot = 0.0F;
         this.leg3.yRot = Mth.cos(var8 * var2 * 5.0F) * 8.0F * var3 * var9;
         this.leg3.zRot = 0.0F;
         this.leg0.yRot = Mth.cos(var2 * 5.0F + 3.1415927F) * 3.0F * var3;
         this.leg0.xRot = 0.0F;
         this.leg1.yRot = Mth.cos(var2 * 5.0F) * 3.0F * var3;
         this.leg1.xRot = 0.0F;
      }

   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Turtle)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Turtle)var1, var2, var3, var4, var5, var6, var7);
   }
}
