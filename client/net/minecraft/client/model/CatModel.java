package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Cat;

public class CatModel<T extends Cat> extends OcelotModel<T> {
   private float lieDownAmount;
   private float lieDownAmountTail;
   private float relaxStateOneAmount;

   public CatModel(ModelPart var1) {
      super(var1);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      this.lieDownAmount = var1.getLieDownAmount(var4);
      this.lieDownAmountTail = var1.getLieDownAmountTail(var4);
      this.relaxStateOneAmount = var1.getRelaxStateOneAmount(var4);
      if (this.lieDownAmount <= 0.0F) {
         this.head.xRot = 0.0F;
         this.head.zRot = 0.0F;
         this.leftFrontLeg.xRot = 0.0F;
         this.leftFrontLeg.zRot = 0.0F;
         this.rightFrontLeg.xRot = 0.0F;
         this.rightFrontLeg.zRot = 0.0F;
         this.rightFrontLeg.field_305 = -1.2F;
         this.leftHindLeg.xRot = 0.0F;
         this.rightHindLeg.xRot = 0.0F;
         this.rightHindLeg.zRot = 0.0F;
         this.rightHindLeg.field_305 = -1.1F;
         this.rightHindLeg.field_306 = 18.0F;
      }

      super.prepareMobModel(var1, var2, var3, var4);
      if (var1.isInSittingPose()) {
         this.body.xRot = 0.7853982F;
         ModelPart var10000 = this.body;
         var10000.field_306 += -4.0F;
         var10000 = this.body;
         var10000.field_307 += 5.0F;
         var10000 = this.head;
         var10000.field_306 += -3.3F;
         ++this.head.field_307;
         var10000 = this.tail1;
         var10000.field_306 += 8.0F;
         var10000 = this.tail1;
         var10000.field_307 += -2.0F;
         var10000 = this.tail2;
         var10000.field_306 += 2.0F;
         var10000 = this.tail2;
         var10000.field_307 += -0.8F;
         this.tail1.xRot = 1.7278761F;
         this.tail2.xRot = 2.670354F;
         this.leftFrontLeg.xRot = -0.15707964F;
         this.leftFrontLeg.field_306 = 16.1F;
         this.leftFrontLeg.field_307 = -7.0F;
         this.rightFrontLeg.xRot = -0.15707964F;
         this.rightFrontLeg.field_306 = 16.1F;
         this.rightFrontLeg.field_307 = -7.0F;
         this.leftHindLeg.xRot = -1.5707964F;
         this.leftHindLeg.field_306 = 21.0F;
         this.leftHindLeg.field_307 = 1.0F;
         this.rightHindLeg.xRot = -1.5707964F;
         this.rightHindLeg.field_306 = 21.0F;
         this.rightHindLeg.field_307 = 1.0F;
         this.state = 3;
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      if (this.lieDownAmount > 0.0F) {
         this.head.zRot = ModelUtils.rotlerpRad(this.head.zRot, -1.2707963F, this.lieDownAmount);
         this.head.yRot = ModelUtils.rotlerpRad(this.head.yRot, 1.2707963F, this.lieDownAmount);
         this.leftFrontLeg.xRot = -1.2707963F;
         this.rightFrontLeg.xRot = -0.47079635F;
         this.rightFrontLeg.zRot = -0.2F;
         this.rightFrontLeg.field_305 = -0.2F;
         this.leftHindLeg.xRot = -0.4F;
         this.rightHindLeg.xRot = 0.5F;
         this.rightHindLeg.zRot = -0.5F;
         this.rightHindLeg.field_305 = -0.3F;
         this.rightHindLeg.field_306 = 20.0F;
         this.tail1.xRot = ModelUtils.rotlerpRad(this.tail1.xRot, 0.8F, this.lieDownAmountTail);
         this.tail2.xRot = ModelUtils.rotlerpRad(this.tail2.xRot, -0.4F, this.lieDownAmountTail);
      }

      if (this.relaxStateOneAmount > 0.0F) {
         this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, -0.58177644F, this.relaxStateOneAmount);
      }

   }
}
