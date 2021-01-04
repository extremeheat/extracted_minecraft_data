package net.minecraft.client.renderer.texture;

import java.util.Collection;

public class StitcherException extends RuntimeException {
   private final Collection<TextureAtlasSprite> allSprites;

   public StitcherException(TextureAtlasSprite var1, Collection<TextureAtlasSprite> var2) {
      super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", var1.getName(), var1.getWidth(), var1.getHeight()));
      this.allSprites = var2;
   }

   public Collection<TextureAtlasSprite> getAllSprites() {
      return this.allSprites;
   }
}
