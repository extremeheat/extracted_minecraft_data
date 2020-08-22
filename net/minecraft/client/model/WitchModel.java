package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class WitchModel extends VillagerModel {
   private boolean holdingItem;
   private final ModelPart mole = (new ModelPart(this)).setTexSize(64, 128);

   public WitchModel(float var1) {
      super(var1, 64, 128);
      this.mole.setPos(0.0F, -2.0F, 0.0F);
      this.mole.texOffs(0, 0).addBox(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, -0.25F);
      this.nose.addChild(this.mole);
      this.head = (new ModelPart(this)).setTexSize(64, 128);
      this.head.setPos(0.0F, 0.0F, 0.0F);
      this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, var1);
      this.hat = (new ModelPart(this)).setTexSize(64, 128);
      this.hat.setPos(-5.0F, -10.03125F, -5.0F);
      this.hat.texOffs(0, 64).addBox(0.0F, 0.0F, 0.0F, 10.0F, 2.0F, 10.0F);
      this.head.addChild(this.hat);
      this.head.addChild(this.nose);
      ModelPart var2 = (new ModelPart(this)).setTexSize(64, 128);
      var2.setPos(1.75F, -4.0F, 2.0F);
      var2.texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 7.0F, 4.0F, 7.0F);
      var2.xRot = -0.05235988F;
      var2.zRot = 0.02617994F;
      this.hat.addChild(var2);
      ModelPart var3 = (new ModelPart(this)).setTexSize(64, 128);
      var3.setPos(1.75F, -4.0F, 2.0F);
      var3.texOffs(0, 87).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F);
      var3.xRot = -0.10471976F;
      var3.zRot = 0.05235988F;
      var2.addChild(var3);
      ModelPart var4 = (new ModelPart(this)).setTexSize(64, 128);
      var4.setPos(1.75F, -2.0F, 2.0F);
      var4.texOffs(0, 95).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.25F);
      var4.xRot = -0.20943952F;
      var4.zRot = 0.10471976F;
      var3.addChild(var4);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.nose.setPos(0.0F, -2.0F, 0.0F);
      float var7 = 0.01F * (float)(var1.getId() % 10);
      this.nose.xRot = Mth.sin((float)var1.tickCount * var7) * 4.5F * 0.017453292F;
      this.nose.yRot = 0.0F;
      this.nose.zRot = Mth.cos((float)var1.tickCount * var7) * 2.5F * 0.017453292F;
      if (this.holdingItem) {
         this.nose.setPos(0.0F, 1.0F, -1.5F);
         this.nose.xRot = -0.9F;
      }

   }

   public ModelPart getNose() {
      return this.nose;
   }

   public void setHoldingItem(boolean var1) {
      this.holdingItem = var1;
   }
}
