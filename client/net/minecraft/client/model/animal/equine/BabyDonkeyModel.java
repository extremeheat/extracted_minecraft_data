package net.minecraft.client.model.animal.equine;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.util.Mth;

public class BabyDonkeyModel extends DonkeyModel {
   public BabyDonkeyModel(final ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBabyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(12, 33).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F), PartPose.offset(2.4F, 18.5F, -5.4F));
      partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 33).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F), PartPose.offset(-2.4F, 18.5F, -5.4F));
      PartDefinition headParts = partdefinition.addOrReplaceChild("head_parts", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 12.0F, -6.9F, 0.17453292F, 0.0F, 0.0F));
      headParts.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(30, 9).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3926991F, 0.0F, 0.0F));
      headParts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.6F, -0.6F, 6.0F, 4.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, -7.4F, 0.34906584F, 0.0F, 0.0F));
      headParts.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 0).addBox(-1.45F, -6.5F, 0.0F, 2.0F, 7.0F, 1.0F), PartPose.offsetAndRotation(2.0F, -8.5F, -2.0F, 0.47996554F, 0.0F, 0.47996554F));
      headParts.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(22, 0).mirror().addBox(-0.55F, -6.5F, 0.0F, 2.0F, 7.0F, 1.0F), PartPose.offsetAndRotation(-2.0F, -8.5F, -2.0F, 0.47996554F, 0.0F, -0.47996554F));
      partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(12, 33).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F), PartPose.offset(2.4F, 18.5F, 5.4F));
      PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -3.0F, -7.0F, 8.0F, 6.0F, 14.0F), PartPose.offset(0.0F, 14.0F, 0.0F));
      PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-1.0F, 5.75F, 6.5F));
      tail.addOrReplaceChild("tail_cube", CubeListBuilder.create().texOffs(24, 33).addBox(-1.5F, -1.0F, -7.5F, 3.0F, 3.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -6.0F, 1.0F, 0.34906584F, 0.0F, 0.0F));
      body.addOrReplaceChild("right_chest", CubeListBuilder.create(), PartPose.offset(-1.0F, 10.0F, 0.0F));
      body.addOrReplaceChild("left_chest", CubeListBuilder.create(), PartPose.offset(-1.0F, 10.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 33).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F), PartPose.offset(-2.4F, 18.5F, 5.4F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(final DonkeyRenderState state) {
      super.setupAnim(state);
      state.xRot = -30.0F;
      float headRotXRad = state.xRot * 0.017453292F;
      float eating = state.eatAnimation;
      float standing = state.standAnimation;
      float feedingAnim = state.feedingAnimation;
      float baseHeadAngle = (1.0F - Math.max(standing, eating)) * (0.5235988F + headRotXRad + feedingAnim * Mth.sin((double)state.ageInTicks) * 0.05F);
      this.headParts.xRot = standing * (0.2617994F + headRotXRad) + eating * (1.5707964F + Mth.sin((double)state.ageInTicks) * 0.05F) + baseHeadAngle;
   }
}
