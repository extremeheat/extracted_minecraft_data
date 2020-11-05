package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class ChestedHorseModel<T extends AbstractChestedHorse> extends HorseModel<T> {
   private final ModelPart boxL = new ModelPart(this, 26, 21);
   private final ModelPart boxR;

   public ChestedHorseModel(float var1) {
      super(var1);
      this.boxL.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
      this.boxR = new ModelPart(this, 26, 21);
      this.boxR.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
      this.boxL.yRot = -1.5707964F;
      this.boxR.yRot = 1.5707964F;
      this.boxL.setPos(6.0F, -8.0F, 0.0F);
      this.boxR.setPos(-6.0F, -8.0F, 0.0F);
      this.body.addChild(this.boxL);
      this.body.addChild(this.boxR);
   }

   protected void addEarModels(ModelPart var1) {
      ModelPart var2 = new ModelPart(this, 0, 12);
      var2.addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
      var2.setPos(1.25F, -10.0F, 4.0F);
      ModelPart var3 = new ModelPart(this, 0, 12);
      var3.addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
      var3.setPos(-1.25F, -10.0F, 4.0F);
      var2.xRot = 0.2617994F;
      var2.zRot = 0.2617994F;
      var3.xRot = 0.2617994F;
      var3.zRot = -0.2617994F;
      var1.addChild(var2);
      var1.addChild(var3);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim((AbstractHorse)var1, var2, var3, var4, var5, var6);
      if (var1.hasChest()) {
         this.boxL.visible = true;
         this.boxR.visible = true;
      } else {
         this.boxL.visible = false;
         this.boxR.visible = false;
      }

   }
}
