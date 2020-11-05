package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeGlyphProvider implements GlyphProvider {
   private final ByteBuffer fontMemory;
   private final STBTTFontinfo font;
   private final float oversample;
   private final IntSet skip = new IntArraySet();
   private final float shiftX;
   private final float shiftY;
   private final float pointScale;
   private final float ascent;

   public TrueTypeGlyphProvider(ByteBuffer var1, STBTTFontinfo var2, float var3, float var4, float var5, float var6, String var7) {
      super();
      this.fontMemory = var1;
      this.font = var2;
      this.oversample = var4;
      IntStream var10000 = var7.codePoints();
      IntSet var10001 = this.skip;
      var10000.forEach(var10001::add);
      this.shiftX = var5 * var4;
      this.shiftY = var6 * var4;
      this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(var2, var3 * var4);
      MemoryStack var8 = MemoryStack.stackPush();
      Throwable var9 = null;

      try {
         IntBuffer var10 = var8.mallocInt(1);
         IntBuffer var11 = var8.mallocInt(1);
         IntBuffer var12 = var8.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(var2, var10, var11, var12);
         this.ascent = (float)var10.get(0) * this.pointScale;
      } catch (Throwable var20) {
         var9 = var20;
         throw var20;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var19) {
                  var9.addSuppressed(var19);
               }
            } else {
               var8.close();
            }
         }

      }

   }

   @Nullable
   public TrueTypeGlyphProvider.Glyph getGlyph(int var1) {
      if (this.skip.contains(var1)) {
         return null;
      } else {
         MemoryStack var2 = MemoryStack.stackPush();
         Throwable var3 = null;

         Object var9;
         try {
            IntBuffer var4 = var2.mallocInt(1);
            IntBuffer var5 = var2.mallocInt(1);
            IntBuffer var6 = var2.mallocInt(1);
            IntBuffer var7 = var2.mallocInt(1);
            int var8 = STBTruetype.stbtt_FindGlyphIndex(this.font, var1);
            if (var8 != 0) {
               STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.font, var8, this.pointScale, this.pointScale, this.shiftX, this.shiftY, var4, var5, var6, var7);
               int var26 = var6.get(0) - var4.get(0);
               int var10 = var7.get(0) - var5.get(0);
               IntBuffer var11;
               if (var26 != 0 && var10 != 0) {
                  var11 = var2.mallocInt(1);
                  IntBuffer var12 = var2.mallocInt(1);
                  STBTruetype.stbtt_GetGlyphHMetrics(this.font, var8, var11, var12);
                  TrueTypeGlyphProvider.Glyph var13 = new TrueTypeGlyphProvider.Glyph(var4.get(0), var6.get(0), -var5.get(0), -var7.get(0), (float)var11.get(0) * this.pointScale, (float)var12.get(0) * this.pointScale, var8);
                  return var13;
               }

               var11 = null;
               return var11;
            }

            var9 = null;
         } catch (Throwable var24) {
            var3 = var24;
            throw var24;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var23) {
                     var3.addSuppressed(var23);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return (TrueTypeGlyphProvider.Glyph)var9;
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

   // $FF: synthetic method
   @Nullable
   public RawGlyph getGlyph(int var1) {
      return this.getGlyph(var1);
   }

   class Glyph implements RawGlyph {
      private final int width;
      private final int height;
      private final float bearingX;
      private final float bearingY;
      private final float advance;
      private final int index;

      private Glyph(int var2, int var3, int var4, int var5, float var6, float var7, int var8) {
         super();
         this.width = var3 - var2;
         this.height = var4 - var5;
         this.advance = var6 / TrueTypeGlyphProvider.this.oversample;
         this.bearingX = (var7 + (float)var2 + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
         this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)var4 + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
         this.index = var8;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getOversample() {
         return TrueTypeGlyphProvider.this.oversample;
      }

      public float getAdvance() {
         return this.advance;
      }

      public float getBearingX() {
         return this.bearingX;
      }

      public float getBearingY() {
         return this.bearingY;
      }

      public void upload(int var1, int var2) {
         NativeImage var3 = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false);
         var3.copyFromFont(TrueTypeGlyphProvider.this.font, this.index, this.width, this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
         var3.upload(0, var1, var2, 0, 0, this.width, this.height, false, true);
      }

      public boolean isColored() {
         return false;
      }

      // $FF: synthetic method
      Glyph(int var2, int var3, int var4, int var5, float var6, float var7, int var8, Object var9) {
         this(var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
