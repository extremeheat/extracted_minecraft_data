package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;

public class MipmappedTexture extends ReloadableTexture {
   private final int maxMipLevel;

   public MipmappedTexture(final Identifier location, final int maxMipLevel) {
      super(location);
      this.maxMipLevel = maxMipLevel;
   }

   public TextureContents loadContents(final ResourceManager resourceManager) throws IOException {
      return TextureContents.load(resourceManager, this.resourceId());
   }

   protected void setSampler(final TextureContents contents) {
      AddressMode addressMode = contents.clamp() ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT;
      FilterMode minFilter = FilterMode.LINEAR;
      FilterMode magFilter = contents.blur() ? FilterMode.LINEAR : FilterMode.NEAREST;
      this.sampler = RenderSystem.getSamplerCache().getSampler(addressMode, addressMode, minFilter, magFilter, true);
   }

   protected void doLoad(final NativeImage image) {
      GpuDevice device = RenderSystem.getDevice();
      this.close();
      int mipLevel = this.clampMipLevel(image.getWidth(), image.getHeight());
      Transparency transparency = image.computeTransparency();
      NativeImage[] mips = MipmapGenerator.generateMipLevels(this.resourceId(), new NativeImage[]{image}, mipLevel, MipmapStrategy.AUTO, 0.0F, transparency);
      Identifier var10002 = this.resourceId();
      Objects.requireNonNull(var10002);
      this.texture = device.createTexture(var10002::toString, 5, GpuFormat.RGBA8_UNORM, image.getWidth(), image.getHeight(), 1, mips.length);
      this.textureView = device.createTextureView(this.texture);

      for(int level = 0; level < mips.length; ++level) {
         device.createCommandEncoder().writeToTexture(this.texture, mips[level], level, 0, 0, 0);
      }

      for(int level = 1; level < mips.length; ++level) {
         mips[level].close();
      }

   }

   private int clampMipLevel(final int width, final int height) {
      int lowestTextureBit = Math.min(Integer.lowestOneBit(width), Integer.lowestOneBit(height));
      int minSize = Math.min(Math.min(width, height), lowestTextureBit);
      return Math.min(this.maxMipLevel, Mth.log2(minSize));
   }
}
