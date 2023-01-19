package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class BitmapProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private final NativeImage image;
   private final Int2ObjectMap<BitmapProvider.Glyph> glyphs;

   BitmapProvider(NativeImage var1, Int2ObjectMap<BitmapProvider.Glyph> var2) {
      super();
      this.image = var1;
      this.glyphs = var2;
   }

   @Override
   public void close() {
      this.image.close();
   }

   @Nullable
   @Override
   public GlyphInfo getGlyph(int var1) {
      return (GlyphInfo)this.glyphs.get(var1);
   }

   @Override
   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   public static class Builder implements GlyphProviderBuilder {
      private final ResourceLocation texture;
      private final List<int[]> chars;
      private final int height;
      private final int ascent;

      public Builder(ResourceLocation var1, int var2, int var3, List<int[]> var4) {
         super();
         this.texture = var1.withPrefix("textures/");
         this.chars = var4;
         this.height = var2;
         this.ascent = var3;
      }

      public static BitmapProvider.Builder fromJson(JsonObject var0) {
         int var1 = GsonHelper.getAsInt(var0, "height", 8);
         int var2 = GsonHelper.getAsInt(var0, "ascent");
         if (var2 > var1) {
            throw new JsonParseException("Ascent " + var2 + " higher than height " + var1);
         } else {
            ArrayList var3 = Lists.newArrayList();
            JsonArray var4 = GsonHelper.getAsJsonArray(var0, "chars");

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               String var6 = GsonHelper.convertToString(var4.get(var5), "chars[" + var5 + "]");
               int[] var7 = var6.codePoints().toArray();
               if (var5 > 0) {
                  int var8 = ((int[])var3.get(0)).length;
                  if (var7.length != var8) {
                     throw new JsonParseException(
                        "Elements of chars have to be the same length (found: " + var7.length + ", expected: " + var8 + "), pad with space or \\u0000"
                     );
                  }
               }

               var3.add(var7);
            }

            if (!var3.isEmpty() && ((int[])var3.get(0)).length != 0) {
               return new BitmapProvider.Builder(new ResourceLocation(GsonHelper.getAsString(var0, "file")), var1, var2, var3);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      @Override
      public GlyphProvider create(ResourceManager var1) {
         try {
            BitmapProvider var22;
            try (InputStream var2 = var1.open(this.texture)) {
               NativeImage var3 = NativeImage.read(NativeImage.Format.RGBA, var2);
               int var4 = var3.getWidth();
               int var5 = var3.getHeight();
               int var6 = var4 / ((int[])this.chars.get(0)).length;
               int var7 = var5 / this.chars.size();
               float var8 = (float)this.height / (float)var7;
               Int2ObjectOpenHashMap var9 = new Int2ObjectOpenHashMap();

               for(int var10 = 0; var10 < this.chars.size(); ++var10) {
                  int var11 = 0;

                  for(int var15 : this.chars.get(var10)) {
                     int var16 = var11++;
                     if (var15 != 0) {
                        int var17 = this.getActualGlyphWidth(var3, var6, var7, var16, var10);
                        BitmapProvider.Glyph var18 = (BitmapProvider.Glyph)var9.put(
                           var15,
                           new BitmapProvider.Glyph(
                              var8, var3, var16 * var6, var10 * var7, var6, var7, (int)(0.5 + (double)((float)var17 * var8)) + 1, this.ascent
                           )
                        );
                        if (var18 != null) {
                           BitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(var15), this.texture);
                        }
                     }
                  }
               }

               var22 = new BitmapProvider(var3, var9);
            }

            return var22;
         } catch (IOException var21) {
            throw new RuntimeException(var21.getMessage());
         }
      }

      private int getActualGlyphWidth(NativeImage var1, int var2, int var3, int var4, int var5) {
         int var6;
         for(var6 = var2 - 1; var6 >= 0; --var6) {
            int var7 = var4 * var2 + var6;

            for(int var8 = 0; var8 < var3; ++var8) {
               int var9 = var5 * var3 + var8;
               if (var1.getLuminanceOrAlpha(var7, var9) != 0) {
                  return var6 + 1;
               }
            }
         }

         return var6 + 1;
      }
   }

   static record Glyph(float a, NativeImage b, int c, int d, int e, int f, int g, int h) implements GlyphInfo {
      final float scale;
      final NativeImage image;
      final int offsetX;
      final int offsetY;
      final int width;
      final int height;
      private final int advance;
      final int ascent;

      Glyph(float var1, NativeImage var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         super();
         this.scale = var1;
         this.image = var2;
         this.offsetX = var3;
         this.offsetY = var4;
         this.width = var5;
         this.height = var6;
         this.advance = var7;
         this.ascent = var8;
      }

      @Override
      public float getAdvance() {
         return (float)this.advance;
      }

      @Override
      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            @Override
            public float getOversample() {
               return 1.0F / Glyph.this.scale;
            }

            @Override
            public int getPixelWidth() {
               return Glyph.this.width;
            }

            @Override
            public int getPixelHeight() {
               return Glyph.this.height;
            }

            @Override
            public float getBearingY() {
               return SheetGlyphInfo.super.getBearingY() + 7.0F - (float)Glyph.this.ascent;
            }

            @Override
            public void upload(int var1, int var2) {
               Glyph.this.image.upload(0, var1, var2, Glyph.this.offsetX, Glyph.this.offsetY, Glyph.this.width, Glyph.this.height, false, false);
            }

            @Override
            public boolean isColored() {
               return Glyph.this.image.format().components() > 1;
            }
         });
      }
   }
}
