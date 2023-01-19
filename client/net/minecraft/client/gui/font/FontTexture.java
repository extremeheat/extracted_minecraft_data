package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class FontTexture extends AbstractTexture {
   private static final int SIZE = 256;
   private final ResourceLocation name;
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final RenderType polygonOffsetType;
   private final boolean colored;
   private final FontTexture.Node root;

   public FontTexture(ResourceLocation var1, boolean var2) {
      super();
      this.name = var1;
      this.colored = var2;
      this.root = new FontTexture.Node(0, 0, 256, 256);
      TextureUtil.prepareImage(var2 ? NativeImage.InternalGlFormat.RGBA : NativeImage.InternalGlFormat.RED, this.getId(), 256, 256);
      this.normalType = var2 ? RenderType.text(var1) : RenderType.textIntensity(var1);
      this.seeThroughType = var2 ? RenderType.textSeeThrough(var1) : RenderType.textIntensitySeeThrough(var1);
      this.polygonOffsetType = var2 ? RenderType.textPolygonOffset(var1) : RenderType.textIntensityPolygonOffset(var1);
   }

   @Override
   public void load(ResourceManager var1) {
   }

   @Override
   public void close() {
      this.releaseId();
   }

   @Nullable
   public BakedGlyph add(SheetGlyphInfo var1) {
      if (var1.isColored() != this.colored) {
         return null;
      } else {
         FontTexture.Node var2 = this.root.insert(var1);
         if (var2 != null) {
            this.bind();
            var1.upload(var2.x, var2.y);
            float var3 = 256.0F;
            float var4 = 256.0F;
            float var5 = 0.01F;
            return new BakedGlyph(
               this.normalType,
               this.seeThroughType,
               this.polygonOffsetType,
               ((float)var2.x + 0.01F) / 256.0F,
               ((float)var2.x - 0.01F + (float)var1.getPixelWidth()) / 256.0F,
               ((float)var2.y + 0.01F) / 256.0F,
               ((float)var2.y - 0.01F + (float)var1.getPixelHeight()) / 256.0F,
               var1.getLeft(),
               var1.getRight(),
               var1.getUp(),
               var1.getDown()
            );
         } else {
            return null;
         }
      }
   }

   public ResourceLocation getName() {
      return this.name;
   }

   static class Node {
      final int x;
      final int y;
      private final int width;
      private final int height;
      @Nullable
      private FontTexture.Node left;
      @Nullable
      private FontTexture.Node right;
      private boolean occupied;

      Node(int var1, int var2, int var3, int var4) {
         super();
         this.x = var1;
         this.y = var2;
         this.width = var3;
         this.height = var4;
      }

      @Nullable
      FontTexture.Node insert(SheetGlyphInfo var1) {
         if (this.left != null && this.right != null) {
            FontTexture.Node var6 = this.left.insert(var1);
            if (var6 == null) {
               var6 = this.right.insert(var1);
            }

            return var6;
         } else if (this.occupied) {
            return null;
         } else {
            int var2 = var1.getPixelWidth();
            int var3 = var1.getPixelHeight();
            if (var2 > this.width || var3 > this.height) {
               return null;
            } else if (var2 == this.width && var3 == this.height) {
               this.occupied = true;
               return this;
            } else {
               int var4 = this.width - var2;
               int var5 = this.height - var3;
               if (var4 > var5) {
                  this.left = new FontTexture.Node(this.x, this.y, var2, this.height);
                  this.right = new FontTexture.Node(this.x + var2 + 1, this.y, this.width - var2 - 1, this.height);
               } else {
                  this.left = new FontTexture.Node(this.x, this.y, this.width, var3);
                  this.right = new FontTexture.Node(this.x, this.y + var3 + 1, this.width, this.height - var3 - 1);
               }

               return this.left.insert(var1);
            }
         }
      }
   }
}