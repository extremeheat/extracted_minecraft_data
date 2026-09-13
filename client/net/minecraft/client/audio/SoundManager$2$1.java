package net.minecraft.client.audio;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import net.minecraft.client.Minecraft;

class SoundManager$2$1 extends URLConnection {
   SoundManager$2$1(SoundManager$2 var1, URL var2) {
      super(var2);
      this.field_148593_a = var1;
   }

   @Override
   public void connect() {
   }

   @Override
   public InputStream getInputStream() {
      return Minecraft.func_71410_x().func_110442_L().func_110536_a(this.field_148593_a.field_148592_a).func_110527_b();
   }
}
