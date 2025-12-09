package net.minecraft.client.model.monster.warden;

import java.util.Set;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.WardenAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.WardenRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public class WardenModel extends EntityModel<WardenRenderState> {
   private static final float DEFAULT_ARM_X_Y = 13.0F;
   private static final float DEFAULT_ARM_Z = 1.0F;
   protected final ModelPart bone;
   protected final ModelPart body;
   protected final ModelPart head;
   protected final ModelPart rightTendril;
   protected final ModelPart leftTendril;
   protected final ModelPart leftLeg;
   protected final ModelPart leftArm;
   protected final ModelPart leftRibcage;
   protected final ModelPart rightArm;
   protected final ModelPart rightLeg;
   protected final ModelPart rightRibcage;
   private final KeyframeAnimation attackAnimation;
   private final KeyframeAnimation sonicBoomAnimation;
   private final KeyframeAnimation diggingAnimation;
   private final KeyframeAnimation emergeAnimation;
   private final KeyframeAnimation roarAnimation;
   private final KeyframeAnimation sniffAnimation;

   public WardenModel(ModelPart var1) {
      super(var1, RenderTypes::entityCutoutNoCull);
      this.bone = var1.getChild("bone");
      this.body = this.bone.getChild("body");
      this.head = this.body.getChild("head");
      this.rightLeg = this.bone.getChild("right_leg");
      this.leftLeg = this.bone.getChild("left_leg");
      this.rightArm = this.body.getChild("right_arm");
      this.leftArm = this.body.getChild("left_arm");
      this.rightTendril = this.head.getChild("right_tendril");
      this.leftTendril = this.head.getChild("left_tendril");
      this.rightRibcage = this.body.getChild("right_ribcage");
      this.leftRibcage = this.body.getChild("left_ribcage");
      this.attackAnimation = WardenAnimation.WARDEN_ATTACK.bake(var1);
      this.sonicBoomAnimation = WardenAnimation.WARDEN_SONIC_BOOM.bake(var1);
      this.diggingAnimation = WardenAnimation.WARDEN_DIG.bake(var1);
      this.emergeAnimation = WardenAnimation.WARDEN_EMERGE.bake(var1);
      this.roarAnimation = WardenAnimation.WARDEN_ROAR.bake(var1);
      this.sniffAnimation = WardenAnimation.WARDEN_SNIFF.bake(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition var3 = var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F), PartPose.offset(0.0F, -21.0F, 0.0F));
      var3.addOrReplaceChild("right_ribcage", CubeListBuilder.create().texOffs(90, 11).addBox(-2.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F), PartPose.offset(-7.0F, -2.0F, -4.0F));
      var3.addOrReplaceChild("left_ribcage", CubeListBuilder.create().texOffs(90, 11).mirror().addBox(-7.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F).mirror(false), PartPose.offset(7.0F, -2.0F, -4.0F));
      PartDefinition var4 = var3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), PartPose.offset(0.0F, -13.0F, 0.0F));
      var4.addOrReplaceChild("right_tendril", CubeListBuilder.create().texOffs(52, 32).addBox(-16.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F), PartPose.offset(-8.0F, -12.0F, 0.0F));
      var4.addOrReplaceChild("left_tendril", CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F), PartPose.offset(8.0F, -12.0F, 0.0F));
      var3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 50).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F), PartPose.offset(-13.0F, -13.0F, 1.0F));
      var3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 58).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F), PartPose.offset(13.0F, -13.0F, 1.0F));
      var2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(76, 48).addBox(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), PartPose.offset(-5.9F, -13.0F, 0.0F));
      var2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(76, 76).addBox(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), PartPose.offset(5.9F, -13.0F, 0.0F));
      return LayerDefinition.create(var0, 128, 128);
   }

   public static LayerDefinition createTendrilsLayer() {
      return createBodyLayer().apply((var0) -> {
         var0.getRoot().retainExactParts(Set.of("left_tendril", "right_tendril"));
         return var0;
      });
   }

   public static LayerDefinition createHeartLayer() {
      return createBodyLayer().apply((var0) -> {
         var0.getRoot().retainExactParts(Set.of("body"));
         return var0;
      });
   }

   public static LayerDefinition createBioluminescentLayer() {
      return createBodyLayer().apply((var0) -> {
         var0.getRoot().retainExactParts(Set.of("head", "left_arm", "right_arm", "left_leg", "right_leg"));
         return var0;
      });
   }

   public static LayerDefinition createPulsatingSpotsLayer() {
      return createBodyLayer().apply((var0) -> {
         var0.getRoot().retainExactParts(Set.of("body", "head", "left_arm", "right_arm", "left_leg", "right_leg"));
         return var0;
      });
   }

   public void setupAnim(WardenRenderState var1) {
      super.setupAnim(var1);
      this.animateHeadLookTarget(var1.yRot, var1.xRot);
      this.animateWalk(var1.walkAnimationPos, var1.walkAnimationSpeed);
      this.animateIdlePose(var1.ageInTicks);
      this.animateTendrils(var1, var1.ageInTicks);
      this.attackAnimation.apply(var1.attackAnimationState, var1.ageInTicks);
      this.sonicBoomAnimation.apply(var1.sonicBoomAnimationState, var1.ageInTicks);
      this.diggingAnimation.apply(var1.diggingAnimationState, var1.ageInTicks);
      this.emergeAnimation.apply(var1.emergeAnimationState, var1.ageInTicks);
      this.roarAnimation.apply(var1.roarAnimationState, var1.ageInTicks);
      this.sniffAnimation.apply(var1.sniffAnimationState, var1.ageInTicks);
   }

   private void animateHeadLookTarget(float var1, float var2) {
      this.head.xRot = var2 * 0.017453292F;
      this.head.yRot = var1 * 0.017453292F;
   }

   private void animateIdlePose(float var1) {
      float var2 = var1 * 0.1F;
      float var3 = Mth.cos((double)var2);
      float var4 = Mth.sin((double)var2);
      ModelPart var10000 = this.head;
      var10000.zRot += 0.06F * var3;
      var10000 = this.head;
      var10000.xRot += 0.06F * var4;
      var10000 = this.body;
      var10000.zRot += 0.025F * var4;
      var10000 = this.body;
      var10000.xRot += 0.025F * var3;
   }

   private void animateWalk(float var1, float var2) {
      float var3 = Math.min(0.5F, 3.0F * var2);
      float var4 = var1 * 0.8662F;
      float var5 = Mth.cos((double)var4);
      float var6 = Mth.sin((double)var4);
      float var7 = Math.min(0.35F, var3);
      ModelPart var10000 = this.head;
      var10000.zRot += 0.3F * var6 * var3;
      var10000 = this.head;
      var10000.xRot += 1.2F * Mth.cos((double)(var4 + 1.5707964F)) * var7;
      this.body.zRot = 0.1F * var6 * var3;
      this.body.xRot = 1.0F * var5 * var7;
      this.leftLeg.xRot = 1.0F * var5 * var3;
      this.rightLeg.xRot = 1.0F * Mth.cos((double)(var4 + 3.1415927F)) * var3;
      this.leftArm.xRot = -(0.8F * var5 * var3);
      this.leftArm.zRot = 0.0F;
      this.rightArm.xRot = -(0.8F * var6 * var3);
      this.rightArm.zRot = 0.0F;
      this.resetArmPoses();
   }

   private void resetArmPoses() {
      this.leftArm.yRot = 0.0F;
      this.leftArm.z = 1.0F;
      this.leftArm.x = 13.0F;
      this.leftArm.y = -13.0F;
      this.rightArm.yRot = 0.0F;
      this.rightArm.z = 1.0F;
      this.rightArm.x = -13.0F;
      this.rightArm.y = -13.0F;
   }

   private void animateTendrils(WardenRenderState var1, float var2) {
      float var3 = var1.tendrilAnimation * (float)(Math.cos((double)var2 * 2.25) * 3.141592653589793 * 0.10000000149011612);
      this.leftTendril.xRot = var3;
      this.rightTendril.xRot = -var3;
   }
}
