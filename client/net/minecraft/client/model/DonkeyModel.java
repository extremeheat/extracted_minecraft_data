package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;

public class DonkeyModel extends AbstractEquineModel<DonkeyRenderState> {
   public static final float DONKEY_SCALE = 0.87F;
   public static final float MULE_SCALE = 0.92F;
   private final ModelPart leftChest;
   private final ModelPart rightChest;

   public DonkeyModel(ModelPart var1) {
      super(var1);
      this.leftChest = this.body.getChild("left_chest");
      this.rightChest = this.body.getChild("right_chest");
   }

   public static LayerDefinition createBodyLayer(float var0) {
      MeshDefinition var1 = AbstractEquineModel.createBodyMesh(CubeDeformation.NONE);
      modifyMesh(var1.getRoot());
      return LayerDefinition.create(var1, 64, 64).apply(MeshTransformer.scaling(var0));
   }

   public static LayerDefinition createBabyLayer(float var0) {
      MeshDefinition var1 = AbstractEquineModel.createFullScaleBabyMesh(CubeDeformation.NONE);
      modifyMesh(var1.getRoot());
      return LayerDefinition.create(AbstractEquineModel.BABY_TRANSFORMER.apply(var1), 64, 64).apply(MeshTransformer.scaling(var0));
   }

   private static void modifyMesh(PartDefinition var0) {
      PartDefinition var1 = var0.getChild("body");
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(26, 21).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
      var1.addOrReplaceChild("left_chest", var2, PartPose.offsetAndRotation(6.0F, -8.0F, 0.0F, 0.0F, -1.5707964F, 0.0F));
      var1.addOrReplaceChild("right_chest", var2, PartPose.offsetAndRotation(-6.0F, -8.0F, 0.0F, 0.0F, 1.5707964F, 0.0F));
      PartDefinition var3 = var0.getChild("head_parts").getChild("head");
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
      var3.addOrReplaceChild("left_ear", var4, PartPose.offsetAndRotation(1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, 0.2617994F));
      var3.addOrReplaceChild("right_ear", var4, PartPose.offsetAndRotation(-1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, -0.2617994F));
   }

   public void setupAnim(DonkeyRenderState var1) {
      super.setupAnim((EquineRenderState)var1);
      this.leftChest.visible = var1.hasChest;
      this.rightChest.visible = var1.hasChest;
   }
}
