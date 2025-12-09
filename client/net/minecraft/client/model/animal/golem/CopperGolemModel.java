package net.minecraft.client.model.animal.golem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Set;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CopperGolemAnimation;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import org.joml.Quaternionfc;

public class CopperGolemModel extends EntityModel<CopperGolemRenderState> implements ArmedModel<CopperGolemRenderState>, HeadedModel {
   private static final float MAX_WALK_ANIMATION_SPEED = 2.0F;
   private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
   private static final float Z_FIGHT_MITIGATION = 0.015F;
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightArm;
   private final ModelPart leftArm;
   private final KeyframeAnimation walkAnimation;
   private final KeyframeAnimation walkWithItemAnimation;
   private final KeyframeAnimation idleAnimation;
   private final KeyframeAnimation interactionGetItem;
   private final KeyframeAnimation interactionGetNoItem;
   private final KeyframeAnimation interactionDropItem;
   private final KeyframeAnimation interactionDropNoItem;

   public CopperGolemModel(ModelPart var1) {
      super(var1);
      this.body = var1.getChild("body");
      this.head = this.body.getChild("head");
      this.rightArm = this.body.getChild("right_arm");
      this.leftArm = this.body.getChild("left_arm");
      this.walkAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK.bake(var1);
      this.walkWithItemAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK_ITEM.bake(var1);
      this.idleAnimation = CopperGolemAnimation.COPPER_GOLEM_IDLE.bake(var1);
      this.interactionGetItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_GET.bake(var1);
      this.interactionGetNoItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_NOGET.bake(var1);
      this.interactionDropItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_DROP.bake(var1);
      this.interactionDropNoItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_NODROP.bake(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = (new MeshDefinition()).transformed((var0x) -> var0x.translated(0.0F, 24.0F, 0.0F));
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 0.0F));
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.015F)).texOffs(56, 0).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE).texOffs(37, 8).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F)).texOffs(37, 0).addBox(-2.0F, -13.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)), PartPose.offset(0.0F, -6.0F, 0.0F));
      var2.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(36, 16).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-4.0F, -6.0F, 0.0F));
      var2.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(50, 16).addBox(0.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(4.0F, -6.0F, 0.0F));
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 0.0F));
      var1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 27).addBox(0.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createRunningPoseBodyLayer() {
      MeshDefinition var0 = (new MeshDefinition()).transformed((var0x) -> var0x.translated(0.0F, 0.0F, 0.0F));
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(-1.064F, -5.0F, 0.0F));
      var2.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-4.02F, -6.116F, -3.5F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, 0.1F, 0.7F, 0.1204F, -0.0064F, -0.0779F));
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.1F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(56, 0).addBox(-1.02F, -2.1F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(37, 8).addBox(-1.02F, -9.1F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F)).texOffs(37, 0).addBox(-2.0F, -13.1F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)), PartPose.offset(0.7F, -5.6F, -1.8F));
      PartDefinition var3 = var2.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-4.0F, -6.0F, 0.0F));
      var3.addOrReplaceChild("right_arm_r1", CubeListBuilder.create().texOffs(36, 16).addBox(-3.052F, -1.11F, -2.036F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7F, -0.248F, -1.62F, 1.0036F, 0.0F, 0.0F));
      PartDefinition var4 = var2.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(4.0F, -6.0F, 0.0F));
      var4.addOrReplaceChild("left_arm_r1", CubeListBuilder.create().texOffs(50, 16).addBox(0.032F, -1.1F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.732F, 0.0F, 0.0F, -0.8715F, -0.0535F, -0.0449F));
      PartDefinition var5 = var1.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-3.064F, -5.0F, 0.0F));
      var5.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-1.856F, -0.1F, -1.09F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.048F, 0.0F, -0.9F, -0.8727F, 0.0F, 0.0F));
      PartDefinition var6 = var1.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(0.936F, -5.0F, 0.0F));
      var6.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(16, 27).addBox(-2.088F, -0.1F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createSittingPoseBodyLayer() {
      MeshDefinition var0 = (new MeshDefinition()).transformed((var0x) -> var0x.translated(0.0F, 0.0F, 0.0F));
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 19).addBox(-3.0F, -4.0F, -4.525F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 15).addBox(-4.0F, -3.0F, -3.525F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 2.325F));
      var2.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(3, 18).addBox(-4.0F, -3.0F, -2.2F, 8.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -4.325F, 0.0F, 0.0F, -3.1416F));
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(37, 8).addBox(-1.0F, -7.0F, -3.3F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F)).texOffs(37, 0).addBox(-2.0F, -11.0F, -4.3F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)).texOffs(0, 0).addBox(-4.0F, -3.0F, -7.325F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(56, 0).addBox(-1.0F, 0.0F, -8.325F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -0.2F));
      PartDefinition var4 = var2.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -5.6F, -1.8F, 0.4363F, 0.0F, 0.0F));
      var4.addOrReplaceChild("right_arm_r1", CubeListBuilder.create().texOffs(36, 16).addBox(-3.075F, -0.9733F, -1.9966F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0893F, 0.1198F, -1.0472F, 0.0F, 0.0F));
      PartDefinition var5 = var2.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -5.6F, -1.7F, 0.4363F, 0.0F, 0.0F));
      var5.addOrReplaceChild("left_arm_r1", CubeListBuilder.create().texOffs(50, 16).addBox(0.075F, -1.0443F, -1.8997F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0015F, -0.0808F, -1.0472F, 0.0F, 0.0F));
      PartDefinition var6 = var1.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.1F, -2.1F, -2.075F));
      var6.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, 0.975F, 0.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.05F, -1.9F, 1.075F, -1.5708F, 0.0F, 0.0F));
      PartDefinition var7 = var1.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, -2.0F, -2.075F));
      var7.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(16, 27).addBox(-2.0F, 0.975F, 0.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.05F, -2.0F, 1.075F, -1.5708F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createStarPoseBodyLayer() {
      MeshDefinition var0 = (new MeshDefinition()).transformed((var0x) -> var0x.translated(0.0F, 0.0F, 0.0F));
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(56, 0).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(37, 8).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F)).texOffs(37, 0).addBox(-2.0F, -13.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)), PartPose.offset(0.0F, -6.0F, 0.0F));
      PartDefinition var3 = var2.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-4.0F, -6.0F, 0.0F));
      var3.addOrReplaceChild("right_arm_r1", CubeListBuilder.create().texOffs(36, 16).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.9199F));
      var3.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(-1.0F, 7.4F, -1.0F));
      PartDefinition var4 = var2.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(4.0F, -6.0F, 0.0F));
      var4.addOrReplaceChild("left_arm_r1", CubeListBuilder.create().texOffs(50, 16).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -1.9199F));
      PartDefinition var5 = var1.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-3.0F, -5.0F, 0.0F));
      var5.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.35F, 2.0F, 0.01F, 0.0F, 0.0F, 0.2618F));
      PartDefinition var6 = var1.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.0F, -5.0F, 0.0F));
      var6.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(16, 27).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.65F, 2.0F, 0.0F, 0.0F, 0.0F, -0.2618F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createEyesLayer() {
      return createBodyLayer().apply((var0) -> {
         var0.getRoot().retainPartsAndChildren(Set.of("eyes"));
         return var0;
      });
   }

   public void setupAnim(CopperGolemRenderState var1) {
      super.setupAnim(var1);
      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      if (var1.rightHandItemState.isEmpty() && var1.leftHandItemState.isEmpty()) {
         this.walkAnimation.applyWalk(var1.walkAnimationPos, var1.walkAnimationSpeed, 2.0F, 2.5F);
      } else {
         this.walkWithItemAnimation.applyWalk(var1.walkAnimationPos, var1.walkAnimationSpeed, 2.0F, 2.5F);
         this.poseHeldItemArmsIfStill();
      }

      this.idleAnimation.apply(var1.idleAnimationState, var1.ageInTicks);
      this.interactionGetItem.apply(var1.interactionGetItem, var1.ageInTicks);
      this.interactionGetNoItem.apply(var1.interactionGetNoItem, var1.ageInTicks);
      this.interactionDropItem.apply(var1.interactionDropItem, var1.ageInTicks);
      this.interactionDropNoItem.apply(var1.interactionDropNoItem, var1.ageInTicks);
   }

   public void translateToHand(CopperGolemRenderState var1, HumanoidArm var2, PoseStack var3) {
      this.root.translateAndRotate(var3);
      this.body.translateAndRotate(var3);
      ModelPart var4 = var2 == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
      var4.translateAndRotate(var3);
      if (var1.copperGolemState.equals(CopperGolemState.IDLE)) {
         var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var2 == HumanoidArm.RIGHT ? -90.0F : 90.0F));
         var3.translate(0.0F, 0.0F, 0.125F);
      } else {
         var3.scale(0.55F, 0.55F, 0.55F);
         var3.translate(-0.125F, 0.3125F, -0.1875F);
      }

   }

   public ModelPart getHead() {
      return this.head;
   }

   public void translateToHead(PoseStack var1) {
      this.body.translateAndRotate(var1);
      this.head.translateAndRotate(var1);
      var1.translate(0.0F, 0.125F, 0.0F);
      var1.scale(1.0625F, 1.0625F, 1.0625F);
   }

   public void applyBlockOnAntennaTransform(PoseStack var1) {
      this.root.translateAndRotate(var1);
      this.body.translateAndRotate(var1);
      this.head.translateAndRotate(var1);
      var1.translate(0.0, -2.25, 0.0);
   }

   private void poseHeldItemArmsIfStill() {
      this.rightArm.xRot = Math.min(this.rightArm.xRot, -0.87266463F);
      this.leftArm.xRot = Math.min(this.leftArm.xRot, -0.87266463F);
      this.rightArm.yRot = Math.min(this.rightArm.yRot, -0.1134464F);
      this.leftArm.yRot = Math.max(this.leftArm.yRot, 0.1134464F);
      this.rightArm.zRot = Math.min(this.rightArm.zRot, -0.064577185F);
      this.leftArm.zRot = Math.max(this.leftArm.zRot, 0.064577185F);
   }
}
