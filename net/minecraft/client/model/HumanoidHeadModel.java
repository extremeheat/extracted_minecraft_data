package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public class HumanoidHeadModel extends SkullModel {
   private final ModelPart hat = new ModelPart(this, 32, 0);

   public HumanoidHeadModel() {
      super(0, 0, 64, 64);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
      this.hat.setPos(0.0F, 0.0F, 0.0F);
   }

   public void setupAnim(float var1, float var2, float var3) {
      super.setupAnim(var1, var2, var3);
      this.hat.yRot = this.head.yRot;
      this.hat.xRot = this.head.xRot;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      super.renderToBuffer(var1, var2, var3, var4, var5, var6, var7, var8);
      this.hat.render(var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
