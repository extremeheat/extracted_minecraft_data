package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DrownedModel extends ZombieModel<ZombieRenderState> {
   public DrownedModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = HumanoidModel.createMesh(var0, 0.0F);
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(5.0F, 2.0F, 0.0F));
      var2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(1.9F, 12.0F, 0.0F));
      return LayerDefinition.create(var1, 64, 64);
   }

   protected HumanoidModel.ArmPose getArmPose(ZombieRenderState var1, HumanoidArm var2) {
      ItemStack var3 = var2 == HumanoidArm.RIGHT ? var1.rightHandItem : var1.leftHandItem;
      return var3.is(Items.TRIDENT) && var1.isAggressive && var1.mainArm == var2 ? HumanoidModel.ArmPose.THROW_SPEAR : HumanoidModel.ArmPose.EMPTY;
   }

   public void setupAnim(ZombieRenderState var1) {
      super.setupAnim(var1);
      if (this.getArmPose(var1, HumanoidArm.LEFT) == HumanoidModel.ArmPose.THROW_SPEAR) {
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 3.1415927F;
         this.leftArm.yRot = 0.0F;
      }

      if (this.getArmPose(var1, HumanoidArm.RIGHT) == HumanoidModel.ArmPose.THROW_SPEAR) {
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 3.1415927F;
         this.rightArm.yRot = 0.0F;
      }

      float var2 = var1.swimAmount;
      if (var2 > 0.0F) {
         this.rightArm.xRot = Mth.rotLerpRad(var2, this.rightArm.xRot, -2.5132742F) + var2 * 0.35F * Mth.sin(0.1F * var1.ageInTicks);
         this.leftArm.xRot = Mth.rotLerpRad(var2, this.leftArm.xRot, -2.5132742F) - var2 * 0.35F * Mth.sin(0.1F * var1.ageInTicks);
         this.rightArm.zRot = Mth.rotLerpRad(var2, this.rightArm.zRot, -0.15F);
         this.leftArm.zRot = Mth.rotLerpRad(var2, this.leftArm.zRot, 0.15F);
         ModelPart var10000 = this.leftLeg;
         var10000.xRot -= var2 * 0.55F * Mth.sin(0.1F * var1.ageInTicks);
         var10000 = this.rightLeg;
         var10000.xRot += var2 * 0.55F * Mth.sin(0.1F * var1.ageInTicks);
         this.head.xRot = 0.0F;
      }

   }
}
