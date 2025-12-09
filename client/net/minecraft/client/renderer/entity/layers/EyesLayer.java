package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class EyesLayer<S extends EntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   public EyesLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      var2.order(1).submitModel(this.getParentModel(), var4, var1, this.renderType(), var3, OverlayTexture.NO_OVERLAY, -1, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public abstract RenderType renderType();
}
