package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

public class TrueTypeGlyphProvider implements GlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final STBTTFontinfo font;
   private final float oversample;
   private final CharSet skip = new CharArraySet();
   private final float shiftX;
   private final float shiftY;
   private final float pointScale;
   private final float ascent;

   public TrueTypeGlyphProvider(STBTTFontinfo var1, float var2, float var3, float var4, float var5, String var6) {
      this.font = var1;
      this.oversample = var3;
      var6.chars().forEach((var1x) -> {
         this.skip.add((char)(var1x & '\uffff'));
      });
      this.shiftX = var4 * var3;
      this.shiftY = var5 * var3;
      this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(var1, var2 * var3);
      MemoryStack var7 = MemoryStack.stackPush();
      Throwable var8 = null;

      try {
         IntBuffer var9 = var7.mallocInt(1);
         IntBuffer var10 = var7.mallocInt(1);
         IntBuffer var11 = var7.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(var1, var9, var10, var11);
         this.ascent = (float)var9.get(0) * this.pointScale;
      } catch (Throwable var19) {
         var8 = var19;
         throw var19;
      } finally {
         if (var7 != null) {
            if (var8 != null) {
               try {
                  var7.close();
               } catch (Throwable var18) {
                  var8.addSuppressed(var18);
               }
            } else {
               var7.close();
            }
         }

      }

   }

   @Nullable
   public TrueTypeGlyphProvider.Glyph getGlyph(char var1) {
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

   public static STBTTFontinfo getStbttFontinfo(ByteBuffer var0) throws IOException {
      STBTTFontinfo var1 = STBTTFontinfo.create();
      if (!STBTruetype.stbtt_InitFont(var1, var0)) {
         throw new IOException("Invalid ttf");
      } else {
         return var1;
      }
   }

   // $FF: synthetic method
   @Nullable
   public RawGlyph getGlyph(char var1) {
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
