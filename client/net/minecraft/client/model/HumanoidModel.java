package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class HumanoidModel<T extends HumanoidRenderState> extends EntityModel<T> implements ArmedModel, HeadedModel {
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
   private final ModelPart root;
   public final ModelPart head;
   public final ModelPart hat;
   public final ModelPart body;
   public final ModelPart rightArm;
   public final ModelPart leftArm;
   public final ModelPart rightLeg;
   public final ModelPart leftLeg;

   public HumanoidModel(ModelPart var1) {
      this(var1, RenderType::entityCutoutNoCull);
   }

   public HumanoidModel(ModelPart var1, Function<ResourceLocation, RenderType> var2) {
      super(var2);
      this.root = var1;
      this.head = var1.getChild("head");
      this.hat = this.head.getChild("hat");
      this.body = var1.getChild("body");
      this.rightArm = var1.getChild("right_arm");
      this.leftArm = var1.getChild("left_arm");
      this.rightLeg = var1.getChild("right_leg");
      this.leftLeg = var1.getChild("left_leg");
   }

   public static MeshDefinition createMesh(CubeDeformation var0, float var1) {
      MeshDefinition var2 = new MeshDefinition();
      PartDefinition var3 = var2.getRoot();
      PartDefinition var4 = var3.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0), PartPose.offset(0.0F, 0.0F + var1, 0.0F)
      );
      var4.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0.extend(0.5F)), PartPose.ZERO);
      var3.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var0), PartPose.offset(0.0F, 0.0F + var1, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(-5.0F, 2.0F + var1, 0.0F)
      );
      var3.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0),
         PartPose.offset(5.0F, 2.0F + var1, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(-1.9F, 12.0F + var1, 0.0F)
      );
      var3.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0),
         PartPose.offset(1.9F, 12.0F + var1, 0.0F)
      );
      return var2;
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   protected HumanoidModel.ArmPose getArmPose(T var1, HumanoidArm var2) {
      return HumanoidModel.ArmPose.EMPTY;
   }

   public void setupAnim(T var1) {
      HumanoidModel.ArmPose var2 = this.getArmPose((T)var1, HumanoidArm.LEFT);
      HumanoidModel.ArmPose var3 = this.getArmPose((T)var1, HumanoidArm.RIGHT);
      float var4 = var1.swimAmount;
      this.body.resetPose();
      this.head.resetPose();
      this.hat.resetPose();
      this.rightLeg.resetPose();
      this.leftLeg.resetPose();
      this.rightArm.resetPose();
      this.leftArm.resetPose();
      boolean var5 = var1.isFallFlying;
      this.head.yRot = var1.yRot * 0.017453292F;
      if (var5) {
         this.head.xRot = -0.7853982F;
      } else if (var4 > 0.0F) {
         if (var1.isSwimming) {
            this.head.xRot = Mth.rotLerpRad(var4, this.head.xRot, -0.7853982F);
         } else {
            this.head.xRot = Mth.rotLerpRad(var4, this.head.xRot, var1.xRot * 0.017453292F);
         }
      } else {
         this.head.xRot = var1.xRot * 0.017453292F;
      }

      float var6 = var1.walkAnimationPos;
      float var7 = var1.walkAnimationSpeed;
      this.rightArm.xRot = Mth.cos(var6 * 0.6662F + 3.1415927F) * 2.0F * var7 * 0.5F / var1.speedValue;
      this.leftArm.xRot = Mth.cos(var6 * 0.6662F) * 2.0F * var7 * 0.5F / var1.speedValue;
      this.rightLeg.xRot = Mth.cos(var6 * 0.6662F) * 1.4F * var7 / var1.speedValue;
      this.leftLeg.xRot = Mth.cos(var6 * 0.6662F + 3.1415927F) * 1.4F * var7 / var1.speedValue;
      this.rightLeg.yRot = 0.005F;
      this.leftLeg.yRot = -0.005F;
      this.rightLeg.zRot = 0.005F;
      this.leftLeg.zRot = -0.005F;
      if (var1.isPassenger) {
         this.rightArm.xRot += -0.62831855F;
         this.leftArm.xRot += -0.62831855F;
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = 0.31415927F;
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = -0.31415927F;
         this.leftLeg.zRot = -0.07853982F;
      }

      boolean var8 = var1.mainArm == HumanoidArm.RIGHT;
      if (var1.isUsingItem) {
         boolean var9 = var1.useItemHand == InteractionHand.MAIN_HAND;
         if (var9 == var8) {
            this.poseRightArm((T)var1, var3);
         } else {
            this.poseLeftArm((T)var1, var2);
         }
      } else {
         boolean var15 = var8 ? var2.isTwoHanded() : var3.isTwoHanded();
         if (var8 != var15) {
            this.poseLeftArm((T)var1, var2);
            this.poseRightArm((T)var1, var3);
         } else {
            this.poseRightArm((T)var1, var3);
            this.poseLeftArm((T)var1, var2);
         }
      }

      this.setupAttackAnimation((T)var1, var1.ageInTicks);
      if (var1.isCrouching) {
         this.body.xRot = 0.5F;
         this.rightArm.xRot += 0.4F;
         this.leftArm.xRot += 0.4F;
         this.rightLeg.z += 4.0F;
         this.leftLeg.z += 4.0F;
         this.head.y += 4.2F;
         this.body.y += 3.2F;
         this.leftArm.y += 3.2F;
         this.rightArm.y += 3.2F;
      }

      if (var3 != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.rightArm, var1.ageInTicks, 1.0F);
      }

      if (var2 != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.leftArm, var1.ageInTicks, -1.0F);
      }

      if (var4 > 0.0F) {
         float var16 = var6 % 26.0F;
         HumanoidArm var10 = var1.attackArm;
         float var11 = var10 == HumanoidArm.RIGHT && var1.attackTime > 0.0F ? 0.0F : var4;
         float var12 = var10 == HumanoidArm.LEFT && var1.attackTime > 0.0F ? 0.0F : var4;
         if (!var1.isUsingItem) {
            if (var16 < 14.0F) {
               this.leftArm.xRot = Mth.rotLerpRad(var12, this.leftArm.xRot, 0.0F);
               this.rightArm.xRot = Mth.lerp(var11, this.rightArm.xRot, 0.0F);
               this.leftArm.yRot = Mth.rotLerpRad(var12, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(var11, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = Mth.rotLerpRad(
                  var12, this.leftArm.zRot, 3.1415927F + 1.8707964F * this.quadraticArmUpdate(var16) / this.quadraticArmUpdate(14.0F)
               );
               this.rightArm.zRot = Mth.lerp(
                  var11, this.rightArm.zRot, 3.1415927F - 1.8707964F * this.quadraticArmUpdate(var16) / this.quadraticArmUpdate(14.0F)
               );
            } else if (var16 >= 14.0F && var16 < 22.0F) {
               float var17 = (var16 - 14.0F) / 8.0F;
               this.leftArm.xRot = Mth.rotLerpRad(var12, this.leftArm.xRot, 1.5707964F * var17);
               this.rightArm.xRot = Mth.lerp(var11, this.rightArm.xRot, 1.5707964F * var17);
               this.leftArm.yRot = Mth.rotLerpRad(var12, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(var11, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = Mth.rotLerpRad(var12, this.leftArm.zRot, 5.012389F - 1.8707964F * var17);
               this.rightArm.zRot = Mth.lerp(var11, this.rightArm.zRot, 1.2707963F + 1.8707964F * var17);
            } else if (var16 >= 22.0F && var16 < 26.0F) {
               float var13 = (var16 - 22.0F) / 4.0F;
               this.leftArm.xRot = Mth.rotLerpRad(var12, this.leftArm.xRot, 1.5707964F - 1.5707964F * var13);
               this.rightArm.xRot = Mth.lerp(var11, this.rightArm.xRot, 1.5707964F - 1.5707964F * var13);
               this.leftArm.yRot = Mth.rotLerpRad(var12, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(var11, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = Mth.rotLerpRad(var12, this.leftArm.zRot, 3.1415927F);
               this.rightArm.zRot = Mth.lerp(var11, this.rightArm.zRot, 3.1415927F);
            }
         }

         float var18 = 0.3F;
         float var14 = 0.33333334F;
         this.leftLeg.xRot = Mth.lerp(var4, this.leftLeg.xRot, 0.3F * Mth.cos(var6 * 0.33333334F + 3.1415927F));
         this.rightLeg.xRot = Mth.lerp(var4, this.rightLeg.xRot, 0.3F * Mth.cos(var6 * 0.33333334F));
      }
   }

   private void poseRightArm(T var1, HumanoidModel.ArmPose var2) {
      switch (var2) {
         case EMPTY:
            this.rightArm.yRot = 0.0F;
            break;
         case ITEM:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.31415927F;
            this.rightArm.yRot = 0.0F;
            break;
         case BLOCK:
            this.poseBlockingArm(this.rightArm, true);
            break;
         case BOW_AND_ARROW:
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
            this.rightArm.xRot = -1.5707964F + this.head.xRot;
            this.leftArm.xRot = -1.5707964F + this.head.xRot;
            break;
         case THROW_SPEAR:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 3.1415927F;
            this.rightArm.yRot = 0.0F;
            break;
         case CROSSBOW_CHARGE:
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, var1.maxCrossbowChargeDuration, var1.ticksUsingItem, true);
            break;
         case CROSSBOW_HOLD:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
            break;
         case SPYGLASS:
            this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (var1.isCrouching ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            this.rightArm.yRot = this.head.yRot - 0.2617994F;
            break;
         case TOOT_HORN:
            this.rightArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
            this.rightArm.yRot = this.head.yRot - 0.5235988F;
            break;
         case BRUSH:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.62831855F;
            this.rightArm.yRot = 0.0F;
      }
   }

   private void poseLeftArm(T var1, HumanoidModel.ArmPose var2) {
      switch (var2) {
         case EMPTY:
            this.leftArm.yRot = 0.0F;
            break;
         case ITEM:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.31415927F;
            this.leftArm.yRot = 0.0F;
            break;
         case BLOCK:
            this.poseBlockingArm(this.leftArm, false);
            break;
         case BOW_AND_ARROW:
            this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
            this.leftArm.yRot = 0.1F + this.head.yRot;
            this.rightArm.xRot = -1.5707964F + this.head.xRot;
            this.leftArm.xRot = -1.5707964F + this.head.xRot;
            break;
         case THROW_SPEAR:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 3.1415927F;
            this.leftArm.yRot = 0.0F;
            break;
         case CROSSBOW_CHARGE:
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, var1.maxCrossbowChargeDuration, var1.ticksUsingItem, false);
            break;
         case CROSSBOW_HOLD:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
            break;
         case SPYGLASS:
            this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (var1.isCrouching ? 0.2617994F : 0.0F), -2.4F, 3.3F);
            this.leftArm.yRot = this.head.yRot + 0.2617994F;
            break;
         case TOOT_HORN:
            this.leftArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
            this.leftArm.yRot = this.head.yRot + 0.5235988F;
            break;
         case BRUSH:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.62831855F;
            this.leftArm.yRot = 0.0F;
      }
   }

   private void poseBlockingArm(ModelPart var1, boolean var2) {
      var1.xRot = var1.xRot * 0.5F - 0.9424779F + Mth.clamp(this.head.xRot, -1.3962634F, 0.43633232F);
      var1.yRot = (var2 ? -30.0F : 30.0F) * 0.017453292F + Mth.clamp(this.head.yRot, -0.5235988F, 0.5235988F);
   }

   protected void setupAttackAnimation(T var1, float var2) {
      float var3 = var1.attackTime;
      if (!(var3 <= 0.0F)) {
         HumanoidArm var4 = var1.attackArm;
         ModelPart var5 = this.getArm(var4);
         this.body.yRot = Mth.sin(Mth.sqrt(var3) * 6.2831855F) * 0.2F;
         if (var4 == HumanoidArm.LEFT) {
            this.body.yRot *= -1.0F;
         }

         this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
         this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
         this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
         this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
         this.rightArm.yRot = this.rightArm.yRot + this.body.yRot;
         this.leftArm.yRot = this.leftArm.yRot + this.body.yRot;
         this.leftArm.xRot = this.leftArm.xRot + this.body.yRot;
         float var6 = 1.0F - var3;
         var6 *= var6;
         var6 *= var6;
         var6 = 1.0F - var6;
         float var7 = Mth.sin(var6 * 3.1415927F);
         float var8 = Mth.sin(var3 * 3.1415927F) * -(this.head.xRot - 0.7F) * 0.75F;
         var5.xRot -= var7 * 1.2F + var8;
         var5.yRot = var5.yRot + this.body.yRot * 2.0F;
         var5.zRot = var5.zRot + Mth.sin(var3 * 3.1415927F) * -0.4F;
      }
   }

   private float quadraticArmUpdate(float var1) {
      return -65.0F * var1 + var1 * var1;
   }

   public void copyPropertiesTo(HumanoidModel<T> var1) {
      var1.head.copyFrom(this.head);
      var1.body.copyFrom(this.body);
      var1.rightArm.copyFrom(this.rightArm);
      var1.leftArm.copyFrom(this.leftArm);
      var1.rightLeg.copyFrom(this.rightLeg);
      var1.leftLeg.copyFrom(this.leftLeg);
   }

   public void setAllVisible(boolean var1) {
      this.head.visible = var1;
      this.hat.visible = var1;
      this.body.visible = var1;
      this.rightArm.visible = var1;
      this.leftArm.visible = var1;
      this.rightLeg.visible = var1;
      this.leftLeg.visible = var1;
   }

   @Override
   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      this.root.translateAndRotate(var2);
      this.getArm(var1).translateAndRotate(var2);
   }

   protected ModelPart getArm(HumanoidArm var1) {
      return var1 == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
   }

   @Override
   public ModelPart getHead() {
      return this.head;
   }

   public static enum ArmPose {
      EMPTY(false),
      ITEM(false),
      BLOCK(false),
      BOW_AND_ARROW(true),
      THROW_SPEAR(false),
      CROSSBOW_CHARGE(true),
      CROSSBOW_HOLD(true),
      SPYGLASS(false),
      TOOT_HORN(false),
      BRUSH(false);

      private final boolean twoHanded;

      private ArmPose(final boolean nullxx) {
         this.twoHanded = nullxx;
      }

      public boolean isTwoHanded() {
         return this.twoHanded;
      }
   }
}
