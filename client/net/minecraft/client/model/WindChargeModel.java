package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class WindChargeModel extends EntityModel<EntityRenderState> {
   private static final int ROTATION_SPEED = 16;
   private final ModelPart bone;
   private final ModelPart windCharge;
   private final ModelPart wind;

   public WindChargeModel(ModelPart var1) {
      super(var1, RenderType::entityTranslucent);
      this.bone = var1.getChild("bone");
      this.wind = this.bone.getChild("wind");
      this.windCharge = this.bone.getChild("wind_charge");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      var2.addOrReplaceChild(
         "wind",
         CubeListBuilder.create()
            .texOffs(15, 20)
            .addBox(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 9)
            .addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
      );
      var2.addOrReplaceChild(
         "wind_charge",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 64, 32);
   }

   @Override
   public void setupAnim(EntityRenderState var1) {
      super.setupAnim(var1);
      this.windCharge.yRot = -var1.ageInTicks * 16.0F * 0.017453292F;
      this.wind.yRot = var1.ageInTicks * 16.0F * 0.017453292F;
   }
}
