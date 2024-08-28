package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.util.Mth;

public class RavagerModel extends EntityModel<RavagerRenderState> {
   private final ModelPart head;
   private final ModelPart mouth;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart neck;

   public RavagerModel(ModelPart var1) {
      super(var1);
      this.neck = var1.getChild("neck");
      this.head = this.neck.getChild("head");
      this.mouth = this.head.getChild("mouth");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      byte var2 = 16;
      PartDefinition var3 = var1.addOrReplaceChild(
         "neck", CubeListBuilder.create().texOffs(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), PartPose.offset(0.0F, -7.0F, 5.5F)
      );
      PartDefinition var4 = var3.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F).texOffs(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F),
         PartPose.offset(0.0F, 16.0F, -17.0F)
      );
      var4.addOrReplaceChild(
         "right_horn",
         CubeListBuilder.create().texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
         PartPose.offsetAndRotation(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
      );
      var4.addOrReplaceChild(
         "left_horn",
         CubeListBuilder.create().texOffs(74, 55).mirror().addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F),
         PartPose.offsetAndRotation(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F)
      );
      var4.addOrReplaceChild(
         "mouth", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), PartPose.offset(0.0F, -2.0F, 2.0F)
      );
      var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 55)
            .addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F)
            .texOffs(0, 91)
            .addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F),
         PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 1.5707964F, 0.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "right_hind_leg", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, 18.0F)
      );
      var1.addOrReplaceChild(
         "left_hind_leg", CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, 18.0F)
      );
      var1.addOrReplaceChild(
         "right_front_leg", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, -5.0F)
      );
      var1.addOrReplaceChild(
         "left_front_leg", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, -5.0F)
      );
      return LayerDefinition.create(var0, 128, 128);
   }

   public void setupAnim(RavagerRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.stunnedTicksRemaining;
      float var3 = var1.attackTicksRemaining;
      byte var4 = 10;
      if (var3 > 0.0F) {
         float var5 = Mth.triangleWave(var3, 10.0F);
         float var6 = (1.0F + var5) * 0.5F;
         float var7 = var6 * var6 * var6 * 12.0F;
         float var8 = var7 * Mth.sin(this.neck.xRot);
         this.neck.z = -6.5F + var7;
         this.neck.y = -7.0F - var8;
         if (var3 > 5.0F) {
            this.mouth.xRot = Mth.sin((-4.0F + var3) / 4.0F) * 3.1415927F * 0.4F;
         } else {
            this.mouth.xRot = 0.15707964F * Mth.sin(3.1415927F * var3 / 10.0F);
         }
      } else {
         float var10 = -1.0F;
         float var12 = -1.0F * Mth.sin(this.neck.xRot);
         this.neck.x = 0.0F;
         this.neck.y = -7.0F - var12;
         this.neck.z = 5.5F;
         boolean var14 = var2 > 0.0F;
         this.neck.xRot = var14 ? 0.21991149F : 0.0F;
         this.mouth.xRot = 3.1415927F * (var14 ? 0.05F : 0.01F);
         if (var14) {
            double var15 = (double)var2 / 40.0;
            this.neck.x = (float)Math.sin(var15 * 10.0) * 3.0F;
         } else if ((double)var1.roarAnimation > 0.0) {
            float var16 = Mth.sin(var1.roarAnimation * 3.1415927F * 0.25F);
            this.mouth.xRot = 1.5707964F * var16;
         }
      }

      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      float var11 = var1.walkAnimationPos;
      float var13 = 0.4F * var1.walkAnimationSpeed;
      this.rightHindLeg.xRot = Mth.cos(var11 * 0.6662F) * var13;
      this.leftHindLeg.xRot = Mth.cos(var11 * 0.6662F + 3.1415927F) * var13;
      this.rightFrontLeg.xRot = Mth.cos(var11 * 0.6662F + 3.1415927F) * var13;
      this.leftFrontLeg.xRot = Mth.cos(var11 * 0.6662F) * var13;
   }
}
