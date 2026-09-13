package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;

class TextureMap$3 implements Callable {
   TextureMap$3(TextureMap var1, TextureAtlasSprite var2) {
      super();
      this.field_147973_b = var1;
      this.field_147974_a = var2;
   }

   public String call() {
      return this.field_147974_a.func_110970_k() + " frames";
   }
}
