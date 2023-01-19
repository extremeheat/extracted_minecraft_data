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

   @Deprecated
   public static int[] getPixels(ResourceManager var0, ResourceLocation var1) throws IOException {
      int[] var4;
      try (
         InputStream var2 = var0.open(var1);
         NativeImage var3 = NativeImage.read(var2);
      ) {
         var4 = var3.makePixelArray();
      }

      return var4;
   }
}
