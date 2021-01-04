package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class HumanoidHeadModel extends SkullModel {
   private final ModelPart hat = new ModelPart(this, 32, 0);

   public HumanoidHeadModel() {
      super(0, 0, 64, 64);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
      this.hat.setPos(0.0F, 0.0F, 0.0F);
   }

   public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
      super.render(var1, var2, var3, var4, var5, var6);
      this.hat.yRot = this.head.yRot;
      this.hat.xRot = this.head.xRot;
      this.hat.render(var6);
   }
}
