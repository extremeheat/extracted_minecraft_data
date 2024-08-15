package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LlamaDecorLayer extends RenderLayer<LlamaRenderState, LlamaModel> {
   private static final ResourceLocation[] TEXTURE_LOCATION = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/white.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/orange.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/magenta.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/light_blue.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/yellow.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/lime.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/pink.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/gray.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/light_gray.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/cyan.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/purple.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/blue.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/brown.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/green.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/red.png"),
      ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/black.png")
   };
   private static final ResourceLocation TRADER_LLAMA = ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/trader_llama.png");
   private final LlamaModel adultModel;
   private final LlamaModel babyModel;

   public LlamaDecorLayer(RenderLayerParent<LlamaRenderState, LlamaModel> var1, EntityModelSet var2) {
      super(var1);
      this.adultModel = new LlamaModel(var2.bakeLayer(ModelLayers.LLAMA_DECOR));
      this.babyModel = new LlamaModel(var2.bakeLayer(ModelLayers.LLAMA_BABY_DECOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LlamaRenderState var4, float var5, float var6) {
      ResourceLocation var7;
      if (var4.decorColor != null) {
         var7 = TEXTURE_LOCATION[var4.decorColor.getId()];
      } else {
         if (!var4.isTraderLlama) {
            return;
         }

         var7 = TRADER_LLAMA;
      }

      LlamaModel var8 = var4.isBaby ? this.babyModel : this.adultModel;
      var8.setupAnim(var4);
      VertexConsumer var9 = var2.getBuffer(RenderType.entityCutoutNoCull(var7));
      var8.renderToBuffer(var1, var9, var3, OverlayTexture.NO_OVERLAY);
   }
}
