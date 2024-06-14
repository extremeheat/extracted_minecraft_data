package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingTextureManager extends TextureAtlasHolder {
   private static final ResourceLocation BACK_SPRITE_LOCATION = ResourceLocation.withDefaultNamespace("back");

   public PaintingTextureManager(TextureManager var1) {
      super(var1, ResourceLocation.withDefaultNamespace("textures/atlas/paintings.png"), ResourceLocation.withDefaultNamespace("paintings"));
   }

   public TextureAtlasSprite get(PaintingVariant var1) {
      return this.getSprite(var1.assetId());
   }

   public TextureAtlasSprite getBackSprite() {
      return this.getSprite(BACK_SPRITE_LOCATION);
   }
}
