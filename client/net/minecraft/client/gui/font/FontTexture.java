package net.minecraft.client.gui.font;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.font.GlyphBitmap;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class FontTexture extends AbstractTexture implements Dumpable {
   private static final int SIZE = 256;
   private final GlyphRenderTypes renderTypes;
   private final boolean colored;
   private final Node root;

   public FontTexture(final Supplier<String> label, final GlyphRenderTypes renderTypes, final boolean colored) {
      super();
      this.colored = colored;
      this.root = new Node(0, 0, 256, 256);
      GpuDevice device = RenderSystem.getDevice();
      this.texture = device.createTexture(label, 7, colored ? GpuFormat.RGBA8_UNORM : GpuFormat.R8_UNORM, 256, 256, 1, 1);
      this.sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
      this.textureView = device.createTextureView(this.texture);
      this.renderTypes = renderTypes;
   }

   public @Nullable BakedSheetGlyph add(final GlyphInfo info, final GlyphBitmap glyph) {
      if (glyph.isColored() != this.colored) {
         return null;
      } else {
         Node node = this.root.insert(glyph);
         if (node != null) {
            glyph.upload(node.x, node.y, this.getTexture());
            float width = 256.0F;
            float height = 256.0F;
            float nudge = 0.01F;
            return new BakedSheetGlyph(info, this.renderTypes, this.getTextureView(), ((float)node.x + 0.01F) / 256.0F, ((float)node.x - 0.01F + (float)glyph.getPixelWidth()) / 256.0F, ((float)node.y + 0.01F) / 256.0F, ((float)node.y - 0.01F + (float)glyph.getPixelHeight()) / 256.0F, glyph.getLeft(), glyph.getRight(), glyph.getTop(), glyph.getBottom());
         } else {
            return null;
         }
      }
   }

   public void dumpContents(final Identifier selfId, final Path dir) {
      if (this.texture != null) {
         String outputId = selfId.toDebugFileName();
         TextureUtil.writeAsPNG(dir, outputId, this.texture, 0, (argb) -> (argb & -16777216) == 0 ? -16777216 : argb);
      }
   }

   private static class Node {
      private final int x;
      private final int y;
      private final int width;
      private final int height;
      private @Nullable Node left;
      private @Nullable Node right;
      private boolean occupied;

      private Node(final int x, final int y, final int width, final int height) {
         super();
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }

      @Nullable Node insert(final GlyphBitmap glyph) {
         if (this.left != null && this.right != null) {
            Node newNode = this.left.insert(glyph);
            if (newNode == null) {
               newNode = this.right.insert(glyph);
            }

            return newNode;
         } else if (this.occupied) {
            return null;
         } else {
            int glyphWidth = glyph.getPixelWidth();
            int glyphHeight = glyph.getPixelHeight();
            if (glyphWidth <= this.width && glyphHeight <= this.height) {
               if (glyphWidth == this.width && glyphHeight == this.height) {
                  this.occupied = true;
                  return this;
               } else {
                  int deltaWidth = this.width - glyphWidth;
                  int deltaHeight = this.height - glyphHeight;
                  if (deltaWidth > deltaHeight) {
                     this.left = new Node(this.x, this.y, glyphWidth, this.height);
                     this.right = new Node(this.x + glyphWidth + 1, this.y, this.width - glyphWidth - 1, this.height);
                  } else {
                     this.left = new Node(this.x, this.y, this.width, glyphHeight);
                     this.right = new Node(this.x, this.y + glyphHeight + 1, this.width, this.height - glyphHeight - 1);
                  }

                  return this.left.insert(glyph);
               }
            } else {
               return null;
            }
         }
      }
   }
}
