package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeGlyphProvider implements GlyphProvider {
   @Nullable
   private ByteBuffer fontMemory;
   @Nullable
   private STBTTFontinfo font;
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
      var7.codePoints().forEach(this.skip::add);
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
   @Override
   public GlyphInfo getGlyph(int var1) {
      STBTTFontinfo var2 = this.validateFontOpen();
      if (this.skip.contains(var1)) {
         return null;
      } else {
         MemoryStack var3 = MemoryStack.stackPush();

         Object var17;
         label61: {
            GlyphInfo var18;
            label62: {
               try {
                  int var4 = STBTruetype.stbtt_FindGlyphIndex(var2, var1);
                  if (var4 == 0) {
                     var17 = null;
                     break label61;
                  }

                  IntBuffer var5 = var3.mallocInt(1);
                  IntBuffer var6 = var3.mallocInt(1);
                  IntBuffer var7 = var3.mallocInt(1);
                  IntBuffer var8 = var3.mallocInt(1);
                  IntBuffer var9 = var3.mallocInt(1);
                  IntBuffer var10 = var3.mallocInt(1);
                  STBTruetype.stbtt_GetGlyphHMetrics(var2, var4, var9, var10);
                  STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(var2, var4, this.pointScale, this.pointScale, this.shiftX, this.shiftY, var5, var6, var7, var8);
                  float var11 = (float)var9.get(0) * this.pointScale;
                  int var12 = var7.get(0) - var5.get(0);
                  int var13 = var8.get(0) - var6.get(0);
                  if (var12 > 0 && var13 > 0) {
                     var18 = new TrueTypeGlyphProvider.Glyph(
                        var5.get(0), var7.get(0), -var6.get(0), -var8.get(0), var11, (float)var10.get(0) * this.pointScale, var4
                     );
                     break label62;
                  }

                  var18 = () -> var11 / this.oversample;
               } catch (Throwable var16) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (var3 != null) {
                  var3.close();
               }

               return var18;
            }

            if (var3 != null) {
               var3.close();
            }

            return var18;
         }

         if (var3 != null) {
            var3.close();
         }

         return (GlyphInfo)var17;
      }
   }

   STBTTFontinfo validateFontOpen() {
      if (this.fontMemory != null && this.font != null) {
         return this.font;
      } else {
         throw new IllegalArgumentException("Provider already closed");
      }
   }

   @Override
   public void close() {
      if (this.font != null) {
         this.font.free();
         this.font = null;
      }

      MemoryUtil.memFree(this.fontMemory);
      this.fontMemory = null;
   }

   @Override
   public IntSet getSupportedGlyphs() {
      return IntStream.range(0, 65535).filter(var1 -> !this.skip.contains(var1)).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
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

      @Override
      public float getAdvance() {
         return this.advance;
      }

      @Override
      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(
            new SheetGlyphInfo() {
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
               public float getBearingX() {
                  return Glyph.this.bearingX;
               }
   
               @Override
               public float getBearingY() {
                  return Glyph.this.bearingY;
               }
   
               @Override
               public void upload(int var1, int var2) {
                  STBTTFontinfo var3 = TrueTypeGlyphProvider.this.validateFontOpen();
                  NativeImage var4 = new NativeImage(NativeImage.Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false);
                  var4.copyFromFont(
                     var3,
                     Glyph.this.index,
                     Glyph.this.width,
                     Glyph.this.height,
                     TrueTypeGlyphProvider.this.pointScale,
                     TrueTypeGlyphProvider.this.pointScale,
                     TrueTypeGlyphProvider.this.shiftX,
                     TrueTypeGlyphProvider.this.shiftY,
                     0,
                     0
                  );
                  var4.upload(0, var1, var2, 0, 0, Glyph.this.width, Glyph.this.height, false, true);
               }
   
               @Override
               public boolean isColored() {
                  return false;
               }
            }
         );
      }
   }
}
