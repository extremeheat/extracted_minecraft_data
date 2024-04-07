package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;

public class LlamaRenderer extends MobRenderer<Llama, LlamaModel<Llama>> {
   private static final ResourceLocation CREAMY = new ResourceLocation("textures/entity/llama/creamy.png");
   private static final ResourceLocation WHITE = new ResourceLocation("textures/entity/llama/white.png");
   private static final ResourceLocation BROWN = new ResourceLocation("textures/entity/llama/brown.png");
   private static final ResourceLocation GRAY = new ResourceLocation("textures/entity/llama/gray.png");

   public LlamaRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1, new LlamaModel<>(var1.bakeLayer(var2)), 0.7F);
      this.addLayer(new LlamaDecorLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Llama var1) {
      return switch (var1.getVariant()) {
         case CREAMY -> CREAMY;
         case WHITE -> WHITE;
         case BROWN -> BROWN;
         case GRAY -> GRAY;
         default -> throw new MatchException(null, null);
      };
   }
}
