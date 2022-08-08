package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Maps;
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
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class LegacyUnicodeBitmapsProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int UNICODE_SHEETS = 256;
   private static final int CODEPOINTS_PER_SHEET = 256;
   private static final int TEXTURE_SIZE = 256;
   private static final byte NO_GLYPH = 0;
   private final ResourceManager resourceManager;
   private final byte[] sizes;
   private final String texturePattern;
   private final Map<ResourceLocation, NativeImage> textures = Maps.newHashMap();

   public LegacyUnicodeBitmapsProvider(ResourceManager var1, byte[] var2, String var3) {
      super();
      this.resourceManager = var1;
      this.sizes = var2;
      this.texturePattern = var3;

      for(int var4 = 0; var4 < 256; ++var4) {
         int var5 = var4 * 256;
         ResourceLocation var6 = this.getSheetLocation(var5);

         try {
            InputStream var7 = this.resourceManager.open(var6);

            label92: {
               try {
                  label105: {
                     NativeImage var8 = NativeImage.read(NativeImage.Format.RGBA, var7);

                     label106: {
                        try {
                           if (var8.getWidth() != 256 || var8.getHeight() != 256) {
                              break label106;
                           }

                           for(int var9 = 0; var9 < 256; ++var9) {
                              byte var10 = var2[var5 + var9];
                              if (var10 != 0 && getLeft(var10) > getRight(var10)) {
                                 var2[var5 + var9] = 0;
                              }
                           }
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
                        break label105;
                     }

                     if (var8 != null) {
                        var8.close();
                     }
                     break label92;
                  }
               } catch (Throwable var14) {
                  if (var7 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var11) {
                        var14.addSuppressed(var11);
                     }
                  }

                  throw var14;
               }

               if (var7 != null) {
                  var7.close();
               }
               continue;
            }

            if (var7 != null) {
               var7.close();
            }
         } catch (IOException var15) {
         }

         Arrays.fill(var2, var5, var5 + 256, (byte)0);
      }

   }

   public void close() {
      this.textures.values().forEach(NativeImage::close);
   }

   private ResourceLocation getSheetLocation(int var1) {
      ResourceLocation var2 = new ResourceLocation(String.format(Locale.ROOT, this.texturePattern, String.format(Locale.ROOT, "%02x", var1 / 256)));
      return new ResourceLocation(var2.getNamespace(), "textures/" + var2.getPath());
   }

   @Nullable
   public GlyphInfo getGlyph(int var1) {
      if (var1 >= 0 && var1 < this.sizes.length) {
         byte var2 = this.sizes[var1];
         if (var2 != 0) {
            NativeImage var3 = (NativeImage)this.textures.computeIfAbsent(this.getSheetLocation(var1), this::loadTexture);
            if (var3 != null) {
               int var4 = getLeft(var2);
               return new Glyph(var1 % 16 * 16 + var4, (var1 & 255) / 16 * 16, getRight(var2) - var4, 16, var3);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public IntSet getSupportedGlyphs() {
      IntOpenHashSet var1 = new IntOpenHashSet();

      for(int var2 = 0; var2 < this.sizes.length; ++var2) {
         if (this.sizes[var2] != 0) {
            var1.add(var2);
         }
      }

      return var1;
   }

   @Nullable
   private NativeImage loadTexture(ResourceLocation var1) {
      try {
         InputStream var2 = this.resourceManager.open(var1);

         NativeImage var3;
         try {
            var3 = NativeImage.read(NativeImage.Format.RGBA, var2);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

         return var3;
      } catch (IOException var7) {
         LOGGER.error("Couldn't load texture {}", var1, var7);
         return null;
      }
   }

   private static int getLeft(byte var0) {
      return var0 >> 4 & 15;
   }

   private static int getRight(byte var0) {
      return (var0 & 15) + 1;
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

      public float getAdvance() {
         return (float)(this.width / 2 + 1);
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }

      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            public float getOversample() {
               return 2.0F;
            }

            public int getPixelWidth() {
               return Glyph.this.width;
            }

            public int getPixelHeight() {
               return Glyph.this.height;
            }

            public void upload(int var1, int var2) {
               Glyph.this.source.upload(0, var1, var2, Glyph.this.sourceX, Glyph.this.sourceY, Glyph.this.width, Glyph.this.height, false, false);
            }

            public boolean isColored() {
               return Glyph.this.source.format().components() > 1;
            }
         });
      }

      public int sourceX() {
         return this.sourceX;
      }

      public int sourceY() {
         return this.sourceY;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public NativeImage source() {
         return this.source;
      }
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
         return new Builder(new ResourceLocation(GsonHelper.getAsString(var0, "sizes")), getTemplate(var0));
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
      public GlyphProvider create(ResourceManager var1) {
         try {
            InputStream var2 = Minecraft.getInstance().getResourceManager().open(this.metadata);

            LegacyUnicodeBitmapsProvider var4;
            try {
               byte[] var3 = var2.readNBytes(65536);
               var4 = new LegacyUnicodeBitmapsProvider(var1, var3, this.texturePattern);
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }

            return var4;
         } catch (IOException var7) {
            LegacyUnicodeBitmapsProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.metadata);
            return null;
         }
      }
   }
}
