package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;

class TextureMap$2 implements Callable {
   TextureMap$2(TextureMap var1, TextureAtlasSprite var2) {
      super();
      this.field_147976_b = var1;
      this.field_147977_a = var2;
   }

   public String call() {
      return this.field_147977_a.func_94211_a() + " x " + this.field_147977_a.func_94216_b();
   }
}
