package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeGlyphProvider implements GlyphProvider {
   private final ByteBuffer fontMemory;
   final STBTTFontinfo font;
   final float oversample;
   private final IntSet skip = new IntArraySet();
   final float shiftX;
   final float shiftY;
   final float pointScale;
   final float ascent;

   public TrueTypeGlyphProvider(ByteBuffer var1, STBTTFontinfo var2, float var3, float var4, float var5, float var6, String var7) {
      super();
      this.fontMemory = var1;
      this.font = var2;
      this.oversample = var4;
      IntStream var10000 = var7.codePoints();
      IntSet var10001 = this.skip;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      this.shiftX = var5 * var4;
      this.shiftY = var6 * var4;
      this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(var2, var3 * var4);
      MemoryStack var8 = MemoryStack.stackPush();

      try {
         IntBuffer var9 = var8.mallocInt(1);
         IntBuffer var10 = var8.mallocInt(1);
         IntBuffer var11 = var8.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(var2, var9, var10, var11);
         this.ascent = (float)var9.get(0) * this.pointScale;
      } catch (Throwable var13) {
         if (var8 != null) {
            try {
               var8.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (var8 != null) {
         var8.close();
      }

   }

   @Nullable
   public GlyphInfo getGlyph(int var1) {
      if (this.skip.contains(var1)) {
         return null;
      } else {
         MemoryStack var2 = MemoryStack.stackPush();

         IntBuffer var4;
         label61: {
            Glyph var16;
            label62: {
               GlyphInfo.SpaceGlyphInfo var13;
               try {
                  int var3 = STBTruetype.stbtt_FindGlyphIndex(this.font, var1);
                  if (var3 == 0) {
                     var4 = null;
                     break label61;
                  }

                  var4 = var2.mallocInt(1);
                  IntBuffer var5 = var2.mallocInt(1);
                  IntBuffer var6 = var2.mallocInt(1);
                  IntBuffer var7 = var2.mallocInt(1);
                  IntBuffer var8 = var2.mallocInt(1);
                  IntBuffer var9 = var2.mallocInt(1);
                  STBTruetype.stbtt_GetGlyphHMetrics(this.font, var3, var8, var9);
                  STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.font, var3, this.pointScale, this.pointScale, this.shiftX, this.shiftY, var4, var5, var6, var7);
                  float var10 = (float)var8.get(0) * this.pointScale;
                  int var11 = var6.get(0) - var4.get(0);
                  int var12 = var7.get(0) - var5.get(0);
                  if (var11 > 0 && var12 > 0) {
                     var16 = new Glyph(var4.get(0), var6.get(0), -var5.get(0), -var7.get(0), var10, (float)var9.get(0) * this.pointScale, var3);
                     break label62;
                  }

                  var13 = () -> {
                     return var10 / this.oversample;
                  };
               } catch (Throwable var15) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                     }
                  }

                  throw var15;
               }

               if (var2 != null) {
                  var2.close();
               }

               return var13;
            }

            if (var2 != null) {
               var2.close();
            }

            return var16;
         }

         if (var2 != null) {
            var2.close();
         }

         return var4;
      }
   }

   public void close() {
      this.font.free();
      MemoryUtil.memFree(this.fontMemory);
   }

   public IntSet getSupportedGlyphs() {
      return (IntSet)IntStream.range(0, 65535).filter((var1) -> {
         return !this.skip.contains(var1);
      }).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
   }

   class Glyph implements GlyphInfo {
      final int width;
      final int height;
      final float bearingX;
      final float bearingY;
      private final float advance;
      final int index;

      Glyph(int var2, int var3, int var4, int var5, float var6, float var7, int var8) {
         super();
         this.width = var3 - var2;
         this.height = var4 - var5;
         this.advance = var6 / TrueTypeGlyphProvider.this.oversample;
         this.bearingX = (var7 + (float)var2 + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
         this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)var4 + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
         this.index = var8;
      }

      public float getAdvance() {
         return this.advance;
      }

      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            public int getPixelWidth() {
               return Glyph.this.width;
            }

            public int getPixelHeight() {
               return Glyph.this.height;
            }

            public float getOversample() {
               return TrueTypeGlyphProvider.this.oversample;
            }

            public float getBearingX() {
               return Glyph.this.bearingX;
            }

            public float getBearingY() {
               return Glyph.this.bearingY;
            }

            public void upload(int var1, int var2) {
               NativeImage var3 = new NativeImage(NativeImage.Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false);
               var3.copyFromFont(TrueTypeGlyphProvider.this.font, Glyph.this.index, Glyph.this.width, Glyph.this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
               var3.upload(0, var1, var2, 0, 0, Glyph.this.width, Glyph.this.height, false, true);
            }

            public boolean isColored() {
               return false;
            }
         });
      }
   }
}
