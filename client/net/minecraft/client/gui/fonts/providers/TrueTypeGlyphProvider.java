package net.minecraft.client.gui.fonts.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

public class TrueTypeGlyphProvider implements IGlyphProvider {
   private static final Logger field_211263_a = LogManager.getLogger();
   private final STBTTFontinfo field_211264_b;
   private final float field_211618_c;
   private final CharSet field_211619_d = new CharArraySet();
   private final float field_211620_e;
   private final float field_211621_f;
   private final float field_211266_d;
   private final float field_211622_h;

   protected TrueTypeGlyphProvider(STBTTFontinfo var1, float var2, float var3, float var4, float var5, String var6) {
      super();
      this.field_211264_b = var1;
      this.field_211618_c = var3;
      var6.chars().forEach((var1x) -> {
         this.field_211619_d.add((char)(var1x & '\uffff'));
      });
      this.field_211620_e = var4 * var3;
      this.field_211621_f = var5 * var3;
      this.field_211266_d = STBTruetype.stbtt_ScaleForPixelHeight(var1, var2 * var3);
      MemoryStack var7 = MemoryStack.stackPush();
      Throwable var8 = null;

      try {
         IntBuffer var9 = var7.mallocInt(1);
         IntBuffer var10 = var7.mallocInt(1);
         IntBuffer var11 = var7.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(var1, var9, var10, var11);
         this.field_211622_h = (float)var9.get(0) * this.field_211266_d;
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
   public TrueTypeGlyphProvider.GlpyhInfo func_212248_a(char var1) {
      if (this.field_211619_d.contains(var1)) {
         return null;
      } else {
         MemoryStack var2 = MemoryStack.stackPush();
         Throwable var3 = null;

         TrueTypeGlyphProvider.GlpyhInfo var13;
         try {
            IntBuffer var4 = var2.mallocInt(1);
            IntBuffer var5 = var2.mallocInt(1);
            IntBuffer var6 = var2.mallocInt(1);
            IntBuffer var7 = var2.mallocInt(1);
            int var8 = STBTruetype.stbtt_FindGlyphIndex(this.field_211264_b, var1);
            if (var8 == 0) {
               Object var26 = null;
               return (TrueTypeGlyphProvider.GlpyhInfo)var26;
            }

            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.field_211264_b, var8, this.field_211266_d, this.field_211266_d, this.field_211620_e, this.field_211621_f, var4, var5, var6, var7);
            int var9 = var6.get(0) - var4.get(0);
            int var10 = var7.get(0) - var5.get(0);
            IntBuffer var11;
            if (var9 == 0 || var10 == 0) {
               var11 = null;
               return var11;
            }

            var11 = var2.mallocInt(1);
            IntBuffer var12 = var2.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics(this.field_211264_b, var8, var11, var12);
            var13 = new TrueTypeGlyphProvider.GlpyhInfo(var4.get(0), var6.get(0), -var5.get(0), -var7.get(0), (float)var11.get(0) * this.field_211266_d, (float)var12.get(0) * this.field_211266_d, var8);
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

         return var13;
      }
   }

   // $FF: synthetic method
   @Nullable
   public IGlyphInfo func_212248_a(char var1) {
      return this.func_212248_a(var1);
   }

   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation field_211249_a;
      private final float field_211250_b;
      private final float field_211625_c;
      private final float field_211626_d;
      private final float field_211627_e;
      private final String field_211628_f;

      public Factory(ResourceLocation var1, float var2, float var3, float var4, float var5, String var6) {
         super();
         this.field_211249_a = var1;
         this.field_211250_b = var2;
         this.field_211625_c = var3;
         this.field_211626_d = var4;
         this.field_211627_e = var5;
         this.field_211628_f = var6;
      }

      public static IGlyphProviderFactory func_211624_a(JsonObject var0) {
         float var1 = 0.0F;
         float var2 = 0.0F;
         if (var0.has("shift")) {
            JsonArray var3 = var0.getAsJsonArray("shift");
            if (var3.size() != 2) {
               throw new JsonParseException("Expected 2 elements in 'shift', found " + var3.size());
            }

            var1 = JsonUtils.func_151220_d(var3.get(0), "shift[0]");
            var2 = JsonUtils.func_151220_d(var3.get(1), "shift[1]");
         }

         StringBuilder var7 = new StringBuilder();
         if (var0.has("skip")) {
            JsonElement var4 = var0.get("skip");
            if (var4.isJsonArray()) {
               JsonArray var5 = JsonUtils.func_151207_m(var4, "skip");

               for(int var6 = 0; var6 < var5.size(); ++var6) {
                  var7.append(JsonUtils.func_151206_a(var5.get(var6), "skip[" + var6 + "]"));
               }
            } else {
               var7.append(JsonUtils.func_151206_a(var4, "skip"));
            }
         }

         return new TrueTypeGlyphProvider.Factory(new ResourceLocation(JsonUtils.func_151200_h(var0, "file")), JsonUtils.func_151221_a(var0, "size", 11.0F), JsonUtils.func_151221_a(var0, "oversample", 1.0F), var1, var2, var7.toString());
      }

      @Nullable
      public IGlyphProvider func_211246_a(IResourceManager var1) {
         try {
            IResource var2 = var1.func_199002_a(new ResourceLocation(this.field_211249_a.func_110624_b(), "font/" + this.field_211249_a.func_110623_a()));
            Throwable var3 = null;

            TrueTypeGlyphProvider var6;
            try {
               TrueTypeGlyphProvider.field_211263_a.info("Loading font");
               ByteBuffer var4 = TextureUtil.func_195724_a(var2.func_199027_b());
               var4.flip();
               STBTTFontinfo var5 = STBTTFontinfo.create();
               TrueTypeGlyphProvider.field_211263_a.info("Reading font");
               if (!STBTruetype.stbtt_InitFont(var5, var4)) {
                  throw new IOException("Invalid ttf");
               }

               var6 = new TrueTypeGlyphProvider(var5, this.field_211250_b, this.field_211625_c, this.field_211626_d, this.field_211627_e, this.field_211628_f);
            } catch (Throwable var16) {
               var3 = var16;
               throw var16;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return var6;
         } catch (IOException var18) {
            TrueTypeGlyphProvider.field_211263_a.error("Couldn't load truetype font {}", this.field_211249_a, var18);
            return null;
         }
      }
   }

   class GlpyhInfo implements IGlyphInfo {
      private final int field_211216_b;
      private final int field_211217_c;
      private final float field_212464_d;
      private final float field_212465_e;
      private final float field_211598_i;
      private final int field_211599_j;

      private GlpyhInfo(int var2, int var3, int var4, int var5, float var6, float var7, int var8) {
         super();
         this.field_211216_b = var3 - var2;
         this.field_211217_c = var4 - var5;
         this.field_211598_i = var6 / TrueTypeGlyphProvider.this.field_211618_c;
         this.field_212464_d = (var7 + (float)var2 + TrueTypeGlyphProvider.this.field_211620_e) / TrueTypeGlyphProvider.this.field_211618_c;
         this.field_212465_e = (TrueTypeGlyphProvider.this.field_211622_h - (float)var4 + TrueTypeGlyphProvider.this.field_211621_f) / TrueTypeGlyphProvider.this.field_211618_c;
         this.field_211599_j = var8;
      }

      public int func_211202_a() {
         return this.field_211216_b;
      }

      public int func_211203_b() {
         return this.field_211217_c;
      }

      public float func_211578_g() {
         return TrueTypeGlyphProvider.this.field_211618_c;
      }

      public float getAdvance() {
         return this.field_211598_i;
      }

      public float getBearingX() {
         return this.field_212464_d;
      }

      public float getBearingY() {
         return this.field_212465_e;
      }

      public void func_211573_a(int var1, int var2) {
         NativeImage var3 = new NativeImage(NativeImage.PixelFormat.LUMINANCE, this.field_211216_b, this.field_211217_c, false);
         Throwable var4 = null;

         try {
            var3.func_211676_a(TrueTypeGlyphProvider.this.field_211264_b, this.field_211599_j, this.field_211216_b, this.field_211217_c, TrueTypeGlyphProvider.this.field_211266_d, TrueTypeGlyphProvider.this.field_211266_d, TrueTypeGlyphProvider.this.field_211620_e, TrueTypeGlyphProvider.this.field_211621_f, 0, 0);
            var3.func_195706_a(0, var1, var2, 0, 0, this.field_211216_b, this.field_211217_c, false);
         } catch (Throwable var13) {
            var4 = var13;
            throw var13;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  var3.close();
               }
            }

         }

      }

      public boolean func_211579_f() {
         return false;
      }

      // $FF: synthetic method
      GlpyhInfo(int var2, int var3, int var4, int var5, float var6, float var7, int var8, Object var9) {
         this(var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
