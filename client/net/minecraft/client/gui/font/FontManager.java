package net.minecraft.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FontManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
   private final FontSet missingFontSet;
   private final Map<ResourceLocation, FontSet> fontSets = Maps.newHashMap();
   private final TextureManager textureManager;
   private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();
   private final PreparableReloadListener reloadListener = new SimplePreparableReloadListener<Map<ResourceLocation, List<GlyphProvider>>>() {
      protected Map<ResourceLocation, List<GlyphProvider>> prepare(ResourceManager var1, ProfilerFiller var2) {
         var2.startTick();
         Gson var3 = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         HashMap var4 = Maps.newHashMap();
         Iterator var5 = var1.listResources("font", (var0) -> {
            return var0.endsWith(".json");
         }).iterator();

         while(var5.hasNext()) {
            ResourceLocation var6 = (ResourceLocation)var5.next();
            String var7 = var6.getPath();
            ResourceLocation var8 = new ResourceLocation(var6.getNamespace(), var7.substring("font/".length(), var7.length() - ".json".length()));
            List var9 = (List)var4.computeIfAbsent(var8, (var0) -> {
               return Lists.newArrayList(new GlyphProvider[]{new AllMissingGlyphProvider()});
            });
            var2.push(var8::toString);

            try {
               for(Iterator var10 = var1.getResources(var6).iterator(); var10.hasNext(); var2.pop()) {
                  Resource var11 = (Resource)var10.next();
                  var2.push(var11::getSourceName);

                  try {
                     InputStream var12 = var11.getInputStream();
                     Throwable var13 = null;

                     try {
                        BufferedReader var14 = new BufferedReader(new InputStreamReader(var12, StandardCharsets.UTF_8));
                        Throwable var15 = null;

                        try {
                           var2.push("reading");
                           JsonArray var16 = GsonHelper.getAsJsonArray((JsonObject)GsonHelper.fromJson(var3, (Reader)var14, (Class)JsonObject.class), "providers");
                           var2.popPush("parsing");

                           for(int var17 = var16.size() - 1; var17 >= 0; --var17) {
                              JsonObject var18 = GsonHelper.convertToJsonObject(var16.get(var17), "providers[" + var17 + "]");

                              try {
                                 String var19 = GsonHelper.getAsString(var18, "type");
                                 GlyphProviderBuilderType var20 = GlyphProviderBuilderType.byName(var19);
                                 var2.push(var19);
                                 GlyphProvider var21 = var20.create(var18).create(var1);
                                 if (var21 != null) {
                                    var9.add(var21);
                                 }

                                 var2.pop();
                              } catch (RuntimeException var49) {
                                 FontManager.LOGGER.warn("Unable to read definition '{}' in {} in resourcepack: '{}': {}", var8, "fonts.json", var11.getSourceName(), var49.getMessage());
                              }
                           }

                           var2.pop();
                        } catch (Throwable var50) {
                           var15 = var50;
                           throw var50;
                        } finally {
                           if (var14 != null) {
                              if (var15 != null) {
                                 try {
                                    var14.close();
                                 } catch (Throwable var48) {
                                    var15.addSuppressed(var48);
                                 }
                              } else {
                                 var14.close();
                              }
                           }

                        }
                     } catch (Throwable var52) {
                        var13 = var52;
                        throw var52;
                     } finally {
                        if (var12 != null) {
                           if (var13 != null) {
                              try {
                                 var12.close();
                              } catch (Throwable var47) {
                                 var13.addSuppressed(var47);
                              }
                           } else {
                              var12.close();
                           }
                        }

                     }
                  } catch (RuntimeException var54) {
                     FontManager.LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}': {}", var8, "fonts.json", var11.getSourceName(), var54.getMessage());
                  }
               }
            } catch (IOException var55) {
               FontManager.LOGGER.warn("Unable to load font '{}' in {}: {}", var8, "fonts.json", var55.getMessage());
            }

            var2.push("caching");
            IntOpenHashSet var56 = new IntOpenHashSet();
            Iterator var57 = var9.iterator();

            while(var57.hasNext()) {
               GlyphProvider var58 = (GlyphProvider)var57.next();
               var56.addAll(var58.getSupportedGlyphs());
            }

            var56.forEach((var1x) -> {
               if (var1x != 32) {
                  Iterator var2 = Lists.reverse(var9).iterator();

                  while(var2.hasNext()) {
                     GlyphProvider var3 = (GlyphProvider)var2.next();
                     if (var3.getGlyph(var1x) != null) {
                        break;
                     }
                  }

               }
            });
            var2.pop();
            var2.pop();
         }

         var2.endTick();
         return var4;
      }

      protected void apply(Map<ResourceLocation, List<GlyphProvider>> var1, ResourceManager var2, ProfilerFiller var3) {
         var3.startTick();
         var3.push("closing");
         FontManager.this.fontSets.values().forEach(FontSet::close);
         FontManager.this.fontSets.clear();
         var3.popPush("reloading");
         var1.forEach((var1x, var2x) -> {
            FontSet var3 = new FontSet(FontManager.this.textureManager, var1x);
            var3.reload(Lists.reverse(var2x));
            FontManager.this.fontSets.put(var1x, var3);
         });
         var3.pop();
         var3.endTick();
      }

      public String getName() {
         return "FontManager";
      }

      // $FF: synthetic method
      protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
         return this.prepare(var1, var2);
      }
   };

   public FontManager(TextureManager var1) {
      super();
      this.textureManager = var1;
      this.missingFontSet = (FontSet)Util.make(new FontSet(var1, MISSING_FONT), (var0) -> {
         var0.reload(Lists.newArrayList(new GlyphProvider[]{new AllMissingGlyphProvider()}));
      });
   }

   public void setRenames(Map<ResourceLocation, ResourceLocation> var1) {
      this.renames = var1;
   }

   public Font createFont() {
      return new Font((var1) -> {
         return (FontSet)this.fontSets.getOrDefault(this.renames.getOrDefault(var1, var1), this.missingFontSet);
      });
   }

   public PreparableReloadListener getReloadListener() {
      return this.reloadListener;
   }

   public void close() {
      this.fontSets.values().forEach(FontSet::close);
      this.missingFontSet.close();
   }
}
