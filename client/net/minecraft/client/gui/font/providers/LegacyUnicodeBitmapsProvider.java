package net.minecraft.client.gui.font.providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class LegacyUnicodeBitmapsProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int UNICODE_SHEETS = 256;
   private static final int CODEPOINTS_PER_SHEET = 256;
   private static final int TEXTURE_SIZE = 256;
   private static final byte NO_GLYPH = 0;
   private static final int TOTAL_CODEPOINTS = 65536;
   private final byte[] sizes;
   private final LegacyUnicodeBitmapsProvider.Sheet[] sheets = new LegacyUnicodeBitmapsProvider.Sheet[256];

   public LegacyUnicodeBitmapsProvider(ResourceManager var1, byte[] var2, String var3) {
      super();
      this.sizes = var2;
      HashSet var4 = new HashSet();

      for(int var5 = 0; var5 < 256; ++var5) {
         int var6 = var5 * 256;
         var4.add(getSheetLocation(var3, var6));
      }

      String var13 = getCommonSearchPrefix(var4);
      HashMap var14 = new HashMap();
      var1.listResources(var13, var4::contains).forEach((var1x, var2x) -> var14.put(var1x, CompletableFuture.supplyAsync(() -> {
            try {
               NativeImage var3x;
               try (InputStream var2xx = var2x.open()) {
                  var3x = NativeImage.read(NativeImage.Format.RGBA, var2xx);
               }

               return var3x;
            } catch (IOException var7x) {
               LOGGER.error("Failed to read resource {} from pack {}", var1x, var2x.sourcePackId());
               return null;
            }
         }, Util.backgroundExecutor())));
      ArrayList var7 = new ArrayList(256);

      for(int var8 = 0; var8 < 256; ++var8) {
         int var9 = var8 * 256;
         int var10 = var8;
         ResourceLocation var11 = getSheetLocation(var3, var9);
         CompletableFuture var12 = (CompletableFuture)var14.get(var11);
         if (var12 != null) {
            var7.add(var12.thenAcceptAsync(var4x -> {
               if (var4x != null) {
                  if (var4x.getWidth() == 256 && var4x.getHeight() == 256) {
                     for(int var5x = 0; var5x < 256; ++var5x) {
                        byte var6x = var2[var9 + var5x];
                        if (var6x != 0 && getLeft(var6x) > getRight(var6x)) {
                           var2[var9 + var5x] = 0;
                        }
                     }

                     this.sheets[var10] = new LegacyUnicodeBitmapsProvider.Sheet(var2, var4x);
                  } else {
                     var4x.close();
                     Arrays.fill(var2, var9, var9 + 256, (byte)0);
                  }
               }
            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf(var7.toArray(var0 -> new CompletableFuture[var0])).join();
   }

   private static String getCommonSearchPrefix(Set<ResourceLocation> var0) {
      String var1 = StringUtils.getCommonPrefix(var0.stream().map(ResourceLocation::getPath).toArray(var0x -> new String[var0x]));
      int var2 = var1.lastIndexOf("/");
      return var2 == -1 ? "" : var1.substring(0, var2);
   }

   @Override
   public void close() {
      for(LegacyUnicodeBitmapsProvider.Sheet var4 : this.sheets) {
         if (var4 != null) {
            var4.close();
         }
      }
   }

   private static ResourceLocation getSheetLocation(String var0, int var1) {
      String var2 = String.format(Locale.ROOT, "%02x", var1 / 256);
      ResourceLocation var3 = new ResourceLocation(String.format(Locale.ROOT, var0, var2));
      return var3.withPrefix("textures/");
   }

   @Nullable
   @Override
   public GlyphInfo getGlyph(int var1) {
      if (var1 >= 0 && var1 < this.sizes.length) {
         int var2 = var1 / 256;
         LegacyUnicodeBitmapsProvider.Sheet var3 = this.sheets[var2];
         return var3 != null ? var3.getGlyph(var1) : null;
      } else {
         return null;
      }
   }

   @Override
   public IntSet getSupportedGlyphs() {
      IntOpenHashSet var1 = new IntOpenHashSet();

      for(int var2 = 0; var2 < this.sizes.length; ++var2) {
         if (this.sizes[var2] != 0) {
            var1.add(var2);
         }
      }

      return var1;
   }

   static int getLeft(byte var0) {
      return var0 >> 4 & 15;
   }

   static int getRight(byte var0) {
      return (var0 & 15) + 1;
   }

   public static class Builder implements GlyphProviderBuilder {
      private final ResourceLocation metadata;
      private final String texturePattern;

      public Builder(ResourceLocation var1, String var2) {
         super();
         this.metadata = var1;
         this.texturePattern = var2;
      }

      public static GlyphProviderBuilder fromJson(JsonObject var0) {
         return new LegacyUnicodeBitmapsProvider.Builder(new ResourceLocation(GsonHelper.getAsString(var0, "sizes")), getTemplate(var0));
      }

      private static String getTemplate(JsonObject var0) {
         String var1 = GsonHelper.getAsString(var0, "template");

         try {
            String.format(Locale.ROOT, var1, "");
            return var1;
         } catch (IllegalFormatException var3) {
            throw new JsonParseException("Invalid legacy unicode template supplied, expected single '%s': " + var1);
         }
      }

      @Nullable
      @Override
      public GlyphProvider create(ResourceManager var1) {
         try {
            LegacyUnicodeBitmapsProvider var4;
            try (InputStream var2 = Minecraft.getInstance().getResourceManager().open(this.metadata)) {
               byte[] var3 = var2.readNBytes(65536);
               var4 = new LegacyUnicodeBitmapsProvider(var1, var3, this.texturePattern);
            }

            return var4;
         } catch (IOException var7) {
            LegacyUnicodeBitmapsProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.metadata);
            return null;
         }
      }
   }

   static record Glyph(int a, int b, int c, int d, NativeImage e) implements GlyphInfo {
      final int sourceX;
      final int sourceY;
      final int width;
      final int height;
      final NativeImage source;

      Glyph(int var1, int var2, int var3, int var4, NativeImage var5) {
         super();
         this.sourceX = var1;
         this.sourceY = var2;
         this.width = var3;
         this.height = var4;
         this.source = var5;
      }

      @Override
      public float getAdvance() {
         return (float)(this.width / 2 + 1);
      }

      @Override
      public float getShadowOffset() {
         return 0.5F;
      }

      @Override
      public float getBoldOffset() {
         return 0.5F;
      }

      @Override
      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            @Override
            public float getOversample() {
               return 2.0F;
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
            public void upload(int var1, int var2) {
               Glyph.this.source.upload(0, var1, var2, Glyph.this.sourceX, Glyph.this.sourceY, Glyph.this.width, Glyph.this.height, false, false);
            }

            @Override
            public boolean isColored() {
               return Glyph.this.source.format().components() > 1;
            }
         });
      }
   }

   static class Sheet implements AutoCloseable {
      private final byte[] sizes;
      private final NativeImage source;

      Sheet(byte[] var1, NativeImage var2) {
         super();
         this.sizes = var1;
         this.source = var2;
      }

      @Override
      public void close() {
         this.source.close();
      }

      @Nullable
      public GlyphInfo getGlyph(int var1) {
         byte var2 = this.sizes[var1];
         if (var2 != 0) {
            int var3 = LegacyUnicodeBitmapsProvider.getLeft(var2);
            return new LegacyUnicodeBitmapsProvider.Glyph(
               var1 % 16 * 16 + var3, (var1 & 0xFF) / 16 * 16, LegacyUnicodeBitmapsProvider.getRight(var2) - var3, 16, this.source
            );
         } else {
            return null;
         }
      }
   }
}
