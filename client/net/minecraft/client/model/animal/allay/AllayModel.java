package net.minecraft.client.model.animal.allay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class AllayModel extends EntityModel<AllayRenderState> implements ArmedModel<AllayRenderState> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart right_arm;
   private final ModelPart left_arm;
   private final ModelPart right_wing;
   private final ModelPart left_wing;
   private static final float FLYING_ANIMATION_X_ROT = 0.7853982F;
   private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -1.134464F;
   private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = -1.0471976F;

   public AllayModel(final ModelPart root) {
      super(root.getChild("root"), RenderTypes::entityTranslucent);
      this.head = this.root.getChild("head");
      this.body = this.root.getChild("body");
      this.right_arm = this.body.getChild("right_arm");
      this.left_arm = this.body.getChild("left_arm");
      this.right_wing = this.body.getChild("right_wing");
      this.left_wing = this.body.getChild("left_wing");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, 0.0F));
      root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.99F, 0.0F));
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 16).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, -4.0F, 0.0F));
      body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(-1.75F, 0.5F, 0.0F));
      body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(1.75F, 0.5F, 0.0F));
      body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.6F));
      body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.6F));
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void setupAnim(final AllayRenderState state) {
      super.setupAnim(state);
      float animationSpeed = state.walkAnimationSpeed;
      float animationPos = state.walkAnimationPos;
      float flapSpeed = state.ageInTicks * 20.0F * 0.017453292F + animationPos;
      float flapAmount = Mth.cos((double)flapSpeed) * 3.1415927F * 0.15F + animationSpeed;
      float idleBobSpeed = state.ageInTicks * 9.0F * 0.017453292F;
      float flyingFactor = Math.min(animationSpeed / 0.3F, 1.0F);
      float idleBobFactor = 1.0F - flyingFactor;
      float holdingItemFactor = state.holdingAnimationProgress;
      if (state.isDancing) {
         float danceSpeed = state.ageInTicks * 8.0F * 0.017453292F + animationSpeed;
         float danceFrequency = Mth.cos((double)danceSpeed) * 16.0F * 0.017453292F;
         float spinningRotation = state.spinningProgress;
         float headTiltZ = Mth.cos((double)danceSpeed) * 14.0F * 0.017453292F;
         float headTiltY = Mth.cos((double)danceSpeed) * 30.0F * 0.017453292F;
         this.root.yRot = state.isSpinning ? 12.566371F * spinningRotation : this.root.yRot;
         this.root.zRot = danceFrequency * (1.0F - spinningRotation);
         this.head.yRot = headTiltY * (1.0F - spinningRotation);
         this.head.zRot = headTiltZ * (1.0F - spinningRotation);
      } else {
         this.head.xRot = state.xRot * 0.017453292F;
         this.head.yRot = state.yRot * 0.017453292F;
      }

      this.right_wing.xRot = 0.43633232F * (1.0F - flyingFactor);
      this.right_wing.yRot = -0.7853982F + flapAmount;
      this.left_wing.xRot = 0.43633232F * (1.0F - flyingFactor);
      this.left_wing.yRot = 0.7853982F - flapAmount;
      this.body.xRot = flyingFactor * 0.7853982F;
      float armFlyingRotX = holdingItemFactor * Mth.lerp(flyingFactor, -1.0471976F, -1.134464F);
      ModelPart var10000 = this.root;
      var10000.y += (float)Math.cos((double)idleBobSpeed) * 0.25F * idleBobFactor;
      this.right_arm.xRot = armFlyingRotX;
      this.left_arm.xRot = armFlyingRotX;
      float armIdleBobFactor = idleBobFactor * (1.0F - holdingItemFactor);
      float armIdleBobAmount = 0.43633232F - Mth.cos((double)(idleBobSpeed + 4.712389F)) * 3.1415927F * 0.075F * armIdleBobFactor;
      this.left_arm.zRot = -armIdleBobAmount;
      this.right_arm.zRot = armIdleBobAmount;
      this.right_arm.yRot = 0.27925268F * holdingItemFactor;
      this.left_arm.yRot = -0.27925268F * holdingItemFactor;
   }

   public void translateToHand(final AllayRenderState state, final HumanoidArm arm, final PoseStack poseStack) {
      float yOffset = 1.0F;
      float zOffset = 3.0F;
      this.root.translateAndRotate(poseStack);
      this.body.translateAndRotate(poseStack);
      poseStack.translate(0.0F, 0.0625F, 0.1875F);
      poseStack.rotate(Axis.XP, this.right_arm.xRot);
      poseStack.scale(0.7F, 0.7F, 0.7F);
      poseStack.translate(0.0625F, 0.0F, 0.0F);
   }
}
