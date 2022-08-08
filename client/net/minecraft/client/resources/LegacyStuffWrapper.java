package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
   public LegacyStuffWrapper() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static int[] getPixels(ResourceManager var0, ResourceLocation var1) throws IOException {
      InputStream var2 = var0.open(var1);

      int[] var4;
      try {
         NativeImage var3 = NativeImage.read(var2);

         try {
            var4 = var3.makePixelArray();
         } catch (Throwable var8) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (Throwable var9) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (var2 != null) {
         var2.close();
      }

      return var4;
   }
}
