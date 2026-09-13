package net.minecraft.client.audio;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import net.minecraft.util.ResourceLocation;

final class SoundManager$2 extends URLStreamHandler {
   SoundManager$2(ResourceLocation var1) {
      super();
      this.field_148592_a = var1;
   }

   @Override
   protected URLConnection openConnection(URL var1) {
      return new SoundManager$2$1(this, var1);
   }
}
