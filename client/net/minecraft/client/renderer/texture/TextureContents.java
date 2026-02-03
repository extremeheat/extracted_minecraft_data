package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

public record TextureContents(NativeImage image, @Nullable TextureMetadataSection metadata) implements Closeable {
   public TextureContents {
      super();
   }

   public static TextureContents load(final ResourceManager resourceManager, final Identifier location) throws IOException {
      Resource resource = resourceManager.getResourceOrThrow(location);
      InputStream is = resource.open();

      NativeImage image;
      try {
         image = NativeImage.read(is);
      } catch (Throwable var8) {
         if (is != null) {
            try {
               is.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (is != null) {
         is.close();
      }

      TextureMetadataSection metadata = (TextureMetadataSection)resource.metadata().getSection(TextureMetadataSection.TYPE).orElse((Object)null);
      return new TextureContents(image, metadata);
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
