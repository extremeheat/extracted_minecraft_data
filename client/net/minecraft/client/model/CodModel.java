package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class CodModel extends EntityModel<LivingEntityRenderState> {
   private final ModelPart tailFin;

   public CodModel(ModelPart var1) {
      super(var1);
      this.tailFin = var1.getChild("tail_fin");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      byte var2 = 22;
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 7.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(11, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
      var1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.offset(0.0F, 22.0F, -3.0F));
      var1.addOrReplaceChild(
         "right_fin",
         CubeListBuilder.create().texOffs(22, 1).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F),
         PartPose.offsetAndRotation(-1.0F, 23.0F, 0.0F, 0.0F, 0.0F, -0.7853982F)
      );
      var1.addOrReplaceChild(
         "left_fin",
         CubeListBuilder.create().texOffs(22, 4).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F),
         PartPose.offsetAndRotation(1.0F, 23.0F, 0.0F, 0.0F, 0.0F, 0.7853982F)
      );
      var1.addOrReplaceChild(
         "tail_fin", CubeListBuilder.create().texOffs(22, 3).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F), PartPose.offset(0.0F, 22.0F, 7.0F)
      );
      var1.addOrReplaceChild(
         "top_fin", CubeListBuilder.create().texOffs(20, -6).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 20.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(LivingEntityRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.isInWater ? 1.0F : 1.5F;
      this.tailFin.yRot = -var2 * 0.45F * Mth.sin(0.6F * var1.ageInTicks);
   }
}
