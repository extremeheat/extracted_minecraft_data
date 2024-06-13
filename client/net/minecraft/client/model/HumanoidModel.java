package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidModel<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
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
   public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
   public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
   public boolean crouching;
   public float swimAmount;

   public HumanoidModel(ModelPart var1) {
      this(var1, RenderType::entityCutoutNoCull);
   }

   public HumanoidModel(ModelPart var1, Function<ResourceLocation, RenderType> var2) {
      super(var2, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
      this.head = var1.getChild("head");
      this.hat = var1.getChild("hat");
      this.body = var1.getChild("body");
      this.rightArm = var1.getChild("right_arm");
      this.leftArm = var1.getChild("left_arm");
      this.rightLeg = var1.getChild("right_leg");
      this.leftLeg = var1.getChild("left_leg");
   }

   public static MeshDefinition createMesh(CubeDeformation var0, float var1) {
      MeshDefinition var2 = new MeshDefinition();
      PartDefinition var3 = var2.getRoot();
      var3.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0), PartPose.offset(0.0F, 0.0F + var1, 0.0F)
      );
      var3.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0.extend(0.5F)),
         PartPose.offset(0.0F, 0.0F + var1, 0.0F)
      );
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
   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   @Override
   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      this.swimAmount = var1.getSwimAmount(var4);
      super.prepareMobModel(var1, var2, var3, var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = var1.getFallFlyingTicks() > 4;
      boolean var8 = var1.isVisuallySwimming();
      this.head.yRot = var5 * 0.017453292F;
      if (var7) {
         this.head.xRot = -0.7853982F;
      } else if (this.swimAmount > 0.0F) {
         if (var8) {
            this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, -0.7853982F);
         } else {
            this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, var6 * 0.017453292F);
         }
      } else {
         this.head.xRot = var6 * 0.017453292F;
      }

      this.body.yRot = 0.0F;
      this.rightArm.z = 0.0F;
      this.rightArm.x = -5.0F;
      this.leftArm.z = 0.0F;
      this.leftArm.x = 5.0F;
      float var9 = 1.0F;
      if (var7) {
         var9 = (float)var1.getDeltaMovement().lengthSqr();
         var9 /= 0.2F;
         var9 *= var9 * var9;
      }

      if (var9 < 1.0F) {
         var9 = 1.0F;
      }

      this.rightArm.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 2.0F * var3 * 0.5F / var9;
      this.leftArm.xRot = Mth.cos(var2 * 0.6662F) * 2.0F * var3 * 0.5F / var9;
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3 / var9;
      this.leftLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3 / var9;
      this.rightLeg.yRot = 0.005F;
      this.leftLeg.yRot = -0.005F;
      this.rightLeg.zRot = 0.005F;
      this.leftLeg.zRot = -0.005F;
      if (this.riding) {
         this.rightArm.xRot += -0.62831855F;
         this.leftArm.xRot += -0.62831855F;
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = 0.31415927F;
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = -0.31415927F;
         this.leftLeg.zRot = -0.07853982F;
      }

      this.rightArm.yRot = 0.0F;
      this.leftArm.yRot = 0.0F;
      boolean var10 = var1.getMainArm() == HumanoidArm.RIGHT;
      if (var1.isUsingItem()) {
         boolean var11 = var1.getUsedItemHand() == InteractionHand.MAIN_HAND;
         if (var11 == var10) {
            this.poseRightArm((T)var1);
         } else {
            this.poseLeftArm((T)var1);
         }
      } else {
         boolean var19 = var10 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
         if (var10 != var19) {
            this.poseLeftArm((T)var1);
            this.poseRightArm((T)var1);
         } else {
            this.poseRightArm((T)var1);
            this.poseLeftArm((T)var1);
         }
      }

      this.setupAttackAnimation((T)var1, var4);
      if (this.crouching) {
         this.body.xRot = 0.5F;
         this.rightArm.xRot += 0.4F;
         this.leftArm.xRot += 0.4F;
         this.rightLeg.z = 4.0F;
         this.leftLeg.z = 4.0F;
         this.rightLeg.y = 12.2F;
         this.leftLeg.y = 12.2F;
         this.head.y = 4.2F;
         this.body.y = 3.2F;
         this.leftArm.y = 5.2F;
         this.rightArm.y = 5.2F;
      } else {
         this.body.xRot = 0.0F;
         this.rightLeg.z = 0.0F;
         this.leftLeg.z = 0.0F;
         this.rightLeg.y = 12.0F;
         this.leftLeg.y = 12.0F;
         this.head.y = 0.0F;
         this.body.y = 0.0F;
         this.leftArm.y = 2.0F;
         this.rightArm.y = 2.0F;
      }

      if (this.rightArmPose != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.rightArm, var4, 1.0F);
      }

      if (this.leftArmPose != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.leftArm, var4, -1.0F);
      }

      if (this.swimAmount > 0.0F) {
         float var20 = var2 % 26.0F;
         HumanoidArm var12 = this.getAttackArm((T)var1);
         float var13 = var12 == HumanoidArm.RIGHT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
         float var14 = var12 == HumanoidArm.LEFT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
         if (!var1.isUsingItem()) {
            if (var20 < 14.0F) {
               this.leftArm.xRot = this.rotlerpRad(var14, this.leftArm.xRot, 0.0F);
               this.rightArm.xRot = Mth.lerp(var13, this.rightArm.xRot, 0.0F);
               this.leftArm.yRot = this.rotlerpRad(var14, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(var13, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = this.rotlerpRad(
                  var14, this.leftArm.zRot, 3.1415927F + 1.8707964F * this.quadraticArmUpdate(var20) / this.quadraticArmUpdate(14.0F)
               );
               this.rightArm.zRot = Mth.lerp(
                  var13, this.rightArm.zRot, 3.1415927F - 1.8707964F * this.quadraticArmUpdate(var20) / this.quadraticArmUpdate(14.0F)
               );
            } else if (var20 >= 14.0F && var20 < 22.0F) {
               float var21 = (var20 - 14.0F) / 8.0F;
               this.leftArm.xRot = this.rotlerpRad(var14, this.leftArm.xRot, 1.5707964F * var21);
               this.rightArm.xRot = Mth.lerp(var13, this.rightArm.xRot, 1.5707964F * var21);
               this.leftArm.yRot = this.rotlerpRad(var14, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(var13, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = this.rotlerpRad(var14, this.leftArm.zRot, 5.012389F - 1.8707964F * var21);
               this.rightArm.zRot = Mth.lerp(var13, this.rightArm.zRot, 1.2707963F + 1.8707964F * var21);
            } else if (var20 >= 22.0F && var20 < 26.0F) {
               float var15 = (var20 - 22.0F) / 4.0F;
               this.leftArm.xRot = this.rotlerpRad(var14, this.leftArm.xRot, 1.5707964F - 1.5707964F * var15);
               this.rightArm.xRot = Mth.lerp(var13, this.rightArm.xRot, 1.5707964F - 1.5707964F * var15);
               this.leftArm.yRot = this.rotlerpRad(var14, this.leftArm.yRot, 3.1415927F);
               this.rightArm.yRot = Mth.lerp(var13, this.rightArm.yRot, 3.1415927F);
               this.leftArm.zRot = this.rotlerpRad(var14, this.leftArm.zRot, 3.1415927F);
               this.rightArm.zRot = Mth.lerp(var13, this.rightArm.zRot, 3.1415927F);
            }
         }

         float var22 = 0.3F;
         float var16 = 0.33333334F;
         this.leftLeg.xRot = Mth.lerp(this.swimAmount, this.leftLeg.xRot, 0.3F * Mth.cos(var2 * 0.33333334F + 3.1415927F));
         this.rightLeg.xRot = Mth.lerp(this.swimAmount, this.rightLeg.xRot, 0.3F * Mth.cos(var2 * 0.33333334F));
      }

      this.hat.copyFrom(this.head);
   }

   private void poseRightArm(T var1) {
      switch (this.rightArmPose) {
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
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, var1, true);
            break;
         case CROSSBOW_HOLD:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
            break;
         case SPYGLASS:
            this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (var1.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
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

   private void poseLeftArm(T var1) {
      switch (this.leftArmPose) {
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
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, var1, false);
            break;
         case CROSSBOW_HOLD:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
            break;
         case SPYGLASS:
            this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (var1.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
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
      if (!(this.attackTime <= 0.0F)) {
         HumanoidArm var3 = this.getAttackArm((T)var1);
         ModelPart var4 = this.getArm(var3);
         float var5 = this.attackTime;
         this.body.yRot = Mth.sin(Mth.sqrt(var5) * 6.2831855F) * 0.2F;
         if (var3 == HumanoidArm.LEFT) {
            this.body.yRot *= -1.0F;
         }

         this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
         this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
         this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
         this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
         this.rightArm.yRot = this.rightArm.yRot + this.body.yRot;
         this.leftArm.yRot = this.leftArm.yRot + this.body.yRot;
         this.leftArm.xRot = this.leftArm.xRot + this.body.yRot;
         var5 = 1.0F - this.attackTime;
         var5 *= var5;
         var5 *= var5;
         var5 = 1.0F - var5;
         float var6 = Mth.sin(var5 * 3.1415927F);
         float var7 = Mth.sin(this.attackTime * 3.1415927F) * -(this.head.xRot - 0.7F) * 0.75F;
         var4.xRot -= var6 * 1.2F + var7;
         var4.yRot = var4.yRot + this.body.yRot * 2.0F;
         var4.zRot = var4.zRot + Mth.sin(this.attackTime * 3.1415927F) * -0.4F;
      }
   }

   protected float rotlerpRad(float var1, float var2, float var3) {
      float var4 = (var3 - var2) % 6.2831855F;
      if (var4 < -3.1415927F) {
         var4 += 6.2831855F;
      }

      if (var4 >= 3.1415927F) {
         var4 -= 6.2831855F;
      }

      return var2 + var1 * var4;
   }

   private float quadraticArmUpdate(float var1) {
      return -65.0F * var1 + var1 * var1;
   }

   public void copyPropertiesTo(HumanoidModel<T> var1) {
      super.copyPropertiesTo(var1);
      var1.leftArmPose = this.leftArmPose;
      var1.rightArmPose = this.rightArmPose;
      var1.crouching = this.crouching;
      var1.head.copyFrom(this.head);
      var1.hat.copyFrom(this.hat);
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
      this.getArm(var1).translateAndRotate(var2);
   }

   protected ModelPart getArm(HumanoidArm var1) {
      return var1 == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
   }

   @Override
   public ModelPart getHead() {
      return this.head;
   }

   private HumanoidArm getAttackArm(T var1) {
      HumanoidArm var2 = var1.getMainArm();
      return var1.swingingArm == InteractionHand.MAIN_HAND ? var2 : var2.getOpposite();
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

      private ArmPose(boolean var3) {
         this.twoHanded = var3;
      }

      public boolean isTwoHanded() {
         return this.twoHanded;
      }
   }
}
