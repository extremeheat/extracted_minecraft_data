package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;

class TextureMap$1 implements Callable {
   TextureMap$1(TextureMap var1, TextureAtlasSprite var2) {
      super();
      this.field_147979_b = var1;
      this.field_147980_a = var2;
   }

   public String call() {
      return this.field_147980_a.func_94215_i();
   }
}
