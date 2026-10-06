package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphBitmap;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.Objects;
import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import net.minecraft.client.renderer.texture.DynamicAtlasTree;
import net.minecraft.client.renderer.texture.DynamicAtlasTreeSlot;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureResources;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public class FontTexture implements AutoCloseable {
   private static final int SIZE = 256;
   private static final int SPACING = 1;
   private final GlyphRenderTypes renderTypes;
   private final boolean colored;
   private final DynamicAtlasTree tree = new DynamicAtlasTree(0, 0, 256, 256);
   private final TextureResources texture;
   private final TextureManager textureManager;
   private final Identifier textureId;

   public FontTexture(final TextureManager textureManager, final Identifier textureId, final GlyphRenderTypes renderTypes, final boolean colored) {
      super();
      this.textureManager = textureManager;
      this.textureId = textureId;
      this.colored = colored;
      GpuDevice device = RenderSystem.getDevice();
      Objects.requireNonNull(textureId);
      GpuTexture texture = device.createTexture(textureId::toString, 7, colored ? GpuFormat.RGBA8_UNORM : GpuFormat.R8_UNORM, 256, 256, 1, 1);
      GpuSampler sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
      GpuTextureView textureView = device.createTextureView(texture);
      this.texture = new TextureResources(texture, textureView, sampler, (argb) -> ARGB.alpha(argb) == 0 ? -16777216 : argb);
      this.renderTypes = renderTypes;
      textureManager.register(textureId, this.texture);
   }

   public void close() {
      this.textureManager.release(this.textureId);
   }

   public @Nullable BakedSheetGlyph add(final GlyphInfo info, final GlyphBitmap glyph) {
      if (glyph.isColored() != this.colored) {
         return null;
      } else {
         DynamicAtlasTreeSlot slot = this.tree.insert(glyph.getPixelWidth(), glyph.getPixelHeight(), 1);
         if (slot != null) {
            glyph.upload(slot.x(), slot.y(), this.texture.texture());
            float width = 256.0F;
            float height = 256.0F;
            float nudge = 0.01F;
            return new BakedSheetGlyph(info, this.renderTypes, this.texture.textureView(), ((float)slot.x() + 0.01F) / 256.0F, ((float)slot.x() - 0.01F + (float)glyph.getPixelWidth()) / 256.0F, ((float)slot.y() + 0.01F) / 256.0F, ((float)slot.y() - 0.01F + (float)glyph.getPixelHeight()) / 256.0F, glyph.getLeft(), glyph.getRight(), glyph.getTop(), glyph.getBottom());
         } else {
            return null;
         }
      }
   }
}
