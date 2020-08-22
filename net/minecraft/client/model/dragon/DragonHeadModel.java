package net.minecraft.client.model.dragon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelPart;

public class DragonHeadModel extends SkullModel {
   private final ModelPart head;
   private final ModelPart jaw;

   public DragonHeadModel(float var1) {
      this.texWidth = 256;
      this.texHeight = 256;
      float var2 = -16.0F;
      this.head = new ModelPart(this);
      this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, var1, 176, 44);
      this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, var1, 112, 30);
      this.head.mirror = true;
      this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, var1, 0, 0);
      this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, var1, 112, 0);
      this.head.mirror = false;
      this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, var1, 0, 0);
      this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, var1, 112, 0);
      this.jaw = new ModelPart(this);
      this.jaw.setPos(0.0F, 4.0F, -8.0F);
      this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, var1, 176, 65);
      this.head.addChild(this.jaw);
   }

   public void setupAnim(float var1, float var2, float var3) {
      this.jaw.xRot = (float)(Math.sin((double)(var1 * 3.1415927F * 0.2F)) + 1.0D) * 0.2F;
      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      var1.pushPose();
      var1.translate(0.0D, -0.37437498569488525D, 0.0D);
      var1.scale(0.75F, 0.75F, 0.75F);
      this.head.render(var1, var2, var3, var4, var5, var6, var7, var8);
      var1.popPose();
   }
}
