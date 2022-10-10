package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.util.ResourceLocation;

public class RenderSpectralArrow extends RenderArrow<EntitySpectralArrow> {
   public static final ResourceLocation field_188303_a = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

   public RenderSpectralArrow(RenderManager var1) {
      super(var1);
   }

   protected ResourceLocation func_110775_a(EntitySpectralArrow var1) {
      return field_188303_a;
   }
}
