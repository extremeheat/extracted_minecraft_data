package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.SheepRenderState;

public class SheepFurModel extends QuadrupedModel<SheepRenderState> {
   public SheepFurModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createFurLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 6.0F, -8.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, new CubeDeformation(1.75F)), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F));
      var1.addOrReplaceChild("right_hind_leg", var2, PartPose.offset(-3.0F, 12.0F, 7.0F));
      var1.addOrReplaceChild("left_hind_leg", var2, PartPose.offset(3.0F, 12.0F, 7.0F));
      var1.addOrReplaceChild("right_front_leg", var2, PartPose.offset(-3.0F, 12.0F, -5.0F));
      var1.addOrReplaceChild("left_front_leg", var2, PartPose.offset(3.0F, 12.0F, -5.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(SheepRenderState var1) {
      super.setupAnim(var1);
      ModelPart var10000 = this.head;
      var10000.y += var1.headEatPositionScale * 9.0F * var1.ageScale;
      this.head.xRot = var1.headEatAngleScale;
   }
}
