package net.minecraft.client.renderer.texture.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

@FunctionalInterface
public interface SpriteResourceLoader {
   Logger LOGGER = LogUtils.getLogger();

   static SpriteResourceLoader create(Collection<MetadataSectionSerializer<?>> var0) {
      return (var1, var2) -> {
         ResourceMetadata var3;
         try {
            var3 = var2.metadata().copySections(var0);
         } catch (Exception var9) {
            LOGGER.error("Unable to parse metadata from {}", var1, var9);
            return null;
         }

         NativeImage var4;
         try {
            InputStream var5 = var2.open();

            try {
               var4 = NativeImage.read(var5);
            } catch (Throwable var10) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var10.addSuppressed(var8);
                  }
               }

               throw var10;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (IOException var11) {
            LOGGER.error("Using missing texture, unable to load {}", var1, var11);
            return null;
         }

         AnimationMetadataSection var12 = (AnimationMetadataSection)var3.getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
         FrameSize var6 = var12.calculateFrameSize(var4.getWidth(), var4.getHeight());
         if (Mth.isMultipleOf(var4.getWidth(), var6.width()) && Mth.isMultipleOf(var4.getHeight(), var6.height())) {
            return new SpriteContents(var1, var6, var4, var3);
         } else {
            LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{var1, var4.getWidth(), var4.getHeight(), var6.width(), var6.height()});
            var4.close();
            return null;
         }
      };
   }

   @Nullable
   SpriteContents loadSprite(ResourceLocation var1, Resource var2);
}
