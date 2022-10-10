package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureGlyphProviderUnicode implements IGlyphProvider {
   private static final Logger field_211256_a = LogManager.getLogger();
   private final IResourceManager field_211257_b;
   private final byte[] field_211258_c;
   private final String field_211259_d;
   private final Map<ResourceLocation, NativeImage> field_211845_e = Maps.newHashMap();

   public TextureGlyphProviderUnicode(IResourceManager var1, byte[] var2, String var3) {
      super();
      this.field_211257_b = var1;
      this.field_211258_c = var2;
      this.field_211259_d = var3;

      label324:
      for(int var4 = 0; var4 < 256; ++var4) {
         char var5 = (char)(var4 * 256);
         ResourceLocation var6 = this.func_211623_c(var5);

         try {
            IResource var7 = this.field_211257_b.func_199002_a(var6);
            Throwable var8 = null;

            try {
               NativeImage var9 = NativeImage.func_211679_a(NativeImage.PixelFormat.RGBA, var7.func_199027_b());
               Throwable var10 = null;

               try {
                  if (var9.func_195702_a() == 256 && var9.func_195714_b() == 256) {
                     int var11 = 0;

                     while(true) {
                        if (var11 >= 256) {
                           continue label324;
                        }

                        byte var12 = var2[var5 + var11];
                        if (var12 != 0 && func_212453_a(var12) > func_212454_b(var12)) {
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
      this.field_211845_e.values().forEach(NativeImage::close);
   }

   private ResourceLocation func_211623_c(char var1) {
      ResourceLocation var2 = new ResourceLocation(String.format(this.field_211259_d, String.format("%02x", var1 / 256)));
      return new ResourceLocation(var2.func_110624_b(), "textures/" + var2.func_110623_a());
   }

   @Nullable
   public IGlyphInfo func_212248_a(char var1) {
      byte var2 = this.field_211258_c[var1];
      if (var2 != 0) {
         NativeImage var3 = (NativeImage)this.field_211845_e.computeIfAbsent(this.func_211623_c(var1), this::func_211255_a);
         if (var3 != null) {
            int var4 = func_212453_a(var2);
            return new TextureGlyphProviderUnicode.GlpyhInfo(var1 % 16 * 16 + var4, (var1 & 255) / 16 * 16, func_212454_b(var2) - var4, 16, var3);
         }
      }

      return null;
   }

   @Nullable
   private NativeImage func_211255_a(ResourceLocation var1) {
      try {
         IResource var2 = this.field_211257_b.func_199002_a(var1);
         Throwable var3 = null;

         NativeImage var4;
         try {
            var4 = NativeImage.func_211679_a(NativeImage.PixelFormat.RGBA, var2.func_199027_b());
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
         field_211256_a.error("Couldn't load texture {}", var1, var16);
         return null;
      }
   }

   private static int func_212453_a(byte var0) {
      return var0 >> 4 & 15;
   }

   private static int func_212454_b(byte var0) {
      return (var0 & 15) + 1;
   }

   static class GlpyhInfo implements IGlyphInfo {
      private final int field_211210_a;
      private final int field_211211_b;
      private final int field_211212_c;
      private final int field_211213_d;
      private final NativeImage field_211214_e;

      private GlpyhInfo(int var1, int var2, int var3, int var4, NativeImage var5) {
         super();
         this.field_211210_a = var3;
         this.field_211211_b = var4;
         this.field_211212_c = var1;
         this.field_211213_d = var2;
         this.field_211214_e = var5;
      }

      public float func_211578_g() {
         return 2.0F;
      }

      public int func_211202_a() {
         return this.field_211210_a;
      }

      public int func_211203_b() {
         return this.field_211211_b;
      }

      public float getAdvance() {
         return (float)(this.field_211210_a / 2 + 1);
      }

      public void func_211573_a(int var1, int var2) {
         this.field_211214_e.func_195706_a(0, var1, var2, this.field_211212_c, this.field_211213_d, this.field_211210_a, this.field_211211_b, false);
      }

      public boolean func_211579_f() {
         return this.field_211214_e.func_211678_c().func_211651_a() > 1;
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }

      // $FF: synthetic method
      GlpyhInfo(int var1, int var2, int var3, int var4, NativeImage var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation field_211247_a;
      private final String field_211248_b;

      public Factory(ResourceLocation var1, String var2) {
         super();
         this.field_211247_a = var1;
         this.field_211248_b = var2;
      }

      public static IGlyphProviderFactory func_211629_a(JsonObject var0) {
         return new TextureGlyphProviderUnicode.Factory(new ResourceLocation(JsonUtils.func_151200_h(var0, "sizes")), JsonUtils.func_151200_h(var0, "template"));
      }

      @Nullable
      public IGlyphProvider func_211246_a(IResourceManager var1) {
         try {
            IResource var2 = Minecraft.func_71410_x().func_195551_G().func_199002_a(this.field_211247_a);
            Throwable var3 = null;

            TextureGlyphProviderUnicode var5;
            try {
               byte[] var4 = new byte[65536];
               var2.func_199027_b().read(var4);
               var5 = new TextureGlyphProviderUnicode(var1, var4, this.field_211248_b);
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
            TextureGlyphProviderUnicode.field_211256_a.error("Cannot load {}, unicode glyphs will not render correctly", this.field_211247_a);
            return null;
         }
      }
   }
}
