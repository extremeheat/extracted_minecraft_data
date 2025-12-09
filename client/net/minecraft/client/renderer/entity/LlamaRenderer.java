package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.Llama;

public class LlamaRenderer extends AgeableMobRenderer<Llama, LlamaRenderState, LlamaModel> {
   private static final Identifier CREAMY = Identifier.withDefaultNamespace("textures/entity/llama/creamy.png");
   private static final Identifier WHITE = Identifier.withDefaultNamespace("textures/entity/llama/white.png");
   private static final Identifier BROWN = Identifier.withDefaultNamespace("textures/entity/llama/brown.png");
   private static final Identifier GRAY = Identifier.withDefaultNamespace("textures/entity/llama/gray.png");

   public LlamaRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3) {
      super(var1, new LlamaModel(var1.bakeLayer(var2)), new LlamaModel(var1.bakeLayer(var3)), 0.7F);
      this.addLayer(new LlamaDecorLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
   }

   public Identifier getTextureLocation(LlamaRenderState var1) {
      Identifier var10000;
      switch (var1.variant) {
         case CREAMY -> var10000 = CREAMY;
         case WHITE -> var10000 = WHITE;
         case BROWN -> var10000 = BROWN;
         case GRAY -> var10000 = GRAY;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
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

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((LlamaRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
