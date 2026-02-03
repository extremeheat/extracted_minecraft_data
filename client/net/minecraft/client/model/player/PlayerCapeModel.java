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

   public PlayerCapeModel(final ModelPart root) {
      super(root, false);
      this.cape = this.body.getChild("cape");
   }

   public static LayerDefinition createCapeLayer() {
      MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, false);
      PartDefinition root = mesh.getRoot().clearRecursively();
      PartDefinition body = root.getChild("body");
      body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, CubeDeformation.NONE, 1.0F, 0.5F), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 3.1415927F, 0.0F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   public void setupAnim(final AvatarRenderState state) {
      super.setupAnim(state);
      this.cape.rotateBy((new Quaternionf()).rotateY(-3.1415927F).rotateX((6.0F + state.capeLean / 2.0F + state.capeFlap) * 0.017453292F).rotateZ(state.capeLean2 / 2.0F * 0.017453292F).rotateY((180.0F - state.capeLean2 / 2.0F) * 0.017453292F));
   }
}
