package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class PiglinHeadModel extends SkullModelBase {
   private final ModelPart head;
   private final ModelPart leftEar;
   private final ModelPart rightEar;

   public PiglinHeadModel(ModelPart var1) {
      super();
      this.head = var1.getChild("head");
      this.leftEar = this.head.getChild("left_ear");
      this.rightEar = this.head.getChild("right_ear");
   }

   public static MeshDefinition createHeadModel() {
      MeshDefinition var0 = new MeshDefinition();
      PiglinModel.addHead(CubeDeformation.NONE, var0);
      return var0;
   }

   @Override
   public void setupAnim(float var1, float var2, float var3) {
      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
      float var4 = 1.2F;
      this.leftEar.zRot = (float)(-(Math.cos((double)(var1 * 3.1415927F * 0.2F * 1.2F)) + 2.5)) * 0.2F;
      this.rightEar.zRot = (float)(Math.cos((double)(var1 * 3.1415927F * 0.2F)) + 2.5) * 0.2F;
   }

   @Override
   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.head.render(var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
