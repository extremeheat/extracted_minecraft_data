package net.minecraft.client.gui.fonts;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.Util;

public enum DefaultGlyph implements IGlyphInfo {
   INSTANCE;

   private static final NativeImage field_211581_b = (NativeImage)Util.func_200696_a(new NativeImage(NativeImage.PixelFormat.RGBA, 5, 8, false), (var0) -> {
      for(int var1 = 0; var1 < 8; ++var1) {
         for(int var2 = 0; var2 < 5; ++var2) {
            boolean var3 = var2 == 0 || var2 + 1 == 5 || var1 == 0 || var1 + 1 == 8;
            var0.func_195700_a(var2, var1, var3 ? -1 : 0);
         }
      }

      var0.func_195711_f();
   });

   private DefaultGlyph() {
   }

   public int func_211202_a() {
      return 5;
   }

   public int func_211203_b() {
      return 8;
   }

   public float getAdvance() {
      return 6.0F;
   }

   public float func_211578_g() {
      return 1.0F;
   }

   public void func_211573_a(int var1, int var2) {
      field_211581_b.func_195697_a(0, var1, var2, false);
   }

   public boolean func_211579_f() {
      return true;
   }
}
