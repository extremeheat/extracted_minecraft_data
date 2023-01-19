package net.minecraft.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.slf4j.Logger;

public class FontManager implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String FONTS_PATH = "fonts.json";
   public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
   private final FontSet missingFontSet;
   final Map<ResourceLocation, FontSet> fontSets = Maps.newHashMap();
   final TextureManager textureManager;
   private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();
   private final PreparableReloadListener reloadListener = new SimplePreparableReloadListener<Map<ResourceLocation, List<GlyphProvider>>>() {
      protected Map<ResourceLocation, List<GlyphProvider>> prepare(ResourceManager var1, ProfilerFiller var2) {
         var2.startTick();
         Gson var3 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
         HashMap var4 = Maps.newHashMap();

         for(Entry var6 : var1.listResourceStacks("font", var0 -> var0.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation var7 = (ResourceLocation)var6.getKey();
            String var8 = var7.getPath();
            ResourceLocation var9 = new ResourceLocation(var7.getNamespace(), var8.substring("font/".length(), var8.length() - ".json".length()));
            List var10 = var4.computeIfAbsent(var9, var0 -> Lists.newArrayList(new GlyphProvider[]{new AllMissingGlyphProvider()}));
            var2.push(var9::toString);

            for(Resource var12 : (List)var6.getValue()) {
               var2.push(var12.sourcePackId());

               try (BufferedReader var13 = var12.openAsReader()) {
                  try {
                     var2.push("reading");
                     JsonArray var14 = GsonHelper.getAsJsonArray(GsonHelper.fromJson(var3, var13, JsonObject.class), "providers");
                     var2.popPush("parsing");

                     for(int var15 = var14.size() - 1; var15 >= 0; --var15) {
                        JsonObject var16 = GsonHelper.convertToJsonObject(var14.get(var15), "providers[" + var15 + "]");
                        String var17 = GsonHelper.getAsString(var16, "type");
                        GlyphProviderBuilderType var18 = GlyphProviderBuilderType.byName(var17);

                        try {
                           var2.push(var17);
                           GlyphProvider var19 = var18.create(var16).create(var1);
                           if (var19 != null) {
                              var10.add(var19);
                           }
                        } finally {
                           var2.pop();
                        }
                     }
                  } finally {
                     var2.pop();
                  }
               } catch (Exception var36) {
                  FontManager.LOGGER
                     .warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{var9, "fonts.json", var12.sourcePackId(), var36});
               }

               var2.pop();
            }

            var2.push("caching");
            IntOpenHashSet var37 = new IntOpenHashSet();

            for(GlyphProvider var39 : var10) {
               var37.addAll(var39.getSupportedGlyphs());
            }

            var37.forEach(var1x -> {
               if (var1x != 32) {
                  for(GlyphProvider var3x : Lists.reverse(var10)) {
                     if (var3x.getGlyph(var1x) != null) {
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
            FontSet var3x = new FontSet(FontManager.this.textureManager, var1x);
            var3x.reload(Lists.reverse(var2x));
            FontManager.this.fontSets.put(var1x, var3x);
         });
         var3.pop();
         var3.endTick();
      }

      @Override
      public String getName() {
         return "FontManager";
      }
   };

   public FontManager(TextureManager var1) {
      super();
      this.textureManager = var1;
      this.missingFontSet = Util.make(
         new FontSet(var1, MISSING_FONT), var0 -> var0.reload(Lists.newArrayList(new GlyphProvider[]{new AllMissingGlyphProvider()}))
      );
   }

   public void setRenames(Map<ResourceLocation, ResourceLocation> var1) {
      this.renames = var1;
   }

   public Font createFont() {
      return new Font(var1 -> this.fontSets.getOrDefault(this.renames.getOrDefault(var1, var1), this.missingFontSet), false);
   }

   public Font createFontFilterFishy() {
      return new Font(var1 -> this.fontSets.getOrDefault(this.renames.getOrDefault(var1, var1), this.missingFontSet), true);
   }

   public PreparableReloadListener getReloadListener() {
      return this.reloadListener;
   }

   @Override
   public void close() {
      this.fontSets.values().forEach(FontSet::close);
      this.missingFontSet.close();
   }
}
