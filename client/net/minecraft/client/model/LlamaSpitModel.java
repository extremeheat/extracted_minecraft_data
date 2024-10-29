package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class LlamaSpitModel extends EntityModel<EntityRenderState> {
   private static final String MAIN = "main";

   public LlamaSpitModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      boolean var2 = true;
      var1.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F).addBox(0.0F, -4.0F, 0.0F, 2.0F, 2.0F, 2.0F).addBox(0.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F).addBox(2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F).addBox(0.0F, 2.0F, 0.0F, 2.0F, 2.0F, 2.0F).addBox(0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }
}
