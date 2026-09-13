package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;

class TextureManager$1 implements Callable {
   TextureManager$1(TextureManager var1, ITextureObject var2) {
      super();
      this.field_135061_b = var1;
      this.field_135062_a = var2;
   }

   public String call() {
      return this.field_135062_a.getClass().getName();
   }
}
