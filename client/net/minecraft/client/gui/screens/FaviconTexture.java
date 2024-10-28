package net.minecraft.client.gui.screens;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class FaviconTexture implements AutoCloseable {
   private static final ResourceLocation MISSING_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/unknown_server.png");
   private static final int WIDTH = 64;
   private static final int HEIGHT = 64;
   private final TextureManager textureManager;
   private final ResourceLocation textureLocation;
   @Nullable
   private DynamicTexture texture;
   private boolean closed;

   private FaviconTexture(TextureManager var1, ResourceLocation var2) {
      super();
      this.textureManager = var1;
      this.textureLocation = var2;
   }

   public static FaviconTexture forWorld(TextureManager var0, String var1) {
      String var10003 = Util.sanitizeName(var1, ResourceLocation::validPathChar);
      return new FaviconTexture(var0, ResourceLocation.withDefaultNamespace("worlds/" + var10003 + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars(var1)) + "/icon"));
   }

   public static FaviconTexture forServer(TextureManager var0, String var1) {
      String var10003 = String.valueOf(Hashing.sha1().hashUnencodedChars(var1));
      return new FaviconTexture(var0, ResourceLocation.withDefaultNamespace("servers/" + var10003 + "/icon"));
   }

   public void upload(NativeImage var1) {
      if (var1.getWidth() == 64 && var1.getHeight() == 64) {
         try {
            this.checkOpen();
            if (this.texture == null) {
               this.texture = new DynamicTexture(var1);
            } else {
               this.texture.setPixels(var1);
               this.texture.upload();
            }

            this.textureManager.register((ResourceLocation)this.textureLocation, (AbstractTexture)this.texture);
         } catch (Throwable var3) {
            var1.close();
            this.clear();
            throw var3;
         }
      } else {
         var1.close();
         int var10002 = var1.getWidth();
         throw new IllegalArgumentException("Icon must be 64x64, but was " + var10002 + "x" + var1.getHeight());
      }
   }

   public void clear() {
      this.checkOpen();
      if (this.texture != null) {
         this.textureManager.release(this.textureLocation);
         this.texture.close();
         this.texture = null;
      }

   }

   public ResourceLocation textureLocation() {
      return this.texture != null ? this.textureLocation : MISSING_LOCATION;
   }

   public void close() {
      this.clear();
      this.closed = true;
   }

   private void checkOpen() {
      if (this.closed) {
         throw new IllegalStateException("Icon already closed");
      }
   }
}
