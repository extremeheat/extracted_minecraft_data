package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;

public class PandaModel<T extends Panda> extends QuadrupedModel<T> {
   private float sitAmount;
   private float lieOnBackAmount;
   private float rollAmount;

   public PandaModel(int var1, float var2) {
      super(var1, var2);
      this.texWidth = 64;
      this.texHeight = 64;
      this.head = new ModelPart(this, 0, 6);
      this.head.addBox(-6.5F, -5.0F, -4.0F, 13, 10, 9);
      this.head.setPos(0.0F, 11.5F, -17.0F);
      this.head.texOffs(45, 16).addBox(-3.5F, 0.0F, -6.0F, 7, 5, 2);
      this.head.texOffs(52, 25).addBox(-8.5F, -8.0F, -1.0F, 5, 4, 1);
      this.head.texOffs(52, 25).addBox(3.5F, -8.0F, -1.0F, 5, 4, 1);
      this.body = new ModelPart(this, 0, 25);
      this.body.addBox(-9.5F, -13.0F, -6.5F, 19, 26, 13);
      this.body.setPos(0.0F, 10.0F, 0.0F);
      boolean var3 = true;
      boolean var4 = true;
      this.leg0 = new ModelPart(this, 40, 0);
      this.leg0.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.leg0.setPos(-5.5F, 15.0F, 9.0F);
      this.leg1 = new ModelPart(this, 40, 0);
      this.leg1.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.leg1.setPos(5.5F, 15.0F, 9.0F);
      this.leg2 = new ModelPart(this, 40, 0);
      this.leg2.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.leg2.setPos(-5.5F, 15.0F, -9.0F);
      this.leg3 = new ModelPart(this, 40, 0);
      this.leg3.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.leg3.setPos(5.5F, 15.0F, -9.0F);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      this.sitAmount = var1.getSitAmount(var4);
      this.lieOnBackAmount = var1.getLieOnBackAmount(var4);
      this.rollAmount = var1.isBaby() ? 0.0F : var1.getRollAmount(var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      boolean var8 = var1.getUnhappyCounter() > 0;
      boolean var9 = var1.isSneezing();
      int var10 = var1.getSneezeCounter();
      boolean var11 = var1.isEating();
      boolean var12 = var1.isScared();
      if (var8) {
         this.head.yRot = 0.35F * Mth.sin(0.6F * var4);
         this.head.zRot = 0.35F * Mth.sin(0.6F * var4);
         this.leg2.xRot = -0.75F * Mth.sin(0.3F * var4);
         this.leg3.xRot = 0.75F * Mth.sin(0.3F * var4);
      } else {
         this.head.zRot = 0.0F;
      }

      if (var9) {
         if (var10 < 15) {
            this.head.xRot = -0.7853982F * (float)var10 / 14.0F;
         } else if (var10 < 20) {
            float var13 = (float)((var10 - 15) / 5);
            this.head.xRot = -0.7853982F + 0.7853982F * var13;
         }
      }

      if (this.sitAmount > 0.0F) {
         this.body.xRot = this.rotlerpRad(this.body.xRot, 1.7407963F, this.sitAmount);
         this.head.xRot = this.rotlerpRad(this.head.xRot, 1.5707964F, this.sitAmount);
         this.leg2.zRot = -0.27079642F;
         this.leg3.zRot = 0.27079642F;
         this.leg0.zRot = 0.5707964F;
         this.leg1.zRot = -0.5707964F;
         if (var11) {
            this.head.xRot = 1.5707964F + 0.2F * Mth.sin(var4 * 0.6F);
            this.leg2.xRot = -0.4F - 0.2F * Mth.sin(var4 * 0.6F);
            this.leg3.xRot = -0.4F - 0.2F * Mth.sin(var4 * 0.6F);
         }

         if (var12) {
            this.head.xRot = 2.1707964F;
            this.leg2.xRot = -0.9F;
            this.leg3.xRot = -0.9F;
         }
      } else {
         this.leg0.zRot = 0.0F;
         this.leg1.zRot = 0.0F;
         this.leg2.zRot = 0.0F;
         this.leg3.zRot = 0.0F;
      }

      if (this.lieOnBackAmount > 0.0F) {
         this.leg0.xRot = -0.6F * Mth.sin(var4 * 0.15F);
         this.leg1.xRot = 0.6F * Mth.sin(var4 * 0.15F);
         this.leg2.xRot = 0.3F * Mth.sin(var4 * 0.25F);
         this.leg3.xRot = -0.3F * Mth.sin(var4 * 0.25F);
         this.head.xRot = this.rotlerpRad(this.head.xRot, 1.5707964F, this.lieOnBackAmount);
      }

      if (this.rollAmount > 0.0F) {
         this.head.xRot = this.rotlerpRad(this.head.xRot, 2.0561945F, this.rollAmount);
         this.leg0.xRot = -0.5F * Mth.sin(var4 * 0.5F);
         this.leg1.xRot = 0.5F * Mth.sin(var4 * 0.5F);
         this.leg2.xRot = 0.5F * Mth.sin(var4 * 0.5F);
         this.leg3.xRot = -0.5F * Mth.sin(var4 * 0.5F);
      }

   }

   protected float rotlerpRad(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -3.1415927F; var4 += 6.2831855F) {
      }

      while(var4 >= 3.1415927F) {
         var4 -= 6.2831855F;
      }

      return var1 + var3 * var4;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var8 = 3.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, this.yHeadOffs * var7, this.zHeadOffs * var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float var9 = 0.6F;
         GlStateManager.scalef(0.5555555F, 0.5555555F, 0.5555555F);
         GlStateManager.translatef(0.0F, 23.0F * var7, 0.3F);
         this.head.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
         GlStateManager.translatef(0.0F, 49.0F * var7, 0.0F);
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
}
