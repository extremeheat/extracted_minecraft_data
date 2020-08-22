package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class SkullModel extends Model {
   protected final ModelPart head;

   public SkullModel() {
      this(0, 35, 64, 64);
   }

   public SkullModel(int var1, int var2, int var3, int var4) {
      super(RenderType::entityTranslucent);
      this.texWidth = var3;
      this.texHeight = var4;
      this.head = new ModelPart(this, var1, var2);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F);
      this.head.setPos(0.0F, 0.0F, 0.0F);
   }

   public void setupAnim(float var1, float var2, float var3) {
      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.head.render(var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
