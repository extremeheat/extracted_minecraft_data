package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class OcelotModel<T extends Entity> extends EntityModel<T> {
   protected final ModelPart backLegL;
   protected final ModelPart backLegR;
   protected final ModelPart frontLegL;
   protected final ModelPart frontLegR;
   protected final ModelPart tail1;
   protected final ModelPart tail2;
   protected final ModelPart head = new ModelPart(this, "head");
   protected final ModelPart body;
   protected int state = 1;

   public OcelotModel(float var1) {
      super();
      this.head.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, var1, 0, 0);
      this.head.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, var1, 0, 24);
      this.head.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, var1, 0, 10);
      this.head.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, var1, 6, 10);
      this.head.setPos(0.0F, 15.0F, -9.0F);
      this.body = new ModelPart(this, 20, 0);
      this.body.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, var1);
      this.body.setPos(0.0F, 12.0F, -10.0F);
      this.tail1 = new ModelPart(this, 0, 15);
      this.tail1.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, var1);
      this.tail1.xRot = 0.9F;
      this.tail1.setPos(0.0F, 15.0F, 8.0F);
      this.tail2 = new ModelPart(this, 4, 15);
      this.tail2.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, var1);
      this.tail2.setPos(0.0F, 20.0F, 14.0F);
      this.backLegL = new ModelPart(this, 8, 13);
      this.backLegL.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, var1);
      this.backLegL.setPos(1.1F, 18.0F, 5.0F);
      this.backLegR = new ModelPart(this, 8, 13);
      this.backLegR.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, var1);
      this.backLegR.setPos(-1.1F, 18.0F, 5.0F);
      this.frontLegL = new ModelPart(this, 40, 0);
      this.frontLegL.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, var1);
      this.frontLegL.setPos(1.2F, 14.1F, -5.0F);
      this.frontLegR = new ModelPart(this, 40, 0);
      this.frontLegR.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, var1);
      this.frontLegR.setPos(-1.2F, 14.1F, -5.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(0.0F, 10.0F * var7, 4.0F * var7);
         this.head.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * var7, 0.0F);
         this.body.render(var7);
         this.backLegL.render(var7);
         this.backLegR.render(var7);
         this.frontLegL.render(var7);
         this.frontLegR.render(var7);
         this.tail1.render(var7);
         this.tail2.render(var7);
         GlStateManager.popMatrix();
      } else {
         this.head.render(var7);
         this.body.render(var7);
         this.tail1.render(var7);
         this.tail2.render(var7);
         this.backLegL.render(var7);
         this.backLegR.render(var7);
         this.frontLegL.render(var7);
         this.frontLegR.render(var7);
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      if (this.state != 3) {
         this.body.xRot = 1.5707964F;
         if (this.state == 2) {
            this.backLegL.xRot = Mth.cos(var2 * 0.6662F) * var3;
            this.backLegR.xRot = Mth.cos(var2 * 0.6662F + 0.3F) * var3;
            this.frontLegL.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F + 0.3F) * var3;
            this.frontLegR.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var3;
            this.tail2.xRot = 1.7278761F + 0.31415927F * Mth.cos(var2) * var3;
         } else {
            this.backLegL.xRot = Mth.cos(var2 * 0.6662F) * var3;
            this.backLegR.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var3;
            this.frontLegL.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * var3;
            this.frontLegR.xRot = Mth.cos(var2 * 0.6662F) * var3;
            if (this.state == 1) {
               this.tail2.xRot = 1.7278761F + 0.7853982F * Mth.cos(var2) * var3;
            } else {
               this.tail2.xRot = 1.7278761F + 0.47123894F * Mth.cos(var2) * var3;
            }
         }
      }

   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      this.body.y = 12.0F;
      this.body.z = -10.0F;
      this.head.y = 15.0F;
      this.head.z = -9.0F;
      this.tail1.y = 15.0F;
      this.tail1.z = 8.0F;
      this.tail2.y = 20.0F;
      this.tail2.z = 14.0F;
      this.frontLegL.y = 14.1F;
      this.frontLegL.z = -5.0F;
      this.frontLegR.y = 14.1F;
      this.frontLegR.z = -5.0F;
      this.backLegL.y = 18.0F;
      this.backLegL.z = 5.0F;
      this.backLegR.y = 18.0F;
      this.backLegR.z = 5.0F;
      this.tail1.xRot = 0.9F;
      ModelPart var10000;
      if (var1.isSneaking()) {
         ++this.body.y;
         var10000 = this.head;
         var10000.y += 2.0F;
         ++this.tail1.y;
         var10000 = this.tail2;
         var10000.y += -4.0F;
         var10000 = this.tail2;
         var10000.z += 2.0F;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
         this.state = 0;
      } else if (var1.isSprinting()) {
         this.tail2.y = this.tail1.y;
         var10000 = this.tail2;
         var10000.z += 2.0F;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
         this.state = 2;
      } else {
         this.state = 1;
      }

   }
}
