package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.util.Mth;

public class VillagerModel extends EntityModel<VillagerRenderState> implements HeadedModel, VillagerLikeModel {
   public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5F);
   private final ModelPart head;
   private final ModelPart hat;
   private final ModelPart hatRim;
   private final ModelPart rightLeg;
   private final ModelPart leftLeg;
   private final ModelPart arms;

   public VillagerModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.hat = this.head.getChild("hat");
      this.hatRim = this.hat.getChild("hat_rim");
      this.rightLeg = var1.getChild("right_leg");
      this.leftLeg = var1.getChild("left_leg");
      this.arms = var1.getChild("arms");
   }

   public static MeshDefinition createBodyModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = 0.5F;
      PartDefinition var3 = var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.ZERO);
      PartDefinition var4 = var3.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F)), PartPose.ZERO);
      var4.addOrReplaceChild("hat_rim", CubeListBuilder.create().texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F), PartPose.rotation(-1.5707964F, 0.0F, 0.0F));
      var3.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
      PartDefinition var5 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), PartPose.ZERO);
      var5.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
      var1.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, true).texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
      var1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
      return var0;
   }

   public void setupAnim(VillagerRenderState var1) {
      super.setupAnim(var1);
      this.head.yRot = var1.yRot * 0.017453292F;
      this.head.xRot = var1.xRot * 0.017453292F;
      if (var1.isUnhappy) {
         this.head.zRot = 0.3F * Mth.sin(0.45F * var1.ageInTicks);
         this.head.xRot = 0.4F;
      } else {
         this.head.zRot = 0.0F;
      }

      this.rightLeg.xRot = Mth.cos(var1.walkAnimationPos * 0.6662F) * 1.4F * var1.walkAnimationSpeed * 0.5F;
      this.leftLeg.xRot = Mth.cos(var1.walkAnimationPos * 0.6662F + 3.1415927F) * 1.4F * var1.walkAnimationSpeed * 0.5F;
      this.rightLeg.yRot = 0.0F;
      this.leftLeg.yRot = 0.0F;
   }

   public ModelPart getHead() {
      return this.head;
   }

   public void hatVisible(boolean var1) {
      this.head.visible = var1;
      this.hat.visible = var1;
      this.hatRim.visible = var1;
   }

   public void translateToArms(PoseStack var1) {
      this.root.translateAndRotate(var1);
      this.arms.translateAndRotate(var1);
   }
}
