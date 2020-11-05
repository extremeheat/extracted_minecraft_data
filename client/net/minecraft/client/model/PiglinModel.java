package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public class PiglinModel<T extends Mob> extends PlayerModel<T> {
   public final ModelPart rightEar;
   private final ModelPart leftEar;
   private final PartPose bodyDefault;
   private final PartPose headDefault;
   private final PartPose leftArmDefault;
   private final PartPose rightArmDefault;

   public PiglinModel(ModelPart var1) {
      super(var1, false);
      this.rightEar = this.head.getChild("right_ear");
      this.leftEar = this.head.getChild("left_ear");
      this.bodyDefault = this.body.storePose();
      this.headDefault = this.head.storePose();
      this.leftArmDefault = this.leftArm.storePose();
      this.rightArmDefault = this.rightArm.storePose();
   }

   public static MeshDefinition createMesh(CubeDeformation var0) {
      MeshDefinition var1 = PlayerModel.createMesh(var0, false);
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var0), PartPose.ZERO);
      PartDefinition var3 = var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, var0).texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, var0).texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, var0).texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, var0), PartPose.ZERO);
      var3.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, var0), PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5235988F));
      var3.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, var0), PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5235988F));
      var2.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      return var1;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.body.loadPose(this.bodyDefault);
      this.head.loadPose(this.headDefault);
      this.leftArm.loadPose(this.leftArmDefault);
      this.rightArm.loadPose(this.rightArmDefault);
      super.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6);
      float var7 = 0.5235988F;
      float var8 = var4 * 0.1F + var2 * 0.5F;
      float var9 = 0.08F + var3 * 0.4F;
      this.leftEar.zRot = -0.5235988F - Mth.cos(var8 * 1.2F) * var9;
      this.rightEar.zRot = 0.5235988F + Mth.cos(var8) * var9;
      if (var1 instanceof AbstractPiglin) {
         AbstractPiglin var10 = (AbstractPiglin)var1;
         PiglinArmPose var11 = var10.getArmPose();
         if (var11 == PiglinArmPose.DANCING) {
            float var12 = var4 / 60.0F;
            this.rightEar.zRot = 0.5235988F + 0.017453292F * Mth.sin(var12 * 30.0F) * 10.0F;
            this.leftEar.zRot = -0.5235988F - 0.017453292F * Mth.cos(var12 * 30.0F) * 10.0F;
            this.head.x = Mth.sin(var12 * 10.0F);
            this.head.y = Mth.sin(var12 * 40.0F) + 0.4F;
            this.rightArm.zRot = 0.017453292F * (70.0F + Mth.cos(var12 * 40.0F) * 10.0F);
            this.leftArm.zRot = this.rightArm.zRot * -1.0F;
            this.rightArm.y = Mth.sin(var12 * 40.0F) * 0.5F + 1.5F;
            this.leftArm.y = Mth.sin(var12 * 40.0F) * 0.5F + 1.5F;
            this.body.y = Mth.sin(var12 * 40.0F) * 0.35F;
         } else if (var11 == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && this.attackTime == 0.0F) {
            this.holdWeaponHigh(var1);
         } else if (var11 == PiglinArmPose.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, !var1.isLeftHanded());
         } else if (var11 == PiglinArmPose.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, var1, !var1.isLeftHanded());
         } else if (var11 == PiglinArmPose.ADMIRING_ITEM) {
            this.head.xRot = 0.5F;
            this.head.yRot = 0.0F;
            if (var1.isLeftHanded()) {
               this.rightArm.yRot = -0.5F;
               this.rightArm.xRot = -0.9F;
            } else {
               this.leftArm.yRot = 0.5F;
               this.leftArm.xRot = -0.9F;
            }
         }
      } else if (var1.getType() == EntityType.ZOMBIFIED_PIGLIN) {
         AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, var1.isAggressive(), this.attackTime, var4);
      }

      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      this.hat.copyFrom(this.head);
   }

   protected void setupAttackAnimation(T var1, float var2) {
      if (this.attackTime > 0.0F && var1 instanceof Piglin && ((Piglin)var1).getArmPose() == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
         AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, var1, this.attackTime, var2);
      } else {
         super.setupAttackAnimation(var1, var2);
      }
   }

   private void holdWeaponHigh(T var1) {
      if (var1.isLeftHanded()) {
         this.leftArm.xRot = -1.8F;
      } else {
         this.rightArm.xRot = -1.8F;
      }

   }
}
