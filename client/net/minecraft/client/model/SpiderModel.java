package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class SpiderModel extends EntityModel<LivingEntityRenderState> {
   private static final String BODY_0 = "body0";
   private static final String BODY_1 = "body1";
   private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
   private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
   private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
   private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
   private final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightMiddleHindLeg;
   private final ModelPart leftMiddleHindLeg;
   private final ModelPart rightMiddleFrontLeg;
   private final ModelPart leftMiddleFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;

   public SpiderModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightMiddleHindLeg = var1.getChild("right_middle_hind_leg");
      this.leftMiddleHindLeg = var1.getChild("left_middle_hind_leg");
      this.rightMiddleFrontLeg = var1.getChild("right_middle_front_leg");
      this.leftMiddleFrontLeg = var1.getChild("left_middle_front_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
   }

   public static LayerDefinition createSpiderBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      boolean var2 = true;
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 15.0F, -3.0F));
      var1.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
      var1.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 15.0F, 9.0F));
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(18, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F);
      float var5 = 0.7853982F;
      float var6 = 0.3926991F;
      var1.addOrReplaceChild("right_hind_leg", var3, PartPose.offsetAndRotation(-4.0F, 15.0F, 2.0F, 0.0F, 0.7853982F, -0.7853982F));
      var1.addOrReplaceChild("left_hind_leg", var4, PartPose.offsetAndRotation(4.0F, 15.0F, 2.0F, 0.0F, -0.7853982F, 0.7853982F));
      var1.addOrReplaceChild("right_middle_hind_leg", var3, PartPose.offsetAndRotation(-4.0F, 15.0F, 1.0F, 0.0F, 0.3926991F, -0.58119464F));
      var1.addOrReplaceChild("left_middle_hind_leg", var4, PartPose.offsetAndRotation(4.0F, 15.0F, 1.0F, 0.0F, -0.3926991F, 0.58119464F));
      var1.addOrReplaceChild("right_middle_front_leg", var3, PartPose.offsetAndRotation(-4.0F, 15.0F, 0.0F, 0.0F, -0.3926991F, -0.58119464F));
      var1.addOrReplaceChild("left_middle_front_leg", var4, PartPose.offsetAndRotation(4.0F, 15.0F, 0.0F, 0.0F, 0.3926991F, 0.58119464F));
      var1.addOrReplaceChild("right_front_leg", var3, PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, 0.0F, -0.7853982F, -0.7853982F));
      var1.addOrReplaceChild("left_front_leg", var4, PartPose.offsetAndRotation(4.0F, 15.0F, -1.0F, 0.0F, 0.7853982F, 0.7853982F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(LivingEntityRenderState var1) {
      super.setupAnim(var1);
      this.head.yRot = var1.yRot * 0.017453292F;
      this.head.xRot = var1.xRot * 0.017453292F;
      float var2 = var1.walkAnimationPos * 0.6662F;
      float var3 = var1.walkAnimationSpeed;
      float var4 = -(Mth.cos(var2 * 2.0F + 0.0F) * 0.4F) * var3;
      float var5 = -(Mth.cos(var2 * 2.0F + 3.1415927F) * 0.4F) * var3;
      float var6 = -(Mth.cos(var2 * 2.0F + 1.5707964F) * 0.4F) * var3;
      float var7 = -(Mth.cos(var2 * 2.0F + 4.712389F) * 0.4F) * var3;
      float var8 = Math.abs(Mth.sin(var2 + 0.0F) * 0.4F) * var3;
      float var9 = Math.abs(Mth.sin(var2 + 3.1415927F) * 0.4F) * var3;
      float var10 = Math.abs(Mth.sin(var2 + 1.5707964F) * 0.4F) * var3;
      float var11 = Math.abs(Mth.sin(var2 + 4.712389F) * 0.4F) * var3;
      ModelPart var10000 = this.rightHindLeg;
      var10000.yRot += var4;
      var10000 = this.leftHindLeg;
      var10000.yRot -= var4;
      var10000 = this.rightMiddleHindLeg;
      var10000.yRot += var5;
      var10000 = this.leftMiddleHindLeg;
      var10000.yRot -= var5;
      var10000 = this.rightMiddleFrontLeg;
      var10000.yRot += var6;
      var10000 = this.leftMiddleFrontLeg;
      var10000.yRot -= var6;
      var10000 = this.rightFrontLeg;
      var10000.yRot += var7;
      var10000 = this.leftFrontLeg;
      var10000.yRot -= var7;
      var10000 = this.rightHindLeg;
      var10000.zRot += var8;
      var10000 = this.leftHindLeg;
      var10000.zRot -= var8;
      var10000 = this.rightMiddleHindLeg;
      var10000.zRot += var9;
      var10000 = this.leftMiddleHindLeg;
      var10000.zRot -= var9;
      var10000 = this.rightMiddleFrontLeg;
      var10000.zRot += var10;
      var10000 = this.leftMiddleFrontLeg;
      var10000.zRot -= var10;
      var10000 = this.rightFrontLeg;
      var10000.zRot += var11;
      var10000 = this.leftFrontLeg;
      var10000.zRot -= var11;
   }
}
