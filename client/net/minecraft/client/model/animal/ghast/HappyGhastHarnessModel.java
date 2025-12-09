package net.minecraft.client.model.animal.ghast;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;

public class HappyGhastHarnessModel extends EntityModel<HappyGhastRenderState> {
   private static final float GOGGLES_Y_OFFSET = 14.0F;
   private final ModelPart goggles;

   public HappyGhastHarnessModel(ModelPart var1) {
      super(var1);
      this.goggles = var1.getChild("goggles");
   }

   public static LayerDefinition createHarnessLayer(boolean var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("harness", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
      var2.addOrReplaceChild("goggles", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -2.5F, -2.5F, 16.0F, 5.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 14.0F, -5.5F));
      return LayerDefinition.create(var1, 64, 64).apply(MeshTransformer.scaling(4.0F)).apply(var0 ? HappyGhastModel.BABY_TRANSFORMER : MeshTransformer.IDENTITY);
   }

   public void setupAnim(HappyGhastRenderState var1) {
      super.setupAnim(var1);
      if (var1.isRidden) {
         this.goggles.xRot = 0.0F;
         this.goggles.y = 14.0F;
      } else {
         this.goggles.xRot = -0.7854F;
         this.goggles.y = 9.0F;
      }

   }
}
