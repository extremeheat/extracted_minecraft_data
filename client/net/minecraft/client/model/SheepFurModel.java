package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Sheep;

public class SheepFurModel<T extends Sheep> extends QuadrupedModel<T> {
   private float headXRot;

   public SheepFurModel() {
      super(12, 0.0F);
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
      this.head.setPos(0.0F, 6.0F, -8.0F);
      this.body = new ModelPart(this, 28, 8);
      this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
      this.body.setPos(0.0F, 5.0F, 2.0F);
      float var1 = 0.5F;
      this.leg0 = new ModelPart(this, 0, 16);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.leg0.setPos(-3.0F, 12.0F, 7.0F);
      this.leg1 = new ModelPart(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.leg1.setPos(3.0F, 12.0F, 7.0F);
      this.leg2 = new ModelPart(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.leg2.setPos(-3.0F, 12.0F, -5.0F);
      this.leg3 = new ModelPart(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.leg3.setPos(3.0F, 12.0F, -5.0F);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      this.head.y = 6.0F + var1.getHeadEatPositionScale(var4) * 9.0F;
      this.headXRot = var1.getHeadEatAngleScale(var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.head.xRot = this.headXRot;
   }
}
