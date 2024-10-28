package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.util.Mth;

public class PandaModel extends QuadrupedModel<PandaRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 23.0F, 4.8F, 2.7F, 3.0F, 49.0F, Set.of("head"));

   public PandaModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 6).addBox(-6.5F, -5.0F, -4.0F, 13.0F, 10.0F, 9.0F).texOffs(45, 16).addBox("nose", -3.5F, 0.0F, -6.0F, 7.0F, 5.0F, 2.0F).texOffs(52, 25).addBox("left_ear", 3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F).texOffs(52, 25).addBox("right_ear", -8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 11.5F, -17.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 1.5707964F, 0.0F, 0.0F));
      boolean var2 = true;
      boolean var3 = true;
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(40, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      var1.addOrReplaceChild("right_hind_leg", var4, PartPose.offset(-5.5F, 15.0F, 9.0F));
      var1.addOrReplaceChild("left_hind_leg", var4, PartPose.offset(5.5F, 15.0F, 9.0F));
      var1.addOrReplaceChild("right_front_leg", var4, PartPose.offset(-5.5F, 15.0F, -9.0F));
      var1.addOrReplaceChild("left_front_leg", var4, PartPose.offset(5.5F, 15.0F, -9.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(PandaRenderState var1) {
      super.setupAnim((LivingEntityRenderState)var1);
      if (var1.isUnhappy) {
         this.head.yRot = 0.35F * Mth.sin(0.6F * var1.ageInTicks);
         this.head.zRot = 0.35F * Mth.sin(0.6F * var1.ageInTicks);
         this.rightFrontLeg.xRot = -0.75F * Mth.sin(0.3F * var1.ageInTicks);
         this.leftFrontLeg.xRot = 0.75F * Mth.sin(0.3F * var1.ageInTicks);
      } else {
         this.head.zRot = 0.0F;
      }

      if (var1.isSneezing) {
         if (var1.sneezeTime < 15) {
            this.head.xRot = -0.7853982F * (float)var1.sneezeTime / 14.0F;
         } else if (var1.sneezeTime < 20) {
            float var2 = (float)((var1.sneezeTime - 15) / 5);
            this.head.xRot = -0.7853982F + 0.7853982F * var2;
         }
      }

      if (var1.sitAmount > 0.0F) {
         this.body.xRot = Mth.rotLerpRad(var1.sitAmount, this.body.xRot, 1.7407963F);
         this.head.xRot = Mth.rotLerpRad(var1.sitAmount, this.head.xRot, 1.5707964F);
         this.rightFrontLeg.zRot = -0.27079642F;
         this.leftFrontLeg.zRot = 0.27079642F;
         this.rightHindLeg.zRot = 0.5707964F;
         this.leftHindLeg.zRot = -0.5707964F;
         if (var1.isEating) {
            this.head.xRot = 1.5707964F + 0.2F * Mth.sin(var1.ageInTicks * 0.6F);
            this.rightFrontLeg.xRot = -0.4F - 0.2F * Mth.sin(var1.ageInTicks * 0.6F);
            this.leftFrontLeg.xRot = -0.4F - 0.2F * Mth.sin(var1.ageInTicks * 0.6F);
         }

         if (var1.isScared) {
            this.head.xRot = 2.1707964F;
            this.rightFrontLeg.xRot = -0.9F;
            this.leftFrontLeg.xRot = -0.9F;
         }
      } else {
         this.rightHindLeg.zRot = 0.0F;
         this.leftHindLeg.zRot = 0.0F;
         this.rightFrontLeg.zRot = 0.0F;
         this.leftFrontLeg.zRot = 0.0F;
      }

      if (var1.lieOnBackAmount > 0.0F) {
         this.rightHindLeg.xRot = -0.6F * Mth.sin(var1.ageInTicks * 0.15F);
         this.leftHindLeg.xRot = 0.6F * Mth.sin(var1.ageInTicks * 0.15F);
         this.rightFrontLeg.xRot = 0.3F * Mth.sin(var1.ageInTicks * 0.25F);
         this.leftFrontLeg.xRot = -0.3F * Mth.sin(var1.ageInTicks * 0.25F);
         this.head.xRot = Mth.rotLerpRad(var1.lieOnBackAmount, this.head.xRot, 1.5707964F);
      }

      if (var1.rollAmount > 0.0F) {
         this.head.xRot = Mth.rotLerpRad(var1.rollAmount, this.head.xRot, 2.0561945F);
         this.rightHindLeg.xRot = -0.5F * Mth.sin(var1.ageInTicks * 0.5F);
         this.leftHindLeg.xRot = 0.5F * Mth.sin(var1.ageInTicks * 0.5F);
         this.rightFrontLeg.xRot = 0.5F * Mth.sin(var1.ageInTicks * 0.5F);
         this.leftFrontLeg.xRot = -0.5F * Mth.sin(var1.ageInTicks * 0.5F);
      }

   }
}
