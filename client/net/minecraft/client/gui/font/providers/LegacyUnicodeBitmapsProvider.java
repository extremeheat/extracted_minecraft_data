package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegacyUnicodeBitmapsProvider implements GlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceManager resourceManager;
   private final byte[] sizes;
   private final String texturePattern;
   private final Map<ResourceLocation, NativeImage> textures = Maps.newHashMap();

   public LegacyUnicodeBitmapsProvider(ResourceManager var1, byte[] var2, String var3) {
      super();
      this.resourceManager = var1;
      this.sizes = var2;
      this.texturePattern = var3;

      label324:
      for(int var4 = 0; var4 < 256; ++var4) {
         char var5 = (char)(var4 * 256);
         ResourceLocation var6 = this.getSheetLocation(var5);

         try {
            Resource var7 = this.resourceManager.getResource(var6);
            Throwable var8 = null;

            try {
               NativeImage var9 = NativeImage.read(NativeImage.Format.RGBA, var7.getInputStream());
               Throwable var10 = null;

               try {
                  if (var9.getWidth() == 256 && var9.getHeight() == 256) {
                     int var11 = 0;

                     while(true) {
                        if (var11 >= 256) {
                           continue label324;
                        }

                        byte var12 = var2[var5 + var11];
                        if (var12 != 0 && getLeft(var12) > getRight(var12)) {
                           var2[var5 + var11] = 0;
                        }

                        ++var11;
                     }
                  }
               } catch (Throwable var39) {
                  var10 = var39;
                  throw var39;
               } finally {
                  if (var9 != null) {
                     if (var10 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var38) {
                           var10.addSuppressed(var38);
                        }
                     } else {
                        var9.close();
                     }
                  }

               }
            } catch (Throwable var41) {
               var8 = var41;
               throw var41;
            } finally {
               if (var7 != null) {
                  if (var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var37) {
                        var8.addSuppressed(var37);
                     }
                  } else {
                     var7.close();
                  }
               }

            }
         } catch (IOException var43) {
         }

         Arrays.fill(var2, var5, var5 + 256, (byte)0);
      }

   }

   public void close() {
      this.textures.values().forEach(NativeImage::close);
   }

   private ResourceLocation getSheetLocation(char var1) {
      ResourceLocation var2 = new ResourceLocation(String.format(this.texturePattern, String.format("%02x", var1 / 256)));
      return new ResourceLocation(var2.getNamespace(), "textures/" + var2.getPath());
   }

   @Nullable
   public RawGlyph getGlyph(char var1) {
      byte var2 = this.sizes[var1];
      if (var2 != 0) {
         NativeImage var3 = (NativeImage)this.textures.computeIfAbsent(this.getSheetLocation(var1), this::loadTexture);
         if (var3 != null) {
            int var4 = getLeft(var2);
            return new LegacyUnicodeBitmapsProvider.Glyph(var1 % 16 * 16 + var4, (var1 & 255) / 16 * 16, getRight(var2) - var4, 16, var3);
         }
      }

      return null;
   }

   @Nullable
   private NativeImage loadTexture(ResourceLocation var1) {
      try {
         Resource var2 = this.resourceManager.getResource(var1);
         Throwable var3 = null;

         NativeImage var4;
         try {
            var4 = NativeImage.read(NativeImage.Format.RGBA, var2.getInputStream());
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var4;
      } catch (IOException var16) {
         LOGGER.error("Couldn't load texture {}", var1, var16);
         return null;
      }
   }

   private static int getLeft(byte var0) {
      return var0 >> 4 & 15;
   }

   private static int getRight(byte var0) {
      return (var0 & 15) + 1;
   }

   static class Glyph implements RawGlyph {
      private final int width;
      private final int height;
      private final int sourceX;
      private final int sourceY;
      private final NativeImage source;

      private Glyph(int var1, int var2, int var3, int var4, NativeImage var5) {
         super();
         this.width = var3;
         this.height = var4;
         this.sourceX = var1;
         this.sourceY = var2;
         this.source = var5;
      }

      public float getOversample() {
         return 2.0F;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)(this.width / 2 + 1);
      }

      public void upload(int var1, int var2) {
         this.source.upload(0, var1, var2, this.sourceX, this.sourceY, this.width, this.height, false);
      }

      public boolean isColored() {
         return this.source.format().components() > 1;
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }

      // $FF: synthetic method
      Glyph(int var1, int var2, int var3, int var4, NativeImage var5, Object var6) {
         this(var1, var2, var3, var4, var5);
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
         return new LegacyUnicodeBitmapsProvider.Builder(new ResourceLocation(GsonHelper.getAsString(var0, "sizes")), GsonHelper.getAsString(var0, "template"));
      }

      @Nullable
      public GlyphProvider create(ResourceManager var1) {
         try {
            Resource var2 = Minecraft.getInstance().getResourceManager().getResource(this.metadata);
            Throwable var3 = null;

            LegacyUnicodeBitmapsProvider var5;
            try {
               byte[] var4 = new byte[65536];
               var2.getInputStream().read(var4);
               var5 = new LegacyUnicodeBitmapsProvider(var1, var4, this.texturePattern);
            } catch (Throwable var15) {
               var3 = var15;
               throw var15;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var14) {
                        var3.addSuppressed(var14);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return var5;
         } catch (IOException var17) {
            LegacyUnicodeBitmapsProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.metadata);
            return null;
         }
      }
   }
}
