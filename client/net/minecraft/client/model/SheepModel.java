package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.SheepRenderState;

public class SheepModel extends QuadrupedModel<SheepRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 8.0F, 4.0F, 2.0F, 2.0F, 24.0F, Set.of("head"));

   public SheepModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = QuadrupedModel.createBodyMesh(12, CubeDeformation.NONE);
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 6.0F, -8.0F));
      var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F),
         PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(SheepRenderState var1) {
      this.head.resetPose();
      this.head.y = this.head.y + var1.headEatPositionScale * 9.0F * var1.ageScale;
      super.setupAnim(var1);
      this.head.xRot = var1.headEatAngleScale;
   }
}
