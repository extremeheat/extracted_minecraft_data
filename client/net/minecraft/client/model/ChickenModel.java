package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ChickenModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart wing0;
   private final ModelPart wing1;
   private final ModelPart beak;
   private final ModelPart redThing;

   public ChickenModel() {
      super();
      boolean var1 = true;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
      this.head.setPos(0.0F, 15.0F, -4.0F);
      this.beak = new ModelPart(this, 14, 0);
      this.beak.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
      this.beak.setPos(0.0F, 15.0F, -4.0F);
      this.redThing = new ModelPart(this, 14, 4);
      this.redThing.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
      this.redThing.setPos(0.0F, 15.0F, -4.0F);
      this.body = new ModelPart(this, 0, 9);
      this.body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
      this.body.setPos(0.0F, 16.0F, 0.0F);
      this.leg0 = new ModelPart(this, 26, 0);
      this.leg0.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
      this.leg0.setPos(-2.0F, 19.0F, 1.0F);
      this.leg1 = new ModelPart(this, 26, 0);
      this.leg1.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
      this.leg1.setPos(1.0F, 19.0F, 1.0F);
      this.wing0 = new ModelPart(this, 24, 13);
      this.wing0.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
      this.wing0.setPos(-4.0F, 13.0F, 0.0F);
      this.wing1 = new ModelPart(this, 24, 13);
      this.wing1.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
      this.wing1.setPos(4.0F, 13.0F, 0.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.young) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * var7, 2.0F * var7);
         this.head.render(var7);
         this.beak.render(var7);
         this.redThing.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * var7, 0.0F);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.wing0.render(var7);
         this.wing1.render(var7);
         GlStateManager.popMatrix();
      } else {
         this.head.render(var7);
         this.beak.render(var7);
         this.redThing.render(var7);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.wing0.render(var7);
         this.wing1.render(var7);
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.beak.xRot = this.head.xRot;
      this.beak.yRot = this.head.yRot;
      this.redThing.xRot = this.head.xRot;
      this.redThing.yRot = this.head.yRot;
      this.body.xRot = 1.5707964F;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.wing0.zRot = var4;
      this.wing1.zRot = -var4;
   }
}
