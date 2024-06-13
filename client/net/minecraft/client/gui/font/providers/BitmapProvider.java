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
   private final CodepointMap<BitmapProvider.Glyph> glyphs;

   BitmapProvider(NativeImage var1, CodepointMap<BitmapProvider.Glyph> var2) {
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
      return this.glyphs.get(var1);
   }

   @Override
   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   public static record Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid) implements GlyphProviderDefinition {
      private static final Codec<int[][]> CODEPOINT_GRID_CODEC = Codec.STRING.listOf().xmap(var0 -> {
         int var1 = var0.size();
         int[][] var2 = new int[var1][];

         for (int var3 = 0; var3 < var1; var3++) {
            var2[var3] = ((String)var0.get(var3)).codePoints().toArray();
         }

         return var2;
      }, var0 -> {
         ArrayList var1 = new ArrayList(var0.length);

         for (int[] var5 : var0) {
            var1.add(new String(var5, 0, var5.length));
         }

         return var1;
      }).validate(BitmapProvider.Definition::validateDimensions);
      public static final MapCodec<BitmapProvider.Definition> CODEC = RecordCodecBuilder.mapCodec(
            var0 -> var0.group(
                     ResourceLocation.CODEC.fieldOf("file").forGetter(BitmapProvider.Definition::file),
                     Codec.INT.optionalFieldOf("height", 8).forGetter(BitmapProvider.Definition::height),
                     Codec.INT.fieldOf("ascent").forGetter(BitmapProvider.Definition::ascent),
                     CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(BitmapProvider.Definition::codepointGrid)
                  )
                  .apply(var0, BitmapProvider.Definition::new)
         )
         .validate(BitmapProvider.Definition::validate);

      public Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid) {
         super();
         this.file = file;
         this.height = height;
         this.ascent = ascent;
         this.codepointGrid = codepointGrid;
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
               for (int var4 = 1; var4 < var1; var4++) {
                  int[] var5 = var0[var4];
                  if (var5.length != var3) {
                     return DataResult.error(
                        () -> "Lines in codepoint grid have to be the same length (found: "
                              + var5.length
                              + " codepoints, expected: "
                              + var3
                              + "), pad with \\u0000"
                     );
                  }
               }

               return DataResult.success(var0);
            }
         }
      }

      private static DataResult<BitmapProvider.Definition> validate(BitmapProvider.Definition var0) {
         return var0.ascent > var0.height ? DataResult.error(() -> "Ascent " + var0.ascent + " higher than height " + var0.height) : DataResult.success(var0);
      }

      @Override
      public GlyphProviderType type() {
         return GlyphProviderType.BITMAP;
      }

      @Override
      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         return Either.left(this::load);
      }

      private GlyphProvider load(ResourceManager var1) throws IOException {
         ResourceLocation var2 = this.file.withPrefix("textures/");

         BitmapProvider var22;
         try (InputStream var3 = var1.open(var2)) {
            NativeImage var4 = NativeImage.read(NativeImage.Format.RGBA, var3);
            int var5 = var4.getWidth();
            int var6 = var4.getHeight();
            int var7 = var5 / this.codepointGrid[0].length;
            int var8 = var6 / this.codepointGrid.length;
            float var9 = (float)this.height / (float)var8;
            CodepointMap var10 = new CodepointMap<>(BitmapProvider.Glyph[]::new, BitmapProvider.Glyph[][]::new);

            for (int var11 = 0; var11 < this.codepointGrid.length; var11++) {
               int var12 = 0;

               for (int var16 : this.codepointGrid[var11]) {
                  int var17 = var12++;
                  if (var16 != 0) {
                     int var18 = this.getActualGlyphWidth(var4, var7, var8, var17, var11);
                     BitmapProvider.Glyph var19 = var10.put(
                        var16,
                        new BitmapProvider.Glyph(
                           var9, var4, var17 * var7, var11 * var8, var7, var8, (int)(0.5 + (double)((float)var18 * var9)) + 1, this.ascent
                        )
                     );
                     if (var19 != null) {
                        BitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(var16), var2);
                     }
                  }
               }
            }

            var22 = new BitmapProvider(var4, var10);
         }

         return var22;
      }

      private int getActualGlyphWidth(NativeImage var1, int var2, int var3, int var4, int var5) {
         int var6;
         for (var6 = var2 - 1; var6 >= 0; var6--) {
            int var7 = var4 * var2 + var6;

            for (int var8 = 0; var8 < var3; var8++) {
               int var9 = var5 * var3 + var8;
               if (var1.getLuminanceOrAlpha(var7, var9) != 0) {
                  return var6 + 1;
               }
            }
         }

         return var6 + 1;
      }
   }

   static record Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) implements GlyphInfo {

      Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) {
         super();
         this.scale = scale;
         this.image = image;
         this.offsetX = offsetX;
         this.offsetY = offsetY;
         this.width = width;
         this.height = height;
         this.advance = advance;
         this.ascent = ascent;
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
            public float getBearingTop() {
               return (float)Glyph.this.ascent;
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
