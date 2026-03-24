package net.minecraft.client.model.animal.feline;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.util.Mth;

public class AdultFelineModel<T extends FelineRenderState> extends AbstractFelineModel<T> {
   private static final float XO = 0.0F;
   private static final float YO = 16.0F;
   private static final float ZO = -9.0F;
   protected static final float BACK_LEG_Y = 18.0F;
   protected static final float BACK_LEG_Z = 5.0F;
   protected static final float FRONT_LEG_Y = 14.1F;
   private static final float FRONT_LEG_Z = -5.0F;

   public AdultFelineModel(final ModelPart root) {
      super(root);
   }

   public static MeshDefinition createBodyMesh(final CubeDeformation g) {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      CubeDeformation tail_g = new CubeDeformation(-0.02F);
      root.addOrReplaceChild("head", CubeListBuilder.create().addBox("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, g).addBox("nose", -1.5F, -0.001F, -4.0F, 3, 2, 2, g, 0, 24).addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, g, 0, 10).addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, g, 6, 10), PartPose.offset(0.0F, 15.0F, -9.0F));
      root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, g), PartPose.offsetAndRotation(0.0F, 12.0F, -10.0F, 1.5707964F, 0.0F, 0.0F));
      root.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, g), PartPose.offsetAndRotation(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F));
      root.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(4, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, tail_g), PartPose.offset(0.0F, 20.0F, 14.0F));
      CubeListBuilder hindLeg = CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, g);
      root.addOrReplaceChild("left_hind_leg", hindLeg, PartPose.offset(1.1F, 18.0F, 5.0F));
      root.addOrReplaceChild("right_hind_leg", hindLeg, PartPose.offset(-1.1F, 18.0F, 5.0F));
      CubeListBuilder frontLeg = CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, g);
      root.addOrReplaceChild("left_front_leg", frontLeg, PartPose.offset(1.2F, 14.1F, -5.0F));
      root.addOrReplaceChild("right_front_leg", frontLeg, PartPose.offset(-1.2F, 14.1F, -5.0F));
      return mesh;
   }

   public void setupAnim(final T state) {
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
         this.body.xRot = 1.5707964F;
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
      }

      if (state.isSitting) {
         this.body.xRot = 0.7853982F;
         ModelPart var10 = this.body;
         var10.y += -4.0F * ageScale;
         var10 = this.body;
         var10.z += 5.0F * ageScale;
         var10 = this.head;
         var10.y += -3.3F * ageScale;
         var10 = this.head;
         var10.z += 1.0F * ageScale;
         var10 = this.tail1;
         var10.y += 8.0F * ageScale;
         var10 = this.tail1;
         var10.z += -2.0F * ageScale;
         var10 = this.tail2;
         var10.y += 2.0F * ageScale;
         var10 = this.tail2;
         var10.z += -0.8F * ageScale;
         this.tail1.xRot = 1.7278761F;
         this.tail2.xRot = 2.670354F;
         this.leftFrontLeg.xRot = -0.15707964F;
         var10 = this.leftFrontLeg;
         var10.y += 2.0F * ageScale;
         var10 = this.leftFrontLeg;
         var10.z -= 2.0F * ageScale;
         this.rightFrontLeg.xRot = -0.15707964F;
         var10 = this.rightFrontLeg;
         var10.y += 2.0F * ageScale;
         var10 = this.rightFrontLeg;
         var10.z -= 2.0F * ageScale;
         this.leftHindLeg.xRot = -1.5707964F;
         var10 = this.leftHindLeg;
         var10.y += 3.0F * ageScale;
         var10 = this.leftHindLeg;
         var10.z -= 4.0F * ageScale;
         this.rightHindLeg.xRot = -1.5707964F;
         var10 = this.rightHindLeg;
         var10.y += 3.0F * ageScale;
         var10 = this.rightHindLeg;
         var10.z -= 4.0F * ageScale;
      }

      if (state.lieDownAmount > 0.0F) {
         this.head.zRot = Mth.rotLerp(state.lieDownAmount, this.head.zRot, -1.2707963F);
         this.head.yRot = Mth.rotLerp(state.lieDownAmount, this.head.yRot, 1.2707963F);
         this.leftFrontLeg.xRot = -1.2707963F;
         this.rightFrontLeg.xRot = -0.47079635F;
         this.rightFrontLeg.zRot = -0.2F;
         ModelPart var26 = this.rightFrontLeg;
         var26.x += ageScale;
         this.leftHindLeg.xRot = -0.4F;
         this.rightHindLeg.xRot = 0.5F;
         this.rightHindLeg.zRot = -0.5F;
         var26 = this.rightHindLeg;
         var26.x += 0.8F * ageScale;
         var26 = this.rightHindLeg;
         var26.y += 2.0F * ageScale;
         this.tail1.xRot = Mth.rotLerp(state.lieDownAmountTail, this.tail1.xRot, 0.8F);
         this.tail2.xRot = Mth.rotLerp(state.lieDownAmountTail, this.tail2.xRot, -0.4F);
      }

      if (state.relaxStateOneAmount > 0.0F) {
         this.head.xRot = Mth.rotLerp(state.relaxStateOneAmount, this.head.xRot, -0.58177644F);
      }

   }
}
