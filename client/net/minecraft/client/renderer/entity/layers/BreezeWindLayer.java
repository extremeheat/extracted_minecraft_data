package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreezeWindLayer extends RenderLayer<BreezeRenderState, BreezeModel> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze_wind.png");
   private final BreezeModel model;

   public BreezeWindLayer(EntityRendererProvider.Context var1, RenderLayerParent<BreezeRenderState, BreezeModel> var2) {
      super(var2);
      this.model = new BreezeModel(var1.bakeLayer(ModelLayers.BREEZE_WIND));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, BreezeRenderState var4, float var5, float var6) {
      VertexConsumer var7 = var2.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(var4.ageInTicks) % 1.0F, 0.0F));
      this.model.setupAnim(var4);
      BreezeRenderer.enable(this.model, this.model.wind()).renderToBuffer(var1, var7, var3, OverlayTexture.NO_OVERLAY);
   }

   private float xOffset(float var1) {
      return var1 * 0.02F;
   }
}
