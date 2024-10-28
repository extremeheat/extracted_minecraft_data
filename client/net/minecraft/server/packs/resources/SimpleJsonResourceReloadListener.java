package net.minecraft.server.packs.resources;

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
      HashMap var3 = new HashMap();
      scanDirectory(var1, this.directory, this.gson, var3);
      return var3;
   }

   public static void scanDirectory(ResourceManager var0, String var1, Gson var2, Map<ResourceLocation, JsonElement> var3) {
      FileToIdConverter var4 = FileToIdConverter.json(var1);
      Iterator var5 = var4.listMatchingResources(var0).entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         ResourceLocation var7 = (ResourceLocation)var6.getKey();
         ResourceLocation var8 = var4.fileToId(var7);

         try {
            BufferedReader var9 = ((Resource)var6.getValue()).openAsReader();

            try {
               JsonElement var10 = (JsonElement)GsonHelper.fromJson(var2, (Reader)var9, (Class)JsonElement.class);
               JsonElement var11 = (JsonElement)var3.put(var8, var10);
               if (var11 != null) {
                  throw new IllegalStateException("Duplicate data file ignored with ID " + String.valueOf(var8));
               }
            } catch (Throwable var13) {
               if (var9 != null) {
                  try {
                     ((Reader)var9).close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (var9 != null) {
               ((Reader)var9).close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var14) {
            LOGGER.error("Couldn't parse data file {} from {}", new Object[]{var8, var7, var14});
         }
      }

   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
