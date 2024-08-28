package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import net.minecraft.util.Mth;

public class WitchModel extends EntityModel<WitchRenderState> implements HeadedModel, VillagerHeadModel {
   protected final ModelPart nose;
   private final ModelPart head;
   private final ModelPart hat;
   private final ModelPart hatRim;
   private final ModelPart rightLeg;
   private final ModelPart leftLeg;

   public WitchModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.hat = this.head.getChild("hat");
      this.hatRim = this.hat.getChild("hat_rim");
      this.nose = this.head.getChild("nose");
      this.rightLeg = var1.getChild("right_leg");
      this.leftLeg = var1.getChild("left_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = VillagerModel.createBodyModel();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.ZERO
      );
      PartDefinition var3 = var2.addOrReplaceChild(
         "hat", CubeListBuilder.create().texOffs(0, 64).addBox(0.0F, 0.0F, 0.0F, 10.0F, 2.0F, 10.0F), PartPose.offset(-5.0F, -10.03125F, -5.0F)
      );
      PartDefinition var4 = var3.addOrReplaceChild(
         "hat2",
         CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 7.0F, 4.0F, 7.0F),
         PartPose.offsetAndRotation(1.75F, -4.0F, 2.0F, -0.05235988F, 0.0F, 0.02617994F)
      );
      PartDefinition var5 = var4.addOrReplaceChild(
         "hat3",
         CubeListBuilder.create().texOffs(0, 87).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F),
         PartPose.offsetAndRotation(1.75F, -4.0F, 2.0F, -0.10471976F, 0.0F, 0.05235988F)
      );
      var5.addOrReplaceChild(
         "hat4",
         CubeListBuilder.create().texOffs(0, 95).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.25F)),
         PartPose.offsetAndRotation(1.75F, -2.0F, 2.0F, -0.20943952F, 0.0F, 0.10471976F)
      );
      PartDefinition var6 = var2.getChild("nose");
      var6.addOrReplaceChild(
         "mole",
         CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)),
         PartPose.offset(0.0F, -2.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 64, 128);
   }

   public void setupAnim(WitchRenderState var1) {
      super.setupAnim(var1);
      this.head.yRot = var1.yRot * 0.017453292F;
      this.head.xRot = var1.xRot * 0.017453292F;
      this.rightLeg.xRot = Mth.cos(var1.walkAnimationPos * 0.6662F) * 1.4F * var1.walkAnimationSpeed * 0.5F;
      this.leftLeg.xRot = Mth.cos(var1.walkAnimationPos * 0.6662F + 3.1415927F) * 1.4F * var1.walkAnimationSpeed * 0.5F;
      float var2 = 0.01F * (float)(var1.entityId % 10);
      this.nose.xRot = Mth.sin(var1.ageInTicks * var2) * 4.5F * 0.017453292F;
      this.nose.zRot = Mth.cos(var1.ageInTicks * var2) * 2.5F * 0.017453292F;
      if (var1.isHoldingItem) {
         this.nose.setPos(0.0F, 1.0F, -1.5F);
         this.nose.xRot = -0.9F;
      }
   }

   public ModelPart getNose() {
      return this.nose;
   }

   @Override
   public ModelPart getHead() {
      return this.head;
   }

   @Override
   public void hatVisible(boolean var1) {
      this.head.visible = var1;
      this.hat.visible = var1;
      this.hatRim.visible = var1;
   }
}
