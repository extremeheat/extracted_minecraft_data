package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;

public class LlamaRenderer extends MobRenderer<Llama, LlamaModel<Llama>> {
   private static final ResourceLocation CREAMY = ResourceLocation.withDefaultNamespace("textures/entity/llama/creamy.png");
   private static final ResourceLocation WHITE = ResourceLocation.withDefaultNamespace("textures/entity/llama/white.png");
   private static final ResourceLocation BROWN = ResourceLocation.withDefaultNamespace("textures/entity/llama/brown.png");
   private static final ResourceLocation GRAY = ResourceLocation.withDefaultNamespace("textures/entity/llama/gray.png");

   public LlamaRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1, new LlamaModel(var1.bakeLayer(var2)), 0.7F);
      this.addLayer(new LlamaDecorLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Llama var1) {
      ResourceLocation var10000;
      switch (var1.getVariant()) {
         case CREAMY -> var10000 = CREAMY;
         case WHITE -> var10000 = WHITE;
         case BROWN -> var10000 = BROWN;
         case GRAY -> var10000 = GRAY;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
