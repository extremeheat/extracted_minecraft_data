package net.minecraft.client.model.player;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.joml.Quaternionf;

public class PlayerCapeModel extends PlayerModel {
   private static final String CAPE = "cape";
   private final ModelPart cape;

   public PlayerCapeModel(ModelPart var1) {
      super(var1, false);
      this.cape = this.body.getChild("cape");
   }

   public static LayerDefinition createCapeLayer() {
      MeshDefinition var0 = PlayerModel.createMesh(CubeDeformation.NONE, false);
      PartDefinition var1 = var0.getRoot().clearRecursively();
      PartDefinition var2 = var1.getChild("body");
      var2.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, CubeDeformation.NONE, 1.0F, 0.5F), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 3.1415927F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(AvatarRenderState var1) {
      super.setupAnim(var1);
      this.cape.rotateBy((new Quaternionf()).rotateY(-3.1415927F).rotateX((6.0F + var1.capeLean / 2.0F + var1.capeFlap) * 0.017453292F).rotateZ(var1.capeLean2 / 2.0F * 0.017453292F).rotateY((180.0F - var1.capeLean2 / 2.0F) * 0.017453292F));
   }
}
