package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.util.Mth;

public class BeeModel extends EntityModel<BeeRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5F);
   private static final String BONE = "bone";
   private static final String STINGER = "stinger";
   private static final String LEFT_ANTENNA = "left_antenna";
   private static final String RIGHT_ANTENNA = "right_antenna";
   private static final String FRONT_LEGS = "front_legs";
   private static final String MIDDLE_LEGS = "middle_legs";
   private static final String BACK_LEGS = "back_legs";
   private final ModelPart bone;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart frontLeg;
   private final ModelPart midLeg;
   private final ModelPart backLeg;
   private final ModelPart stinger;
   private final ModelPart leftAntenna;
   private final ModelPart rightAntenna;
   private float rollAmount;

   public BeeModel(ModelPart var1) {
      super(var1);
      this.bone = var1.getChild("bone");
      ModelPart var2 = this.bone.getChild("body");
      this.stinger = var2.getChild("stinger");
      this.leftAntenna = var2.getChild("left_antenna");
      this.rightAntenna = var2.getChild("right_antenna");
      this.rightWing = this.bone.getChild("right_wing");
      this.leftWing = this.bone.getChild("left_wing");
      this.frontLeg = this.bone.getChild("front_legs");
      this.midLeg = this.bone.getChild("middle_legs");
      this.backLeg = this.bone.getChild("back_legs");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));
      PartDefinition var3 = var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F), PartPose.ZERO);
      var3.addOrReplaceChild("stinger", CubeListBuilder.create().texOffs(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F), PartPose.ZERO);
      var3.addOrReplaceChild("left_antenna", CubeListBuilder.create().texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), PartPose.offset(0.0F, -2.0F, -5.0F));
      var3.addOrReplaceChild("right_antenna", CubeListBuilder.create().texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), PartPose.offset(0.0F, -2.0F, -5.0F));
      CubeDeformation var4 = new CubeDeformation(0.001F);
      var2.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var4), PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F));
      var2.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var4), PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F));
      var2.addOrReplaceChild("front_legs", CubeListBuilder.create().addBox("front_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 1), PartPose.offset(1.5F, 3.0F, -2.0F));
      var2.addOrReplaceChild("middle_legs", CubeListBuilder.create().addBox("middle_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 3), PartPose.offset(1.5F, 3.0F, 0.0F));
      var2.addOrReplaceChild("back_legs", CubeListBuilder.create().addBox("back_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 5), PartPose.offset(1.5F, 3.0F, 2.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(BeeRenderState var1) {
      super.setupAnim(var1);
      this.rollAmount = var1.rollAmount;
      this.stinger.visible = var1.hasStinger;
      float var2;
      if (!var1.isOnGround) {
         var2 = var1.ageInTicks * 120.32113F * 0.017453292F;
         this.rightWing.yRot = 0.0F;
         this.rightWing.zRot = Mth.cos(var2) * 3.1415927F * 0.15F;
         this.leftWing.xRot = this.rightWing.xRot;
         this.leftWing.yRot = this.rightWing.yRot;
         this.leftWing.zRot = -this.rightWing.zRot;
         this.frontLeg.xRot = 0.7853982F;
         this.midLeg.xRot = 0.7853982F;
         this.backLeg.xRot = 0.7853982F;
      }

      if (!var1.isAngry && !var1.isOnGround) {
         var2 = Mth.cos(var1.ageInTicks * 0.18F);
         this.bone.xRot = 0.1F + var2 * 3.1415927F * 0.025F;
         this.leftAntenna.xRot = var2 * 3.1415927F * 0.03F;
         this.rightAntenna.xRot = var2 * 3.1415927F * 0.03F;
         this.frontLeg.xRot = -var2 * 3.1415927F * 0.1F + 0.3926991F;
         this.backLeg.xRot = -var2 * 3.1415927F * 0.05F + 0.7853982F;
         ModelPart var10000 = this.bone;
         var10000.y -= Mth.cos(var1.ageInTicks * 0.18F) * 0.9F;
      }

      if (this.rollAmount > 0.0F) {
         this.bone.xRot = Mth.rotLerpRad(this.rollAmount, this.bone.xRot, 3.0915928F);
      }

   }
}
