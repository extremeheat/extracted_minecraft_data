package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.util.Mth;

public class EndermanModel<T extends EndermanRenderState> extends HumanoidModel<T> {
   public EndermanModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      float var0 = -14.0F;
      MeshDefinition var1 = HumanoidModel.createMesh(CubeDeformation.NONE, -14.0F);
      PartDefinition var2 = var1.getRoot();
      PartDefinition var3 = var2.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, -13.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "hat", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), PartPose.ZERO
      );
      var2.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offset(0.0F, -14.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-5.0F, -12.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(5.0F, -12.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-2.0F, -5.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(2.0F, -5.0F, 0.0F)
      );
      return LayerDefinition.create(var1, 64, 32);
   }

   public void setupAnim(T var1) {
      super.setupAnim((T)var1);
      this.head.visible = true;
      this.rightArm.xRot *= 0.5F;
      this.leftArm.xRot *= 0.5F;
      this.rightLeg.xRot *= 0.5F;
      this.leftLeg.xRot *= 0.5F;
      float var2 = 0.4F;
      this.rightArm.xRot = Mth.clamp(this.rightArm.xRot, -0.4F, 0.4F);
      this.leftArm.xRot = Mth.clamp(this.leftArm.xRot, -0.4F, 0.4F);
      this.rightLeg.xRot = Mth.clamp(this.rightLeg.xRot, -0.4F, 0.4F);
      this.leftLeg.xRot = Mth.clamp(this.leftLeg.xRot, -0.4F, 0.4F);
      if (var1.carriedBlock != null) {
         this.rightArm.xRot = -0.5F;
         this.leftArm.xRot = -0.5F;
         this.rightArm.zRot = 0.05F;
         this.leftArm.zRot = -0.05F;
      }

      if (var1.isCreepy) {
         float var3 = 5.0F;
         this.head.y -= 5.0F;
         this.hat.y += 5.0F;
      }
   }
}
