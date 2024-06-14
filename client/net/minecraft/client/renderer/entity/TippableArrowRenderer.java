package net.minecraft.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;

public class TippableArrowRenderer extends ArrowRenderer<Arrow> {
   public static final ResourceLocation NORMAL_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
   public static final ResourceLocation TIPPED_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/tipped_arrow.png");

   public TippableArrowRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public ResourceLocation getTextureLocation(Arrow var1) {
      return var1.getColor() > 0 ? TIPPED_ARROW_LOCATION : NORMAL_ARROW_LOCATION;
   }
}
