package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
   private static final Logger LOGGER = LogManager.getLogger();
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
         return var0.endsWith(".json");
      }).iterator();

      while(var5.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var5.next();
         String var7 = var6.getPath();
         ResourceLocation var8 = new ResourceLocation(var6.getNamespace(), var7.substring(var4, var7.length() - PATH_SUFFIX_LENGTH));

         try {
            Resource var9 = var1.getResource(var6);

            try {
               InputStream var10 = var9.getInputStream();

               try {
                  BufferedReader var11 = new BufferedReader(new InputStreamReader(var10, StandardCharsets.UTF_8));

                  try {
                     JsonElement var12 = (JsonElement)GsonHelper.fromJson(this.gson, (Reader)var11, (Class)JsonElement.class);
                     if (var12 != null) {
                        JsonElement var13 = (JsonElement)var3.put(var8, var12);
                        if (var13 != null) {
                           throw new IllegalStateException("Duplicate data file ignored with ID " + var8);
                        }
                     } else {
                        LOGGER.error("Couldn't load data file {} from {} as it's null or empty", var8, var6);
                     }
                  } catch (Throwable var17) {
                     try {
                        var11.close();
                     } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                     }

                     throw var17;
                  }

                  var11.close();
               } catch (Throwable var18) {
                  if (var10 != null) {
                     try {
                        var10.close();
                     } catch (Throwable var15) {
                        var18.addSuppressed(var15);
                     }
                  }

                  throw var18;
               }

               if (var10 != null) {
                  var10.close();
               }
            } catch (Throwable var19) {
               if (var9 != null) {
                  try {
                     var9.close();
                  } catch (Throwable var14) {
                     var19.addSuppressed(var14);
                  }
               }

               throw var19;
            }

            if (var9 != null) {
               var9.close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var20) {
            LOGGER.error("Couldn't parse data file {} from {}", var8, var6, var20);
         }
      }

      return var3;
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
