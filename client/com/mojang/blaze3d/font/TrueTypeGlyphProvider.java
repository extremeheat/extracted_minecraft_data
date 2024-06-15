package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
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
   private final IntSet skip = new IntArraySet();

   public TrueTypeGlyphProvider(ByteBuffer var1, FT_Face var2, float var3, float var4, float var5, float var6, String var7) {
      super();
      this.fontMemory = var1;
      this.face = var2;
      this.oversample = var4;
      var7.codePoints().forEach(this.skip::add);
      int var8 = Math.round(var3 * var4);
      FreeType.FT_Set_Pixel_Sizes(var2, var8, var8);
      float var9 = var5 * var4;
      float var10 = -var6 * var4;
      MemoryStack var11 = MemoryStack.stackPush();

      try {
         FT_Vector var12 = FreeTypeUtil.setVector(FT_Vector.malloc(var11), var9, var10);
         FreeType.FT_Set_Transform(var2, null, var12);
      } catch (Throwable var15) {
         if (var11 != null) {
            try {
               var11.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (var11 != null) {
         var11.close();
      }
   }

   @Nullable
   @Override
   public GlyphInfo getGlyph(int var1) {
      FT_Face var2 = this.validateFontOpen();
      if (this.skip.contains(var1)) {
         return null;
      } else {
         int var3 = FreeType.FT_Get_Char_Index(var2, (long)var1);
         if (var3 == 0) {
            return null;
         } else {
            FreeTypeUtil.assertError(FreeType.FT_Load_Glyph(var2, var3, 4194312), "Loading glyph");
            FT_GlyphSlot var4 = Objects.requireNonNull(var2.glyph(), "Glyph not initialized");
            float var5 = FreeTypeUtil.x(var4.advance());
            FT_Bitmap var6 = var4.bitmap();
            int var7 = var4.bitmap_left();
            int var8 = var4.bitmap_top();
            int var9 = var6.width();
            int var10 = var6.rows();
            return (GlyphInfo)(var9 > 0 && var10 > 0
               ? new TrueTypeGlyphProvider.Glyph((float)var7, (float)var8, var9, var10, var5, var3)
               : () -> var5 / this.oversample);
         }
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
      FT_Face var1 = this.validateFontOpen();
      IntOpenHashSet var2 = new IntOpenHashSet();
      MemoryStack var3 = MemoryStack.stackPush();

      try {
         IntBuffer var4 = var3.mallocInt(1);

         for (long var5 = FreeType.FT_Get_First_Char(var1, var4); var4.get(0) != 0; var5 = FreeType.FT_Get_Next_Char(var1, var5, var4)) {
            var2.add((int)var5);
         }
      } catch (Throwable var8) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var3 != null) {
         var3.close();
      }

      var2.removeAll(this.skip);
      return var2;
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
}
