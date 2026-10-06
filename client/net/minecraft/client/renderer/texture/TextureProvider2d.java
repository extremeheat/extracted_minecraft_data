package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class TextureProvider2d implements TextureProvider<TextureContents> {
   public TextureProvider2d() {
      super();
   }

   public TextureContents prepareState(final ResourceManager resourceManager, final Identifier identifier) throws IOException {
      return TextureContents.load(resourceManager, identifier);
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
}
