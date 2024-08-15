package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.renderer.entity.state.CatRenderState;

public class CatModel extends FelineModel<CatRenderState> {
   public static final MeshTransformer CAT_TRANSFORMER = MeshTransformer.scaling(0.8F);

   public CatModel(ModelPart var1) {
      super(var1);
   }
}
