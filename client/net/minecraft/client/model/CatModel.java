package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Cat;

public class CatModel<T extends Cat> extends OcelotModel<T> {
   private float lieDownAmount;
   private float lieDownAmountTail;
   private float relaxStateOneAmount;

   public CatModel(float var1) {
      super(var1);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      this.lieDownAmount = var1.getLieDownAmount(var4);
      this.lieDownAmountTail = var1.getLieDownAmountTail(var4);
      this.relaxStateOneAmount = var1.getRelaxStateOneAmount(var4);
      if (this.lieDownAmount <= 0.0F) {
         this.head.xRot = 0.0F;
         this.head.zRot = 0.0F;
         this.frontLegL.xRot = 0.0F;
         this.frontLegL.zRot = 0.0F;
         this.frontLegR.xRot = 0.0F;
         this.frontLegR.zRot = 0.0F;
         this.frontLegR.x = -1.2F;
         this.backLegL.xRot = 0.0F;
         this.backLegR.xRot = 0.0F;
         this.backLegR.zRot = 0.0F;
         this.backLegR.x = -1.1F;
         this.backLegR.y = 18.0F;
      }

      super.prepareMobModel(var1, var2, var3, var4);
      if (var1.isSitting()) {
         this.body.xRot = 0.7853982F;
         ModelPart var10000 = this.body;
         var10000.y += -4.0F;
         var10000 = this.body;
         var10000.z += 5.0F;
         var10000 = this.head;
         var10000.y += -3.3F;
         ++this.head.z;
         var10000 = this.tail1;
         var10000.y += 8.0F;
         var10000 = this.tail1;
         var10000.z += -2.0F;
         var10000 = this.tail2;
         var10000.y += 2.0F;
         var10000 = this.tail2;
         var10000.z += -0.8F;
         this.tail1.xRot = 1.7278761F;
         this.tail2.xRot = 2.670354F;
         this.frontLegL.xRot = -0.15707964F;
         this.frontLegL.y = 16.1F;
         this.frontLegL.z = -7.0F;
         this.frontLegR.xRot = -0.15707964F;
         this.frontLegR.y = 16.1F;
         this.frontLegR.z = -7.0F;
         this.backLegL.xRot = -1.5707964F;
         this.backLegL.y = 21.0F;
         this.backLegL.z = 1.0F;
         this.backLegR.xRot = -1.5707964F;
         this.backLegR.y = 21.0F;
         this.backLegR.z = 1.0F;
         this.state = 3;
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      if (this.lieDownAmount > 0.0F) {
         this.head.zRot = this.rotlerpRad(this.head.zRot, -1.2707963F, this.lieDownAmount);
         this.head.yRot = this.rotlerpRad(this.head.yRot, 1.2707963F, this.lieDownAmount);
         this.frontLegL.xRot = -1.2707963F;
         this.frontLegR.xRot = -0.47079635F;
         this.frontLegR.zRot = -0.2F;
         this.frontLegR.x = -0.2F;
         this.backLegL.xRot = -0.4F;
         this.backLegR.xRot = 0.5F;
         this.backLegR.zRot = -0.5F;
         this.backLegR.x = -0.3F;
         this.backLegR.y = 20.0F;
         this.tail1.xRot = this.rotlerpRad(this.tail1.xRot, 0.8F, this.lieDownAmountTail);
         this.tail2.xRot = this.rotlerpRad(this.tail2.xRot, -0.4F, this.lieDownAmountTail);
      }

      if (this.relaxStateOneAmount > 0.0F) {
         this.head.xRot = this.rotlerpRad(this.head.xRot, -0.58177644F, this.relaxStateOneAmount);
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
}
