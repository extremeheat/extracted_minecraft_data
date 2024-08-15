package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.SalmonRenderState;
import net.minecraft.util.Mth;

public class SalmonModel extends EntityModel<SalmonRenderState> {
   public static final MeshTransformer SMALL_TRANSFORMER = MeshTransformer.scaling(0.5F);
   public static final MeshTransformer LARGE_TRANSFORMER = MeshTransformer.scaling(1.5F);
   private static final String BODY_FRONT = "body_front";
   private static final String BODY_BACK = "body_back";
   private final ModelPart root;
   private final ModelPart bodyBack;

   public SalmonModel(ModelPart var1) {
      super();
      this.root = var1;
      this.bodyBack = var1.getChild("body_back");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      byte var2 = 20;
      PartDefinition var3 = var1.addOrReplaceChild(
         "body_front", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), PartPose.offset(0.0F, 20.0F, 0.0F)
      );
      PartDefinition var4 = var1.addOrReplaceChild(
         "body_back", CubeListBuilder.create().texOffs(0, 13).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), PartPose.offset(0.0F, 20.0F, 8.0F)
      );
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), PartPose.offset(0.0F, 20.0F, 0.0F));
      var4.addOrReplaceChild(
         "back_fin", CubeListBuilder.create().texOffs(20, 10).addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 8.0F)
      );
      var3.addOrReplaceChild(
         "top_front_fin", CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F), PartPose.offset(0.0F, -4.5F, 5.0F)
      );
      var4.addOrReplaceChild(
         "top_back_fin", CubeListBuilder.create().texOffs(0, 2).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F), PartPose.offset(0.0F, -4.5F, -1.0F)
      );
      var1.addOrReplaceChild(
         "right_fin",
         CubeListBuilder.create().texOffs(-4, 0).addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F),
         PartPose.offsetAndRotation(-1.5F, 21.5F, 0.0F, 0.0F, 0.0F, -0.7853982F)
      );
      var1.addOrReplaceChild(
         "left_fin",
         CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F),
         PartPose.offsetAndRotation(1.5F, 21.5F, 0.0F, 0.0F, 0.0F, 0.7853982F)
      );
      return LayerDefinition.create(var0, 32, 32);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(SalmonRenderState var1) {
      float var2 = 1.0F;
      float var3 = 1.0F;
      if (!var1.isInWater) {
         var2 = 1.3F;
         var3 = 1.7F;
      }

      this.bodyBack.yRot = -var2 * 0.25F * Mth.sin(var3 * 0.6F * var1.ageInTicks);
   }
}
