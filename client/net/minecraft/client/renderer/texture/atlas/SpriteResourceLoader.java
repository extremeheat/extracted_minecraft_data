package net.minecraft.client.renderer.texture.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@FunctionalInterface
public interface SpriteResourceLoader {
   Logger LOGGER = LogUtils.getLogger();

   static SpriteResourceLoader create(Set<MetadataSectionType<?>> var0) {
      return (var1, var2) -> {
         Optional var3;
         Optional var4;
         List var5;
         try {
            ResourceMetadata var6 = var2.metadata();
            var3 = var6.getSection(AnimationMetadataSection.TYPE);
            var4 = var6.getSection(TextureMetadataSection.TYPE);
            var5 = var6.getTypedSections(var0);
         } catch (Exception var11) {
            LOGGER.error("Unable to parse metadata from {}", var1, var11);
            return null;
         }

         NativeImage var14;
         try {
            InputStream var7 = var2.open();

            try {
               var14 = NativeImage.read(var7);
            } catch (Throwable var12) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var10) {
                     var12.addSuppressed(var10);
                  }
               }

               throw var12;
            }

            if (var7 != null) {
               var7.close();
            }
         } catch (IOException var13) {
            LOGGER.error("Using missing texture, unable to load {}", var1, var13);
            return null;
         }

         FrameSize var15;
         if (var3.isPresent()) {
            var15 = ((AnimationMetadataSection)var3.get()).calculateFrameSize(var14.getWidth(), var14.getHeight());
            if (!Mth.isMultipleOf(var14.getWidth(), var15.width()) || !Mth.isMultipleOf(var14.getHeight(), var15.height())) {
               LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{var1, var14.getWidth(), var14.getHeight(), var15.width(), var15.height()});
               var14.close();
               return null;
            }
         } else {
            var15 = new FrameSize(var14.getWidth(), var14.getHeight());
         }

         MipmapStrategy var8 = (MipmapStrategy)var4.map(TextureMetadataSection::mipmapStrategy).orElse(MipmapStrategy.AUTO);
         return new SpriteContents(var1, var15, var14, var3, var5, var8);
      };
   }

   @Nullable SpriteContents loadSprite(Identifier var1, Resource var2);
}
