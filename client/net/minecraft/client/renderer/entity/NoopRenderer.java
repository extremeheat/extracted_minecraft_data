package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class NoopRenderer<T extends Entity> extends EntityRenderer<T> {
   public NoopRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public ResourceLocation getTextureLocation(T var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
