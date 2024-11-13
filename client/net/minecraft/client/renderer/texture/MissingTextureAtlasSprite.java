package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

public final class MissingTextureAtlasSprite {
   private static final int MISSING_IMAGE_WIDTH = 16;
   private static final int MISSING_IMAGE_HEIGHT = 16;
   private static final String MISSING_TEXTURE_NAME = "missingno";
   private static final ResourceLocation MISSING_TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("missingno");

   public MissingTextureAtlasSprite() {
      super();
   }

   public static NativeImage generateMissingImage() {
      return generateMissingImage(16, 16);
   }

   public static NativeImage generateMissingImage(int var0, int var1) {
      NativeImage var2 = new NativeImage(var0, var1, false);
      int var3 = -524040;

      for(int var4 = 0; var4 < var1; ++var4) {
         for(int var5 = 0; var5 < var0; ++var5) {
            if (var4 < var1 / 2 ^ var5 < var0 / 2) {
               var2.setPixel(var5, var4, -524040);
            } else {
               var2.setPixel(var5, var4, -16777216);
            }
         }
      }

      return var2;
   }

   public static SpriteContents create() {
      NativeImage var0 = generateMissingImage(16, 16);
      return new SpriteContents(MISSING_TEXTURE_LOCATION, new FrameSize(16, 16), var0, ResourceMetadata.EMPTY);
   }

   public static ResourceLocation getLocation() {
      return MISSING_TEXTURE_LOCATION;
   }
}
