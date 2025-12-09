package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;

public class SpectralArrowRenderer extends ArrowRenderer<SpectralArrow, ArrowRenderState> {
   public static final Identifier SPECTRAL_ARROW_LOCATION = Identifier.withDefaultNamespace("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   protected Identifier getTextureLocation(ArrowRenderState var1) {
      return SPECTRAL_ARROW_LOCATION;
   }

   public ArrowRenderState createRenderState() {
      return new ArrowRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
