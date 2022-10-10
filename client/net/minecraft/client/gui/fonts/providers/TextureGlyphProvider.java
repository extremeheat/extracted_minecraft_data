package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureGlyphProvider implements IGlyphProvider {
   private static final Logger field_211609_a = LogManager.getLogger();
   private final NativeImage field_211610_b;
   private final Char2ObjectMap<TextureGlyphProvider.GlyphInfo> field_211267_a;

   public TextureGlyphProvider(NativeImage var1, Char2ObjectMap<TextureGlyphProvider.GlyphInfo> var2) {
      super();
      this.field_211610_b = var1;
      this.field_211267_a = var2;
   }

   public void close() {
      this.field_211610_b.close();
   }

   @Nullable
   public IGlyphInfo func_212248_a(char var1) {
      return (IGlyphInfo)this.field_211267_a.get(var1);
   }

   static final class GlyphInfo implements IGlyphInfo {
      private final float field_211582_a;
      private final NativeImage field_211583_b;
      private final int field_211584_c;
      private final int field_211585_d;
      private final int field_211586_e;
      private final int field_211587_f;
      private final int field_211588_g;
      private final int field_211589_h;

      private GlyphInfo(float var1, NativeImage var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         super();
         this.field_211582_a = var1;
         this.field_211583_b = var2;
         this.field_211584_c = var3;
         this.field_211585_d = var4;
         this.field_211586_e = var5;
         this.field_211587_f = var6;
         this.field_211588_g = var7;
         this.field_211589_h = var8;
      }

      public float func_211578_g() {
         return 1.0F / this.field_211582_a;
      }

      public int func_211202_a() {
         return this.field_211586_e;
      }

      public int func_211203_b() {
         return this.field_211587_f;
      }

      public float getAdvance() {
         return (float)this.field_211588_g;
      }

      public float getBearingY() {
         return IGlyphInfo.super.getBearingY() + 7.0F - (float)this.field_211589_h;
      }

      public void func_211573_a(int var1, int var2) {
         this.field_211583_b.func_195706_a(0, var1, var2, this.field_211584_c, this.field_211585_d, this.field_211586_e, this.field_211587_f, false);
      }

      public boolean func_211579_f() {
         return this.field_211583_b.func_211678_c().func_211651_a() > 1;
      }

      // $FF: synthetic method
      GlyphInfo(float var1, NativeImage var2, int var3, int var4, int var5, int var6, int var7, int var8, Object var9) {
         this(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation field_211252_a;
      private final List<String> field_211634_b;
      private final int field_211635_c;
      private final int field_211636_d;

      public Factory(ResourceLocation var1, int var2, int var3, List<String> var4) {
         super();
         this.field_211252_a = new ResourceLocation(var1.func_110624_b(), "textures/" + var1.func_110623_a());
         this.field_211634_b = var4;
         this.field_211635_c = var2;
         this.field_211636_d = var3;
      }

      public static TextureGlyphProvider.Factory func_211633_a(JsonObject var0) {
         int var1 = JsonUtils.func_151208_a(var0, "height", 8);
         int var2 = JsonUtils.func_151203_m(var0, "ascent");
         if (var2 > var1) {
            throw new JsonParseException("Ascent " + var2 + " higher than height " + var1);
         } else {
            ArrayList var3 = Lists.newArrayList();
            JsonArray var4 = JsonUtils.func_151214_t(var0, "chars");

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               String var6 = JsonUtils.func_151206_a(var4.get(var5), "chars[" + var5 + "]");
               if (var5 > 0) {
                  int var7 = var6.length();
                  int var8 = ((String)var3.get(0)).length();
                  if (var7 != var8) {
                     throw new JsonParseException("Elements of chars have to be the same lenght (found: " + var7 + ", expected: " + var8 + "), pad with space or \\u0000");
                  }
               }

               var3.add(var6);
            }

            if (!var3.isEmpty() && !((String)var3.get(0)).isEmpty()) {
               return new TextureGlyphProvider.Factory(new ResourceLocation(JsonUtils.func_151200_h(var0, "file")), var1, var2, var3);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      public IGlyphProvider func_211246_a(IResourceManager var1) {
         try {
            IResource var2 = var1.func_199002_a(this.field_211252_a);
            Throwable var3 = null;

            try {
               NativeImage var4 = NativeImage.func_211679_a(NativeImage.PixelFormat.RGBA, var2.func_199027_b());
               int var5 = var4.func_195702_a();
               int var6 = var4.func_195714_b();
               int var7 = var5 / ((String)this.field_211634_b.get(0)).length();
               int var8 = var6 / this.field_211634_b.size();
               float var9 = (float)this.field_211635_c / (float)var8;
               Char2ObjectOpenHashMap var10 = new Char2ObjectOpenHashMap();

               for(int var11 = 0; var11 < this.field_211634_b.size(); ++var11) {
                  String var12 = (String)this.field_211634_b.get(var11);

                  for(int var13 = 0; var13 < var12.length(); ++var13) {
                     char var14 = var12.charAt(var13);
                     if (var14 != 0 && var14 != ' ') {
                        int var15 = this.func_211632_a(var4, var7, var8, var13, var11);
                        var10.put(var14, new TextureGlyphProvider.GlyphInfo(var9, var4, var13 * var7, var11 * var8, var7, var8, (int)(0.5D + (double)((float)var15 * var9)) + 1, this.field_211636_d));
                     }
                  }
               }

               TextureGlyphProvider var27 = new TextureGlyphProvider(var4, var10);
               return var27;
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
         } catch (IOException var26) {
            throw new RuntimeException(var26.getMessage());
         }
      }

      private int func_211632_a(NativeImage var1, int var2, int var3, int var4, int var5) {
         int var6;
         for(var6 = var2 - 1; var6 >= 0; --var6) {
            int var7 = var4 * var2 + var6;

            for(int var8 = 0; var8 < var3; ++var8) {
               int var9 = var5 * var3 + var8;
               if (var1.func_211675_e(var7, var9) != 0) {
                  return var6 + 1;
               }
            }
         }

         return var6 + 1;
      }
   }
}
