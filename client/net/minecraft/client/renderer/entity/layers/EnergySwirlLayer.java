package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public abstract class EnergySwirlLayer<S extends EntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   public EnergySwirlLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      if (this.isPowered(var4)) {
         float var7 = var4.ageInTicks;
         EntityModel var8 = this.model();
         var2.order(1).submitModel(var8, var4, var1, RenderTypes.energySwirl(this.getTextureLocation(), this.xOffset(var7) % 1.0F, var7 * 0.01F % 1.0F), var3, OverlayTexture.NO_OVERLAY, -8355712, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }

   protected abstract boolean isPowered(S var1);

   protected abstract float xOffset(float var1);

   protected abstract Identifier getTextureLocation();

   protected abstract M model();
}
