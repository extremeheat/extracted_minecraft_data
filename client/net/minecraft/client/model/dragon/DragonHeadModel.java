package net.minecraft.client.model.dragon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DragonHeadModel extends SkullModelBase {
   private final ModelPart head;
   private final ModelPart jaw;

   public DragonHeadModel(ModelPart var1) {
      super();
      this.head = var1.getChild("head");
      this.jaw = this.head.getChild("jaw");
   }

   public static LayerDefinition createHeadLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = -16.0F;
      PartDefinition var3 = var1.addOrReplaceChild("head", CubeListBuilder.create().addBox("upper_lip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44).addBox("upper_head", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30).mirror(true).addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0).mirror(false).addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0), PartPose.ZERO);
      var3.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(176, 65).addBox("jaw", -6.0F, 0.0F, -16.0F, 12.0F, 4.0F, 16.0F), PartPose.offset(0.0F, 4.0F, -8.0F));
      return LayerDefinition.create(var0, 256, 256);
   }

   public void setupAnim(float var1, float var2, float var3) {
      this.jaw.xRot = (float)(Math.sin((double)(var1 * 3.1415927F * 0.2F)) + 1.0) * 0.2F;
      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      var1.pushPose();
      var1.translate(0.0F, -0.374375F, 0.0F);
      var1.scale(0.75F, 0.75F, 0.75F);
      this.head.render(var1, var2, var3, var4, var5);
      var1.popPose();
   }
}
