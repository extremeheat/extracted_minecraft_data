package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

public class TrueTypeGlyphProvider implements GlyphProvider {
   @Nullable
   private ByteBuffer fontMemory;
   @Nullable
   private FT_Face face;
   final float oversample;
   private final CodepointMap<TrueTypeGlyphProvider.GlyphEntry> glyphs = new CodepointMap<>(
      TrueTypeGlyphProvider.GlyphEntry[]::new, TrueTypeGlyphProvider.GlyphEntry[][]::new
   );

   public TrueTypeGlyphProvider(ByteBuffer var1, FT_Face var2, float var3, float var4, float var5, float var6, String var7) {
      super();
      this.fontMemory = var1;
      this.face = var2;
      this.oversample = var4;
      IntArraySet var8 = new IntArraySet();
      var7.codePoints().forEach(var8::add);
      int var9 = Math.round(var3 * var4);
      FreeType.FT_Set_Pixel_Sizes(var2, var9, var9);
      float var10 = var5 * var4;
      float var11 = -var6 * var4;
      MemoryStack var12 = MemoryStack.stackPush();

      try {
         FT_Vector var13 = FreeTypeUtil.setVector(FT_Vector.malloc(var12), var10, var11);
         FreeType.FT_Set_Transform(var2, null, var13);
         IntBuffer var14 = var12.mallocInt(1);
         int var15 = (int)FreeType.FT_Get_First_Char(var2, var14);

         while (true) {
            int var16 = var14.get(0);
            if (var16 == 0) {
               break;
            }

            if (!var8.contains(var15)) {
               this.glyphs.put(var15, new TrueTypeGlyphProvider.GlyphEntry(var16));
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

   @Nullable
   @Override
   public GlyphInfo getGlyph(int var1) {
      TrueTypeGlyphProvider.GlyphEntry var2 = this.glyphs.get(var1);
      return var2 != null ? this.getOrLoadGlyphInfo(var1, var2) : null;
   }

   private GlyphInfo getOrLoadGlyphInfo(int var1, TrueTypeGlyphProvider.GlyphEntry var2) {
      GlyphInfo var3 = var2.glyph;
      if (var3 == null) {
         FT_Face var4 = this.validateFontOpen();
         synchronized (var4) {
            var3 = var2.glyph;
            if (var3 == null) {
               var3 = this.loadGlyph(var1, var4, var2.index);
               var2.glyph = var3;
            }
         }
      }

      return var3;
   }

   private GlyphInfo loadGlyph(int var1, FT_Face var2, int var3) {
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
         return (GlyphInfo)(var10 > 0 && var11 > 0
            ? new TrueTypeGlyphProvider.Glyph((float)var8, (float)var9, var10, var11, var6, var3)
            : () -> var6 / this.oversample);
      }
   }

   FT_Face validateFontOpen() {
      if (this.fontMemory != null && this.face != null) {
         return this.face;
      } else {
         throw new IllegalStateException("Provider already closed");
      }
   }

   @Override
   public void close() {
      if (this.face != null) {
         synchronized (FreeTypeUtil.LIBRARY_LOCK) {
            FreeTypeUtil.checkError(FreeType.FT_Done_Face(this.face), "Deleting face");
         }

         this.face = null;
      }

      MemoryUtil.memFree(this.fontMemory);
      this.fontMemory = null;
   }

   @Override
   public IntSet getSupportedGlyphs() {
      return this.glyphs.keySet();
   }

   class Glyph implements GlyphInfo {
      final int width;
      final int height;
      final float bearingX;
      final float bearingY;
      private final float advance;
      final int index;

      Glyph(final float nullx, final float nullxx, final int nullxxx, final int nullxxxx, final float nullxxxxx, final int nullxxxxxx) {
         super();
         this.width = nullxxx;
         this.height = nullxxxx;
         this.advance = nullxxxxx / TrueTypeGlyphProvider.this.oversample;
         this.bearingX = nullx / TrueTypeGlyphProvider.this.oversample;
         this.bearingY = nullxx / TrueTypeGlyphProvider.this.oversample;
         this.index = nullxxxxxx;
      }

      @Override
      public float getAdvance() {
         return this.advance;
      }

      @Override
      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            @Override
            public int getPixelWidth() {
               return Glyph.this.width;
            }

            @Override
            public int getPixelHeight() {
               return Glyph.this.height;
            }

            @Override
            public float getOversample() {
               return TrueTypeGlyphProvider.this.oversample;
            }

            @Override
            public float getBearingLeft() {
               return Glyph.this.bearingX;
            }

            @Override
            public float getBearingTop() {
               return Glyph.this.bearingY;
            }

            @Override
            public void upload(int var1, int var2) {
               FT_Face var3 = TrueTypeGlyphProvider.this.validateFontOpen();
               NativeImage var4 = new NativeImage(NativeImage.Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false);
               if (var4.copyFromFont(var3, Glyph.this.index)) {
                  var4.upload(0, var1, var2, 0, 0, Glyph.this.width, Glyph.this.height, false, true);
               } else {
                  var4.close();
               }
            }

            @Override
            public boolean isColored() {
               return false;
            }
         });
      }
   }

   static class GlyphEntry {
      final int index;
      @Nullable
      volatile GlyphInfo glyph;

      GlyphEntry(int var1) {
         super();
         this.index = var1;
      }
   }
}
