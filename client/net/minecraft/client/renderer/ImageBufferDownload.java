package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.NativeImage;

public class ImageBufferDownload implements IImageBuffer {
   public ImageBufferDownload() {
      super();
   }

   public NativeImage func_195786_a(NativeImage var1) {
      boolean var2 = var1.func_195714_b() == 32;
      if (var2) {
         NativeImage var3 = new NativeImage(64, 64, true);
         var3.func_195703_a(var1);
         var1.close();
         var1 = var3;
         var3.func_195715_a(0, 32, 64, 32, 0);
         var3.func_195699_a(4, 16, 16, 32, 4, 4, true, false);
         var3.func_195699_a(8, 16, 16, 32, 4, 4, true, false);
         var3.func_195699_a(0, 20, 24, 32, 4, 12, true, false);
         var3.func_195699_a(4, 20, 16, 32, 4, 12, true, false);
         var3.func_195699_a(8, 20, 8, 32, 4, 12, true, false);
         var3.func_195699_a(12, 20, 16, 32, 4, 12, true, false);
         var3.func_195699_a(44, 16, -8, 32, 4, 4, true, false);
         var3.func_195699_a(48, 16, -8, 32, 4, 4, true, false);
         var3.func_195699_a(40, 20, 0, 32, 4, 12, true, false);
         var3.func_195699_a(44, 20, -8, 32, 4, 12, true, false);
         var3.func_195699_a(48, 20, -16, 32, 4, 12, true, false);
         var3.func_195699_a(52, 20, -8, 32, 4, 12, true, false);
      }

      func_195787_b(var1, 0, 0, 32, 16);
      if (var2) {
         func_195788_a(var1, 32, 0, 64, 32);
      }

      func_195787_b(var1, 0, 16, 64, 32);
      func_195787_b(var1, 16, 48, 48, 64);
      return var1;
   }

   public void func_152634_a() {
   }

   private static void func_195788_a(NativeImage var0, int var1, int var2, int var3, int var4) {
      int var5;
      int var6;
      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            int var7 = var0.func_195709_a(var5, var6);
            if ((var7 >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            var0.func_195700_a(var5, var6, var0.func_195709_a(var5, var6) & 16777215);
         }
      }

   }

   private static void func_195787_b(NativeImage var0, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            var0.func_195700_a(var5, var6, var0.func_195709_a(var5, var6) | -16777216);
         }
      }

   }
}
