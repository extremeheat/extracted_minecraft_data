package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
   @Deprecated
   public static int[] getPixels(ResourceManager var0, ResourceLocation var1) throws IOException {
      Resource var2 = var0.getResource(var1);
      Throwable var3 = null;

      Object var6;
      try {
         NativeImage var4 = NativeImage.read(var2.getInputStream());
         Throwable var5 = null;

         try {
            var6 = var4.makePixelArray();
         } catch (Throwable var29) {
            var6 = var29;
            var5 = var29;
            throw var29;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var28) {
                     var5.addSuppressed(var28);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var31) {
         var3 = var31;
         throw var31;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var27) {
                  var3.addSuppressed(var27);
               }
            } else {
               var2.close();
            }
         }

      }

      return (int[])var6;
   }
}
