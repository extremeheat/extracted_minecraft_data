package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.SnifferRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.phys.AABB;

public class SnifferRenderer extends AgeableMobRenderer<Sniffer, SnifferRenderState, SnifferModel> {
   private static final ResourceLocation SNIFFER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sniffer/sniffer.png");

   public SnifferRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SnifferModel(var1.bakeLayer(ModelLayers.SNIFFER)), new SnifferModel(var1.bakeLayer(ModelLayers.SNIFFER_BABY)), 1.1F);
   }

   public ResourceLocation getTextureLocation(SnifferRenderState var1) {
      return SNIFFER_LOCATION;
   }

   public SnifferRenderState createRenderState() {
      return new SnifferRenderState();
   }

   public void extractRenderState(Sniffer var1, SnifferRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isSearching = var1.isSearching();
      var2.diggingAnimationState.copyFrom(var1.diggingAnimationState);
      var2.sniffingAnimationState.copyFrom(var1.sniffingAnimationState);
      var2.risingAnimationState.copyFrom(var1.risingAnimationState);
      var2.feelingHappyAnimationState.copyFrom(var1.feelingHappyAnimationState);
      var2.scentingAnimationState.copyFrom(var1.scentingAnimationState);
   }

   protected AABB getBoundingBoxForCulling(Sniffer var1) {
      return super.getBoundingBoxForCulling(var1).inflate(0.6000000238418579);
   }
}
