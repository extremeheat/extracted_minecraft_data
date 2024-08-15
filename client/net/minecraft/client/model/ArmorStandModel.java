package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.entity.HumanoidArm;

public class ArmorStandModel extends ArmorStandArmorModel {
   private static final String RIGHT_BODY_STICK = "right_body_stick";
   private static final String LEFT_BODY_STICK = "left_body_stick";
   private static final String SHOULDER_STICK = "shoulder_stick";
   private static final String BASE_PLATE = "base_plate";
   private final ModelPart rightBodyStick;
   private final ModelPart leftBodyStick;
   private final ModelPart shoulderStick;
   private final ModelPart basePlate;

   public ArmorStandModel(ModelPart var1) {
      super(var1);
      this.rightBodyStick = var1.getChild("right_body_stick");
      this.leftBodyStick = var1.getChild("left_body_stick");
      this.shoulderStick = var1.getChild("shoulder_stick");
      this.basePlate = var1.getChild("base_plate");
      this.hat.visible = false;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.offset(0.0F, 1.0F, 0.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 26).addBox(-6.0F, 0.0F, -1.5F, 12.0F, 3.0F, 3.0F), PartPose.ZERO);
      var1.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(32, 16).mirror().addBox(0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      var1.addOrReplaceChild("right_body_stick", CubeListBuilder.create().texOffs(16, 0).addBox(-3.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.ZERO);
      var1.addOrReplaceChild("left_body_stick", CubeListBuilder.create().texOffs(48, 16).addBox(1.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.ZERO);
      var1.addOrReplaceChild("shoulder_stick", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, 10.0F, -1.0F, 8.0F, 2.0F, 2.0F), PartPose.ZERO);
      var1.addOrReplaceChild(
         "base_plate", CubeListBuilder.create().texOffs(0, 32).addBox(-6.0F, 11.0F, -6.0F, 12.0F, 1.0F, 12.0F), PartPose.offset(0.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 64, 64);
   }

   @Override
   public void setupAnim(ArmorStandRenderState var1) {
      this.basePlate.xRot = 0.0F;
      this.basePlate.yRot = 0.017453292F * -var1.yRot;
      this.basePlate.zRot = 0.0F;
      super.setupAnim(var1);
      this.leftArm.visible = var1.showArms;
      this.rightArm.visible = var1.showArms;
      this.basePlate.visible = var1.showBasePlate;
      this.rightBodyStick.xRot = 0.017453292F * var1.bodyPose.getX();
      this.rightBodyStick.yRot = 0.017453292F * var1.bodyPose.getY();
      this.rightBodyStick.zRot = 0.017453292F * var1.bodyPose.getZ();
      this.leftBodyStick.xRot = 0.017453292F * var1.bodyPose.getX();
      this.leftBodyStick.yRot = 0.017453292F * var1.bodyPose.getY();
      this.leftBodyStick.zRot = 0.017453292F * var1.bodyPose.getZ();
      this.shoulderStick.xRot = 0.017453292F * var1.bodyPose.getX();
      this.shoulderStick.yRot = 0.017453292F * var1.bodyPose.getY();
      this.shoulderStick.zRot = 0.017453292F * var1.bodyPose.getZ();
   }

   @Override
   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      ModelPart var3 = this.getArm(var1);
      boolean var4 = var3.visible;
      var3.visible = true;
      super.translateToHand(var1, var2);
      var3.visible = var4;
   }
}
