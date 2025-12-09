package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Matrix;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

public class TrueTypeGlyphProvider implements GlyphProvider {
   private @Nullable ByteBuffer fontMemory;
   private @Nullable FT_Face face;
   final float oversample;
   private final CodepointMap<GlyphEntry> glyphs = new CodepointMap<GlyphEntry>((var0) -> new GlyphEntry[var0], (var0) -> new GlyphEntry[var0][]);

   public TrueTypeGlyphProvider(ByteBuffer var1, FT_Face var2, float var3, float var4, float var5, float var6, String var7) {
      super();
      this.fontMemory = var1;
      this.face = var2;
      this.oversample = var4;
      IntArraySet var8 = new IntArraySet();
      IntStream var10000 = var7.codePoints();
      Objects.requireNonNull(var8);
      var10000.forEach(var8::add);
      int var9 = Math.round(var3 * var4);
      FreeType.FT_Set_Pixel_Sizes(var2, var9, var9);
      float var10 = var5 * var4;
      float var11 = -var6 * var4;
      MemoryStack var12 = MemoryStack.stackPush();

      try {
         FT_Vector var13 = FreeTypeUtil.setVector(FT_Vector.malloc(var12), var10, var11);
         FreeType.FT_Set_Transform(var2, (FT_Matrix)null, var13);
         IntBuffer var14 = var12.mallocInt(1);
         int var15 = (int)FreeType.FT_Get_First_Char(var2, var14);

         while(true) {
            int var16 = var14.get(0);
            if (var16 == 0) {
               break;
            }

            if (!var8.contains(var15)) {
               this.glyphs.put(var15, new GlyphEntry(var16));
            }

            var15 = (int)FreeType.FT_Get_Next_Char(var2, (long)var15, var14);
         }
      } catch (Throwable var18) {
         if (var12 != null) {
            try {
               var12.close();
            } catch (Throwable var17) {
               var18.addSuppressed(var17);
            }
         }

         throw var18;
      }

      if (var12 != null) {
         var12.close();
      }

   }

   public @Nullable UnbakedGlyph getGlyph(int var1) {
      GlyphEntry var2 = this.glyphs.get(var1);
      return var2 != null ? this.getOrLoadGlyphInfo(var1, var2) : null;
   }

   private UnbakedGlyph getOrLoadGlyphInfo(int var1, GlyphEntry var2) {
      UnbakedGlyph var3 = var2.glyph;
      if (var3 == null) {
         FT_Face var4 = this.validateFontOpen();
         synchronized(var4) {
            var3 = var2.glyph;
            if (var3 == null) {
               var3 = this.loadGlyph(var1, var4, var2.index);
               var2.glyph = var3;
            }
         }
      }

      return var3;
   }

   private UnbakedGlyph loadGlyph(int var1, FT_Face var2, int var3) {
      int var4 = FreeType.FT_Load_Glyph(var2, var3, 4194312);
      if (var4 != 0) {
         FreeTypeUtil.assertError(var4, String.format(Locale.ROOT, "Loading glyph U+%06X", var1));
      }

      FT_GlyphSlot var5 = var2.glyph();
      if (var5 == null) {
         throw new NullPointerException(String.format(Locale.ROOT, "Glyph U+%06X not initialized", var1));
      } else {
         float var6 = FreeTypeUtil.x(var5.advance());
         FT_Bitmap var7 = var5.bitmap();
         int var8 = var5.bitmap_left();
         int var9 = var5.bitmap_top();
         int var10 = var7.width();
         int var11 = var7.rows();
         return (UnbakedGlyph)(var10 > 0 && var11 > 0 ? new Glyph((float)var8, (float)var9, var10, var11, var6, var3) : new EmptyGlyph(var6 / this.oversample));
      }
   }

   FT_Face validateFontOpen() {
      if (this.fontMemory != null && this.face != null) {
         return this.face;
      } else {
         throw new IllegalStateException("Provider already closed");
      }
   }

   public void close() {
      if (this.face != null) {
         synchronized(FreeTypeUtil.LIBRARY_LOCK) {
            FreeTypeUtil.checkError(FreeType.FT_Done_Face(this.face), "Deleting face");
         }

         this.face = null;
      }

      MemoryUtil.memFree(this.fontMemory);
      this.fontMemory = null;
   }

   public IntSet getSupportedGlyphs() {
      return this.glyphs.keySet();
   }

   static class GlyphEntry {
      final int index;
      volatile @Nullable UnbakedGlyph glyph;

      GlyphEntry(int var1) {
         super();
         this.index = var1;
      }
   }

   class Glyph implements UnbakedGlyph {
      final int width;
      final int height;
      final float bearingX;
      final float bearingY;
      private final GlyphInfo info;
      final int index;

      Glyph(final float var2, final float var3, final int var4, final int var5, final float var6, final int var7) {
         super();
         this.width = var4;
         this.height = var5;
         this.info = GlyphInfo.simple(var6 / TrueTypeGlyphProvider.this.oversample);
         this.bearingX = var2 / TrueTypeGlyphProvider.this.oversample;
         this.bearingY = var3 / TrueTypeGlyphProvider.this.oversample;
         this.index = var7;
      }

      public GlyphInfo info() {
         return this.info;
      }

      public BakedGlyph bake(UnbakedGlyph.Stitcher var1) {
         return var1.stitch(this.info, new GlyphBitmap() {
            public int getPixelWidth() {
               return Glyph.this.width;
            }

            public int getPixelHeight() {
               return Glyph.this.height;
            }

            public float getOversample() {
               return TrueTypeGlyphProvider.this.oversample;
            }

            public float getBearingLeft() {
               return Glyph.this.bearingX;
            }

            public float getBearingTop() {
               return Glyph.this.bearingY;
            }

            public void upload(int var1, int var2, GpuTexture var3) {
               FT_Face var4 = TrueTypeGlyphProvider.this.validateFontOpen();

               try (NativeImage var5 = new NativeImage(NativeImage.Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false)) {
                  if (var5.copyFromFont(var4, Glyph.this.index)) {
                     RenderSystem.getDevice().createCommandEncoder().writeToTexture(var3, var5, 0, 0, var1, var2, Glyph.this.width, Glyph.this.height, 0, 0);
                  }
               }

            }

            public boolean isColored() {
               return false;
            }
         });
      }
   }
}
