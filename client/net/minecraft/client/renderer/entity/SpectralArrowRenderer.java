package net.minecraft.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.SpectralArrow;

public class SpectralArrowRenderer extends ArrowRenderer<SpectralArrow> {
   public static final ResourceLocation SPECTRAL_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public ResourceLocation getTextureLocation(SpectralArrow var1) {
      return SPECTRAL_ARROW_LOCATION;
   }
}
