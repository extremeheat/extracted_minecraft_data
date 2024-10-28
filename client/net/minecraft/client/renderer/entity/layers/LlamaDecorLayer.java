package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.DyeColor;

public class LlamaDecorLayer extends RenderLayer<Llama, LlamaModel<Llama>> {
   private static final ResourceLocation[] TEXTURE_LOCATION = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/white.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/orange.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/magenta.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/light_blue.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/yellow.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/lime.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/pink.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/gray.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/light_gray.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/cyan.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/purple.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/blue.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/brown.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/green.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/red.png"), ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/black.png")};
   private static final ResourceLocation TRADER_LLAMA = ResourceLocation.withDefaultNamespace("textures/entity/llama/decor/trader_llama.png");
   private final LlamaModel<Llama> model;

   public LlamaDecorLayer(RenderLayerParent<Llama, LlamaModel<Llama>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new LlamaModel(var2.bakeLayer(ModelLayers.LLAMA_DECOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Llama var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      DyeColor var11 = var4.getSwag();
      ResourceLocation var12;
      if (var11 != null) {
         var12 = TEXTURE_LOCATION[var11.getId()];
      } else {
         if (!var4.isTraderLlama()) {
            return;
         }

         var12 = TRADER_LLAMA;
      }

      ((LlamaModel)this.getParentModel()).copyPropertiesTo(this.model);
      this.model.setupAnim((AbstractChestedHorse)var4, var5, var6, var8, var9, var10);
      VertexConsumer var13 = var2.getBuffer(RenderType.entityCutoutNoCull(var12));
      this.model.renderToBuffer(var1, var13, var3, OverlayTexture.NO_OVERLAY);
   }
}
