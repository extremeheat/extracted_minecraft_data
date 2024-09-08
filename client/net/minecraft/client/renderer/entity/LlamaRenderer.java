package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;

public class LlamaRenderer extends AgeableMobRenderer<Llama, LlamaRenderState, LlamaModel> {
   private static final ResourceLocation CREAMY = ResourceLocation.withDefaultNamespace("textures/entity/llama/creamy.png");
   private static final ResourceLocation WHITE = ResourceLocation.withDefaultNamespace("textures/entity/llama/white.png");
   private static final ResourceLocation BROWN = ResourceLocation.withDefaultNamespace("textures/entity/llama/brown.png");
   private static final ResourceLocation GRAY = ResourceLocation.withDefaultNamespace("textures/entity/llama/gray.png");

   public LlamaRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3) {
      super(var1, new LlamaModel(var1.bakeLayer(var2)), new LlamaModel(var1.bakeLayer(var3)), 0.7F);
      this.addLayer(new LlamaDecorLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
   }

   public ResourceLocation getTextureLocation(LlamaRenderState var1) {
      return switch (var1.variant) {
         case CREAMY -> CREAMY;
         case WHITE -> WHITE;
         case BROWN -> BROWN;
         case GRAY -> GRAY;
      };
   }

   public LlamaRenderState createRenderState() {
      return new LlamaRenderState();
   }

   public void extractRenderState(Llama var1, LlamaRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
      var2.hasChest = !var1.isBaby() && var1.hasChest();
      var2.bodyItem = var1.getBodyArmorItem();
      var2.isTraderLlama = var1.isTraderLlama();
   }
}
