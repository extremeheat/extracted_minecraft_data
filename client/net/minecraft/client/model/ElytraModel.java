package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class ElytraModel extends EntityModel<HumanoidRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5F);
   private final ModelPart root;
   private final ModelPart rightWing;
   private final ModelPart leftWing;

   public ElytraModel(ModelPart var1) {
      super();
      this.root = var1;
      this.leftWing = var1.getChild("left_wing");
      this.rightWing = var1.getChild("right_wing");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeDeformation var2 = new CubeDeformation(1.0F);
      var1.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().texOffs(22, 0).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, var2),
         PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, -0.2617994F)
      );
      var1.addOrReplaceChild(
         "right_wing",
         CubeListBuilder.create().texOffs(22, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, var2),
         PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, 0.2617994F)
      );
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(HumanoidRenderState var1) {
      this.leftWing.y = var1.isCrouching ? 3.0F : 0.0F;
      this.leftWing.xRot = var1.elytraRotX;
      this.leftWing.zRot = var1.elytraRotZ;
      this.leftWing.yRot = var1.elytraRotY;
      this.rightWing.yRot = -this.leftWing.yRot;
      this.rightWing.y = this.leftWing.y;
      this.rightWing.xRot = this.leftWing.xRot;
      this.rightWing.zRot = -this.leftWing.zRot;
   }

   @Override
   public ModelPart root() {
      return this.root;
   }
}
