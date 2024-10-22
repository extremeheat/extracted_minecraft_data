package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class BeeStingerModel extends Model {
   public BeeStingerModel(ModelPart var1) {
      super(var1, RenderType::entityCutout);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F);
      var1.addOrReplaceChild("cross_1", var2, PartPose.rotation(0.7853982F, 0.0F, 0.0F));
      var1.addOrReplaceChild("cross_2", var2, PartPose.rotation(2.3561945F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 16, 16);
   }
}
