package net.minecraft.client.renderer.texture.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

@FunctionalInterface
public interface SpriteResourceLoader {
   Logger LOGGER = LogUtils.getLogger();

   static SpriteResourceLoader create(Set<MetadataSectionType<?>> var0) {
      return (var1, var2) -> {
         Optional var3;
         List var4;
         try {
            ResourceMetadata var5 = var2.metadata();
            var3 = var5.getSection(AnimationMetadataSection.TYPE);
            var4 = var5.getTypedSections(var0);
         } catch (Exception var10) {
            LOGGER.error("Unable to parse metadata from {}", var1, var10);
            return null;
         }

         NativeImage var13;
         try {
            InputStream var6 = var2.open();

            try {
               var13 = NativeImage.read(var6);
            } catch (Throwable var11) {
               if (var6 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var9) {
                     var11.addSuppressed(var9);
                  }
               }

               throw var11;
            }

            if (var6 != null) {
               var6.close();
            }
         } catch (IOException var12) {
            LOGGER.error("Using missing texture, unable to load {}", var1, var12);
            return null;
         }

         FrameSize var14;
         if (var3.isPresent()) {
            var14 = ((AnimationMetadataSection)var3.get()).calculateFrameSize(var13.getWidth(), var13.getHeight());
            if (!Mth.isMultipleOf(var13.getWidth(), var14.width()) || !Mth.isMultipleOf(var13.getHeight(), var14.height())) {
               LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{var1, var13.getWidth(), var13.getHeight(), var14.width(), var14.height()});
               var13.close();
               return null;
            }
         } else {
            var14 = new FrameSize(var13.getWidth(), var13.getHeight());
         }

         return new SpriteContents(var1, var14, var13, var3, var4);
      };
   }

   @Nullable
   SpriteContents loadSprite(ResourceLocation var1, Resource var2);
}
