package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.util.Mth;

public class ArrowModel extends EntityModel<ArrowRenderState> {
   public ArrowModel(ModelPart var1) {
      super(var1, RenderType::entityCutout);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(-11.0F, 0.0F, 0.0F, 0.7853982F, 0.0F, 0.0F).withScale(0.8F));
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -2.0F, 0.0F, 16.0F, 4.0F, 0.0F, CubeDeformation.NONE, 1.0F, 0.8F);
      var1.addOrReplaceChild("cross_1", var2, PartPose.rotation(0.7853982F, 0.0F, 0.0F));
      var1.addOrReplaceChild("cross_2", var2, PartPose.rotation(2.3561945F, 0.0F, 0.0F));
      return LayerDefinition.create(var0.transformed((var0x) -> {
         return var0x.scaled(0.9F);
      }), 32, 32);
   }

   public void setupAnim(ArrowRenderState var1) {
      super.setupAnim(var1);
      if (var1.shake > 0.0F) {
         float var2 = -Mth.sin(var1.shake * 3.0F) * var1.shake;
         ModelPart var10000 = this.root;
         var10000.zRot += var2 * 0.017453292F;
      }

   }
}
