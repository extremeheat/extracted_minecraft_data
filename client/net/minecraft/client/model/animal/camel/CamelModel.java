package net.minecraft.client.model.animal.camel;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CamelAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.util.Mth;

public class CamelModel extends EntityModel<CamelRenderState> {
   private static final float MAX_WALK_ANIMATION_SPEED = 2.0F;
   private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
   public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.45F);
   protected final ModelPart head;
   private final KeyframeAnimation walkAnimation;
   private final KeyframeAnimation sitAnimation;
   private final KeyframeAnimation sitPoseAnimation;
   private final KeyframeAnimation standupAnimation;
   private final KeyframeAnimation idleAnimation;
   private final KeyframeAnimation dashAnimation;

   public CamelModel(ModelPart var1) {
      super(var1);
      ModelPart var2 = var1.getChild("body");
      this.head = var2.getChild("head");
      this.walkAnimation = CamelAnimation.CAMEL_WALK.bake(var1);
      this.sitAnimation = CamelAnimation.CAMEL_SIT.bake(var1);
      this.sitPoseAnimation = CamelAnimation.CAMEL_SIT_POSE.bake(var1);
      this.standupAnimation = CamelAnimation.CAMEL_STANDUP.bake(var1);
      this.idleAnimation = CamelAnimation.CAMEL_IDLE.bake(var1);
      this.dashAnimation = CamelAnimation.CAMEL_DASH.bake(var1);
   }

   public static LayerDefinition createBodyLayer() {
      return LayerDefinition.create(createBodyMesh(), 128, 128);
   }

   protected static MeshDefinition createBodyMesh() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F), PartPose.offset(0.0F, 4.0F, 9.5F));
      var2.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(74, 0).addBox(-4.5F, -5.0F, -5.5F, 9.0F, 5.0F, 11.0F), PartPose.offset(0.0F, -12.0F, -10.0F));
      var2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(122, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F), PartPose.offset(0.0F, -9.0F, 3.5F));
      PartDefinition var3 = var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 24).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F).texOffs(21, 0).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F).texOffs(50, 0).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F), PartPose.offset(0.0F, -3.0F, -19.5F));
      var3.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(2.5F, -21.0F, -9.5F));
      var3.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(67, 0).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(-2.5F, -21.0F, -9.5F));
      var1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(58, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, 9.5F));
      var1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(94, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, 9.5F));
      var1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, -10.5F));
      var1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, -10.5F));
      return var0;
   }

   public void setupAnim(CamelRenderState var1) {
      super.setupAnim(var1);
      this.applyHeadRotation(var1, var1.yRot, var1.xRot);
      this.walkAnimation.applyWalk(var1.walkAnimationPos, var1.walkAnimationSpeed, 2.0F, 2.5F);
      this.sitAnimation.apply(var1.sitAnimationState, var1.ageInTicks);
      this.sitPoseAnimation.apply(var1.sitPoseAnimationState, var1.ageInTicks);
      this.standupAnimation.apply(var1.sitUpAnimationState, var1.ageInTicks);
      this.idleAnimation.apply(var1.idleAnimationState, var1.ageInTicks);
      this.dashAnimation.apply(var1.dashAnimationState, var1.ageInTicks);
   }

   private void applyHeadRotation(CamelRenderState var1, float var2, float var3) {
      var2 = Mth.clamp(var2, -30.0F, 30.0F);
      var3 = Mth.clamp(var3, -25.0F, 45.0F);
      if (var1.jumpCooldown > 0.0F) {
         float var4 = 45.0F * var1.jumpCooldown / 55.0F;
         var3 = Mth.clamp(var3 + var4, -25.0F, 70.0F);
      }

      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
   }
}
