package net.minecraft.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.SpectralArrow;

public class SpectralArrowRenderer extends ArrowRenderer<SpectralArrow> {
   public static final ResourceLocation SPECTRAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   protected ResourceLocation getTextureLocation(SpectralArrow var1) {
      return SPECTRAL_ARROW_LOCATION;
   }
}
