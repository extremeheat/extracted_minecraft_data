package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.Stitcher;

public class StitcherException extends RuntimeException {
   private final Stitcher.Holder field_98149_a;

   public StitcherException(Stitcher.Holder var1, String var2) {
      super(var2);
      this.field_98149_a = var1;
   }
}
