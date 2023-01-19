package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Gson gson;
   private final String directory;

   public SimpleJsonResourceReloadListener(Gson var1, String var2) {
      super();
      this.gson = var1;
      this.directory = var2;
   }

   protected Map<ResourceLocation, JsonElement> prepare(ResourceManager var1, ProfilerFiller var2) {
      HashMap var3 = Maps.newHashMap();
      FileToIdConverter var4 = FileToIdConverter.json(this.directory);

      for(Entry var6 : var4.listMatchingResources(var1).entrySet()) {
         ResourceLocation var7 = (ResourceLocation)var6.getKey();
         ResourceLocation var8 = var4.fileToId(var7);

         try (BufferedReader var9 = ((Resource)var6.getValue()).openAsReader()) {
            JsonElement var10 = GsonHelper.fromJson(this.gson, var9, JsonElement.class);
            JsonElement var11 = (JsonElement)var3.put(var8, var10);
            if (var11 != null) {
               throw new IllegalStateException("Duplicate data file ignored with ID " + var8);
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var14) {
            LOGGER.error("Couldn't parse data file {} from {}", new Object[]{var8, var7, var14});
         }
      }

      return var3;
   }
}
