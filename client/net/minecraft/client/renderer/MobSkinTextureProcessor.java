package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;

public class MobSkinTextureProcessor implements HttpTextureProcessor {
   public MobSkinTextureProcessor() {
      super();
   }

   public NativeImage process(NativeImage var1) {
      boolean var2 = var1.getHeight() == 32;
      if (var2) {
         NativeImage var3 = new NativeImage(64, 64, true);
         var3.copyFrom(var1);
         var1.close();
         var1 = var3;
         var3.fillRect(0, 32, 64, 32, 0);
         var3.copyRect(4, 16, 16, 32, 4, 4, true, false);
         var3.copyRect(8, 16, 16, 32, 4, 4, true, false);
         var3.copyRect(0, 20, 24, 32, 4, 12, true, false);
         var3.copyRect(4, 20, 16, 32, 4, 12, true, false);
         var3.copyRect(8, 20, 8, 32, 4, 12, true, false);
         var3.copyRect(12, 20, 16, 32, 4, 12, true, false);
         var3.copyRect(44, 16, -8, 32, 4, 4, true, false);
         var3.copyRect(48, 16, -8, 32, 4, 4, true, false);
         var3.copyRect(40, 20, 0, 32, 4, 12, true, false);
         var3.copyRect(44, 20, -8, 32, 4, 12, true, false);
         var3.copyRect(48, 20, -16, 32, 4, 12, true, false);
         var3.copyRect(52, 20, -8, 32, 4, 12, true, false);
      }

      setNoAlpha(var1, 0, 0, 32, 16);
      if (var2) {
         doLegacyTransparencyHack(var1, 32, 0, 64, 32);
      }

      setNoAlpha(var1, 0, 16, 64, 32);
      setNoAlpha(var1, 16, 48, 48, 64);
      return var1;
   }

   public void onTextureDownloaded() {
   }

   private static void doLegacyTransparencyHack(NativeImage var0, int var1, int var2, int var3, int var4) {
      int var5;
      int var6;
      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            int var7 = var0.getPixelRGBA(var5, var6);
            if ((var7 >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            var0.setPixelRGBA(var5, var6, var0.getPixelRGBA(var5, var6) & 16777215);
         }
      }

   }

   private static void setNoAlpha(NativeImage var0, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            var0.setPixelRGBA(var5, var6, var0.getPixelRGBA(var5, var6) | -16777216);
         }
      }

   }
}
