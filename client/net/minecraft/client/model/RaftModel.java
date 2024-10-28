package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RaftModel extends AbstractBoatModel {
   public RaftModel(ModelPart var1) {
      super(var1);
   }

   private static void addCommonParts(PartDefinition var0) {
      var0.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -11.0F, -4.0F, 28.0F, 20.0F, 4.0F).texOffs(0, 0).addBox(-14.0F, -9.0F, -8.0F, 28.0F, 16.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -2.1F, 1.0F, 1.5708F, 0.0F, 0.0F));
      boolean var1 = true;
      boolean var2 = true;
      boolean var3 = true;
      float var4 = -5.0F;
      var0.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(3.0F, -4.0F, 9.0F, 0.0F, 0.0F, 0.19634955F));
      var0.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(40, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(3.0F, -4.0F, -9.0F, 0.0F, 3.1415927F, 0.19634955F));
   }

   public static LayerDefinition createRaftModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      addCommonParts(var1);
      return LayerDefinition.create(var0, 128, 64);
   }

   public static LayerDefinition createChestRaftModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      addCommonParts(var1);
      var1.addOrReplaceChild("chest_bottom", CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F), PartPose.offsetAndRotation(-2.0F, -10.1F, -6.0F, 0.0F, -1.5707964F, 0.0F));
      var1.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F), PartPose.offsetAndRotation(-2.0F, -14.1F, -6.0F, 0.0F, -1.5707964F, 0.0F));
      var1.addOrReplaceChild("chest_lock", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(-1.0F, -11.1F, -1.0F, 0.0F, -1.5707964F, 0.0F));
      return LayerDefinition.create(var0, 128, 128);
   }
}
