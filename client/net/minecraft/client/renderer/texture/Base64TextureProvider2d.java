package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.system.MemoryUtil;

public class Base64TextureProvider2d implements TextureProvider<TextureContents, TextureResources> {
   private final String encodedImage;

   public Base64TextureProvider2d(final String encodedImage) {
      super();
      this.encodedImage = encodedImage;
   }

   public TextureContents prepareState(final ResourceManager resourceManager, final Identifier identifier) throws IOException {
      byte[] bytes = Base64.getDecoder().decode(this.encodedImage);
      ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);

      TextureContents var6;
      try {
         NativeImage image = NativeImage.read(buffer.put(bytes).flip());
         var6 = new TextureContents(image, new TextureMetadataSection(false, true, MipmapStrategy.AUTO, 0.0F));
      } finally {
         MemoryUtil.memFree(buffer);
      }

      return var6;
   }

   public TextureContents missingState() {
      return TextureContents.createMissing();
   }

   public TextureResources createTexture(final TextureContents state, final Identifier identifier) {
      AddressMode addressMode = state.clamp() ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT;
      FilterMode minMag = state.blur() ? FilterMode.LINEAR : FilterMode.NEAREST;
      GpuSampler sampler = RenderSystem.getSamplerCache().getSampler(addressMode, addressMode, minMag, minMag, false);
      Objects.requireNonNull(identifier);
      return TextureResources.from2dImage(identifier::toString, state.image(), sampler);
   }

   public void dumpContents(final Identifier identifier, final Path outputDirectory, final TextureResources texture) {
      texture.dumpContents(identifier, outputDirectory);
   }

   public void releaseTexture(final TextureResources texture) {
      texture.close();
   }
}
