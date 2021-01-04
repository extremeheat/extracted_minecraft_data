package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FontManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Font> fonts = Maps.newHashMap();
   private final Set<GlyphProvider> providers = Sets.newHashSet();
   private final TextureManager textureManager;
   private boolean forceUnicode;
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
                                 if (!FontManager.this.forceUnicode || var20 == GlyphProviderBuilderType.LEGACY_UNICODE || !var8.equals(Minecraft.DEFAULT_FONT)) {
                                    var2.push(var19);
                                    var9.add(var20.create(var18).create(var1));
                                    var2.pop();
                                 }
                              } catch (RuntimeException var48) {
                                 FontManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", var8, var11.getSourceName(), var48.getMessage());
                              }
                           }

                           var2.pop();
                        } catch (Throwable var49) {
                           var15 = var49;
                           throw var49;
                        } finally {
                           if (var14 != null) {
                              if (var15 != null) {
                                 try {
                                    var14.close();
                                 } catch (Throwable var47) {
                                    var15.addSuppressed(var47);
                                 }
                              } else {
                                 var14.close();
                              }
                           }

                        }
                     } catch (Throwable var51) {
                        var13 = var51;
                        throw var51;
                     } finally {
                        if (var12 != null) {
                           if (var13 != null) {
                              try {
                                 var12.close();
                              } catch (Throwable var46) {
                                 var13.addSuppressed(var46);
                              }
                           } else {
                              var12.close();
                           }
                        }

                     }
                  } catch (RuntimeException var53) {
                     FontManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", var8, var11.getSourceName(), var53.getMessage());
                  }
               }
            } catch (IOException var54) {
               FontManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", var8, var54.getMessage());
            }

            var2.push("caching");

            for(char var55 = 0; var55 < '\uffff'; ++var55) {
               if (var55 != ' ') {
                  Iterator var56 = Lists.reverse(var9).iterator();

                  while(var56.hasNext()) {
                     GlyphProvider var57 = (GlyphProvider)var56.next();
                     if (var57.getGlyph(var55) != null) {
                        break;
                     }
                  }
               }
            }

            var2.pop();
            var2.pop();
         }

         var2.endTick();
         return var4;
      }

      protected void apply(Map<ResourceLocation, List<GlyphProvider>> var1, ResourceManager var2, ProfilerFiller var3) {
         var3.startTick();
         var3.push("reloading");
         Stream.concat(FontManager.this.fonts.keySet().stream(), var1.keySet().stream()).distinct().forEach((var2x) -> {
            List var3 = (List)var1.getOrDefault(var2x, Collections.emptyList());
            Collections.reverse(var3);
            ((Font)FontManager.this.fonts.computeIfAbsent(var2x, (var1x) -> {
               return new Font(FontManager.this.textureManager, new FontSet(FontManager.this.textureManager, var1x));
            })).reload(var3);
         });
         Collection var10000 = var1.values();
         Set var10001 = FontManager.this.providers;
         var10000.forEach(var10001::addAll);
         var3.pop();
         var3.endTick();
      }

      // $FF: synthetic method
      protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
         return this.prepare(var1, var2);
      }
   };

   public FontManager(TextureManager var1, boolean var2) {
      super();
      this.textureManager = var1;
      this.forceUnicode = var2;
   }

   @Nullable
   public Font get(ResourceLocation var1) {
      return (Font)this.fonts.computeIfAbsent(var1, (var1x) -> {
         Font var2 = new Font(this.textureManager, new FontSet(this.textureManager, var1x));
         var2.reload(Lists.newArrayList(new GlyphProvider[]{new AllMissingGlyphProvider()}));
         return var2;
      });
   }

   public void setForceUnicode(boolean var1, Executor var2, Executor var3) {
      if (var1 != this.forceUnicode) {
         this.forceUnicode = var1;
         ResourceManager var4 = Minecraft.getInstance().getResourceManager();
         PreparableReloadListener.PreparationBarrier var5 = new PreparableReloadListener.PreparationBarrier() {
            public <T> CompletableFuture<T> wait(T var1) {
               return CompletableFuture.completedFuture(var1);
            }
         };
         this.reloadListener.reload(var5, var4, InactiveProfiler.INACTIVE, InactiveProfiler.INACTIVE, var2, var3);
      }
   }

   public PreparableReloadListener getReloadListener() {
      return this.reloadListener;
   }

   public void close() {
      this.fonts.values().forEach(Font::close);
      this.providers.forEach(GlyphProvider::close);
   }
}
