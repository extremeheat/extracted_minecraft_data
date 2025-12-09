package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.breeze.BreezeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public class BreezeWindLayer extends RenderLayer<BreezeRenderState, BreezeModel> {
   private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/breeze/breeze_wind.png");
   private final BreezeModel model;

   public BreezeWindLayer(RenderLayerParent<BreezeRenderState, BreezeModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new BreezeModel(var2.bakeLayer(ModelLayers.BREEZE_WIND));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, BreezeRenderState var4, float var5, float var6) {
      RenderType var7 = RenderTypes.breezeWind(TEXTURE_LOCATION, this.xOffset(var4.ageInTicks) % 1.0F, 0.0F);
      var2.order(1).submitModel(this.model, var4, var1, var7, var3, OverlayTexture.NO_OVERLAY, -1, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   private float xOffset(float var1) {
      return var1 * 0.02F;
   }
}
