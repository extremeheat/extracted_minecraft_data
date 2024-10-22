package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.BatAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BatRenderState;

public class BatModel extends EntityModel<BatRenderState> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart rightWingTip;
   private final ModelPart leftWingTip;
   private final ModelPart feet;

   public BatModel(ModelPart var1) {
      super(var1, RenderType::entityCutout);
      this.body = var1.getChild("body");
      this.head = var1.getChild("head");
      this.rightWing = this.body.getChild("right_wing");
      this.rightWingTip = this.rightWing.getChild("right_wing_tip");
      this.leftWing = this.body.getChild("left_wing");
      this.leftWingTip = this.leftWing.getChild("left_wing_tip");
      this.feet = this.body.getChild("feet");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F), PartPose.offset(0.0F, 17.0F, 0.0F)
      );
      PartDefinition var3 = var1.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 17.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_ear", CubeListBuilder.create().texOffs(1, 15).addBox(-2.5F, -4.0F, 0.0F, 3.0F, 5.0F, 0.0F), PartPose.offset(-1.5F, -2.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "left_ear", CubeListBuilder.create().texOffs(8, 15).addBox(-0.1F, -3.0F, 0.0F, 3.0F, 5.0F, 0.0F), PartPose.offset(1.1F, -3.0F, 0.0F)
      );
      PartDefinition var4 = var2.addOrReplaceChild(
         "right_wing", CubeListBuilder.create().texOffs(12, 0).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), PartPose.offset(-1.5F, 0.0F, 0.0F)
      );
      var4.addOrReplaceChild(
         "right_wing_tip", CubeListBuilder.create().texOffs(16, 0).addBox(-6.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F), PartPose.offset(-2.0F, 0.0F, 0.0F)
      );
      PartDefinition var5 = var2.addOrReplaceChild(
         "left_wing", CubeListBuilder.create().texOffs(12, 7).addBox(0.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), PartPose.offset(1.5F, 0.0F, 0.0F)
      );
      var5.addOrReplaceChild(
         "left_wing_tip", CubeListBuilder.create().texOffs(16, 8).addBox(0.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F), PartPose.offset(2.0F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(16, 16).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F), PartPose.offset(0.0F, 5.0F, 0.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(BatRenderState var1) {
      super.setupAnim(var1);
      if (var1.isResting) {
         this.applyHeadRotation(var1.yRot);
      }

      this.animate(var1.flyAnimationState, BatAnimation.BAT_FLYING, var1.ageInTicks, 1.0F);
      this.animate(var1.restAnimationState, BatAnimation.BAT_RESTING, var1.ageInTicks, 1.0F);
   }

   private void applyHeadRotation(float var1) {
      this.head.yRot = var1 * 0.017453292F;
   }
}
