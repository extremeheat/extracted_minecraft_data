package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;

public class TextureProviderMipped2d implements TextureProvider<TextureContents> {
   private final int maxMipLevel;

   public TextureProviderMipped2d(final int maxMipLevel) {
      super();
      this.maxMipLevel = maxMipLevel;
   }

   public TextureContents prepareState(final ResourceManager resourceManager, final Identifier identifier) throws IOException {
      return TextureContents.load(resourceManager, identifier);
   }

   public TextureContents missingState() {
      return TextureContents.createMissing();
   }

   public TextureResources createTexture(final TextureContents state, final Identifier identifier) {
      GpuDevice device = RenderSystem.getDevice();
      int mipLevel = this.clampMipLevel(state.image().getWidth(), state.image().getHeight());
      Transparency transparency = state.image().computeTransparency();
      NativeImage[] mips = MipmapGenerator.generateMipLevels(identifier, new NativeImage[]{state.image()}, mipLevel, MipmapStrategy.AUTO, 0.0F, transparency);
      Objects.requireNonNull(identifier);
      GpuTexture texture = device.createTexture(identifier::toString, 5, GpuFormat.RGBA8_UNORM, state.image().getWidth(), state.image().getHeight(), 1, mips.length);
      GpuTextureView textureView = device.createTextureView(texture);

      for(int level = 0; level < mips.length; ++level) {
         mips[level].writeToGpuTexture(device.createCommandEncoder(), texture, level, 0, 0, 0);
      }

      for(int level = 1; level < mips.length; ++level) {
         mips[level].close();
      }

      AddressMode addressMode = state.clamp() ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT;
      FilterMode minFilter = FilterMode.LINEAR;
      FilterMode magFilter = state.blur() ? FilterMode.LINEAR : FilterMode.NEAREST;
      GpuSampler sampler = RenderSystem.getSamplerCache().getSampler(addressMode, addressMode, minFilter, magFilter, true);
      return new TextureResources(texture, textureView, sampler);
   }

   private int clampMipLevel(final int width, final int height) {
      int lowestTextureBit = Math.min(Integer.lowestOneBit(width), Integer.lowestOneBit(height));
      int minSize = Math.min(Math.min(width, height), lowestTextureBit);
      return Math.min(this.maxMipLevel, Mth.log2(minSize));
   }
}
