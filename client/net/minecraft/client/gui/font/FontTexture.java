package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class FontTexture extends AbstractTexture implements Dumpable {
   private static final int SIZE = 256;
   private final GlyphRenderTypes renderTypes;
   private final boolean colored;
   private final Node root;

   public FontTexture(GlyphRenderTypes var1, boolean var2) {
      super();
      this.colored = var2;
      this.root = new Node(0, 0, 256, 256);
      TextureUtil.prepareImage(var2 ? NativeImage.InternalGlFormat.RGBA : NativeImage.InternalGlFormat.RED, this.getId(), 256, 256);
      this.renderTypes = var1;
   }

   public void load(ResourceManager var1) {
   }

   public void close() {
      this.releaseId();
   }

   @Nullable
   public BakedGlyph add(SheetGlyphInfo var1) {
      if (var1.isColored() != this.colored) {
         return null;
      } else {
         Node var2 = this.root.insert(var1);
         if (var2 != null) {
            this.bind();
            var1.upload(var2.x, var2.y);
            float var3 = 256.0F;
            float var4 = 256.0F;
            float var5 = 0.01F;
            return new BakedGlyph(this.renderTypes, ((float)var2.x + 0.01F) / 256.0F, ((float)var2.x - 0.01F + (float)var1.getPixelWidth()) / 256.0F, ((float)var2.y + 0.01F) / 256.0F, ((float)var2.y - 0.01F + (float)var1.getPixelHeight()) / 256.0F, var1.getLeft(), var1.getRight(), var1.getTop(), var1.getBottom());
         } else {
            return null;
         }
      }
   }

   public void dumpContents(ResourceLocation var1, Path var2) {
      String var3 = var1.toDebugFileName();
      TextureUtil.writeAsPNG(var2, var3, this.getId(), 0, 256, 256, (var0) -> {
         return (var0 & -16777216) == 0 ? -16777216 : var0;
      });
   }

   static class Node {
      final int x;
      final int y;
      private final int width;
      private final int height;
      @Nullable
      private Node left;
      @Nullable
      private Node right;
      private boolean occupied;

      Node(int var1, int var2, int var3, int var4) {
         super();
         this.x = var1;
         this.y = var2;
         this.width = var3;
         this.height = var4;
      }

      @Nullable
      Node insert(SheetGlyphInfo var1) {
         if (this.left != null && this.right != null) {
            Node var6 = this.left.insert(var1);
            if (var6 == null) {
               var6 = this.right.insert(var1);
            }

            return var6;
         } else if (this.occupied) {
            return null;
         } else {
            int var2 = var1.getPixelWidth();
            int var3 = var1.getPixelHeight();
            if (var2 <= this.width && var3 <= this.height) {
               if (var2 == this.width && var3 == this.height) {
                  this.occupied = true;
                  return this;
               } else {
                  int var4 = this.width - var2;
                  int var5 = this.height - var3;
                  if (var4 > var5) {
                     this.left = new Node(this.x, this.y, var2, this.height);
                     this.right = new Node(this.x + var2 + 1, this.y, this.width - var2 - 1, this.height);
                  } else {
                     this.left = new Node(this.x, this.y, this.width, var3);
                     this.right = new Node(this.x, this.y + var3 + 1, this.width, this.height - var3 - 1);
                  }

                  return this.left.insert(var1);
               }
            } else {
               return null;
            }
         }
      }
   }
}
