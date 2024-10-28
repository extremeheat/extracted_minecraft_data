package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SpiderModel<T extends Entity> extends HierarchicalModel<T> {
   private static final String BODY_0 = "body0";
   private static final String BODY_1 = "body1";
   private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
   private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
   private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
   private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
   private final ModelPart root;
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
      super();
      this.root = var1;
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
      var1.addOrReplaceChild("right_hind_leg", var3, PartPose.offset(-4.0F, 15.0F, 2.0F));
      var1.addOrReplaceChild("left_hind_leg", var4, PartPose.offset(4.0F, 15.0F, 2.0F));
      var1.addOrReplaceChild("right_middle_hind_leg", var3, PartPose.offset(-4.0F, 15.0F, 1.0F));
      var1.addOrReplaceChild("left_middle_hind_leg", var4, PartPose.offset(4.0F, 15.0F, 1.0F));
      var1.addOrReplaceChild("right_middle_front_leg", var3, PartPose.offset(-4.0F, 15.0F, 0.0F));
      var1.addOrReplaceChild("left_middle_front_leg", var4, PartPose.offset(4.0F, 15.0F, 0.0F));
      var1.addOrReplaceChild("right_front_leg", var3, PartPose.offset(-4.0F, 15.0F, -1.0F));
      var1.addOrReplaceChild("left_front_leg", var4, PartPose.offset(4.0F, 15.0F, -1.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      float var7 = 0.7853982F;
      this.rightHindLeg.zRot = -0.7853982F;
      this.leftHindLeg.zRot = 0.7853982F;
      this.rightMiddleHindLeg.zRot = -0.58119464F;
      this.leftMiddleHindLeg.zRot = 0.58119464F;
      this.rightMiddleFrontLeg.zRot = -0.58119464F;
      this.leftMiddleFrontLeg.zRot = 0.58119464F;
      this.rightFrontLeg.zRot = -0.7853982F;
      this.leftFrontLeg.zRot = 0.7853982F;
      float var8 = -0.0F;
      float var9 = 0.3926991F;
      this.rightHindLeg.yRot = 0.7853982F;
      this.leftHindLeg.yRot = -0.7853982F;
      this.rightMiddleHindLeg.yRot = 0.3926991F;
      this.leftMiddleHindLeg.yRot = -0.3926991F;
      this.rightMiddleFrontLeg.yRot = -0.3926991F;
      this.leftMiddleFrontLeg.yRot = 0.3926991F;
      this.rightFrontLeg.yRot = -0.7853982F;
      this.leftFrontLeg.yRot = 0.7853982F;
      float var10 = -(Mth.cos(var2 * 0.6662F * 2.0F + 0.0F) * 0.4F) * var3;
      float var11 = -(Mth.cos(var2 * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * var3;
      float var12 = -(Mth.cos(var2 * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * var3;
      float var13 = -(Mth.cos(var2 * 0.6662F * 2.0F + 4.712389F) * 0.4F) * var3;
      float var14 = Math.abs(Mth.sin(var2 * 0.6662F + 0.0F) * 0.4F) * var3;
      float var15 = Math.abs(Mth.sin(var2 * 0.6662F + 3.1415927F) * 0.4F) * var3;
      float var16 = Math.abs(Mth.sin(var2 * 0.6662F + 1.5707964F) * 0.4F) * var3;
      float var17 = Math.abs(Mth.sin(var2 * 0.6662F + 4.712389F) * 0.4F) * var3;
      ModelPart var10000 = this.rightHindLeg;
      var10000.yRot += var10;
      var10000 = this.leftHindLeg;
      var10000.yRot += -var10;
      var10000 = this.rightMiddleHindLeg;
      var10000.yRot += var11;
      var10000 = this.leftMiddleHindLeg;
      var10000.yRot += -var11;
      var10000 = this.rightMiddleFrontLeg;
      var10000.yRot += var12;
      var10000 = this.leftMiddleFrontLeg;
      var10000.yRot += -var12;
      var10000 = this.rightFrontLeg;
      var10000.yRot += var13;
      var10000 = this.leftFrontLeg;
      var10000.yRot += -var13;
      var10000 = this.rightHindLeg;
      var10000.zRot += var14;
      var10000 = this.leftHindLeg;
      var10000.zRot += -var14;
      var10000 = this.rightMiddleHindLeg;
      var10000.zRot += var15;
      var10000 = this.leftMiddleHindLeg;
      var10000.zRot += -var15;
      var10000 = this.rightMiddleFrontLeg;
      var10000.zRot += var16;
      var10000 = this.leftMiddleFrontLeg;
      var10000.zRot += -var16;
      var10000 = this.rightFrontLeg;
      var10000.zRot += var17;
      var10000 = this.leftFrontLeg;
      var10000.zRot += -var17;
   }
}
