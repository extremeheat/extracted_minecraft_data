package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class WitchModel<T extends Entity> extends VillagerModel<T> {
   private boolean holdingItem;
   private final ModelPart mole = (new ModelPart(this)).setTexSize(64, 128);

   public WitchModel(float var1) {
      super(var1, 64, 128);
      this.mole.setPos(0.0F, -2.0F, 0.0F);
      this.mole.texOffs(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
      this.nose.addChild(this.mole);
      this.head.removeChild(this.hat);
      this.hat = (new ModelPart(this)).setTexSize(64, 128);
      this.hat.setPos(-5.0F, -10.03125F, -5.0F);
      this.hat.texOffs(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
      this.head.addChild(this.hat);
      ModelPart var2 = (new ModelPart(this)).setTexSize(64, 128);
      var2.setPos(1.75F, -4.0F, 2.0F);
      var2.texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
      var2.xRot = -0.05235988F;
      var2.zRot = 0.02617994F;
      this.hat.addChild(var2);
      ModelPart var3 = (new ModelPart(this)).setTexSize(64, 128);
      var3.setPos(1.75F, -4.0F, 2.0F);
      var3.texOffs(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      var3.xRot = -0.10471976F;
      var3.zRot = 0.05235988F;
      var2.addChild(var3);
      ModelPart var4 = (new ModelPart(this)).setTexSize(64, 128);
      var4.setPos(1.75F, -2.0F, 2.0F);
      var4.texOffs(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      var4.xRot = -0.20943952F;
      var4.zRot = 0.10471976F;
      var3.addChild(var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.nose.translateX = 0.0F;
      this.nose.translateY = 0.0F;
      this.nose.translateZ = 0.0F;
      float var8 = 0.01F * (float)(var1.getId() % 10);
      this.nose.xRot = Mth.sin((float)var1.tickCount * var8) * 4.5F * 0.017453292F;
      this.nose.yRot = 0.0F;
      this.nose.zRot = Mth.cos((float)var1.tickCount * var8) * 2.5F * 0.017453292F;
      if (this.holdingItem) {
         this.nose.xRot = -0.9F;
         this.nose.translateZ = -0.09375F;
         this.nose.translateY = 0.1875F;
      }

   }

   public ModelPart getNose() {
      return this.nose;
   }

   public void setHoldingItem(boolean var1) {
      this.holdingItem = var1;
   }
}
