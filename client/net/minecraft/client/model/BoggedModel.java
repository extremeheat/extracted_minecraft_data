package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BoggedRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;

public class BoggedModel extends SkeletonModel<BoggedRenderState> {
   private final ModelPart mushrooms;

   public BoggedModel(ModelPart var1) {
      super(var1);
      this.mushrooms = var1.getChild("head").getChild("mushrooms");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition var1 = var0.getRoot();
      SkeletonModel.createDefaultSkeletonMesh(var1);
      PartDefinition var2 = var1.getChild("head").clearChild("mushrooms");
      var2.addOrReplaceChild("red_mushroom_1", CubeListBuilder.create().texOffs(50, 16).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F), PartPose.offsetAndRotation(3.0F, -8.0F, 3.0F, 0.0F, 0.7853982F, 0.0F));
      var2.addOrReplaceChild("red_mushroom_2", CubeListBuilder.create().texOffs(50, 16).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F), PartPose.offsetAndRotation(3.0F, -8.0F, 3.0F, 0.0F, 2.3561945F, 0.0F));
      var2.addOrReplaceChild("brown_mushroom_1", CubeListBuilder.create().texOffs(50, 22).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F), PartPose.offsetAndRotation(-3.0F, -8.0F, -3.0F, 0.0F, 0.7853982F, 0.0F));
      var2.addOrReplaceChild("brown_mushroom_2", CubeListBuilder.create().texOffs(50, 22).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F), PartPose.offsetAndRotation(-3.0F, -8.0F, -3.0F, 0.0F, 2.3561945F, 0.0F));
      var2.addOrReplaceChild("brown_mushroom_3", CubeListBuilder.create().texOffs(50, 28).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 4.0F, 0.0F), PartPose.offsetAndRotation(-2.0F, -1.0F, 4.0F, -1.5707964F, 0.0F, 0.7853982F));
      var2.addOrReplaceChild("brown_mushroom_4", CubeListBuilder.create().texOffs(50, 28).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 4.0F, 0.0F), PartPose.offsetAndRotation(-2.0F, -1.0F, 4.0F, -1.5707964F, 0.0F, 2.3561945F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(BoggedRenderState var1) {
      super.setupAnim((SkeletonRenderState)var1);
      this.mushrooms.visible = !var1.isSheared;
   }
}
