package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class PiglinHeadModel extends SkullModelBase {
   private final ModelPart head;
   private final ModelPart leftEar;
   private final ModelPart rightEar;

   public PiglinHeadModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.leftEar = this.head.getChild("left_ear");
      this.rightEar = this.head.getChild("right_ear");
   }

   public static MeshDefinition createHeadModel() {
      MeshDefinition var0 = new MeshDefinition();
      PiglinModel.addHead(CubeDeformation.NONE, var0);
      return var0;
   }

   public void setupAnim(float var1, float var2, float var3) {
      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
      float var4 = 1.2F;
      this.leftEar.zRot = (float)(-(Math.cos((double)(var1 * 3.1415927F * 0.2F * 1.2F)) + 2.5)) * 0.2F;
      this.rightEar.zRot = (float)(Math.cos((double)(var1 * 3.1415927F * 0.2F)) + 2.5) * 0.2F;
   }
}
