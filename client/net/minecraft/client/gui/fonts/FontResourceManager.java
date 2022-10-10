package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.GlyphProviderTypes;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FontResourceManager implements IResourceManagerReloadListener {
   private static final Logger field_211509_a = LogManager.getLogger();
   private final Map<ResourceLocation, FontRenderer> field_211510_b = Maps.newHashMap();
   private final TextureManager field_211511_c;
   private boolean field_211826_d;

   public FontResourceManager(TextureManager var1, boolean var2) {
      super();
      this.field_211511_c = var1;
      this.field_211826_d = var2;
   }

   public void func_195410_a(IResourceManager var1) {
      Gson var2 = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      HashMap var3 = Maps.newHashMap();
      Iterator var4 = var1.func_199003_a("font", (var0) -> {
         return var0.endsWith(".json");
      }).iterator();

      while(var4.hasNext()) {
         ResourceLocation var5 = (ResourceLocation)var4.next();
         String var6 = var5.func_110623_a();
         ResourceLocation var7 = new ResourceLocation(var5.func_110624_b(), var6.substring("font/".length(), var6.length() - ".json".length()));
         List var8 = (List)var3.computeIfAbsent(var7, (var0) -> {
            return Lists.newArrayList(new IGlyphProvider[]{new DefaultGlyphProvider()});
         });

         try {
            Iterator var9 = var1.func_199004_b(var5).iterator();

            while(var9.hasNext()) {
               IResource var10 = (IResource)var9.next();

               try {
                  InputStream var11 = var10.func_199027_b();
                  Throwable var12 = null;

                  try {
                     JsonArray var13 = JsonUtils.func_151214_t((JsonObject)JsonUtils.func_188178_a(var2, IOUtils.toString(var11, StandardCharsets.UTF_8), JsonObject.class), "providers");

                     for(int var14 = var13.size() - 1; var14 >= 0; --var14) {
                        JsonObject var15 = JsonUtils.func_151210_l(var13.get(var14), "providers[" + var14 + "]");

                        try {
                           GlyphProviderTypes var16 = GlyphProviderTypes.func_211638_a(JsonUtils.func_151200_h(var15, "type"));
                           if (!this.field_211826_d || var16 == GlyphProviderTypes.LEGACY_UNICODE || !var7.equals(Minecraft.field_211502_b)) {
                              IGlyphProvider var17 = var16.func_211637_a(var15).func_211246_a(var1);
                              if (var17 != null) {
                                 var8.add(var17);
                              }
                           }
                        } catch (RuntimeException var28) {
                           field_211509_a.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", var7, var10.func_199026_d(), var28.getMessage());
                        }
                     }
                  } catch (Throwable var29) {
                     var12 = var29;
                     throw var29;
                  } finally {
                     if (var11 != null) {
                        if (var12 != null) {
                           try {
                              var11.close();
                           } catch (Throwable var27) {
                              var12.addSuppressed(var27);
                           }
                        } else {
                           var11.close();
                        }
                     }

                  }
               } catch (RuntimeException var31) {
                  field_211509_a.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", var7, var10.func_199026_d(), var31.getMessage());
               }
            }
         } catch (IOException var32) {
            field_211509_a.warn("Unable to load font '{}' in fonts.json: {}", var7, var32.getMessage());
         }
      }

      Stream.concat(this.field_211510_b.keySet().stream(), var3.keySet().stream()).distinct().forEach((var2x) -> {
         List var3x = (List)var3.getOrDefault(var2x, Collections.emptyList());
         Collections.reverse(var3x);
         ((FontRenderer)this.field_211510_b.computeIfAbsent(var2x, (var1) -> {
            return new FontRenderer(this.field_211511_c, new Font(this.field_211511_c, var1));
         })).func_211568_a(var3x);
      });
   }

   @Nullable
   public FontRenderer func_211504_a(ResourceLocation var1) {
      return (FontRenderer)this.field_211510_b.computeIfAbsent(var1, (var1x) -> {
         FontRenderer var2 = new FontRenderer(this.field_211511_c, new Font(this.field_211511_c, var1x));
         var2.func_211568_a(Lists.newArrayList(new IGlyphProvider[]{new DefaultGlyphProvider()}));
         return var2;
      });
   }

   public void func_211825_a(boolean var1) {
      if (var1 != this.field_211826_d) {
         this.field_211826_d = var1;
         this.func_195410_a(Minecraft.func_71410_x().func_195551_G());
      }
   }
}
