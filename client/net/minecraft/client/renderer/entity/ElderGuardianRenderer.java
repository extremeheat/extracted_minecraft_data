package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.resources.ResourceLocation;

public class ElderGuardianRenderer extends GuardianRenderer {
   public static final ResourceLocation GUARDIAN_ELDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian_elder.png");

   public ElderGuardianRenderer(EntityRendererProvider.Context var1) {
      super(var1, 1.2F, ModelLayers.ELDER_GUARDIAN);
   }

   @Override
   public ResourceLocation getTextureLocation(GuardianRenderState var1) {
      return GUARDIAN_ELDER_LOCATION;
   }
}