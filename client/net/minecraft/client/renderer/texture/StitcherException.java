package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Locale;

public class StitcherException extends RuntimeException {
   private final Collection<Stitcher.Entry> allSprites;

   public StitcherException(Stitcher.Entry var1, Collection<Stitcher.Entry> var2) {
      super(String.format(Locale.ROOT, "Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", var1.name(), var1.width(), var1.height()));
      this.allSprites = var2;
   }

   public Collection<Stitcher.Entry> getAllSprites() {
      return this.allSprites;
   }
}
