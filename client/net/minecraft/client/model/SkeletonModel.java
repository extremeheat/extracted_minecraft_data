package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class SkeletonModel<S extends SkeletonRenderState> extends HumanoidModel<S> {
   public SkeletonModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition var1 = var0.getRoot();
      createDefaultSkeletonMesh(var1);
      return LayerDefinition.create(var0, 64, 32);
   }

   protected static void createDefaultSkeletonMesh(PartDefinition var0) {
      var0.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
      var0.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
      var0.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
      var0.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
   }

   public void setupAnim(S var1) {
      super.setupAnim((HumanoidRenderState)var1);
      if (var1.isAggressive && !var1.isHoldingBow) {
         float var2 = var1.attackTime;
         float var3 = Mth.sin(var2 * 3.1415927F);
         float var4 = Mth.sin((1.0F - (1.0F - var2) * (1.0F - var2)) * 3.1415927F);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - var3 * 0.6F);
         this.leftArm.yRot = 0.1F - var3 * 0.6F;
         this.rightArm.xRot = -1.5707964F;
         this.leftArm.xRot = -1.5707964F;
         ModelPart var10000 = this.rightArm;
         var10000.xRot -= var3 * 1.2F - var4 * 0.4F;
         var10000 = this.leftArm;
         var10000.xRot -= var3 * 1.2F - var4 * 0.4F;
         AnimationUtils.bobArms(this.rightArm, this.leftArm, var1.ageInTicks);
      }

   }

   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      this.root().translateAndRotate(var2);
      float var3 = var1 == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      ModelPart var4 = this.getArm(var1);
      var4.x += var3;
      var4.translateAndRotate(var2);
      var4.x -= var3;
   }
}
