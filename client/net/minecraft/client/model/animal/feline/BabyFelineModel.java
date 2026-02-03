package net.minecraft.client.model.animal.feline;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.util.Mth;

public class BabyFelineModel<S extends FelineRenderState> extends AbstractFelineModel<S> {
   protected BabyFelineModel(final ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBabyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -2.875F, 5.0F, 4.0F, 4.0F).texOffs(18, 0).addBox(-2.0F, -4.0F, -0.875F, 1.0F, 1.0F, 2.0F).texOffs(24, 0).addBox(1.0F, -4.0F, -0.875F, 1.0F, 1.0F, 2.0F).texOffs(18, 3).addBox(-1.5F, -1.0F, -3.875F, 3.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 20.0F, -3.125F));
      partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(18, 18).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(1.0F, 22.0F, -1.5F));
      partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(12, 18).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(-1.0F, 22.0F, -1.5F));
      partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(18, 22).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(1.0F, 22.0F, 2.5F));
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -1.5F, -3.5F, 4.0F, 3.0F, 7.0F), PartPose.offset(0.0F, 20.5F, 0.5F));
      partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(12, 22).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(-1.0F, 22.0F, 2.5F));
      partdefinition.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 18).addBox(-0.5F, -0.107F, 0.0849F, 1.0F, 1.0F, 5.0F), PartPose.offsetAndRotation(0.0F, 19.107F, 3.9151F, -0.567232F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void setupAnim(final S state) {
      super.setupAnim(state);
      float ageScale = state.ageScale;
      if (state.isCrouching) {
         ModelPart var10000 = this.body;
         var10000.y += 1.0F * ageScale;
         var10000 = this.head;
         var10000.y += 2.0F * ageScale;
         var10000 = this.tail1;
         var10000.y += 1.0F * ageScale;
         var10000 = this.tail2;
         var10000.y += -4.0F * ageScale;
         var10000 = this.tail2;
         var10000.z += 2.0F * ageScale;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
      } else if (state.isSprinting) {
         this.tail2.y = this.tail1.y;
         ModelPart var9 = this.tail2;
         var9.z += 2.0F * ageScale;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
      }

      this.head.xRot = state.xRot * 0.017453292F;
      this.head.yRot = state.yRot * 0.017453292F;
      if (!state.isSitting) {
         float animationSpeed = state.walkAnimationSpeed;
         float animationPos = state.walkAnimationPos;
         if (state.isSprinting) {
            this.leftHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * animationSpeed;
            this.rightHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 0.3F)) * animationSpeed;
            this.leftFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F + 0.3F)) * animationSpeed;
            this.rightFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * animationSpeed;
            this.tail2.xRot = 1.7278761F + 0.31415927F * Mth.cos((double)animationPos) * animationSpeed;
         } else {
            this.leftHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * animationSpeed;
            this.rightHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * animationSpeed;
            this.leftFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * animationSpeed;
            this.rightFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * animationSpeed;
            if (!state.isCrouching) {
               this.tail2.xRot = 1.7278761F + 0.7853982F * Mth.cos((double)animationPos) * animationSpeed;
            } else {
               this.tail2.xRot = 1.7278761F + 0.47123894F * Mth.cos((double)animationPos) * animationSpeed;
            }
         }
      } else if (state.isSitting) {
         ModelPart var10 = this.body;
         var10.xRot += -0.43633232F;
         ++this.body.y;
         var10 = this.head;
         var10.z += 0.75F;
         var10 = this.tail1;
         var10.xRot += 0.5454154F;
         var10 = this.tail1;
         var10.y += 4.0F;
         var10 = this.tail1;
         var10.z -= 0.9F;
         var10 = this.leftHindLeg;
         var10.z -= 0.9F;
         var10 = this.rightHindLeg;
         var10.z -= 0.9F;
      }

      if (state.lieDownAmount > 0.0F) {
         ++this.body.x;
         this.head.xRot = Mth.rotLerp(state.lieDownAmount, this.head.xRot, 0.17453292F);
         this.head.zRot = Mth.rotLerp(state.lieDownAmount, this.head.zRot, -1.3089969F);
         ++this.head.x;
         ModelPart var17 = this.head;
         var17.y += 0.75F;
         var17 = this.head;
         var17.z -= 0.5F;
         this.rightFrontLeg.xRot = -0.7853982F;
         var17 = this.rightFrontLeg;
         var17.x += 3.5F;
         var17 = this.rightFrontLeg;
         var17.y -= 0.5F;
         var17 = this.rightFrontLeg;
         var17.z += 0.0F;
         this.leftFrontLeg.xRot = -1.5707964F;
         ++this.leftFrontLeg.x;
         --this.leftFrontLeg.y;
         var17 = this.leftFrontLeg;
         var17.z -= 2.0F;
         this.rightHindLeg.xRot = 0.6981317F;
         this.rightHindLeg.yRot = 0.34906584F;
         this.rightHindLeg.zRot = -0.34906584F;
         var17 = this.rightHindLeg;
         var17.x += 2.5F;
         var17 = this.rightHindLeg;
         var17.y -= 0.25F;
         var17 = this.rightHindLeg;
         var17.z += 0.5F;
         ++this.leftHindLeg.x;
         --this.leftHindLeg.z;
         var17 = this.tail1;
         var17.xRot += Mth.rotLerp(state.lieDownAmountTail, this.tail1.xRot, -0.5235988F);
         var17 = this.tail1;
         var17.yRot += Mth.rotLerp(state.lieDownAmountTail, this.tail1.yRot, 0.0F);
         var17 = this.tail1;
         var17.zRot += Mth.rotLerp(state.lieDownAmountTail, this.tail1.zRot, -0.17453292F);
         ++this.tail1.x;
         var17 = this.tail1;
         var17.y += 0.5F;
         var17 = this.tail1;
         var17.z -= 0.25F;
      }

      if (state.relaxStateOneAmount > 0.0F) {
         this.head.xRot = Mth.rotLerp(state.relaxStateOneAmount, this.head.xRot, -0.58177644F);
      }

   }
}
