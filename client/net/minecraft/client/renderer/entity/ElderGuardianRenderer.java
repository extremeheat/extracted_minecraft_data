package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class ElderGuardianRenderer extends GuardianRenderer {
   public static final Identifier GUARDIAN_ELDER_LOCATION = Identifier.withDefaultNamespace("textures/entity/guardian_elder.png");

   public ElderGuardianRenderer(EntityRendererProvider.Context var1) {
      super(var1, 1.2F, ModelLayers.ELDER_GUARDIAN);
   }

   public Identifier getTextureLocation(GuardianRenderState var1) {
      return GUARDIAN_ELDER_LOCATION;
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((GuardianRenderState)var1);
   }
}
