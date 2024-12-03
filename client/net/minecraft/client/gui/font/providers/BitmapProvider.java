package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class BitmapProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private final NativeImage image;
   private final CodepointMap<Glyph> glyphs;

   BitmapProvider(NativeImage var1, CodepointMap<Glyph> var2) {
      super();
      this.image = var1;
      this.glyphs = var2;
   }

   public void close() {
      this.image.close();
   }

   @Nullable
   public GlyphInfo getGlyph(int var1) {
      return this.glyphs.get(var1);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   public static record Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid) implements GlyphProviderDefinition {
      private static final Codec<int[][]> CODEPOINT_GRID_CODEC;
      public static final MapCodec<Definition> CODEC;

      public Definition(ResourceLocation var1, int var2, int var3, int[][] var4) {
         super();
         this.file = var1;
         this.height = var2;
         this.ascent = var3;
         this.codepointGrid = var4;
      }

      private static DataResult<int[][]> validateDimensions(int[][] var0) {
         int var1 = var0.length;
         if (var1 == 0) {
            return DataResult.error(() -> "Expected to find data in codepoint grid");
         } else {
            int[] var2 = var0[0];
            int var3 = var2.length;
            if (var3 == 0) {
               return DataResult.error(() -> "Expected to find data in codepoint grid");
            } else {
               for(int var4 = 1; var4 < var1; ++var4) {
                  int[] var5 = var0[var4];
                  if (var5.length != var3) {
                     return DataResult.error(() -> "Lines in codepoint grid have to be the same length (found: " + var5.length + " codepoints, expected: " + var3 + "), pad with \\u0000");
                  }
               }

               return DataResult.success(var0);
            }
         }
      }

      private static DataResult<Definition> validate(Definition var0) {
         return var0.ascent > var0.height ? DataResult.error(() -> "Ascent " + var0.ascent + " higher than height " + var0.height) : DataResult.success(var0);
      }

      public GlyphProviderType type() {
         return GlyphProviderType.BITMAP;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         return Either.left(this::load);
      }

      private GlyphProvider load(ResourceManager var1) throws IOException {
         ResourceLocation var2 = this.file.withPrefix("textures/");
         InputStream var3 = var1.open(var2);

         BitmapProvider var22;
         try {
            NativeImage var4 = NativeImage.read(NativeImage.Format.RGBA, var3);
            int var5 = var4.getWidth();
            int var6 = var4.getHeight();
            int var7 = var5 / this.codepointGrid[0].length;
            int var8 = var6 / this.codepointGrid.length;
            float var9 = (float)this.height / (float)var8;
            CodepointMap var10 = new CodepointMap((var0) -> new Glyph[var0], (var0) -> new Glyph[var0][]);

            for(int var11 = 0; var11 < this.codepointGrid.length; ++var11) {
               int var12 = 0;

               for(int var16 : this.codepointGrid[var11]) {
                  int var17 = var12++;
                  if (var16 != 0) {
                     int var18 = this.getActualGlyphWidth(var4, var7, var8, var17, var11);
                     Glyph var19 = (Glyph)var10.put(var16, new Glyph(var9, var4, var17 * var7, var11 * var8, var7, var8, (int)(0.5 + (double)((float)var18 * var9)) + 1, this.ascent));
                     if (var19 != null) {
                        BitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(var16), var2);
                     }
                  }
               }
            }

            var22 = new BitmapProvider(var4, var10);
         } catch (Throwable var21) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var20) {
                  var21.addSuppressed(var20);
               }
            }

            throw var21;
         }

         if (var3 != null) {
            var3.close();
         }

         return var22;
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

      static {
         CODEPOINT_GRID_CODEC = Codec.STRING.listOf().xmap((var0) -> {
            int var1 = var0.size();
            int[][] var2 = new int[var1][];

            for(int var3 = 0; var3 < var1; ++var3) {
               var2[var3] = ((String)var0.get(var3)).codePoints().toArray();
            }

            return var2;
         }, (var0) -> {
            ArrayList var1 = new ArrayList(var0.length);

            for(int[] var5 : var0) {
               var1.add(new String(var5, 0, var5.length));
            }

            return var1;
         }).validate(Definition::validateDimensions);
         CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("file").forGetter(Definition::file), Codec.INT.optionalFieldOf("height", 8).forGetter(Definition::height), Codec.INT.fieldOf("ascent").forGetter(Definition::ascent), CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(Definition::codepointGrid)).apply(var0, Definition::new)).validate(Definition::validate);
      }
   }

   static record Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) implements GlyphInfo {
      final float scale;
      final NativeImage image;
      final int offsetX;
      final int offsetY;
      final int width;
      final int height;
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

      public float getAdvance() {
         return (float)this.advance;
      }

      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            public float getOversample() {
               return 1.0F / Glyph.this.scale;
            }

            public int getPixelWidth() {
               return Glyph.this.width;
            }

            public int getPixelHeight() {
               return Glyph.this.height;
            }

            public float getBearingTop() {
               return (float)Glyph.this.ascent;
            }

            public void upload(int var1, int var2) {
               Glyph.this.image.upload(0, var1, var2, Glyph.this.offsetX, Glyph.this.offsetY, Glyph.this.width, Glyph.this.height, false);
            }

            public boolean isColored() {
               return Glyph.this.image.format().components() > 1;
            }
         });
      }
   }
}
