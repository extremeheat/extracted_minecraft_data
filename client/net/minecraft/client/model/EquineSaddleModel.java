package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EquineRenderState;

public class EquineSaddleModel extends AbstractEquineModel<EquineRenderState> {
   private static final String SADDLE = "saddle";
   private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
   private static final String LEFT_SADDLE_LINE = "left_saddle_line";
   private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
   private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
   private static final String HEAD_SADDLE = "head_saddle";
   private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
   private final ModelPart[] ridingParts;

   public EquineSaddleModel(ModelPart var1) {
      super(var1);
      ModelPart var2 = this.headParts.getChild("left_saddle_line");
      ModelPart var3 = this.headParts.getChild("right_saddle_line");
      this.ridingParts = new ModelPart[]{var2, var3};
   }

   public static LayerDefinition createSaddleLayer(boolean var0) {
      return createFullScaleSaddleLayer(var0).apply(var0 ? BABY_TRANSFORMER : MeshTransformer.IDENTITY);
   }

   public static LayerDefinition createFullScaleSaddleLayer(boolean var0) {
      MeshDefinition var1 = var0 ? createFullScaleBabyMesh(CubeDeformation.NONE) : createBodyMesh(CubeDeformation.NONE);
      PartDefinition var2 = var1.getRoot();
      PartDefinition var3 = var2.getChild("body");
      PartDefinition var4 = var2.getChild("head_parts");
      var3.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(26, 0).addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
      var4.addOrReplaceChild("left_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F), PartPose.ZERO);
      var4.addOrReplaceChild("right_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F), PartPose.ZERO);
      var4.addOrReplaceChild("left_saddle_line", CubeListBuilder.create().texOffs(32, 2).addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F), PartPose.rotation(-0.5235988F, 0.0F, 0.0F));
      var4.addOrReplaceChild("right_saddle_line", CubeListBuilder.create().texOffs(32, 2).addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F), PartPose.rotation(-0.5235988F, 0.0F, 0.0F));
      var4.addOrReplaceChild("head_saddle", CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.22F)), PartPose.ZERO);
      var4.addOrReplaceChild("mouth_saddle_wrap", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.ZERO);
      return LayerDefinition.create(var1, 64, 64);
   }

   public void setupAnim(EquineRenderState var1) {
      super.setupAnim(var1);

      for(ModelPart var5 : this.ridingParts) {
         var5.visible = var1.isRidden;
      }

   }
}
