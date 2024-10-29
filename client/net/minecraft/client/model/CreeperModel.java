package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.util.Mth;

public class CreeperModel extends EntityModel<CreeperRenderState> {
   private final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private static final int Y_OFFSET = 6;

   public CreeperModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.leftHindLeg = var1.getChild("right_hind_leg");
      this.rightHindLeg = var1.getChild("left_hind_leg");
      this.leftFrontLeg = var1.getChild("right_front_leg");
      this.rightFrontLeg = var1.getChild("left_front_leg");
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0), PartPose.offset(0.0F, 6.0F, 0.0F));
      var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var0), PartPose.offset(0.0F, 6.0F, 0.0F));
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, var0);
      var2.addOrReplaceChild("right_hind_leg", var3, PartPose.offset(-2.0F, 18.0F, 4.0F));
      var2.addOrReplaceChild("left_hind_leg", var3, PartPose.offset(2.0F, 18.0F, 4.0F));
      var2.addOrReplaceChild("right_front_leg", var3, PartPose.offset(-2.0F, 18.0F, -4.0F));
      var2.addOrReplaceChild("left_front_leg", var3, PartPose.offset(2.0F, 18.0F, -4.0F));
      return LayerDefinition.create(var1, 64, 32);
   }

   public void setupAnim(CreeperRenderState var1) {
      super.setupAnim(var1);
      this.head.yRot = var1.yRot * 0.017453292F;
      this.head.xRot = var1.xRot * 0.017453292F;
      float var2 = var1.walkAnimationSpeed;
      float var3 = var1.walkAnimationPos;
      this.rightHindLeg.xRot = Mth.cos(var3 * 0.6662F) * 1.4F * var2;
      this.leftHindLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.rightFrontLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.leftFrontLeg.xRot = Mth.cos(var3 * 0.6662F) * 1.4F * var2;
   }
}
