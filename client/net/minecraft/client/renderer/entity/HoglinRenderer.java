package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.HoglinRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class HoglinRenderer extends AbstractHoglinRenderer<Hoglin> {
   private static final Identifier HOGLIN_LOCATION = Identifier.withDefaultNamespace("textures/entity/hoglin/hoglin.png");

   public HoglinRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.HOGLIN, ModelLayers.HOGLIN_BABY, 0.7F);
   }

   public Identifier getTextureLocation(HoglinRenderState var1) {
      return HOGLIN_LOCATION;
   }

   public void extractRenderState(Hoglin var1, HoglinRenderState var2, float var3) {
      super.extractRenderState(var1, (HoglinRenderState)var2, var3);
      var2.isConverting = var1.isConverting();
   }

   protected boolean isShaking(HoglinRenderState var1) {
      return super.isShaking(var1) || var1.isConverting;
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState var1) {
      return this.isShaking((HoglinRenderState)var1);
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((HoglinRenderState)var1);
   }
}
