package net.minecraft.client.model.player;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PlayerEarsModel extends PlayerModel {
   public PlayerEarsModel(ModelPart var1) {
      super(var1, false);
   }

   public static LayerDefinition createEarsLayer() {
      MeshDefinition var0 = PlayerModel.createMesh(CubeDeformation.NONE, false);
      PartDefinition var1 = var0.getRoot().clearRecursively();
      PartDefinition var2 = var1.getChild("head");
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(1.0F));
      var2.addOrReplaceChild("left_ear", var3, PartPose.offset(-6.0F, -6.0F, 0.0F));
      var2.addOrReplaceChild("right_ear", var3, PartPose.offset(6.0F, -6.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }
}
