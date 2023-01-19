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
import net.minecraft.resources.FileToIdConverter;
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
   static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
   private final FontSet missingFontSet;
   final Map<ResourceLocation, FontSet> fontSets = Maps.newHashMap();
   final TextureManager textureManager;
   private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();
   private final PreparableReloadListener reloadListener = new SimplePreparableReloadListener<Map<ResourceLocation, List<GlyphProvider>>>() {
      protected Map<ResourceLocation, List<GlyphProvider>> prepare(ResourceManager var1, ProfilerFiller var2) {
         var2.startTick();
         Gson var3 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
         HashMap var4 = Maps.newHashMap();

         for(Entry var6 : FontManager.FONT_DEFINITIONS.listMatchingResourceStacks(var1).entrySet()) {
            ResourceLocation var7 = (ResourceLocation)var6.getKey();
            ResourceLocation var8 = FontManager.FONT_DEFINITIONS.fileToId(var7);
            List var9 = var4.computeIfAbsent(var8, var0 -> Lists.newArrayList(new GlyphProvider[]{new AllMissingGlyphProvider()}));
            var2.push(var8::toString);

            for(Resource var11 : (List)var6.getValue()) {
               var2.push(var11.sourcePackId());

               try (BufferedReader var12 = var11.openAsReader()) {
                  try {
                     var2.push("reading");
                     JsonArray var13 = GsonHelper.getAsJsonArray(GsonHelper.fromJson(var3, var12, JsonObject.class), "providers");
                     var2.popPush("parsing");

                     for(int var14 = var13.size() - 1; var14 >= 0; --var14) {
                        JsonObject var15 = GsonHelper.convertToJsonObject(var13.get(var14), "providers[" + var14 + "]");
                        String var16 = GsonHelper.getAsString(var15, "type");
                        GlyphProviderBuilderType var17 = GlyphProviderBuilderType.byName(var16);

                        try {
                           var2.push(var16);
                           GlyphProvider var18 = var17.create(var15).create(var1);
                           if (var18 != null) {
                              var9.add(var18);
                           }
                        } finally {
                           var2.pop();
                        }
                     }
                  } finally {
                     var2.pop();
                  }
               } catch (Exception var35) {
                  FontManager.LOGGER
                     .warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{var8, "fonts.json", var11.sourcePackId(), var35});
               }

               var2.pop();
            }

            var2.push("caching");
            IntOpenHashSet var36 = new IntOpenHashSet();

            for(GlyphProvider var38 : var9) {
               var36.addAll(var38.getSupportedGlyphs());
            }

            var36.forEach(var1x -> {
               if (var1x != 32) {
                  for(GlyphProvider var3x : Lists.reverse(var9)) {
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
