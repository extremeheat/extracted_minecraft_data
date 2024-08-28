package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PolarBearRenderState;

public class PolarBearModel extends QuadrupedModel<PolarBearRenderState> {
   private static final float BABY_HEAD_SCALE = 2.25F;
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.0F, 4.0F, 2.25F, 2.0F, 24.0F, Set.of("head"));

   public PolarBearModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F)
            .texOffs(0, 44)
            .addBox("mouth", -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F)
            .texOffs(26, 0)
            .addBox("right_ear", -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F)
            .texOffs(26, 0)
            .mirror()
            .addBox("left_ear", 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F),
         PartPose.offset(0.0F, 10.0F, -16.0F)
      );
      var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 19)
            .addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F)
            .texOffs(39, 0)
            .addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F),
         PartPose.offsetAndRotation(-2.0F, 9.0F, 12.0F, 1.5707964F, 0.0F, 0.0F)
      );
      byte var2 = 10;
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(50, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
      var1.addOrReplaceChild("right_hind_leg", var3, PartPose.offset(-4.5F, 14.0F, 6.0F));
      var1.addOrReplaceChild("left_hind_leg", var3, PartPose.offset(4.5F, 14.0F, 6.0F));
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(50, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
      var1.addOrReplaceChild("right_front_leg", var4, PartPose.offset(-3.5F, 14.0F, -8.0F));
      var1.addOrReplaceChild("left_front_leg", var4, PartPose.offset(3.5F, 14.0F, -8.0F));
      return LayerDefinition.create(var0, 128, 64).apply(MeshTransformer.scaling(1.2F));
   }

   public void setupAnim(PolarBearRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.standScale * var1.standScale;
      float var3 = var1.ageScale;
      float var4 = var1.isBaby ? 0.44444445F : 1.0F;
      this.body.xRot -= var2 * 3.1415927F * 0.35F;
      this.body.y += var2 * var3 * 2.0F;
      this.rightFrontLeg.y -= var2 * var3 * 20.0F;
      this.rightFrontLeg.z += var2 * var3 * 4.0F;
      this.rightFrontLeg.xRot -= var2 * 3.1415927F * 0.45F;
      this.leftFrontLeg.y = this.rightFrontLeg.y;
      this.leftFrontLeg.z = this.rightFrontLeg.z;
      this.leftFrontLeg.xRot -= var2 * 3.1415927F * 0.45F;
      this.head.y -= var2 * var4 * 24.0F;
      this.head.z += var2 * var4 * 13.0F;
      this.head.xRot += var2 * 3.1415927F * 0.15F;
   }
}
