package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SkullModel extends SkullModelBase {
   protected final ModelPart head;

   public SkullModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
   }

   public static MeshDefinition createHeadModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return var0;
   }

   public static LayerDefinition createHumanoidHeadLayer() {
      MeshDefinition var0 = createHeadModel();
      PartDefinition var1 = var0.getRoot();
      var1.getChild("head").addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createMobHeadLayer() {
      MeshDefinition var0 = createHeadModel();
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(float var1, float var2, float var3) {
      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
   }
}
