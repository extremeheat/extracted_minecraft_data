package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.util.Mth;

public class TropicalFishModelA extends EntityModel<TropicalFishRenderState> {
   private final ModelPart root;
   private final ModelPart tail;

   public TropicalFishModelA(ModelPart var1) {
      super();
      this.root = var1;
      this.tail = var1.getChild("tail");
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      byte var3 = 22;
      var2.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, var0), PartPose.offset(0.0F, 22.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "tail", CubeListBuilder.create().texOffs(22, -6).addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, var0), PartPose.offset(0.0F, 22.0F, 3.0F)
      );
      var2.addOrReplaceChild(
         "right_fin",
         CubeListBuilder.create().texOffs(2, 16).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, var0),
         PartPose.offsetAndRotation(-1.0F, 22.5F, 0.0F, 0.0F, 0.7853982F, 0.0F)
      );
      var2.addOrReplaceChild(
         "left_fin",
         CubeListBuilder.create().texOffs(2, 12).addBox(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, var0),
         PartPose.offsetAndRotation(1.0F, 22.5F, 0.0F, 0.0F, -0.7853982F, 0.0F)
      );
      var2.addOrReplaceChild(
         "top_fin", CubeListBuilder.create().texOffs(10, -5).addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, var0), PartPose.offset(0.0F, 20.5F, -3.0F)
      );
      return LayerDefinition.create(var1, 32, 32);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(TropicalFishRenderState var1) {
      float var2 = var1.isInWater ? 1.0F : 1.5F;
      this.tail.yRot = -var2 * 0.45F * Mth.sin(0.6F * var1.ageInTicks);
   }
}
