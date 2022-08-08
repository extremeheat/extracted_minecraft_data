package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PATH_SUFFIX = ".json";
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Gson gson;
   private final String directory;

   public SimpleJsonResourceReloadListener(Gson var1, String var2) {
      super();
      this.gson = var1;
      this.directory = var2;
   }

   protected Map<ResourceLocation, JsonElement> prepare(ResourceManager var1, ProfilerFiller var2) {
      HashMap var3 = Maps.newHashMap();
      int var4 = this.directory.length() + 1;
      Iterator var5 = var1.listResources(this.directory, (var0) -> {
         return var0.getPath().endsWith(".json");
      }).entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         ResourceLocation var7 = (ResourceLocation)var6.getKey();
         String var8 = var7.getPath();
         ResourceLocation var9 = new ResourceLocation(var7.getNamespace(), var8.substring(var4, var8.length() - PATH_SUFFIX_LENGTH));

         try {
            BufferedReader var10 = ((Resource)var6.getValue()).openAsReader();

            try {
               JsonElement var11 = (JsonElement)GsonHelper.fromJson(this.gson, (Reader)var10, (Class)JsonElement.class);
               if (var11 != null) {
                  JsonElement var12 = (JsonElement)var3.put(var9, var11);
                  if (var12 != null) {
                     throw new IllegalStateException("Duplicate data file ignored with ID " + var9);
                  }
               } else {
                  LOGGER.error("Couldn't load data file {} from {} as it's null or empty", var9, var7);
               }
            } catch (Throwable var14) {
               if (var10 != null) {
                  try {
                     var10.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (var10 != null) {
               var10.close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var15) {
            LOGGER.error("Couldn't parse data file {} from {}", new Object[]{var9, var7, var15});
         }
      }

      return var3;
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
