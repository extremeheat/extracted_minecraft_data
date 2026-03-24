package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
   public LegacyStuffWrapper() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static int[] getPixels(final ResourceManager resourceManager, final Identifier location) throws IOException {
      InputStream resource = resourceManager.open(location);

      int[] var4;
      try (NativeImage image = NativeImage.read(resource)) {
         var4 = image.makePixelArray();
      } catch (Throwable var9) {
         if (resource != null) {
            try {
               resource.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (resource != null) {
         resource.close();
      }

      return var4;
   }
}
