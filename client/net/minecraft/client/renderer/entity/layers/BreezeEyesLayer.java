package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreezeEyesLayer extends RenderLayer<BreezeRenderState, BreezeModel> {
   private static final RenderType BREEZE_EYES = RenderType.breezeEyes(ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze_eyes.png"));

   public BreezeEyesLayer(RenderLayerParent<BreezeRenderState, BreezeModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, BreezeRenderState var4, float var5, float var6) {
      VertexConsumer var7 = var2.getBuffer(BREEZE_EYES);
      BreezeModel var8 = this.getParentModel();
      BreezeRenderer.enable(var8, var8.head(), var8.eyes()).renderToBuffer(var1, var7, var3, OverlayTexture.NO_OVERLAY);
   }
}
