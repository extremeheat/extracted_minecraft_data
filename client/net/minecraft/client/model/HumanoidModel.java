package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class HumanoidModel<T extends HumanoidRenderState> extends EntityModel<T> implements ArmedModel<T>, HeadedModel {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F, Set.of("head"));
   public static final float OVERLAY_SCALE = 0.25F;
   public static final float HAT_OVERLAY_SCALE = 0.5F;
   public static final float LEGGINGS_OVERLAY_SCALE = -0.1F;
   private static final float DUCK_WALK_ROTATION = 0.005F;
   private static final float SPYGLASS_ARM_ROT_Y = 0.2617994F;
   private static final float SPYGLASS_ARM_ROT_X = 1.9198622F;
   private static final float SPYGLASS_ARM_CROUCH_ROT_X = 0.2617994F;
   private static final float HIGHEST_SHIELD_BLOCKING_ANGLE = -1.3962634F;
   private static final float LOWEST_SHIELD_BLOCKING_ANGLE = 0.43633232F;
   private static final float HORIZONTAL_SHIELD_MOVEMENT_LIMIT = 0.5235988F;
   public static final float TOOT_HORN_XROT_BASE = 1.4835298F;
   public static final float TOOT_HORN_YROT_BASE = 0.5235988F;
   public final ModelPart head;
   public final ModelPart hat;
   public final ModelPart body;
   public final ModelPart rightArm;
   public final ModelPart leftArm;
   public final ModelPart rightLeg;
   public final ModelPart leftLeg;

   public HumanoidModel(final ModelPart root) {
      this(root, RenderTypes::entityCutout);
   }

   public HumanoidModel(final ModelPart root, final Function<Identifier, RenderType> renderType) {
      super(root, renderType);
      this.head = root.getChild("head");
      this.hat = this.head.getChild("hat");
      this.body = root.getChild("body");
      this.rightArm = root.getChild("right_arm");
      this.leftArm = root.getChild("left_arm");
      this.rightLeg = root.getChild("right_leg");
      this.leftLeg = root.getChild("left_leg");
   }

   public static MeshDefinition createMesh(final CubeDeformation g, final float yOffset) {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, g), PartPose.offset(0.0F, 0.0F + yOffset, 0.0F));
      head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, g.extend(0.5F)), PartPose.ZERO);
      root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, g), PartPose.offset(0.0F, 0.0F + yOffset, 0.0F));
      root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(-5.0F, 2.0F + yOffset, 0.0F));
      root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(5.0F, 2.0F + yOffset, 0.0F));
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(-1.9F, 12.0F + yOffset, 0.0F));
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(1.9F, 12.0F + yOffset, 0.0F));
      return mesh;
   }

   public static ArmorModelSet<MeshDefinition> createArmorMeshSet(final CubeDeformation innerDeformation, final CubeDeformation outerDeformation) {
      return createArmorMeshSet(HumanoidModel::createBaseArmorMesh, innerDeformation, outerDeformation);
   }

   protected static ArmorModelSet<MeshDefinition> createArmorMeshSet(final Function<CubeDeformation, MeshDefinition> baseFactory, final CubeDeformation innerDeformation, final CubeDeformation outerDeformation) {
      MeshDefinition head = (MeshDefinition)baseFactory.apply(outerDeformation);
      head.getRoot().retainPartsAndChildren(Set.of("head"));
      MeshDefinition chest = (MeshDefinition)baseFactory.apply(outerDeformation);
      chest.getRoot().retainExactParts(Set.of("body", "left_arm", "right_arm"));
      MeshDefinition legs = (MeshDefinition)baseFactory.apply(innerDeformation);
      legs.getRoot().retainExactParts(Set.of("left_leg", "right_leg", "body"));
      MeshDefinition feet = (MeshDefinition)baseFactory.apply(outerDeformation);
      feet.getRoot().retainExactParts(Set.of("left_leg", "right_leg"));
      return new ArmorModelSet<MeshDefinition>(head, chest, legs, feet);
   }

   private static MeshDefinition createBaseArmorMesh(final CubeDeformation g) {
      MeshDefinition mesh = createMesh(g, 0.0F);
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g.extend(-0.1F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g.extend(-0.1F)), PartPose.offset(1.9F, 12.0F, 0.0F));
      return mesh;
   }

   public void setupAnim(final T state) {
      super.setupAnim(state);
      ArmPose leftArmPose = state.leftArmPose;
      ArmPose rightArmPose = state.rightArmPose;
      float swimAmount = state.swimAmount;
      boolean fallFlying = state.isFallFlying;
      this.head.xRot = state.xRot * 0.017453292F;
      this.head.yRot = state.yRot * 0.017453292F;
      if (fallFlying) {
         this.head.xRot = -0.7853982F;
      } else if (swimAmount > 0.0F) {
         this.head.xRot = Mth.rotLerpRad(swimAmount, this.head.xRot, -0.7853982F);
      }

      float animationPos = state.walkAnimationPos;
      float animationSpeed = state.walkAnimationSpeed;
      this.rightArm.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 2.0F * animationSpeed * 0.5F / state.speedValue;
      this.leftArm.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 2.0F * animationSpeed * 0.5F / state.speedValue;
      this.rightLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed / state.speedValue;
      this.leftLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 1.4F * animationSpeed / state.speedValue;
      this.rightLeg.yRot = 0.005F;
      this.leftLeg.yRot = -0.005F;
      this.rightLeg.zRot = 0.005F;
      this.leftLeg.zRot = -0.005F;
      if (state.isPassenger) {
         ModelPart var10000 = this.rightArm;
         var10000.xRot += -0.62831855F;
         var10000 = this.leftArm;
         var10000.xRot += -0.62831855F;
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = 0.31415927F;
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = -0.31415927F;
         this.leftLeg.zRot = -0.07853982F;
      }

      boolean rightHanded = state.mainArm == HumanoidArm.RIGHT;
      if (state.isUsingItem) {
         boolean mainHandUsed = state.useItemHand == InteractionHand.MAIN_HAND;
         if (mainHandUsed == rightHanded) {
            this.poseRightArm(state);
            if (!state.rightArmPose.affectsOffhandPose()) {
               this.poseLeftArm(state);
            }
         } else {
            this.poseLeftArm(state);
            if (!state.leftArmPose.affectsOffhandPose()) {
               this.poseRightArm(state);
            }
         }
      } else {
         boolean twoHandedOffhand = rightHanded ? leftArmPose.isTwoHanded() : rightArmPose.isTwoHanded();
         if (rightHanded != twoHandedOffhand) {
            this.poseLeftArm(state);
            if (!state.leftArmPose.affectsOffhandPose()) {
               this.poseRightArm(state);
            }
         } else {
            this.poseRightArm(state);
            if (!state.rightArmPose.affectsOffhandPose()) {
               this.poseLeftArm(state);
            }
         }
      }

      this.setupAttackAnimation(state);
      if (state.isCrouching) {
         this.body.xRot = 0.5F;
         ModelPart var20 = this.rightArm;
         var20.xRot += 0.4F;
         var20 = this.leftArm;
         var20.xRot += 0.4F;
         var20 = this.rightLeg;
         var20.z += 4.0F;
         var20 = this.leftLeg;
         var20.z += 4.0F;
         var20 = this.head;
         var20.y += 4.2F;
         var20 = this.body;
         var20.y += 3.2F;
         var20 = this.leftArm;
         var20.y += 3.2F;
         var20 = this.rightArm;
         var20.y += 3.2F;
      }

      if (rightArmPose != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.rightArm, state.ageInTicks, 1.0F);
      }

      if (leftArmPose != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.leftArm, state.ageInTicks, -1.0F);
      }

      if (swimAmount > 0.0F) {
         float swimPos = animationPos % 26.0F;
         HumanoidArm attackArm = state.attackArm;
         float rightArmSwimAmount = state.rightArmPose != HumanoidModel.ArmPose.SPEAR && (attackArm != HumanoidArm.RIGHT || !(state.attackTime > 0.0F)) ? swimAmount : 0.0F;
         float leftArmSwimAmount = state.leftArmPose != HumanoidModel.ArmPose.SPEAR && (attackArm != HumanoidArm.LEFT || !(state.attackTime > 0.0F)) ? swimAmount : 0.0F;
         if (!state.isUsingItem) {
            if (swimPos < 14.0F) {
               this.leftArm.xRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.xRot, 0.0F);
               this.rightArm.xRot = Mth.lerp(rightArmSwimAmount, this.rightArm.xRot, 0.0F);
               this.leftArm.yRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(rightArmSwimAmount, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.zRot, 3.1415927F + 1.8707964F * this.quadraticArmUpdate(swimPos) / this.quadraticArmUpdate(14.0F));
               this.rightArm.zRot = Mth.lerp(rightArmSwimAmount, this.rightArm.zRot, 3.1415927F - 1.8707964F * this.quadraticArmUpdate(swimPos) / this.quadraticArmUpdate(14.0F));
            } else if (swimPos >= 14.0F && swimPos < 22.0F) {
               float internalSwimPos = (swimPos - 14.0F) / 8.0F;
               this.leftArm.xRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.xRot, 1.5707964F * internalSwimPos);
               this.rightArm.xRot = Mth.lerp(rightArmSwimAmount, this.rightArm.xRot, 1.5707964F * internalSwimPos);
               this.leftArm.yRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(rightArmSwimAmount, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.zRot, 5.012389F - 1.8707964F * internalSwimPos);
               this.rightArm.zRot = Mth.lerp(rightArmSwimAmount, this.rightArm.zRot, 1.2707963F + 1.8707964F * internalSwimPos);
            } else if (swimPos >= 22.0F && swimPos < 26.0F) {
               float internalSwimPos = (swimPos - 22.0F) / 4.0F;
               this.leftArm.xRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.xRot, 1.5707964F - 1.5707964F * internalSwimPos);
               this.rightArm.xRot = Mth.lerp(rightArmSwimAmount, this.rightArm.xRot, 1.5707964F - 1.5707964F * internalSwimPos);
               this.leftArm.yRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(rightArmSwimAmount, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = Mth.rotLerpRad(leftArmSwimAmount, this.leftArm.zRot, 3.1415927F);
               this.rightArm.zRot = Mth.lerp(rightArmSwimAmount, this.rightArm.zRot, 3.1415927F);
            }
         }

         float amplitude = 0.3F;
         float slowdown = 0.33333334F;
         this.leftLeg.xRot = Mth.lerp(swimAmount, this.leftLeg.xRot, 0.3F * Mth.cos((double)(animationPos * 0.33333334F + 3.1415927F)));
         this.rightLeg.xRot = Mth.lerp(swimAmount, this.rightLeg.xRot, 0.3F * Mth.cos((double)(animationPos * 0.33333334F)));
      }

   }

   private void poseRightArm(final T state) {
      switch (state.rightArmPose.ordinal()) {
         case 0:
            this.rightArm.yRot = 0.0F;
            break;
         case 1:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.31415927F;
            this.rightArm.yRot = 0.0F;
            break;
         case 2:
            this.poseBlockingArm(this.rightArm, true);
            break;
         case 3:
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
            this.rightArm.xRot = -1.5707964F + this.head.xRot;
            this.leftArm.xRot = -1.5707964F + this.head.xRot;
            break;
         case 4:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 3.1415927F;
            this.rightArm.yRot = 0.0F;
            break;
         case 5:
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, state.maxCrossbowChargeDuration, state.ticksUsingItem, true);
            break;
         case 6:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
            break;
         case 7:
            this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (state.isCrouching ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            this.rightArm.yRot = this.head.yRot - 0.2617994F;
            break;
         case 8:
            this.rightArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
            this.rightArm.yRot = this.head.yRot - 0.5235988F;
            break;
         case 9:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.62831855F;
            this.rightArm.yRot = 0.0F;
            break;
         case 10:
            SpearAnimations.thirdPersonHandUse(this.rightArm, this.head, true, state.getUseItemStackForArm(HumanoidArm.RIGHT), state);
      }

   }

   private void poseLeftArm(final T state) {
      switch (state.leftArmPose.ordinal()) {
         case 0:
            this.leftArm.yRot = 0.0F;
            break;
         case 1:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.31415927F;
            this.leftArm.yRot = 0.0F;
            break;
         case 2:
            this.poseBlockingArm(this.leftArm, false);
            break;
         case 3:
            this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
            this.leftArm.yRot = 0.1F + this.head.yRot;
            this.rightArm.xRot = -1.5707964F + this.head.xRot;
            this.leftArm.xRot = -1.5707964F + this.head.xRot;
            break;
         case 4:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 3.1415927F;
            this.leftArm.yRot = 0.0F;
            break;
         case 5:
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, state.maxCrossbowChargeDuration, state.ticksUsingItem, false);
            break;
         case 6:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
            break;
         case 7:
            this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (state.isCrouching ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            this.leftArm.yRot = this.head.yRot + 0.2617994F;
            break;
         case 8:
            this.leftArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
            this.leftArm.yRot = this.head.yRot + 0.5235988F;
            break;
         case 9:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.62831855F;
            this.leftArm.yRot = 0.0F;
            break;
         case 10:
            SpearAnimations.thirdPersonHandUse(this.leftArm, this.head, false, state.getUseItemStackForArm(HumanoidArm.LEFT), state);
      }

   }

   private void poseBlockingArm(final ModelPart arm, final boolean right) {
      arm.xRot = arm.xRot * 0.5F - 0.9424779F + Mth.clamp(this.head.xRot, -1.3962634F, 0.43633232F);
      arm.yRot = (right ? -30.0F : 30.0F) * 0.017453292F + Mth.clamp(this.head.yRot, -0.5235988F, 0.5235988F);
   }

   protected void setupAttackAnimation(final T state) {
      float attackTime = state.attackTime;
      if (!(attackTime <= 0.0F)) {
         this.body.yRot = Mth.sin((double)(Mth.sqrt(attackTime) * 6.2831855F)) * 0.2F;
         if (state.attackArm == HumanoidArm.LEFT) {
            ModelPart var10000 = this.body;
            var10000.yRot *= -1.0F;
         }

         float ageScale = state.ageScale;
         this.rightArm.z = Mth.sin((double)this.body.yRot) * 5.0F * ageScale;
         this.rightArm.x = -Mth.cos((double)this.body.yRot) * 5.0F * ageScale;
         this.leftArm.z = -Mth.sin((double)this.body.yRot) * 5.0F * ageScale;
         this.leftArm.x = Mth.cos((double)this.body.yRot) * 5.0F * ageScale;
         ModelPart var8 = this.rightArm;
         var8.yRot += this.body.yRot;
         var8 = this.leftArm;
         var8.yRot += this.body.yRot;
         var8 = this.leftArm;
         var8.xRot += this.body.yRot;
         switch (state.swingAnimationType) {
            case WHACK:
               float swing = Ease.outQuart(attackTime);
               float aa = Mth.sin((double)(swing * 3.1415927F));
               float bb = Mth.sin((double)(attackTime * 3.1415927F)) * -(this.head.xRot - 0.7F) * 0.75F;
               ModelPart attackArm = this.getArm(state.attackArm);
               attackArm.xRot -= aa * 1.2F + bb;
               attackArm.yRot += this.body.yRot * 2.0F;
               attackArm.zRot += Mth.sin((double)(attackTime * 3.1415927F)) * -0.4F;
            case NONE:
            default:
               break;
            case STAB:
               SpearAnimations.thirdPersonAttackHand(this, state);
         }

      }
   }

   private float quadraticArmUpdate(final float x) {
      return -65.0F * x + x * x;
   }

   public void setAllVisible(final boolean visible) {
      this.head.visible = visible;
      this.hat.visible = visible;
      this.body.visible = visible;
      this.rightArm.visible = visible;
      this.leftArm.visible = visible;
      this.rightLeg.visible = visible;
      this.leftLeg.visible = visible;
   }

   public void translateToHand(final HumanoidRenderState state, final HumanoidArm arm, final PoseStack poseStack) {
      this.root.translateAndRotate(poseStack);
      this.getArm(arm).translateAndRotate(poseStack);
   }

   public ModelPart getArm(final HumanoidArm arm) {
      return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelPart getHead() {
      return this.head;
   }

   public static enum ArmPose {
      EMPTY(false, false),
      ITEM(false, false),
      BLOCK(false, false),
      BOW_AND_ARROW(true, true),
      THROW_TRIDENT(false, true),
      CROSSBOW_CHARGE(true, true),
      CROSSBOW_HOLD(true, true),
      SPYGLASS(false, false),
      TOOT_HORN(false, false),
      BRUSH(false, false),
      SPEAR(false, true) {
         public <S extends ArmedEntityRenderState> void animateUseItem(final S state, final PoseStack poseStack, final float ticksUsingItem, final HumanoidArm arm, final ItemStack actualItem) {
            SpearAnimations.thirdPersonUseItem(state, poseStack, ticksUsingItem, arm, actualItem);
         }
      };

      private final boolean twoHanded;
      private final boolean affectsOffhandPose;

      private ArmPose(final boolean twoHanded, final boolean affectsOffhandPose) {
         this.twoHanded = twoHanded;
         this.affectsOffhandPose = affectsOffhandPose;
      }

      public boolean isTwoHanded() {
         return this.twoHanded;
      }

      public boolean affectsOffhandPose() {
         return this.affectsOffhandPose;
      }

      public <S extends ArmedEntityRenderState> void animateUseItem(final S state, final PoseStack poseStack, final float ticksUsingItem, final HumanoidArm arm, final ItemStack actualItem) {
      }

      // $FF: synthetic method
      private static ArmPose[] $values() {
         return new ArmPose[]{EMPTY, ITEM, BLOCK, BOW_AND_ARROW, THROW_TRIDENT, CROSSBOW_CHARGE, CROSSBOW_HOLD, SPYGLASS, TOOT_HORN, BRUSH, SPEAR};
      }
   }
}
