package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;

public class SkeletonClothingLayer<S extends SkeletonRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final SkeletonModel<S> layerModel;
   private final ResourceLocation clothesLocation;

   public SkeletonClothingLayer(RenderLayerParent<S, M> var1, EntityModelSet var2, ModelLayerLocation var3, ResourceLocation var4) {
      super(var1);
      this.clothesLocation = var4;
      this.layerModel = new SkeletonModel<S>(var2.bakeLayer(var3));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      coloredCutoutModelCopyLayerRender(this.layerModel, this.clothesLocation, var1, var2, var3, var4, -1);
   }
}
