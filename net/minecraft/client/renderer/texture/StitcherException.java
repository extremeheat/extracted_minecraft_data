package net.minecraft.client.renderer.texture;

import java.util.Collection;

public class StitcherException extends RuntimeException {
   private final Collection allSprites;

   public StitcherException(TextureAtlasSprite.Info var1, Collection var2) {
      super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", var1.name(), var1.width(), var1.height()));
      this.allSprites = var2;
   }

   public Collection getAllSprites() {
      return this.allSprites;
   }
}
