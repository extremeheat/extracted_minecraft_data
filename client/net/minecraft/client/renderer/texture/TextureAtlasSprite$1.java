package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;

class TextureAtlasSprite$1 implements Callable {
   TextureAtlasSprite$1(TextureAtlasSprite var1, int[][] var2) {
      super();
      this.field_147982_b = var1;
      this.field_147983_a = var2;
   }

   public String call() {
      StringBuilder var1 = new StringBuilder();

      for(int[] var5 : this.field_147983_a) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append(var5 == null ? "null" : var5.length);
      }

      return var1.toString();
   }
}
