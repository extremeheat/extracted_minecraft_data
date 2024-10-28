package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.FrogAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogModel<T extends Frog> extends HierarchicalModel<T> {
   private static final float MAX_WALK_ANIMATION_SPEED = 1.5F;
   private static final float MAX_SWIM_ANIMATION_SPEED = 1.0F;
   private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart head;
   private final ModelPart eyes;
   private final ModelPart tongue;
   private final ModelPart leftArm;
   private final ModelPart rightArm;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;
   private final ModelPart croakingBody;

   public FrogModel(ModelPart var1) {
      super();
      this.root = var1.getChild("root");
      this.body = this.root.getChild("body");
      this.head = this.body.getChild("head");
      this.eyes = this.head.getChild("eyes");
      this.tongue = this.body.getChild("tongue");
      this.leftArm = this.body.getChild("left_arm");
      this.rightArm = this.body.getChild("right_arm");
      this.leftLeg = this.root.getChild("left_leg");
      this.rightLeg = this.root.getChild("right_leg");
      this.croakingBody = this.body.getChild("croaking_body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition var3 = var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 1).addBox(-3.5F, -2.0F, -8.0F, 7.0F, 3.0F, 9.0F).texOffs(23, 22).addBox(-3.5F, -1.0F, -8.0F, 7.0F, 0.0F, 9.0F), PartPose.offset(0.0F, -2.0F, 4.0F));
      PartDefinition var4 = var3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(23, 13).addBox(-3.5F, -1.0F, -7.0F, 7.0F, 0.0F, 9.0F).texOffs(0, 13).addBox(-3.5F, -2.0F, -7.0F, 7.0F, 3.0F, 9.0F), PartPose.offset(0.0F, -2.0F, -1.0F));
      PartDefinition var5 = var4.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(-0.5F, 0.0F, 2.0F));
      var5.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(-1.5F, -3.0F, -6.5F));
      var5.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 5).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(2.5F, -3.0F, -6.5F));
      var3.addOrReplaceChild("croaking_body", CubeListBuilder.create().texOffs(26, 5).addBox(-3.5F, -0.1F, -2.9F, 7.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, -1.0F, -5.0F));
      PartDefinition var6 = var3.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(17, 13).addBox(-2.0F, 0.0F, -7.1F, 4.0F, 0.0F, 7.0F), PartPose.offset(0.0F, -1.01F, 1.0F));
      PartDefinition var7 = var3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F), PartPose.offset(4.0F, -1.0F, -6.5F));
      var7.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(18, 40).addBox(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(0.0F, 3.0F, -1.0F));
      PartDefinition var8 = var3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F), PartPose.offset(-4.0F, -1.0F, -6.5F));
      var8.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(2, 40).addBox(-4.0F, 0.01F, -5.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(0.0F, 3.0F, 0.0F));
      PartDefinition var9 = var2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(14, 25).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F), PartPose.offset(3.5F, -3.0F, 4.0F));
      var9.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(2, 32).addBox(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(2.0F, 3.0F, 0.0F));
      PartDefinition var10 = var2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F), PartPose.offset(-3.5F, -3.0F, 4.0F));
      var10.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(18, 32).addBox(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(-2.0F, 3.0F, 0.0F));
      return LayerDefinition.create(var0, 48, 48);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      this.animate(var1.jumpAnimationState, FrogAnimation.FROG_JUMP, var4);
      this.animate(var1.croakAnimationState, FrogAnimation.FROG_CROAK, var4);
      this.animate(var1.tongueAnimationState, FrogAnimation.FROG_TONGUE, var4);
      if (var1.isInWaterOrBubble()) {
         this.animateWalk(FrogAnimation.FROG_SWIM, var2, var3, 1.0F, 2.5F);
      } else {
         this.animateWalk(FrogAnimation.FROG_WALK, var2, var3, 1.5F, 2.5F);
      }

      this.animate(var1.swimIdleAnimationState, FrogAnimation.FROG_IDLE_WATER, var4);
      this.croakingBody.visible = var1.croakAnimationState.isStarted();
   }

   public ModelPart root() {
      return this.root;
   }
}
