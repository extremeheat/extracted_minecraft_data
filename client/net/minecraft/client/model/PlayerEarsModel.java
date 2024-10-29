package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class PlayerEarsModel extends HumanoidModel<PlayerRenderState> {
   public PlayerEarsModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createEarsLayer() {
      MeshDefinition var0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.clearChild("head");
      var2.clearChild("hat");
      var1.clearChild("body");
      var1.clearChild("left_arm");
      var1.clearChild("right_arm");
      var1.clearChild("left_leg");
      var1.clearChild("right_leg");
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(1.0F));
      var2.addOrReplaceChild("left_ear", var3, PartPose.offset(-6.0F, -6.0F, 0.0F));
      var2.addOrReplaceChild("right_ear", var3, PartPose.offset(6.0F, -6.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }
}
