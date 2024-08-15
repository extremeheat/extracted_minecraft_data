package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;

public class MinecartModel extends EntityModel<MinecartRenderState> {
   private final ModelPart root;

   public MinecartModel(ModelPart var1) {
      super();
      this.root = var1;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      byte var2 = 20;
      byte var3 = 8;
      byte var4 = 16;
      byte var5 = 4;
      var1.addOrReplaceChild(
         "bottom",
         CubeListBuilder.create().texOffs(0, 10).addBox(-10.0F, -8.0F, -1.0F, 20.0F, 16.0F, 2.0F),
         PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 1.5707964F, 0.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "front",
         CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
         PartPose.offsetAndRotation(-9.0F, 4.0F, 0.0F, 0.0F, 4.712389F, 0.0F)
      );
      var1.addOrReplaceChild(
         "back",
         CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
         PartPose.offsetAndRotation(9.0F, 4.0F, 0.0F, 0.0F, 1.5707964F, 0.0F)
      );
      var1.addOrReplaceChild(
         "left",
         CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F),
         PartPose.offsetAndRotation(0.0F, 4.0F, -7.0F, 0.0F, 3.1415927F, 0.0F)
      );
      var1.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F), PartPose.offset(0.0F, 4.0F, 7.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(MinecartRenderState var1) {
   }

   @Override
   public ModelPart root() {
      return this.root;
   }
}
