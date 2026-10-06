package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class CubemapTextureProvider implements TextureProvider<TextureContents> {
   private static final String[] SUFFIXES = new String[]{"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};

   public CubemapTextureProvider() {
      super();
   }

   public TextureContents prepareState(final ResourceManager resourceManager, final Identifier identifier) throws IOException {
      TextureContents var15;
      try (TextureContents first = TextureContents.load(resourceManager, identifier.withSuffix(SUFFIXES[0]))) {
         int width = first.image().getWidth();
         int height = first.image().getHeight();
         NativeImage stackedImage = new NativeImage(width, height * 6, false);
         first.image().copyRect(stackedImage, 0, 0, 0, 0, width, height, false, true);

         for(int i = 1; i < 6; ++i) {
            try (TextureContents part = TextureContents.load(resourceManager, identifier.withSuffix(SUFFIXES[i]))) {
               if (part.image().getWidth() != width || part.image().getHeight() != height) {
                  String var10002 = String.valueOf(identifier);
                  throw new IOException("Image dimensions of cubemap '" + var10002 + "' sides do not match: part 0 is " + width + "x" + height + ", but part " + i + " is " + part.image().getWidth() + "x" + part.image().getHeight());
               }

               part.image().copyRect(stackedImage, 0, 0, 0, i * height, width, height, false, true);
            }
         }

         var15 = new TextureContents(stackedImage, new TextureMetadataSection(true, false, MipmapStrategy.MEAN, 0.0F));
      }

      return var15;
   }

   public TextureContents missingState() {
      NativeImage image = MissingTextureAtlasSprite.generateMissingImage(16, 96);
      return new TextureContents(image, (TextureMetadataSection)null);
   }

   public TextureResources createTexture(final TextureContents state, final Identifier identifier) {
      GpuDevice device = RenderSystem.getDevice();
      int width = state.image().getWidth();
      int height = state.image().getHeight() / 6;
      Objects.requireNonNull(identifier);
      GpuTexture texture = device.createTexture(identifier::toString, 21, GpuFormat.RGBA8_UNORM, width, height, 6, 1);
      GpuTextureView textureView = device.createTextureView(texture);
      GpuBufferSlice stagingBuffer = device.createCommandEncoder().transientMemory().uploadStaging(state.image().getPixelBytes(), 1L, 16);

      for(int i = 0; i < 6; ++i) {
         device.createCommandEncoder().copyBufferToTexture(stagingBuffer, 0, height * i, state.image().getWidth(), state.image().getHeight(), texture, 0, 0, width, height, 0, i);
      }

      return new TextureResources(texture, textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR));
   }
}
