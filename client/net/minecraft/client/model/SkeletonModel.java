package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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

   public static LayerDefinition createSingleModelDualBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F).texOffs(28, 0).addBox(-4.0F, 10.0F, -2.0F, 8.0F, 1.0F, 4.0F).texOffs(16, 48).addBox(-4.0F, 0.0F, -2.05F, 8.0F, 12.0F, 4.1F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 0.0F, 0.0F));
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F).texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F)).addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      var1.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F).texOffs(42, 33).addBox(-1.55F, -2.025F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(-5.5F, 2.0F, 0.0F));
      var1.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(56, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F).texOffs(40, 48).addBox(-1.45F, -2.025F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(5.5F, 2.0F, 0.0F));
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F).texOffs(0, 49).addBox(-1.4F, -0.2588F, -1.5341F, 3.0F, 12.0F, 3.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
      var1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F).texOffs(5, 49).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(S var1) {
      super.setupAnim(var1);
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

   public void translateToHand(SkeletonRenderState var1, HumanoidArm var2, PoseStack var3) {
      this.root().translateAndRotate(var3);
      float var4 = var2 == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      ModelPart var5 = this.getArm(var2);
      var5.x += var4;
      var5.translateAndRotate(var3);
      var5.x -= var4;
   }
}
