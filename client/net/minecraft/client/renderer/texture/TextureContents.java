package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public record TextureContents(NativeImage image, @Nullable TextureMetadataSection metadata) implements Closeable {
   public TextureContents(NativeImage var1, @Nullable TextureMetadataSection var2) {
      super();
      this.image = var1;
      this.metadata = var2;
   }

   public static TextureContents load(ResourceManager var0, ResourceLocation var1) throws IOException {
      Resource var2 = var0.getResourceOrThrow(var1);
      InputStream var4 = var2.open();

      NativeImage var3;
      try {
         var3 = NativeImage.read(var4);
      } catch (Throwable var8) {
         if (var4 != null) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var4 != null) {
         var4.close();
      }

      TextureMetadataSection var9 = (TextureMetadataSection)var2.metadata().getSection(TextureMetadataSection.TYPE).orElse((Object)null);
      return new TextureContents(var3, var9);
   }

   public static TextureContents createMissing() {
      return new TextureContents(MissingTextureAtlasSprite.generateMissingImage(), (TextureMetadataSection)null);
   }

   public boolean blur() {
      return this.metadata != null ? this.metadata.blur() : false;
   }

   public boolean clamp() {
      return this.metadata != null ? this.metadata.clamp() : false;
   }

   public void close() {
      this.image.close();
   }
}
